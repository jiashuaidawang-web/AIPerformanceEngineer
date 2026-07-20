# AI Performance Engineer

# WP014 Timeline Engine Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP012 Observation Engine

IM-004 ClickHouse Mapping

IM-003 Persistence Mapping

M2-010 Timeline Model

RequiredBy:

WP015 Evidence Engine

EstimatedJavaFiles: 15

EstimatedWorkload: 3 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Timeline 运行时构建引擎。Timeline 不是存储对象，而是运行时按 Resource + 时间范围 + 指标名称聚合 Observation 的结果。用户查询 Resource Timeline 时，系统从 ClickHouse 动态生成，并支撑 Evidence Engine 的推理。

核心解决问题：Observation 是点（单条事实），Timeline 是线（历史演变）。WP014 提供 Timeline 的构建和查询能力。

Before：API 只能查单条 Observation，无法回答"Order Service 过去 1 小时的 CPU 趋势"

After：API 返回完整的 Resource Timeline（按时间排序的 Observation 序列 + 统计特征）

核心原则：Timeline Is Computed, Never Stored（Persistence Law-004 / Architecture Law-004）。

Scope：

MVP 支持：Timeline 运行时构建 + 按 Resource / 指标 / 时间范围 查询 + Timeline 统计特征（均值 / 最大值 / 最小值 / 标准差 / 样本数）+ Timeline 查询 API + 支撑后续 Evidence Engine 推理

不包含：Timeline 持久化（永远不存）；实时流计算（只支持离线查询）；Timeline 异常标注（WP015 实现）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：

Timeline 运行时构建：TimelineService.buildTimeline(resourceId, metricName, timeRange) → Timeline 值对象

Timeline 按 Resource + 时间范围查询：queryByResource(resourceId, timeRange) → Timeline

Timeline 按指标类型 / 多指标查询：queryByResource(resourceId, metricNames, timeRange) → Timeline

Timeline 统计特征：每条 Timeline 自动 计算 / min / max / avg / stdDev / count

Timeline 查询 API：/api/v1/timelines?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx → TimelineResponse

    2.2 Technical Acceptance

必须满足：

Timeline 是内存值对象（不存储，每次查询新建）

调用 ObservationRepository 查询 ClickHouse observation_fact（WHERE resource_id=? AND metric_name=? AND timestamp BETWEEN ? AND ? ORDER BY timestamp）

Timeline 构建耗时 < 200ms（毫秒级时间范围 + 万级 Observation）

对齐 IM-004 ClickHouse WHERE + ORDER BY（利用 MergeTree 排序性能）

支持毫秒级 + 秒级 timestamp 输入

    2.3 Integration Acceptance

完整链路：

Agent 采集 Observation → ClickHouse → TimelineService 动态构建 Timeline → 返回 TimelineResponse

---

## 3. Package List

com.aipe.timeline

├── domain/                ← Timeline + TimelinePoint + TimelineStats + TimelineQuery

├── application/           ← TimelineService（构建引擎）+ TimelineStatsCalculator

├── infrastructure/        ← 无（Timeline 无存储，只调用 ObservationRepository）

└── api/                   ← TimelineController + DTO + 异常处理

Package Responsibility

Package	职责

domain	Timeline 值对象 + 统计特征（TimelineStats）+ 查询参数（TimelineQuery）

application	TimelineService（调用 ObservationRepository + 排序 + 统计计算）+ TimelineStatsCalculator

infrastructure	（无存储，调用 ObservationRepository 接口）

api	TimelineController + TimelineResponse

---

## 4. Class List

    4.1 Timeline（Domain 值对象）

职责：指定 Resource + 指标 + 时间范围的 Timeline 视图（运行时对象，不存储）

字段：字段	类型	说明

timelineId	String	"TL-{resourceId}-{metricName}-{start}-{end}"

resourceId	ResourceId	所属 Resource

metricName	String	指标名称

startTime	Long	开始时间（毫秒）

endTime	Long	结束时间（毫秒）

points	List<TimelinePoint>	按时间排序的观察序列

stats	TimelineStats	统计特征（min/max/avg/stdDev/count）

builtAt	Long	构建时间戳

    4.2 TimelinePoint（Domain 值对象）

职责：Timeline 中的单个观察点

字段：timestamp（Long）+ value（Double）+ unit（String）+ connectorId（String）+ labels（Map）

    4.3 TimelineStats（Domain 值对象）

职责：Timeline 自动计算的统计特征

字段：min / max / avg / stdDev / count / firstTimestamp / lastTimestamp

    4.4 TimelineQuery（Domain 值对象）

职责：Timeline 查询参数

字段：resourceId / metricName / startTime / endTime / limit / direction（forward/backward）

    4.5 TimelineService（Application）

职责：Timeline 构建引擎（核心：调用 ObservationRepository + 排序 + 计算统计特征）

    4.6 TimelineStatsCalculator（Application）

职责：时序统计特征计算（min/max/avg/stdDev + 趋势方向 + 变化率）

---

## 5. Method List

TimelineService（Application 核心）

// 构建指定 Resource + 指标 在时间范围内的 Timeline（先查 Observation，装 Timeline，算统计特征）

Timeline buildTimeline(TimelineQuery query)

// 构建指定 Resource 的 多指标 Timeline（调用 buildTimeline 多次 + 合并返回）

List<Timeline> buildTimelines(TimelineQuery query, List<String> metricNames)

// 构建指定 Resource 在 时间范围内的 全指标 Timeline（查该 Resource 所有 distinct metricName → 分别 build）

List<Timeline> buildAllMetricsTimelines(ResourceId resourceId, TimeRange range)

// 构建用于 Evidence Engine 的 增强 Timeline（含统计特征 + 边界 + 对齐 等处理）

Timeline buildEnhancedTimeline(TimelineQuery query)

TimelineStatsCalculator

// 计算统计特征（min/max/avg/stdDev/count）

TimelineStats calculate(List<TimelinePoint> points)

// 计算趋势方向（上升/下降/平稳）

TrendDirection detectTrend(List<TimelinePoint> points)

// 计算变化率（首尾变化百分比）

double calculateChangeRate(List<TimelinePoint> points)

TimelineController（API）

// GET /api/v1/timelines?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx

TimelineResponse getTimeline(@Valid TimelineRequest request)

// GET /api/v1/timelines/batch?resource_id=xxx&metric_names=cpu,memory&start_time=xxx&end_time=xxx

List<TimelineResponse> getTimelines(@Valid TimelineBatchRequest request)

// GET /api/v1/timelines/aggregated?resource_id=xxx&start_time=xxx&end_time=xxx&interval=1m

TimelineResponse getAggregatedTimeline(@Valid TimelineAggregateRequest request)
    5.1 物理文件清单

aipe-timeline/

├── pom.xml

├── src/main/java/com/aipe/timeline/

│   ├── TimelineApplication.java

│   ├── domain/

│   │   ├── Timeline.java

│   │   ├── TimelinePoint.java

│   │   ├── TimelineStats.java

│   │   ├── TimelineQuery.java

│   │   └── TimelineSpecification.java      ← 校验（时间范围合理 + metricName 必填 + resourceId 必填）

│   ├── application/

│   │   ├── TimelineService.java              ← 核心

│   │   └── TimelineStatsCalculator.java     ← 统计特征计算

│   ├── infrastructure/                       ← 无（Timeline 无存储，调用 ObservationRepository 接口）

│   └── api/

│       ├── TimelineController.java

│       ├── TimelineDtoMapper.java

│       ├── dto/

│       │   ├── TimelineRequest.java

│       │   ├── TimelineResponse.java

│       │   └── TimelineBatchRequest.java

│       └── exception/

│           └── TimelineExceptionHandler.java

---

## 6. ClickHouse 查询（对齐 IM-004）

```sql
-- Timeline 核心查询（利用 MergeTree 排序）
SELECT *
FROM observation_fact
WHERE resource_id = #{resourceId}
  AND metric_name = #{metricName}
  AND timestamp BETWEEN #{startTime} AND #{endTime}
  AND deleted = 0
ORDER BY timestamp ASC
LIMIT #{limit};

-- 多指标 Timeline 查询（支持批量）
SELECT *
FROM observation_fact
WHERE resource_id = #{resourceId}
  AND metric_name IN (#{metricNames})
  AND timestamp BETWEEN #{startTime} AND #{endTime}
  AND deleted = 0
ORDER BY metric_name ASC, timestamp ASC
LIMIT #{limit};

-- 聚合查询（时间桶，用于趋势推断）
SELECT
    toStartOfInterval(timestamp, INTERVAL #{interval} minute) AS bucket,
    AVG(metric_value) AS avg_value,
    MAX(metric_value) AS max_value,
    MIN(metric_value) AS min_value,
    COUNT(*) AS sample_count
FROM observation_fact
WHERE resource_id = #{resourceId}
  AND metric_name = #{metricName}
  AND timestamp BETWEEN #{startTime} AND #{endTime}
  AND deleted = 0
GROUP BY bucket
ORDER BY bucket ASC;
```

对齐检查

IM-004 规范	本 WP 实现

ClickHouse WHERE + ORDER BY	✅ 核心查询利用 MergeTree 排序性能

toYYYYMM(timestamp) 分区	✅ 按月分区

(resource_id, metric_name, timestamp) 排序	✅ Timeline 查询最快

365 天 TTL	✅ TTL 自动过期

不存储 Timeline	✅ 永远不存，每次查询新建

---

## 7. API 设计

方法	路径	说明	入参

GET	/api/v1/timelines?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx	Single Timeline	resource_id / metric_name / start_time / end_time / limit

GET	/api/v1/timelines/batch?resource_id=xxx&metric_names=cpu,memory&start_time=xxx&end_time=xxx	Multi Timeline	resource_id / metric_names / start_time / end_time

GET	/api/v1/timelines/aggregated?resource_id=xxx&metric_name=xxx&interval=1m&start_time=xxx&end_time=xxx	Aggregated Timeline	resource_id / metric_name / interval / start_time / end_time

---
## 8. Sequence Diagram

Timeline 查询

user TimelineController TimelineService ObservationRepository ClickHouse

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Timeline 运行时构建，不存储（Persistence Law-004）

必须实现：Timeline 返回 Domain（TimelineResponse 必须含 timelineId + points + stats）

必须实现：TimelineStats 自动计算 + 变化率 + 趋势方向

必须实现：调用 ObservationRepository 接口（禁止直接调 Mapper）

必须实现：支持毫秒级 + 秒级 timestamp 输入（自动识别）

    9.2 Forbidden

禁止：Timeline 存储（永远不建 timeline 表）

禁止：TimelineService 直接 new Mapper（必须走 Repository）

禁止：长期持有 Timeline 对象（运行即用，用后释放）

    9.3 Engineering Rules

必须：buildTimeline 前 validate TimelineQuery（resourceId + metricName + 合理的时间范围）

必须：API 返回 TimelineResponse（必须含 stats）

必须：统一返回 ApiResponse

必须：集成测试必须覆盖 + 统计特征正确 + 趋势方向判断

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-timeline -DskipTests

    10.2 Test Scenario

构建 Order Service 过去 1 小时的 CPU Timeline

构建 Order Service 过去 24 小时的 heap Timeline

多指标 Timeline 查询（cpu + memory + gc）

空 Timeline（不存在的 resourceId）→ 返回非法请求

Illegal TimelineQuery（无 resourceId / metricName）→ 返回 400

Timeline 统计特征正确性（手动计算 min/max/avg 对比）

Timeline 趋势方向检测（上升趋势 / 下降趋势 / 平稳）

    10.3 Verification

检查：Timeline 是内存值对象（不存储）

检查：stats 自动计算且正确（min/max/avg/stdDev/count）

检查：points 按 timestamp 升序

检查：API 返回 TimelineResponse 含 stats + points

    10.4 Expected Result

返回 Timeline 含 points（按时间排序的观察序列）

返回 TimelineStats（min/max/avg/stdDev/count）

符合 M2-009：Timeline 运行即用，不存储

    10.5 Troubleshooting

查询慢：检查 ClickHouse WHERE + ORDER BY 字段顺序（必须 resource_id, metric_name, timestamp 顺序）

stats 计算错误：检查 stdDev 计算方法（样本标准差 / 总体标准差）

趋势 检测抖动：考虑使用最小二乘法（Least Squares）替代首尾比较

---

## 11. Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package（com.aipe.timeline）

不得改变 Package 名称

Timeline 不存储 / 不建表（永远不存）

TimelineService 必须通过 ObservationRepository 接口查询数据（禁止直接调 Mapper）

所有新增类必须放入规定 Package

任何违反 Persistence Law / Gateway Law / Architecture Law 的代码必须拒绝提交

Status: Draft