package com.aipe.relationship.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Topology 视图值对象
 *
 * <p>Architecture Law-004：Topology Is A View —— Topology 不是存储对象，是 Resource Graph 在某一时刻的投影。
 *
 * <p>每次查询实时从 Relationship 计算生成，不持久化。
 *
 * <p>对齐 M2-009 Topology Model ch7 Schema
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TopologyView {

    /**
     * 查询标识（每次查询唯一）
     */
    private final String topologyId;

    /**
     * 构建时间
     */
    private final LocalDateTime builtAt;

    /**
     * 根节点 Resource ID
     */
    private final String rootResourceId;

    /**
     * 节点数量
     */
    private final int nodeCount;

    /**
     * 边数量
     */
    private final int edgeCount;

    /**
     * Resource 节点集合
     */
    private final List<ResourceNode> nodes;

    /**
     * Relationship 边集合
     */
    private final List<ResourceEdge> edges;

    public TopologyView(String topologyId,
                        LocalDateTime builtAt,
                        String rootResourceId,
                        List<ResourceNode> nodes,
                        List<ResourceEdge> edges) {
        this.topologyId = topologyId;
        this.builtAt = builtAt;
        this.rootResourceId = rootResourceId;
        this.nodes = nodes != null ? Collections.unmodifiableList(nodes) : Collections.emptyList();
        this.edges = edges != null ? Collections.unmodifiableList(edges) : Collections.emptyList();
        this.nodeCount = this.nodes.size();
        this.edgeCount = this.edges.size();
    }

    public String getTopologyId() {
        return topologyId;
    }

    public LocalDateTime getBuiltAt() {
        return builtAt;
    }

    public String getRootResourceId() {
        return rootResourceId;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public List<ResourceNode> getNodes() {
        return nodes;
    }

    public List<ResourceEdge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyView that = (TopologyView) o;
        return Objects.equals(topologyId, that.topologyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topologyId);
    }

    @Override
    public String toString() {
        return "TopologyView{" +
                "id='" + topologyId + '\'' +
                ", root='" + rootResourceId + '\'' +
                ", nodes=" + nodeCount +
                ", edges=" + edgeCount +
                '}';
    }
}
