package com.aipe.relationship.domain;

/**
 * Relationship 方向枚举
 *
 * <p>对齐 M2-008 Relationship Model ch4.1 Directed
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum RelationshipDirection {

    /**
     * 单向关系: Source → Target
     */
    SINGLE,

    /**
     * 双向关系: Source ↔ Target
     */
    BIDIRECTIONAL;

    public static RelationshipDirection parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SINGLE;
        }
        try {
            return RelationshipDirection.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SINGLE;
        }
    }
}
