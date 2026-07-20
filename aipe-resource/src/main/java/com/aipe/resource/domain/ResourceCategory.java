package com.aipe.resource.domain;

/**
 * 资源分类枚举
 *
 * <p>资源的大类划分，用于权限、展示、统计维度
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ResourceCategory {

    /**
     * 业务资源（应用、服务、API 等）
     */
    BUSINESS,

    /**
     * 基础设施资源（主机、容器、网络等）
     */
    INFRA,

    /**
     * 平台资源（中间件、数据库、MQ 等）
     */
    PLATFORM;
}
