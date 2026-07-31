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
 * Kafka Producer 指标采集器
 */
public class ProducerCollector implements KafkaCollector {

    private static final Logger log = LoggerFactory.getLogger(ProducerCollector.class);

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

            // Producer 指标
            String[] producerMetrics = {
                "kafka.producer:type=producer-metrics,client-id=([-.w]+):record-send-rate",
                "kafka.producer:type=producer-metrics,client-id=([-.w]+):record-error-rate",
                "kafka.producer:type=producer-metrics,client-id=([-.w]+):request-latency-avg",
                "kafka.producer:type=producer-metrics,client-id=([-.w]+):outgoing-byte-rate",
                "kafka.producer:type=producer-metrics,client-id=([-.w]+):compression-rate-avg"
            };

            Set<ObjectName> objectNames = mbsc.queryNames(new ObjectName("kafka.producer:*"), null);
            for (ObjectName on : objectNames) {
                try {
                    String name = on.getKeyProperty("name");
                    String clientId = on.getKeyProperty("client-id");
                    Object value = mbsc.getAttribute(on, "Value");
                    if (value instanceof Number) {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("source", "jmx");
                        tags.put("type", "producer");
                        tags.put("clientId", clientId);
                        results.add(ObservationData.builder()
                                .agentId(agentId).connectorId(connectorId).connectorType("KAFKA")
                                .targetResource("kafka-producer-" + clientId)
                                .collectTime(now).metricName("kafka.producer." + name)
                                .metricValue(((Number) value).doubleValue())
                                .unit(getUnit(name)).tags(tags).build());
                    }
                } catch (Exception e) {
                    // 跳过
                }
            }

            log.debug("Collected {} Kafka producer metrics", results.size());

        } catch (Exception e) {
            log.warn("Failed to collect Kafka producer metrics: {}", e.getMessage());
        } finally {
            if (jmxConnector != null) {
                try { jmxConnector.close(); } catch (Exception ignored) {}
            }
        }

        return results;
    }

    private String getUnit(String metricName) {
        if (metricName.contains("rate")) return "per_sec";
        if (metricName.contains("latency")) return "ms";
        if (metricName.contains("byte")) return "bytes";
        if (metricName.contains("compression")) return "ratio";
        return "";
    }

    @Override
    public String getCollectorName() {
        return "producer";
    }
}
