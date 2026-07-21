package com.aipe.execution.infrastructure;

import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.ExecutionId;
import com.aipe.execution.domain.ExecutionRepository;
import com.aipe.execution.domain.ExecutionStatus;
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
 * Execution 仓储实现（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class ExecutionRepositoryImpl implements ExecutionRepository {

    private static final Logger log = LoggerFactory.getLogger(ExecutionRepositoryImpl.class);

    @Autowired
    private ExecutionMapper executionMapper;

    @Autowired
    private ExecutionConverter executionConverter;

    @Override
    public Execution save(Execution execution) {
        if (execution == null) throw new IllegalArgumentException("Execution cannot be null");
        ExecutionPO po = executionConverter.toPO(execution);
        executionMapper.insert(po);
        log.debug("Saved execution: id={}, status={}", po.getId(), po.getStatus());
        return executionConverter.toDomain(po);
    }

    @Override
    public Optional<Execution> findById(ExecutionId id) {
        if (id == null) return Optional.empty();
        return findById(id.getValue());
    }

    @Override
    public Optional<Execution> findById(String id) {
        if (id == null) return Optional.empty();
        ExecutionPO po = executionMapper.selectById(id);
        return Optional.ofNullable(executionConverter.toDomain(po));
    }

    @Override
    public List<Execution> findByRecommendation(String recommendationId) {
        if (recommendationId == null) return Collections.emptyList();
        List<ExecutionPO> poList = executionMapper.selectByRecommendation(recommendationId);
        return poList.stream().map(executionConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Execution> findByStatus(ExecutionStatus status) {
        if (status == null) return Collections.emptyList();
        List<ExecutionPO> poList = executionMapper.selectByStatus(status.name());
        return poList.stream().map(executionConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Execution> findByExecutor(String executor) {
        if (executor == null) return Collections.emptyList();
        List<ExecutionPO> poList = executionMapper.selectByExecutor(executor);
        return poList.stream().map(executionConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Execution> findAll() {
        List<ExecutionPO> poList = executionMapper.selectList(new QueryWrapper<>());
        return poList.stream().map(executionConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(ExecutionId id, ExecutionStatus newStatus) {
        if (id == null || newStatus == null) return false;
        int rows = executionMapper.updateStatus(id.getValue(), newStatus.name());
        log.debug("Updated execution status: id={}, status={}, rows={}", id.getValue(), newStatus, rows);
        return rows > 0;
    }
}
