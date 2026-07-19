package com.aipe.connector.linux.collector.memory;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.linux.collector.LinuxCollector;
import com.aipe.connector.linux.parser.ProcFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(MemoryCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        List<String> lines = reader.readLines("meminfo");
        if (lines.isEmpty()) return results;

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc/meminfo");

        Map<String, Long> memData = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length != 2) continue;
            String key = parts[0].trim();
            String valPart = parts[1].trim().replaceAll("\\s+kB", "");
            try {
                memData.put(key, Long.parseLong(valPart) * 1024);
            } catch (NumberFormatException e) {
                // skip
            }
        }

        Long total = memData.get("MemTotal");
        Long free = memData.get("MemFree");
        Long available = memData.get("MemAvailable");
        Long buffers = memData.get("Buffers");
        Long cached = memData.get("Cached");
        Long swapTotal = memData.get("SwapTotal");
        Long swapFree = memData.get("SwapFree");

        if (total != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.total", total, MetricUnit.BYTES, tags));
        if (free != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.free", free, MetricUnit.BYTES, tags));
        if (available != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.available", available, MetricUnit.BYTES, tags));
        if (buffers != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.buffers", buffers, MetricUnit.BYTES, tags));
        if (cached != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.cached", cached, MetricUnit.BYTES, tags));
        if (swapTotal != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.swap_total", swapTotal, MetricUnit.BYTES, tags));
        if (swapFree != null) results.add(buildObs(agentId, connectorId, now, "linux.memory.swap_free", swapFree, MetricUnit.BYTES, tags));

        if (total != null && free != null && total > 0) {
            double usagePercent = (double)(total - free) / total * 100;
            results.add(buildObs(agentId, connectorId, now, "linux.memory.usage_percent", usagePercent, MetricUnit.PERCENT, tags));
        }

        return results;
    }

    private ObservationData buildObs(String agentId, String connectorId, long time, String name, double val, MetricUnit u, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                .collectTime(time).metricName(name).metricValue(val).unit(u.getSymbol())
                .tags(new HashMap<>(tags)).build();
    }

    @Override
    public String getCollectorName() { return "memory"; }
}
