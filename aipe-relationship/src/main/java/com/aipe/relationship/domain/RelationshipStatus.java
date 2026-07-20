package com.aipe.relationship.domain;

/**
 * Relationship 生命周期状态枚举
 *
 * <p>对齐 M2-008 Relationship Model ch6 Lifecycle（Discover → Register → Validate → Active → Changed → Inactive → Archived）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum RelationshipStatus {

    /**
     * 活跃（已验证生效）
     */
    ACTIVE,

    /**
     * 失效（Discovery 未再确认）
     */
    INACTIVE,

    /**
     * 归档（替代 / 历史记录）
     */
    ARCHIVED;

    public static RelationshipStatus parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ACTIVE;
        }
        try {
            return RelationshipStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }

    public boolean isQueryable() {
        return this == ACTIVE;
    }
}
