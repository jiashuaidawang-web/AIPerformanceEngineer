package com.aipe.connector.linux.collector.load;

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

public class LoadCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(LoadCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        String content = reader.read("loadavg");
        if (content.isEmpty()) return results;

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc/loadavg");

        String[] parts = content.trim().split("\\s+");
        if (parts.length < 3) return results;

        try {
            double load1 = Double.parseDouble(parts[0]);
            double load5 = Double.parseDouble(parts[1]);
            double load15 = Double.parseDouble(parts[2]);

            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                    .collectTime(now).metricName("linux.load.1min").metricValue(load1)
                    .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                    .collectTime(now).metricName("linux.load.5min").metricValue(load5)
                    .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                    .collectTime(now).metricName("linux.load.15min").metricValue(load15)
                    .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse loadavg: {}", content);
        }

        return results;
    }

    @Override
    public String getCollectorName() { return "load"; }
}
