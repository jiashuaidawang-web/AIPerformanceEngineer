package com.aipe.resource.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 资源聚合根
 *
 * <p>AI World 中所有 IT 对象的统一抽象（Constitution Article 3：Everything Is Resource）
 *
 * <p>业务规则：
 * <ul>
 *   <li>每个 Resource 必须有明确的业务归属（businessSystem 必填，禁止游离资源）</li>
 *   <li>资源状态流转必须合法（RUNNING → MAINTENANCE → RUNNING / STOPPED）</li>
 *   <li>资源拥有版本号（乐观锁，防止并发覆盖）</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class Resource {

    /**
     * 业务主键（UUID）
     */
    private final ResourceId id;

    /**
     * 资源名称
     */
    private ResourceName resourceName;

    /**
     * 资源类型
     */
    private ResourceType resourceType;

    /**
     * 资源分类
     */
    private ResourceCategory resourceCategory;

    /**
     * 业务系统（必填，禁止游离资源 - Law-001 / Law-002）
     */
    private String businessSystem;

    /**
     * 父资源 ID（支持层级结构）
     */
    private ResourceId parentResourceId;

    /**
     * 集群标识
     */
    private String cluster;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 环境标识（prod/staging/test）
     */
    private String environment;

    /**
     * 资源状态
     */
    private ResourceStatus status;

    /**
     * 乐观锁版本号
     */
    private int version;

    /**
     * 业务标签（部门、负责人、环境等）
     */
    private Map<String, String> labels;

    /**
     * 扩展属性（IP、端口、集群规格等）
     */
    private Map<String, String> attributes;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 构造函数（包级私有，强制使用 Factory）
     */
    Resource(ResourceId id,
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
        this.id = id;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.resourceCategory = resourceCategory;
        this.businessSystem = businessSystem;
        this.parentResourceId = parentResourceId;
        this.cluster = cluster;
        this.namespace = namespace;
        this.environment = environment;
        this.status = status;
        this.version = version;
        this.labels = labels != null ? new HashMap<>(labels) : new HashMap<>();
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // ==================== 业务方法 ====================

    /**
     * 校验资源是否合法
     *
     * @throws IllegalArgumentException 校验失败
     */
    public void validate() {
        if (businessSystem == null || businessSystem.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource must belong to a business system (Law-001: no orphan resource)");
        }
        if (resourceType == null) {
            throw new IllegalArgumentException("ResourceType is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("ResourceStatus is required");
        }
    }

    /**
     * 是否属于指定业务系统
     *
     * @param system 业务系统名
     * @return 是否属于
     */
    public boolean belongsTo(String system) {
        return businessSystem != null && businessSystem.equals(system);
    }

    /**
     * 是否处于运行状态
     *
     * @return 是否运行中
     */
    public boolean isRunning() {
        return ResourceStatus.RUNNING.equals(status);
    }

    /**
     * 是否已过期（STOPPED 或 MAINTENANCE 超过阈值可判定）
     *
     * @return 是否已停止或维护
     */
    public boolean isStopped() {
        return ResourceStatus.STOPPED.equals(status) || ResourceStatus.MAINTENANCE.equals(status);
    }

    /**
     * 状态流转（带合法性校验）
     *
     * <p>注意：version 由 MyBatis Plus 乐观锁自动管理，此处不手动递增
     *
     * @param newStatus 目标状态
     * @throws IllegalStateException 非法状态流转
     */
    public void transitionStatus(ResourceStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", this.status, newStatus));
        }
        this.status = newStatus;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新扩展属性
     *
     * @param key   属性名
     * @param value 属性值
     */
    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 添加标签
     *
     * @param key   标签名
     * @param value 标签值
     */
    public void addLabel(String key, String value) {
        this.labels.put(key, value);
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新资源基础信息
     *
     * @param resourceName 新名称
     * @param environment  新环境
     */
    public void updateInfo(ResourceName resourceName, String environment) {
        if (resourceName != null) {
            this.resourceName = resourceName;
        }
        if (environment != null) {
            this.environment = environment;
        }
        this.updatedTime = LocalDateTime.now();
    }

    // ==================== Getter ====================

    public ResourceId getId() {
        return id;
    }

    public ResourceName getResourceName() {
        return resourceName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public String getBusinessSystem() {
        return businessSystem;
    }

    public ResourceId getParentResourceId() {
        return parentResourceId;
    }

    public String getCluster() {
        return cluster;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEnvironment() {
        return environment;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public int getVersion() {
        return version;
    }

    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", name=" + resourceName +
                ", type=" + resourceType +
                ", system=" + businessSystem +
                ", status=" + status +
                '}';
    }
}
