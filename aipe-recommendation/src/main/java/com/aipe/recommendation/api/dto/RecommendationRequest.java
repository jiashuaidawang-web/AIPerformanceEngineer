package com.aipe.recommendation.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Recommendation 生成请求 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class RecommendationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "knowledgeId is required")
    private String knowledgeId;

    @NotBlank(message = "targetResourceId is required")
    private String targetResourceId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotNull(message = "confidence is required")
    private Double confidence;

    private String expectedOutcome;

    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public String getTargetResourceId() { return targetResourceId; }
    public void setTargetResourceId(String targetResourceId) { this.targetResourceId = targetResourceId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getExpectedOutcome() { return expectedOutcome; }
    public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
}
