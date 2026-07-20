package com.aipe.resource.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 资源上报 DTO（Connector / Agent 上报用）
 *
 * <p>Law-001：所有 Connector 上报的资源信息通过 ResourceReport → ResourceDiscoveryService 统一处理
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ResourceReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源 ID（唯一标识，幂等键）
     */
    @NotBlank(message = "resourceId is required")
    private String resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型（对齐 ResourceType 枚举）
     */
    @NotBlank(message = "resourceType is required")
    private String resourceType;

    /**
     * 业务系统（必填 - Law-001 禁止游离资源）
     */
    @NotBlank(message = "businessSystem is required")
    private String businessSystem;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 扩展标签
     */
    private Map<String, String> labels;

    /**
     * 扩展属性
     */
    private Map<String, String> attributes;

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

    public String getBusinessSystem() {
        return businessSystem;
    }

    public void setBusinessSystem(String businessSystem) {
        this.businessSystem = businessSystem;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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
