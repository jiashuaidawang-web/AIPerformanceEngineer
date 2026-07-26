package com.aipe.timeline.infrastructure;

import com.aipe.timeline.domain.ObservationQueryPort;
import com.aipe.timeline.domain.ObservationQueryPort.MetricPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ClickHouse 查询适配器（原生 HTTP API，绕过 JDBC LZ4 问题）
 */
@Component
public class HttpClickHouseAdapter implements ObservationQueryPort {

    private static final Logger log = LoggerFactory.getLogger(HttpClickHouseAdapter.class);

    @Value("${clickhouse.url:http://124.223.220.245:8123}")
    private String chUrl;

    @Value("${clickhouse.user:default}")
    private String chUser;

    @Value("${clickhouse.password:pamirs@123}")
    private String chPassword;

    @Override
    public List<MetricPoint> queryMetricSeries(String resourceId, String metricName,
                                                long startTime, long endTime, int limit) {
        String sql = "SELECT timestamp, metric_value, unit FROM metric_observation.observation_fact " +
                "WHERE resource_id = '" + escape(resourceId) + "' " +
                "AND metric_name = '" + escape(metricName) + "' " +
                "AND timestamp >= toDateTime64(" + (startTime / 1000.0) + ", 3) " +
                "AND timestamp <= toDateTime64(" + (endTime / 1000.0) + ", 3) " +
                "ORDER BY timestamp ASC LIMIT " + limit;
        return executeQuery(sql);
    }

    @Override
    public List<String> queryDistinctMetricNames(String resourceId, long startTime, long endTime) {
        String sql = "SELECT DISTINCT metric_name FROM metric_observation.observation_fact " +
                "WHERE resource_id = '" + escape(resourceId) + "' " +
                "AND timestamp >= toDateTime64(" + (startTime / 1000.0) + ", 3) " +
                "AND timestamp <= toDateTime64(" + (endTime / 1000.0) + ", 3)";
        List<String> names = new ArrayList<>();
        String result = executeRaw(sql + " FORMAT JSONEachRow");
        if (result != null && !result.isEmpty()) {
            for (String line : result.split("\n")) {
                if (line.trim().isEmpty()) continue;
                // parse {"metric_name":"cpu.usage"}
                int start = line.indexOf("\"metric_name\":\"") + "\"metric_name\":\"".length();
                int end = line.indexOf("\"", start);
                if (start > 0 && end > start) {
                    names.add(line.substring(start, end));
                }
            }
        }
        return names;
    }

    private List<MetricPoint> executeQuery(String sql) {
        List<MetricPoint> points = new ArrayList<>();
        String result = executeRaw(sql + " FORMAT JSONEachRow");
        if (result != null && !result.isEmpty()) {
            for (String line : result.split("\n")) {
                if (line.trim().isEmpty()) continue;
                try {
                    // parse {"timestamp":"...", "metric_value":88.5, "unit":"%"}
                    String ts = extractJsonString(line, "timestamp");
                    double val = Double.parseDouble(extractJsonNumber(line, "metric_value"));
                    String unit = extractJsonString(line, "unit");
                    points.add(new MetricPoint(0L, val, unit != null ? unit : "", "", ts));
                } catch (Exception e) {
                    log.warn("Failed to parse line: {}", line);
                }
            }
        }
        return points;
    }

    private String executeRaw(String sql) {
        try {
            URL url = new URL(chUrl + "/?query=" + URLEncoder.encode(sql, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-ClickHouse-User", chUser);
            conn.setRequestProperty("X-ClickHouse-Key", chPassword);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            int code = conn.getResponseCode();
            if (code != 200) {
                log.error("CH HTTP error: {}", code);
                return null;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("CH HTTP query failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "\\'");
    }

    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    private String extractJsonNumber(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start < 0) return "0";
        start += search.length();
        int end = json.indexOf(",", start);
        if (end < 0) end = json.indexOf("}", start);
        if (end < 0) return "0";
        return json.substring(start, end).trim();
    }
}
