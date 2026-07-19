package com.aipe.connector.linux.collector.cpu;

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

public class CpuCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(CpuCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        String content = reader.read("stat");
        if (content.isEmpty()) return results;

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc/stat");

        String[] lines = content.split("\n");
        for (String line : lines) {
            if (!line.startsWith("cpu")) continue;
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 5) continue;

            String cpuId = parts[0];
            try {
                long user = Long.parseLong(parts[1]);
                long nice = Long.parseLong(parts[2]);
                long system = Long.parseLong(parts[3]);
                long idle = Long.parseLong(parts[4]);
                long total = user + nice + system + idle;

                tags.put("cpu", cpuId);
                results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".user", user, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".nice", nice, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".system", system, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".idle", idle, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".total", total, MetricUnit.COUNT, tags));

                if (total > 0) {
                    double usagePercent = (double)(user + nice + system) / total * 100;
                    results.add(buildObs(agentId, connectorId, now, "linux.cpu." + cpuId + ".usage_percent", usagePercent, MetricUnit.PERCENT, tags));
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse CPU line: {}", line);
            }
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
    public String getCollectorName() { return "cpu"; }
}
