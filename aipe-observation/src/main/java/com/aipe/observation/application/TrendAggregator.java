package com.aipe.observation.application;

import com.aipe.observation.domain.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 趋势聚合器
 *
 * <p>将 Observation 按时间桶聚合，生成趋势数据（avg / max / min / count）
 * <p>对齐 WP012 Blueprint §5 queryTrend：至少支持 1m / 5m / 1h / 1d
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TrendAggregator {

    private static final Logger log = LoggerFactory.getLogger(TrendAggregator.class);

    private TrendAggregator() {
        // 工具类，禁止实例化
    }

    /**
     * 按时间桶聚合 Observation 列表
     *
     * @param observations Observation 列表（已按时间排序）
     * @param intervalMs   时间桶大小（毫秒，如 60000=1m）
     * @return 趋势点列表
     */
    public static List<TrendPoint> aggregate(List<Observation> observations, long intervalMs) {
        if (observations == null || observations.isEmpty() || intervalMs <= 0) {
            return Collections.emptyList();
        }

        List<TrendPoint> result = new ArrayList<>();

        // 按时间桶分桶（取 floor(timestamp/intervalMs) * intervalMs）
        // 使用简单顺序扫描（假设已按 timestamp 升序）
        long currentBucket = -1;
        TrendPoint currentPoint = null;

        for (Observation obs : observations) {
            long bucket = (obs.getTimestamp() / intervalMs) * intervalMs;
            if (bucket != currentBucket) {
                if (currentPoint != null) {
                    result.add(currentPoint);
                }
                currentBucket = bucket;
                currentPoint = new TrendPoint(bucket, obs.getValue());
            } else {
                currentPoint.merge(obs.getValue());
            }
        }
        if (currentPoint != null) {
            result.add(currentPoint);
        }

        log.debug("Aggregated {} observations into {} trend points (intervalMs={})",
                observations.size(), result.size(), intervalMs);
        return result;
    }

    /**
     * 预定义时间桶（1m / 5m / 1h / 1d）
     */
    public static final long INTERVAL_1M = 60_000L;
    public static final long INTERVAL_5M = 300_000L;
    public static final long INTERVAL_1H = 3_600_000L;
    public static final long INTERVAL_1D = 86_400_000L;

    /**
     * 解析时间桶字符串（对齐 WP012 Blueprint §7.1 interval 参数）
     *
     * @param interval 1m / 5m / 1h / 1d
     * @return 毫秒数
     * @throws IllegalArgumentException 不支持的间隔
     */
    public static long parseInterval(String interval) {
        if (interval == null) {
            throw new IllegalArgumentException("Interval cannot be null");
        }
        switch (interval.trim().toLowerCase()) {
            case "1m":
                return INTERVAL_1M;
            case "5m":
                return INTERVAL_5M;
            case "1h":
                return INTERVAL_1H;
            case "1d":
                return INTERVAL_1D;
            default:
                try {
                    return Long.parseLong(interval.trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Unsupported interval: " + interval);
                }
        }
    }

    /**
     * 趋势数据点
     */
    public static final class TrendPoint {

        private final long bucketTimestamp;
        private double sum = 0.0;
        private double max = Double.NEGATIVE_INFINITY;
        private double min = Double.POSITIVE_INFINITY;
        private int count = 0;

        TrendPoint(long bucketTimestamp, double firstValue) {
            this.bucketTimestamp = bucketTimestamp;
            this.sum = firstValue;
            this.max = firstValue;
            this.min = firstValue;
            this.count = 1;
        }

        void merge(double value) {
            sum += value;
            if (value > max) max = value;
            if (value < min) min = value;
            count++;
        }

        public long getBucketTimestamp() {
            return bucketTimestamp;
        }

        public double getAvg() {
            return count == 0 ? 0.0 : sum / count;
        }

        public double getMax() {
            return max;
        }

        public double getMin() {
            return min;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "TrendPoint{bucket=" + bucketTimestamp + ", avg=" + getAvg()
                    + ", max=" + max + ", min=" + min + ", count=" + count + '}';
        }
    }
}
