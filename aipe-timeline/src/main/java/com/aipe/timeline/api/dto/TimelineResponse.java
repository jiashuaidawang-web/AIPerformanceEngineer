package com.aipe.timeline.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Timeline 响应 DTO
 *
 * <p>含 points（按时间排序的观察序列）+ stats（统计特征）
 * <p>对齐 WP014 Blueprint §4.1 Timeline
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class TimelineResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timelineId;
    private String resourceId;
    private String metricName;
    private long startTime;
    private long endTime;
    private int pointCount;
    private List<TimelinePointDto> points;
    private TimelineStatsDto stats;

    public String getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(String timelineId) {
        this.timelineId = timelineId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public List<TimelinePointDto> getPoints() {
        return points;
    }

    public void setPoints(List<TimelinePointDto> points) {
        this.points = points;
    }

    public TimelineStatsDto getStats() {
        return stats;
    }

    public void setStats(TimelineStatsDto stats) {
        this.stats = stats;
    }

    /**
     * 观察点 DTO
     */
    public static class TimelinePointDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private long timestamp;
        private double value;
        private String unit;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    /**
     * 统计特征 DTO
     */    public static class TimelineStatsDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private double min;
        private double max;
        private double avg;
        private double stdDev;
        private int count;

        public double getMin() {
            return min;
        }

        public void setMin(double min) {            this.min = min;
        }        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getAvg() {
            return avg;        }

        public void setAvg(double avg) {
            this.avg = avg;
        }

        public double getStdDev() {
            return stdDev;
        }

        public void setStdDev(double stdDev) {            this.stdDev = stdDev;
        }        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;        }
    }
}
