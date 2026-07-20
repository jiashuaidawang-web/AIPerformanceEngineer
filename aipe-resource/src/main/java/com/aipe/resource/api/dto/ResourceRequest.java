package com.aipe.resource.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 资源创建请求 DTO
 *
 * <p>对齐 IM-006 REST API Mapping / WP011 Blueprint
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ResourceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源名称
     */
    @NotBlank(message = "resourceName is required")
    private String resourceName;

    /**
     * 资源类型（对齐 ResourceType 枚举）
     */
    @NotBlank(message = "resourceType is required")
    private String resourceType;

    /**
     * 资源分类（BUSINESS/INFRA/PLATFORM）
     */
    private String resourceCategory;

    /**
     * 业务系统（必填 - Law-001）
     */
    @NotBlank(message = "businessSystem is required")
    private String businessSystem;

    /**
     * 父资源 ID（支持层级结构）
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
     * 环境标识（prod/staging/test）
     */
    private String environment;

    /**
     * 业务标签
     */
    private Map<String, String> labels;

    /**
     * 扩展属性
     */
    private Map<String, String> attributes;

    // ==================== Getter & Setter ====================

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
}
