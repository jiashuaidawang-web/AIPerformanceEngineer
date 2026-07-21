package com.aipe.evidence.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evidence 持久化对象（PO）
 *
 * <p>对应 MySQL evidence 表（对齐 WP014 Blueprint §6 / IM-003）
 * <p>Evidence 是元数据 → 落 MySQL（不是 ClickHouse - Persistence Law-002）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("evidence")
public class EvidencePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("evidence_type")
    private String evidenceType;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("root_resource_id")
    private String rootResourceId;

    @TableField("observation_ids")
    private String observationIds; // JSON array

    @TableField("relationship_ids")
    private String relationshipIds; // JSON array

    @TableField("timeline_id")
    private String timelineId;

    @TableField("confidence")
    private Double confidence;

    @TableField("reasoning_steps")
    private String reasoningSteps; // JSON array

    @TableField("status")
    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("version")
    private Integer version;

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRootResourceId() { return rootResourceId; }
    public void setRootResourceId(String rootResourceId) { this.rootResourceId = rootResourceId; }
    public String getObservationIds() { return observationIds; }
    public void setObservationIds(String observationIds) { this.observationIds = observationIds; }
    public String getRelationshipIds() { return relationshipIds; }
    public void setRelationshipIds(String relationshipIds) { this.relationshipIds = relationshipIds; }
    public String getTimelineId() { return timelineId; }
    public void setTimelineId(String timelineId) { this.timelineId = timelineId; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getReasoningSteps() { return reasoningSteps; }
    public void setReasoningSteps(String reasoningSteps) { this.reasoningSteps = reasoningSteps; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public String toString() {
        return "EvidencePO{id='" + id + "', type='" + evidenceType + "', confidence=" + confidence + "}";
    }
}
