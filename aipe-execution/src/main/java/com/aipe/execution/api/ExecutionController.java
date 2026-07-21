package com.aipe.execution.api;

import com.aipe.execution.api.dto.ExecutionRequest;
import com.aipe.execution.api.dto.ExecutionResponse;
import com.aipe.execution.application.ExecutionService;
import com.aipe.execution.application.OptimizationService;
import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.ExecutionId;
import com.aipe.execution.domain.ExecutionStatus;
import com.aipe.execution.domain.Optimization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

    private static final Logger log = LoggerFactory.getLogger(ExecutionController.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private OptimizationService optimizationService;

    @PostMapping
    public ApiResponse<ExecutionResponse> create(@RequestBody ExecutionRequest request) {
        String beforeSnapshot = request.getBeforeSnapshot() != null ? request.getBeforeSnapshot()
                : "{\"snapshot\":\"auto-generated\",\"timestamp\":\"" + java.time.Instant.now() + "\"}";
        Execution exec = executionService.execute(request.getRecommendationId(), request.getExecutor(), beforeSnapshot);
        return ApiResponse.success(toResponse(exec, null));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<ExecutionResponse> complete(@PathVariable("id") String id,
                                                   @RequestBody(required = false) String afterSnapshot) {
        String after = afterSnapshot != null ? afterSnapshot
                : "{\"snapshot\":\"auto-generated\",\"timestamp\":\"" + java.time.Instant.now() + "\"}";
        Execution exec = executionService.complete(ExecutionId.of(id), after);
        Optimization opt = optimizationService.evaluate(exec);
        return ApiResponse.success(toResponse(exec, opt));
    }

    @PostMapping("/{id}/rollback")
    public ApiResponse<ExecutionResponse> rollback(@PathVariable("id") String id,
                                                   @RequestParam(value = "reason", defaultValue = "用户触发回滚") String reason) {
        Execution exec = executionService.rollback(ExecutionId.of(id), reason);
        return ApiResponse.success(toResponse(exec, null));
    }

    @GetMapping("/{id}")
    public ApiResponse<ExecutionResponse> findById(@PathVariable("id") String id) {
        Execution exec = executionService.findById(ExecutionId.of(id)).orElse(null);
        if (exec == null) return ApiResponse.error(404, "Execution not found: " + id);
        return ApiResponse.success(toResponse(exec, null));
    }

    @GetMapping("/{id}/report")
    public ApiResponse<String> report(@PathVariable("id") String id) {
        return ApiResponse.success(executionService.generateReport(ExecutionId.of(id)));
    }

    @GetMapping
    public ApiResponse<List<ExecutionResponse>> list(
            @RequestParam(value = "recommendation_id", required = false) String recommendationId,
            @RequestParam(value = "status", required = false) String status) {
        List<Execution> executions;
        if (recommendationId != null) executions = executionService.findByRecommendation(recommendationId);
        else if (status != null) {
            try { executions = executionService.findByStatus(ExecutionStatus.valueOf(status.toUpperCase())); }
            catch (Exception e) { executions = executionService.findByStatus(ExecutionStatus.PENDING); }
        } else executions = executionService.findByStatus(ExecutionStatus.PENDING);
        return ApiResponse.success(executions.stream().map(e -> toResponse(e, null)).collect(Collectors.toList()));
    }

    private ExecutionResponse toResponse(Execution exec, Optimization opt) {
        ExecutionResponse r = new ExecutionResponse();
        r.setExecutionId(exec.getExecutionId() != null ? exec.getExecutionId().getValue() : null);
        r.setRecommendationId(exec.getRecommendationId());
        r.setExecutor(exec.getExecutor());
        r.setExecutionType(exec.getExecutionType() != null ? exec.getExecutionType().name() : null);
        r.setStatus(exec.getStatus() != null ? exec.getStatus().name() : null);
        r.setImprovementScore(exec.getImprovementScore());
        r.setStartedAt(exec.getStartedAt());
        r.setFinishedAt(exec.getFinishedAt());
        r.setCreatedAt(exec.getCreatedAt());
        r.setVersion(exec.getVersion());
        if (opt != null) {
            ExecutionResponse.OptimizationDto dto = new ExecutionResponse.OptimizationDto();
            dto.setStatus(opt.getStatus());
            dto.setImprovementScore(opt.getImprovementScore());
            dto.setSummary(opt.getSummary());
            r.setOptimization(dto);
        }
        return r;
    }
}
