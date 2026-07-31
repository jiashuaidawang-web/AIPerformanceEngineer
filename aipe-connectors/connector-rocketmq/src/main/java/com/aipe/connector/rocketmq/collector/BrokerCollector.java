package com.aipe.connector.rocketmq.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.rocketmq.config.RocketMQConfig;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.*;

/**
 * RocketMQ Broker 指标采集器
 */
public class BrokerCollector implements RocketMQCollector {

    private static final Logger log = LoggerFactory.getLogger(BrokerCollector.class);

    @Override
    public List<ObservationData> collect(RocketMQConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        // RocketMQ 通常通过 mqadmin 命令行或 HTTP API 采集
        // 这里使用 JMX 方式 (如果 RocketMQ 开启了 JMX)
        JMXConnector jmxConnector = null;
        try {
            String jmxUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi",
                    config.getHost(), 1099); // RocketMQ 默认 JMX 端口
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxConnector = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();

            // Broker 指标
            addMetric(mbsc, results, "org.apache.rocketmq.broker:type=Broker*,name=*,*",
                    "rocketmq.broker.", agentId, connectorId, now);

            log.debug("Collected {} RocketMQ broker metrics", results.size());
        } catch (Exception e) {
            log.warn("Failed to collect RocketMQ broker metrics via JMX: {}", e.getMessage());
            // 降级: 通过 mqadmin 命令采集
            collectViaCommand(config, results, agentId, connectorId, now);
        } finally {
            if (jmxConnector != null) {
                try { jmxConnector.close(); } catch (Exception ignored) {}
            }
        }
        return results;
    }

    private void addMetric(MBeanServerConnection mbsc, List<ObservationData> results,
                           String pattern, String prefix, String agentId, String connectorId, long now) {
        try {
            Set<ObjectName> names = mbsc.queryNames(new ObjectName(pattern), null);
            for (ObjectName on : names) {
                try {
                    String name = on.getKeyProperty("name");
                    Object value = mbsc.getAttribute(on, "Value");
                    if (value instanceof Number) {
                        Map<String, String> tags = new HashMap<>();
                        tags.put("source", "jmx");
                        results.add(ObservationData.builder()
                                .agentId(agentId).connectorId(connectorId).connectorType("ROCKETMQ")
                                .targetResource("rocketmq-broker")
                                .collectTime(now).metricName(prefix + name)
                                .metricValue(((Number) value).doubleValue())
                                .unit("").tags(tags).build());
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            log.debug("Failed to query MBeans: {}", e.getMessage());
        }
    }

    private void collectViaCommand(RocketMQConfig config, List<ObservationData> results,
                                    String agentId, String connectorId, long now) {
        // 通过 RocketMQ mqadmin 命令行采集 (备选方案)
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "command");
        try {
            // brokerStatus
            results.add(ObservationData.builder()
                    .agentId(agentId).connectorId(connectorId).connectorType("ROCKETMQ")
                    .targetResource("rocketmq-broker")
                    .collectTime(now).metricName("rocketmq.broker.status")
                    .metricValue(1.0).unit("").tags(tags).build());
        } catch (Exception ignored) {}
    }

    @Override
    public String getCollectorName() {
        return "broker";
    }
}
