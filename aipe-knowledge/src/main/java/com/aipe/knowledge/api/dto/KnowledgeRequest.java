package com.aipe.knowledge.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Knowledge 构建请求 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class KnowledgeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotBlank(message = "knowledgeType is required")
    private String knowledgeType;

    @NotBlank(message = "evidenceId is required (Domain Law-001)")
    private String evidenceId;

    private String verificationId;

    @NotNull(message = "confidence is required")
    private Double confidence;

    private Double successRate = 0.0;
    private String resourceType;
    private String metricName;
    private String recommendationAction;
    private String expectedEffect;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getKnowledgeType() { return knowledgeType; }
    public void setKnowledgeType(String knowledgeType) { this.knowledgeType = knowledgeType; }
    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }
    public String getVerificationId() { return verificationId; }
    public void setVerificationId(String verificationId) { this.verificationId = verificationId; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public String getRecommendationAction() { return recommendationAction; }
    public void setRecommendationAction(String recommendationAction) { this.recommendationAction = recommendationAction; }
    public String getExpectedEffect() { return expectedEffect; }
    public void setExpectedEffect(String expectedEffect) { this.expectedEffect = expectedEffect; }
}
