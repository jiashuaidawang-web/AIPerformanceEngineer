package com.aipe.evidence.infrastructure;

import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceBuilder;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.EvidenceStatus;
import com.aipe.evidence.domain.EvidenceType;
import com.aipe.evidence.domain.ReasoningStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Evidence 转换器 + 持久化对象转换器
 *
 * <p>Domain ↔ Persistence；JSON 序列化/反序列化
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class EvidenceConverter {

    private static final Logger log = LoggerFactory.getLogger(EvidenceConverter.class);

    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
    }

    private static final com.fasterxml.jackson.core.type.TypeReference<List<String>> STRING_LIST =
            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {};

    private static final com.fasterxml.jackson.core.type.TypeReference<List<ReasoningStep>> STEP_LIST =
            new com.fasterxml.jackson.core.type.TypeReference<List<ReasoningStep>>() {};

    /**
     * Domain → PO（写入 MySQL）
     */
    public EvidencePO toPO(Evidence evidence) {
        if (evidence == null) return null;
        EvidencePO po = new EvidencePO();
        po.setId(evidence.getEvidenceId() != null ? evidence.getEvidenceId().getValue() : null);
        po.setEvidenceType(evidence.getEvidenceType() != null ? evidence.getEvidenceType().name() : null);
        po.setTitle(evidence.getTitle());
        po.setDescription(evidence.getDescription());
        po.setRootResourceId(evidence.getRootResourceId());
        po.setObservationIds(toJson(evidence.getObservationIds()));
        po.setRelationshipIds(toJson(evidence.getRelationshipIds()));
        po.setTimelineId(evidence.getTimelineId());
        po.setConfidence(evidence.getConfidence());
        po.setReasoningSteps(toJsonSteps(evidence.getReasoningSteps()));
        po.setStatus(evidence.getStatus() != null ? evidence.getStatus().name() : null);
        po.setCreatedAt(evidence.getCreatedAt());
        po.setUpdatedAt(evidence.getUpdatedAt());
        po.setVersion(evidence.getVersion());
        return po;
    }

    /**
     * PO → Domain（从 MySQL 读取）
     */
    public Evidence toDomain(EvidencePO po) {
        if (po == null) return null;
        EvidenceId id = po.getId() != null ? EvidenceId.of(po.getId()) : null;
        List<String> obsIds = parseStringList(po.getObservationIds());
        List<String> relIds = parseStringList(po.getRelationshipIds());
        List<ReasoningStep> reasoningSteps = parseStepList(po.getReasoningSteps());

        return EvidenceBuilder.reconstruct(
                id,
                EvidenceType.parse(po.getEvidenceType()),
                po.getTitle(),
                po.getDescription(),
                po.getRootResourceId(),
                obsIds, relIds, po.getTimelineId(),
                po.getConfidence() != null ? po.getConfidence() : 50.0,
                reasoningSteps,
                EvidenceStatus.parse(po.getStatus()),
                po.getCreatedAt(), po.getUpdatedAt(),
                po.getVersion() != null ? po.getVersion() : 1);
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        try { return MAPPER.writeValueAsString(list); }
        catch (Exception e) { return "[]"; }
    }

    private String toJsonSteps(List<ReasoningStep> steps) {
        if (steps == null || steps.isEmpty()) return "[]";
        try { return MAPPER.writeValueAsString(steps); }
        catch (Exception e) { return "[]"; }
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) return new ArrayList<>();
        try { List<String> r = MAPPER.readValue(json, STRING_LIST); return r != null ? r : new ArrayList<>(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private List<ReasoningStep> parseStepList(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) return new ArrayList<>();
        try { List<ReasoningStep> r = MAPPER.readValue(json, STEP_LIST); return r != null ? r : new ArrayList<>(); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
