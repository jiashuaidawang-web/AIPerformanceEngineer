package com.aipe.observation.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Observation 聚合根
 *
 * <p>AI World 中唯一合法的 Runtime Fact（Runtime Fact）：某一个 Resource 在某一个时间点产生的一条不可变运行事实。
 *
 * <p>不可变特征（M2-006 ch4 / Law-002）：
 * <ul>
 *   <li>Immutable：一旦产生，禁止修改，只能新增</li>
 *   <li>Timestamped：毫秒级采集时间（IM-004 对齐）</li>
 *   <li>Resource Scoped：resourceId 必填，不存在脱离 Resource 的 Observation</li>
 *   <li>Traceable：可追溯 Connector / Agent / Source / Collection Time</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Observation {

    /**
     * 业务主键（值对象）
     */
    private final ObservationId observationId;

    /**
     * 所属 Resource（必填 - Law-002: Observation Belongs To Resource）
     */
    private final String resourceId;

    /**
     * Observation 类型（Metric / Log / Trace / Event / Snapshot）
     */
    private final ObservationType type;

    /**
     * 数据来源（JVM / Linux / Redis / MySQL / Prometheus）
     */
    private final ObservationSource source;

    /**
     * 指标名（heap.used / cpu.usage）
     */
    private final String name;

    /**
     * 指标值
     */
    private final Double value;

    /**
     * 单位（ms / % / bytes）
     */
    private final String unit;

    /**
     * 采集时间（毫秒级 - IM-004）
     */
    private final long timestamp;

    /**
     * Connector 标识
     */
    private final String connectorId;

    /**
     * 扩展标签
     */
    private final Map<String, String> labels;

    /**
     * 原始数据（JSON）
     */
    private final String payload;

    /**
     * 构造函数（包级私有，强制使用 Factory 创建）
     */
    Observation(ObservationId observationId,
                String resourceId,
                ObservationType type,
                ObservationSource source,
                String name,
                Double value,
                String unit,
                long timestamp,
                String connectorId,
                Map<String, String> labels,
                String payload) {
        this.observationId = observationId;
        this.resourceId = resourceId;
        this.type = type;
        this.source = source;
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
        this.connectorId = connectorId;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.payload = payload;
    }

    // ==================== 业务方法 ====================

    /**
     * 校验 Observation 是否合法
     *
     * <p>必须满足：resourceId + timestamp + type + name + value 必填
     *
     * @throws IllegalArgumentException 校验失败
     */
    public void validate() {
        if (observationId == null) {
            throw new IllegalArgumentException("ObservationId is required");
        }
        if (resourceId == null || resourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("ResourceId is required (Law-002: Observation must belong to a Resource)");
        }
        if (type == null) {
            throw new IllegalArgumentException("ObservationType is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Observation name is required");
        }
        if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Observation value must be a valid number");
        }
        if (timestamp <= 0) {
            throw new IllegalArgumentException("Observation timestamp must be a positive long (milliseconds)");
        }
    }

    /**
     * 是否属于指定 Resource
     *
     * @param resourceId 资源 ID
     * @return 是否属于（null-safe）
     */
    public boolean belongsTo(String resourceId) {
        return this.resourceId != null && this.resourceId.equals(resourceId);
    }

    /**
     * 是否处于时间范围内
     *
     * @param startTime 开始时间（毫秒）
     * @param endTime   结束时间（毫秒）
     * @return 是否在范围内（含端点）
     */
    public boolean isWithin(long startTime, long endTime) {
        return this.timestamp >= startTime && this.timestamp <= endTime;
    }

    /**
     * 是否属于指定指标类型
     *
     * @param type 类型
     * @return 是否匹配
     */
    public boolean isType(ObservationType type) {
        return this.type == type;
    }

    // ==================== Getter ====================

    public ObservationId getObservationId() {
        return observationId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ObservationType getType() {
        return type;
    }

    public ObservationSource getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation that = (Observation) o;
        return Objects.equals(observationId, that.observationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(observationId);
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + observationId +
                ", resourceId='" + resourceId + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
