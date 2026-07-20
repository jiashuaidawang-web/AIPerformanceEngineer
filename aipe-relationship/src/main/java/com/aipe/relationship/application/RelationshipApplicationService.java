package com.aipe.relationship.application;

import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipDirection;
import com.aipe.relationship.domain.RelationshipFactory;
import com.aipe.relationship.domain.RelationshipId;
import com.aipe.relationship.domain.RelationshipRepository;
import com.aipe.relationship.domain.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Relationship 业务编排服务（Application 层事务 Owner）
 *
 * <p>负责 Relationship 的完整 CRUD 编排 + 查询（Orchestration Law-001）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class RelationshipApplicationService {

    private static final Logger log = LoggerFactory.getLogger(RelationshipApplicationService.class);

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private RelationshipValidator relationshipValidator;

    /**
     * 新建 Relationship
     *
     * @param type          Relationship 类型
     * @param sourceResourceId 源 Resource（必填）
     * @param targetResourceId 目标 Resource（必填）
     * @param direction     方向
     * @param confidence    置信度（0~100，默认 100）
     * @param discoveredBy  发现来源
     * @param labels        扩展属性
     * @return 创建后的 Relationship（含生成的 ID）
     */
    @Transactional
    public Relationship createRelationship(RelationshipType type,
                                          String sourceResourceId,
                                          String targetResourceId,
                                          RelationshipDirection direction,
                                          double confidence,
                                          String discoveredBy,
                                          Map<String, String> labels) {
        Relationship relationship = RelationshipFactory.create(
                type, sourceResourceId, targetResourceId,
                direction != null ? direction : RelationshipDirection.SINGLE,
                confidence, discoveredBy, labels);
        // 存在性校验 + 循环校验
        relationshipValidator.validateForCreate(relationship);
        Relationship saved = relationshipRepository.save(relationship);
        log.info("Relationship created: id={}, type={}, source={}, target={}",
                saved.getRelationshipId(), type, sourceResourceId, targetResourceId);
        return saved;
    }

    /**
     * 删除 Relationship（归档）
     *
     * @param id Relationship ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean removeRelationship(RelationshipId id) {
        if (id == null) {
            throw new IllegalArgumentException("RelationshipId cannot be null");
        }
        Optional<Relationship> existing = relationshipRepository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        boolean deleted = relationshipRepository.deleteById(id);
        if (deleted) {
            log.info("Relationship removed: id={}", id);
        }
        return deleted;
    }

    /**
     * 根据 ID 查询
     */
    @Transactional(readOnly = true)
    public Optional<Relationship> findById(RelationshipId id) {
        return relationshipRepository.findById(id);
    }

    /**
     * 查询 Resource 的所有 Relationships（Source + Target 双方）
     */
    @Transactional(readOnly = true)
    public List<Relationship> findRelationships(String resourceId) {
        return relationshipRepository.findByResource(resourceId);
    }

    /**
     * 查询 Resource 的上游依赖（沿 target→source 方向）
     */
    @Transactional(readOnly = true)
    public List<Relationship> findUpstream(String resourceId) {
        if (resourceId == null) {
            return java.util.Collections.emptyList();
        }
        return relationshipRepository.findByTarget(resourceId);
    }

    /**
     * 查询 Resource 的下游依赖（沿 source→target 方向）
     */
    @Transactional(readOnly = true)
    public List<Relationship> findDownstream(String resourceId) {
        if (resourceId == null) {
            return java.util.Collections.emptyList();
        }
        return relationshipRepository.findBySource(resourceId);
    }

    /**
     * 按类型查询
     */
    @Transactional(readOnly = true)
    public List<Relationship> findByType(RelationshipType type) {
        return relationshipRepository.findByType(type);
    }

    /**
     * 查询 Resource 的邻居（一度关系）
     */
    @Transactional(readOnly = true)
    public List<Relationship> findNeighbors(String resourceId) {
        return relationshipRepository.findNeighbors(resourceId);
    }

    /**
     * 查询所有 ACTIVE Relationship
     */
    @Transactional(readOnly = true)
    public List<Relationship> findAllActive() {
        return relationshipRepository.findAllActive();
    }
}
