package com.aipe.timeline.application;

import com.aipe.timeline.domain.TimelinePoint;
import com.aipe.timeline.domain.TimelineStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Timeline 统计特征计算器
 *
 * <p>对齐 WP014 Blueprint §4.6 TimelineStatsCalculator
 *
 * <p>计算 min / max / avg / stdDev / count + 变化率 + 趋势方向
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class TimelineStatsCalculator {

    private static final Logger log = LoggerFactory.getLogger(TimelineStatsCalculator.class);

    /**
     * 计算 Timeline 统计特征（min/max/avg/stdDev/count）
     *
     * @param points Timeline 观察点列表（按时间排序）
     * @return TimelineStats
     */
    public TimelineStats calculate(List<TimelinePoint> points) {
        if (points == null || points.isEmpty()) {
            return new TimelineStats(0.0, 0.0, 0.0, 0.0, 0, 0L, 0L);
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double sum = 0.0;
        long firstTs = Long.MAX_VALUE;
        long lastTs = Long.MIN_VALUE;

        for (TimelinePoint p : points) {
            double v = p.getValue();
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
            if (p.getTimestamp() < firstTs) firstTs = p.getTimestamp();
            if (p.getTimestamp() > lastTs) lastTs = p.getTimestamp();
        }

        int count = points.size();
        double avg = sum / count;

        // 标准差（样本标准差 / 总体标准差）
        double variance = 0.0;
        for (TimelinePoint p : points) {
            double diff = p.getValue() - avg;
            variance += diff * diff;
        }
        // 总体标准差（除以 n）；样本标准差除以 (n-1)，此处用总体
        double stdDev = count > 0 ? Math.sqrt(variance / count) : 0.0;

        TimelineStats stats = new TimelineStats(min, max, avg, stdDev, count, firstTs, lastTs);
        log.debug("Calculated stats: {}", stats);
        return stats;
    }

    /**
     * 计算变化率（首尾变化百分比）
     *
     * <p>用于衡量整个 Timeline 区间内的相对变化幅度
     *
     * @param points Timeline 观察点列表
     * @return 变化率（0.0 ~ 1.0+，正数 = 上升）
     */
    public double calculateChangeRate(List<TimelinePoint> points) {
        if (points == null || points.size() < 2) {
            return 0.0;
        }
        double first = points.get(0).getValue();
        double last = points.get(points.size() - 1).getValue();
        if (first == 0.0) {
            return last == 0.0 ? 0.0 : 1.0;
        }
        return (last - first) / Math.abs(first);
    }

    /**
     * 趋势方向检测（上升 / 下降 / 平稳）
     *
     * <p>使用最小二乘法（Least Squares）拟合斜率
     *
     * @param points Timeline 观察点列表
     * @return 趋势方向
     */
    public TrendDirection detectTrend(List<TimelinePoint> points) {
        if (points == null || points.size() < 2) {
            return TrendDirection.FLAT;
        }

        // 用中心化后的 timestamp 计算（避免大数溢出）
        long baseTs = points.get(0).getTimestamp();
        int n = points.size();
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;

        for (TimelinePoint p : points) {
            double x = (p.getTimestamp() - baseTs) / 1000.0; // 转为秒，避免大数
            double y = p.getValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double denom = (n * sumXX - sumX * sumX);
        if (Math.abs(denom) < 1e-12) {
            return TrendDirection.FLAT;
        }

        double slope = (n * sumXY - sumX * sumY) / denom;

        // 阈值：斜率绝对值 / y均值 < 1% 视为平稳
        double avgY = sumY / n;
        double relativeSlope = avgY != 0 ? Math.abs(slope) / Math.abs(avgY) : Math.abs(slope);

        if (relativeSlope < 0.001) {
            return TrendDirection.FLAT;
        }
        return slope > 0 ? TrendDirection.RISING : TrendDirection.FALLING;
    }

    /**
     * 趋势方向
     */
    public enum TrendDirection {
        /** 上升 */
        RISING,
        /** 下降 */
        FALLING,
        /** 平稳 */
        FLAT
    }
}
