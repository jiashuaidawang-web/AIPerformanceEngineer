package com.aipe.resource.domain;

/**
 * 资源类型枚举
 *
 * <p>对齐 IM-003 Persistence Mapping / WP011 Blueprint
 * <p>所有 IT 对象统一抽象为 Resource，通过 type 区分具体类型
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ResourceType {

    /**
     * 应用 / 服务
     */
    APPLICATION,

    /**
     * 服务（微服务实例）
     */
    SERVICE,

    /**
     * 实例（单个运行实例）
     */
    INSTANCE,

    /**
     * 集群
     */
    CLUSTER,

    /**
     * 数据库（通用）
     */
    DATABASE,

    /**
     * 中间件（通用）
     */
    MIDDLEWARE,

    /**
     * 主机 / 物理机 / VM
     */
    HOST,

    /**
     * Redis
     */
    REDIS,

    /**
     * 消息队列（通用）
     */
    MQ,

    /**
     * Nginx
     */
    NGINX,

    /**
     * API 接口
     */
    API,

    /**
     * 容器
     */
    CONTAINER,

    /**
     * Pod（K8s）
     */
    POD,

    /**
     * JVM
     */
    JVM,

    /**
     * Kafka
     */
    KAFKA,

    /**
     * RocketMQ
     */
    ROCKETMQ,

    /**
     * ClickHouse
     */
    CLICKHOUSE,

    /**
     * MySQL
     */
    MYSQL,

    /**
     * 未知类型
     */
    UNKNOWN;
}
