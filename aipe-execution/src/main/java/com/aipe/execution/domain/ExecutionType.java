package com.aipe.execution.domain;

/**
 * Execution 类型枚举
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ExecutionType {
    MANUAL, SEMI_AUTO, AUTO;

    public static ExecutionType parse(String value) {
        if (value == null || value.trim().isEmpty()) return MANUAL;
        try { return ExecutionType.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return MANUAL; }
    }
}
