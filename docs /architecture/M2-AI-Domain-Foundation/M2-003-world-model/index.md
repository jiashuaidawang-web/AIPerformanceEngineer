# AI Performance Engineer

# M2-006 Observation Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 AI Domain Foundation

---

# 1. Purpose（设计目标）

定义 AI Performance Engineer 中 Observation 的统一领域模型。

Observation 是整个 AI 世界中唯一的运行时事实（Runtime Fact）。

所有 Connector、Pipeline、Storage、Timeline、Evidence、Knowledge 都必须遵守本规范。

---

# 2. Design Philosophy（设计哲学）

Observation 不是：

- Metric
- Log
- Trace

Observation 是：

> Resource 在某一时刻产生的一条不可变事实（Immutable Runtime Fact）。

Observation 是整个 AI 世界唯一认可的运行时事实。

---

# 3. Observation Definition

Observation 定义：

Observation =

Resource

+

Timestamp

+

Fact

其中：

Fact 可以表现为：

- Metric
- Log
- Trace
- Event
- Snapshot

但这些都只是 Observation 的不同表现形式。

---

# 4. Observation Characteristics（核心特征）

Observation 必须满足：

## Immutable

Observation 永远不可修改。

只能新增。

禁止 Update。

---

## Timestamped

每一条 Observation 必须有精确时间。

时间精度建议：

毫秒。

---

## Resource Scoped

Observation 必须属于一个 Resource。

resourceId 必须存在。

---

## Traceable

Observation 必须能够追溯采集来源。

例如：

JMX

Redis INFO

MySQL

OTLP

JMeter

---

## Extensible

允许未来新增新的 Observation Type。

无需修改已有 Schema。

---

# 5. Observation Classification（分类）

Observation 分为：

## Metric

数值型指标。

例如：

CPU

Heap

Memory

TPS

---

## Log

文本日志。

例如：

Exception

GC Log

Application Log

---

## Trace

调用链。

例如：

HTTP Span

RPC Span

Database Span

---

## Event

离散事件。

例如：

Deployment

Pod Restart

Node Offline

Redis Failover

---

## Snapshot

状态快照。

例如：

Thread Dump

Heap Dump

Redis INFO

MySQL SHOW STATUS

---

# 6. Observation Source（采集来源）

Observation 的 Source 仅表示采集方式。

例如：

JMX

OS API

Redis Protocol

JDBC

HTTP

OTLP

File

JMeter

未来允许扩展。

---

# 7. Observation Schema

统一字段：

ObservationId

ResourceId

ObservationType

Source

Name

Value

Unit

Labels

Payload

Timestamp

Version

---

# 8. Observation Lifecycle

Resource

↓

Produce Observation

↓

Connector Collect

↓

Pipeline

↓

Storage

↓

Timeline

↓

Evidence

↓

Knowledge

Connector 永远不是 Observation 的 Owner。

---

# 9. Observation Relationship

Observation

属于：

一个 Resource。

Observation

进入：

一个 Timeline。

Observation

参与：

多个 Evidence。

Observation

最终沉淀：

Knowledge。

---

# 10. Observation Immutability

Observation 不允许：

Update。

Delete。

Merge。

Observation 只能：

Append。

所有分析建立在 Append Only 数据模型。

---

# 11. Observation Version

Schema Version。

Payload Version。

Connector Version。

允许未来升级。

---

# 12. Observation Examples

示例：

JVM Heap

Redis Memory

MySQL Threads

JMeter TPS

GC Log

OTLP Trace

统一表示。

---

# 13. Reference Java Model

Observation.java

ObservationType.java

ObservationSource.java

ObservationRepository.java

ObservationPipeline.java

ObservationCollector.java

---

# 14. Architecture Constraints

必须遵守：

Law-000

Law-001

Law-002

违反任何 Law 的 Observation

视为非法 Observation。

---

# 15. Acceptance Criteria

满足：

✔ 所有 Observation 必须属于 Resource

✔ 所有 Observation 必须不可变

✔ Connector 不允许创建 Observation

✔ Observation 必须可追溯

✔ Observation 支持未来扩展

✔ Observation 能支撑 Timeline

✔ Observation 能支撑 Evidence

✔ Observation 能支撑 Knowledge

---

# Freeze Statement

本规范冻结后，

任何字段新增、

删除、

修改，

必须通过 RFC 审核。

禁止直接修改。




另外一个顺序
M2-000 AI World Model

↓

Architecture Laws

↓

AI World Vocabulary ⭐⭐⭐⭐⭐

↓

Observation Model

↓

Resource Model

↓

Relationship

↓

Topology

↓

Timeline



