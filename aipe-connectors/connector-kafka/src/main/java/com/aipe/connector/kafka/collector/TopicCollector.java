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
 * Kafka Topic 指标采集器
 */
public class TopicCollector implements KafkaCollector {

    private static final Logger log = LoggerFactory.getLogger(TopicCollector.class);

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

            // Topic 级别指标
            Set<ObjectName> objectNames = mbsc.queryNames(
                    new ObjectName("kafka.server:type=BrokerTopicMetrics,name=*,topic=*"), null);

            for (ObjectName on : objectNames) {
                try {
                    String topic = on.getKeyProperty("topic");
                    String name = on.getKeyProperty("name");
                    Object value = mbsc.getAttribute(on, "OneMinuteRate");
                    if (value == null) value = mbsc.getAttribute(on, "Count");
                    if (value instanceof Number) {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("source", "jmx");
                        tags.put("type", "topic");
                        tags.put("topic", topic);
                        results.add(ObservationData.builder()
                                .agentId(agentId).connectorId(connectorId).connectorType("KAFKA")
                                .targetResource("kafka-topic-" + topic)
                                .collectTime(now).metricName("kafka.topic." + name)
                                .metricValue(((Number) value).doubleValue())
                                .unit(getUnit(name)).tags(tags).build());
                    }
                } catch (Exception e) {
                    // 跳过
                }
            }

            log.debug("Collected {} Kafka topic metrics", results.size());

        } catch (Exception e) {
            log.warn("Failed to collect Kafka topic metrics: {}", e.getMessage());
        } finally {
            if (jmxConnector != null) {
                try { jmxConnector.close(); } catch (Exception ignored) {}
            }
        }

        return results;
    }

    private String getUnit(String metricName) {
        if (metricName.contains("PerSec")) return "per_sec";
        if (metricName.contains("Bytes")) return "bytes";
        return "";
    }

    @Override
    public String getCollectorName() {
        return "topic";
    }
}
