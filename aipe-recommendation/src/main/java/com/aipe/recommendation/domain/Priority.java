package com.aipe.recommendation.domain;

/**
 * Recommendation 优先级枚举
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum Priority {
    HIGH, MEDIUM, LOW;

    public static Priority parse(String value) {
        if (value == null || value.trim().isEmpty()) return MEDIUM;
        try { return Priority.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return MEDIUM; }
    }
}
