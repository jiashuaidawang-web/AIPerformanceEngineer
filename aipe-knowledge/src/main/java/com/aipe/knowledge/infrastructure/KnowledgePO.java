package com.aipe.knowledge.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Knowledge 持久化对象（PO）
 *
 * <p>对应 MySQL knowledge 表（对齐 WP016 Blueprint §6 / IM-003）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("knowledge")
public class KnowledgePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_id", type = IdType.AUTO)
    private Long pkId;

    @TableField("id")
    private String id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("knowledge_type")
    private String knowledgeType;

    @TableField("evidence_id")
    private String evidenceId;

    @TableField("verification_id")
    private String verificationId;

    @TableField("confidence")
    private Double confidence;

    @TableField("applicable_conditions")
    private String applicableConditions; // JSON

    @TableField("recommendation")
    private String recommendation; // JSON

    @TableField("success_rate")
    private Double successRate;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("version")
    private Integer version;

    public Long getPkId() { return pkId; }
    public void setPkId(Long pkId) { this.pkId = pkId; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getApplicableConditions() { return applicableConditions; }
    public void setApplicableConditions(String applicableConditions) { this.applicableConditions = applicableConditions; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public String toString() {
        return "KnowledgePO{pkId=" + pkId + ", id='" + id + "', v=" + version + "}";
    }
}
