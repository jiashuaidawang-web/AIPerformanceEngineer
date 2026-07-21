package com.aipe.execution.domain;

import java.time.LocalDateTime;

/**
 * Execution 构造器
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ExecutionBuilder {

    private ExecutionBuilder() {}

    public static Execution create(ExecutionId executionId,
                                   String recommendationId,
                                   String executor,
                                   ExecutionType executionType,
                                   String beforeSnapshot) {
        Execution execution = new Execution(executionId, recommendationId, executor, executionType,
                ExecutionStatus.PENDING, beforeSnapshot, null, 0.0,
                null, null, null, LocalDateTime.now(), LocalDateTime.now(), 1);
        execution.validate();
        return execution;
    }

    public static Execution reconstruct(ExecutionId executionId,
                                        String recommendationId,
                                        String executor,
                                        ExecutionType executionType,
                                        ExecutionStatus status,
                                        String beforeSnapshot,
                                        String afterSnapshot,
                                        double improvementScore,
                                        LocalDateTime startedAt,
                                        LocalDateTime finishedAt,
                                        String rollbackInfo,
                                        LocalDateTime createdAt,
                                        LocalDateTime updatedAt,
                                        int version) {
        return new Execution(executionId, recommendationId, executor, executionType,
                status, beforeSnapshot, afterSnapshot, improvementScore,
                startedAt, finishedAt, rollbackInfo, createdAt, updatedAt, version);
    }
}
