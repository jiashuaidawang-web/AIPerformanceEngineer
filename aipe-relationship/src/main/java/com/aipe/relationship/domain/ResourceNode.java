package com.aipe.relationship.domain;

import java.util.Objects;

/**
 * Topology 节点值对象
 *
 * <p>表示拓扑视图中的一个 Resource 节点（对齐 M2-009 ch7 nodes）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceNode {

    /**
     * Resource ID
     */
    private final String resourceId;

    /**
     * Resource 名称
     */
    private final String resourceName;

    /**
     * Resource 类型
     */
    private final String resourceType;

    /**
     * 是否为根节点
     */
    private final boolean root;

    /**
     * 距离根节点的度数（BFS 层级）
     */
    private final int degree;

    public ResourceNode(String resourceId, String resourceName, String resourceType, boolean root, int degree) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.root = root;
        this.degree = degree;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public boolean isRoot() {
        return root;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceNode that = (ResourceNode) o;
        return Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }

    @Override
    public String toString() {
        return "ResourceNode{" +
                "id='" + resourceId + '\'' +
                ", name='" + resourceName + '\'' +
                ", type='" + resourceType + '\'' +
                ", root=" + root +
                ", degree=" + degree +
                '}';
    }
}
