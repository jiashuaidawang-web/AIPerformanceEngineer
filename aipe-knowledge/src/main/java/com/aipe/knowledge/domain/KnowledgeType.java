package com.aipe.knowledge.domain;

/**
 * Knowledge 类型枚举（6 类 - 对齐 M2-012 ch5）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum KnowledgeType {

    /** 性能瓶颈：CPU Saturation / GC Pause / Redis Blocking */
    BOTTLENECK,

    /** 依赖问题：MySQL Slow Query / Redis Timeout / Kafka Backlog */
    DEPENDENCY,

    /** 部署问题：Rolling Update / Node Failure / Pod Restart */
    DEPLOYMENT,

    /** 业务模式：秒杀 / 双十一 / 支付高峰 */
    BUSINESS,

    /** 优化经验：连接池调整 / JVM 参数优化 */
    OPTIMIZATION,

    /** AI 自动学习形成 */
    AI;

    public static KnowledgeType parse(String value) {
        if (value == null || value.trim().isEmpty()) return AI;
        try { return KnowledgeType.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return AI; }
    }
}
