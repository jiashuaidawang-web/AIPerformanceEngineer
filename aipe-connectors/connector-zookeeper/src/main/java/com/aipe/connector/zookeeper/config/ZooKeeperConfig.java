package com.aipe.connector.zookeeper.config;

import com.aipe.connector.sdk.config.ConnectorConfig;

public class ZooKeeperConfig {

    private String host = "localhost";
    private int port = 2181;

    public static ZooKeeperConfig defaultConfig() {
        return new ZooKeeperConfig();
    }

    public static ZooKeeperConfig fromConnectorConfig(ConnectorConfig sdkConfig) {
        ZooKeeperConfig config = new ZooKeeperConfig();
        if (sdkConfig.getProperties() != null) {
            sdkConfig.getProperties().forEach((k, v) -> {
                switch (k) {
                    case "host": config.host = v; break;
                    case "port": config.port = Integer.parseInt(v); break;
                }
            });
        }
        return config;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
}
