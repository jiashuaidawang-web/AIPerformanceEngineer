package com.aipe.observation.domain;

/**
 * Observation 类型枚举
 *
 * <p>对齐 M2-006 Observation Model Specification Chapter 5 Classification
 * <p>Metric / Log / Trace / Event / Snapshot —— 都是 Observation 的不同表现形式
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ObservationType {

    /**
     * 数值型运行指标（CPU / Memory / TPS / QPS / Heap Usage）
     */
    METRIC,

    /**
     * 文本运行事实（ERROR 日志 / GC Log / Exception）
     */
    LOG,

    /**
     * 调用链事实（HTTP Span / RPC Span / SQL Span）
     */
    TRACE,

    /**
     * 离散事件（Pod Restart / Deployment / Redis Failover / JVM Restart）
     */
    EVENT,

    /**
     * 状态快照（Thread Dump / Heap Dump / Redis INFO / SHOW STATUS）
     */
    SNAPSHOT;
}
