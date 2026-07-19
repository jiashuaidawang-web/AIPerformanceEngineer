package com.aipe.connector.jvm.collector.thread;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.jvm.collector.JvmCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadCollector implements JvmCollector {
    private static final Logger log = LoggerFactory.getLogger(ThreadCollector.class);
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Override
    public List<ObservationData> collect(String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "jmx");
        tags.put("bean", "ThreadMXBean");

        results.add(build("jvm.thread.count", threadMXBean.getThreadCount(), tags, agentId, connectorId, now));
        results.add(build("jvm.thread.peak", threadMXBean.getPeakThreadCount(), tags, agentId, connectorId, now));
        results.add(build("jvm.thread.daemon", threadMXBean.getDaemonThreadCount(), tags, agentId, connectorId, now));
        results.add(build("jvm.thread.total_started", threadMXBean.getTotalStartedThreadCount(), tags, agentId, connectorId, now));

        return results;
    }

    private ObservationData build(String name, double value, Map<String, String> tags,
                                   String agentId, String connectorId, long now) {
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("JVM").targetResource("jvm-local")
                .collectTime(now).metricName(name).metricValue(value)
                .unit(MetricUnit.COUNT.getSymbol()).tags(tags).build();
    }

    @Override
    public String getCollectorName() {
        return "thread";
    }
}
