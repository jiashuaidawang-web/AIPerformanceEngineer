package com.aipe.execution.domain;

/**
 * Execution 规格校验
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ExecutionSpecification {

    private ExecutionSpecification() {}

    public static void validateForCreate(Execution execution) {
        if (execution == null) throw new IllegalArgumentException("Execution cannot be null");
        execution.validate();
    }
}
