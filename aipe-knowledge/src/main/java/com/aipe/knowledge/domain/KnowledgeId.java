package com.aipe.knowledge.domain;

/**
 * Knowledge ID 值对象
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class KnowledgeId {

    private final String value;

    private KnowledgeId(String value) { this.value = value; }

    public static KnowledgeId of(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("KnowledgeId cannot be null or empty");
        return new KnowledgeId(value.trim());
    }

    public static KnowledgeId generate() {
        return new KnowledgeId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((KnowledgeId) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
