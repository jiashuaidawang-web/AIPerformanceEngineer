package com.aipe.connector.mysql.config;

import com.aipe.connector.sdk.config.ConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MySQLConfig implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String connectorId;
    private String targetResource;
    private Boolean enabled;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String database;
    private Long timeoutMs;
    private Long intervalMs;

    public static MySQLConfig defaultConfig() {
        return MySQLConfig.builder()
                .host("localhost").port(3306).database("aipe_metadata")
                .timeoutMs(5000L).intervalMs(3000L).enabled(true).build();
    }

    public static MySQLConfig fromConnectorConfig(ConnectorConfig config) {
        MySQLConfig mc = defaultConfig();
        mc.setConnectorId(config.getConnectorId());
        if (config.getProperties() != null) {
            String v;
            if ((v = config.getProperties().get("host")) != null) mc.setHost(v);
            if ((v = config.getProperties().get("port")) != null) mc.setPort(Integer.parseInt(v));
            if ((v = config.getProperties().get("user")) != null) mc.setUser(v);
            if ((v = config.getProperties().get("password")) != null) mc.setPassword(v);
            if ((v = config.getProperties().get("database")) != null) mc.setDatabase(v);
        }
        return mc;
    }
}
