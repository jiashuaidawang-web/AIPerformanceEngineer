package com.aipe.execution.application;

import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.ExecutionBuilder;
import com.aipe.execution.domain.ExecutionId;
import com.aipe.execution.domain.ExecutionRepository;
import com.aipe.execution.domain.ExecutionStatus;
import com.aipe.execution.domain.ExecutionType;
import com.aipe.execution.domain.Optimization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Execution 服务（Application 层核心）
 *
 * <p>对齐 WP018 Blueprint §4.2 ExecutionService
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private OptimizationService optimizationService;

    /**
     * 创建 + 启动 Execution（beforeSnapshot 采集 + PENDING → EXECUTING）
     */
    public Execution execute(String recommendationId, String executor, String beforeSnapshot) {
        Execution execution = ExecutionBuilder.create(
                ExecutionId.generate(), recommendationId, executor, ExecutionType.MANUAL, beforeSnapshot);

        Execution saved = executionRepository.save(execution);
        // 立即开始执行
        Execution executing = saved.markExecuting();
        executionRepository.updateStatus(executing.getExecutionId(), ExecutionStatus.EXECUTING);
        log.info("Execution started: id={}, recommendation={}", saved.getRecommendationId(), recommendationId);
        return executionRepository.findById(saved.getExecutionId()).orElse(executing);
    }

    /**
     * 完成 Execution（afterSnapshot 采集 + Optimization 评估）
     */
    public Execution complete(ExecutionId executionId, String afterSnapshot) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        // 评估优化效果
        Execution tempExec = ExecutionBuilder.reconstruct(
                execution.getExecutionId(), execution.getRecommendationId(), execution.getExecutor(),
                execution.getExecutionType(), ExecutionStatus.EXECUTING,
                execution.getBeforeSnapshot(), afterSnapshot, 0.0,
                execution.getStartedAt(), LocalDateTime.now(), null,
                execution.getCreatedAt(), LocalDateTime.now(), execution.getVersion());

        Optimization optimization = optimizationService.evaluate(tempExec);

        // 标记成功/失败
        Execution result;
        if ("PASS".equals(optimization.getStatus())) {
            result = execution.markSuccess(afterSnapshot, optimization.getImprovementScore());
        } else {
            result = execution.markFailed(afterSnapshot);
        }
        executionRepository.updateStatus(result.getExecutionId(), result.getStatus());
        log.info("Execution completed: id={}, status={}, score={}", executionId, result.getStatus(), result.getImprovementScore());
        return result;
    }

    /**
     * 回滚 Execution
     */
    public Execution rollback(ExecutionId executionId, String reason) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        String rollbackInfo = "{\"reason\":\"" + (reason != null ? reason : "未说明") + "\",\"time\":\"" + LocalDateTime.now() + "\"}";
        Execution rolledBack = execution.markRolledBack(rollbackInfo);
        executionRepository.updateStatus(rolledBack.getExecutionId(), ExecutionStatus.ROLLED_BACK);
        log.info("Execution rolled back: id={}, reason={}", executionId, reason);
        return rolledBack;
    }

    /**
     * 生成执行报告
     */
    public String generateReport(ExecutionId executionId) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        StringBuilder report = new StringBuilder();
        report.append("=== Execution Report ===\n");
        report.append("Execution ID: ").append(executionId.getValue()).append("\n");
        report.append("Recommendation ID: ").append(execution.getRecommendationId()).append("\n");
        report.append("Executor: ").append(execution.getExecutor()).append("\n");
        report.append("Status: ").append(execution.getStatus()).append("\n");
        report.append("Improvement Score: ").append(String.format("%.1f", execution.getImprovementScore())).append("/100\n");
        report.append("Started At: ").append(execution.getStartedAt()).append("\n");
        report.append("Finished At: ").append(execution.getFinishedAt()).append("\n");
        report.append("Before Snapshot: ").append(execution.getBeforeSnapshot()).append("\n");
        report.append("After Snapshot: ").append(execution.getAfterSnapshot()).append("\n");
        if (execution.getRollbackInfo() != null) {
            report.append("Rollback Info: ").append(execution.getRollbackInfo()).append("\n");
        }
        return report.toString();
    }

    public Optional<Execution> findById(ExecutionId id) { return executionRepository.findById(id); }
    public List<Execution> findByRecommendation(String recommendationId) { return executionRepository.findByRecommendation(recommendationId); }
    public List<Execution> findByStatus(ExecutionStatus status) { return executionRepository.findByStatus(status); }
}
