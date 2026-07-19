package com.aipe.connector.linux.collector.tcp;

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

public class TcpCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(TcpCollector.class);

    // TCP state codes from /proc/net/tcp
    private static final Map<String, String> TCP_STATES = new HashMap<>();
    static {
        TCP_STATES.put("01", "ESTABLISHED");
        TCP_STATES.put("02", "SYN_SENT");
        TCP_STATES.put("03", "SYN_RECV");
        TCP_STATES.put("04", "FIN_WAIT1");
        TCP_STATES.put("05", "FIN_WAIT2");
        TCP_STATES.put("06", "TIME_WAIT");
        TCP_STATES.put("07", "CLOSE");
        TCP_STATES.put("08", "CLOSE_WAIT");
        TCP_STATES.put("09", "LAST_ACK");
        TCP_STATES.put("0A", "LISTEN");
        TCP_STATES.put("0B", "CLOSING");
    }

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        List<String> lines = reader.readLines("net/tcp");
        if (lines.isEmpty()) return results;

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc/net/tcp");

        Map<String, Integer> stateCounts = new HashMap<>();
        // Skip header
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).trim().split("\\s+");
            if (parts.length < 4) continue;
            String stateCode = parts[3];
            String stateName = TCP_STATES.getOrDefault(stateCode, "UNKNOWN");
            stateCounts.merge(stateName, 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : stateCounts.entrySet()) {
            tags.put("tcp_state", entry.getKey());
            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                    .collectTime(now).metricName("linux.tcp." + entry.getKey().toLowerCase() + "_count")
                    .metricValue(entry.getValue().doubleValue())
                    .unit(MetricUnit.COUNT.getSymbol())
                    .tags(new HashMap<>(tags)).build());
        }

        return results;
    }

    @Override
    public String getCollectorName() { return "tcp"; }
}
