package com.aipe.connector.jvm.collector.cpu;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CpuCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(CpuCollector.class);
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "jmx");
        tags.put("bean", "OperatingSystemMXBean");

        double systemLoadAvg = osMXBean.getSystemLoadAverage();
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.cpu.system_load_average")
                .metricValue(systemLoadAvg < 0 ? 0 : systemLoadAvg)
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName("jvm.cpu.available_processors")
                .metricValue((double) osMXBean.getAvailableProcessors())
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build());

        // Try to get process CPU time via com.sun.management extension
        try {
            if (osMXBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunBean = (com.sun.management.OperatingSystemMXBean) osMXBean;
                double processCpuLoad = sunBean.getProcessCpuLoad();
                double systemCpuLoad = sunBean.getSystemCpuLoad();
                long processCpuTime = sunBean.getProcessCpuTime();
                long totalPhysicalMemory = sunBean.getTotalPhysicalMemorySize();
                long freePhysicalMemory = sunBean.getFreePhysicalMemorySize();

                results.add(ObservationData.builder()
                        .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                        .collectTime(now).metricName("jvm.cpu.process_cpu_load")
                        .metricValue(processCpuLoad < 0 ? 0 : processCpuLoad * 100)
                        .unit(MetricUnit.PERCENT.getSymbol()).tags(tags).build());

                results.add(ObservationData.builder()
                        .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                        .collectTime(now).metricName("jvm.cpu.system_cpu_load")
                        .metricValue(systemCpuLoad < 0 ? 0 : systemCpuLoad * 100)
                        .unit(MetricUnit.PERCENT.getSymbol()).tags(tags).build());

                results.add(ObservationData.builder()
                        .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                        .collectTime(now).metricName("jvm.cpu.process_cpu_time_ns")
                        .metricValue((double) processCpuTime)
                        .unit("ns").tags(tags).build());

                results.add(ObservationData.builder()
                        .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                        .collectTime(now).metricName("jvm.memory.physical.total")
                        .metricValue((double) totalPhysicalMemory)
                        .unit(MetricUnit.BYTES.getSymbol()).tags(tags).build());

                results.add(ObservationData.builder()
                        .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                        .collectTime(now).metricName("jvm.memory.physical.free")
                        .metricValue((double) freePhysicalMemory)
                        .unit(MetricUnit.BYTES.getSymbol()).tags(tags).build());
            }
        } catch (Exception e) {
            log.warn("sun.management CPU extension not available: {}", e.getMessage());
        }

        return results;
    }

    @Override
    public String getCollectorName() {
        return "cpu";
    }
}
