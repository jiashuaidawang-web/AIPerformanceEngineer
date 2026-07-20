我认为，Timeline 是整个 AI Performance Engineer 从"监控系统"进化为"AI 系统"的分水岭。

很多监控平台都有时间序列（Time Series）。

但是它们认为：

时间序列 = Metric 曲线。

而我们认为：

Timeline 是 Resource 在时间维度上的完整生命轨迹（Life Story）。

这是两个完全不同的思想。

Prometheus 存的是：

CPU
Memory
TPS

我们存的是：

Redis

↓

09:00 启动

↓

09:03 Memory 增长

↓

09:08 QPS 激增

↓

09:12 GC

↓

09:15 Connection Pool 满

↓

09:17 Timeout

↓

09:18 恢复

AI 推理的对象，不是曲线，而是时间上的事实演化。

所以 Timeline 的定位应该重新定义。

# AI Performance Engineer

# M2-010 Timeline Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Timeline Model）

```text
                   AI World
                       │
                  Resource
                       │
                  Observation
                       │
                       ▼
                ★ Timeline ★
                       │
         ┌─────────────┼──────────────┐
         ▼             ▼              ▼
    AI Reasoning   Root Cause     Playback
         │             │              │
         ▼             ▼              ▼
      Evidence     Knowledge     Verification
```

Timeline 定义 Resource 在时间维度上的完整运行历史。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Timeline 的统一领域模型。

Timeline 是 Observation 在时间维度上的有序组织。

Timeline 不只是时间序列。

Timeline 是 Resource 的运行历史（Life Story）。

Timeline 为：

- AI 推理
- 故障回放
- 根因定位
- 趋势分析
- 知识沉淀

提供统一时间基础。

---

# Chapter 2 Design Philosophy（设计哲学）

传统监控：

Metric → Time Series

AI Performance Engineer：

Observation → Timeline

区别：

传统系统分析的是：

> 一个指标随时间变化。

AI Performance Engineer 分析的是：

> 一个 Resource 在时间上的行为演化。

Timeline 是行为。

不是曲线。

Observation 是事实。

Timeline 是事实的发展过程。

---

# Chapter 3 Definition（统一定义）

## Timeline

Timeline 是：

> 某一个 Resource 在连续时间上的 Observation 有序集合。

Timeline 不产生 Observation。

Timeline 不修改 Observation。

Timeline 仅负责组织 Observation。

Timeline 是 Resource 的历史。

---

# Chapter 4 Characteristics（核心特征）

Timeline 必须具备以下特征。

---

## 4.1 Ordered（有序）

所有 Observation 必须按时间排序。

Timeline 永远保持时间有序。

---

## 4.2 Immutable（不可修改）

Observation 不允许修改。

Timeline 仅允许追加。

禁止覆盖历史。

---

## 4.3 Resource Scoped（资源维度）

Timeline 永远属于：

一个 Resource。

禁止跨 Resource Timeline。

跨 Resource 分析属于 Evidence。

---

## 4.4 Replayable（可回放）

Timeline 必须支持：

任意时间窗口回放。

例如：

09:00

↓

09:05

↓

09:10

↓

09:15

AI 能完整重建运行过程。

---

## 4.5 Explainable（可解释）

Timeline 必须能够回答：

发生了什么？

什么时候发生？

先发生什么？

后发生什么？

---

## 4.6 Continuous（连续）

Observation 可以缺失。

Timeline 永远连续。

时间窗口允许为空。

---

# Chapter 5 Classification（分类）

Timeline 分为五类。

---

## Resource Timeline

单个 Resource 的生命周期。

例如：

Redis Timeline。

---

## Business Timeline

一个 Business System 的历史。

例如：

Order Service Timeline。

---

## Scenario Timeline

一次压测。

一次发布。

一次秒杀。

---

## Incident Timeline

一次故障的发展过程。

例如：

CPU High

↓

GC

↓

Redis Timeout

↓

订单失败

---

## AI Timeline

AI 自动推理生成。

例如：

Evidence Timeline。

---

# Chapter 6 Lifecycle（生命周期）

Timeline 生命周期：

```text
Observation Append
        ↓
Timeline Update
        ↓
AI Query
        ↓
Evidence Generate
        ↓
Knowledge Archive
```

Timeline 永远由 Observation 自动维护。

---

# Chapter 7 Schema（统一结构）

统一逻辑模型：

| 字段 | 必填 | 描述 |
|------|------|------|
| timelineId | ✔ | Timeline 标识 |
| resourceId | ✔ | 所属 Resource |
| startTime | ✔ | 起始时间 |
| endTime | ✔ | 结束时间 |
| observationCount | ✔ | Observation 数量 |
| observations | ✔ | Observation 列表 |
| version | ✔ | Schema Version |

Timeline 是逻辑视图。

Observation 仍然独立存储。

---

# Chapter 8 Relationship（关系）

Timeline：

来源：

Observation

输入：

Evidence

Knowledge

Visualization

Replay Engine

Timeline 不拥有 Observation。

---

# Chapter 9 Ownership（归属原则）

Owner：

Timeline Service。

Producer：

Observation Pipeline。

Consumer：

Evidence Engine。

Knowledge Engine。

Replay Engine。

Topology 不维护 Timeline。

---

# Chapter 10 Constraints（架构约束）

禁止：

Timeline 修改 Observation。

禁止：

Timeline 保存 Resource。

禁止：

Timeline 保存 Relationship。

禁止：

Connector 创建 Timeline。

Timeline 必须自动生成。

---

# Chapter 11 Versioning（版本策略）

Timeline Schema：

独立维护。

新增能力：

Minor Version。

保持兼容。

---

# Chapter 12 Examples（标准示例）

Redis Timeline：

```text
09:00
Started

↓

09:03
Memory Used = 3 GB

↓

09:07
Connected Clients = 1500

↓

09:12
Latency = 20 ms

↓

09:15
Latency = 500 ms

↓

09:16
Timeout

↓

09:18
Recovered
```

Timeline 清晰描述了运行过程。

---

AI Replay：

```text
Replay 09:10 ~ 09:18

↓

重建 Redis 故障全过程
```

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```text
com.aipe.domain.timeline

├── Timeline
├── TimelineWindow
├── TimelineEvent
├── TimelineRepository
├── TimelineBuilder
├── TimelineService
├── TimelineReplayEngine
├── TimelineQuery
└── TimelineValidator
```

Reference API：

```java
buildTimeline(resourceId);

appendObservation();

queryTimeline();

queryWindow();

replayTimeline();

queryLatest();
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ Timeline 来源于 Observation。

✔ Timeline 永远属于一个 Resource。

✔ Timeline 按时间排序。

✔ Timeline 支持回放。

✔ Timeline 支持时间窗口查询。

✔ Timeline 不修改 Observation。

✔ Timeline 可作为 AI 推理输入。

---

# Chapter 15 Freeze Statement（冻结声明）

Timeline 是 AI World 中唯一合法的时间组织模型。

Timeline 不保存事实。

Timeline 不修改事实。

Timeline 仅组织事实。

任何 AI 推理必须基于 Timeline。

任何修改必须通过 RFC。

Status：Frozen

我建议这里冻结一个新的世界原则（不是 Law，而是 World Principle）

随着 Timeline 完成，我发现我们整个世界模型已经出现了一条非常自然的演化链：

Resource
│
产生（Produce）
▼
Observation
│
组织（Organize）
▼
Timeline
│
解释（Explain）
▼
Evidence
│
验证（Verify）
▼
Knowledge

这条链比之前更准确，因为它强调的是职责，而不是对象关系。

因此，我建议在 M2 文档中增加一页（不是新的 Law，而是 World Principle）：

Everything that happens is an Observation.

Everything remembered is a Timeline.

Everything explained is Evidence.

Everything verified becomes Knowledge.

这四句话可以成为整个 AI Performance Engineer 的核心价值观，也会让后面的 M2-011 Evidence Model 和 M2-012 Knowledge Model 有一个非常自然、统一的设计起点。