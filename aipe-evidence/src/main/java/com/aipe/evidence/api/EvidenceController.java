package com.aipe.evidence.api;

import com.aipe.evidence.api.dto.EvidenceRequest;
import com.aipe.evidence.api.dto.EvidenceResponse;
import com.aipe.evidence.api.dto.VerifyEvidenceRequest;
import com.aipe.evidence.application.EvidenceEngine;
import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.ReasoningStep;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Evidence REST Controller
 *
 * <p>IM-006 / WP014 Blueprint §7
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/evidences")
public class EvidenceController {

    private static final Logger log = LoggerFactory.getLogger(EvidenceController.class);

    @Autowired
    private EvidenceEngine evidenceEngine;

    /**
     * POST /api/v1/evidences/generate  触发 Evidence 生成
     */
    @PostMapping("/generate")
    public ApiResponse<EvidenceResponse> generate(@Valid @RequestBody EvidenceRequest request) {
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                request.getResourceId(), request.getMetricName(),
                request.getStartTime(), request.getEndTime());
        if (evidence == null) {
            return ApiResponse.error(404, "No anomaly detected for resource=" + request.getResourceId()
                    + ", metric=" + request.getMetricName());
        }
        return ApiResponse.success(toResponse(evidence, true));
    }

    /**
     * POST /api/v1/evidences/generate-all  扫描所有指标生成 Evidence
     */
    @PostMapping("/generate-all")
    public ApiResponse<List<EvidenceResponse>> generateAll(@Valid @RequestBody EvidenceRequest request) {
        List<Evidence> evidences = evidenceEngine.generateAllAnomalyEvidences(
                request.getResourceId(), request.getStartTime(), request.getEndTime());
        List<EvidenceResponse> responses = new ArrayList<>();
        for (Evidence e : evidences) {
            responses.add(toResponse(e, false));
        }
        return ApiResponse.success(responses);
    }

    /**
     * GET /api/v1/evidences/{id}  查询 Evidence
     */
    @GetMapping("/{id}")
    public ApiResponse<EvidenceResponse> findById(@PathVariable("id") String id) {
        Evidence evidence = evidenceEngine.findById(EvidenceId.of(id)).orElse(null);
        if (evidence == null) {
            return ApiResponse.error(404, "Evidence not found: " + id);
        }
        return ApiResponse.success(toResponse(evidence, false));
    }

    /**
     * GET /api/v1/evidences?resource_id=xxx&status=VERIFIED  按 Resource / 状态查询
     */
    @GetMapping
    public ApiResponse<List<EvidenceResponse>> list(
            @RequestParam(value = "resource_id", required = false) String resourceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "min_confidence", required = false) Double minConfidence) {

        List<Evidence> evidences;
        if (resourceId != null) {
            evidences = evidenceEngine.findByRootResource(resourceId);
        } else if (minConfidence != null) {
            evidences = evidenceEngine.findHighConfidence(minConfidence);
        } else {
            evidences = evidenceEngine.findByRootResource(resourceId != null ? resourceId : "");
        }
        List<EvidenceResponse> responses = evidences.stream()
                .map(e -> toResponse(e, false))
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    /**
     * POST /api/v1/evidences/{id}/verify  验证 Evidence
     */
    @PostMapping("/{id}/verify")
    public ApiResponse<Boolean> verify(@PathVariable("id") String id,
                                       @RequestBody VerifyEvidenceRequest request) {
        boolean updated = evidenceEngine.verifyEvidence(EvidenceId.of(id), request.isApproved());
        return ApiResponse.success(updated);
    }

    /**
     * GET /api/v1/evidences/{id}/explain  解释 Evidence
     */
    @GetMapping("/{id}/explain")
    public ApiResponse<String> explain(@PathVariable("id") String id) {
        Evidence evidence = evidenceEngine.findById(EvidenceId.of(id)).orElse(null);
        if (evidence == null) {
            return ApiResponse.error(404, "Evidence not found: " + id);
        }
        return ApiResponse.success(evidenceEngine.explain(evidence));
    }

    private EvidenceResponse toResponse(Evidence evidence, boolean withExplanation) {
        EvidenceResponse response = new EvidenceResponse();
        response.setEvidenceId(evidence.getEvidenceId() != null ? evidence.getEvidenceId().getValue() : null);
        response.setEvidenceType(evidence.getEvidenceType() != null ? evidence.getEvidenceType().name() : null);
        response.setTitle(evidence.getTitle());
        response.setDescription(evidence.getDescription());
        response.setRootResourceId(evidence.getRootResourceId());
        response.setObservationIds(evidence.getObservationIds());
        response.setRelationshipIds(evidence.getRelationshipIds());
        response.setTimelineId(evidence.getTimelineId());
        response.setConfidence(evidence.getConfidence());
        response.setStatus(evidence.getStatus() != null ? evidence.getStatus().name() : null);
        response.setCreatedAt(evidence.getCreatedAt());
        response.setVersion(evidence.getVersion());

        if (evidence.getReasoningSteps() != null) {
            List<EvidenceResponse.ReasoningStepDto> stepDtos = new ArrayList<>();
            for (ReasoningStep s : evidence.getReasoningSteps()) {
                EvidenceResponse.ReasoningStepDto dto = new EvidenceResponse.ReasoningStepDto();
                dto.setStep(s.getStep());
                dto.setAction(s.getAction());
                dto.setResult(s.getResult());
                dto.setConfidence(s.getConfidence());
                stepDtos.add(dto);
            }
            response.setReasoningSteps(stepDtos);
        }

        if (withExplanation) {
            response.setExplanation(evidence.explain());
        }
        return response;
    }
}
