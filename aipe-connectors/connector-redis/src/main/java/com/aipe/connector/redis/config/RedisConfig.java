package com.aipe.connector.redis.config;

import com.aipe.connector.sdk.config.ConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RedisConfig implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String connectorId;
    private String targetResource;
    private Boolean enabled;
    private String host;
    private Integer port;
    private String password;
    private Integer database;
    private Long timeoutMs;
    private Boolean tlsEnabled;
    private Long intervalMs;
    private String mode; // STANDALONE / SENTINEL / CLUSTER

    public static RedisConfig defaultConfig() {
        return RedisConfig.builder()
                .host("localhost").port(6379).database(0)
                .timeoutMs(5000L).tlsEnabled(false)
                .intervalMs(30000L).enabled(true)
                .mode("STANDALONE").build();
    }

    public static RedisConfig fromConnectorConfig(ConnectorConfig config) {
        RedisConfig rc = defaultConfig();
        rc.setConnectorId(config.getConnectorId());
        if (config.getTargetResource() != null) rc.setTargetResource(config.getTargetResource());
        if (config.getIntervalMs() != null) rc.setIntervalMs(config.getIntervalMs());
        if (config.getTimeoutMs() != null) rc.setTimeoutMs(config.getTimeoutMs());
        if (config.getProperties() != null) {
            String v;
            if ((v = config.getProperties().get("host")) != null) rc.setHost(v);
            if ((v = config.getProperties().get("port")) != null) rc.setPort(Integer.parseInt(v));
            if ((v = config.getProperties().get("password")) != null) rc.setPassword(v);
            if ((v = config.getProperties().get("database")) != null) rc.setDatabase(Integer.parseInt(v));
            if ((v = config.getProperties().get("tlsEnabled")) != null) rc.setTlsEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("mode")) != null) rc.setMode(v);
        }
        return rc;
    }
}
