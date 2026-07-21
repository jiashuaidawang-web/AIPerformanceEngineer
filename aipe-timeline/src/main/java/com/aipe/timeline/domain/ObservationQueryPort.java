package com.aipe.timeline.domain;

import java.util.List;

/**
 * Observation 查询端口（六边形架构）
 *
 * <p>Timeline 模块在运行时构建 Timeline 时需要从 ClickHouse observation_fact 表查询 Observation。
 * <p>为了避免引入 aipe-observation 的 Boot 配置（避免双 datasource 冲突），
 * <p>本模块通过此端口抽象 Observation 查询能力；Infrastructure 层提供 ClickHouse JdbcTemplate 实现。
 *
 * <p>IM-009：Repository Interface In Domain, Implementation In Infrastructure
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ObservationQueryPort {

    /**
     * 查询指定 Resource + 指标 + 时间范围内按时间排序的 Observation 列表
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名（可为空 = 全指标）
     * @param startTime  开始时间（毫秒）
     * @param endTime    结束时间（毫秒）
     * @param limit      限制条数
     * @return Observation 列表（按 timestamp 升序）
     */
    List<ObservationData> queryByResourceAndTimeRange(String resourceId, String metricName,
                                                     long startTime, long endTime, int limit);

    /**
     * 查询指定 Resource 的所有 distinct 指标名
     *
     * @param resourceId 资源 ID
     * @return 指标名列表
     */
    List<String> queryDistinctMetricNames(String resourceId, long startTime, long endTime);

    /**
     * Observation 数据载体（domain 内部模型，不依赖 infrastructure 实现）
     */
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

        public long getTimestamp() {
            return timestamp;
        }

        public double getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        public String getConnectorId() {
            return connectorId;
        }

        public String getLabels() {
            return labels;
        }
    }
}
