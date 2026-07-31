package com.aipe.connector.kafka;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.kafka.config.KafkaConfig;
import com.aipe.connector.sdk.exception.ConnectorException;

import java.util.List;

/**
 * Kafka 采集器接口
 */
public interface KafkaCollector {
    /**
     * 采集指标
     * @param config Kafka 配置
     * @param agentId Agent ID
     * @param connectorId Connector ID
     * @return 观测数据列表
     * @throws ConnectorException 采集异常
     */
    List<ObservationData> collect(KafkaConfig config, String agentId, String connectorId) throws ConnectorException;

    /**
     * 采集器名称
     */
    String getCollectorName();
}
