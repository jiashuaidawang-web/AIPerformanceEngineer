# AI Performance Engineer

# WP012 Observation Engine Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP011 Resource Domain

IM-003 Persistence Mapping

IM-004 ClickHouse Mapping

M2-006 Observation Model Specification

RequiredBy:

WP014 Timeline

WP015 Evidence Engine

EstimatedJavaFiles: 25

EstimatedWorkload: 4 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Observation 领域模型 和 Repository，实现 Observation 从采集到存储的完整生命周期管理，并与 Resource 关联（通过 resourceId）。

核心解决问题：M1 的 Observation 只是一个 `Map<String,传来就存`，缺少领域模型、类型约束、与 Resource 的强关联。WP012 将 Observation 提升为正式的 Domain Object，并赋予完整的业务行为和能力边界。

Before：Observation 是 Map，无类型、无校验、无关联

After：Observation 是 Domain Object，有类型、有校验、有 Resource 关联、有 Repository

核心原则：Observation 是 AI World 中唯一合法的 Runtime Fact，永远属于一个 Resource（Law-002），不可变（Immutable），只能追加（Append Only）。

Scope：

MVP 支持：Observation 领域模型 + Repository（ClickHouse 实现）+ 与 Resource 关联 + Pipeline 对接 + 查询 API

不包含：Timeline 构建（WP014）、异常检测（WP015）、自动 AI 推理

---

## 2. Acceptance Criteria

2.1 Functional Acceptation

必须支持：

Observation 领域模型（含 ResourceId / Type / Name / Value / Timestamp / ConnectorId）

Observation 进入系统时自动校验并关联 Resource（resourceId 必增）

Observation 按 Resource 查询

Observation 按时间范围查询

Observation 按指标名称查询

Observation trend（时间桶聚合：每分钟 / 每小时 / 每天）

M1 的 MetricGateway 平滑迁移（新 API 兼容旧 Agent 协议）

2.2 Technical Acceptance

必须满足：

Repository 接口在 Domain，实现在 Infrastructure

Repository 返回 Domain，禁止返回 ResultSet / Map

使用 ClickHouse MergeTree 引擎（对齐 IM-004）

Observation 不进入 MySQL（对齐 Persistence Law-001）

支持每秒 10k+ 写入（ClickHouse 批量插入）

分区 + 排序键对齐 IM-004（toYYYYMM(timestamp) + (resource_id, metric_name, timestamp)）

2.3 Integration Acceptance

完整链路：

Agent 采集 → Backend 接收 → Observation Domain 校验 → ClickHouse 落地 → API 查询 → 返回 Domain

---

## 3. Package List

com.aipe.observation

├── domain/                ← Observation + ObservationType + ObservationSource + Repository 接口

├── application/           ← Observation Pipeline 编排 + 校验

├── infrastructure/        ← ObservationRepositoryImpl + ClickHouse Mapper + PO + Converter

├── api/                   ← ObservationController + DTO + ExceptionHandler

└── config/                ← ClickHouse 数据源配置

Package Responsibility

Package	职责

domain	Observation 聚合根 + 枚举 + Repository 接口 + 校验规格

application	ObservationApplicationService 编排：接收 → 校验 → 入库 → 查询

infrastructure	ObservationRepositoryImpl（ClickHouse 实现）+ ObservationPO + Mapper + Converter

api	ObservationController + DTO + Mapper（DTO↔Domain）

config	ClickHouseDataSourceConfig + MyBatis 配置

---

## 4. Class List

4.1 Observation（Domain）

Package: domain

职责：Observation 聚合根，不可变，关联 Resource

字段：

字段	类型	说明

observationId	ObservationId	业务主键（值对象）

resourceId	ResourceId	所属 Resource（必填 - Law-002）

type	ObservationType	Metric / Log / Trace / Event / Snapshot

source	ObservationSource	JVM / Linux / Redis / MySQL / Prometheus

name	String	指标名（heap.used / cpu.usage）

value	Double	指标值

unit	String	单位（ms / % / bytes）

timestamp	Long	采集时间（毫秒级 - IM-004）

connectorId	String	Connector 标识

labels	Map<String, String>	扩展标签

payload	String	原始数据（JSON）

4.2 ObservationRepository（Domain 接口）

职责：Observation 仓储接口

4.3 ObservationApplicationService（Application）

职责：编排 Observation 接收、校验、存储、查询

4.4 ObservationRepositoryImpl（Infrastructure）

职责：ClickHouse 实现，返回 Domain

4.5 ObservationController（API）

职责：REST API（/query / /batch / /trend）

---

## 5. Method List

Observation（Domain 业务方法）

// 校验 Observation 是否合法（resourceId + timestamp + type + name + value 必填）

void validate()

// 是否属于指定 Resource

boolean belongsTo(ResourceId resourceId)

// 是否处于时间范围内

boolean isWithin(long startTime, long endTime)

// 是否属于指定指标类型

boolean isType(ObservationType type)

ObservationApplicationService（Application）

// 处理单条 Observation 入库

ObservationIncomingResult processIncoming(Observation observation)

// 批量处理 Observation 入库

BatchIncomingResult batchProcessIncoming(List<Observation> observations)

// 查询 Resource 的时间线 observations

List<Observation> queryByResource(ResourceId resourceId, TimeRange range, int limit)

// 查询 Resource 指定指标在时间范围内的 observations

List<Observation> queryByMetric(ResourceId resourceId, String metricName, TimeRange range, int limit)

// 时间桶趋势查询（聚合）

List<TrendPoint> queryTrend(ResourceId resourceId, String metricName, TimeRange range, TrendInterval interval)

ObservationRepositoryImpl（Infrastructure）

// 单条保存（调用 ClickHouse Mapper 批量接口，单条封装）

Observation save(Observation observation)

// 批量保存（对齐 Pipeline 的 batchSave）

List<Observation> batchSave(List<Observation> observations)
    5.1 物理文件清单

aipe-observation/

├── pom.xml

├── src/main/java/com/aipe/observation/

│   ├── ObservationApplication.java            ← 启动类

│   ├── domain/

│   │   ├── Observation.java                   ← 聚合根

│   │   ├── ObservationId.java                 ← 值对象

│   │   ├── ObservationType.java               ← 枚举（Metric/Log/Trace/Event/Snapshot）

│   │   ├── ObservationSource.java             ← 枚举（JVM/Linux/Redis/MySQL/Prometheus）

│   │   ├── ObservationRepository.java         ← 接口

│   │   └── ObservationSpecification.java      ← 校验规格

│   ├── application/

│   │   ├── ObservationApplicationService.java ← 编排服务

│   │   ├── ObservationPipeline.java            ← 接收 → 校验 → 入库 编排

│   │   ├── ObservationValidator.java          ← 校验器

│   │   └── TrendAggregator.java               ← 时间桶聚合

│   ├── infrastructure/

│   │   ├── ObservationRepositoryImpl.java     ← ClickHouse 实现

│   │   ├── ObservationPO.java                 ← 持久化对象

│   │   ├── ObservationMapper.java             ← MyBatis Mapper（ClickHouse）

│   │   ├── ObservationConverter.java          ← PO ↔ Domain 转换

│   │   └── ClickHouseDataSourceConfig.java    ← 数据源配置

│   ├── api/

│   │   ├── ObservationController.java         ← REST API

│   │   ├── ObservationDtoMapper.java          ← DTO ↔ Domain

│   │   ├── dto/

│   │   │   ├── ObservationRequest.java        ← 请求 DTO

│   │   │   ├── ObservationResponse.java       ← 响应 DTO

│   │   │   └── TrendResponse.java             ← 趋势响应 DTO

│   │   └── exception/

│   │       └── ObservationExceptionHandler.java← 异常处理

│   └── config/
│       └── MyBatisConfig.java                 ← ClickHouse MyBatis 配置
    ---
## 6. ClickHouse Schema（对齐 IM-004）

    6.1 核心表

```sql
CREATE TABLE IF NOT EXISTS observation_fact (
    observation_id String DEFAULT generateUUIDv4(),
    resource_id String,
    resource_type String DEFAULT 'UNKNOWN',
    metric_name String,
    metric_type Enum8('METRIC'=1, 'LOG'=2, 'TRACE'=3, 'EVENT'=4, 'SNAPSHOT'=5),
    metric_value Float64,
    unit String DEFAULT '',
    source String,
    connector_id String DEFAULT '',
    labels String DEFAULT '{}',
    payload String DEFAULT '',
    timestamp DateTime64(3),
    received_at DateTime64(3) DEFAULT now64(3)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (resource_id, metric_name, timestamp)
TTL timestamp + INTERVAL 365 DAY;
```

    6.2 设计对齐

IM-004 规范	本 WP 实现

MergeTree 引擎	✅ 使用 MergeTree

toYYYYMM(timestamp) 分区	✅ 按月分区

(resource_id, metric_name, timestamp) 排序	✅ 保证 Timeline 查询最快

365 天 TTL	✅ 自动过期

对齐 M1 observation_fact	✅ 向后兼容（旧表迁移）

---

## 7. API 设计

    7.1 Observation API（新）

方法	路径	说明	入参

POST	/api/v1/observations	单条入库	ObservationRequest

POST	/api/v1/observations/batch	批量入库	List<ObservationRequest>

GET	/api/v1/observations?resource_id=xxx&metric_name=xxx&start_time=xxx&end_time=xxx	查询	resource_id / metric_name / start_time / end_time / limit

GET	/api/v1/observations/trend?resource_id=xxx&metric_name=xxx&interval=1m&start_time=xxx&end_time=xxx	trend 查询	resource_id / metric_name / interval / start_time / end_time

    7.2 旧协议兼容（M1 Agent 平滑迁移）

旧 API：POST /api/v1/observations/batch（M1）

新 API：POST /api/v1/observations/batch（新）

前后兼容策略：新 ObservationApplicationService 先兼容旧 ObservationBatchRequest 协议，然后逐步切换 Agent 到新协议。旧 Agent 代码无需修改即可对接新后端。

---
## 8. Sequence Diagram

单条入库

HTTP Request (ObservationRequest)
  ↓

ObservationController

  ↓
ObservationApplicationService.processIncoming()

  ↓
ObservationValidator.validate(resourceId + timestamp + ...)

  ↓
ObservationRepository.save()

  ↓
ClickHouse Mapper.insert(observation)

  ↓
返回 ObservationResponse（含 observation_id）

trend 查询

HTTP Request (resource_id + metric_name + interval)

  ↓

ObservationController

  ↓
ObservationApplicationService.queryTrend()

  ↓
ObservationRepository.findForTrend()

  ↓
TrendAggregator.aggregate(time bucket)

  ↓
返回 TrendResponse（含 points: [{timestamp, avg, max, min, count}]）

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Observation 关联 Resource（resourceId 必填）

必须实现：Repository 接口在 Domain + 实现在 Infrastructure + 返回 Domain

必须实现：ClickHouse MergeTree + 分区 + 排序 + TTL 对齐 IM-004

必须实现：趋势查询时间桶聚合（至少支持 1m / 5m / 1h / 1d）

必须实现：M1 旧协议兼容（agent 不修改即可对接）

    9.2 Forbidden

禁止：Observation 落 MySQL（Persistence Law-001：Observation → ClickHouse）

禁止：Repository 返回 Map / ResultSet

禁止：Repository 包含业务逻辑

禁止：Observation 被 Update（Observation is Immutable）

禁止：Controller 直接访问 Repository

    9.3 Engineering Rules

必须：所有入库前 validate()（必须有 resourceId + timestamp）

必须：API 返回 Domain（ObservationRepository 必须转为 ObservationResponse）

必须：使用 Druid 连接池（ClickHouse 连接）

必须：统一返回 ApiResponse（code/message/data）

必须：集成测试必须覆盖单条入库 + 批量入库 + 趋势查询 + 非法 resourceId 拒绝

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-observation -DskipTests

    10.2 Test Scenario

创建 Observation 观测数据

Observation obs = Observation.builder()

        .resourceId(ResourceId.of("order-service-001"))

        .type(ObservationType.METRIC)

        .name("heap.used")

        .value(512.0)

        .unit("MB")

        .timestamp(System.currentTimeMillis())

        .connectorId("jvm-001")

        .build();

    observationApplicationService.processIncoming(obs);

查询 Resource 的 Observation 列表

查询时间范围内的指标趋势（1m 桶聚合）

非法 Observation 校验：无 resourceId → 拒绝

批量写入 1000 条 Observation（验证吞吐量）

    10.3 Verification

检查：Observation 落 ClickHouse observation_fact，resourceId 正确

检查：趋势查询返回时间桶聚合结果（avg / max / min / count）

检查：API 返回 Domain（ObservationResponse 必须含 observationId）

检查：非法 Observation（无 resourceId）被拒绝（返回 400）

    10.4 Expected Result

返回 Resource 的时间线 observations（按时间倒序）

返回趋势数据（每分钟聚合平均 / 最大 / 最小 / 样本数）

符合 Law-002：所有 Observation 都关联 Resource

    10.5 Troubleshooting

查询无结果：检查 resourceId 是否匹配、timestamp 是否在范围内（注意毫秒 vs 秒）

写入失败：检查 ClickHouse 连接、Map → JSON 序列化、必填字段

趋势聚合慢：检查 ORDER BY 字段顺序（必须是 resource_id, metric_name, timestamp）

---
## 11. Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package（com.aipe.observation）

不得改变 Package 名称

Observation 必须关联 Resource（resourceId 是必填，不是可选）

Repository 接口在 domain/，实现在 infrastructure/，返回 Domain

API 层需要兼容 M1 旧协议（ObservationBatchRequest），但内部转为 Observation Domain

所有新增类必须放入规定 Package

任何违反 Package Mapping / Persistence Law / Gateway Law 的代码必须拒绝提交

Status: Draft