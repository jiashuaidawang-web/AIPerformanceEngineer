package com.aipe.connector.linux.collector.network;

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

public class NetworkCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(NetworkCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        List<String> lines = reader.readLines("net/dev");
        if (lines.isEmpty()) return results;

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc/net/dev");

        // Skip header lines
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(":");
            if (parts.length != 2) continue;

            String iface = parts[0].trim();
            String[] vals = parts[1].trim().split("\\s+");
            if (vals.length < 16) continue;

            try {
                long rxBytes = Long.parseLong(vals[0]);
                long rxPackets = Long.parseLong(vals[1]);
                long txBytes = Long.parseLong(vals[8]);
                long txPackets = Long.parseLong(vals[9]);

                tags.put("iface", iface);
                results.add(buildObs(agentId, connectorId, now, "linux.net." + iface + ".rx_bytes", rxBytes, MetricUnit.BYTES, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.net." + iface + ".rx_packets", rxPackets, MetricUnit.COUNT, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.net." + iface + ".tx_bytes", txBytes, MetricUnit.BYTES, tags));
                results.add(buildObs(agentId, connectorId, now, "linux.net." + iface + ".tx_packets", txPackets, MetricUnit.COUNT, tags));
            } catch (NumberFormatException e) {
                log.warn("Failed to parse net/dev line for iface={}", iface);
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
    public String getCollectorName() { return "network"; }
}
