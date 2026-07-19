package com.aipe.connector.linux.builder;

import com.aipe.common.domain.ObservationData;

import java.util.HashMap;
import java.util.Map;

public class LinuxObservationBuilder {

    private String agentId;
    private String connectorId;
    private String targetResource = "linux-local";
    private Map<String, String> baseTags = new HashMap<>();

    public static LinuxObservationBuilder create() {
        return new LinuxObservationBuilder();
    }

    public LinuxObservationBuilder agentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public LinuxObservationBuilder connectorId(String connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public LinuxObservationBuilder targetResource(String targetResource) {
        this.targetResource = targetResource;
        return this;
    }

    public LinuxObservationBuilder addTag(String key, String value) {
        if (key != null && value != null) {
            this.baseTags.put(key, value);
        }
        return this;
    }

    public ObservationData build(String metricName, double value, String unit) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.put("source", "proc");
        return ObservationData.builder()
                .agentId(agentId)
                .connectorId(connectorId)
                .connectorType("LINUX")
                .targetResource(targetResource)
                .collectTime(System.currentTimeMillis())
                .metricName(metricName)
                .metricValue(value)
                .unit(unit)
                .tags(tags)
                .build();
    }
}
