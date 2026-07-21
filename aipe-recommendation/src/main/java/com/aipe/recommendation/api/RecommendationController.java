package com.aipe.recommendation.api;

import com.aipe.recommendation.api.dto.RecommendationRequest;
import com.aipe.recommendation.api.dto.RecommendationResponse;
import com.aipe.recommendation.application.RecommendationEngine;
import com.aipe.recommendation.domain.Recommendation;
import com.aipe.recommendation.domain.RecommendationId;
import com.aipe.recommendation.domain.RecommendationStatus;
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

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recommendation Controller（Blueprint §7）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationEngine recommendationEngine;

    @PostMapping("/generate")
    public ApiResponse<RecommendationResponse> generate(@Valid @RequestBody RecommendationRequest request) {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                request.getKnowledgeId(), request.getTargetResourceId(),
                request.getTitle(), request.getDescription(),
                request.getConfidence(),
                request.getExpectedOutcome() != null ? request.getExpectedOutcome() : "预期系统性能提升");
        return ApiResponse.success(toResponse(rec));
    }

    @GetMapping("/{id}")
    public ApiResponse<RecommendationResponse> findById(@PathVariable("id") String id) {
        Recommendation rec = recommendationEngine.findByResource(id).stream().findFirst()
                .orElseGet(() -> recommendationEngine.findByStatus(RecommendationStatus.PENDING).stream()
                        .filter(r -> r.getRecommendationId().getValue().equals(id)).findFirst().orElse(null));
        if (rec == null) {
            // Try direct lookup via repository
            rec = recommendationEngine.findByStatus(RecommendationStatus.EXECUTED).stream()
                    .filter(r -> r.getRecommendationId().getValue().equals(id)).findFirst().orElse(null);
        }
        if (rec == null) return ApiResponse.error(404, "Recommendation not found: " + id);
        return ApiResponse.success(toResponse(rec));
    }

    @GetMapping
    public ApiResponse<List<RecommendationResponse>> list(
            @RequestParam(value = "resource_id", required = false) String resourceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority) {
        List<Recommendation> recs;
        if (resourceId != null) {
            recs = recommendationEngine.findByResource(resourceId);
        } else if (status != null) {
            try {
                recs = recommendationEngine.findByStatus(RecommendationStatus.valueOf(status.toUpperCase()));
            } catch (Exception e) {
                recs = recommendationEngine.findByStatus(RecommendationStatus.PENDING);
            }
        } else {
            recs = recommendationEngine.findByStatus(RecommendationStatus.PENDING);
        }
        return ApiResponse.success(recs.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Boolean> approve(@PathVariable("id") String id) {
        boolean result = recommendationEngine.approve(RecommendationId.of(id));
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Boolean> reject(@PathVariable("id") String id) {
        boolean result = recommendationEngine.reject(RecommendationId.of(id));
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/execute")
    public ApiResponse<Boolean> execute(@PathVariable("id") String id) {
        boolean result = recommendationEngine.markExecuted(RecommendationId.of(id));
        return ApiResponse.success(result);
    }

    private RecommendationResponse toResponse(Recommendation rec) {
        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendationId(rec.getRecommendationId() != null ? rec.getRecommendationId().getValue() : null);
        response.setKnowledgeId(rec.getKnowledgeId());
        response.setTargetResourceId(rec.getTargetResourceId());
        response.setTitle(rec.getTitle());
        response.setDescription(rec.getDescription());
        response.setPriority(rec.getPriority() != null ? rec.getPriority().name() : null);
        response.setConfidence(rec.getConfidence());
        response.setExpectedOutcome(rec.getExpectedOutcome());
        response.setExecutionPlan(rec.getExecutionPlan());
        response.setRollbackPlan(rec.getRollbackPlan());
        response.setStatus(rec.getStatus() != null ? rec.getStatus().name() : null);
        response.setCreatedAt(rec.getCreatedAt());
        response.setVersion(rec.getVersion());
        return response;
    }
}
