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
 * Kafka Broker 指标采集器
 *
 * <p>通过 JMX 采集 Broker 级别指标:
 * <ul>
 *   <li>MessagesInPerSec - 消息写入速率</li>
 *   <li>BytesInPerSec - 字节写入速率</li>
 *   <li>BytesOutPerSec - 字节读取速率</li>
 *   <li>UnderReplicatedPartitions - 副本不足分区数</li>
 *   <li>ActiveControllerCount - 活跃 Controller 数</li>
 * </ul>
 */
public class BrokerCollector implements KafkaCollector {

    private static final Logger log = LoggerFactory.getLogger(BrokerCollector.class);

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

            // Broker 消息指标
            addMetricIfExists(mbsc, results, "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec",
                    "kafka.broker.messages_in_rate", agentId, connectorId, now, MetricUnit.COUNT);
            addMetricIfExists(mbsc, results, "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec",
                    "kafka.broker.bytes_in_rate", agentId, connectorId, now, MetricUnit.BYTES);
            addMetricIfExists(mbsc, results, "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec",
                    "kafka.broker.bytes_out_rate", agentId, connectorId, now, MetricUnit.BYTES);

            // 副本指标
            addMetricIfExists(mbsc, results, "kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions",
                    "kafka.broker.under_replicated_partitions", agentId, connectorId, now, MetricUnit.COUNT);
            addMetricIfExists(mbsc, results, "kafka.server:type=ReplicaManager,name=PartitionCount",
                    "kafka.broker.partition_count", agentId, connectorId, now, MetricUnit.COUNT);
            addMetricIfExists(mbsc, results, "kafka.controller:type=KafkaController,name=ActiveControllerCount",
                    "kafka.broker.active_controller_count", agentId, connectorId, now, MetricUnit.COUNT);
            addMetricIfExists(mbsc, results, "kafka.controller:type=KafkaController,name=OfflinePartitionsCount",
                    "kafka.broker.offline_partitions_count", agentId, connectorId, now, MetricUnit.COUNT);

            // 请求指标
            addMetricIfExists(mbsc, results, "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce",
                    "kafka.broker.produce_requests_rate", agentId, connectorId, now, MetricUnit.COUNT);
            addMetricIfExists(mbsc, results, "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer",
                    "kafka.broker.fetch_requests_rate", agentId, connectorId, now, MetricUnit.COUNT);

            log.debug("Collected {} Kafka broker metrics", results.size());

        } catch (Exception e) {
            log.warn("Failed to collect Kafka broker metrics via JMX: {}", e.getMessage());
        } finally {
            if (jmxConnector != null) {
                try { jmxConnector.close(); } catch (Exception ignored) {}
            }
        }

        return results;
    }

    private void addMetricIfExists(MBeanServerConnection mbsc, List<ObservationData> results,
                                    String objectName, String metricName,
                                    String agentId, String connectorId, long now, MetricUnit unit) {
        try {
            ObjectName on = new ObjectName(objectName);
            if (mbsc.isRegistered(on)) {
                Object value = mbsc.getAttribute(on, "OneMinuteRate");
                if (value == null) value = mbsc.getAttribute(on, "Count");
                if (value instanceof Number) {
                    Map<String, String> tags = new HashMap<>();
                    tags.put("source", "jmx");
                    tags.put("type", "broker");
                    results.add(ObservationData.builder()
                            .agentId(agentId).connectorId(connectorId).connectorType("KAFKA")
                            .targetResource("kafka-" + metricName)
                            .collectTime(now).metricName(metricName)
                            .metricValue(((Number) value).doubleValue())
                            .unit(unit.getSymbol()).tags(tags).build());
                }
            }
        } catch (Exception e) {
            // MBean 不存在，跳过
        }
    }

    @Override
    public String getCollectorName() {
        return "broker";
    }
}
