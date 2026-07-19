package com.aipe.connector.redis.collector.slowlog;

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

public class RedisSlowLogCollector implements RedisCollector {
    private static final Logger log = LoggerFactory.getLogger(RedisSlowLogCollector.class);
    private long lastSlowLogCount = 0;

    @Override
    public List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "redis-slowlog");

        String slowLogInfo = connection.info("Stats");
        Map<String, String> data = parseInfoString(slowLogInfo);

        String slowLogLen = data.get("slowlog_len");
        if (slowLogLen != null) {
            try {
                double count = Double.parseDouble(slowLogLen);
                results.add(buildObs(agentId, connectorId, now, "redis.slowlog.length", count, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "redis.slowlog.new_entries", Math.max(0, count - lastSlowLogCount), MetricUnit.COUNT, tags));
                lastSlowLogCount = (long) count;
            } catch (NumberFormatException e) {}
        }

        // Try to get slow log entries (optional - best effort)
        try {
            String slowLog = connection.slowLogGet(5);
            if (slowLog != null && !slowLog.isEmpty() && !slowLog.equals("[]")) {
                // Parse entries count from string representation
                int entryCount = slowLog.split("timestamp").length - 1;
                results.add(buildObs(agentId, connectorId, now, "redis.slowlog.recent_count", (double) entryCount, MetricUnit.COUNT, tags));
            }
        } catch (Exception e) {
            log.debug("SLOWLOG GET retrieval skipped: {}", e.getMessage());
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
    public String getCollectorName() { return "slowlog"; }
}
