package com.aipe.relationship.domain;

import java.util.Objects;

/**
 * Topology 边值对象
 *
 * <p>表示拓扑视图中的一条 Relationship 边（对齐 M2-009 ch7 edges）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceEdge {

    /**
     * Relationship ID
     */
    private final String relationshipId;

    /**
     * 源 Resource ID
     */
    private final String sourceResourceId;

    /**
     * 目标 Resource ID
     */
    private final String targetResourceId;

    /**
     * Relationship 类型
     */
    private final RelationshipType relationshipType;

    /**
     * 方向
     */
    private final RelationshipDirection direction;

    /**
     * 置信度
     */
    private final double confidence;

    public ResourceEdge(String relationshipId,
                       String sourceResourceId,
                       String targetResourceId,
                       RelationshipType relationshipType,
                       RelationshipDirection direction,
                       double confidence) {
        this.relationshipId = relationshipId;
        this.sourceResourceId = sourceResourceId;
        this.targetResourceId = targetResourceId;
        this.relationshipType = relationshipType;
        this.direction = direction;
        this.confidence = confidence;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public String getTargetResourceId() {
        return targetResourceId;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public RelationshipDirection getDirection() {
        return direction;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceEdge that = (ResourceEdge) o;
        return Objects.equals(relationshipId, that.relationshipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipId);
    }

    @Override
    public String toString() {
        return "ResourceEdge{" +
                "id='" + relationshipId + '\'' +
                ", source='" + sourceResourceId + '\'' +
                ", target='" + targetResourceId + '\'' +
                ", type=" + relationshipType +
                '}';
    }
}
