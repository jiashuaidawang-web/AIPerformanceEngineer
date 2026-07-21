package com.aipe.execution.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Execution 聚合根（不可变 - Immutable）
 *
 * <p>M2-013 Optimization Model：执行 Recommendation 并验证效果。
 * <p>核心流程：PENDING → EXECUTING → SUCCESS / FAILED → ROLLED_BACK
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Execution implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ExecutionId executionId;
    private final String recommendationId;
    private final String executor;
    private final ExecutionType executionType;
    private final ExecutionStatus status;
    private final String beforeSnapshot; // JSON
    private final String afterSnapshot; // JSON
    private final double improvementScore;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;
    private final String rollbackInfo; // JSON
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int version;

    Execution(ExecutionId executionId,
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
        this.executionId = executionId;
        this.recommendationId = recommendationId;
        this.executor = executor;
        this.executionType = executionType;
        this.status = status;
        this.beforeSnapshot = beforeSnapshot;
        this.afterSnapshot = afterSnapshot;
        this.improvementScore = improvementScore;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.rollbackInfo = rollbackInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public void validate() {
        if (executionId == null) throw new IllegalArgumentException("ExecutionId is required");
        if (recommendationId == null || recommendationId.trim().isEmpty())
            throw new IllegalArgumentException("RecommendationId is required");
        if (executor == null || executor.trim().isEmpty())
            throw new IllegalArgumentException("Executor is required");
        if (executionType == null) throw new IllegalArgumentException("ExecutionType is required");
        if (status == null) throw new IllegalArgumentException("Status is required");
    }

    public boolean canExecute() { return status == ExecutionStatus.PENDING; }
    public boolean canComplete() { return status == ExecutionStatus.EXECUTING; }
    public boolean canRollback() { return status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED; }
    public boolean isFinished() {
        return status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED || status == ExecutionStatus.ROLLED_BACK;
    }

    public Execution markExecuting() {
        if (status != ExecutionStatus.PENDING)
            throw new IllegalStateException("Cannot execute from status: " + status);
        return new Execution(executionId, recommendationId, executor, executionType,
                ExecutionStatus.EXECUTING, beforeSnapshot, afterSnapshot, improvementScore,
                LocalDateTime.now(), finishedAt, rollbackInfo, createdAt, LocalDateTime.now(), version);
    }

    public Execution markSuccess(String afterSnapshot, double improvementScore) {
        if (status != ExecutionStatus.EXECUTING)
            throw new IllegalStateException("Cannot mark success from status: " + status);
        return new Execution(executionId, recommendationId, executor, executionType,
                ExecutionStatus.SUCCESS, beforeSnapshot, afterSnapshot, improvementScore,
                startedAt, LocalDateTime.now(), rollbackInfo, createdAt, LocalDateTime.now(), version);
    }

    public Execution markFailed(String afterSnapshot) {
        if (status != ExecutionStatus.EXECUTING)
            throw new IllegalStateException("Cannot mark failed from status: " + status);
        return new Execution(executionId, recommendationId, executor, executionType,
                ExecutionStatus.FAILED, beforeSnapshot, afterSnapshot, 0.0,
                startedAt, LocalDateTime.now(), rollbackInfo, createdAt, LocalDateTime.now(), version);
    }

    public Execution markRolledBack(String rollbackInfo) {
        if (!canRollback())
            throw new IllegalStateException("Cannot rollback from status: " + status);
        return new Execution(executionId, recommendationId, executor, executionType,
                ExecutionStatus.ROLLED_BACK, beforeSnapshot, afterSnapshot, improvementScore,
                startedAt, finishedAt, rollbackInfo, createdAt, LocalDateTime.now(), version);
    }

    // Getters
    public ExecutionId getExecutionId() { return executionId; }
    public String getRecommendationId() { return recommendationId; }
    public String getExecutor() { return executor; }
    public ExecutionType getExecutionType() { return executionType; }
    public ExecutionStatus getStatus() { return status; }
    public String getBeforeSnapshot() { return beforeSnapshot; }
    public String getAfterSnapshot() { return afterSnapshot; }
    public double getImprovementScore() { return improvementScore; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public String getRollbackInfo() { return rollbackInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public int getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Execution execution = (Execution) o;
        return Objects.equals(executionId, execution.executionId);
    }

    @Override
    public int hashCode() { return Objects.hash(executionId); }

    @Override
    public String toString() {
        return "Execution{id=" + executionId + ", status=" + status + ", score=" + improvementScore + "}";
    }
}
