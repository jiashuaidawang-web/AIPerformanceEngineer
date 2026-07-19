package com.aipe.connector.redis.collector.replication;

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

public class RedisReplicationCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisReplicationCollector.class);

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-info");

        String replicationInfo = connection.info("Replication");
        if (replicationInfo == null || replicationInfo.isEmpty()) return results;

        Map<String, String> data = parseInfoString(replicationInfo);

        // Role
        String role = data.get("role");
        if (role != null) {
            tags.put("role", role);
            results.add(buildObs(agentId, connectorId, now, "redis.replication.role", "master".equalsIgnoreCase(role) ? 1.0 : 0.0, MetricUnit.COUNT, tags));
        }

        String[] keys = {"connected_slaves", "master_repl_offset", "master_replid", "second_repl_offset",
                "repl_backlog_active", "repl_backlog_size", "repl_backlog_first_byte_offset", "repl_backlog_histlen"};

        for (String key : keys) {
            String val = data.get(key);
            if (val != null) {
                try {
                    results.add(buildObs(agentId, connectorId, now, "redis.replication." + key, Double.parseDouble(val), MetricUnit.COUNT, tags));
                } catch (NumberFormatException e) {}
            }
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
                .agentId(agentId).connectorId(connectorId).connectorType("REDIS").targetResource("redis-node")
                .collectTime(time).metricName(name).metricValue(val).unit(u.getSymbol())
                .tags(new HashMap<>(tags)).build();
    }

    @Override
    public String getCollectorName() { return "replication"; }
}
