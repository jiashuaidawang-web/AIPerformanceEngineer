package com.aipe.relationship.infrastructure;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Relationship 持久化对象（PO）
 *
 * <p>对应 MySQL relationship 表（对齐 WP013 Blueprint §6）
 * <p>Gateway Law-001：Repository 不返回 PO，返回 Domain（通过 Converter 转换）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("relationship")
public class RelationshipPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Relationship ID（业务主键 UUID）
     * 对齐 IM-005 / Blueprint §6: VARCHAR(64) PRIMARY KEY
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * Relationship 类型
     */
    @TableField("relationship_type")
    private String relationshipType;

    /**
     * 源 Resource ID
     */
    @TableField("source_resource_id")
    private String sourceResourceId;

    /**
     * 目标 Resource ID
     */
    @TableField("target_resource_id")
    private String targetResourceId;

    /**
     * 方向（SINGLE / BIDIRECTIONAL）
     */
    @TableField("direction")
    private String direction;

    /**
     * 置信度（0~100）
     */
    @TableField("confidence")
    private Double confidence;

    /**
     * 发现来源（Discovery / AI / MANUAL）
     */
    @TableField("discovered_by")
    private String discoveredBy;

    /**
     * 生命周期状态（ACTIVE / INACTIVE / ARCHIVED）
     */
    @TableField("status")
    private String status;

    /**
     * 扩展属性（JSON）
     */
    @TableField("labels")
    private String labels;

    /**
     * 首次发现时间
     */
    @TableField("discovered_at")
    private LocalDateTime discoveredAt;

    /**
     * 最近更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // ==================== Getter & Setter ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(String sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    public String getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(String targetResourceId) {
        this.targetResourceId = targetResourceId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getDiscoveredBy() {
        return discoveredBy;
    }

    public void setDiscoveredBy(String discoveredBy) {
        this.discoveredBy = discoveredBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public LocalDateTime getDiscoveredAt() {
        return discoveredAt;
    }

    public void setDiscoveredAt(LocalDateTime discoveredAt) {
        this.discoveredAt = discoveredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RelationshipPO{" +
                "id='" + id + '\'' +
                ", type='" + relationshipType + '\'' +
                ", source='" + sourceResourceId + '\'' +
                ", target='" + targetResourceId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
