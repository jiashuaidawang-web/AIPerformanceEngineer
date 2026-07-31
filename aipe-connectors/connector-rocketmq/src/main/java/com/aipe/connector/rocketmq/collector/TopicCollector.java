package com.aipe.connector.rocketmq.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.rocketmq.config.RocketMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * RocketMQ Topic 指标采集器
 */
public class TopicCollector implements RocketMQCollector {

    private static final Logger log = LoggerFactory.getLogger(TopicCollector.class);

    @Override
    public List<ObservationData> collect(RocketMQConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        Map<String, String> tags = new HashMap<>();
        tags.put("source", "api");

        // Topic 消息堆积量 (通过 mqadmin 或 HTTP API)
        // 这里提供基础框架，实际部署时根据 RocketMQ 版本调整
        results.add(ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("ROCKETMQ")
                .targetResource("rocketmq-topic")
                .collectTime(now).metricName("rocketmq.topic.queue.count")
                .metricValue(0.0).unit("count").tags(tags).build());

        return results;
    }

    @Override
    public String getCollectorName() {
        return "topic";
    }
}
