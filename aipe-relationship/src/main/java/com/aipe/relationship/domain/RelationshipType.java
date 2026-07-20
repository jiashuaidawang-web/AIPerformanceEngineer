package com.aipe.relationship.domain;

/**
 * Relationship 类型枚举
 *
 * <p>对齐 IM-005 Graph Mapping ch5（10 种边类型枚举）+ M2-008 Relationship Model ch5
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum RelationshipType {

    /**
     * 归属关系（上下级）: Service BELONGS_TO Business Domain
     */
    BELONGS_TO,

    /**
     * 部署关系: Application DEPLOYS_ON Host
     */
    DEPLOYS_ON,

    /**
     * 运行关系: Service RUNS_ON Host/Container
     */
    RUNS_ON,

    /**
     * 网络连接: Service CONNECTS_TO Service
     */
    CONNECTS_TO,

    /**
     * 依赖关系: Service DEPENDS_ON Redis/MySQL
     */
    DEPENDS_ON,

    /**
     * 调用关系: Service CALLS Service
     */
    CALLS,

    /**
     * 使用关系: Service USES Middleware
     */
    USES,

    /**
     * 宿主机关系: Host HOSTS Container
     */
    HOSTS,

    /**
     * 隶属关系: Resource MEMBER_OF Cluster
     */
    MEMBER_OF,

    /**
     * 组成关系: Resource PART_OF Cluster
     */
    PART_OF;

    /**
     * 解析字符串（null-safe, case-insensitive）
     */
    public static RelationshipType parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return DEPENDS_ON;
        }
        try {
            return RelationshipType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEPENDS_ON;
        }
    }

    /**
     * 是否为传播影响关系的类型（用于 Impact Analysis）
     *
     * <p>DEPENDS_ON / CALLS / USES / CONNECTS_TO / RUNS_ON / DEPLOYS_ON / HOSTS 都属于影响传播链
     */
    public boolean isImpactPropagation() {
        switch (this) {
            case DEPENDS_ON:
            case CALLS:
            case USES:
            case CONNECTS_TO:
            case RUNS_ON:
            case DEPLOYS_ON:
            case HOSTS:
                return true;
            case BELONGS_TO:
            case MEMBER_OF:
            case PART_OF:
            default:
                return false;
        }
    }

    /**
     * 反转关系对端（source/target 互换时推断反向类型）
     *
     * <p>用于 neighbors 查询时同时返回入边和出边
     */
    public RelationshipType reverse() {
        switch (this) {
            case RUNS_ON:
                return HOSTS;
            case HOSTS:
                return RUNS_ON;
            case DEPLOYS_ON:
                return DEPENDS_ON;
            default:
                return this;
        }
    }
}
