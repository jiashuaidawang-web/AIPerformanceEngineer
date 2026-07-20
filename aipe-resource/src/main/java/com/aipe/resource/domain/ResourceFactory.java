package com.aipe.resource.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源工厂
 *
 * <p>负责创建合法的 Resource 对象（Domain Law-003：每个 Aggregate 必须拥有 Factory）
 * <p>禁止 Controller 直接 new Resource()
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceFactory {

    private ResourceFactory() {
        // 工具类，禁止实例化
    }

    /**
     * 创建新资源（自动分配 ID、初始化状态和版本）
     *
     * @param resourceName      资源名称
     * @param resourceType      资源类型
     * @param resourceCategory  资源分类
     * @param businessSystem    业务系统（必填）
     * @param parentResourceId  父资源 ID（可为 null）
     * @param cluster           集群标识
     * @param namespace         命名空间
     * @param environment       环境标识
     * @param labels            业务标签
     * @param attributes        扩展属性
     * @return 新的 Resource（状态为 RUNNING，版本为 1）
     */
    public static Resource create(ResourceName resourceName,
                                  ResourceType resourceType,
                                  ResourceCategory resourceCategory,
                                  String businessSystem,
                                  ResourceId parentResourceId,
                                  String cluster,
                                  String namespace,
                                  String environment,
                                  Map<String, String> labels,
                                  Map<String, String> attributes) {
        Resource resource = new Resource(
                ResourceId.generate(),
                resourceName,
                resourceType,
                resourceCategory,
                businessSystem,
                parentResourceId,
                cluster,
                namespace,
                environment,
                ResourceStatus.RUNNING,
                1,
                labels != null ? new HashMap<>(labels) : new HashMap<>(),
                attributes != null ? new HashMap<>(attributes) : new HashMap<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        // 创建时立即校验
        resource.validate();
        return resource;
    }

    /**
     * 从持久化数据重建Resource（Repository 专用）
     *
     * @param id                业务主键
     * @param resourceName      资源名称
     * @param resourceType      资源类型
     * @param resourceCategory  资源分类
     * @param businessSystem    业务系统
     * @param parentResourceId  父资源 ID
     * @param cluster           集群标识
     * @param namespace         命名空间
     * @param environment       环境标识
     * @param status            状态
     * @param version           版本号
     * @param labels            标签
     * @param attributes        扩展属性
     * @param createdTime       创建时间
     * @param updatedTime       更新时间
     * @return 重建的 Resource
     */
    public static Resource reconstruct(ResourceId id,
                                       ResourceName resourceName,
                                       ResourceType resourceType,
                                       ResourceCategory resourceCategory,
                                       String businessSystem,
                                       ResourceId parentResourceId,
                                       String cluster,
                                       String namespace,
                                       String environment,
                                       ResourceStatus status,
                                       int version,
                                       Map<String, String> labels,
                                       Map<String, String> attributes,
                                       LocalDateTime createdTime,
                                       LocalDateTime updatedTime) {
        return new Resource(
                id,
                resourceName,
                resourceType,
                resourceCategory,
                businessSystem,
                parentResourceId,
                cluster,
                namespace,
                environment,
                status,
                version,
                labels,
                attributes,
                createdTime,
                updatedTime
        );
    }
}
