package com.aipe.connector.redis.builder;

import com.aipe.common.domain.ObservationData;

import java.util.HashMap;
import java.util.Map;

public class RedisObservationBuilder {
    private String agentId;
    private String connectorId;
    private String targetResource;
    private String host;
    private Integer port;
    private Map<String, String> baseTags = new HashMap<>();

    public static RedisObservationBuilder create() { return new RedisObservationBuilder(); }

    public RedisObservationBuilder agentId(String agentId) { this.agentId = agentId; return this; }
    public RedisObservationBuilder connectorId(String connectorId) { this.connectorId = connectorId; return this; }
    public RedisObservationBuilder targetResource(String targetResource) { this.targetResource = targetResource; return this; }
    public RedisObservationBuilder host(String host) { this.host = host; return this; }
    public RedisObservationBuilder port(Integer port) { this.port = port; return this; }
    public RedisObservationBuilder addTag(String key, String value) { if (key != null && value != null) baseTags.put(key, value); return this; }

    public ObservationData build(String metricName, double value, String unit) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.put("source", "redis-info");
        if (host != null) tags.put("host", host);
        if (port != null) tags.put("port", String.valueOf(port));
        return ObservationData.builder()
                .agentId(agentId).connectorId(connectorId).connectorType("REDIS")
                .targetResource(targetResource != null ? targetResource : "redis-node")
                .collectTime(System.currentTimeMillis())
                .metricName(metricName).metricValue(value).unit(unit)
                .tags(tags).build();
    }
}
