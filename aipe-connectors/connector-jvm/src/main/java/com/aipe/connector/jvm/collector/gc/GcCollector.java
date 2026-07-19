package com.aipe.connector.jvm.collector.gc;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GcCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(GcCollector.class);
    private final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            String name = gcBean.getName().replaceAll("\\s+", "_").toLowerCase();
            Map<String, String> tags = new HashMap<>();
            tags.put("source", "jmx");
            tags.put("bean", "GarbageCollectorMXBean");
            tags.put("gc_name", gcBean.getName());

            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                    .collectTime(now).metricName("jvm.gc." + name + ".count")
                    .metricValue((double) gcBean.getCollectionCount())
                    .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                    .collectTime(now).metricName("jvm.gc." + name + ".time_ms")
                    .metricValue((double) gcBean.getCollectionTime())
                    .unit(MetricUnit.MILLISECONDS.getSymbol()).tags(tags).build());
        }

        return results;
    }

    @Override
    public String getCollectorName() {
        return "gc";
    }
}
