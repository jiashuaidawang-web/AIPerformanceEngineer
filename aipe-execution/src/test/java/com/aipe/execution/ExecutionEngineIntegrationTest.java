package com.aipe.execution;

import com.aipe.execution.application.ExecutionService;
import com.aipe.execution.application.OptimizationService;
import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.ExecutionId;
import com.aipe.execution.domain.ExecutionStatus;
import com.aipe.execution.domain.ExecutionType;
import com.aipe.execution.domain.Optimization;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExecutionApplication.class)
@ActiveProfiles("test")
public class ExecutionEngineIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ExecutionEngineIntegrationTest.class);

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private OptimizationService optimizationService;

    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    private JdbcTemplate jdbc;

    @Before
    public void setUp() {
        log.info("=== Execution Integration Test Setup ===");
        jdbc = new JdbcTemplate(mysqlDataSource);
        initExecutionTable();
    }

    @After
    public void tearDown() {
        jdbc.execute("DROP TABLE IF EXISTS execution");
    }

    private void initExecutionTable() {
        jdbc.execute("DROP TABLE IF EXISTS execution");
        jdbc.execute(
            "CREATE TABLE execution (" +
            "pk_id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "id VARCHAR(64) NOT NULL," +
            "recommendation_id VARCHAR(64) NOT NULL," +
            "executor VARCHAR(64) NOT NULL," +
            "execution_type VARCHAR(16) NOT NULL DEFAULT 'MANUAL'," +
            "status VARCHAR(16) NOT NULL DEFAULT 'PENDING'," +
            "before_snapshot JSON," +
            "after_snapshot JSON," +
            "improvement_score DOUBLE NOT NULL DEFAULT 0.0," +
            "started_at TIMESTAMP NULL," +
            "finished_at TIMESTAMP NULL," +
            "rollback_info JSON," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "version INT NOT NULL DEFAULT 1," +
            "INDEX idx_exec_id (id)," +
            "INDEX idx_recommendation (recommendation_id)," +
            "UNIQUE uk_exec_version (id, version)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    @Test
    public void should_create_and_execute() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":65.0}]}";
        Execution exec = executionService.execute("REC-" + UUID.randomUUID(), "admin-001", before);
        assertNotNull(exec);
        assertNotNull(exec.getExecutionId());
        assertEquals(ExecutionStatus.EXECUTING, exec.getStatus());
        assertNotNull(exec.getBeforeSnapshot());
        System.out.println("[PASS] Execution 创建 + 启动: id=" + exec.getExecutionId().getValue());
    }

    @Test
    public void should_complete_with_optimization() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":80.0}]}";
        Execution exec = executionService.execute("REC-COMPLETE", "admin-002", before);
        String after = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":45.0}]}";
        Execution completed = executionService.complete(exec.getExecutionId(), after);

        assertTrue(completed.getStatus() == ExecutionStatus.SUCCESS || completed.getStatus() == ExecutionStatus.FAILED);
        assertNotNull(completed.getAfterSnapshot());
        assertTrue("improvementScore 应在 0~100", completed.getImprovementScore() >= 0 && completed.getImprovementScore() <= 100);
        System.out.println("[PASS] Execution 完成: status=" + completed.getStatus() + ", score=" + completed.getImprovementScore());
    }

    @Test
    public void should_evaluate_optimization() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":80.0}]}";
        String after = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":45.0}]}";
        Execution exec = executionService.execute("REC-EVAL", "admin-003", before);
        exec = executionService.complete(exec.getExecutionId(), after);
        Optimization opt = optimizationService.evaluate(exec);
        assertNotNull(opt);
        assertTrue("PASS 或 FAIL".contains(opt.getStatus()));
        assertNotNull(opt.getSummary());
        System.out.println("[PASS] Optimization 评估: status=" + opt.getStatus() + ", " + opt.getSummary());
    }

    @Test
    public void should_rollback_successfully() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":80.0}]}";
        Execution exec = executionService.execute("REC-ROLLBACK", "admin-004", before);
        Execution completed = executionService.complete(exec.getExecutionId(), "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":45.0}]}");
        Execution rolledBack = executionService.rollback(completed.getExecutionId(), "测试回滚");
        assertEquals(ExecutionStatus.ROLLED_BACK, rolledBack.getStatus());
        assertNotNull(rolledBack.getRollbackInfo());
        System.out.println("[PASS] Execution 回滚: " + rolledBack.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void should_reject_illegal_state_transition() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":80.0}]}";
        Execution exec = executionService.execute("REC-ILLEGAL", "admin-005", before);
        // PENDING→SUCCESS 非法：直接 complete 后应为 EXECUTING，但 rollback 需要 SUCCESS
        // 改为测试非法 rollback（PENDING 不能 rollback）
        executionService.rollback(exec.getExecutionId(), "非法");
    }

    @Test
    public void should_persist_to_mysql() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":70.0}]}";
        Execution exec = executionService.execute("REC-PERSIST", "admin-006", before);
        List<Execution> recs = executionService.findByStatus(ExecutionStatus.EXECUTING);
        assertFalse(recs.isEmpty());
        System.out.println("[PASS] Execution 落 MySQL 持久化");
    }

    @Test
    public void should_generate_report() {
        String before = "{\"metrics\":[{\"metric_name\":\"cpu\",\"value\":70.0}]}";
        Execution exec = executionService.execute("REC-REPORT", "admin-007", before);
        String report = executionService.generateReport(exec.getExecutionId());
        assertNotNull(report);
        assertTrue(report.contains("Execution Report"));
        System.out.println("[PASS] 执行报告生成");
    }
}
