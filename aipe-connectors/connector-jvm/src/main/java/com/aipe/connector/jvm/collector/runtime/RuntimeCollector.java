package com.aipe.connector.jvm.collector.runtime;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(RuntimeCollector.class);
    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "jmx");
        tags.put("bean", "RuntimeMXBean");

        long uptimeMs = runtimeMXBean.getUptime();
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.runtime.uptime_ms")
                .metricValue((double) uptimeMs)
                .unit(MetricUnit.MILLISECONDS.getSymbol()).tags(tags).build());

        long startTime = runtimeMXBean.getStartTime();
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.runtime.start_time")
                .metricValue((double) startTime)
                .unit(MetricUnit.MILLISECONDS.getSymbol()).tags(tags).build());

        // JMV info (as tag-only observation)
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.runtime.name")
                .metricValue(0.0)
                .unit(MetricUnit.NONE.getSymbol())
                .tags(buildInfoTags(tags))
                .build());

        return results;
    }

    private Map<String, String> buildInfoTags(Map<String, String> baseTags) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.put("vm_name", runtimeMXBean.getVmName());
        tags.put("vm_vendor", runtimeMXBean.getVmVendor());
        tags.put("vm_version", runtimeMXBean.getVmVersion());
        tags.put("spec_name", runtimeMXBean.getSpecName());
        tags.put("java_version", System.getProperty("java.version", ""));
        return tags;
    }

    @Override
    public String getCollectorName() {
        return "runtime";
    }
}
