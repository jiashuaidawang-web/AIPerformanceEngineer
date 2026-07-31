package com.aipe.connector.kafka.config;

import com.aipe.connector.sdk.config.ConnectorConfig;

/**
 * Kafka 配置
 */
public class KafkaConfig {

    private String host = "localhost";
    private int port = 9092;
    private int jmxPort = 9999;
    private String jmxHost = "localhost";

    // 采集器开关
    private boolean brokerEnabled = true;
    private boolean producerEnabled = true;
    private boolean consumerEnabled = true;
    private boolean topicEnabled = true;

    public static KafkaConfig defaultConfig() {
        return new KafkaConfig();
    }

    public static KafkaConfig fromConnectorConfig(ConnectorConfig sdkConfig) {
        KafkaConfig config = new KafkaConfig();
        if (sdkConfig.getProperties() != null) {
            sdkConfig.getProperties().forEach((k, v) -> {
                switch (k) {
                    case "host": config.host = v; break;
                    case "port": config.port = Integer.parseInt(v); break;
                    case "jmxHost": config.jmxHost = v; break;
                    case "jmxPort": config.jmxPort = Integer.parseInt(v); break;
                    case "brokerEnabled": config.brokerEnabled = Boolean.parseBoolean(v); break;
                    case "producerEnabled": config.producerEnabled = Boolean.parseBoolean(v); break;
                    case "consumerEnabled": config.consumerEnabled = Boolean.parseBoolean(v); break;
                    case "topicEnabled": config.topicEnabled = Boolean.parseBoolean(v); break;
                }
            });
        }
        // 如果 jmxHost 未设置，默认使用 host
        if (config.jmxHost == null) config.jmxHost = config.host;
        return config;
    }

    public boolean isJmxAvailable() {
        return jmxPort > 0 && jmxHost != null;
    }

    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getJmxPort() { return jmxPort; }
    public void setJmxPort(int jmxPort) { this.jmxPort = jmxPort; }
    public String getJmxHost() { return jmxHost; }
    public void setJmxHost(String jmxHost) { this.jmxHost = jmxHost; }
    public boolean getBrokerEnabled() { return brokerEnabled; }
    public void setBrokerEnabled(boolean brokerEnabled) { this.brokerEnabled = brokerEnabled; }
    public boolean getProducerEnabled() { return producerEnabled; }
    public void setProducerEnabled(boolean producerEnabled) { this.producerEnabled = producerEnabled; }
    public boolean getConsumerEnabled() { return consumerEnabled; }
    public void setConsumerEnabled(boolean consumerEnabled) { this.consumerEnabled = consumerEnabled; }
    public boolean getTopicEnabled() { return topicEnabled; }
    public void setTopicEnabled(boolean topicEnabled) { this.topicEnabled = topicEnabled; }
}
