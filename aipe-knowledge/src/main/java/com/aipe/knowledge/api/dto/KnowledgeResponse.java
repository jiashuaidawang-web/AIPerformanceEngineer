package com.aipe.knowledge.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Knowledge 响应 DTO（对齐 Blueprint §9.3）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class KnowledgeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String knowledgeId;
    private String title;
    private String description;
    private String knowledgeType;
    private String evidenceId;
    private String verificationId;
    private Double confidence;
    private Map<String, String> applicableConditions;
    private RecommendationDto recommendation;
    private Double successRate;
    private LocalDateTime createdAt;
    private Integer version;

    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
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
    public Map<String, String> getApplicableConditions() { return applicableConditions; }
    public void setApplicableConditions(Map<String, String> applicableConditions) { this.applicableConditions = applicableConditions; }
    public RecommendationDto getRecommendation() { return recommendation; }
    public void setRecommendation(RecommendationDto recommendation) { this.recommendation = recommendation; }
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    /**
     * 推荐方案 DTO
     */
    public static class RecommendationDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private String action;
        private String expectedEffect;
        private String riskLevel;

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getExpectedEffect() { return expectedEffect; }
        public void setExpectedEffect(String expectedEffect) { this.expectedEffect = expectedEffect; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    }
}
