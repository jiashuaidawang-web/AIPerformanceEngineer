package com.aipe.execution.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Optimization 值对象（执行效果评估）
 *
 * <p>M2-013 Optimization Model：评估执行效果，闭环更新 Knowledge。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Optimization implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String optimizationId;
    private final String executionId;
    private final String status; // PASS / FAIL
    private final double improvementScore;
    private final String summary;
    private final LocalDateTime evaluatedAt;

    public Optimization(String optimizationId, String executionId, String status,
                        double improvementScore, String summary, LocalDateTime evaluatedAt) {
        this.optimizationId = optimizationId;
        this.executionId = executionId;
        this.status = status;
        this.improvementScore = improvementScore;
        this.summary = summary;
        this.evaluatedAt = evaluatedAt;
    }

    public String getOptimizationId() { return optimizationId; }
    public String getExecutionId() { return executionId; }
    public String getStatus() { return status; }
    public double getImprovementScore() { return improvementScore; }
    public String getSummary() { return summary; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Optimization that = (Optimization) o;
        return Objects.equals(optimizationId, that.optimizationId);
    }

    @Override
    public int hashCode() { return Objects.hash(optimizationId); }

    @Override
    public String toString() {
        return "Optimization{status=" + status + ", score=" + improvementScore + "}";
    }
}
