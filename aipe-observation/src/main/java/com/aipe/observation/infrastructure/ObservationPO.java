package com.aipe.observation.infrastructure;

import java.io.Serializable;

/**
 * Observation 持久化对象（PO）
 *
 * <p>对应 ClickHouse observation_fact 表（对齐 IM-004 / WP012 Blueprint §6.1）
 * <p>Gateway Law-001：Repository 不返回 PO，返回 Domain（通过 Converter 转换）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ObservationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Observation 唯一标识
     */
    private String observationId;

    /**
     * 所属 Resource（必填 - Law-002）
     */
    private String resourceId;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 指标名
     */
    private String metricName;

    /**
     * Observation 类型（Metric/Log/Trace/Event/Snapshot）
     */
    private String metricType;

    /**
     * 指标值
     */
    private double metricValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数据来源
     */
    private String source;

    /**
     * Connector 标识
     */
    private String connectorId;

    /**
     * 扩展标签（JSON String）
     */
    private String labels;

    /**
     * 原始数据（JSON String）
     */
    private String payload;

    /**
     * 采集时间（毫秒级 - IM-004）
     */
    private long timestamp;

    /**
     * 入库时间（毫秒级）
     */
    private long receivedAt;

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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public String toString() {
        return "ObservationPO{" +
                "observationId='" + observationId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", timestamp=" + timestamp +
                '}';
    }
}
