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
 * Elasticsearch 节点统计采集器
 */
public class NodeStatsCollector implements ElasticsearchCollector {

    private static final Logger log = LoggerFactory.getLogger(NodeStatsCollector.class);

    @Override
    public List<ObservationData> collect(ElasticsearchConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        try {
            String statsJson = httpGet(config, "/_nodes/stats/jvm,os,process");
            if (statsJson != null) {
                // 解析节点统计
                addMetric(results, "elasticsearch.nodes.count",
                        countJsonFields(statsJson, "\"nodes\""), agentId, connectorId, now);

                // JVM 指标
                addMetric(results, "elasticsearch.jvm.mem.heap_used_percent",
                        parseJsonField(statsJson, "heap_used_percent"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.jvm.gc.collectors.young.collection_count",
                        parseJsonField(statsJson, "collection_count"), agentId, connectorId, now);
            }
        } catch (Exception e) {
            log.warn("Failed to collect ES node stats: {}", e.getMessage());
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

    private double countJsonFields(String json, String field) {
        int count = 0;
        int idx = 0;
        while ((idx = json.indexOf(field, idx)) != -1) {
            count++;
            idx += field.length();
        }
        return count;
    }

    private void addMetric(List<ObservationData> results, String name, double value,
                           String agentId, String connectorId, long now) {
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "http_api");
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("ELASTICSEARCH")
                .targetResource("es-nodes")
                .collectTime(now).metricName(name)
                .metricValue(value).unit("").tags(tags).build());
    }

    @Override
    public String getCollectorName() {
        return "node-stats";
    }
}
