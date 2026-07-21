package com.aipe.knowledge.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Knowledge 聚合根（不可变 - Immutable）
 *
 * <p>M2-012 Knowledge Model：Knowledge = Verified Evidence 的沉淀。
 * <p>Domain Law-001: Knowledge Is Verified Evidence — 不是 AI Memory，不是 Rule，不是 Vector。
 * <p>Reality Before Memory — AI 相信现实。
 *
 * <p>不可变特征：版本升级 = 新 version 新记录（旧记录保留）。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Knowledge implements Serializable {

    private static final long serialVersionUID = 1L;

    private final KnowledgeId knowledgeId;
    private final String title;
    private final String description;
    private final KnowledgeType knowledgeType;
    private final String evidenceId;
    private final String verificationId;
    private final double confidence;
    private final Map<String, String> applicableConditions;
    private final Recommendation recommendation;
    private final double successRate;
    private final LocalDateTime createdAt;
    private final int version;

    Knowledge(KnowledgeId knowledgeId,
              String title,
              String description,
              KnowledgeType knowledgeType,
              String evidenceId,
              String verificationId,
              double confidence,
              Map<String, String> applicableConditions,
              Recommendation recommendation,
              double successRate,
              LocalDateTime createdAt,
              int version) {
        this.knowledgeId = knowledgeId;
        this.title = title;
        this.description = description;
        this.knowledgeType = knowledgeType;
        this.evidenceId = evidenceId;
        this.verificationId = verificationId;
        this.confidence = confidence;
        this.applicableConditions = applicableConditions != null ? Collections.unmodifiableMap(applicableConditions) : Collections.emptyMap();
        this.recommendation = recommendation;
        this.successRate = successRate;
        this.createdAt = createdAt;
        this.version = version;
    }

    public void validate() {
        if (knowledgeId == null) throw new IllegalArgumentException("KnowledgeId is required");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title is required");
        if (evidenceId == null || evidenceId.trim().isEmpty()) {
            throw new IllegalArgumentException("EvidenceId is required (Domain Law-001: Knowledge must come from Verified Evidence)");
        }
        if (confidence < 0.0 || confidence > 100.0) throw new IllegalArgumentException("Confidence must be between 0 and 100");
        if (knowledgeType == null) throw new IllegalArgumentException("KnowledgeType is required");
    }

    public boolean isHighValue() {
        return confidence >= 80.0 && successRate >= 70.0;
    }

    /**
     * 是否可应用于指定 Resource 类型（对齐 Blueprint §4.1 isApplicableTo）
     */
    public boolean isApplicableTo(String resourceType, String metricName) {
        if (applicableConditions == null || applicableConditions.isEmpty()) return true;
        String condType = applicableConditions.get("resourceType");
        String condMetric = applicableConditions.get("metricName");
        boolean typeMatch = condType == null || condType.equalsIgnoreCase(resourceType);
        boolean metricMatch = condMetric == null || condMetric.equalsIgnoreCase(metricName);
        return typeMatch && metricMatch;
    }

    /**
     * 升级版本（返回新 version Knowledge，原记录不变）
     */
    public Knowledge upgrade(Map<String, String> newConditions, Recommendation newRecommendation) {
        return new Knowledge(
                this.knowledgeId, this.title, this.description, this.knowledgeType,
                this.evidenceId, this.verificationId,
                this.confidence,
                newConditions != null ? newConditions : this.applicableConditions,
                newRecommendation != null ? newRecommendation : this.recommendation,
                this.successRate,
                LocalDateTime.now(),
                this.version + 1);
    }

    // Getters
    public KnowledgeId getKnowledgeId() { return knowledgeId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public KnowledgeType getKnowledgeType() { return knowledgeType; }
    public String getEvidenceId() { return evidenceId; }
    public String getVerificationId() { return verificationId; }
    public double getConfidence() { return confidence; }
    public Map<String, String> getApplicableConditions() { return applicableConditions; }
    public Recommendation getRecommendation() { return recommendation; }
    public double getSuccessRate() { return successRate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Knowledge knowledge = (Knowledge) o;
        return version == knowledge.version && Objects.equals(knowledgeId, knowledge.knowledgeId);
    }

    @Override
    public int hashCode() { return Objects.hash(knowledgeId, version); }

    @Override
    public String toString() {
        return "Knowledge{id=" + knowledgeId + " v" + version + ", type=" + knowledgeType + ", confidence=" + confidence + "}";
    }
}
