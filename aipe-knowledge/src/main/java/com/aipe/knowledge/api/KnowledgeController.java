package com.aipe.knowledge.api;

import com.aipe.knowledge.api.dto.KnowledgeRequest;
import com.aipe.knowledge.api.dto.KnowledgeResponse;
import com.aipe.knowledge.application.KnowledgeEngine;
import com.aipe.knowledge.domain.Knowledge;
import com.aipe.knowledge.domain.KnowledgeId;
import com.aipe.knowledge.domain.KnowledgeType;
import com.aipe.knowledge.domain.Recommendation;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Knowledge Controller（Blueprint §7）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeController.class);

    @Autowired
    private KnowledgeEngine knowledgeEngine;

    @PostMapping
    public ApiResponse<KnowledgeResponse> buildKnowledge(@Valid @RequestBody KnowledgeRequest request) {
        Knowledge knowledge = knowledgeEngine.buildKnowledge(
                request.getTitle(), request.getDescription(),
                KnowledgeType.parse(request.getKnowledgeType()),
                request.getEvidenceId(), request.getVerificationId(),
                request.getConfidence(), request.getSuccessRate() != null ? request.getSuccessRate() : 0.0,
                request.getResourceType(), request.getMetricName(),
                request.getRecommendationAction(), request.getExpectedEffect());
        return ApiResponse.success(toResponse(knowledge));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeResponse> findById(@PathVariable("id") String id) {
        Knowledge knowledge = knowledgeEngine.findLatest(KnowledgeId.of(id)).orElse(null);
        if (knowledge == null) return ApiResponse.error(404, "Knowledge not found: " + id);
        return ApiResponse.success(toResponse(knowledge));
    }

    @GetMapping
    public ApiResponse<List<KnowledgeResponse>> list(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "min_confidence", required = false) Double minConfidence,
            @RequestParam(value = "evidence_id", required = false) String evidenceId) {
        List<Knowledge> knowledgeList;
        if (type != null) {
            knowledgeList = knowledgeEngine.findByType(KnowledgeType.parse(type));
        } else if (minConfidence != null) {
            knowledgeList = knowledgeEngine.findByMinConfidence(minConfidence);
        } else {
            knowledgeList = knowledgeEngine.listVersions(KnowledgeId.of(""));
        }
        List<KnowledgeResponse> responses = knowledgeList.stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<KnowledgeResponse>> versions(@PathVariable("id") String id) {
        List<Knowledge> allVersions = knowledgeEngine.listVersions(KnowledgeId.of(id));
        if (allVersions.isEmpty()) return ApiResponse.error(404, "Knowledge not found: " + id);
        List<KnowledgeResponse> responses = allVersions.stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    @PostMapping("/{id}/upgrade")
    public ApiResponse<KnowledgeResponse> upgrade(@PathVariable("id") String id, @RequestBody Map<String, String> changeSet) {
        Knowledge upgraded = knowledgeEngine.verifyAndUpgrade(KnowledgeId.of(id), changeSet, null, -1);
        return ApiResponse.success(toResponse(upgraded));
    }

    @PostMapping("/{id}/recommend")
    public ApiResponse<Map<String, String>> recommend(@PathVariable("id") String id,
                                                      @RequestParam("resource_type") String resourceType,
                                                      @RequestParam(value = "metric_name", required = false) String metricName) {
        Recommendation rec = knowledgeEngine.recommendForResource(KnowledgeId.of(id), resourceType, metricName);
        if (rec == null) return ApiResponse.error(400, "Knowledge not applicable to resourceType=" + resourceType);
        Map<String, String> result = new HashMap<>();
        result.put("action", rec.getAction());
        result.put("expectedEffect", rec.getExpectedEffect());
        result.put("riskLevel", rec.getRiskLevel());
        return ApiResponse.success(result);
    }

    private KnowledgeResponse toResponse(Knowledge knowledge) {
        KnowledgeResponse response = new KnowledgeResponse();
        response.setKnowledgeId(knowledge.getKnowledgeId() != null ? knowledge.getKnowledgeId().getValue() : null);
        response.setTitle(knowledge.getTitle());
        response.setDescription(knowledge.getDescription());
        response.setKnowledgeType(knowledge.getKnowledgeType() != null ? knowledge.getKnowledgeType().name() : null);
        response.setEvidenceId(knowledge.getEvidenceId());
        response.setVerificationId(knowledge.getVerificationId());
        response.setConfidence(knowledge.getConfidence());
        response.setApplicableConditions(knowledge.getApplicableConditions());
        response.setSuccessRate(knowledge.getSuccessRate());
        response.setCreatedAt(knowledge.getCreatedAt());
        response.setVersion(knowledge.getVersion());

        Recommendation rec = knowledge.getRecommendation();
        if (rec != null) {
            KnowledgeResponse.RecommendationDto dto = new KnowledgeResponse.RecommendationDto();
            dto.setAction(rec.getAction());
            dto.setExpectedEffect(rec.getExpectedEffect());
            dto.setRiskLevel(rec.getRiskLevel());
            response.setRecommendation(dto);
        }
        return response;
    }
}
