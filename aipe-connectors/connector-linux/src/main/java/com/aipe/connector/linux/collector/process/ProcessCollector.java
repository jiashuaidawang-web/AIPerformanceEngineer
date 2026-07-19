package com.aipe.connector.linux.collector.process;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.linux.collector.LinuxCollector;
import com.aipe.connector.linux.parser.ProcFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(ProcessCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "proc");
        tags.put("file", "/proc");

        // Count processes by iterating /proc/[pid]
        File procDir = new File(reader.getProcPath());
        File[] procEntries = procDir.listFiles();
        int totalProcesses = 0;
        int runningProcesses = 0;
        int sleepingProcesses = 0;

        if (procEntries != null) {
            for (File entry : procEntries) {
                if (entry.isDirectory()) {
                    try {
                        Integer.parseInt(entry.getName());
                        totalProcesses++;
                        // Read stat file for state
                        String statContent = reader.read(entry.getName() + "/stat");
                        if (!statContent.isEmpty()) {
                            String[] statParts = statContent.split("\\s+");
                            if (statParts.length > 2) {
                                String state = statParts[2];
                                if ("R".equals(state)) runningProcesses++;
                                else if ("S".equals(state)) sleepingProcesses++;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // not a pid directory
                    }
                }
            }
        }

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                .collectTime(now).metricName("linux.process.total").metricValue((double) totalProcesses)
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                .collectTime(now).metricName("linux.process.running").metricValue((double) runningProcesses)
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("LINUX").targetResource("linux-local")
                .collectTime(now).metricName("linux.process.sleeping").metricValue((double) sleepingProcesses)
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        return results;
    }

    @Override
    public String getCollectorName() { return "process"; }
}
