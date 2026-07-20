package com.aipe.resource.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 资源响应 DTO
 *
 * <p>对齐 IM-006 REST API Mapping
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ResourceResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源分类
     */
    private String resourceCategory;

    /**
     * 业务系统
     */
    private String businessSystem;

    /**
     * 父资源 ID
     */
    private String parentResourceId;

    /**
     * 集群标识
     */
    private String cluster;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 环境标识
     */
    private String environment;

    /**
     * 资源状态
     */
    private String status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 业务标签
     */
    private Map<String, String> labels;

    /**
     * 扩展属性
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

    // ==================== Getter & Setter ====================

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(String resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public String getBusinessSystem() {
        return businessSystem;
    }

    public void setBusinessSystem(String businessSystem) {
        this.businessSystem = businessSystem;
    }

    public String getParentResourceId() {
        return parentResourceId;
    }

    public void setParentResourceId(String parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
