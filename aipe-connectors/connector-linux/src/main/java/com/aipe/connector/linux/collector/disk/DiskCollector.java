package com.aipe.connector.linux.collector.disk;

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

public class DiskCollector implements LinuxCollector {
    private static final Logger log = LoggerFactory.getLogger(DiskCollector.class);

    @Override
    public List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();
        File[] roots = File.listRoots();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "java.io.File");

        for (File root : roots) {
            String path = root.getAbsolutePath().replace("/", "_").replace(":", "");
            if (path.isEmpty()) path = "root";

            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usableSpace = root.getUsableSpace();
            long usedSpace = totalSpace - freeSpace;

            tags.put("mount", root.getAbsolutePath());
            results.add(buildObs(agentId, connectorId, now, "linux.disk." + path + ".total", totalSpace, MetricUnit.BYTES, tags));
            results.add(buildObs(agentId, connectorId, now, "linux.disk." + path + ".free", freeSpace, MetricUnit.BYTES, tags));
            results.add(buildObs(agentId, connectorId, now, "linux.disk." + path + ".usable", usableSpace, MetricUnit.BYTES, tags));
            results.add(buildObs(agentId, connectorId, now, "linux.disk." + path + ".used", usedSpace, MetricUnit.BYTES, tags));

            if (totalSpace > 0) {
                double usagePercent = (double) usedSpace / totalSpace * 100;
                results.add(buildObs(agentId, connectorId, now, "linux.disk." + path + ".usage_percent", usagePercent, MetricUnit.PERCENT, tags));
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
    public String getCollectorName() { return "disk"; }
}
