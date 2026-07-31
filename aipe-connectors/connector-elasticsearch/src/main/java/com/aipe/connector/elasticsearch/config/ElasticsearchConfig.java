package com.aipe.connector.elasticsearch.config;

import com.aipe.connector.sdk.config.ConnectorConfig;

public class ElasticsearchConfig {

    private String host = "localhost";
    private int port = 9200;
    private String username;
    private String password;

    public static ElasticsearchConfig defaultConfig() {
        return new ElasticsearchConfig();
    }

    public static ElasticsearchConfig fromConnectorConfig(ConnectorConfig sdkConfig) {
        ElasticsearchConfig config = new ElasticsearchConfig();
        if (sdkConfig.getProperties() != null) {
            sdkConfig.getProperties().forEach((k, v) -> {
                switch (k) {
                    case "host": config.host = v; break;
                    case "port": config.port = Integer.parseInt(v); break;
                    case "username": config.username = v; break;
                    case "password": config.password = v; break;
                }
            });
        }
        return config;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
