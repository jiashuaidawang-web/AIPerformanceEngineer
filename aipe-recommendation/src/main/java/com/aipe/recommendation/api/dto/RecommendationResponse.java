package com.aipe.recommendation.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Recommendation 响应 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class RecommendationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recommendationId;
    private String knowledgeId;
    private String targetResourceId;
    private String title;
    private String description;
    private String priority;
    private Double confidence;
    private String expectedOutcome;
    private List<String> executionPlan;
    private List<String> rollbackPlan;
    private String status;
    private LocalDateTime createdAt;
    private Integer version;

    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public String getTargetResourceId() { return targetResourceId; }
    public void setTargetResourceId(String targetResourceId) { this.targetResourceId = targetResourceId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getExpectedOutcome() { return expectedOutcome; }
    public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
    public List<String> getExecutionPlan() { return executionPlan; }
    public void setExecutionPlan(List<String> executionPlan) { this.executionPlan = executionPlan; }
    public List<String> getRollbackPlan() { return rollbackPlan; }
    public void setRollbackPlan(List<String> rollbackPlan) { this.rollbackPlan = rollbackPlan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
