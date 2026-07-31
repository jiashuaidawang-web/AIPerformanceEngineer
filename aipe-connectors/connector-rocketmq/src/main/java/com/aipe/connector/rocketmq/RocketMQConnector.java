package com.aipe.connector.rocketmq;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.rocketmq.collector.*;
import com.aipe.connector.rocketmq.config.RocketMQConfig;
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
 * RocketMQ Connector - 通过 JMX + 控制台 API 采集 RocketMQ 指标
 *
 * <p>采集指标:
 * <ul>
 *   <li>Broker: 消息堆积、发送/消费 TPS</li>
 *   <li>Topic: 消息量、延迟</li>
 *   <li>ConsumerGroup: 消费进度、Lag</li>
 * </ul>
 */
public class RocketMQConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(RocketMQConnector.class);

    private RocketMQConfig rocketMQConfig;
    private final List<RocketMQCollector> collectors = new CopyOnWriteArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (rocketMQConfig != null) return "rocketmq-" + rocketMQConfig.getHost() + "-" + rocketMQConfig.getPort();
        return "rocketmq-unknown";
    }

    @Override
    public String getConnectorType() {
        return "ROCKETMQ";
    }

    @Override
    public String getTargetResource() {
        if (rocketMQConfig != null) return "rocketmq-" + rocketMQConfig.getHost() + ":" + rocketMQConfig.getPort();
        return "rocketmq-unknown";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.rocketMQConfig = RocketMQConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.rocketMQConfig = RocketMQConfig.defaultConfig();
            }
        } else {
            this.rocketMQConfig = RocketMQConfig.defaultConfig();
        }
        if (this.connectorId == null) this.connectorId = getConnectorId();
        initCollectors();
        log.info("RocketMQConnector initialized. agentId={}, host={}", agentId, rocketMQConfig.getHost());
    }

    private void initCollectors() {
        if (rocketMQConfig.getBrokerEnabled()) collectors.add(new BrokerCollector());
        if (rocketMQConfig.getTopicEnabled()) collectors.add(new TopicCollector());
        if (rocketMQConfig.getConsumerEnabled()) collectors.add(new ConsumerCollector());
        log.info("Initialized {} RocketMQ collectors", collectors.size());
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();

        for (RocketMQCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(rocketMQConfig, agentId, connectorId);
                if (data != null && !data.isEmpty()) allResults.addAll(data);
            } catch (Exception e) {
                log.error("RocketMQ collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }
        return allResults;
    }

    @Override
    protected void onStart() { log.info("RocketMQConnector starting..."); }

    @Override
    protected void onStop() { log.info("RocketMQConnector stopping..."); }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("RocketMQConnector destroyed.");
    }
}
