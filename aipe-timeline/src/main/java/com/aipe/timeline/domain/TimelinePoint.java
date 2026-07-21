package com.aipe.timeline.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Timeline 中的单个观察点（值对象）
 *
 * <p>对齐 WP014 Blueprint §4.2 TimelinePoint
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TimelinePoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 时间戳（毫秒 - IM-004）
     */
    private final long timestamp;

    /**
     * 观察值
     */
    private final double value;

    /**
     * 单位
     */
    private final String unit;

    /**
     * Connector 标识
     */
    private final String connectorId;

    /**
     * 扩展标签
     */
    private final Map<String, String> labels;

    public TimelinePoint(long timestamp, double value, String unit, String connectorId, Map<String, String> labels) {
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit != null ? unit : "";
        this.connectorId = connectorId != null ? connectorId : "";
        this.labels = labels != null ? Collections.unmodifiableMap(labels) : Collections.emptyMap();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelinePoint that = (TimelinePoint) o;
        return timestamp == that.timestamp && Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value);
    }

    @Override
    public String toString() {
        return "TimelinePoint{timestamp=" + timestamp + ", value=" + value + '}';
    }
}
