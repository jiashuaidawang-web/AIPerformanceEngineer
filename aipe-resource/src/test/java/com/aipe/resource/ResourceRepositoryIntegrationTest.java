package com.aipe.resource;

import com.aipe.resource.application.ResourceLifecycleManager;
import com.aipe.resource.application.ResourceQueryService;
import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceName;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;
import com.aipe.resource.domain.ResourceCategory;
import com.aipe.resource.infrastructure.mapper.ResourceMapper;
import com.aipe.resource.infrastructure.po.ResourcePO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * ResourceRepository 集成测试（真实 MySQL）
 *
 * <p>对齐 WP011 Blueprint 验收场景：
 * <ul>
 *   <li>创建"订单"业务域下多个 Resource</li>
 *   <li>查询"订单"业务域返回正确数量</li>
 *   <li>资源状态流转合法</li>
 *   <li>非法操作被拒绝</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourceApplication.class)
@ActiveProfiles("test")
public class ResourceRepositoryIntegrationTest {

    @Autowired
    private ResourceLifecycleManager lifecycleManager;

    @Autowired
    private ResourceQueryService queryService;

    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 每个测试前清理测试数据
     */
    @Before
    public void setUp() {
        // 清理所有测试数据（按业务系统查询后删除）
        List<Resource> existing = queryService.findByBusinessSystem("测试订单系统");
        existing.forEach(r -> lifecycleManager.deleteResource(r.getId()));
    }

    /**
     * 每个测试后清理
     */
    @After
    public void tearDown() {
        List<Resource> existing = queryService.findByBusinessSystem("测试订单系统");
        existing.forEach(r -> lifecycleManager.deleteResource(r.getId()));
    }

    /**
     * 测试场景 1：创建"订单"业务域下 3 个 Resource
     * 对齐 WP011 Blueprint 11.2 Test Scenario
     */
    @Test
    public void should_create_three_resources_in_order_domain() {
        // 创建 订单服务
        Resource orderService = createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        // 创建 订单MySQL集群
        Resource orderMysql = createTestResource("测试订单系统", "order-mysql-001", ResourceType.MYSQL);
        // 创建 订单Redis集群
        Resource orderRedis = createTestResource("测试订单系统", "order-redis-001", ResourceType.REDIS);

        assertNotNull(orderService);
        assertNotNull(orderMysql);
        assertNotNull(orderRedis);
        assertNotNull(orderService.getId());
        assertNotNull(orderMysql.getId());
        assertNotNull(orderRedis.getId());

        // 验证业务归属
        assertTrue(orderService.belongsTo("测试订单系统"));
        assertTrue(orderMysql.belongsTo("测试订单系统"));
        assertTrue(orderRedis.belongsTo("测试订单系统"));

        System.out.println("[PASS] 创建" + orderService.getId().getValue() + " / " + orderMysql.getId().getValue() + " / " + orderRedis.getId().getValue() + ")");
    }

    /**
     * 测试场景 2：查询"订单"业务域返回 3 个 Resource
     * 对齐 WP011 Blueprint 11.2 Test Scenario
     */
    @Test
    public void should_query_order_domain_return_three_resources() {
        // 先创建 3 个资源
        createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        createTestResource("测试订单系统", "order-mysql-001", ResourceType.MYSQL);
        createTestResource("测试订单系统", "order-redis-001", ResourceType.REDIS);

        // 查询
        List<Resource> resources = queryService.findByBusinessSystem("测试订单系统");
        assertNotNull(resources);
        assertEquals(3, resources.size());

        System.out.println("[PASS] 查询业务域返回 " + resources.size() + " 个资源");
    }

    /**
     * 测试场景 3：资源状态流转合法（RUNNING → MAINTENANCE → RUNNING）
     * 对齐 WP011 Blueprint State Machine
     */
    @Test
    public void should_allow_valid_status_transition() {
        Resource resource = createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        assertTrue(resource.isRunning());

        // RUNNING → MAINTENANCE
        Resource updated = lifecycleManager.updateResourceStatus(resource.getId(), ResourceStatus.MAINTENANCE);
        assertEquals(ResourceStatus.MAINTENANCE, updated.getStatus());

        // MAINTENANCE → RUNNING
        Resource restored = lifecycleManager.updateResourceStatus(resource.getId(), ResourceStatus.RUNNING);
        assertEquals(ResourceStatus.RUNNING, restored.getStatus());

        System.out.println("[PASS] 状态流转: RUNNING → MAINTENANCE → RUNNING");
    }

    /**
     * 测试场景 4：非法状态流转被拒绝（STOPPED → MAINTENANCE 非法）
     */
    @Test(expected = IllegalStateException.class)
    public void should_reject_invalid_status_transition() {
        Resource resource = createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        // RUNNING → STOPPED（合法）
        lifecycleManager.updateResourceStatus(resource.getId(), ResourceStatus.STOPPED);
        // STOPPED → MAINTENANCE（非法）- 应抛出异常
        lifecycleManager.updateResourceStatus(resource.getId(), ResourceStatus.MAINTENANCE);
    }

    /**
     * 测试场景 5：无 businessDomain 的资源被拒绝（Law-001）
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_resource_without_business_system() {
        // 直接构造一个无 businessSystem 的资源
        Resource orphan = com.aipe.resource.domain.ResourceFactory.create(
                ResourceName.of("orphan"),
                ResourceType.UNKNOWN,
                ResourceCategory.INFRA,
                null, // businessSystem 为 null
                null, null, null, null,
                new HashMap<>(), new HashMap<>()
        );
        lifecycleManager.createResource(orphan);
    }

    /**
     * 测试场景 6：按类型查询
     */
    @Test
    public void should_find_resources_by_type() {
        createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        createTestResource("测试订单系统", "order-service-002", ResourceType.APPLICATION);
        createTestResource("测试订单系统", "order-mysql-001", ResourceType.MYSQL);

        List<Resource> applications = queryService.findByType(ResourceType.APPLICATION);
        assertTrue(applications.size() >= 2);

        List<Resource> mysqls = queryService.findByType(ResourceType.MYSQL);
        assertTrue(mysqls.size() >= 1);

        System.out.println("[PASS] 按类型查询: APPLICATION=" + applications.size() + ", MYSQL=" + mysqls.size());
    }

    /**
     * 测试场景 7：逻辑删除
     */
    @Test
    public void should_logically_delete_resource() {
        Resource resource = createTestResource("测试订单系统", "order-service-001", ResourceType.APPLICATION);
        ResourceId id = resource.getId();

        // 删除前可查询
        assertNotNull(queryService.findById(id));

        // 删除
        boolean deleted = lifecycleManager.deleteResource(id);
        assertTrue(deleted);

        // 删除后查不到
        assertNull(queryService.findById(id));

        System.out.println("[PASS] 逻辑删除成功: " + id);
    }

    // ==================== 辅助方法 ====================

    private Resource createTestResource(String businessSystem, String name, ResourceType type) {
        Map<String, String> labels = new HashMap<>();
        labels.put("env", "test");
        labels.put("owner", "test-runner");

        Map<String, String> attributes = new HashMap<>();
        attributes.put("host", "192.168.1.1");
        attributes.put("port", "8080");

        Resource resource = com.aipe.resource.domain.ResourceFactory.create(
                ResourceName.of(name),
                type,
                ResourceCategory.BUSINESS,
                businessSystem,
                null, null, null, "test",
                labels, attributes
        );
        return lifecycleManager.createResource(resource);
    }
}
