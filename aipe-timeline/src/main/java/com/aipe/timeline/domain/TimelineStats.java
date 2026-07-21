package com.aipe.timeline.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Timeline 统计特征（值对象，运行时自动计算）
 *
 * <p>对齐 WP014 Blueprint §4.3 TimelineStats
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TimelineStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最小值
     */
    private final double min;

    /**
     * 最大值
     */
    private final double max;

    /**
     * 平均值
     */
    private final double avg;

    /**
     * 标准差（样本标准差 / 总体标准差）
     */
    private final double stdDev;

    /**
     * 样本数
     */
    private final int count;

    /**
     * 首点时间戳
     */
    private final long firstTimestamp;

    /**
     * 末点时间戳
     */
    private final long lastTimestamp;

    public TimelineStats(double min, double max, double avg, double stdDev, int count,
                         long firstTimestamp, long lastTimestamp) {
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.stdDev = stdDev;
        this.count = count;
        this.firstTimestamp = firstTimestamp;
        this.lastTimestamp = lastTimestamp;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }

    public double getStdDev() {
        return stdDev;
    }

    public int getCount() {
        return count;
    }

    public long getFirstTimestamp() {
        return firstTimestamp;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    /**
     * 变化率（首尾变化百分比）
     */
    public double getChangeRate() {
        if (count < 2 || firstTimestamp == lastTimestamp) {
            return 0.0;
        }
        // 用线性回归的首尾预估差 / 首值
        return 0.0; // 由 TimelineStatsCalculator 外部计算更合适
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelineStats that = (TimelineStats) o;
        return count == that.count && Double.compare(avg, that.avg) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, avg);
    }

    @Override
    public String toString() {
        return "TimelineStats{min=" + min + ", max=" + max + ", avg=" + avg
                + ", stdDev=" + stdDev + ", count=" + count + '}';
    }
}
