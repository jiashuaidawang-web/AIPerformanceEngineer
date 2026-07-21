package com.aipe.evidence.domain;

/**
 * Evidence 类型枚举
 *
 * <p>对齐 M2-011 Evidence Model ch5 Classification（6 类）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum EvidenceType {

    /** 性能瓶颈：CPU Saturation / Memory Leak / GC Pause */
    PERFORMANCE,

    /** 依赖问题：Redis Timeout / MySQL Slow Query / Kafka Backlog */
    DEPENDENCY,

    /** 部署问题：Pod Restart / Rolling Update / Node Failure */
    DEPLOYMENT,

    /** 业务异常：Order Failure / Payment Timeout / Inventory Delay */
    BUSINESS,

    /** AI 自动推理：Likely Root Cause / Likely Impact / Likely Bottleneck */
    AI,

    /** 多个 Evidence 聚合 */
    COMPOSITE;

    public static EvidenceType parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return AI;
        }
        try {
            return EvidenceType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AI;
        }
    }
}
