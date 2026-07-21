package com.aipe.execution.domain;

import java.util.List;
import java.util.Optional;

/**
 * Execution 仓储接口（Domain 层）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ExecutionRepository {

    Execution save(Execution execution);

    Optional<Execution> findById(ExecutionId id);

    Optional<Execution> findById(String id);

    List<Execution> findByRecommendation(String recommendationId);

    List<Execution> findByStatus(ExecutionStatus status);

    List<Execution> findByExecutor(String executor);

    List<Execution> findAll();

    boolean updateStatus(ExecutionId id, ExecutionStatus newStatus);
}
