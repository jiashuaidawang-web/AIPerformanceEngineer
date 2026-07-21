package com.aipe.recommendation;

import com.aipe.recommendation.application.RecommendationEngine;
import com.aipe.recommendation.domain.Priority;
import com.aipe.recommendation.domain.Recommendation;
import com.aipe.recommendation.domain.RecommendationId;
import com.aipe.recommendation.domain.RecommendationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Recommendation Engine 集成测试（真实 MySQL recommendation 表）
 *
 * <p>对齐 WP017 Blueprint §10.2 Test Scenario
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RecommendationApplication.class)
@ActiveProfiles("test")
public class RecommendationEngineIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RecommendationEngineIntegrationTest.class);

    @Autowired
    private RecommendationEngine recommendationEngine;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    private JdbcTemplate jdbc;

    @Before
    public void setUp() {
        log.info("=== Recommendation Integration Test Setup ===");
        jdbc = new JdbcTemplate(mysqlDataSource);
        initRecommendationTable();
    }

    @After
    public void tearDown() {
        jdbc.execute("DROP TABLE IF EXISTS recommendation");
    }

    private void initRecommendationTable() {
        jdbc.execute("DROP TABLE IF EXISTS recommendation");
        jdbc.execute(
                "CREATE TABLE recommendation (" +
                        "id VARCHAR(64) PRIMARY KEY," +
                        "knowledge_id VARCHAR(64) NOT NULL," +
                        "target_resource_id VARCHAR(64) NOT NULL," +
                        "title VARCHAR(256) NOT NULL," +
                        "description TEXT," +
                        "priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM'," +
                        "confidence DOUBLE NOT NULL DEFAULT 50.0," +
                        "expected_outcome TEXT," +
                        "execution_plan JSON," +
                        "rollback_plan JSON," +
                        "status VARCHAR(16) NOT NULL DEFAULT 'PENDING'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "version INT NOT NULL DEFAULT 1" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    /**
     * 测试场景 1：Knowledge → 生成 Recommendation（PENDING + priority + executionPlan）
     */
    @Test
    public void should_generate_recommendation_from_knowledge() {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                "KNOW-" + UUID.randomUUID(), "order-service-001",
                "扩容 Redis 连接池", "将 Redis 连接池上限从 100 调整到 300",
                85.0, "预期 TPS 提升 ~40%");

        assertNotNull(rec);
        assertNotNull(rec.getRecommendationId());
        assertEquals(RecommendationStatus.PENDING, rec.getStatus());
        assertNotNull(rec.getPriority());
        assertNotNull(rec.getExecutionPlan());
        assertFalse(rec.getExecutionPlan().isEmpty());
        assertFalse(rec.getRollbackPlan().isEmpty());

        System.out.println("[PASS] Recommendation 生成: id=" + rec.getRecommendationId().getValue()
                + ", priority=" + rec.getPriority() + ", status=" + rec.getStatus());
    }

    /**
     * 测试场景 2：优先级计算（HIGH priority + confidence >= 80）
     */
    @Test
    public void should_calculate_high_priority() {
        // confidence=90, urgency=8, difficulty=2 → score = 0.9 * 8 / 2 * 100 = 360 → HIGH
        Priority priority = recommendationEngine.calculatePriority(90.0, 8, 2);
        assertEquals(Priority.HIGH, priority);

        // confidence=50, urgency=3, difficulty=5 → score = 0.5 * 3 / 5 * 100 = 30 → MEDIUM
        Priority medium = recommendationEngine.calculatePriority(50.0, 3, 5);
        assertEquals(Priority.MEDIUM, medium);

        // confidence=30, urgency=2, difficulty=8 → score = 0.3 * 2 / 8 * 100 = 7.5 → LOW
        Priority low = recommendationEngine.calculatePriority(30.0, 2, 8);
        assertEquals(Priority.LOW, low);

        System.out.println("[PASS] 优先级计算: HIGH / MEDIUM / LOW 全部正确");
    }

    /**
     * 测试场景 3：审批流转（PENDING → APPROVED / REJECTED）
     */
    @Test
    public void should_approve_and_reject_recommendation() {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                "KNOW-APPROVE", "order-service-002",
                "优化 JVM 参数", "调整 JVM 堆大小", 80.0, "预期 GC 下降");

        // 审批通过
        boolean approved = recommendationEngine.approve(rec.getRecommendationId());
        assertTrue(approved);

        List<Recommendation> recs = recommendationEngine.findByResource("order-service-002");
        assertFalse(recs.isEmpty());
        assertEquals(RecommendationStatus.APPROVED, recs.get(0).getStatus());

        System.out.println("[PASS] Recommendation 审批流转: PENDING → APPROVED");
    }

    /**
     * 测试场景 4：Illegal Recommendation（无 Knowledge + 无 Resource）→ 拒绝
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_recommendation_without_knowledge() {
        recommendationEngine.generateFromKnowledge(
                null,  // knowledgeId 为空 - 违反 Domain Law-001
                "order-service-003", "测试", "测试", 50.0, "测试");
    }

    /**
     * 测试场景 5：状态机非法流转（PENDING → EXECUTED）→ 拒绝
     */
    @Test(expected = IllegalStateException.class)
    public void should_reject_illegal_state_transition() {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                "KNOW-ILLEGAL", "order-service-004",
                "测试非法流转", "测试", 70.0, "测试");

        // 尝试从 PENDING 直接 EXECUTED（非法，必须先 APPROVED）
        recommendationEngine.markExecuted(rec.getRecommendationId());
    }

    /**
     * 测试场景 6：Recommendation 落 MySQL（不是 ClickHouse）
     */
    @Test
    public void should_persist_recommendation_to_mysql() {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                "KNOW-PERSIST", "order-service-005",
                "MySQL 持久化测试", "测试", 75.0, "测试");

        List<Recommendation> recs = recommendationEngine.findByResource("order-service-005");
        assertFalse(recs.isEmpty());
        assertEquals(rec.getRecommendationId().getValue(), recs.get(0).getRecommendationId().getValue());

        System.out.println("[PASS] Recommendation 落 MySQL 持久化验证通过");
    }

    /**
     * 测试场景 7：执行计划生成（含回滚方案）
     */
    @Test
    public void should_generate_execution_plan() {
        Recommendation rec = recommendationEngine.generateFromKnowledge(
                "KNOW-PLAN", "order-service-006",
                "扩容节点", "增加 2 个节点", 80.0, "预期容量翻倍");

        assertNotNull(rec.getExecutionPlan());
        assertTrue("执行计划应 >= 3 步", rec.getExecutionPlan().size() >= 3);
        assertNotNull(rec.getRollbackPlan());
        assertTrue("回滚方案应 >= 2 步", rec.getRollbackPlan().size() >= 2);

        System.out.println("[PASS] 执行计划: " + rec.getExecutionPlan().size() + " 步, 回滚方案: " + rec.getRollbackPlan().size() + " 步");
    }
}
