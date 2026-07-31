package com.aipe.connector.kafka;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.kafka.collector.*;
import com.aipe.connector.kafka.config.KafkaConfig;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Kafka Connector - 通过 JMX 采集 Kafka 指标
 *
 * <p>采集指标:
 * <ul>
 *   <li>Broker: 消息吞吐、分区数、ISR、Leader 选举</li>
 *   <li>Producer: 发送速率、延迟、错误率</li>
 *   <li>Consumer: 消费速率、Lag、重平衡</li>
 *   <li>Topic: 消息堆积、分区分布</li>
 * </ul>
 *
 * <p>对接方式: JMX (Kafka 默认开启 JMX 端口)
 * <p>需要重启: ❌ 不需要
 * <p>客户改动: 开放 JMX 端口或配置 JMX exporter
 */
public class KafkaConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(KafkaConnector.class);

    private KafkaConfig kafkaConfig;
    private final List<KafkaCollector> collectors = new CopyOnWriteArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (kafkaConfig != null) return "kafka-" + kafkaConfig.getHost() + "-" + kafkaConfig.getJmxPort();
        return "kafka-unknown";
    }

    @Override
    public String getConnectorType() {
        return "KAFKA";
    }

    @Override
    public String getTargetResource() {
        if (kafkaConfig != null) return "kafka-" + kafkaConfig.getHost() + ":" + kafkaConfig.getPort();
        return "kafka-unknown";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.kafkaConfig = KafkaConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.kafkaConfig = KafkaConfig.defaultConfig();
            }
        } else {
            this.kafkaConfig = KafkaConfig.defaultConfig();
        }

        if (this.connectorId == null) this.connectorId = getConnectorId();
        initCollectors();
        log.info("KafkaConnector initialized. agentId={}, host={}, jmxPort={}",
                agentId, kafkaConfig.getHost(), kafkaConfig.getJmxPort());
    }

    private void initCollectors() {
        if (kafkaConfig.getBrokerEnabled()) collectors.add(new BrokerCollector());
        if (kafkaConfig.getProducerEnabled()) collectors.add(new ProducerCollector());
        if (kafkaConfig.getConsumerEnabled()) collectors.add(new ConsumerCollector());
        if (kafkaConfig.getTopicEnabled()) collectors.add(new TopicCollector());
        log.info("Initialized {} Kafka collectors", collectors.size());
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();
        if (!kafkaConfig.isJmxAvailable()) {
            log.warn("Kafka JMX not available, skipping collect");
            return allResults;
        }

        for (KafkaCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(kafkaConfig, agentId, connectorId);
                if (data != null && !data.isEmpty()) allResults.addAll(data);
            } catch (Exception e) {
                log.error("Kafka collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }

        return allResults;
    }

    @Override
    protected void onStart() {
        log.info("KafkaConnector starting... agentId={}", agentId);
    }

    @Override
    protected void onStop() {
        log.info("KafkaConnector stopping...");
    }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("KafkaConnector destroyed.");
    }

    public List<KafkaCollector> getCollectors() {
        return new ArrayList<>(collectors);
    }

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }
}
