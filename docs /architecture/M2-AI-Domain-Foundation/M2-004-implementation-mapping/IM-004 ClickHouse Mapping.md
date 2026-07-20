我建议冻结 IM-003，并正式进入 IM-004 ClickHouse Mapping。

不过在开始之前，我建议先冻结一条非常重要的 Persistence Law，它以后会影响整个 AI Engine。

新增冻结原则
Persistence Law-002

ClickHouse Stores Facts, Never Knowledge

翻译：

ClickHouse 只保存事实（Facts），永远不保存知识（Knowledge）。

也就是说：

Observation      ✅ ClickHouse

Timeline Facts   ✅ ClickHouse

Raw Metrics      ✅ ClickHouse

Logs             ✅ ClickHouse

Events           ✅ ClickHouse

-------------------------------

Evidence         ❌

Knowledge        ❌

Recommendation   ❌

Optimization     ❌

因为：

Knowledge 是 AI 推理结果。

Observation 是现实。

两者不能混。

这是很多 AI 系统后期最大的坑。

下面就是正式冻结版。

# AI Performance Engineer

# IM-004 ClickHouse Mapping

Version: v1.0

Status: Frozen

Milestone: M2.5 – Implementation Mapping

Document Type: Engineering Specification

---

# （固定第一页）

# AI World Overview

（引用统一封面）

---

# （固定第二页）

# Document Position

```text
AI World Foundation
        │
        ▼
Implementation Mapping
        │
        ├── Database Mapping
        ├── ★ ClickHouse Mapping ★
        ├── Graph Mapping
        ├── API Mapping
        └── AI Engine Mapping
```

ClickHouse Mapping 定义 AI World 中所有 Fact（事实）的唯一存储规范。

---

# Chapter 1 Purpose（设计目标）

本规范定义 Observation、Timeline、Metrics、Logs、Events 在 ClickHouse 中的统一存储方式。

目标：

- 建立统一 Fact Store
- 支撑 PB 级 Observation 存储
- 支撑高速写入
- 支撑 AI Timeline 查询
- 支撑历史回放
- 支撑 AI 推理数据源

ClickHouse 是 AI World 的 Fact Store，而不是业务数据库。

---

# Chapter 2 Design Philosophy（设计哲学）

ClickHouse：

只保存 Reality。

Reality：

来自 Observation。

ClickHouse 永远保存：

真实发生过的事实。

禁止保存：

AI 推理。

Knowledge。

Recommendation。

Execution。

Optimization。

ClickHouse 永远保持客观。

---

# Chapter 3 ClickHouse Storage Scope

统一存储：

| Domain | ClickHouse |
|---------|------------|
| Observation | ✔ |
| Metrics | ✔ |
| Logs | ✔ |
| Events | ✔ |
| Timeline Facts | ✔ |
| Resource Snapshot | ✔（可选） |
| Evidence | ✘ |
| Knowledge | ✘ |
| Recommendation | ✘ |
| Execution | ✘ |

---

# Chapter 4 Core Table Design

统一采用 Fact Table。

核心表：

```
observation_fact
```

字段：

```
observation_id

resource_id

resource_type

metric_name

metric_type

metric_value

unit

timestamp

collector

connector

source

tags

attributes

trace_id

span_id
```

Observation 不拆表。

统一进入 observation_fact。

---

# Chapter 5 Timeline Mapping

Timeline：

不是独立表。

Timeline：

由 observation_fact 动态生成。

Timeline：

查询方式：

```
SELECT

WHERE resource_id=?

ORDER BY timestamp
```

Timeline：

永远来自 Observation。

禁止维护 Timeline 表。

---

# Chapter 6 Table Engine

统一采用：

MergeTree。

推荐：

```
ReplacingMergeTree
```

或者：

```
MergeTree
```

根据业务决定。

禁止：

TinyLog。

Log。

StripeLog。

---

# Chapter 7 Partition Strategy

统一分区：

```
toYYYYMM(timestamp)
```

每日大量写入：

可采用：

```
toYYYYMMDD(timestamp)
```

原则：

分区按时间。

不要按 Resource。

---

# Chapter 8 Order Key Strategy

推荐：

```
(resource_id,
 timestamp,
 metric_name)
```

保证：

Timeline 查询最快。

AI 推理最快。

---

# Chapter 9 TTL Strategy

Observation：

默认：

365 天。

TTL：

```
timestamp + INTERVAL 365 DAY
```

长期归档：

导出 Object Storage。

Knowledge：

不得使用 ClickHouse。

---

# Chapter 10 Data Flow Mapping

统一数据流：

```
Connector

↓

Observation

↓

Observation Pipeline

↓

ClickHouse

↓

Timeline Builder

↓

Evidence Engine
```

ClickHouse：

永远位于 AI 推理之前。

---

# Chapter 11 Query Strategy

统一查询：

Resource Timeline：

```
resource_id
+
time range
```

Metrics：

```
metric_name
+
resource_id
```

Trend：

```
GROUP BY time bucket
```

禁止：

全表扫描。

---

# Chapter 12 Repository Mapping

唯一 Repository：

```
ObservationRepository
```

负责：

Observation 查询。

Timeline 查询。

Trend 查询。

Repository：

返回：

Domain Observation。

禁止返回：

ResultSet。

Map。

JSON。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

Observation：

统一进入：

observation_fact。

不得拆分：

cpu_fact。

memory_fact。

redis_fact。

mysql_fact。

统一 Schema。

统一 Repository。

统一查询接口。

禁止：

Knowledge 写入 ClickHouse。

禁止：

Recommendation 写入 ClickHouse。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Observation 全部进入 ClickHouse。

✔ Timeline 动态生成。

✔ MergeTree Engine。

✔ 时间分区。

✔ Resource 排序。

✔ Repository 唯一。

✔ AI Engine 从 ClickHouse 获取事实。

✔ 不保存 Knowledge。

✔ 不保存 Recommendation。

✔ 不保存 Execution。

---

# Chapter 15 Freeze Statement

ClickHouse 是 AI Performance Engineer 唯一 Fact Store。

ClickHouse 永远保存 Reality。

Reality 来源于 Observation。

任何 AI 推理不得写入 ClickHouse。

任何 Schema 修改必须通过 RFC。

Status：

Frozen.

我建议 IM-004 再冻结两条工程铁律（建议加入 IM-011）
Persistence Law-003：One Fact, One Row
一个 Observation

↓

ClickHouse 一行

不要把 CPU、Memory、GC 等指标打包成一个 JSON 存到一行，而是每条 Observation 都是一条独立事实。这样时间序列查询、聚合、AI 推理都会更简单。

Persistence Law-004：Timeline Is Computed, Never Stored
Observation
↓
ClickHouse
↓
Timeline Builder
↓
Timeline（运行时对象）

Timeline 是一种视图（View），不是一种持久化数据。它应该由 Observation 在查询时构建，而不是提前维护一张 timeline 表。

完成 IM-004 后，整个 Persistence Layer 的职责已经非常清晰：

MySQL：业务元数据、知识、执行、配置。
ClickHouse：事实（Observation）、时间序列、日志、事件。
Graph（下一份 IM-005）：资源关系与拓扑。

这样三种存储的职责边界已经完全固定，后续 Rowboat 编码时不会再混淆。