package com.aipe.execution.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Execution 响应 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ExecutionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String executionId;
    private String recommendationId;
    private String executor;
    private String executionType;
    private String status;
    private Double improvementScore;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private Integer version;
    private OptimizationDto optimization;

    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(Double improvementScore) { this.improvementScore = improvementScore; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public OptimizationDto getOptimization() { return optimization; }
    public void setOptimization(OptimizationDto optimization) { this.optimization = optimization; }

    /**
     * Optimization DTO
     */
    public static class OptimizationDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private String status;
        private Double improvementScore;
        private String summary;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Double getImprovementScore() { return improvementScore; }
        public void setImprovementScore(Double improvementScore) { this.improvementScore = improvementScore; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
}
