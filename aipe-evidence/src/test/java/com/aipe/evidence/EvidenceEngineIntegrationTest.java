package com.aipe.evidence;

import com.aipe.evidence.application.ConfidenceCalculator;
import com.aipe.evidence.application.EvidenceEngine;
import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.EvidenceStatus;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

import javax.sql.DataSource;
/**
 * Evidence Engine 集成测试（真实 MySQL evidence + ClickHouse observation_fact）
 *
 * <p>对齐 WP014 Blueprint §10.2 Test Scenario
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EvidenceApplication.class)
@ActiveProfiles("test")
public class EvidenceEngineIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(EvidenceEngineIntegrationTest.class);

    @Autowired
    private EvidenceEngine evidenceEngine;

    @Autowired
    private ConfidenceCalculator confidenceCalculator;

    private static final String TEST_RESOURCE_ID = "order-service-evidence-test";

    @Autowired
    @Qualifier("clickhouseDataSource")
    private DataSource clickhouseDataSource;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    private JdbcTemplate chJdbc;
    private JdbcTemplate mysqlJdbc;

    @Before
    public void setUp() {
        log.info("=== Evidence Integration Test Setup ===");
        chJdbc = new JdbcTemplate(clickhouseDataSource);
        mysqlJdbc = new JdbcTemplate(mysqlDataSource);
        // 确保 evidence 表存在
        initEvidenceTable();
        insertTestObservations();
    }

    private void initEvidenceTable() {
        mysqlJdbc.execute("DROP TABLE IF EXISTS evidence");
        mysqlJdbc.execute(
                "CREATE TABLE evidence (" +
                        "id VARCHAR(64) PRIMARY KEY," +
                        "evidence_type VARCHAR(32) NOT NULL," +
                        "title VARCHAR(256) NOT NULL," +
                        "description TEXT," +
                        "root_resource_id VARCHAR(64) NOT NULL," +

                        "observation_ids JSON," +
                        "relationship_ids JSON," +
                        "timeline_id VARCHAR(64)," +
                        "confidence DOUBLE NOT NULL DEFAULT 50.0," +
                        "reasoning_steps JSON," +
                        "status VARCHAR(16) NOT NULL DEFAULT 'NEW'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "version INT NOT NULL DEFAULT 1" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    @After
    public void tearDown() {
        chJdbc.update("DELETE FROM observation_fact WHERE resource_id = ?", TEST_RESOURCE_ID);
    }

    /** 插入 6 个超阈值 CPU 点（> 80%） */
    private void insertTestObservations() {
        long base = System.currentTimeMillis() - 3600_000L;
        for (int i = 0; i < 6; i++) {
            long ts = base + i * 300_000L; // 每 5 分钟
            double value = 80.0 + i * 3.0; // 80, 83, 86, 89, 92, 95
            insertObservation(TEST_RESOURCE_ID, "cpu.usage", value, "%", ts);
        }
        // 强制 sync
        try { chJdbc.execute("SYSTEM FLUSH LOGS"); } catch (Exception ignored) {}
    }

    private void insertObservation(String resourceId, String metricName, double value, String unit, long timestamp) {
        BigDecimal ts = new BigDecimal(timestamp / 1000 + "." + String.format("%03d", timestamp % 1000));
        chJdbc.update(
                "INSERT INTO observation_fact (" +
                        "observation_id, resource_id, resource_type, metric_name, metric_type, " +
                        "metric_value, unit, source, connector_id, labels, payload, timestamp, received_at) " +
                        "VALUES (?, ?, ?, ?, 'METRIC', ?, ?, 'JVM', '', '{}', '', toDateTime64(?, 3), now64(3))",
                UUID.randomUUID().toString(), resourceId, "APPLICATION",
                metricName, value, unit, ts);
    }

    /**
     * 测试场景 1：异常 Timeline 触发 Evidence 生成（CPU 超阈值 → 生成 Performance Evidence）
     */
    @Test
    public void should_generate_cpu_evidence() {
        long now = System.currentTimeMillis();
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now);

        assertNotNull("应生成 Evidence", evidence);
        assertNotNull(evidence.getEvidenceId());
        assertTrue("Evidence 应引用 Observation", evidence.referencesObservations());
        assertTrue("confidence 应在 0~100", evidence.getConfidence() >= 0 && evidence.getConfidence() <= 100);
        assertTrue("Evidence ID 不要是临时丢失的", evidence.getEvidenceId().getValue() != null);
        assertEquals(EvidenceStatus.NEW, evidence.getStatus());

        System.out.println("[PASS] Evidence 生成: id=" + evidence.getEvidenceId().getValue()
                + ", confidence=" + String.format("%.1f%%", evidence.getConfidence())
                + ", obs_count=" + evidence.getObservationIds().size());
    }

    /**
     * 测试场景 2：Evidence 落 MySQL（不是 ClickHouse）
     */
    @Test
    public void should_persist_evidence_to_mysql() {
        long now = System.currentTimeMillis();
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now);
        assertNotNull(evidence);

        Evidence reloaded = evidenceEngine.findById(evidence.getEvidenceId()).orElse(null);
        assertNotNull("应能从 MySQL 重新加载 Evidence", reloaded);
        assertEquals(evidence.getEvidenceId(), reloaded.getEvidenceId());
        assertNotNull("reasoningSteps 应持久化", reloaded.getReasoningSteps());
        assertFalse(reloaded.getReasoningSteps().isEmpty());

        System.out.println("[PASS] Evidence 落 MySQL 持久化验证通过");
    }

    /**
     * 测试场景 3：置信度计算（不是硬编码 100）
     */
    @Test
    public void should_calculate_confidence_dynamically() {
        double confidence = confidenceCalculator.calculateConfidence(80.0, 6, 6, 6);
        assertTrue("confidence 应在 50~100", confidence >= 50.0 && confidence <= 100.0);
        assertFalse("confidence 不应是固定值 100（应动态计算）", confidence == 100.0 && 6 < 10);
        System.out.println("[PASS] 动态置信度: " + String.format("%.1f%%", confidence));
    }

    /**
     * 测试场景 4：explain() 返回推理过程自然语言
     */
    @Test
    public void should_return_explanation() {
        long now = System.currentTimeMillis();
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now);
        assertNotNull(evidence);

        String explanation = evidence.explain();
        assertNotNull(explanation);
        assertFalse(explanation.isEmpty());
        assertTrue("应包含推理关键词", explanation.contains("推理步骤") || explanation.contains("根资源"));

        System.out.println("[PASS] explain() 返回自然语言:\n" + explanation);
    }

    /**
     * 测试场景 5：Evidence 验证（通过 → VERIFIED）；失败 → REJECTED
     */
    @Test
    public void should_verify_evidence() {
        long now = System.currentTimeMillis();
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now);
        assertNotNull(evidence);

        // 验证通过
        boolean verified = evidenceEngine.verifyEvidence(evidence.getEvidenceId(), true);
        assertTrue(verified);

        Evidence reloaded = evidenceEngine.findById(evidence.getEvidenceId()).orElse(null);
        assertNotNull(reloaded);
        assertEquals(EvidenceStatus.VERIFIED, reloaded.getStatus());

        // 再拒绝
        evidenceEngine.verifyEvidence(evidence.getEvidenceId(), false);
        Evidence rejected = evidenceEngine.findById(evidence.getEvidenceId()).orElse(null);
        assertEquals(EvidenceStatus.REJECTED, rejected.getStatus());

        System.out.println("[PASS] Evidence 验证（VERIFIED / REJECTED）");
    }

    /**     * 测试场景 6：Illegal Evidence（confidence=100 但无 reasoning steps）→ 拒绝
     */
    /**
     * 测试场景 6：Illegal Evidence（confidence=100 但无 reasoning steps）→ 拒绝
     * 对齐 Blueprint §9.2 - Confidence 不能固定 100（必须动态计算）
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_illegal_evidence_without_reasoning_steps() {
        com.aipe.evidence.domain.EvidenceBuilder.build(
                EvidenceId.generate(),
                com.aipe.evidence.domain.EvidenceType.PERFORMANCE,
                "Test",
                "Test",
                TEST_RESOURCE_ID,
                Arrays.asList("obs1"), null, null,
                100.0,  // 高置信度但无推理步骤 → Specification 应拒绝
                java.util.Collections.emptyList(),
                EvidenceStatus.NEW);
    }

    /**
     * 测试场景 7：Evidence 引用 Observation（必须有 observationIds 非空 - Law-002）
     */
    @Test
    public void evidence_should_reference_observations() {
        long now = System.currentTimeMillis();
        Evidence evidence = evidenceEngine.generateFromAnomaly(
                TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now);
        assertNotNull(evidence);
        assertTrue("Evidence 必须引用 Observation（对齐 Law-002）", evidence.referencesObservations());
        assertFalse("observationIds 不应为空", evidence.getObservationIds().isEmpty());
        System.out.println("[PASS] Evidence 引用 Observation 数: " + evidence.getObservationIds().size());
    }
}
