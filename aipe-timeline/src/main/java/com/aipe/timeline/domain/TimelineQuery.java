package com.aipe.timeline.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Timeline 查询参数（值对象）
 *
 * <p>对齐 WP014 Blueprint §4.4 TimelineQuery
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TimelineQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID（必填）
     */
    private final String resourceId;

    /**
     * 指标名称（可为空 = 全指标）
     */
    private final String metricName;

    /**
     * 开始时间（毫秒）
     */
    private final long startTime;

    /**
     * 结束时间（毫秒）
     */
    private final long endTime;

    /**
     * 限制条数
     */
    private final int limit;

    public TimelineQuery(String resourceId, String metricName, long startTime, long endTime, int limit) {
        this.resourceId = resourceId;
        this.metricName = metricName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.limit = limit > 0 ? limit : 10000;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getLimit() {
        return limit;
    }

    /**
     * 是否针对特定指标（false = 全指标）
     */
    public boolean isSpecificMetric() {
        return metricName != null && !metricName.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelineQuery that = (TimelineQuery) o;
        return Objects.equals(resourceId, that.resourceId)
                && Objects.equals(metricName, that.metricName)
                && startTime == that.startTime && endTime == that.endTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, metricName, startTime, endTime);
    }

    @Override
    public String toString() {
        return "TimelineQuery{resource='" + resourceId + '\'' +
                ", metric='" + metricName + '\'' +
                ", range=[" + startTime + "," + endTime + "]" +
                ", limit=" + limit + '}';
    }
}
