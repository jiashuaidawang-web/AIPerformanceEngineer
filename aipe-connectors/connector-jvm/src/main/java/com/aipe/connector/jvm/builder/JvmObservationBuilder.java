package com.aipe.connector.jvm.builder;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.jvm.config.JvmConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * JVM Observation 构建器
 *
 * <p>统一构建 Observation 的标签和元信息。
 */
public class JvmObservationBuilder {

    private String agentId;
    private String connectorId;
    private String connectorType = "JVM";
    private String targetResource = "jvm-local";
    private Map<String, String> baseTags = new HashMap<>();

    public static JvmObservationBuilder create() {
        return new JvmObservationBuilder();
    }

    public JvmObservationBuilder agentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public JvmObservationBuilder connectorId(String connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public JvmObservationBuilder targetResource(String targetResource) {
        this.targetResource = targetResource;
        return this;
    }

    public JvmObservationBuilder addTag(String key, String value) {
        if (key != null && value != null) {
            this.baseTags.put(key, value);
        }
        return this;
    }

    public ObservationData build(String metricName, double value, String unit) {
        Map<String, String> tags = new HashMap<>(baseTags);
        tags.put("source", "jmx");
        return ObservationData.builder()
                .agentId(agentId)
                .connectorId(connectorId)
                .connectorType(connectorType)
                .targetResource(targetResource)
                .collectTime(System.currentTimeMillis())
                .metricName(metricName)
                .metricValue(value)
                .unit(unit)
                .tags(tags)
                .build();
    }
}
