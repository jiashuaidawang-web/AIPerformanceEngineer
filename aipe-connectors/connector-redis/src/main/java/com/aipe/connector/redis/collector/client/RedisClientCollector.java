package com.aipe.connector.redis.collector.client;

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

public class RedisClientCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisClientCollector.class);

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-info");

        String clientsInfo = connection.info("Clients");
        Map<String, String> data = parseInfoString(clientsInfo);

        String[] keys = {"connected_clients", "blocked_clients", "tracking_clients", "clients_in_timeout_table"};
        for (String key : keys) {
            String val = data.get(key);
            if (val != null) {
                try {
                    results.add(buildObs(agentId, connectorId, now, "redis.clients." + key, Double.parseDouble(val), MetricUnit.COUNT, tags));
                } catch (NumberFormatException e) {}
            }
        }

        // Parse CLIENT LIST for per-client details
        String clientList = connection.clientList();
        if (clientList != null && !clientList.isEmpty()) {
            int clientCount = clientList.split("\n").length;
            results.add(buildObs(agentId, connectorId, now, "redis.clients.total_from_list", (double) clientCount, MetricUnit.COUNT, tags));
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
    public String getCollectorName() { return "client"; }
}
