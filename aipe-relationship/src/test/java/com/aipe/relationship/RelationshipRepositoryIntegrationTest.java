package com.aipe.relationship;

import com.aipe.relationship.application.RelationshipApplicationService;
import com.aipe.relationship.application.TopologyService;
import com.aipe.relationship.domain.Relationship;
import com.aipe.relationship.domain.RelationshipType;
import com.aipe.relationship.domain.TopologyView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Relationship + Topology 集成测试（真实 MySQL，通过 JdbcTemplate 管理 resource 测试数据）
 *
 * <p>对齐 WP013 Blueprint §10.2 Test Scenario
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RelationshipApplication.class)
@ActiveProfiles("test")
public class RelationshipRepositoryIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RelationshipRepositoryIntegrationTest.class);

    @Autowired
    private RelationshipApplicationService applicationService;

    @Autowired
    private TopologyService topologyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 测试资源的 resource_id
    private String orderId;
    private String invId;
    private String redisId;
    private String hostId;

    @Before
    public void setUp() {
        log.info("=== Relationship Integration Test Setup ===");
        // 新建 4 个测试用 Resource（直接插 resource 表，避免依赖 aipe-resource 模块）
        orderId = createTestResource("order-svc-test", "测试订单系统");
        invId = createTestResource("inventory-svc-test", "测试订单系统");
        redisId = createTestResource("redis-cluster-test", "测试订单系统");
        hostId = createTestResource("linux-host-test", "测试订单系统");
    }

    @After
    public void tearDown() {
        // 清理：归档关联的 Relationship，再删除 Resource
        try {
            List<Relationship> all = applicationService.findAllActive();
            for (Relationship r : all) {
                if (isTestRelationship(r)) {
                    try {
                        applicationService.removeRelationship(r.getRelationshipId());
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Cleanup relationships failed: {}", e.getMessage());
        }
        // 删除测试 Resource
        deleteResource(orderId);
        deleteResource(invId);
        deleteResource(redisId);
        deleteResource(hostId);
    }

    private boolean isTestRelationship(Relationship r) {
        return r.involves(orderId) || r.involves(invId) || r.involves(redisId) || r.involves(hostId);
    }

    /**
     * 测试场景 1：新建 Order Service CALLS Inventory Service + DEPENDS_ON Redis Cluster + RUNS_ON Linux Host
     * 对齐 Blueprint §10.2
     */
    @Test
    public void should_create_relationships_successfully() {
        Relationship calls = applicationService.createRelationship(
                RelationshipType.CALLS, orderId, invId, null, 100.0, "MANUAL", null);
        assertNotNull(calls);
        assertNotNull(calls.getRelationshipId());
        assertTrue(calls.involves(invId));

        Relationship depends = applicationService.createRelationship(
                RelationshipType.DEPENDS_ON, orderId, redisId, null, 95.0, "Discovery", null);
        assertNotNull(depends);
        Relationship runsOn = applicationService.createRelationship(
                RelationshipType.RUNS_ON, redisId, hostId, null, 100.0, "MANUAL", null);
        assertNotNull(runsOn);

        System.out.println("[PASS] 新建 3 条 Relationship: CALLS / DEPENDS_ON / RUNS_ON");
    }

    /**
     * 测试场景 2：Order Service 的 neighbors 应该包含 [Inventory Service, Redis Cluster]
     */
    @Test
    public void should_query_neighbors() {
        applicationService.createRelationship(RelationshipType.CALLS, orderId, invId, null, 100.0, "MANUAL", null);
        applicationService.createRelationship(RelationshipType.DEPENDS_ON, orderId, redisId, null, 100.0, "MANUAL", null);
        applicationService.createRelationship(RelationshipType.RUNS_ON, redisId, hostId, null, 100.0, "MANUAL", null);

        List<Relationship> neighbors = applicationService.findNeighbors(orderId);
        assertNotNull(neighbors);
        assertTrue("至少 2 个邻居（Inventory + Redis）", neighbors.size() >= 2);

        System.out.println("[PASS] 查询 Order Service 的 neighbors: 共 " + neighbors.size() + " 条关系");
    }

    /**
     * 测试场景 3：Order Service 的 impact 应该包含 [Inventory Service, Redis Cluster, Linux Host]
     * 对齐 Blueprint §10.2
     */
    @Test
    public void should_analyze_impact() {
        applicationService.createRelationship(RelationshipType.CALLS, orderId, invId, null, 100.0, "MANUAL", null);
        applicationService.createRelationship(RelationshipType.DEPENDS_ON, orderId, redisId, null, 100.0, "MANUAL", null);
        applicationService.createRelationship(RelationshipType.RUNS_ON, redisId, hostId, null, 100.0, "MANUAL", null);

        List<com.aipe.relationship.domain.ResourceNode> impactNodes = topologyService.queryImpact(orderId);
        assertNotNull(impactNodes);
        // Order 的影响域需包含 inv (direct) 和 redis (direct) —— BFS 下游链
        assertTrue("至少 2 个影响节点", impactNodes.size() >= 2);

        System.out.println("[PASS] 影响分析: Order Service 故障会影响 " + impactNodes.size() + " 个 Resource");
    }

    /**
     * 测试场景 4：TopologyView 实时计算，每次新建，不存储
     * 对齐 Architecture Law-004
     */
    @Test
    public void should_build_topology_view_without_storing() {
        applicationService.createRelationship(RelationshipType.CALLS, orderId, invId, null, 100.0, "MANUAL", null);
        applicationService.createRelationship(RelationshipType.DEPENDS_ON, orderId, redisId, null, 100.0, "MANUAL", null);

        TopologyView view1 = topologyService.buildCurrent(orderId);
        TopologyView view2 = topologyService.buildCurrent(orderId);

        assertNotNull(view1);
        assertNotNull(view2);
        // 每次 build 生成不同的 topologyId（不存储）
        assertNotEquals("TopologyView 每次生成新 ID（不存储）", view1.getTopologyId(), view2.getTopologyId());
        assertTrue("节点数 >= 3（order + inv + redis）", view1.getNodeCount() >= 3);

        System.out.println("[PASS] TopologyView 实时计算（不存储）: " + view1);
    }

    /**
     * 测试场景 5：非法 Relationship（引用不存在的 Resource）→ 应该被拒绝
     * 对齐 Blueprint §10.2
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_relationship_with_nonexistent_resource() {
        applicationService.createRelationship(
                RelationshipType.CALLS, orderId, "NONEXISTENT-RESOURCE-999",
                null, 100.0, "MANUAL", null);
    }

    /**
     * 测试场景 6：循环依赖检测（A→B，B→C，C→A 应该拒绝）
     * 对齐 Blueprint §9.2 + §10.2
     */
    @Test
    public void should_detect_circular_dependency() {
        // A→B（order → inv）
        applicationService.createRelationship(RelationshipType.DEPENDS_ON, orderId, invId, null, 100.0, "MANUAL", null);
        // B→C（inv → redis）
        applicationService.createRelationship(RelationshipType.DEPENDS_ON, invId, redisId, null, 100.0, "MANUAL", null);

        // C→A（redis → order）→ 应该被拒绝（形成环）
        try {
            applicationService.createRelationship(RelationshipType.DEPENDS_ON, redisId, orderId, null, 100.0, "MANUAL", null);
            fail("应该拒绝形成循环依赖的 Relationship");
        } catch (IllegalArgumentException e) {
            assertTrue("异常消息应该提到循环",
                    e.getMessage().toLowerCase().contains("circular")
                            || e.getMessage().toLowerCase().contains("cycle"));
            System.out.println("[PASS] 循环依赖被拒绝: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private String createTestResource(String name, String businessSystem) {
        String id = name + "-" + UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO resource (resource_id, resource_name, resource_type, resource_status, business_system, environment, version, deleted) " +
                        "VALUES (?, ?, ?, 'RUNNING', ?, 'test', 1, 0)",
                id, name, "APPLICATION", businessSystem);
        return id;
    }

    private void deleteResource(String resourceId) {
        if (resourceId == null) return;
        try {
            jdbcTemplate.update("DELETE FROM resource WHERE resource_id = ?", resourceId);
        } catch (Exception ignored) {
        }
    }
}
