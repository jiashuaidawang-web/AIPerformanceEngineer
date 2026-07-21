package com.aipe.recommendation.domain;

/**
 * Recommendation ID 值对象
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RecommendationId {

    private final String value;

    private RecommendationId(String value) { this.value = value; }

    public static RecommendationId of(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("RecommendationId cannot be null or empty");
        return new RecommendationId(value.trim());
    }

    public static RecommendationId generate() {
        return new RecommendationId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((RecommendationId) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
