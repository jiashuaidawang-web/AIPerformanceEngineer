package com.aipe.relationship.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Relationship 工厂
 *
 * <p>负责创建合法的 Relationship 对象（Domain Law-003：Aggregate Must Have Factory）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RelationshipFactory {

    private RelationshipFactory() {
        // 工具类，禁止实例化
    }

    /**
     * 创建新的 Relationship（自动生成 ID、初始化状态和发现时间）
     *
     * @param relationshipType Relationship 类型
     * @param sourceResourceId 源 Resource ID（必填）
     * @param targetResourceId 目标 Resource ID（必填）
     * @param direction        方向
     * @param confidence       置信度（0~100）
     * @param discoveredBy     发现来源
     * @param labels           扩展属性
     * @return 新的 Relationship（已通过 validate()）
     */
    public static Relationship create(RelationshipType relationshipType,
                                      String sourceResourceId,
                                      String targetResourceId,
                                      RelationshipDirection direction,
                                      double confidence,
                                      String discoveredBy,
                                      Map<String, String> labels) {
        Relationship relationship = new Relationship(
                RelationshipId.generate(),
                relationshipType,
                sourceResourceId,
                targetResourceId,
                direction,
                confidence,
                discoveredBy != null ? discoveredBy : "MANUAL",
                RelationshipStatus.ACTIVE,
                labels,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        relationship.validate();
        return relationship;
    }

    /**
     * 从持久化数据重建 Relationship（Repository 专用）
     */
    public static Relationship reconstruct(RelationshipId relationshipId,
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
        return new Relationship(
                relationshipId,
                relationshipType,
                sourceResourceId,
                targetResourceId,
                direction,
                confidence,
                discoveredBy,
                status,
                labels,
                discoveredAt,
                updatedAt
        );
    }
}
