package com.aipe.timeline.domain;

import java.util.List;

/**
 * Observation 查询端口（六边形架构）
 *
 * <p>Timeline 模块在运行时构建 Timeline 时需要从 ClickHouse observation_fact 表查询 Observation 数据。
 * <p>为了避免引入 aipe-observation Boot 模块导致双 datasource 冲突，
 * <p>本模块通过此端口抽象 Observation 查询能力；Infrastructure 层提供 ClickHouse HTTP 实现。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ObservationQueryPort {

    /**
     * 查询指定 Resource + 指标 + 时间范围的时序数据
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
        private final String connectorId;
        private final String labels;

        public MetricPoint(long timestamp, double value, String unit, String connectorId, String labels) {
            this.observationId = "";
            this.timestamp = timestamp;
            this.value = value;
            this.unit = unit;
            this.connectorId = connectorId;
            this.labels = labels;
        }

        public MetricPoint(String observationId, long timestamp, double value, String unit, String connectorId, String labels) {
            this.observationId = observationId;
            this.timestamp = timestamp;
            this.value = value;
            this.unit = unit;
            this.connectorId = connectorId;
            this.labels = labels;
        }

        public String getObservationId() { return observationId; }
        public long getTimestamp() { return timestamp; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
        public String getConnectorId() { return connectorId; }
        public String getLabels() { return labels; }
    }

    /**
     * @deprecated 用 MetricPoint 代替
     */
    @Deprecated
    final class ObservationData {
        private final long timestamp;
        private final double value;
        private final String unit;
        private final String connectorId;
        private final String labels;

        public ObservationData(long timestamp, double value, String unit, String connectorId, String labels) {
            this.timestamp = timestamp;
            this.value = value;
            this.unit = unit;
            this.connectorId = connectorId;
            this.labels = labels;
        }

        public long getTimestamp() { return timestamp; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
        public String getConnectorId() { return connectorId; }
        public String getLabels() { return labels; }
    }
}
