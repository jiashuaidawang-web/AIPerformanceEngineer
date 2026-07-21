package com.aipe.evidence.domain;

import java.util.List;

/**
 * Observation 查询端口（evidence 模块的本地端口）
 *
 * <p>EvidenceEngine 需要通过此端口从 ClickHouse observation_fact 表查询 Observation 数据，
 * <p>避免引入 aipe-observation Boot 模块导致双 datasource 冲突。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface EvidenceObservationPort {

    /**
     * 查询指定 Resource + 指标 + 时间范围的时序数据（按时间升序）
     *
     * @return List of [observationId, timestamp, value, unit] 四元组
     */
    List<MetricPoint> queryMetricSeries(String resourceId, String metricName,
                                        long startTime, long endTime, int limit);

    /**
     * 查询 Resource 的所有 distinct 指标名
     */
    List<String> queryDistinctMetricNames(String resourceId, long startTime, long endTime);

    /**
     * 时序点数据
     */
    final class MetricPoint {
        private final String observationId;
        private final long timestamp;
        private final double value;
        private final String unit;

        public MetricPoint(String observationId, long timestamp, double value, String unit) {
            this.observationId = observationId;
            this.timestamp = timestamp;
            this.value = value;
            this.unit = unit;
        }

        public String getObservationId() { return observationId; }
        public long getTimestamp() { return timestamp; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
    }
}
