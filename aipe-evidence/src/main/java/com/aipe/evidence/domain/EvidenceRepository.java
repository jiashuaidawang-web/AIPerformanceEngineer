package com.aipe.evidence.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Evidence 仓储接口（Domain 层）
 *
 * <p>Gateway Law-001/003: 接口在 Domain，实现返回 Domain
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface EvidenceRepository {

    Evidence save(Evidence evidence);

    java.util.Optional<Evidence> findById(EvidenceId id);

    java.util.Optional<Evidence> findById(String id);

    List<Evidence> findByRootResource(String resourceId);

    List<Evidence> findByStatus(EvidenceStatus status);

    List<Evidence> findHighConfidence(double minConfidence);

    List<Evidence> findAll();

    boolean updateStatus(EvidenceId id, EvidenceStatus newStatus);

    long count();
}
