package com.aipe.relationship.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 影响分析响应 DTO
 *
 * <p>Resource 故障 → 影响哪些 Resource（对齐 WP013 Blueprint §7.1 topology/impact）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ImpactResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 故障根 Resource ID */
    private String rootResourceId;

    /** 受影响 Resource 数量 */
    private int impactCount;

    /** 受影响的 Resource ID 列表 */
    private List<String> impactedResourceIds;

    public String getRootResourceId() {
        return rootResourceId;
    }

    public void setRootResourceId(String rootResourceId) {
        this.rootResourceId = rootResourceId;
    }

    public int getImpactCount() {
        return impactCount;
    }

    public void setImpactCount(int impactCount) {
        this.impactCount = impactCount;
    }

    public List<String> getImpactedResourceIds() {
        return impactedResourceIds;
    }

    public void setImpactedResourceIds(List<String> impactedResourceIds) {
        this.impactedResourceIds = impactedResourceIds;
    }
}
