package com.aipe.execution.infrastructure;

import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.ExecutionBuilder;
import com.aipe.execution.domain.ExecutionId;
import com.aipe.execution.domain.ExecutionStatus;
import com.aipe.execution.domain.ExecutionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Execution 转换器（PO ↔ Domain）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ExecutionConverter {

    private static final Logger log = LoggerFactory.getLogger(ExecutionConverter.class);

    public ExecutionPO toPO(Execution execution) {
        if (execution == null) return null;
        ExecutionPO po = new ExecutionPO();
        po.setId(execution.getExecutionId() != null ? execution.getExecutionId().getValue() : null);
        po.setRecommendationId(execution.getRecommendationId());
        po.setExecutor(execution.getExecutor());
        po.setExecutionType(execution.getExecutionType() != null ? execution.getExecutionType().name() : null);
        po.setStatus(execution.getStatus() != null ? execution.getStatus().name() : null);
        po.setBeforeSnapshot(execution.getBeforeSnapshot());
        po.setAfterSnapshot(execution.getAfterSnapshot());
        po.setImprovementScore(execution.getImprovementScore());
        po.setStartedAt(execution.getStartedAt());
        po.setFinishedAt(execution.getFinishedAt());
        po.setRollbackInfo(execution.getRollbackInfo());
        po.setCreatedAt(execution.getCreatedAt());
        po.setUpdatedAt(LocalDateTime.now());
        po.setVersion(execution.getVersion());
        return po;
    }

    public Execution toDomain(ExecutionPO po) {
        if (po == null) return null;
        ExecutionId id = po.getId() != null ? ExecutionId.of(po.getId()) : null;
        return ExecutionBuilder.reconstruct(
                id, po.getRecommendationId(), po.getExecutor(),
                ExecutionType.parse(po.getExecutionType()),
                ExecutionStatus.parse(po.getStatus()),
                po.getBeforeSnapshot(), po.getAfterSnapshot(),
                po.getImprovementScore() != null ? po.getImprovementScore() : 0.0,
                po.getStartedAt(), po.getFinishedAt(), po.getRollbackInfo(),
                po.getCreatedAt(), po.getUpdatedAt(),
                po.getVersion() != null ? po.getVersion() : 1);
    }
}
