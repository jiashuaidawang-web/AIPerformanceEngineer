package com.aipe.resource.api.controller;

import com.aipe.resource.api.dto.ApiResponse;
import com.aipe.resource.api.dto.ResourceRequest;
import com.aipe.resource.api.dto.ResourceResponse;
import com.aipe.resource.api.mapper.ResourceDtoMapper;
import com.aipe.resource.application.ResourceLifecycleManager;
import com.aipe.resource.application.ResourceQueryService;
import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源管理 REST Controller
 *
 * <p>对齐 IM-006 REST API Mapping
 * <p>Architecture Law-007：Controller Is A Protocol Translator（只做协议转换，无业务逻辑）
 *
 * <p>API 列表：
 * <ul>
 *   <li>POST   /api/v1/resources              创建资源</li>
 *   <li>GET    /api/v1/resources/{id}         查询资源</li>
 *   <li>PUT    /api/v1/resources/{id}         更新资源</li>
 *   <li>DELETE /api/v1/resources/{id}         删除资源</li>
 *   <li>GET    /api/v1/resources              列表查询</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceLifecycleManager lifecycleManager;

    @Autowired
    private ResourceQueryService queryService;

    /**
     * 创建资源
     * POST /api/v1/resources
     */
    @PostMapping
    public ApiResponse<ResourceResponse> create(@Valid @RequestBody ResourceRequest request) {
        Resource resource = ResourceDtoMapper.toDomain(request);
        Resource created = lifecycleManager.createResource(resource);
        return ApiResponse.success(ResourceDtoMapper.toResponse(created));
    }

    /**
     * 根据 ID 查询资源
     * GET /api/v1/resources/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<ResourceResponse> findById(@PathVariable("id") String id) {
        Resource resource = queryService.findById(id);
        if (resource == null) {
            return ApiResponse.error(404, "Resource not found: " + id);
        }
        return ApiResponse.success(ResourceDtoMapper.toResponse(resource));
    }

    /**
     * 更新资源
     * PUT /api/v1/resources/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<ResourceResponse> update(@PathVariable("id") String id,
                                                @RequestBody ResourceRequest request) {
        Resource existing = queryService.findById(id);
        if (existing == null) {
            return ApiResponse.error(404, "Resource not found: " + id);
        }
        Resource updated = lifecycleManager.updateResource(existing);
        return ApiResponse.success(ResourceDtoMapper.toResponse(updated));
    }

    /**
     * 删除资源
     * DELETE /api/v1/resources/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") String id) {
        boolean deleted = lifecycleManager.deleteResource(ResourceId.of(id));
        return ApiResponse.success(deleted);
    }

    /**
     * 查询资源列表（支持按业务系统/类型/状态过滤）
     * GET /api/v1/resources?business_system=xxx&type=xxx&status=xxx
     */
    @GetMapping
    public ApiResponse<List<ResourceResponse>> list(
            @RequestParam(value = "business_system", required = false) String businessSystem,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status) {

        List<Resource> resources;
        if (businessSystem != null) {
            resources = queryService.findByBusinessSystem(businessSystem);
        } else if (type != null) {
            resources = queryService.findByType(parseType(type));
        } else if (status != null) {
            resources = queryService.findByStatus(parseStatus(status));
        } else {
            resources = queryService.findAll();
        }

        List<ResourceResponse> responseList = resources.stream()
                .map(ResourceDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responseList);
    }

    /**
     * 更新资源状态
     * PATCH /api/v1/resources/{id}/status?status=xxx
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<ResourceResponse> updateStatus(@PathVariable("id") String id,
                                                      @RequestParam("status") String status) {
        ResourceStatus newStatus = parseStatus(status);
        Resource updated = lifecycleManager.updateResourceStatus(ResourceId.of(id), newStatus);
        return ApiResponse.success(ResourceDtoMapper.toResponse(updated));
    }

    // ==================== 辅助方法 ====================

    private com.aipe.resource.domain.ResourceType parseType(String type) {
        try {
            return com.aipe.resource.domain.ResourceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource type: " + type);
        }
    }

    private ResourceStatus parseStatus(String status) {
        try {
            return ResourceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource status: " + status);
        }
    }
}
