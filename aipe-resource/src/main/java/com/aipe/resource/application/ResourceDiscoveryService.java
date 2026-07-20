package com.aipe.resource.application;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceCategory;
import com.aipe.resource.domain.ResourceFactory;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceName;
import com.aipe.resource.domain.ResourceRepository;
import com.aipe.resource.domain.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源发现服务
 *
 * <p>Law-001：所有 Connector/Agent 上报的资源信息必须通过 ResourceDiscoveryService 统一处理
 * <p>禁止 Connector/Agent 直接创建/修改 Resource
 *
 * <p>职责：
 * <ul>
 *   <li>接收 Connector 上报的资源信息</li>
 *   <li>校验后统一创建 / 更新 Resource</li>
 *   <li>幂等处理（同一 resourceId 多次上报应更新而非重复创建）</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ResourceDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(ResourceDiscoveryService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceLifecycleManager lifecycleManager;

    /**
     * 处理 Connector 上报的资源信息
     *
     * <p>幂等逻辑：如果 resourceId 已存在则更新，否则创建
     *
     * @param resourceId     资源 ID（唯一标识）
     * @param resourceName   资源名称
     * @param resourceType   资源类型
     * @param businessSystem 业务系统（必填）
     * @param host           主机地址
     * @param port           端口
     * @param labels         标签
     * @return 创建或更新后的资源
     */
    @Transactional
    public Resource handleResourceReport(String resourceId,
                                         String resourceName,
                                         String resourceType,
                                         String businessSystem,
                                         String host,
                                         Integer port,
                                         Map<String, String> labels) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("resourceId is required");
        }
        if (businessSystem == null || businessSystem.trim().isEmpty()) {
            throw new IllegalArgumentException("businessSystem is required (Law-001: no orphan resource)");
        }

        ResourceId id = ResourceId.of(resourceId);

        // 检查是否已存在
        Resource existing = resourceRepository.findById(id).orElse(null);
        if (existing != null) {
            // 更新现有资源
            return updateExistingResource(existing, resourceName, resourceType, businessSystem, host, port, labels);
        } else {
            // 创建新资源
            return createNewResource(id, resourceName, resourceType, businessSystem, host, port, labels);
        }
    }

    /**
     * 批量处理资源上报
     *
     * @param reports 资源上报列表（每个元素为 Map，包含 resourceId/resourceName/resourceType/businessSystem/host/port）
     * @return 成功处理的数量
     */
    @Transactional
    public int batchHandleResourceReport(java.util.List<Map<String, String>> reports) {
        if (reports == null || reports.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Map<String, String> report : reports) {
            try {
                String resourceId = report.get("resourceId");
                String resourceName = report.get("resourceName");
                String resourceType = report.get("resourceType");
                String businessSystem = report.get("businessSystem");
                String host = report.get("host");
                String portStr = report.get("port");
                Integer port = portStr != null ? Integer.parseInt(portStr) : null;

                handleResourceReport(resourceId, resourceName, resourceType, businessSystem, host, port, null);
                count++;
            } catch (Exception e) {
                log.warn("Failed to handle resource report: {}, error: {}", report, e.getMessage());
            }
        }
        log.info("Batch handled {} / {} resource reports", count, reports.size());
        return count;
    }

    // ==================== 私有方法 ====================

    private Resource createNewResource(ResourceId id,
                                       String resourceName,
                                       String resourceType,
                                       String businessSystem,
                                       String host,
                                       Integer port,
                                       Map<String, String> labels) {
        ResourceName name = ResourceName.of(resourceName != null ? resourceName : id.getValue());
        ResourceType type = parseResourceType(resourceType);
        ResourceCategory category = inferCategory(type);

        Map<String, String> attributes = new HashMap<>();
        if (host != null) {
            attributes.put("host", host);
        }
        if (port != null) {
            attributes.put("port", String.valueOf(port));
        }

        Map<String, String> finalLabels = labels != null ? labels : new HashMap<>();

        Resource resource = ResourceFactory.create(
                name, type, category, businessSystem,
                null, null, null, null,
                finalLabels, attributes
        );

        return lifecycleManager.createResource(resource);
    }

    private Resource updateExistingResource(Resource existing,
                                             String resourceName,
                                             String resourceType,
                                             String businessSystem,
                                             String host,
                                             Integer port,
                                             Map<String, String> labels) {
        // 更新基础信息（保留原有 ID 和版本）
        if (resourceName != null) {
            existing.getResourceName(); // 校验现有
        }
        if (host != null) {
            existing.setAttribute("host", host);
        }
        if (port != null) {
            existing.setAttribute("port", String.valueOf(port));
        }
        if (labels != null) {
            labels.forEach(existing::addLabel);
        }
        return lifecycleManager.updateResource(existing);
    }

    private ResourceType parseResourceType(String resourceType) {
        if (resourceType == null) {
            return ResourceType.UNKNOWN;
        }
        try {
            return ResourceType.valueOf(resourceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResourceType.UNKNOWN;
        }
    }

    private ResourceCategory inferCategory(ResourceType type) {
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
