# AI Performance Engineer

# M2-006 Observation Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）
AI Performance Engineer
AI World Overview
Version: v1.0
Status: Frozen
=========================================================

                         Enterprise
                              │
            ┌─────────────────┴─────────────────┐
            │                                   │
     Business Domain                    Shared Platform
            │
     Business System
            │
         Resource
            │
     ┌──────┼──────────────┐
     │      │              │
Observation  Relationship  Topology
│             │             │
└─────────────┼─────────────┘
│
Timeline
│
Evidence
│
Knowledge
│
Optimization
│
Verification
│
Knowledge Growth

---

# （固定第二页）
Document Position（Current Document：Observation）

```
AI World
    │
    ├── Vocabulary
    │
    ├── Resource
    │
    ├───────────────► Observation ◄──────────────┐
    │                     │                      │
    │                     ▼                      │
    │                 Timeline                  │
    │                     ▼                      │
    │                 Evidence                  │
    │                     ▼                      │
    │                 Knowledge                 │
    └───────────────────────────────────────────┘
```

Observation 是 AI World 中唯一合法的 Runtime Fact。

---

我认为，现在可以正式开始 M2-006。

而且我想把它定位再提升一级。

它不是数据库模型，不是 Java Bean，也不是 Observation 表设计。

它定义的是：

AI 世界中的"事实（Fact）"是什么。

这是整个 AI Performance Engineer 最重要的 Domain Model。

如果 Observation 定义得好，后面的：

Timeline
Evidence
Knowledge
AI Engine

都会变得非常自然。

所以这份文档，我建议作为整个 World Model 中仅次于 M2-000 的第二重要文档。


# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Observation 的统一领域模型。

Observation 是 AI 世界中唯一合法的运行时事实（Runtime Fact）。

所有 Connector、Agent、Pipeline、Storage、Timeline、Evidence、Knowledge 都必须遵循本规范。

Observation 是整个 AI 推理链的起点。

---

# Chapter 2 Design Philosophy（设计哲学）

传统监控平台认为：

```
Metric
Log
Trace
```

是三种不同的数据。

AI Performance Engineer 不采用这种设计。

我们认为：

Metric、Log、Trace、Event、Snapshot

都只是 Observation 的不同表现形式。

Observation 是统一事实（Unified Runtime Fact）。

AI 永远分析 Observation。

而不是分析 Metric。

Observation 是整个 AI 世界唯一认可的事实表达。

---

# Chapter 3 Definition（统一定义）

## Observation

Observation 是：

> 某一个 Resource 在某一个时间点产生的一条不可变运行事实（Immutable Runtime Fact）。

Observation 永远属于一个 Resource。

Observation 永远拥有时间。

Observation 永远不可修改。

Observation 永远可追溯。

Observation 是 AI 推理唯一合法输入。

---

# Chapter 4 Characteristics（核心特征）

Observation 必须具备以下特征：

## 4.1 Immutable

Observation 一旦产生，禁止修改。

允许新增。

禁止 Update。

---

## 4.2 Timestamped

Observation 必须具有唯一时间。

推荐毫秒级时间戳。

时间必须来自采集时刻。

---

## 4.3 Resource Scoped

Observation 必须属于一个 Resource。

resourceId 为必填字段。

不存在脱离 Resource 的 Observation。

---

## 4.4 Traceable

Observation 必须能够追溯：

- Connector
- Agent
- Source
- Collection Time

必须能够回答：

"这条事实从哪里来的？"

---

## 4.5 Explainable

Observation 本身不做推理。

但必须能够成为 Evidence 的组成部分。

---

## 4.6 Append Only

Observation 永远采用 Append Only 模式。

任何更新都应产生新的 Observation。

不得覆盖历史。

---

# Chapter 5 Classification（分类）

Observation 分为五类：

## Metric Observation

数值型运行指标。

例如：

- CPU
- Memory
- TPS
- QPS
- Heap Usage

---

## Log Observation

文本运行事实。

例如：

- ERROR 日志
- GC Log
- Exception

---

## Trace Observation

调用链事实。

例如：

- HTTP Span
- RPC Span
- SQL Span

---

## Event Observation

离散事件。

例如：

- Pod Restart
- Deployment
- Redis Failover
- JVM Restart

---

## Snapshot Observation

状态快照。

例如：

- Thread Dump
- Heap Dump
- Redis INFO
- SHOW STATUS

所有分类都遵循同一 Observation Schema。

---

# Chapter 6 Lifecycle（生命周期）

Observation 生命周期如下：

```
Resource

↓

Produce Runtime Fact

↓

Connector Collect

↓

Agent Pipeline

↓

Backend Receive

↓

Storage

↓

Timeline

↓

Evidence

↓

Knowledge
```

注意：

Connector 是 Collector。

不是 Producer。

Producer 永远是 Resource。

---

# Chapter 7 Schema（统一结构）

Observation 统一逻辑模型：

| 字段 | 必填 | 描述 |
|------|------|------|
| observationId | ✔ | Observation 唯一标识 |
| resourceId | ✔ | 所属 Resource |
| type | ✔ | Observation 类型 |
| source | ✔ | 数据来源 |
| name | ✔ | 指标或事实名称 |
| value | ✔ | 数据值 |
| unit | | 单位 |
| labels | | 扩展标签 |
| payload | | 原始数据 |
| timestamp | ✔ | 采集时间 |
| connectorId | ✔ | Connector 标识 |
| agentId | ✔ | Agent 标识 |
| version | ✔ | Observation Schema 版本 |

说明：

Schema 为逻辑模型。

数据库可采用不同实现（ClickHouse、Kafka、Iceberg 等）。

---

# Chapter 8 Relationship（关系模型）

Observation 与其他模型关系如下：

```
Business System
        │
        ▼
    Resource
        │
        ▼
 Observation
        │
        ├────────► Timeline
        │
        ├────────► Evidence
        │
        └────────► AI Analysis
```

Observation 是 Runtime Layer 与 AI Layer 的唯一桥梁。

---

# Chapter 9 Ownership（归属原则）

Observation 遵循：

Architecture Law-002：

> Observation Belongs To Resource

Owner：

Resource。

Collector：

Connector。

Transport：

Agent。

Storage：

Observation Repository。

Consumer：

Timeline

Evidence

AI Engine

Knowledge Engine

任何模块不得修改 Observation。

---

# Chapter 10 Constraints（架构约束）

禁止：

Observation 不属于 Resource。

禁止：

Observation 被修改。

禁止：

Connector 创建 Resource。

禁止：

Evidence 不引用 Observation。

禁止：

Knowledge 直接来源于 Observation。

Knowledge 必须经过：

Observation

↓

Evidence

↓

Verification

↓

Knowledge

---

# Chapter 11 Versioning（版本策略）

Observation Schema：

Semantic Version。

新增字段：

Minor Version。

删除字段：

Major Version。

Connector 必须声明：

Observation Version。

Backend 必须支持：

向后兼容。

---

# Chapter 12 Examples（标准示例）

## 示例一：JVM Heap

```
Resource:
Order-Service JVM

Observation:

Type: Metric

Name: heap.used

Value: 512 MB

Timestamp:
2026-07-20 15:02:01
```

---

## 示例二：Redis Memory

```
Resource:
Redis Cluster

Observation:

Type:
Snapshot

Name:
used_memory

Value:
8.2GB
```

---

## 示例三：GC Log

```
Type:
Log

Name:
GC

Payload:
...
```

---

## 错误示例

```
CPU=95%
```

错误原因：

没有 Resource。

没有 Timestamp。

没有 Source。

不是合法 Observation。

---

# Chapter 13 Reference Implementation（参考实现）

建议实现如下：

```
com.aipe.domain.observation

├── Observation
├── ObservationType
├── ObservationSource
├── ObservationLabel
├── ObservationRepository
├── ObservationPipeline
├── ObservationValidator
├── ObservationSerializer
└── ObservationFactory
```

Reference API：

```
ObservationRepository

save()

batchSave()

query()

queryByResource()

queryTimeline()
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ Observation 必须属于一个 Resource。

✔ Observation 必须不可变。

✔ Observation 必须具有时间。

✔ Observation 必须可追溯。

✔ Observation 必须能够进入 Timeline。

✔ Observation 必须能够组成 Evidence。

✔ Observation Schema 支持未来扩展。

✔ 所有 Connector 输出 Observation。

✔ 所有 AI 输入 Observation。

---

# Chapter 15 Freeze Statement（冻结声明）

Observation 是 AI Performance Engineer 中唯一合法的 Runtime Fact。

本规范冻结后：

- 禁止修改 Observation 定义；
- 禁止新增未经过 RFC 的 Observation 类型；
- 禁止绕过 Resource 创建 Observation；
- 所有 Runtime 数据必须转换为 Observation。

任何修改必须通过 RFC 审核。

Status：Frozen







