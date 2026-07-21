package com.aipe.knowledge;

import com.aipe.knowledge.application.KnowledgeEngine;
import com.aipe.knowledge.domain.Knowledge;
import com.aipe.knowledge.domain.KnowledgeId;
import com.aipe.knowledge.domain.KnowledgeType;
import com.aipe.knowledge.domain.Recommendation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Knowledge Engine 集成测试（真实 MySQL knowledge 表）
 *
 * <p>对齐 WP016 Blueprint §10.2 Test Scenario
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KnowledgeApplication.class)
@ActiveProfiles("test")
public class KnowledgeEngineIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeEngineIntegrationTest.class);

    @Autowired
    private KnowledgeEngine knowledgeEngine;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    private JdbcTemplate jdbc;

    @Before
    public void setUp() {
        log.info("=== Knowledge Integration Test Setup ===");
        jdbc = new JdbcTemplate(mysqlDataSource);
        initKnowledgeTable();
    }

    @After
    public void tearDown() {
        jdbc.execute("DROP TABLE IF EXISTS knowledge");
    }

    private void initKnowledgeTable() {
        jdbc.execute("DROP TABLE IF EXISTS knowledge");
        jdbc.execute(
                "CREATE TABLE knowledge (" +
                        "pk_id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "id VARCHAR(64) NOT NULL," +
                        "title VARCHAR(256) NOT NULL," +
                        "description TEXT," +
                        "knowledge_type VARCHAR(32) NOT NULL," +
                        "evidence_id VARCHAR(64) NOT NULL," +
                        "verification_id VARCHAR(64)," +
                        "confidence DOUBLE NOT NULL DEFAULT 50.0," +
                        "applicable_conditions JSON," +
                        "recommendation JSON," +
                        "success_rate DOUBLE NOT NULL DEFAULT 0.0," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "version INT NOT NULL DEFAULT 1," +
                        "INDEX idx_knowledge_id (id)," +
                        "UNIQUE uk_knowledge_version (id, version)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    /**
     * 测试场景 1：Verified Evidence → build Knowledge（knowledgeId + version 1 + evidenceId 引用）
     */
    @Test
    public void should_build_knowledge_from_verified_evidence() {
        Knowledge knowledge = knowledgeEngine.buildKnowledge(
                "Redis 连接池耗尽导致 TPS 下降",
                "当 Redis 连接池达到上限时，Order Service TPS 显著下降",
                KnowledgeType.BOTTLENECK,
                "EVIDENCE-" + UUID.randomUUID(),  // evidenceId
                "VERIFY-" + UUID.randomUUID(),
                85.0, 75.0,
                "REDIS", "connection.pool",
                "增加 Redis 连接池上限从 100 到 300",
                "预期 TPS 提升 ~40%");

        assertNotNull(knowledge);
        assertNotNull(knowledge.getKnowledgeId());
        assertEquals(1, knowledge.getVersion());
        assertNotNull(knowledge.getEvidenceId());
        assertNotNull(knowledge.getEvidenceId());
        assertFalse(knowledge.getEvidenceId().isEmpty());

        System.out.println("[PASS] Knowledge 构建: id=" + knowledge.getKnowledgeId().getValue()
                + ", v=" + knowledge.getVersion()
                + ", evidenceId=" + knowledge.getEvidenceId());
    }

    /**
     * 测试场景 2：Knowledge 升级（新 ChangeSet → 新 version 2 + 旧 version 1 保留）
     */
    @Test
    public void should_upgrade_knowledge() {
        Knowledge v1 = knowledgeEngine.buildKnowledge(
                "CPU 超阈值应对方案", "CPU 持续高位 Knowledge",
                KnowledgeType.BOTTLENECK, "EVIDENCE-V1", "VERIFY-1",
                80.0, 70.0, "APPLICATION", "cpu.usage",
                "扩容节点", "预期 CPU 下降 30%");
        assertNotNull(v1);

        Map<String, String> changeSet = new HashMap<>();
        changeSet.put("resourceType", "APPLICATION");
        changeSet.put("recommendationAction", "扩容节点 + 增加 CPU 限制");
        changeSet.put("expectedEffect", "预期 CPU 下降 50%");

        Knowledge v2 = knowledgeEngine.verifyAndUpgrade(v1.getKnowledgeId(), changeSet, "EVIDENCE-V2", 90.0);
        assertNotNull(v2);
        assertEquals(2, v2.getVersion());

        // 验证版本 history
        List<Knowledge> versions = knowledgeEngine.listVersions(v1.getKnowledgeId());
        assertEquals("应包含 v1 + v2 两个版本", 2, versions.size());

        System.out.println("[PASS] Knowledge 升级: v1 → v2, history 包含 " + versions.size() + " 个版本");
    }

    /**
     * 测试场景 3：Knowledge 推荐应用于 Resource（applicableConditions 匹配 → 返回 recommendation）
     */
    @Test
    public void should_recommend_knowledge_for_resource() {
        Knowledge knowledge = knowledgeEngine.buildKnowledge(
                "Redis 连接池优化", "Redis 连接池优化经验",
                KnowledgeType.BOTTLENECK, "EVIDENCE-REC", "VERIFY-REC",
                85.0, 80.0, "REDIS", "connection.pool",
                "增加连接池上限", "预期 TPS 提升");

        // 匹配的 Resource 类型
        Recommendation rec = knowledgeEngine.recommendForResource(
                knowledge.getKnowledgeId(), "REDIS", "connection.pool");
        assertNotNull("应返回推荐方案", rec);
        assertFalse(rec.getAction().isEmpty());

        // 不匹配的 Resource 类型
        Recommendation notApplicable = knowledgeEngine.recommendForResource(
                knowledge.getKnowledgeId(), "MYSQL", "cpu.usage");
        assertNull("不匹配的 Resource 类型应返回 null", notApplicable);

        System.out.println("[PASS] Knowledge 推荐: 匹配时 action=" + rec.getAction());
    }

    /**
     * 测试场景 4：Knowledge 版本 history 查询（返回 [v1, v2]）
     */
    @Test
    public void should_list_knowledge_versions() {
        Knowledge v1 = knowledgeEngine.buildKnowledge(
                "测试知识", "测试", KnowledgeType.DEPENDENCY,
                "EVIDENCE-HIST", "VERIFY-HIST", 70.0, 60.0,
                "SERVICE", "latency", "优化延迟", "预期下降");

        Map<String, String> changeSet = new HashMap<>();
        changeSet.put("action", "升级方案");
        knowledgeEngine.verifyAndUpgrade(v1.getKnowledgeId(), changeSet, "EVIDENCE-HIST-2", 75.0);

        List<Knowledge> versions = knowledgeEngine.listVersions(v1.getKnowledgeId());
        assertTrue("应至少 2 个版本", versions.size() >= 2);

        System.out.println("[PASS] Knowledge 版本 history: " + versions.size() + " 个版本");
    }

    /**
     * 测试场景 5：Illegal Knowledge（无 Evidence）→ 拒绝构建
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_knowledge_without_evidence() {
        knowledgeEngine.buildKnowledge(
                "无来源知识", "违反 Domain Law-001",
                KnowledgeType.AI,
                null,  // evidenceId 为空 - 违反 Domain Law-001
                null, 50.0, 0.0, null, null, null, null);
    }

    /**
     * 测试场景 6：Knowledge 落 MySQL（不是 ClickHouse）
     */
    @Test
    public void should_persist_knowledge_to_mysql() {
        Knowledge knowledge = knowledgeEngine.buildKnowledge(
                "MySQL 持久化测试", "测试",
                KnowledgeType.DEPENDENCY, "EVIDENCE-MYSQL", "VERIFY-MYSQL",
                75.0, 65.0, "SERVICE", "qps", "优化 QPS", "预期提升");

        Knowledge latest = knowledgeEngine.findLatest(knowledge.getKnowledgeId()).orElse(null);
        assertNotNull("应能从 MySQL 重新加载", latest);
        assertEquals(knowledge.getKnowledgeId(), latest.getKnowledgeId());
        assertEquals(knowledge.getVersion(), latest.getVersion());

        System.out.println("[PASS] Knowledge 落 MySQL 持久化验证通过");
    }

    /**
     * 测试场景 7：跨 Resource 类型复用（Resource 类型匹配 + 适用条件 match → 推荐）
     */
    @Test
    public void should_support_cross_resource_reuse() {
        // 创建无特定 Resource 类型限制的 Knowledge（适用所有类型）
        Knowledge knowledge = knowledgeEngine.buildKnowledge(
                "通用性能优化", "适用于所有 Resource 的通用优化",
                KnowledgeType.OPTIMIZATION, "EVIDENCE-CROSS", "VERIFY-CROSS",
                80.0, 70.0,
                null, null,  // 无特定 Resource 类型限制
                "检查资源使用率", "预期性能提升");

        // 任何 Resource 类型都应适用
        Recommendation rec = knowledgeEngine.recommendForResource(
                knowledge.getKnowledgeId(), "REDIS", "cpu.usage");
        assertNotNull("通用 Knowledge 应适用于任何 Resource 类型", rec);

        System.out.println("[PASS] 跨 Resource 类型复用: 通用 Knowledge 适用所有类型");
    }
}
