package com.aipe.timeline.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Timeline 值对象（运行时计算，不存储）
 *
 * <p>Persistence Law-004：Timeline Is Computed, Never Stored
 * <p>M2-010 Timeline Model：Timeline 是 Resource 在连续时间上的 Observation 有序集合。
 *
 * <p>Timeline 不是时间序列（Time Series）；Timeline 是 Resource 的运行历史。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Timeline {

    /**
     * Timeline 标识：TL-{resourceId}-{metricName}-{start}-{end}
     */
    private final String timelineId;

    /**
     * 所属 Resource（必填 - Law-002）
     */
    private final String resourceId;

    /**
     * 指标名称
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
     * 按时间排序的观察序列
     */
    private final List<TimelinePoint> points;

    /**
     * 统计特征
     */
    private final TimelineStats stats;

    /**
     * 构建时间戳
     */
    private final long builtAt;

    public Timeline(String timelineId,
                    String resourceId,
                    String metricName,
                    long startTime,
                    long endTime,
                    List<TimelinePoint> points,
                    TimelineStats stats,
                    long builtAt) {
        this.timelineId = timelineId;
        this.resourceId = resourceId;
        this.metricName = metricName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points != null ? Collections.unmodifiableList(points) : Collections.emptyList();
        this.stats = stats;
        this.builtAt = builtAt;
    }

    public String getTimelineId() {
        return timelineId;
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

    public List<TimelinePoint> getPoints() {
        return points;
    }

    public TimelineStats getStats() {
        return stats;
    }

    public long getBuiltAt() {
        return builtAt;
    }

    /**
     * 是否空 Timeline（无观察点）
     */
    public boolean isEmpty() {
        return points == null || points.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeline timeline = (Timeline) o;
        return Objects.equals(timelineId, timeline.timelineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timelineId);
    }

    @Override
    public String toString() {
        return "Timeline{" +
                "id='" + timelineId + '\'' +
                ", resource='" + resourceId + '\'' +
                ", metric='" + metricName + '\'' +
                ", points=" + points.size() +
                ", stats=" + stats +
                '}';
    }
}
