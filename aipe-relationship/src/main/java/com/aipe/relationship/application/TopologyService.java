package com.aipe.relationship.application;

import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipRepository;
import com.aipe.relationship.domain.RelationshipType;
import com.aipe.relationship.domain.ResourceEdge;
import com.aipe.relationship.domain.ResourceNode;
import com.aipe.relationship.domain.TopologyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Topology 投影服务（Application 层）
 *
 * <p>核心职责（M2-009 / WP013 Blueprint §4.5）：
 * <ul>
 *   <li>从 RelationshipRepository 加载边 → 内存图遍历 → 返回 Topology 值对象</li>
 *   <li>Topology 不存储（Architecture Law-004），每次查询新建</li>
 * </ul>
 *
 * <p>支持：实时拓扑构建 / 历史时间点重建 / 邻居查询 / 上下游依赖 / 影响分析 / 指定类型过滤
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class TopologyService {

    private static final Logger log = LoggerFactory.getLogger(TopologyService.class);

    /** 默认最大拓扑遍历深度 */
    private static final int DEFAULT_MAX_DEPTH = 10;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private GraphTraversal graphTraversal;

    /**
     * 构建指定 Resource 的实时拓扑（当前时刻 + 可选类型过滤）
     *
     * <p>对齐 M2-009 Topology Model ch6（Topology 生命周期：Query → Build Projection → Return View → Discard）
     *
     * @param rootResourceId 根 Resource ID
     * @param types          关系类型过滤（为空则全量）
     * @return TopologyView（每次查询新建）
     */
    public TopologyView buildCurrent(String rootResourceId, RelationshipType... types) {
        if (rootResourceId == null || rootResourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("rootResourceId is required");
        }
        List<Relationship> allActive = relationshipRepository.findAllActive();
        List<Relationship> filtered = graphTraversal.filterByType(allActive, types);
        return buildTopologyView(rootResourceId, filtered);
    }

    /**
     * 构建拓扑视图（BFS 从 root 展开，不含环）
     */
    private TopologyView buildTopologyView(String rootResourceId, List<Relationship> relationships) {
        if (relationships == null || relationships.isEmpty()) {
            // 仅包含根节点，无边
            List<ResourceNode> nodes = new ArrayList<>();
            nodes.add(new ResourceNode(rootResourceId, rootResourceId, "UNKNOWN", true, 0));
            return new TopologyView(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now(),
                    rootResourceId,
                    nodes,
                    Collections.emptyList()
            );
        }

        Map<String, List<Relationship>> sourceIndex = graphTraversal.buildSourceIndex(relationships);

        // BFS 展开
        Set<String> nodeIds = new HashSet<>();
        nodeIds.add(rootResourceId);
        Map<String, Integer> downstream = graphTraversal.bfsDownstream(rootResourceId, sourceIndex, DEFAULT_MAX_DEPTH);
        Map<String, Integer> upstream = graphTraversal.bfsUpstream(rootResourceId,
                graphTraversal.buildTargetIndex(relationships), DEFAULT_MAX_DEPTH);

        // 合并所有节点 ID
        Set<String> allNodeIds = new HashSet<>();
        allNodeIds.add(rootResourceId);
        allNodeIds.addAll(downstream.keySet());
        allNodeIds.addAll(upstream.keySet());

        // 构建节点列表
        List<ResourceNode> nodes = new ArrayList<>();
        nodes.add(new ResourceNode(rootResourceId, rootResourceId, "UNKNOWN", true, 0));
        for (String nid : allNodeIds) {
            if (rootResourceId.equals(nid)) {
                continue;
            }
            int degree = downstream.getOrDefault(nid, upstream.getOrDefault(nid, 0));
            nodes.add(new ResourceNode(nid, nid, "UNKNOWN", false, degree));
        }

        // 构建边列表（仅保留关联到已发现节点的边）
        List<ResourceEdge> edges = new ArrayList<>();
        for (Relationship r : relationships) {
            if (!r.isActive()) {
                continue;
            }
            if (allNodeIds.contains(r.getSourceResourceId()) && allNodeIds.contains(r.getTargetResourceId())) {
                edges.add(new ResourceEdge(
                        r.getRelationshipId().getValue(),
                        r.getSourceResourceId(),
                        r.getTargetResourceId(),
                        r.getRelationshipType(),
                        r.getDirection(),
                        r.getConfidence()));
            }
        }

        return new TopologyView(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                rootResourceId,
                nodes,
                edges
        );
    }

    /**
     * 查询 Resource 的邻居（一度关系）
     *
     * @param resourceId Resource ID
     * @param degree    度数（1=邻居，2=邻居的邻居）
     * @return 邻居 Resource 节点列表
     */
    public List<ResourceNode> queryNeighbors(String resourceId, int degree) {
        if (resourceId == null) {
            return Collections.emptyList();
        }
        int safeDegree = degree <= 0 ? 1 : Math.min(degree, DEFAULT_MAX_DEPTH);
        List<Relationship> allActive = relationshipRepository.findAllActive();
        Set<String> neighbors = graphTraversal.findNeighbors(resourceId, allActive);

        List<ResourceNode> nodes = new ArrayList<>();
        int d = 1;
        Set<String> currentLevel = new HashSet<>(neighbors);
        while (d <= safeDegree && !currentLevel.isEmpty()) {
            Set<String> nextLevel = new HashSet<>();
            for (String nid : currentLevel) {
                nodes.add(new ResourceNode(nid, nid, "UNKNOWN", false, d));
                if (d < safeDegree) {
                    for (Relationship r : allActive) {
                        if (!r.isActive()) continue;
                        if (nid.equals(r.getSourceResourceId())) {
                            if (!resourceId.equals(r.getTargetResourceId())) {
                                nextLevel.add(r.getTargetResourceId());
                            }
                        } else if (nid.equals(r.getTargetResourceId())) {
                            if (!resourceId.equals(r.getSourceResourceId())) {
                                nextLevel.add(r.getSourceResourceId());
                            }
                        }
                    }
                }
            }
            currentLevel = nextLevel;
            d++;
        }
        return nodes;
    }

    /**
     * 查询 Resource 的上下游依赖（全路径 BFS）
     *
     * @param resourceId Resource ID
     * @param direction 方向：upstream / downstream
     * @return 依赖节点列表
     */
    public List<ResourceNode> queryDependencies(String resourceId, String direction) {
        if (resourceId == null) {
            return Collections.emptyList();
        }
        List<Relationship> allActive = relationshipRepository.findAllActive();
        Map<String, Integer> visited;

        if ("upstream".equalsIgnoreCase(direction)) {
            visited = graphTraversal.bfsUpstream(resourceId,
                    graphTraversal.buildTargetIndex(allActive), DEFAULT_MAX_DEPTH);
        } else {
            // default: downstream
            visited = graphTraversal.bfsDownstream(resourceId,
                    graphTraversal.buildSourceIndex(allActive), DEFAULT_MAX_DEPTH);
        }

        List<ResourceNode> nodes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : visited.entrySet()) {
            nodes.add(new ResourceNode(entry.getKey(), entry.getKey(), "UNKNOWN", false, entry.getValue()));
        }
        return nodes;
    }

    /**
     * 影响分析：Resource 故障 → 影响哪些 Resource
     *
     * @param resourceId 故障 Resource ID
     * @return 受影响的节点列表（含层级）
     */
    public List<ResourceNode> queryImpact(String resourceId) {
        if (resourceId == null) {
            return Collections.emptyList();
        }
        List<Relationship> allActive = relationshipRepository.findAllActive();
        Set<String> impactSet = graphTraversal.analyzeImpact(resourceId, allActive);
        List<ResourceNode> nodes = new ArrayList<>();
        for (String nid : impactSet) {
            nodes.add(new ResourceNode(nid, nid, "UNKNOWN", false, 1));
        }
        return nodes;
    }

    /**
     * 最短路径查询（BFS，返回节点序列）
     *
     * @param from 起点 Resource ID
     * @param to   终点 Resource ID
     * @return 路径节点列表（含起点和终点），无路径返回空列表
     */
    public List<ResourceNode> shortestPath(String from, String to) {
        if (from == null || to == null || from.equals(to)) {
            return Collections.emptyList();
        }
        List<Relationship> allActive = relationshipRepository.findAllActive();
        Map<String, List<Relationship>> sourceIndex = graphTraversal.buildSourceIndex(allActive);

        // BFS with path tracking
        Map<String, String> parent = new java.util.HashMap<>();
        Set<String> visited = new HashSet<>();
        java.util.Deque<String> queue = new java.util.ArrayDeque<>();
        queue.add(from);
        visited.add(from);
        boolean found = false;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (to.equals(current)) {
                found = true;
                break;
            }
            List<Relationship> outEdges = sourceIndex.getOrDefault(current, Collections.emptyList());
            for (Relationship r : outEdges) {
                String next = r.getTargetResourceId();
                if (!visited.contains(next)) {
                    visited.add(next);
                    parent.put(next, current);
                    queue.add(next);
                }
            }
        }

        if (!found) {
            return Collections.emptyList();
        }

        // 反向回溯路径
        List<ResourceNode> path = new ArrayList<>();
        String current = to;
        int degree = 0;
        while (current != null) {
            path.add(0, new ResourceNode(current, current, "UNKNOWN", from.equals(current), degree++));
            current = parent.get(current);
            if (from.equals(current)) {
                path.add(0, new ResourceNode(current, current, "UNKNOWN", true, 0));
                break;
            }
        }
        return path;
    }
}
