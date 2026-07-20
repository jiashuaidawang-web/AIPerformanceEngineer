package com.aipe.relationship.domain;

import java.util.Objects;

/**
 * Relationship ID 值对象
 *
 * <p>不可变，全局唯一标识（Domain Law-004：Value Object Is Immutable）
 * <p>Relationship 是独立领域对象，拥有独立 ID（Architecture Law-005：Relationship Is First-Class Citizen）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RelationshipId {

    private final String value;

    private RelationshipId(String value) {
        this.value = value;
    }

    public static RelationshipId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("RelationshipId cannot be null or empty");
        }
        return new RelationshipId(value.trim());
    }

    public static RelationshipId generate() {
        return new RelationshipId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipId that = (RelationshipId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
