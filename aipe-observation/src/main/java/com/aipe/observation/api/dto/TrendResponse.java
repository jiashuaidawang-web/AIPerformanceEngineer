package com.aipe.observation.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 趋势响应 DTO
 *
 * <p>对齐 WP012 Blueprint §8 trend 响应
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class TrendResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资源 ID */
    private String resourceId;

    /** 指标名 */
    private String metricName;

    /** 时间桶（1m / 5m / 1h / 1d） */
    private String interval;

    /** 趋势点列表 */
    private List<TrendPoint> points;

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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<TrendPoint> getPoints() {
        return points;
    }

    public void setPoints(List<TrendPoint> points) {
        this.points = points;
    }

    /**
     * 趋势数据点
     */
    public static class TrendPoint implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 时间桶起点（毫秒） */
        private long timestamp;

        /** 平均值 */
        private double avg;

        /** 最大值 */
        private double max;

        /** 最小值 */
        private double min;

        /** 样本数 */
        private int count;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public double getAvg() {
            return avg;
        }

        public void setAvg(double avg) {
            this.avg = avg;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
