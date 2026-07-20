package com.aipe.observation.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * Observation 查询/承接请求 DTO
 *
 * <p>用于 POST /api/v1/observations（单条入库）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ObservationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属 Resource（必填 - Law-002）
     */
    @NotBlank(message = "resourceId is required")
    private String resourceId;

    /**
     * Observation 类型（METRIC / LOG / TRACE / EVENT / SNAPSHOT）
     */
    private String type;

    /**
     * 数据来源（JVM / LINUX / REDIS / MYSQL / PROMETHEUS）
     */
    private String source;

    /**
     * 指标名
     */
    @NotBlank(message = "name is required")
    private String name;

    /**
     * 指标值
     */
    @NotNull(message = "value is required")
    private Double value;

    /**
     * 单位（ms / % / bytes）
     */
    private String unit;

    /**
     * 采集时间（毫秒级 - IM-004）（为空则使用当前时间）
     */
    private Long timestamp;

    /**
     * Connector 标识
     */
    private String connectorId;

    /**
     * 扩展标签
     */
    private Map<String, String> labels;

    /**
     * 原始数据（JSON）
     */
    private String payload;

    // ==================== Getter & Setter ====================

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
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
