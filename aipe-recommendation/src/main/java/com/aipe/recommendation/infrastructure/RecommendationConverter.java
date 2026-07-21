package com.aipe.recommendation.infrastructure;

import com.aipe.recommendation.domain.Priority;
import com.aipe.recommendation.domain.Recommendation;
import com.aipe.recommendation.domain.RecommendationBuilder;
import com.aipe.recommendation.domain.RecommendationId;
import com.aipe.recommendation.domain.RecommendationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recommendation 转换器（PO ↔ Domain + JSON 序列化）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class RecommendationConverter {

    private static final Logger log = LoggerFactory.getLogger(RecommendationConverter.class);

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();
    static { MAPPER.findAndRegisterModules(); }

    public RecommendationPO toPO(Recommendation recommendation) {
        if (recommendation == null) return null;
        RecommendationPO po = new RecommendationPO();
        po.setId(recommendation.getRecommendationId() != null ? recommendation.getRecommendationId().getValue() : null);
        po.setKnowledgeId(recommendation.getKnowledgeId());
        po.setTargetResourceId(recommendation.getTargetResourceId());
        po.setTitle(recommendation.getTitle());
        po.setDescription(recommendation.getDescription());
        po.setPriority(recommendation.getPriority() != null ? recommendation.getPriority().name() : null);
        po.setConfidence(recommendation.getConfidence());
        po.setExpectedOutcome(recommendation.getExpectedOutcome());
        po.setExecutionPlan(toJson(recommendation.getExecutionPlan()));
        po.setRollbackPlan(toJson(recommendation.getRollbackPlan()));
        po.setStatus(recommendation.getStatus() != null ? recommendation.getStatus().name() : null);
        po.setCreatedAt(recommendation.getCreatedAt());
        po.setUpdatedAt(LocalDateTime.now());
        po.setVersion(recommendation.getVersion());
        return po;
    }

    public Recommendation toDomain(RecommendationPO po) {
        if (po == null) return null;
        RecommendationId id = po.getId() != null ? RecommendationId.of(po.getId()) : null;
        return RecommendationBuilder.reconstruct(
                id, po.getKnowledgeId(), po.getTargetResourceId(),
                po.getTitle(), po.getDescription(),
                Priority.parse(po.getPriority()),
                po.getConfidence() != null ? po.getConfidence() : 50.0,
                po.getExpectedOutcome(),
                parseList(po.getExecutionPlan()),
                parseList(po.getRollbackPlan()),
                RecommendationStatus.parse(po.getStatus()),
                po.getCreatedAt(), po.getUpdatedAt(),
                po.getVersion() != null ? po.getVersion() : 1);
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        try { return MAPPER.writeValueAsString(list); } catch (Exception e) { return "[]"; }
    }

    private List<String> parseList(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) return new ArrayList<>();
        try { return MAPPER.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
