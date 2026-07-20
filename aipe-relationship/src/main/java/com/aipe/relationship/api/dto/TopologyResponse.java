package com.aipe.relationship.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Topology 响应 DTO
 *
 * <p>Architecture Law-004：Topology Is A View —— 返回视图数据，不存储
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class TopologyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topologyId;
    private LocalDateTime builtAt;
    private String rootResourceId;
    private int nodeCount;
    private int edgeCount;
    private List<TopologyNodeDto> nodes;
    private List<TopologyEdgeDto> edges;

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    public LocalDateTime getBuiltAt() {
        return builtAt;
    }

    public void setBuiltAt(LocalDateTime builtAt) {
        this.builtAt = builtAt;
    }

    public String getRootResourceId() {
        return rootResourceId;
    }

    public void setRootResourceId(String rootResourceId) {
        this.rootResourceId = rootResourceId;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    public List<TopologyNodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<TopologyNodeDto> nodes) {
        this.nodes = nodes;
    }

    public List<TopologyEdgeDto> getEdges() {
        return edges;
    }

    public void setEdges(List<TopologyEdgeDto> edges) {
        this.edges = edges;
    }

    /**
     * Topology 节点
     */
    public static class TopologyNodeDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private String resourceId;
        private String resourceName;
        private String resourceType;
        private boolean root;
        private int degree;

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

        public boolean isRoot() {
            return root;
        }

        public void setRoot(boolean root) {
            this.root = root;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }
    }

    /**
     * Topology 边
     */
    public static class TopologyEdgeDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private String relationshipId;
        private String sourceResourceId;
        private String targetResourceId;
        private String relationshipType;
        private String direction;
        private double confidence;

        public String getRelationshipId() {
            return relationshipId;
        }

        public void setRelationshipId(String relationshipId) {
            this.relationshipId = relationshipId;
        }

        public String getSourceResourceId() {
            return sourceResourceId;
        }

        public void setSourceResourceId(String sourceResourceId) {
            this.sourceResourceId = sourceResourceId;
        }

        public String getTargetResourceId() {
            return targetResourceId;
        }

        public void setTargetResourceId(String targetResourceId) {
            this.targetResourceId = targetResourceId;
        }

        public String getRelationshipType() {
            return relationshipType;
        }

        public void setRelationshipType(String relationshipType) {
            this.relationshipType = relationshipType;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }
}
