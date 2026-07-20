package com.aipe.observation.domain;

/**
 * Observation 数据来源枚举
 *
 * <p>对齐 M2-006 Observation Model Specification Chapter 7 Schema
 * <p>Connector 是 Collector，不是 Producer；Producer 永远是 Resource（Law-002）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ObservationSource {

    /**
     * JVM 采集器（JMX）
     */
    JVM,

    /**
     * Linux 系统采集器
     */
    LINUX,

    /**
     * Redis 采集器
     */
    REDIS,

    /**
     * MySQL 采集器
     */
    MYSQL,

    /**
     * Prometheus 采集器
     */
    PROMETHEUS;
}
