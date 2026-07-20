package com.aipe.resource.api.mapper;

import com.aipe.resource.api.dto.ResourceReport;
import com.aipe.resource.api.dto.ResourceRequest;
import com.aipe.resource.api.dto.ResourceResponse;
import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceFactory;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceName;
import com.aipe.resource.domain.ResourceCategory;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO ↔ Domain 映射器
 *
 * <p>Orchestration Law-004：Mapper Is A Pure Transformer
 * <p>只做数据转换，禁止业务逻辑
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceDtoMapper {

    private ResourceDtoMapper() {
        // 工具类，禁止实例化
    }

    /**
     * ResourceRequest → Resource（创建场景）
     */
    public static Resource toDomain(ResourceRequest request) {
        if (request == null) {
            return null;
        }
        ResourceName name = ResourceName.of(request.getResourceName());
        ResourceType type = parseType(request.getResourceType());
        ResourceCategory category = parseCategory(request.getResourceCategory());
        ResourceId parentId = request.getParentResourceId() != null ? ResourceId.of(request.getParentResourceId()) : null;

        Map<String, String> labels = request.getLabels() != null ? new HashMap<>(request.getLabels()) : new HashMap<>();
        Map<String, String> attributes = request.getAttributes() != null ? new HashMap<>(request.getAttributes()) : new HashMap<>();

        return ResourceFactory.create(
                name, type, category, request.getBusinessSystem(),
                parentId, request.getCluster(), request.getNamespace(), request.getEnvironment(),
                labels, attributes
        );
    }

    /**
     * ResourceReport → Resource（Connector 上报场景，指定 ID）
     */
    public static Resource toDomainFromReport(ResourceReport report) {
        if (report == null) {
            return null;
        }
        ResourceName name = ResourceName.of(report.getResourceName() != null ? report.getResourceName() : report.getResourceId());
        ResourceType type = parseType(report.getResourceType());
        ResourceCategory category = inferCategory(type);
        ResourceId id = ResourceId.of(report.getResourceId());

        Map<String, String> attributes = new HashMap<>();
        if (report.getHost() != null) {
            attributes.put("host", report.getHost());
        }
        if (report.getPort() != null) {
            attributes.put("port", String.valueOf(report.getPort()));
        }
        if (report.getAttributes() != null) {
            attributes.putAll(report.getAttributes());
        }

        Map<String, String> labels = report.getLabels() != null ? new HashMap<>(report.getLabels()) : new HashMap<>();

        return ResourceFactory.reconstruct(
                id, name, type, category, report.getBusinessSystem(),
                null, null, null, null,
                ResourceStatus.RUNNING, 1, labels, attributes,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
    }

    /**
     * Resource → ResourceResponse
     */
    public static ResourceResponse toResponse(Resource resource) {
        if (resource == null) {
            return null;
        }
        ResourceResponse response = new ResourceResponse();
        response.setResourceId(resource.getId() != null ? resource.getId().getValue() : null);
        response.setResourceName(resource.getResourceName() != null ? resource.getResourceName().getValue() : null);
        response.setResourceType(resource.getResourceType() != null ? resource.getResourceType().name() : null);
        response.setResourceCategory(resource.getResourceCategory() != null ? resource.getResourceCategory().name() : null);
        response.setBusinessSystem(resource.getBusinessSystem());
        response.setParentResourceId(resource.getParentResourceId() != null ? resource.getParentResourceId().getValue() : null);
        response.setCluster(resource.getCluster());
        response.setNamespace(resource.getNamespace());
        response.setEnvironment(resource.getEnvironment());
        response.setStatus(resource.getStatus() != null ? resource.getStatus().name() : null);
        response.setVersion(resource.getVersion());
        response.setLabels(resource.getLabels());
        response.setAttributes(resource.getAttributes());
        response.setCreatedTime(resource.getCreatedTime());
        response.setUpdatedTime(resource.getUpdatedTime());
        return response;
    }

    // ==================== 枚举解析 ====================

    private static ResourceType parseType(String type) {
        if (type == null) {
            return ResourceType.UNKNOWN;
        }
        try {
            return ResourceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResourceType.UNKNOWN;
        }
    }

    private static ResourceCategory parseCategory(String category) {
        if (category == null) {
            return ResourceCategory.INFRA;
        }
        try {
            return ResourceCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResourceCategory.INFRA;
        }
    }

    private static ResourceCategory inferCategory(ResourceType type) {
        if (type == null) {
            return ResourceCategory.INFRA;
        }
        switch (type) {
            case APPLICATION:
            case SERVICE:
            case API:
                return ResourceCategory.BUSINESS;
            case MYSQL:
            case REDIS:
            case KAFKA:
            case ROCKETMQ:
            case CLICKHOUSE:
            case MQ:
            case MIDDLEWARE:
            case NGINX:
            case JVM:
                return ResourceCategory.PLATFORM;
            default:
                return ResourceCategory.INFRA;
        }
    }
}
