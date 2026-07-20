package com.aipe.relationship.domain;

import java.util.List;
import java.util.Optional;

/**
 * Relationship 仓储接口
 *
 * <p>Gateway Law-001：Repository Returns Domain, Never PO
 * <p>Gateway Law-002：One Aggregate, One Repository
 * <p>Gateway Law-003：Repository Interface In Domain, Implementation In Infrastructure
 *
 * <p>Relationship 的 Primary Storage 是 MySQL（IM-003 / IM-005）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface RelationshipRepository {

    /**
     * 保存 Relationship（新增）
     *
     * @param relationship Relationship 聚合根（不可为 null）     * @return 保存后的 Relationship
     */
    Relationship save(Relationship relationship);

    /**
     * 根据 ID 查找 Relationship
     *
     * @param id Relationship ID
     * @return Optional Relationship
     */
    Optional<Relationship> findById(RelationshipId id);

    /**
     * 根据 ID 查找 Relationship（字符串版本）
     *
     * @param id Relationship ID 字符串
     * @return Optional Relationship
     */
    Optional<Relationship> findById(String id);

    /**
     * 查询 Resource 作为 Source 的所有 Relationship
     *
     * @param sourceResourceId 源 Resource ID
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findBySource(String sourceResourceId);

    /**
     * 查询 Resource 作为 Target 的所有 Relationship
     *
     * @param targetResourceId 目标 Resource ID
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findByTarget(String targetResourceId);

    /**
     * 查询 Resource 的所有 Relationship（Source + Target 双方）
     *
     * @param resourceId Resource ID
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findByResource(String resourceId);

    /**
     * 按类型查询 Relationship
     *
     * @param type Relationship 类型
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findByType(RelationshipType type);

    /**
     * 查询 Resource 的邻居（一度关系，Source + Target 双方）
     *
     * <p>用于 Topology neighbors 查询
     *
     * @param resourceId Resource ID
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findNeighbors(String resourceId);

    /**
     * 查询所有 ACTIVE 的 Relationship（用于 Topology 构建）
     *
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findAllActive();

    /**
     * 查询所有 Relationship
     *
     * @return Relationship 列表（不可为 null）
     */
    List<Relationship> findAll();

    /**
     * 根据 ID 删除 Relationship（逻辑删除：归档）
     *
     * @param id Relationship ID
     * @return 是否删除成功
     */
    boolean deleteById(RelationshipId id);

    /**
     * 统计 Resource 的 Relationship 数量
     *
     * @param resourceId Resource ID
     * @return 数量
     */
    long countByResource(String resourceId);
}
