package com.aipe.connector.jvm.collector.classloader;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassLoaderCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(ClassLoaderCollector.class);
    private final ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "jmx");
        tags.put("bean", "ClassLoadingMXBean");

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.classloader.loaded_count")
                .metricValue((double) classLoadingMXBean.getLoadedClassCount())
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.classloader.unloaded_count")
                .metricValue((double) classLoadingMXBean.getUnloadedClassCount())
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.classloader.total_loaded_count")
                .metricValue((double) classLoadingMXBean.getTotalLoadedClassCount())
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        return results;
    }

    @Override
    public String getCollectorName() {
        return "classloader";
    }
}
