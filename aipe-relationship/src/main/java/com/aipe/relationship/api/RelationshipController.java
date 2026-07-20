package com.aipe.relationship.api;

import com.aipe.relationship.api.dto.CreateRelationshipRequest;
import com.aipe.relationship.api.dto.ImpactResponse;
import com.aipe.relationship.api.dto.RelationshipResponse;
import com.aipe.relationship.api.dto.TopologyResponse;
import com.aipe.relationship.application.RelationshipApplicationService;
import com.aipe.relationship.application.TopologyService;
import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipId;
import com.aipe.relationship.domain.TopologyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Relationship REST Controller
 *
 * <p>IM-006 / WP013 Blueprint §7.1
 * <p>Architecture Law-007：Controller Is A Protocol Translator（只做协议转换，无业务逻辑）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/relationships")
public class RelationshipController {

    private static final Logger log = LoggerFactory.getLogger(RelationshipController.class);

    @Autowired
    private RelationshipApplicationService applicationService;

    @Autowired
    private RelationshipDtoMapper dtoMapper;

    /**
     * POST /api/v1/relationships  新建 Relationship
     */
    @PostMapping
    public ApiResponse<RelationshipResponse> create(@Valid @RequestBody CreateRelationshipRequest request) {
        Relationship created = applicationService.createRelationship(
                com.aipe.relationship.domain.RelationshipType.parse(request.getRelationshipType()),
                request.getSourceResourceId(),
                request.getTargetResourceId(),
                com.aipe.relationship.domain.RelationshipDirection.parse(request.getDirection()),
                request.getConfidence(),
                request.getDiscoveredBy(),
                request.getLabels()
        );
        return ApiResponse.success(dtoMapper.toResponse(created));
    }

    /**
     * GET /api/v1/relationships/{id}  查询
     */
    @GetMapping("/{id}")
    public ApiResponse<RelationshipResponse> findById(@PathVariable("id") String id) {
        Relationship relationship = applicationService.findById(RelationshipId.of(id)).orElse(null);
        if (relationship == null) {
            return ApiResponse.error(404, "Relationship not found: " + id);
        }
        return ApiResponse.success(dtoMapper.toResponse(relationship));
    }

    /**
     * DELETE /api/v1/relationships/{id}  删除（归档）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") String id) {
        boolean deleted = applicationService.removeRelationship(RelationshipId.of(id));
        return ApiResponse.success(deleted);
    }

    /**
     * GET /api/v1/relationships?resource_id=xxx&type=xxx  查询 Resource 的关系
     */
    @GetMapping
    public ApiResponse<List<RelationshipResponse>> list(
            @RequestParam(value = "resource_id", required = false) String resourceId,
            @RequestParam(value = "type", required = false) String type) {

        List<Relationship> relationships;
        if (resourceId != null) {
            relationships = applicationService.findRelationships(resourceId);
        } else if (type != null) {
            relationships = applicationService.findByType(com.aipe.relationship.domain.RelationshipType.parse(type));
        } else {
            relationships = applicationService.findAllActive();
        }
        return ApiResponse.success(dtoMapper.toResponseList(relationships));
    }

    /**
     * GET /api/v1/relationships/{id}/neighbors  查询邻居
     */
    @GetMapping("/{id}/neighbors")
    public ApiResponse<List<RelationshipResponse>> neighbors(@PathVariable("id") String id) {
        List<Relationship> relationships = applicationService.findNeighbors(id);
        return ApiResponse.success(dtoMapper.toResponseList(relationships));
    }

    /**
     * GET /api/v1/relationships/upstream?resource_id=xxx  上游依赖
     */
    @GetMapping("/upstream")
    public ApiResponse<List<RelationshipResponse>> upstream(@RequestParam("resource_id") String resourceId) {
        List<Relationship> relationships = applicationService.findUpstream(resourceId);
        return ApiResponse.success(dtoMapper.toResponseList(relationships));
    }

    /**
     * GET /api/v1/relationships/downstream?resource_id=xxx  下游影响
     */
    @GetMapping("/downstream")
    public ApiResponse<List<RelationshipResponse>> downstream(@RequestParam("resource_id") String resourceId) {
        List<Relationship> relationships = applicationService.findDownstream(resourceId);
        return ApiResponse.success(dtoMapper.toResponseList(relationships));
    }
}
