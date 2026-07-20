package com.aipe.relationship.infrastructure;

import com.aipe.relationship.domain.RelationshipDirection;
import com.aipe.relationship.domain.RelationshipFactory;
import com.aipe.relationship.domain.RelationshipId;
import com.aipe.relationship.domain.RelationshipStatus;
import com.aipe.relationship.domain.RelationshipType;
import com.aipe.relationship.domain.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Relationship 转换器
 *
 * <p>Domain ↔ Persistence 对象转换（Gateway Law-001：Repository 返回 Domain）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class RelationshipConverter {

    private static final Logger log = LoggerFactory.getLogger(RelationshipConverter.class);

    private static final com.fasterxml.jackson.core.type.TypeReference<Map<String, String>> MAP_TYPE =
            new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {
            };

    private static final com.fasterxml.jackson.databind.ObjectMapper OBJECT_MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();

    /**
     * Domain → PO（写入 MySQL 前调用）
     */
    public RelationshipPO toPO(Relationship relationship) {
        if (relationship == null) {
            return null;
        }
        RelationshipPO po = new RelationshipPO();
        po.setId(relationship.getRelationshipId() != null ? relationship.getRelationshipId().getValue() : null);
        po.setRelationshipType(relationship.getRelationshipType() != null ? relationship.getRelationshipType().name() : null);
        po.setSourceResourceId(relationship.getSourceResourceId());
        po.setTargetResourceId(relationship.getTargetResourceId());
        po.setDirection(relationship.getDirection() != null ? relationship.getDirection().name() : null);
        po.setConfidence(relationship.getConfidence());
        po.setDiscoveredBy(relationship.getDiscoveredBy());
        po.setStatus(relationship.getStatus() != null ? relationship.getStatus().name() : null);
        po.setLabels(mapToJson(relationship.getLabels()));
        po.setDiscoveredAt(relationship.getDiscoveredAt());
        po.setUpdatedAt(relationship.getUpdatedAt());
        return po;
    }

    /**
     * PO → Domain（从 MySQL 读取后调用）
     */
    public Relationship toDomain(RelationshipPO po) {
        if (po == null) {
            return null;
        }
        RelationshipId id = po.getId() != null ? RelationshipId.of(po.getId()) : null;
        Map<String, String> labels = parseLabels(po.getLabels());

        return RelationshipFactory.reconstruct(
                id,
                RelationshipType.parse(po.getRelationshipType()),
                po.getSourceResourceId(),
                po.getTargetResourceId(),
                RelationshipDirection.parse(po.getDirection()),
                po.getConfidence() != null ? po.getConfidence() : 100.0,
                po.getDiscoveredBy(),
                RelationshipStatus.parse(po.getStatus()),
                labels,
                po.getDiscoveredAt(),
                po.getUpdatedAt()
        );
    }

    private String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("Failed to serialize labels to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    public Map<String, String> parseLabels(String labels) {
        if (labels == null || labels.trim().isEmpty() || "{}".equals(labels.trim())) {
            return new HashMap<>();
        }
        try {
            Map<String, String> result = OBJECT_MAPPER.readValue(labels, MAP_TYPE);
            return result != null ? result : new HashMap<>();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("Failed to parse labels JSON: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
