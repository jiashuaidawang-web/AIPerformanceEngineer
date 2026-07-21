package com.aipe.execution.domain;

/**
 * Execution 状态枚举（状态机：PENDING → EXECUTING → SUCCESS/FAILED → ROLLED_BACK）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ExecutionStatus {
    PENDING, EXECUTING, SUCCESS, FAILED, ROLLED_BACK;

    public static ExecutionStatus parse(String value) {
        if (value == null || value.trim().isEmpty()) return PENDING;
        try { return ExecutionStatus.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return PENDING; }
    }
}
