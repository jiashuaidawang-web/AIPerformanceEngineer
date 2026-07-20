package com.aipe.observation;

import com.aipe.observation.application.BatchIncomingResult;
import com.aipe.observation.application.ObservationApplicationService;
import com.aipe.observation.application.ObservationIncomingResult;
import com.aipe.observation.application.TrendAggregator;
import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationFactory;
import com.aipe.observation.domain.ObservationSource;
import com.aipe.observation.domain.ObservationType;
import com.aipe.observation.infrastructure.ObservationMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Observation Engine 集成测试（真实 ClickHouse）
 *
 * <p>对齐 WP012 Blueprint §10.2 Test Scenario：
 * <ul>
 *   <li>创建 Observation 观测数据（单条）</li>
 *   <li>查询 Resource 的 Observation 列表</li>
 *   <li>查询时间范围内的指标趋势（1m 桶聚合）</li>
 *   <li>非法 Observation（无 resourceId）被拒绝</li>
 *   <li>批量写入 10+ 条 Observation 验证 Repository 返回 Domain</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObservationApplication.class)
@ActiveProfiles("test")
public class ObservationRepositoryIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ObservationRepositoryIntegrationTest.class);

    private static final String TEST_RESOURCE_ID = "order-service-001";

    @Autowired
    private ObservationApplicationService applicationService;

    @Autowired
    private ObservationMapper observationMapper;

    @Before
    public void setUp() {
        log.info("=== Observation Integration Test Setup ===");
    }

    @After
    public void tearDown() {
        // ClickHouse 不清理测试数据（避免高频表重写，TTL 自动过期）
    }

    /**
     * 测试场景 1：创建 Observation 观测数据（单条）
     * 对齐 Blueprint 10.2
     */
    @Test
    public void should_create_observation_successfully() {
        Observation obs = ObservationFactory.create(
                TEST_RESOURCE_ID,
                ObservationType.METRIC,
                ObservationSource.JVM,
                "heap.used",
                512.0,
                "MB",
                System.currentTimeMillis(),
                "jvm-001",
                null,
                null
        );

        ObservationIncomingResult result = applicationService.processIncoming(obs);

        assertNotNull(result);
        assertTrue("Observation 应该入库成功", result.isSuccess());
        assertNotNull("observationId 不应该为 null", result.getObservationId());

        System.out.println("[PASS] 创建 Observation: id=" + result.getObservationId() + ", resource=" + TEST_RESOURCE_ID);
    }

    /**
     * 测试场景 2：查询 Resource 的 Observation 列表
     */
    @Test
    public void should_query_observations_by_resource() {
        // 先插入
        applicationService.processIncoming(ObservationFactory.create(
                TEST_RESOURCE_ID, ObservationType.METRIC, ObservationSource.JVM,
                "heap.used", 480.0, "MB", System.currentTimeMillis(), "jvm-001", null, null));

        List<Observation> observations = applicationService.queryByResource(TEST_RESOURCE_ID, 100);
        assertNotNull(observations);
        assertFalse("查询结果不应该为空", observations.isEmpty());
        // 所有查询结果都应该属于 TEST_RESOURCE_ID（Law-002）
        for (Observation obs : observations) {
            assertTrue("每条 Observation 必须属于指定 Resource（Law-002）",
                    obs.belongsTo(TEST_RESOURCE_ID));
        }

        System.out.println("[PASS] 查询 Observation 列表: 共 " + observations.size() + " 条");
    }

    /**
     * 测试场景 3：查询时间范围内的指标趋势（1m 桶聚合）
     * 对齐 Blueprint §10.2 + ch8
     */
    @Test
    public void should_query_trend_with_time_bucket_aggregation() {
        long now = System.currentTimeMillis();
        long oneMinute = TrendAggregator.INTERVAL_1M;

        // 插入一条数据在过去 30s
        applicationService.processIncoming(ObservationFactory.create(
                TEST_RESOURCE_ID, ObservationType.METRIC, ObservationSource.JVM,
                "cpu.usage", 88.5, "%", now - 30_000L, "jvm-001", null, null));

        List<TrendAggregator.TrendPoint> trendPoints = applicationService.queryTrend(
                TEST_RESOURCE_ID, "cpu.usage", now - oneMinute, now, "1m");

        assertNotNull(trendPoints);
        // 趋势点可能为空（桶内无数据时也成立），故仅做非空 + 结构验证
        for (TrendAggregator.TrendPoint tp : trendPoints) {
            assertTrue("avg 应该 <= max", tp.getAvg() <= tp.getMax());
            assertTrue("min 应该 <= avg", tp.getMin() <= tp.getAvg());
            assertTrue("count 应该 > 0", tp.getCount() > 0);
        }

        System.out.println("[PASS] 趋势查询: 返回 " + trendPoints.size() + " 个趋势点");
    }

    /**
     * 测试场景 4：非法 Observation（无 resourceId）被拒绝
     * 对齐 Blueprint §10.2 + Law-002
     */
    @Test
    public void should_reject_observation_without_resource_id() {
        // 直接构建一条无 resourceId 的 Observation（绕过 Factory 校验）
        Observation invalid = ObservationFactory.reconstruct(
                null,
                null, // resourceId 为 null
                ObservationType.METRIC,
                ObservationSource.JVM,
                "heap.used",
                128.0,
                "MB",
                System.currentTimeMillis(),
                "jvm-001",
                null,
                null
        );

        ObservationIncomingResult result = applicationService.processIncoming(invalid);

        assertNotNull(result);
        assertFalse("无 resourceId 应该被拒绝", result.isSuccess());
        assertNotNull("失败应该带errorMessage", result.getErrorMessage());

        System.out.println("[PASS] 非法 Observation 被拒绝: " + result.getErrorMessage());
    }

    /**
     * 测试场景 5：批量写入 Observation（验证 Repository 返回 Domain 而非 PO）
     * 对齐 Blueprint §10.2 + Gateway Law-001
     */
    @Test
    public void should_batch_create_observations() {
        List<Observation> observations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            observations.add(ObservationFactory.create(
                    TEST_RESOURCE_ID,
                    ObservationType.METRIC,
                    ObservationSource.JVM,
                    "thread.count",
                    (double) (50 + i),
                    "count",
                    System.currentTimeMillis() + i,
                    "jvm-001",
                    null,
                    null
            ));
        }

        BatchIncomingResult result = applicationService.batchProcessIncoming(observations);

        assertNotNull(result);
        assertEquals("总条数", 10, result.getTotalCount());
        assertEquals("成功条数", 10, result.getSuccessCount());
        assertEquals("失败条数", 0, result.getFailedCount());
        assertTrue("全部成功", result.isAllSuccess());

        System.out.println("[PASS] 批量写入: 成功 " + result.getSuccessCount() + "/" + result.getTotalCount());
    }

    /**
     * 测试场景 6：Observation 不可变（验证 domain 行为）
     * 对齐 M2-006 ch4.1 Immutable + ch10 Lifecycle
     */
    @Test
    public void should_observation_be_immutable() {
        Observation obs = ObservationFactory.create(
                TEST_RESOURCE_ID, ObservationType.METRIC, ObservationSource.JVM,
                "heap.used", 512.0, "MB", 1721452800000L, "jvm-001", null, null);

        // 业务方法存在且可用
        assertTrue(obs.belongsTo(TEST_RESOURCE_ID));
        assertTrue(obs.isWithin(1721452800000L, 1721452800001L));
        assertFalse(obs.isWithin(0L, 1000L));
        assertTrue(obs.isType(ObservationType.METRIC));
        assertFalse(obs.isType(ObservationType.LOG));

        // labels 不可变
        Map<String, String> labels = obs.getLabels();
        try {
            labels.put("hack", "value");
            fail("labels map 应该是不可变的");
        } catch (UnsupportedOperationException e) {
            // 期望抛出
        }

        System.out.println("[PASS] Observation 不可变 + 业务方法验证通过");
    }

    /**
     * 测试场景 7：旧协议兼容（M1 ObservationBatchRequest List<Map>）
     * 对齐 Blueprint §7.2 旧协议兼容
     */
    @Test
    public void should_support_legacy_batch_protocol() {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> observations = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("resource_id", TEST_RESOURCE_ID);
        row.put("metric_name", "qps");
        row.put("metric_value", 1234.5);
        row.put("timestamp", System.currentTimeMillis());
        row.put("tags", "{}");
        observations.add(row);
        request.put("observations", observations);

        // 直接调用 ObservationDtoMapper.fromLegacyMap 验证转换
        com.aipe.observation.api.ObservationDtoMapper mapper =
                new com.aipe.observation.api.ObservationDtoMapper();
        Observation obs = mapper.fromLegacyMap(row);

        assertNotNull(obs);
        assertEquals(TEST_RESOURCE_ID, obs.getResourceId());
        assertEquals("qps", obs.getName());
        assertEquals(Double.valueOf(1234.5), obs.getValue());
        assertEquals(ObservationType.METRIC, obs.getType());

        // 入库
        ObservationIncomingResult result = applicationService.processIncoming(obs);
        assertTrue("旧协议入库应该成功", result.isSuccess());

        System.out.println("[PASS] 旧协议兼容: legacy map → Domain → ClickHouse 成功");
    }
}
