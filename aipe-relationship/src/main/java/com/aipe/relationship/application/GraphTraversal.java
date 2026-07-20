package com.aipe.relationship.application;

import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图遍历工具（BFS / DFS / 影响分析 / 依赖方向）
 *
 * <p>对齐 WP013 Blueprint §9.2：邻接表查询性能 < 100ms（万级 Relationship）；支持 BFS / DFS / 上游 / 下游 / 邻居 / 影响（Impact Analysis）
 *
 * <p>算法说明（M2-008 ch8：Relationship 是 Graph，不是 Tree）：
 * <ul>
 *   <li>neighbors：同时查 source + target 双方（因为 Relationship 可能任一端关联到指定 Resource）</li>
 *   <li>downstream：从 source Resource 出发沿 source→target 方向遍历下游</li>
 *   <li>upstream：从 target Resource 出发沿 target→source 方向遍历上游</li>
 *   <li>impact：单次 BFS 返回所有受影响的 downstream + upstream 节点</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class GraphTraversal {

    private static final Logger log = LoggerFactory.getLogger(GraphTraversal.class);

    /** 默认最大遍历深度 */
    private static final int DEFAULT_MAX_DEPTH = 10;

    /**
     * 构建邻接表（source → list of relationships）
     */
    public Map<String, List<Relationship>> buildSourceIndex(Collection<Relationship> relationships) {
        Map<String, List<Relationship>> index = new HashMap<>();
        for (Relationship r : relationships) {
            if (r == null || !r.isActive()) {
                continue;
            }
            index.computeIfAbsent(r.getSourceResourceId(), k -> new ArrayList<>()).add(r);
        }
        return index;
    }

    /**
     * 构建反向邻接表（target → list of relationships，用于上游查询）
     */
    public Map<String, List<Relationship>> buildTargetIndex(Collection<Relationship> relationships) {
        Map<String, List<Relationship>> index = new HashMap<>();
        for (Relationship r : relationships) {
            if (r == null || !r.isActive()) {
                continue;
            }
            index.computeIfAbsent(r.getTargetResourceId(), k -> new ArrayList<>()).add(r);
        }
        return index;
    }

    /**
     * BFS 遍历下游（从 source 资源开始，沿 source → target 方向）
     *
     * @param startResourceId 起始 Resource ID
     * @param edges           源邻接表
     * @param maxDepth        最大深度
     * @return 节点 → 层级映射（按 BFS 层级，有序）
     */
    public Map<String, Integer> bfsDownstream(String startResourceId,
                                               Map<String, List<Relationship>> edges,
                                               int maxDepth) {
        Map<String, Integer> visited = new LinkedHashMap<>();
        if (startResourceId == null || edges == null) {
            return visited;
        }
        Deque<String> queue = new ArrayDeque<>();
        visited.put(startResourceId, 0);
        queue.add(startResourceId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDepth = visited.get(current);
            if (currentDepth >= maxDepth) {
                continue;
            }
            List<Relationship> outEdges = edges.getOrDefault(current, Collections.emptyList());
            for (Relationship r : outEdges) {
                if (!r.propagatesImpact()) {
                    continue;
                }
                String next = r.getTargetResourceId();
                if (!visited.containsKey(next)) {
                    visited.put(next, currentDepth + 1);
                    queue.add(next);
                }
            }
        }
        // 移除起始节点自身（影响分析不包含自己）
        visited.remove(startResourceId);
        return visited;
    }

    /**
     * BFS 遍历上游（从 target 资源开始，沿 target → source 方向）
     *
     * @param startResourceId 起始 Resource ID
     * @param reverseEdges   反向邻接表（target → relationships）
     * @param maxDepth       最大深度
     * @return 节点 → 层级映射
     */
    public Map<String, Integer> bfsUpstream(String startResourceId,
                                             Map<String, List<Relationship>> reverseEdges,
                                             int maxDepth) {
        Map<String, Integer> visited = new LinkedHashMap<>();
        if (startResourceId == null || reverseEdges == null) {
            return visited;
        }
        Deque<String> queue = new ArrayDeque<>();
        visited.put(startResourceId, 0);
        queue.add(startResourceId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDepth = visited.get(current);
            if (currentDepth >= maxDepth) {
                continue;
            }
            List<Relationship> inEdges = reverseEdges.getOrDefault(current, Collections.emptyList());
            for (Relationship r : inEdges) {
                if (!r.propagatesImpact()) {
                    continue;
                }
                String next = r.getSourceResourceId();
                if (!visited.containsKey(next)) {
                    visited.put(next, currentDepth + 1);
                    queue.add(next);
                }
            }
        }
        visited.remove(startResourceId);
        return visited;
    }

    /**
     * 影响分析：Resource 故障 → 影响哪些 Resource（下游 + 上游）
     *
     * @param resourceId       故障 Resource ID
     * @param allRelationships 全量 ACTIVE Relationship
     * @return 受影响的 Resource ID 集合（不含自身）
     */
    public Set<String> analyzeImpact(String resourceId, Collection<Relationship> allRelationships) {
        if (resourceId == null || allRelationships == null || allRelationships.isEmpty()) {
            return Collections.emptySet();
        }
        Map<String, List<Relationship>> sourceIndex = buildSourceIndex(allRelationships);
        Map<String, List<Relationship>> targetIndex = buildTargetIndex(allRelationships);

        Set<String> impacted = new HashSet<>();
        impacted.addAll(bfsDownstream(resourceId, sourceIndex, DEFAULT_MAX_DEPTH).keySet());
        impacted.addAll(bfsUpstream(resourceId, targetIndex, DEFAULT_MAX_DEPTH).keySet());

        impacted.remove(resourceId);
        log.debug("Impact analysis for {}: {} impacted resources", resourceId, impacted.size());
        return impacted;
    }

    /**
     * 检测循环依赖（A→B→C→A）
     *
     * <p>在新建 Relationship 时调用：如果加入这条边后形成环，则拒绝。
     *
     * @param newRelationship 待新建的 Relationship
     * @param existingEdges   现有 Relationship 的源邻接表
     * @return 是否构成环
     */
    public boolean wouldCreateCycle(Relationship newRelationship,
                                    Map<String, List<Relationship>> existingEdges) {
        if (newRelationship == null || existingEdges == null) {
            return false;
        }
        String source = newRelationship.getSourceResourceId();
        String target = newRelationship.getTargetResourceId();
        if (source.equals(target)) {
            return true;
        }
        // 如果从 target 出发能回到 source，则加入 source→target 会形成环
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(target);
        visited.add(target);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<Relationship> outEdges = existingEdges.getOrDefault(current, Collections.emptyList());
            for (Relationship r : outEdges) {
                String next = r.getTargetResourceId();
                if (source.equals(next)) {
                    return true;
                }
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    /**
     * 查询邻居（一度关系，Source + Target 双方）
     *
     * @param resourceId        Resource ID
     * @param allRelationships  全量 ACTIVE Relationship
     * @return 邻居 Resource ID 集合（不含自身）
     */
    public Set<String> findNeighbors(String resourceId, Collection<Relationship> allRelationships) {
        Set<String> neighbors = new HashSet<>();
        if (resourceId == null || allRelationships == null) {
            return neighbors;
        }
        for (Relationship r : allRelationships) {
            if (r == null || !r.isActive()) {
                continue;
            }
            if (resourceId.equals(r.getSourceResourceId())) {
                neighbors.add(r.getTargetResourceId());
            } else if (resourceId.equals(r.getTargetResourceId())) {
                neighbors.add(r.getSourceResourceId());
            }
        }
        neighbors.remove(resourceId);
        return neighbors;
    }

    /**
     * 过滤指定类型的 Relationship
     */
    public List<Relationship> filterByType(Collection<Relationship> relationships, RelationshipType... types) {
        if (relationships == null || types == null || types.length == 0) {
            return new ArrayList<>(relationships != null ? relationships : Collections.emptyList());
        }
        Set<RelationshipType> typeSet = new HashSet<>();
        for (RelationshipType t : types) {
            typeSet.add(t);
        }
        List<Relationship> result = new ArrayList<>();
        for (Relationship r : relationships) {
            if (r != null && typeSet.contains(r.getRelationshipType())) {
                result.add(r);
            }
        }
        return result;
    }
}
