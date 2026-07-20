package com.aipe.observation.domain;

/**
 * Observation 规格校验
 *
 * <p>封装 Observation 级业务校验规则（Domain Law-005：Aggregate Root Enforces Consistency）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ObservationSpecification {

    private ObservationSpecification() {
        // 工具类，禁止实例化
    }

    /**
     * 校验 Observation 是否可以创建
     *
     * <p>必须满足：resourceId + timestamp + type + name + value 必填
     *
     * @param observation 待校验
     * @throws IllegalArgumentException 校验失败
     */
    public static void validateForCreate(Observation observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Observation cannot be null");
        }
        observation.validate();
    }

    /**
     * 校验 Metric 类型的 Observation 是否合法
     *
     * <p>Metric 类型必须有 value 表示
     *
     * @param observation 待校验
     * @throws IllegalArgumentException 校验失败
     */
    public static void validateMetric(Observation observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Observation cannot be null");
        }
        if (observation.getType() != ObservationType.METRIC) {
            throw new IllegalArgumentException("Observation is not of type METRIC");
        }
        if (observation.getValue() == null) {
            throw new IllegalArgumentException("Metric observation must have a numeric value");
        }
    }
}
