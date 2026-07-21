package com.aipe.timeline;

import com.aipe.timeline.application.TimelineService;
import com.aipe.timeline.application.TimelineStatsCalculator;
import com.aipe.timeline.application.TimelineStatsCalculator.TrendDirection;
import com.aipe.timeline.domain.ObservationQueryPort;
import com.aipe.timeline.domain.Timeline;
import com.aipe.timeline.domain.TimelinePoint;
import com.aipe.timeline.domain.TimelineQuery;
import com.aipe.timeline.domain.TimelineStats;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Timeline Engine 集成测试（真实 ClickHouse observation_fact）
 *
 * <p>对齐 WP014 Blueprint §10.2 Test Scenario
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimelineApplication.class)
@ActiveProfiles("test")
public class TimelineEngineIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(TimelineEngineIntegrationTest.class);

    @Autowired
    private TimelineService timelineService;

    @Autowired
    private TimelineStatsCalculator statsCalculator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 测试资源 ID（唯一标识测试数据） */
    private static final String TEST_RESOURCE_ID = "order-service-timeline-test";
    private static final String TEST_RESOURCE_TYPE = "APPLICATION";

    @Before
    public void setUp() {
        log.info("=== Timeline Integration Test Setup ===");
        insertTestObservations();
    }

    @After
    public void tearDown() {
        // 清理测试数据
        try {
            jdbcTemplate.update("DELETE FROM observation_fact WHERE resource_id = ?", TEST_RESOURCE_ID);
        } catch (Exception e) {
            log.warn("Cleanup failed: {}", e.getMessage());
        }
    }

    /**
     * 插入 CPU（上升趋势）+ Heap（波动）+ GC（平稳）三类测试 observation
     */
    private void insertTestObservations() {
        // CPU 上升趋势（10 个点，从 20 升到 90）
        long baseTime = System.currentTimeMillis() - 3600_000L; // 1 小时前
        for (int i = 0; i < 10; i++) {
            long ts = baseTime + i * 360_000L; // 每 6 分钟一个点
            insertObservation(TEST_RESOURCE_ID, "cpu.usage", 20.0 + i * 7.5, "%", ts);
        }
        // Heap 波动（5 个点）
        for (int i = 0; i < 5; i++) {
            long ts = baseTime + i * 600_000L;
            insertObservation(TEST_RESOURCE_ID, "heap.used", 256.0 + i * 50.0, "MB", ts);
        }
    }

    private void insertObservation(String resourceId, String metricName, double value, String unit, long timestamp) {
        long seconds = timestamp / 1000;
        jdbcTemplate.update(
                "INSERT INTO observation_fact (" +
                        "observation_id, resource_id, resource_type, metric_name, metric_type, " +
                        "metric_value, unit, source, connector_id, labels, payload, timestamp, received_at) " +
                        "VALUES (?, ?, ?, ?, 'METRIC', ?, ?, 'JVM', '', '{}', '', toDateTime64(?, 3), now64(3))",
                java.util.UUID.randomUUID().toString(), resourceId, TEST_RESOURCE_TYPE,
                metricName, value, unit, seconds);
    }

    /**
     * 测试场景 1：构建 CPU Timeline（上升趋势）
     * 对齐 Blueprint §10.2（Order Service CPU Timeline）
     */
    @Test
    public void should_build_cpu_timeline() {
        long now = System.currentTimeMillis();
        TimelineQuery query = new TimelineQuery(TEST_RESOURCE_ID, "cpu.usage", now - 7200_000L, now, 1000);
        Timeline timeline = timelineService.buildTimeline(query);

        assertNotNull(timeline);
        assertNotNull(timeline.getTimelineId());
        assertTrue("timelineId 应以 TL- 开头", timeline.getTimelineId().startsWith("TL-"));
        assertNotNull(timeline.getPoints());

        TimelineStats stats = timeline.getStats();
        assertTrue("CPU min 应 >= 20", stats.getMin() >= 20.0);
        assertTrue("CPU max 应 >= 80", stats.getMax() >= 80.0);
        assertTrue("CPU count 应 >= 5", stats.getCount() >= 5);
        assertTrue("stdDev 应 >= 0", stats.getStdDev() >= 0.0);

        // 按时间升序
        long prev = 0;
        for (TimelinePoint p : timeline.getPoints()) {
            assertTrue("points 按时间升序", p.getTimestamp() >= prev);
            prev = p.getTimestamp();
        }

        System.out.println("[PASS] CPU Timeline: points=" + stats.getCount()
                + ", min=" + stats.getMin() + ", max=" + stats.getMax()
                + ", avg=" + String.format("%.2f", stats.getAvg()));
    }

    /**
     * 测试场景 2：Timeline 统计特征正确性（手动计算 min/max/avg 对比）
     * 对齐 Blueprint §10.2
     */
    @Test
    public void should_calculate_stats_correctly() {
        // 构造三个已知值
        List<TimelinePoint> points = Arrays.asList(
                new TimelinePoint(1000L, 10.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(2000L, 20.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(3000L, 30.0, "", "", java.util.Collections.emptyMap())
        );

        TimelineStats stats = statsCalculator.calculate(points);

        assertEquals("min", 10.0, stats.getMin(), 0.001);
        assertEquals("max", 30.0, stats.getMax(), 0.001);
        assertEquals("avg", 20.0, stats.getAvg(), 0.001);
        assertEquals("count", 3, stats.getCount());
        // 总体标准差：sqrt(((10-20)^2 + 0 + (30-20)^2)/3) = sqrt(200/3) ≈ 8.165
        assertEquals("stdDev", Math.sqrt(200.0 / 3), stats.getStdDev(), 0.001);

        System.out.println("[PASS] 统计特征正确性：min=" + stats.getMin() + ", max=" + stats.getMax()
                + ", avg=" + stats.getAvg() + ", stdDev=" + String.format("%.4f", stats.getStdDev()));
    }

    /**
     * 测试场景 3：Timeline 趋势方向检测（上升）
     * 对齐 Blueprint §10.2 + §10.5
     */
    @Test
    public void should_detect_rising_trend() {
        List<TimelinePoint> rising = Arrays.asList(
                new TimelinePoint(1000L, 10.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(2000L, 20.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(3000L, 35.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(4000L, 50.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(5000L, 70.0, "", "", java.util.Collections.emptyMap())
        );
        assertEquals(TrendDirection.RISING, statsCalculator.detectTrend(rising));

        List<TimelinePoint> falling = Arrays.asList(
                new TimelinePoint(1000L, 70.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(2000L, 50.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(3000L, 35.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(4000L, 20.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(5000L, 10.0, "", "", java.util.Collections.emptyMap())
        );
        assertEquals(TrendDirection.FALLING, statsCalculator.detectTrend(falling));

        List<TimelinePoint> flat = Arrays.asList(
                new TimelinePoint(1000L, 50.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(2000L, 50.5, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(3000L, 49.8, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(4000L, 50.2, "", "", java.util.Collections.emptyMap())
        );
        assertEquals(TrendDirection.FLAT, statsCalculator.detectTrend(flat));

        System.out.println("[PASS] 趋势方向检测：RISING / FALLING / FLAT 全部正确");
    }

    /**
     * 测试场景 4：Illegal TimelineQuery（无 resourceId）→ IllegalArgumentException
     * 对齐 Blueprint §10.2
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_query_without_resource_id() {
        long now = System.currentTimeMillis();
        TimelineQuery query = new TimelineQuery(null, "cpu.usage", now - 3600_000L, now, 1000);
        timelineService.buildTimeline(query);
    }

    /**
     * 测试场景 5：Illegal TimelineQuery（开始时间 >= 结束时间）→ IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_query_with_invalid_time_range() {
        long now = System.currentTimeMillis();
        TimelineQuery query = new TimelineQuery(TEST_RESOURCE_ID, "cpu.usage", now, now - 3600_000L, 1000);
        timelineService.buildTimeline(query);
    }

    /**
     * 测试场景 6：多指标 Timeline（cpu + heap 合并查询）
     * 对齐 Blueprint §10.2
     */
    @Test
    public void should_build_multi_metric_timelines() {
        long now = System.currentTimeMillis();
        TimelineQuery baseQuery = new TimelineQuery(TEST_RESOURCE_ID, null, now - 7200_000L, now, 1000);
        List<Timeline> timelines = timelineService.buildTimelines(baseQuery, Arrays.asList("cpu.usage", "heap.used"));

        assertNotNull(timelines);
        assertEquals("2 个 Timeline（cpu + heap）", 2, timelines.size());

        // 每个 Timeline 都有统计特征
        for (Timeline t : timelines) {
            assertNotNull(t.getStats());
            assertTrue("stats.count >= 1", t.getStats().getCount() >= 1);
        }

        System.out.println("[PASS] 多指标 Timeline：返回 " + timelines.size() + " 个 Timeline");
    }

    /**
     * 测试场景 7：changeRate 计算（上升 10→50 = +400%）
     */
    @Test
    public void should_calculate_change_rate() {
        List<TimelinePoint> points = Arrays.asList(
                new TimelinePoint(1000L, 10.0, "", "", java.util.Collections.emptyMap()),
                new TimelinePoint(2000L, 50.0, "", "", java.util.Collections.emptyMap())
        );
        double rate = statsCalculator.calculateChangeRate(points);
        // (50-10)/10 = 4.0
        assertEquals("changeRate", 4.0, rate, 0.001);

        System.out.println("[PASS] 变化率计算：10→50 = " + (rate * 100) + "%");
    }
}
