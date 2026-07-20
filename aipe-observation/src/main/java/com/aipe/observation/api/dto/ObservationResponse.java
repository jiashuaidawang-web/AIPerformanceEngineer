package com.aipe.observation.api.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * Observation 响应 DTO
 *
 * <p>对齐 IM-006 REST API Mapping
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ObservationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** observation_id */
    private String observationId;

    /** 所属 Resource */
    private String resourceId;

    /** Observation 类型 */
    private String type;

    /** 数据来源 */
    private String source;

    /** 指标名 */    private String name;

    /** 指标值 */
    private Double value;

    /** 单位 */
    private String unit;

    /** 采集时间（毫秒级） */
    private long timestamp;

    /** Connector 标识 */
    private String connectorId;

    /** 扩展标签 */
    private Map<String, String> labels;

    /** 原始数据（JSON） */
    private String payload;

    // ==================== Getter & Setter ====================

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
