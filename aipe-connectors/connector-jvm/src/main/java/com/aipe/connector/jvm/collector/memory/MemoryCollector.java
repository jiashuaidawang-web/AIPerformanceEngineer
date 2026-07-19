package com.aipe.connector.jvm.collector.memory;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(MemoryCollector.class);
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        // Heap Memory
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.heap.used", heap.getUsed(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.heap.committed", heap.getCommitted(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.heap.max", heap.getMax(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.heap.init", heap.getInit(), MetricUnit.BYTES));

        double heapUsagePercent = heap.getMax() > 0 ? (double) heap.getUsed() / heap.getMax() * 100 : 0;
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.heap.usage_percent", heapUsagePercent, MetricUnit.PERCENT));

        // NonHeap Memory
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.nonheap.used", nonHeap.getUsed(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.nonheap.committed", nonHeap.getCommitted(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.nonheap.max", nonHeap.getMax(), MetricUnit.BYTES));
        results.add(buildObservation(agentId, connectorId, now, "jvm.memory.nonheap.init", nonHeap.getInit(), MetricUnit.BYTES));

        return results;
    }

    private ObservationData buildObservation(String agentId, String connectorId, long time, String name, double value, MetricUnit unit) {
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "jmx");
        tags.put("bean", "MemoryMXBean");
        return ObservationData.builder()
                .agentId(agentId)
                .connectorId(connectorId)
                .connectorType("JVM")
                .targetResource("jvm-local")
                .collectTime(time)
                .metricName(name)
                .metricValue(value)
                .unit(unit.getSymbol())
                .tags(tags)
                .build();
    }

    @Override
    public String getCollectorName() {
        return "memory";
    }
}
