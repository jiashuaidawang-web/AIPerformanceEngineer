package com.aipe.relationship.api;

import com.aipe.relationship.api.dto.ImpactResponse;
import com.aipe.relationship.api.dto.TopologyResponse;
import com.aipe.relationship.application.TopologyService;
import com.aipe.relationship.domain.RelationshipType;
import com.aipe.relationship.domain.TopologyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Topology REST Controller
 *
 * <p>对齐 WP013 Blueprint §7.1 + M2-009 Topology Model
 * <p>Architecture Law-004：Topology 不存储，每次查询实时计算返回
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/topology")
public class TopologyController {

    private static final Logger log = LoggerFactory.getLogger(TopologyController.class);

    @Autowired
    private TopologyService topologyService;

    @Autowired
    private RelationshipDtoMapper dtoMapper;

    /**
     * GET /api/v1/topology/current?resource_id=xxx&amp;type=xxx  当前拓扑
     *
     * <p>实时从 Relationship 投影计算
     */
    @GetMapping("/current")
    public ApiResponse<TopologyResponse> current(
            @RequestParam("resource_id") String resourceId,
            @RequestParam(value = "type", required = false) String type) {

        TopologyView view;
        if (type != null && !type.trim().isEmpty()) {
            view = topologyService.buildCurrent(resourceId,
                    com.aipe.relationship.domain.RelationshipType.parse(type));
        } else {
            view = topologyService.buildCurrent(resourceId);
        }

        TopologyResponse response = new TopologyResponse();
        response.setTopologyId(view.getTopologyId());
        response.setBuiltAt(view.getBuiltAt());
        response.setRootResourceId(view.getRootResourceId());
        response.setNodeCount(view.getNodeCount());
        response.setEdgeCount(view.getEdgeCount());
        response.setNodes(dtoMapper.toNodeDtos(view.getNodes()));
        response.setEdges(view.getEdges().stream()
                .map(e -> {
                    TopologyResponse.TopologyEdgeDto dto = new TopologyResponse.TopologyEdgeDto();
                    dto.setRelationshipId(e.getRelationshipId());
                    dto.setSourceResourceId(e.getSourceResourceId());
                    dto.setTargetResourceId(e.getTargetResourceId());
                    dto.setRelationshipType(e.getRelationshipType() != null ? e.getRelationshipType().name() : null);
                    dto.setDirection(e.getDirection() != null ? e.getDirection().name() : null);
                    dto.setConfidence(e.getConfidence());
                    return dto;
                })
                .collect(Collectors.toList()));

        return ApiResponse.success(response);
    }

    /**
     * GET /api/v1/topology/neighbors?resource_id=xxx&amp;degree=1
     */
    @GetMapping("/neighbors")
    public ApiResponse<List<TopologyResponse.TopologyNodeDto>> neighbors(
            @RequestParam("resource_id") String resourceId,
            @RequestParam(value = "degree", defaultValue = "1") Integer degree) {
        List<TopologyResponse.TopologyNodeDto> nodes = dtoMapper.toNodeDtos(
                topologyService.queryNeighbors(resourceId, degree));
        return ApiResponse.success(nodes);
    }

    /**
     * GET /api/v1/topology/dependencies?resource_id=xxx&amp;direction=downstream
     */
    @GetMapping("/dependencies")
    public ApiResponse<List<TopologyResponse.TopologyNodeDto>> dependencies(
            @RequestParam("resource_id") String resourceId,
            @RequestParam(value = "direction", defaultValue = "downstream") String direction) {
        List<TopologyResponse.TopologyNodeDto> nodes = dtoMapper.toNodeDtos(
                topologyService.queryDependencies(resourceId, direction));
        return ApiResponse.success(nodes);
    }

    /**
     * GET /api/v1/topology/impact?resource_id=xxx
     */
    @GetMapping("/impact")
    public ApiResponse<ImpactResponse> impact(@RequestParam("resource_id") String resourceId) {
        ImpactResponse response = dtoMapper.toImpactResponse(
                topologyService.queryImpact(resourceId), resourceId);
        return ApiResponse.success(response);
    }

    /**
     * GET /api/v1/topology/path?from=xxx&amp;to=xxx
     */
    @GetMapping("/path")
    public ApiResponse<List<TopologyResponse.TopologyNodeDto>> path(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        List<TopologyResponse.TopologyNodeDto> pathNodes = dtoMapper.toNodeDtos(
                topologyService.shortestPath(from, to));
        return ApiResponse.success(pathNodes);
    }
}
