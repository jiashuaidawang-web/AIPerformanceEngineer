package com.aipe.relationship.infrastructure;

import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipId;
import com.aipe.relationship.domain.RelationshipRepository;
import com.aipe.relationship.domain.RelationshipStatus;
import com.aipe.relationship.domain.RelationshipType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Relationship 仓储实现（MySQL）
 *
 * <p>Gateway Law-003：Repository Implementation In Infrastructure
 * <p>Gateway Law-001：Returns Domain, Never PO
 * <p>Gateway Law-005：Repository Is Not Transaction Owner
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class RelationshipRepositoryImpl implements RelationshipRepository {

    private static final Logger log = LoggerFactory.getLogger(RelationshipRepositoryImpl.class);

    @Autowired
    private RelationshipMapper relationshipMapper;

    @Autowired
    private RelationshipConverter relationshipConverter;

    @Override
    public Relationship save(Relationship relationship) {
        if (relationship == null) {
            throw new IllegalArgumentException("Relationship cannot be null");
        }
        RelationshipPO po = relationshipConverter.toPO(relationship);
        relationshipMapper.insert(po);
        log.debug("Saved relationship: id={}, type={}, source={}, target={}",
                po.getId(), po.getRelationshipType(), po.getSourceResourceId(), po.getTargetResourceId());
        return relationshipConverter.toDomain(po);
    }

    @Override
    public Optional<Relationship> findById(RelationshipId id) {
        if (id == null) {
            return Optional.empty();
        }
        return findById(id.getValue());
    }

    @Override
    public Optional<Relationship> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        RelationshipPO po = relationshipMapper.selectById(id);
        return Optional.ofNullable(relationshipConverter.toDomain(po));
    }

    @Override
    public List<Relationship> findBySource(String sourceResourceId) {
        if (sourceResourceId == null || sourceResourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RelationshipPO> poList = relationshipMapper.selectBySource(sourceResourceId);
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findByTarget(String targetResourceId) {
        if (targetResourceId == null || targetResourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RelationshipPO> poList = relationshipMapper.selectByTarget(targetResourceId);
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findByResource(String resourceId) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RelationshipPO> poList = relationshipMapper.selectByResource(resourceId);
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findByType(RelationshipType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        List<RelationshipPO> poList = relationshipMapper.selectByType(type.name());
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findNeighbors(String resourceId) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RelationshipPO> poList = relationshipMapper.selectNeighbors(resourceId);
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findAllActive() {
        List<RelationshipPO> poList = relationshipMapper.selectAllActive();
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Relationship> findAll() {
        List<RelationshipPO> poList = relationshipMapper.selectList(new QueryWrapper<>());
        return poList.stream()
                .map(relationshipConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(RelationshipId id) {
        if (id == null) {
            return false;
        }
        // 归档（软删除），对齐 M2-008 ch6 Lifecycle
        RelationshipPO po = relationshipMapper.selectById(id.getValue());
        if (po == null) {
            return false;
        }
        po.setStatus(RelationshipStatus.ARCHIVED.name());
        int rows = relationshipMapper.updateById(po);
        log.debug("Archived relationship: id={}, rows={}", id.getValue(), rows);
        return rows > 0;
    }

    @Override
    public long countByResource(String resourceId) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return 0;
        }
        return relationshipMapper.countByResource(resourceId);
    }
}
