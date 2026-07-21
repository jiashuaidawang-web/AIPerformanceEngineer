package com.aipe.knowledge.domain;

import java.util.List;
import java.util.Optional;

/**
 * Knowledge 仓储接口（Domain 层）
 *
 * <p>Gateway Law-001/003
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface KnowledgeRepository {

    Knowledge save(Knowledge knowledge);

    Optional<Knowledge> findById(KnowledgeId id);

    Optional<Knowledge> findById(String id);

    List<Knowledge> findAllVersions(KnowledgeId id);

    Optional<Knowledge> findLatest(KnowledgeId id);

    List<Knowledge> findByType(KnowledgeType type);

    List<Knowledge> findByEvidenceId(String evidenceId);

    List<Knowledge> findByMinConfidence(double minConfidence);

    List<Knowledge> findAll();
}
