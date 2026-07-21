package com.aipe.evidence.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evidence 响应 DTO
 *
 * <p>含 evidenceId + confidence + reasoningSteps + observationIds
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class EvidenceResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String evidenceId;
    private String evidenceType;
    private String title;
    private String description;
    private String rootResourceId;
    private List<String> observationIds;
    private List<String> relationshipIds;
    private String timelineId;
    private Double confidence;
    private List<ReasoningStepDto> reasoningSteps;
    private String status;
    private LocalDateTime createdAt;
    private Integer version;
    private String explanation;

    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRootResourceId() { return rootResourceId; }
    public void setRootResourceId(String rootResourceId) { this.rootResourceId = rootResourceId; }
    public List<String> getObservationIds() { return observationIds; }
    public void setObservationIds(List<String> observationIds) { this.observationIds = observationIds; }
    public List<String> getRelationshipIds() { return relationshipIds; }
    public void setRelationshipIds(List<String> relationshipIds) { this.relationshipIds = relationshipIds; }
    public String getTimelineId() { return timelineId; }
    public void setTimelineId(String timelineId) { this.timelineId = timelineId; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public List<ReasoningStepDto> getReasoningSteps() { return reasoningSteps; }
    public void setReasoningSteps(List<ReasoningStepDto> reasoningSteps) { this.reasoningSteps = reasoningSteps; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    /**
     * 推理步骤 DTO
     */    public static class ReasoningStepDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private int step;
        private String action;
        private String result;
        private double confidence;

        public int getStep() { return step; }
        public void setStep(int step) { this.step = step; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
