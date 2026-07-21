package com.aipe.recommendation.domain;

/**
 * Recommendation 状态枚举（状态机：PENDING → APPROVED → EXECUTED / REJECTED）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum RecommendationStatus {

    /** 待审批 */
    PENDING,

    /** 已审批通过 */
    APPROVED,

    /** 已拒绝 */
    REJECTED,

    /** 已执行 */
    EXECUTED;

    public static RecommendationStatus parse(String value) {
        if (value == null || value.trim().isEmpty()) return PENDING;
        try { return RecommendationStatus.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return PENDING; }
    }
}
