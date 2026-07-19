package com.aipe.connector.redis.collector.memory;

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

public class RedisMemoryCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisMemoryCollector.class);

    private static final String[] MEMORY_KEYS = {"used_memory", "used_memory_rss", "used_memory_peak",
            "used_memory_dataset", "maxmemory", "mem_fragmentation_ratio", "used_memory_lua", "used_memory_scripts"};

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-info");

        String memoryInfo = connection.info("Memory");
        Map<String, String> memData = parseInfoString(memoryInfo);

        for (String key : MEMORY_KEYS) {
            String val = memData.get(key);
            if (val != null) {
                try {
                    double d = Double.parseDouble(val);
                    String unit = key.contains("ratio") ? MetricUnit.PERCENT.getSymbol() : MetricUnit.BYTES.getSymbol();
                    if (key.equals("maxmemory") && d == 0) continue; // 0 means no limit
                    results.add(buildObs(agentId, connectorId, now, "redis.memory." + key, d, unit, tags));
                } catch (NumberFormatException e) {}
            }
        }

        // Memory usage percentage
        String used = memData.get("used_memory");
        String max = memData.get("maxmemory");
        if (used != null && max != null) {
            try {
                double u = Double.parseDouble(used);
                double m = Double.parseDouble(max);
                if (m > 0) {
                    results.add(buildObs(agentId, connectorId, now, "redis.memory.usage_percent", u / m * 100, MetricUnit.PERCENT.getSymbol(), tags));
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
            if (parts.length == 2) map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }

    private ObservationData buildObs(String agentId, String connectorId, long time, String name, double val, String unit, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("REDIS").targetResource("redis-node")
                .collectTime(time).metricName(name).metricValue(val).unit(unit)
                .tags(new HashMap<>(tags)).build();
    }

    @Override
    public String getCollectorName() { return "memory"; }
}
