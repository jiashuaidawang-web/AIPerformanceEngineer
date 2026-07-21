package com.aipe.execution.domain;

/**
 * Execution ID 值对象
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ExecutionId {

    private final String value;
    private ExecutionId(String value) { this.value = value; }

    public static ExecutionId of(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("ExecutionId cannot be null or empty");
        return new ExecutionId(value.trim());
    }

    public static ExecutionId generate() {
        return new ExecutionId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return value.equals(((ExecutionId) o).value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
