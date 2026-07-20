package com.aipe.relationship.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Relationship 聚合根
 *
 * <p>Resource 之间的一条有向关联：自身拥有独立 ID、类型、置信度、来源、生命周期。
 *
 * <p>Architecture Law-005：Relationship Is First-Class Citizen —— 不只是字段，是一个独立对象。
 *
 * <p>领域特征（对齐 M2-008 ch4）：
 * <ul>
 *   <li>Directed: 必须具有 Source → Target 方向</li>
 *   <li>Typed: 必须拥有唯一类型</li>
 *   <li>Independent: 独立生命周期，不随 Resource 删除而消失</li>
 *   <li>Observable: Relationship 自身可产生 Observation</li>
 *   <li>Versioned: Schema 版本独立维护</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Relationship {

    /**
     * 全局唯一标识（值对象）
     */
    private final RelationshipId relationshipId;

    /**
     * Relationship 类型（DEPENDS_ON / CALLS / RUNS_ON / … 共 10 种）
     */
    private final RelationshipType relationshipType;

    /**
     * 源 Resource ID（必填 - 新建时校验引用存在）
     */
    private final String sourceResourceId;

    /**
     * 目标 Resource ID（必填 - 新建时校验引用存在）
     */
    private final String targetResourceId;

    /**
     * 方向（SINGLE / BIDIRECTIONAL）
     */
    private final RelationshipDirection direction;

    /**
     * AI/Discovery 判定置信度（0~100）
     */
    private final double confidence;

    /**
     * 发现来源（Discovery / AI / MANUAL）
     */
    private final String discoveredBy;

    /**
     * 生命周期状态（ACTIVE / INACTIVE / ARCHIVED）
     */
    private RelationshipStatus status;

    /**
     * 扩展属性（版本约束、合约类型等）
     */
    private final Map<String, String> labels;

    /**
     * 首次发现时间
     */
    private final LocalDateTime discoveredAt;

    /**
     * 最近更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 构造函数（包级私有，强制使用 Factory）
     */
    Relationship(RelationshipId relationshipId,
                 RelationshipType relationshipType,
                 String sourceResourceId,
                 String targetResourceId,
                 RelationshipDirection direction,
                 double confidence,
                 String discoveredBy,
                 RelationshipStatus status,
                 Map<String, String> labels,
                 LocalDateTime discoveredAt,
                 LocalDateTime updatedAt) {
        this.relationshipId = relationshipId;
        this.relationshipType = relationshipType;
        this.sourceResourceId = sourceResourceId;
        this.targetResourceId = targetResourceId;
        this.direction = direction;
        this.confidence = confidence;
        this.discoveredBy = discoveredBy;
        this.status = status;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.discoveredAt = discoveredAt;
        this.updatedAt = updatedAt;
    }

    // ==================== 业务方法 ====================

    /**
     * 校验 Relationship 是否合法
     *
     * <p>必须满足：source + target + type 必填 + source ≠ target
     *
     * @throws IllegalArgumentException 校验失败
     */
    public void validate() {
        if (relationshipId == null) {
            throw new IllegalArgumentException("RelationshipId is required");
        }
        if (sourceResourceId == null || sourceResourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("SourceResourceId is required");
        }
        if (targetResourceId == null || targetResourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("TargetResourceId is required");
        }
        if (sourceResourceId.equals(targetResourceId)) {
            throw new IllegalArgumentException("Source and Target Resource cannot be the same");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("RelationshipType is required");
        }
        if (confidence < 0.0 || confidence > 100.0) {
            throw new IllegalArgumentException("Confidence must be between 0 and 100");
        }
    }

    /**
     * 是否涉及指定 Resource（Source 或 Target）
     *
     * @param resourceId Resource ID
     * @return 是否关联
     */
    public boolean involves(String resourceId) {
        return sourceResourceId != null && sourceResourceId.equals(resourceId)
                || targetResourceId != null && targetResourceId.equals(resourceId);
    }

    /**
     * 是否有向（direction = SINGLE）
     *
     * @return 是否有向
     */
    public boolean isDirected() {
        return direction == RelationshipDirection.SINGLE;
    }

    /**
     * 反转关系（source ↔ target）。
     *
     * <p>反转时同时改变类型（RUNS_ON ↔ HOSTS 等）。
     *
     * @return 反转后的 Relationship（新实例）
     */
    public Relationship reverse() {
        return new Relationship(
                relationshipId,
                relationshipType.reverse(),
                targetResourceId,
                sourceResourceId,
                direction,
                confidence,
                discoveredBy,
                status,
                labels,
                discoveredAt,
                updatedAt
        );
    }

    /**
     * 是否处于 ACTIVE 状态（参与 Topology 计算）
     *
     * @return 是否活跃
     */
    public boolean isActive() {
        return status == RelationshipStatus.ACTIVE;
    }

    /**
     * 是否对上游/下游影响传播链有贡献（用于 Impact Analysis）
     *
     * @return 是否参与影响传播
     */
    public boolean propagatesImpact() {
        return isActive() && relationshipType.isImpactPropagation();
    }

    /**
     * 归档 Relationship
     */
    public void archive() {
        this.status = RelationshipStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活 Relationship
     */
    public void activate() {
        this.status = RelationshipStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== Getter ====================

    public RelationshipId getRelationshipId() {
        return relationshipId;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public String getTargetResourceId() {
        return targetResourceId;
    }

    public RelationshipDirection getDirection() {
        return direction;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getDiscoveredBy() {
        return discoveredBy;
    }

    public RelationshipStatus getStatus() {
        return status;
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public LocalDateTime getDiscoveredAt() {
        return discoveredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        return Objects.equals(relationshipId, that.relationshipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipId);
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "id=" + relationshipId +
                ", type=" + relationshipType +
                ", source='" + sourceResourceId + '\'' +
                ", target='" + targetResourceId + '\'' +
                ", direction=" + direction +
                ", confidence=" + confidence +
                ", status=" + status +
                '}';
    }
}
