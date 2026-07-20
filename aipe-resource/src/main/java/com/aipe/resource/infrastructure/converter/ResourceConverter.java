package com.aipe.resource.infrastructure.converter;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceCategory;
import com.aipe.resource.domain.ResourceFactory;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceName;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;
import com.aipe.resource.infrastructure.po.ResourcePO;

import java.util.Map;

/**
 * 资源转换器
 *
 * <p>Domain ↔ Persistence 对象转换（Gateway Law-001：Repository 返回 Domain）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceConverter {

    private ResourceConverter() {
        // 工具类，禁止实例化
    }

    /**
     * Domain → PO（写入数据库前调用）
     *
     * @param resource 资源聚合根
     * @return ResourcePO
     */
    public static ResourcePO toPO(Resource resource) {
        if (resource == null) {
            return null;
        }
        ResourcePO po = new ResourcePO();
        po.setResourceId(resource.getId() != null ? resource.getId().getValue() : null);
        po.setResourceName(resource.getResourceName() != null ? resource.getResourceName().getValue() : null);
        po.setResourceType(resource.getResourceType() != null ? resource.getResourceType().name() : null);
        po.setResourceCategory(resource.getResourceCategory() != null ? resource.getResourceCategory().name() : null);
        po.setParentResourceId(resource.getParentResourceId() != null ? resource.getParentResourceId().getValue() : null);
        po.setBusinessSystem(resource.getBusinessSystem());
        po.setCluster(resource.getCluster());
        po.setNamespace(resource.getNamespace());
        po.setEnvironment(resource.getEnvironment());
        po.setStatus(resource.getStatus() != null ? resource.getStatus().name() : null);
        po.setVersion(resource.getVersion());
        po.setLabels(JsonConverter.mapToJson(resource.getLabels()));
        po.setAttributes(JsonConverter.mapToJson(resource.getAttributes()));
        po.setCreatedTime(resource.getCreatedTime());
        po.setUpdatedTime(resource.getUpdatedTime());
        return po;
    }

    /**
     * PO → Domain（从数据库读取后调用）
     *
     * @param po 资源 PO
     * @return Resource 聚合根
     */
    public static Resource toDomain(ResourcePO po) {
        if (po == null) {
            return null;
        }

        // 解析 labels / attributes JSON
        Map<String, String> labels = JsonConverter.jsonToMap(po.getLabels());
        Map<String, String> attributes = JsonConverter.jsonToMap(po.getAttributes());

        // 解析枚举
        ResourceType type = parseEnum(ResourceType.class, po.getResourceType(), ResourceType.UNKNOWN);
        ResourceCategory category = parseEnum(ResourceCategory.class, po.getResourceCategory(), ResourceCategory.INFRA);
        ResourceStatus status = parseEnum(ResourceStatus.class, po.getStatus(), ResourceStatus.UNKNOWN);

        // 解析值对象
        ResourceId id = po.getResourceId() != null ? ResourceId.of(po.getResourceId()) : null;
        ResourceName name = po.getResourceName() != null ? ResourceName.of(po.getResourceName()) : null;
        ResourceId parentId = po.getParentResourceId() != null ? ResourceId.of(po.getParentResourceId()) : null;

        return ResourceFactory.reconstruct(
                id,
                name,
                type,
                category,
                po.getBusinessSystem(),
                parentId,
                po.getCluster(),
                po.getNamespace(),
                po.getEnvironment(),
                status,
                po.getVersion() != null ? po.getVersion() : 1,
                labels,
                attributes,
                po.getCreatedTime(),
                po.getUpdatedTime()
        );
    }

    /**
     * 安全解析枚举
     */
    private static <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
