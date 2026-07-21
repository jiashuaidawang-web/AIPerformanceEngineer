package com.aipe.recommendation.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Recommendation 聚合根（不可变 - Immutable）
 *
 * <p>M2-013 Optimization Model：Knowledge 应用于具体 Resource 的建议。
 * <p>Recommendation 只是建议，不改变世界（Recommendation != Execution）。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Recommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final RecommendationId recommendationId;
    private final String knowledgeId;
    private final String targetResourceId;
    private final String title;
    private final String description;
    private final Priority priority;
    private final double confidence;
    private final String expectedOutcome;
    private final List<String> executionPlan;
    private final List<String> rollbackPlan;
    private final RecommendationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int version;

    Recommendation(RecommendationId recommendationId,
                   String knowledgeId,
                   String targetResourceId,
                   String title,
                   String description,
                   Priority priority,
                   double confidence,
                   String expectedOutcome,
                   List<String> executionPlan,
                   List<String> rollbackPlan,
                   RecommendationStatus status,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt,
                   int version) {
        this.recommendationId = recommendationId;
        this.knowledgeId = knowledgeId;
        this.targetResourceId = targetResourceId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.confidence = confidence;
        this.expectedOutcome = expectedOutcome;
        this.executionPlan = executionPlan != null ? Collections.unmodifiableList(executionPlan) : Collections.emptyList();
        this.rollbackPlan = rollbackPlan != null ? Collections.unmodifiableList(rollbackPlan) : Collections.emptyList();
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public void validate() {
        if (recommendationId == null) throw new IllegalArgumentException("RecommendationId is required");
        if (knowledgeId == null || knowledgeId.trim().isEmpty()) {
            throw new IllegalArgumentException("KnowledgeId is required (Domain Law-001: Recommendation must come from Knowledge)");
        }
        if (targetResourceId == null || targetResourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("TargetResourceId is required (Law-002)");
        }
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title is required");
        if (confidence < 0.0 || confidence > 100.0) throw new IllegalArgumentException("Confidence must be between 0 and 100");
        if (priority == null) throw new IllegalArgumentException("Priority is required");
    }

    public boolean isActionable() {
        return status == RecommendationStatus.APPROVED;
    }

    public boolean canApprove() {
        return status == RecommendationStatus.PENDING;
    }

    public boolean canReject() {
        return status == RecommendationStatus.PENDING;
    }

    public Recommendation approve() {
        if (status != RecommendationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve Recommendation in status: " + status);
        }
        return new Recommendation(recommendationId, knowledgeId, targetResourceId, title, description,
                priority, confidence, expectedOutcome, executionPlan, rollbackPlan,
                RecommendationStatus.APPROVED, createdAt, LocalDateTime.now(), version);
    }

    public Recommendation reject() {
        if (status != RecommendationStatus.PENDING) {
            throw new IllegalStateException("Cannot reject Recommendation in status: " + status);
        }
        return new Recommendation(recommendationId, knowledgeId, targetResourceId, title, description,
                priority, confidence, expectedOutcome, executionPlan, rollbackPlan,
                RecommendationStatus.REJECTED, createdAt, LocalDateTime.now(), version);
    }

    public Recommendation markExecuted() {
        if (status != RecommendationStatus.APPROVED) {
            throw new IllegalStateException("Cannot mark executed from status: " + status + " (must be APPROVED)");
        }
        return new Recommendation(recommendationId, knowledgeId, targetResourceId, title, description,
                priority, confidence, expectedOutcome, executionPlan, rollbackPlan,
                RecommendationStatus.EXECUTED, createdAt, LocalDateTime.now(), version);
    }

    // Getters
    public RecommendationId getRecommendationId() { return recommendationId; }
    public String getKnowledgeId() { return knowledgeId; }
    public String getTargetResourceId() { return targetResourceId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public double getConfidence() { return confidence; }
    public String getExpectedOutcome() { return expectedOutcome; }
    public List<String> getExecutionPlan() { return executionPlan; }
    public List<String> getRollbackPlan() { return rollbackPlan; }
    public RecommendationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public int getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(recommendationId, that.recommendationId);
    }

    @Override
    public int hashCode() { return Objects.hash(recommendationId); }

    @Override
    public String toString() {
        return "Recommendation{id=" + recommendationId + ", priority=" + priority + ", status=" + status + "}";
    }
}
