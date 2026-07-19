package com.aipe.connector.redis.collector.cluster;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.redis.client.RedisConnection;
import com.aipe.connector.redis.collector.RedisCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisClusterCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisClusterCollector.class);

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-cluster");

        String clusterInfo = connection.clusterInfo();
        if (clusterInfo == null || clusterInfo.isEmpty()) {
            // Not a cluster mode
            return results;
        }

        Map<String, String> data = parseInfoString(clusterInfo);
        String[] keys = {"cluster_state", "cluster_slots_assigned", "cluster_slots_ok", "cluster_slots_pfail",
                "cluster_slots_fail", "cluster_known_nodes", "cluster_size"};

        for (String key : keys) {
            String val = data.get(key);
            if (val != null) {
                try {
                    results.add(buildObs(agentId, connectorId, now, "redis.cluster." + key, Double.parseDouble(val), MetricUnit.COUNT, tags));
                } catch (NumberFormatException e) {}
            }
        }

        // cluster_state: ok=1, fail=0
        String state = data.get("cluster_state");
        if ("ok".equalsIgnoreCase(state)) {
            results.add(buildObs(agentId, connectorId, now, "redis.cluster.state_ok", 1.0, MetricUnit.COUNT, tags));
        } else if (state != null) {
            results.add(buildObs(agentId, connectorId, now, "redis.cluster.state_ok", 0.0, MetricUnit.COUNT, tags));
        }

        return results;
    }

    private Map<String, String> parseInfoString(String info) {
        Map<String, String> map = new HashMap<>();
        if (info == null || info.isEmpty()) return map;
        for (String line : info.split("\r\n")) {
            if (line.startsWith("#") || !line.contains(":")) continue;
            String[] parts = line.split(":", 2);
            if (parts.length == 2) map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }

    private ObservationData buildObs(String agentId, String connectorId, long time, String name, double val, MetricUnit u, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("REDIS").targetResource("redis-cluster")
                .collectTime(time).metricName(name).metricValue(val).unit(u.getSymbol())
                .tags(new HashMap<>(tags)).build();
    }

    @Override
    public String getCollectorName() { return "cluster"; }
}
