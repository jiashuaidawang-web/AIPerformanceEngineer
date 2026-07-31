package com.aipe.connector.kafka.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.kafka.config.KafkaConfig;
import com.aipe.connector.kafka.KafkaCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.*;

/**
 * Kafka Consumer 指标采集器
 */
public class ConsumerCollector implements KafkaCollector {

    private static final Logger log = LoggerFactory.getLogger(ConsumerCollector.class);

    @Override
    public List<ObservationData> collect(KafkaConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        JMXConnector jmxConnector = null;
        try {
            String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi",
                    config.getJmxHost(), config.getJmxPort());
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxConnector = JMXConnectorFactory.connect(url);
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

            // Consumer 指标
            Set<ObjectName> objectNames = mbsc.queryNames(new ObjectName("kafka.consumer:*"), null);
            for (ObjectName on : objectNames) {
                try {
                    String name = on.getKeyProperty("name");
                    String clientId = on.getKeyProperty("client-id");
                    Object value = mbsc.getAttribute(on, "Value");
                    if (value instanceof Number) {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("source", "jmx");
                        tags.put("type", "consumer");
                        tags.put("clientId", clientId);
                        results.add(ObservationData.builder()
                                .agentId(agentId).connectorId(connectorId).connectorType("KAFKA")
                                .targetResource("kafka-consumer-" + clientId)
                                .collectTime(now).metricName("kafka.consumer." + name)
                                .metricValue(((Number) value).doubleValue())
                                .unit(getUnit(name)).tags(tags).build());
                    }
                } catch (Exception e) {
                    // 跳过
                }
            }

            // Consumer Lag (通过 AdminClient API 获取，可选)
            // 注意: Lag 需要通过 Kafka AdminClient 获取，不是 JMX
            // 这里只采集 JMX 指标

            log.debug("Collected {} Kafka consumer metrics", results.size());

        } catch (Exception e) {
            log.warn("Failed to collect Kafka consumer metrics: {}", e.getMessage());
        } finally {
            if (jmxConnector != null) {
                try { jmxConnector.close(); } catch (Exception ignored) {}
            }
        }

        return results;
    }

    private String getUnit(String metricName) {
        if (metricName.contains("rate")) return "per_sec";
        if (metricName.contains("lag")) return "count";
        if (metricName.contains("byte")) return "bytes";
        return "";
    }

    @Override
    public String getCollectorName() {
        return "consumer";
    }
}
