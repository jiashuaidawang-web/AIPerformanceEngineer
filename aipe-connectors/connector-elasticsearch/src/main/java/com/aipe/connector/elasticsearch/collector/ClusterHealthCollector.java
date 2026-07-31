package com.aipe.connector.elasticsearch.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.elasticsearch.config.ElasticsearchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Elasticsearch 集群健康采集器
 *
 * <p>API: GET /_cluster/health
 * <p>GET /_nodes/stats/jvm,os,process,fs,thread_pool,breaker
 */
public class ClusterHealthCollector implements ElasticsearchCollector {

    private static final Logger log = LoggerFactory.getLogger(ClusterHealthCollector.class);

    @Override
    public List<ObservationData> collect(ElasticsearchConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        try {
            String healthJson = httpGet(config, "/_cluster/health");
            if (healthJson != null) {
                // 简单 JSON 解析 (生产环境建议用 Jackson)
                addMetric(results, "elasticsearch.cluster.status",
                        parseJsonField(healthJson, "status"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.number_of_nodes",
                        parseJsonField(healthJson, "number_of_nodes"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.active_primary_shards",
                        parseJsonField(healthJson, "active_primary_shards"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.active_shards",
                        parseJsonField(healthJson, "active_shards"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.unassigned_shards",
                        parseJsonField(healthJson, "unassigned_shards"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.initializing_shards",
                        parseJsonField(healthJson, "initializing_shards"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.relocating_shards",
                        parseJsonField(healthJson, "relocating_shards"), agentId, connectorId, now);
                addMetric(results, "elasticsearch.cluster.number_of_pending_tasks",
                        parseJsonField(healthJson, "number_of_pending_tasks"), agentId, connectorId, now);
            }
        } catch (Exception e) {
            log.warn("Failed to collect ES cluster health: {}", e.getMessage());
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
            // 跳过空白
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
                .targetResource("es-cluster")
                .collectTime(now).metricName(name)
                .metricValue(value).unit("").tags(tags).build());
    }

    @Override
    public String getCollectorName() {
        return "cluster-health";
    }
}
