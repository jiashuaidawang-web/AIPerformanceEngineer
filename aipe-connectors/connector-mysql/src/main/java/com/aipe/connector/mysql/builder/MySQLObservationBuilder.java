package com.aipe.connector.mysql.builder;

import com.aipe.common.domain.ObservationData;

import java.util.HashMap;
import java.util.Map;

public class MySQLObservationBuilder {
    private String agentId;
    private String connectorId;
    private String targetResource;
    private String host;
    private Integer port;
    private Map<String, String> baseTags = new HashMap<>();

    public static MySQLObservationBuilder create() { return new MySQLObservationBuilder(); }
    public MySQLObservationBuilder agentId(String agentId) { this.agentId = agentId; return this; }
    public MySQLObservationBuilder connectorId(String connectorId) { this.connectorId = connectorId; return this; }
    public MySQLObservationBuilder targetResource(String targetResource) { this.targetResource = targetResource; return this; }
    public MySQLObservationBuilder host(String host) { this.host = host; return this; }
    public MySQLObservationBuilder port(Integer port) { this.port = port; return this; }
    public MySQLObservationBuilder addTag(String key, String value) { if (key != null && value != null) baseTags.put(key, value); return this; }

    public ObservationData build(String metricName, double value, String unit) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.put("source", "mysql");
        if (host != null) tags.put("host", host);
        if (port != null) tags.put("port", String.valueOf(port));
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("MYSQL")
                .targetResource(targetResource != null ? targetResource : "mysql-node")
                .collectTime(System.currentTimeMillis())
                .metricName(metricName).metricValue(value).unit(unit).tags(tags).build();
    }
}
