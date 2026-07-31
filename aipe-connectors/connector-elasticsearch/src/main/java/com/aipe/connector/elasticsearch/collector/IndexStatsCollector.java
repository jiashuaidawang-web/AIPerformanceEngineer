package com.aipe.connector.elasticsearch.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.elasticsearch.config.ElasticsearchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Elasticsearch 索引统计采集器
 */
public class IndexStatsCollector implements ElasticsearchCollector {

    private static final Logger log = LoggerFactory.getLogger(IndexStatsCollector.class);

    @Override
    public List<ObservationData> collect(ElasticsearchConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        try {
            String statsJson = httpGet(config, "/_stats/docs,store,search,indexing");
            if (statsJson != null) {
                addMetric(results, "elasticsearch.indices.count",
                        parseJsonField(statsJson, "count"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.indices.docs.count",
                        parseJsonField(statsJson, "count"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.indices.store.size_in_bytes",
                        parseJsonField(statsJson, "size_in_bytes"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.search.query_total",
                        parseJsonField(statsJson, "query_total"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.search.query_time_in_millis",
                        parseJsonField(statsJson, "query_time_in_millis"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.indexing.index_total",
                        parseJsonField(statsJson, "index_total"), agentId, connectorId, now);
            }
        } catch (Exception e) {
            log.warn("Failed to collect ES index stats: {}", e.getMessage());
        }

        return results;
    }

    private String httpGet(ElasticsearchConfig config, String path) {
        try {
            URL url = new URL("http://" + config.getHost() + ":" + config.getPort() + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            if (config.getUsername() != null) {
                String auth = config.getUsername() + ":" + config.getPassword();
                String encoded = Base64.getEncoder().encodeToString(auth.getBytes());
                conn.setRequestProperty("Authorization", "Basic " + encoded);
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) sb.append(line);
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            log.debug("ES HTTP GET failed: {}", e.getMessage());
        }
        return null;
    }

    private double parseJsonField(String json, String field) {
        try {
            String search = "\"" + field + "\":";
            int idx = json.indexOf(search);
            if (idx < 0) return 0;
            int start = idx + search.length();
            while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\"')) start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) end++;
            return Double.parseDouble(json.substring(start, end));
        } catch (Exception e) {
            return 0;
        }
    }

    private void addMetric(List<ObservationData> results, String name, double value,
                           String agentId, String connectorId, long now) {
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "http_api");
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("ELASTICSEARCH")
                .targetResource("es-indices")
                .collectTime(now).metricName(name)
                .metricValue(value).unit("").tags(tags).build());
    }

    @Override
    public String getCollectorName() {
        return "index-stats";
    }
}
