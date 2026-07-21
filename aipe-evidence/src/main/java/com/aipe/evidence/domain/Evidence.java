package com.aipe.evidence.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Evidence 聚合根（不可变 - Immutable）
 *
 * <p>M2-011 Evidence Model：AI 推理结果（可解释证据链）。
 * <p>AI Principle-001: Evidence Before Conclusion — AI 必须先生成 Evidence 才能输出 Recommendation。
 *
 * <p>不可变特征（M2-011 ch4.5）：一旦生成不得修改。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Evidence implements Serializable {

    private static final long serialVersionUID = 1L;

    private final EvidenceId evidenceId;
    private final EvidenceType evidenceType;
    private final String title;
    private final String description;
    private final String rootResourceId;
    private final List<String> observationIds;
    private final List<String> relationshipIds;
    private final String timelineId;
    private final double confidence;
    private final List<ReasoningStep> reasoningSteps;
    private final EvidenceStatus status;
    private final java.time.LocalDateTime createdAt;
    private final java.time.LocalDateTime updatedAt;
    private final int version;

    Evidence(EvidenceId evidenceId,
             EvidenceType evidenceType,
             String title,
             String description,
             String rootResourceId,
             List<String> observationIds,
             List<String> relationshipIds,
             String timelineId,
             double confidence,
             List<ReasoningStep> reasoningSteps,
             EvidenceStatus status,
             java.time.LocalDateTime createdAt,
             java.time.LocalDateTime updatedAt,
             int version) {
        this.evidenceId = evidenceId;
        this.evidenceType = evidenceType;
        this.title = title;
        this.description = description;
        this.rootResourceId = rootResourceId;
        this.observationIds = observationIds != null ? Collections.unmodifiableList(observationIds) : Collections.emptyList();
        this.relationshipIds = relationshipIds != null ? Collections.unmodifiableList(relationshipIds) : Collections.emptyList();
        this.timelineId = timelineId;
        this.confidence = confidence;
        this.reasoningSteps = reasoningSteps != null ? Collections.unmodifiableList(reasoningSteps) : Collections.emptyList();
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    /**
     * 校验 Evidence 是否合法
     *
     * @throws IllegalArgumentException 校验失败
     */
    public void validate() {
        if (evidenceId == null) {
            throw new IllegalArgumentException("EvidenceId is required");
        }
        if (rootResourceId == null || rootResourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("RootResourceId is required (Law-002)");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (confidence < 0.0 || confidence > 100.0) {
            throw new IllegalArgumentException("Confidence must be between 0 and 100");
        }
        if (evidenceType == null) {
            throw new IllegalArgumentException("EvidenceType is required");
        }
    }

    public boolean isHighConfidence() {
        return confidence >= 80.0;
    }

    public boolean isVerified() {
        return status == EvidenceStatus.VERIFIED;
    }

    public boolean referencesObservations() {
        return observationIds != null && !observationIds.isEmpty();
    }

    /**
     * 产生自然语言解释
     *
     * <p>对齐 M2-011 ch4.1 Explainable — AI 推理路径必须完整保存
     */
    public String explain() {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(title).append("】\n");
        sb.append("类型: ").append(evidenceType).append("\n");
        sb.append("根资源: ").append(rootResourceId).append("\n");
        sb.append("置信度: ").append(String.format("%.1f%%", confidence)).append("\n");
        sb.append("描述: ").append(description != null ? description : "无").append("\n");
        sb.append("推理步骤:\n");
        if (reasoningSteps != null) {
            int stepNum = 1;
            for (ReasoningStep step : reasoningSteps) {
                sb.append("  ").append(stepNum++).append(". ")
                        .append(step.getAction())
                        .append(" → ").append(step.getResult())
                        .append(" (置信度: ").append(String.format("%.1f%%", step.getConfidence())).append(")\n");
            }
        }
        sb.append("引用 Observation 数: ").append(observationIds != null ? observationIds.size() : 0).append("\n");
        return sb.toString();
    }

    // Getter
    public EvidenceId getEvidenceId() { return evidenceId; }
    public EvidenceType getEvidenceType() { return evidenceType; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRootResourceId() { return rootResourceId; }
    public List<String> getObservationIds() { return observationIds; }
    public List<String> getRelationshipIds() { return relationshipIds; }
    public String getTimelineId() { return timelineId; }
    public double getConfidence() { return confidence; }
    public List<ReasoningStep> getReasoningSteps() { return reasoningSteps; }
    public EvidenceStatus getStatus() { return status; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public int getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(evidenceId, evidence.evidenceId);
    }

    @Override
    public int hashCode() { return Objects.hash(evidenceId); }

    @Override
    public String toString() {
        return "Evidence{id=" + evidenceId + ", type=" + evidenceType + ", confidence=" + confidence + "}";
    }
}
