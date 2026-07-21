package com.aipe.evidence.domain;

/**
 * Evidence ID 值对象
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class EvidenceId {

    private final String value;

    private EvidenceId(String value) { this.value = value; }

    public static EvidenceId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("EvidenceId cannot be null or empty");
        }
        return new EvidenceId(value.trim());
    }

    public static EvidenceId generate() {
        return new EvidenceId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((EvidenceId) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
