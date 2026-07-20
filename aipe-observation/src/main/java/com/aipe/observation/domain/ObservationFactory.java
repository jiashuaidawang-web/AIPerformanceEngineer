package com.aipe.observation.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Observation 工厂
 *
 * <p>负责创建合法的 Observation 对象（Domain Law-003：Aggregate Root Must Have Factory）
 * <p>禁止 Controller 直接 new Observation()
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ObservationFactory {

    private ObservationFactory() {
        // 工具类，禁止实例化
    }

    /**
     * 创建新的 Observation（自动生成 ID、初始化 receivedAt）
     *
     * @param resourceId  所属 Resource（必填 - Law-002）
     * @param type        Observation 类型
     * @param source      数据来源
     * @param name        指标名
     * @param value       指标值
     * @param unit        单位
     * @param timestamp   采集时间（毫秒级 - IM-004）
     * @param connectorId Connector 标识
     * @param labels      扩展标签
     * @param payload     原始数据
     * @return 新的 Observation（已通过 validate()）
     */
    public static Observation create(String resourceId,
                                     ObservationType type,
                                     ObservationSource source,
                                     String name,
                                     Double value,
                                     String unit,
                                     long timestamp,
                                     String connectorId,
                                     Map<String, String> labels,
                                     String payload) {
        Observation observation = new Observation(
                ObservationId.generate(),
                resourceId,
                type,
                source,
                name,
                value,
                unit,
                timestamp,
                connectorId,
                labels,
                payload
        );
        observation.validate();
        return observation;
    }

    /**
     * 从持久化数据重建 Observation（Repository 专用，保留 ID）
     *
     * @param observationId 业务主键
     * @param resourceId    所属 Resource
     * @param type          Observation 类型
     * @param source        数据来源
     * @param name          指标名
     * @param value         指标值
     * @param unit          单位
     * @param timestamp     采集时间（毫秒级）
     * @param connectorId   Connector 标识
     * @param labels        扩展标签
     * @param payload       原始数据
     * @return 重建的 Observation
     */
    public static Observation reconstruct(ObservationId observationId,
                                          String resourceId,
                                          ObservationType type,
                                          ObservationSource source,
                                          String name,
                                          Double value,
                                          String unit,
                                          long timestamp,
                                          String connectorId,
                                          Map<String, String> labels,
                                          String payload) {
        return new Observation(
                observationId,
                resourceId,
                type,
                source,
                name,
                value,
                unit,
                timestamp,
                connectorId,
                labels,
                payload
        );
    }
}
