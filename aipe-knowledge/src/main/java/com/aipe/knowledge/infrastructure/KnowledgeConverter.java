package com.aipe.knowledge.infrastructure;

import com.aipe.knowledge.domain.Knowledge;
import com.aipe.knowledge.domain.KnowledgeBuilder;
import com.aipe.knowledge.domain.KnowledgeId;
import com.aipe.knowledge.domain.KnowledgeType;
import com.aipe.knowledge.domain.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Knowledge 转换器（PO ↔ Domain + JSON 序列化）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class KnowledgeConverter {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeConverter.class);

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();
    static { MAPPER.findAndRegisterModules(); }

    @SuppressWarnings("unchecked")
    public KnowledgePO toPO(Knowledge knowledge) {
        if (knowledge == null) return null;
        KnowledgePO po = new KnowledgePO();
        po.setId(knowledge.getKnowledgeId() != null ? knowledge.getKnowledgeId().getValue() : null);
        po.setTitle(knowledge.getTitle());
        po.setDescription(knowledge.getDescription());
        po.setKnowledgeType(knowledge.getKnowledgeType() != null ? knowledge.getKnowledgeType().name() : null);
        po.setEvidenceId(knowledge.getEvidenceId());
        po.setVerificationId(knowledge.getVerificationId());
        po.setConfidence(knowledge.getConfidence());
        po.setApplicableConditions(toJson(knowledge.getApplicableConditions()));
        po.setRecommendation(toJsonRecommendation(knowledge.getRecommendation()));
        po.setSuccessRate(knowledge.getSuccessRate());
        po.setCreatedAt(knowledge.getCreatedAt());
        po.setUpdatedAt(LocalDateTime.now());
        po.setVersion(knowledge.getVersion());
        return po;
    }

    public Knowledge toDomain(KnowledgePO po) {
        if (po == null) return null;
        KnowledgeId id = po.getId() != null ? KnowledgeId.of(po.getId()) : null;
        return KnowledgeBuilder.reconstruct(
                id, po.getTitle(), po.getDescription(),
                KnowledgeType.parse(po.getKnowledgeType()),
                po.getEvidenceId(), po.getVerificationId(),
                po.getConfidence() != null ? po.getConfidence() : 50.0,
                parseMap(po.getApplicableConditions()),
                parseRecommendation(po.getRecommendation()),
                po.getSuccessRate() != null ? po.getSuccessRate() : 0.0,
                po.getCreatedAt(), po.getVersion() != null ? po.getVersion() : 1);
    }

    private String toJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) return "{}";
        try { return MAPPER.writeValueAsString(map); } catch (Exception e) { return "{}"; }
    }

    private String toJsonRecommendation(Recommendation rec) {
        if (rec == null) return "{}";
        try { return MAPPER.writeValueAsString(rec); } catch (Exception e) { return "{}"; }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseMap(String json) {
        if (json == null || json.trim().isEmpty() || "{}".equals(json.trim())) return new HashMap<>();
        try { return MAPPER.readValue(json, Map.class); } catch (Exception e) { return new HashMap<>(); }
    }

    private Recommendation parseRecommendation(String json) {
        if (json == null || json.trim().isEmpty() || "{}".equals(json.trim())) {
            return new Recommendation("暂无", "暂无", "MEDIUM");
        }
        try {
            Map<String, Object> map = MAPPER.readValue(json, Map.class);
            return new Recommendation(
                    (String) map.getOrDefault("action", "暂无"),
                    (String) map.getOrDefault("expectedEffect", "暂无"),
                    (String) map.getOrDefault("riskLevel", "MEDIUM"));
        } catch (Exception e) {
            return new Recommendation("暂无", "暂无", "MEDIUM");
        }
    }
}
