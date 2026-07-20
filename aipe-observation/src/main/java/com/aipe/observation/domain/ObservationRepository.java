package com.aipe.observation.domain;

import java.util.List;

/**
 * Observation 仓储接口
 *
 * <p>Gateway Law-001：Repository Returns Domain, Never PO
 * <p>Gateway Law-002：One Aggregate, One Repository
 * <p>Gateway Law-003：Repository Interface In Domain, Implementation In Infrastructure
 *
 * <p>Observation 的 Primary Storage 是 ClickHouse（IM-004 / Persistence Law-001）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ObservationRepository {

    /**
     * 保存 Observation（新增）
     *
     * <p>Observation append-only：永远新增，禁止更新
     *
     * @param observation Observation 聚合根（不可为 null）
     * @return 保存后的 Observation（含生成的 observationId）
     */
    Observation save(Observation observation);

    /**
     * 批量保存 Observation（对齐 Pipeline 的 batchSave）
     *
     * @param observations Observation 列表（不可为 null 或空）     * @return 保存后的 Observation 列表
     */
    List<Observation> batchSave(List<Observation> observations);

    /**
     * 根据 Resource 查询 Observation 列表（按时间倒序）
     *
     * @param resourceId 资源 ID（不可为 null）
     * @param limit      限制条数
     * @return Observation 列表（不可为 null）
     */
    List<Observation> findByResourceId(String resourceId, int limit);

    /**
     * 根据 Resource + 时间范围 查询 Observation 列表（用于 Timeline）
     *
     * @param resourceId 资源 ID
     * @param startTime  开始时间（毫秒）
     * @param endTime    结束时间（毫秒）
     * @param limit      限制条数
     * @return Observation 列表（不可为 null）
     */
    List<Observation> findByResourceAndTimeRange(String resourceId, long startTime, long endTime, int limit);

    /**
     * 根据 Resource + 指标名 查询 Observation 列表
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名称
     * @param limit      限制条数
     * @return Observation 列表（不可为 null）
     */
    List<Observation> findByResourceAndMetric(String resourceId, String metricName, int limit);

    /**
     * 根据 Resource + 指标名 + 时间范围 查询 Observation 列表
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名称
     * @param startTime  开始时间（毫秒）
     * @param endTime    结束时间（毫秒）
     * @param limit      限制条数
     * @return Observation 列表（不可为 null）
     */
    List<Observation> findByMetricAndTimeRange(String resourceId, String metricName,
                                               long startTime, long endTime, int limit);
}
