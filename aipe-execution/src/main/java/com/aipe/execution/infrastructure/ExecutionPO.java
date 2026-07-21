package com.aipe.execution.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Execution 持久化对象（PO）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("execution")
public class ExecutionPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("recommendation_id")
    private String recommendationId;

    @TableField("executor")
    private String executor;

    @TableField("execution_type")
    private String executionType;

    @TableField("status")
    private String status;

    @TableField("before_snapshot")
    private String beforeSnapshot;

    @TableField("after_snapshot")
    private String afterSnapshot;

    @TableField("improvement_score")
    private Double improvementScore;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("finished_at")
    private LocalDateTime finishedAt;

    @TableField("rollback_info")
    private String rollbackInfo;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("version")
    private Integer version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBeforeSnapshot() { return beforeSnapshot; }
    public void setBeforeSnapshot(String beforeSnapshot) { this.beforeSnapshot = beforeSnapshot; }
    public String getAfterSnapshot() { return afterSnapshot; }
    public void setAfterSnapshot(String afterSnapshot) { this.afterSnapshot = afterSnapshot; }
    public Double getImprovementScore() { return improvementScore; }
    public void setImprovementScore(Double improvementScore) { this.improvementScore = improvementScore; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    public String getRollbackInfo() { return rollbackInfo; }
    public void setRollbackInfo(String rollbackInfo) { this.rollbackInfo = rollbackInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public String toString() {
        return "ExecutionPO{id='" + id + "', status='" + status + "', score=" + improvementScore + "}";
    }
}
