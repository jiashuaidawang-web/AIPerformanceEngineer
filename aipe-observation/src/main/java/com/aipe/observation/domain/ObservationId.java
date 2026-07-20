package com.aipe.observation.domain;

import java.util.Objects;

/**
 * Observation ID 值对象
 *
 * <p>不可变，全局唯一标识（Domain Law-004：Value Object Is Immutable）
 * <p>Observation 是 AI World 中唯一合法的 Runtime Fact（M2-006 Observation Model Specification）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ObservationId {

    private final String value;

    private ObservationId(String value) {
        this.value = value;
    }

    /**
     * 从字符串创建 ObservationId
     *
     * @param value ID 值（不可为 null 或空）
     * @return ObservationId
     */
    public static ObservationId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ObservationId cannot be null or empty");
        }
        return new ObservationId(value.trim());
    }

    /**
     * 生成新的随机 ObservationId（UUID）
     *
     * @return 新的 ObservationId
     */
    public static ObservationId generate() {
        return new ObservationId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservationId that = (ObservationId) o;
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
