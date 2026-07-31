package com.aipe.connector.rocketmq.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.rocketmq.config.RocketMQConfig;
import com.aipe.connector.sdk.exception.ConnectorException;
import java.util.List;

/**
 * RocketMQ 采集器接口
 */
public interface RocketMQCollector {
    List<ObservationData> collect(RocketMQConfig config, String agentId, String connectorId) throws ConnectorException;
    String getCollectorName();
}
