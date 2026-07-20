package com.aipe.relationship.application;

import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipRepository;
import com.aipe.relationship.domain.RelationshipSpecification;
import com.aipe.relationship.domain.ResourceLookupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Relationship 校验器
 *
 * <p>封装复杂的业务校验逻辑：
 * <ul>
 *   <li>source / target Resource 必须存在（通过 ResourceLookupPort 端口校验）</li>
 *   <li>循环依赖检测（A→B→C→A 拒绝）</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class RelationshipValidator {

    private static final Logger log = LoggerFactory.getLogger(RelationshipValidator.class);

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private ResourceLookupPort resourceLookupPort;

    @Autowired
    private GraphTraversal graphTraversal;

    /**
     * 校验 Relationship 是否可以创建
     *
     * @param relationship 待校验 Relationship
     * @throws IllegalArgumentException 校验失败
     */
    public void validateForCreate(Relationship relationship) {
        if (relationship == null) {
            throw new IllegalArgumentException("Relationship cannot be null");
        }
        RelationshipSpecification.validateForCreate(relationship);

        // 校验 source Resource 存在
        String sourceId = relationship.getSourceResourceId();
        if (!resourceLookupPort.exists(sourceId)) {
            throw new IllegalArgumentException(
                    "Source Resource does not exist: " + sourceId + " (Blueprint §9.1: source/target must exist)");
        }

        // 校验 target Resource 存在
        String targetId = relationship.getTargetResourceId();
        if (!resourceLookupPort.exists(targetId)) {
            throw new IllegalArgumentException(
                    "Target Resource does not exist: " + targetId + " (Blueprint §9.1: source/target must exist)");
        }

        // 循环依赖检测
        List<Relationship> existing = relationshipRepository.findAllActive();
        if (graphTraversal.wouldCreateCycle(relationship,
                graphTraversal.buildSourceIndex(existing))) {
            throw new IllegalArgumentException(
                    "Circular dependency detected: " + sourceId + " -> " + targetId + " would form a cycle");
        }
    }
}
