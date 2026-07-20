package com.aipe.resource.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源持久化对象（PO）
 *
 * <p>对应 MySQL resource 表
 * <p>Gateway Law-001：Repository 不返回 PO，返回 Domain（通过 Converter 转换）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@TableName("resource")
public class ResourcePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID（业务主键 UUID）
     * 对应 DB 列：resource_id（对齐 IM-003 Persistence Mapping）
     */
    @TableId(value = "resource_id", type = IdType.ASSIGN_UUID)
    private String resourceId;

    /**
     * 资源名称
     */
    @TableField("resource_name")
    private String resourceName;

    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源分类
     */
    @TableField("resource_category")
    private String resourceCategory;

    /**
     * 父资源 ID
     */
    @TableField("parent_resource_id")
    private String parentResourceId;

    /**
     * 业务系统
     */
    @TableField("business_system")
    private String businessSystem;

    /**
     * 集群标识
     */
    @TableField("cluster")
    private String cluster;

    /**
     * 命名空间
     */
    @TableField("namespace")
    private String namespace;

    /**
     * 环境标识
     */
    @TableField("environment")
    private String environment;

    /**
     * 资源状态
     */
    /**
     * 资源状态（对齐 IM-003：使用 resource_status 列）
     */
    @TableField("resource_status")
    private String status;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 业务标签（JSON）
     */
    @TableField("labels")
    private String labels;

    /**
     * 扩展属性（JSON）
     */
    @TableField("attributes")
    private String attributes;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

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

    public String getParentResourceId() {
        return parentResourceId;
    }

    public void setParentResourceId(String parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public String getBusinessSystem() {
        return businessSystem;
    }

    public void setBusinessSystem(String businessSystem) {
        this.businessSystem = businessSystem;
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

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "ResourcePO{" +
                "id='" + resourceId + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", businessSystem='" + businessSystem + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
