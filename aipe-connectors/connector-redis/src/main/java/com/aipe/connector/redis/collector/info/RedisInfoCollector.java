package com.aipe.connector.redis.collector.info;

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

public class RedisInfoCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisInfoCollector.class);

    private static final String[] SERVER_KEYS = {"redis_version", "uptime_in_seconds", "tcp_port"};
    private static final String[] STATS_KEYS = {"total_commands_processed", "instantaneous_ops_per_sec",
            "total_net_input_bytes", "total_net_output_bytes", "rejected_connections", "expired_keys", "evicted_keys", "keyspace_hits", "keyspace_misses"};

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-info");

        // Parse INFO SERVER
        String serverInfo = connection.info("Server");
        Map<String, String> serverData = parseInfoString(serverInfo);
        for (String key : SERVER_KEYS) {
            String val = serverData.get(key);
            if (val != null) {
                try {
                    results.add(buildObs(agentId, connectorId, now, "redis.server." + key, Double.parseDouble(val), MetricUnit.COUNT, tags));
                } catch (NumberFormatException e) {
                    // Skip non-numeric
                }
            }
        }

        // Parse INFO STATS
        String statsInfo = connection.info("Stats");
        Map<String, String> statsData = parseInfoString(statsInfo);
        for (String key : STATS_KEYS) {
            String val = statsData.get(key);
            if (val != null) {
                try {
                    results.add(buildObs(agentId, connectorId, now, "redis.stats." + key, Double.parseDouble(val), MetricUnit.COUNT, tags));
                } catch (NumberFormatException e) {}
            }
        }

        // Calculate hit rate
        String hits = statsData.get("keyspace_hits");
        String misses = statsData.get("keyspace_misses");
        if (hits != null && misses != null) {
            try {
                double h = Double.parseDouble(hits);
                double m = Double.parseDouble(misses);
                double total = h + m;
                if (total > 0) {
                    results.add(buildObs(agentId, connectorId, now, "redis.stats.keyspace_hit_rate", h / total * 100, MetricUnit.PERCENT, tags));
                }
            } catch (NumberFormatException e) {}
        }

        return results;
    }

    private Map<String, String> parseInfoString(String info) {
        Map<String, String> map = new HashMap<>();
        if (info == null || info.isEmpty()) return map;
        for (String line : info.split("\r\n")) {
            if (line.startsWith("#") || !line.contains(":")) continue;
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }

    private ObservationData buildObs(String agentId, String connectorId, long time, String name, double val, MetricUnit u, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("REDIS").targetResource("redis-" + tags.get("host"))
                .collectTime(time).metricName(name).metricValue(val).unit(u.getSymbol())
                .tags(new HashMap<>(tags)).build();
    }

    @Override
    public String getCollectorName() { return "info"; }
}
