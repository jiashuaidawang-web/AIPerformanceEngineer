package com.aipe.relationship.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 新建 Relationship 请求 DTO
 *
 * <p>对齐 WP013 Blueprint §7.1
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class CreateRelationshipRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "relationshipType is required")
    private String relationshipType;

    @NotBlank(message = "sourceResourceId is required")
    private String sourceResourceId;

    @NotBlank(message = "targetResourceId is required")
    private String targetResourceId;

    private String direction = "SINGLE";

    private Double confidence = 100.0;

    private String discoveredBy = "MANUAL";

    private Map<String, String> labels;

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(String sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    public String getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(String targetResourceId) {
        this.targetResourceId = targetResourceId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getDiscoveredBy() {
        return discoveredBy;
    }

    public void setDiscoveredBy(String discoveredBy) {
        this.discoveredBy = discoveredBy;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
}
