package com.aipe.relationship.api;

import com.aipe.relationship.api.dto.CreateRelationshipRequest;
import com.aipe.relationship.api.dto.RelationshipResponse;
import com.aipe.relationship.api.dto.ImpactResponse;
import com.aipe.relationship.api.dto.TopologyResponse;
import com.aipe.relationship.application.RelationshipApplicationService;
import com.aipe.relationship.application.TopologyService;
import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipDirection;
import com.aipe.relationship.domain.RelationshipId;
import com.aipe.relationship.domain.RelationshipType;
import com.aipe.relationship.domain.ResourceNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO ↔ Domain 映射器
 *
 * <p>Orchestration Law-004：Mapper Is A Pure Transformer（只做数据转换，无业务逻辑）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public final class RelationshipDtoMapper {

    private RelationshipDtoMapper() {
        // 工具类，禁止实例化
    }

    /**
     * CreateRelationshipRequest + 参数 → Relationship（调用工厂）
     */
    public static Relationship toDomain(CreateRelationshipRequest request) {
        if (request == null) {
            return null;
        }
        return com.aipe.relationship.domain.RelationshipFactory.create(
                RelationshipType.parse(request.getRelationshipType()),
                request.getSourceResourceId(),
                request.getTargetResourceId(),
                RelationshipDirection.parse(request.getDirection()),
                request.getConfidence() != null ? request.getConfidence() : 100.0,
                request.getDiscoveredBy(),
                request.getLabels()
        );
    }

    /**
     * Relationship → RelationshipResponse
     */
    public static RelationshipResponse toResponse(Relationship relationship) {
        if (relationship == null) {
            return null;
        }
        RelationshipResponse response = new RelationshipResponse();
        response.setRelationshipId(relationship.getRelationshipId() != null ? relationship.getRelationshipId().getValue() : null);
        response.setRelationshipType(relationship.getRelationshipType() != null ? relationship.getRelationshipType().name() : null);
        response.setSourceResourceId(relationship.getSourceResourceId());
        response.setTargetResourceId(relationship.getTargetResourceId());
        response.setDirection(relationship.getDirection() != null ? relationship.getDirection().name() : null);
        response.setConfidence(relationship.getConfidence());
        response.setDiscoveredBy(relationship.getDiscoveredBy());
        response.setStatus(relationship.getStatus() != null ? relationship.getStatus().name() : null);
        response.setLabels(relationship.getLabels());
        response.setDiscoveredAt(relationship.getDiscoveredAt());
        response.setUpdatedAt(relationship.getUpdatedAt());
        return response;
    }

    /**
     * Relationship 列表 → RelationshipResponse 列表
     */
    public static List<RelationshipResponse> toResponseList(List<Relationship> relationships) {
        return relationships.stream()
                .map(RelationshipDtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * ResourceNode 列表 → ImpactResponse
     */
    public static ImpactResponse toImpactResponse(List<ResourceNode> impactNodes, String rootResourceId) {
        ImpactResponse response = new ImpactResponse();
        response.setRootResourceId(rootResourceId);
        if (impactNodes != null) {
            response.setImpactCount(impactNodes.size());
            response.setImpactedResourceIds(impactNodes.stream()
                    .map(ResourceNode::getResourceId)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    /**
     * ResourceNode 列表 → TopologyResponse.Topo* DTO 列表
     */
    public static List<TopologyResponse.TopologyNodeDto> toNodeDtos(List<ResourceNode> nodes) {
        return nodes.stream()
                .map(n -> {
                    TopologyResponse.TopologyNodeDto dto = new TopologyResponse.TopologyNodeDto();
                    dto.setResourceId(n.getResourceId());
                    dto.setResourceName(n.getResourceName());
                    dto.setResourceType(n.getResourceType());
                    dto.setRoot(n.isRoot());
                    dto.setDegree(n.getDegree());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
