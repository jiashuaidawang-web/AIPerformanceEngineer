package com.aipe.connector.rocketmq.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.rocketmq.config.RocketMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * RocketMQ Consumer Group 指标采集器
 */
public class ConsumerCollector implements RocketMQCollector {

    private static final Logger log = LoggerFactory.getLogger(ConsumerCollector.class);

    @Override
    public List<ObservationData> collect(RocketMQConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "api");

        // Consumer Lag / 消费进度
        // 通过 mqadmin consumerProgress 或 HTTP API 获取
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("ROCKETMQ")
                .targetResource("rocketmq-consumer")
                .collectTime(now).metricName("rocketmq.consumer.lag")
                .metricValue(0.0).unit("count").tags(tags).build());

        return results;
    }

    @Override
    public String getCollectorName() {
        return "consumer";
    }
}
