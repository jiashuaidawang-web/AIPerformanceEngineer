package com.aipe.connector.rocketmq.config;

import com.aipe.connector.sdk.config.ConnectorConfig;

/**
 * RocketMQ 配置
 */
public class RocketMQConfig {

    private String host = "localhost";
    private int port = 9876;       // Broker 端口
    private int consolePort = 8080; // 控制台端口
    private String namesrvAddr;    // NameServer 地址
    private boolean brokerEnabled = true;
    private boolean topicEnabled = true;
    private boolean consumerEnabled = true;

    public static RocketMQConfig defaultConfig() {
        return new RocketMQConfig();
    }

    public static RocketMQConfig fromConnectorConfig(ConnectorConfig sdkConfig) {
        RocketMQConfig config = new RocketMQConfig();
        if (sdkConfig.getProperties() != null) {
            sdkConfig.getProperties().forEach((k, v) -> {
                switch (k) {
                    case "host": config.host = v; break;
                    case "port": config.port = Integer.parseInt(v); break;
                    case "consolePort": config.consolePort = Integer.parseInt(v); break;
                    case "namesrvAddr": config.namesrvAddr = v; break;
                }
            });
        }
        return config;
    }

    // Getters
    public String getHost() { return host; }
    public int getPort() { return port; }
    public int getConsolePort() { return consolePort; }
    public String getNamesrvAddr() { return namesrvAddr != null ? namesrvAddr : host + ":9876"; }
    public boolean getBrokerEnabled() { return brokerEnabled; }
    public boolean getTopicEnabled() { return topicEnabled; }
    public boolean getConsumerEnabled() { return consumerEnabled; }
}
