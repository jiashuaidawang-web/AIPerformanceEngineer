package com.aipe.recommendation.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Recommendation 持久化对象（PO）
 *
 * <p>对应 MySQL recommendation 表
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("recommendation")
public class RecommendationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("knowledge_id")
    private String knowledgeId;

    @TableField("target_resource_id")
    private String targetResourceId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("priority")
    private String priority;

    @TableField("confidence")
    private Double confidence;

    @TableField("expected_outcome")
    private String expectedOutcome;

    @TableField("execution_plan")
    private String executionPlan; // JSON

    @TableField("rollback_plan")
    private String rollbackPlan; // JSON

    @TableField("status")
    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("version")
    private Integer version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getExecutionPlan() { return executionPlan; }
    public void setExecutionPlan(String executionPlan) { this.executionPlan = executionPlan; }
    public String getRollbackPlan() { return rollbackPlan; }
    public void setRollbackPlan(String rollbackPlan) { this.rollbackPlan = rollbackPlan; }
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
        return "RecommendationPO{id='" + id + "', priority='" + priority + "', status='" + status + "'}";
    }
}
