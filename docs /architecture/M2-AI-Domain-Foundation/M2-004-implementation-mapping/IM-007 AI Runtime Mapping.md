我认为 IM-007 是整个 AI Performance Engineer 的核心文档。

如果说：

IM-001 定义了工程目录；
IM-002 定义了世界对象；
IM-003~IM-006 定义了存储和接口；

那么：

IM-007 定义了 AI 如何思考（How AI Thinks）。

这也是 AI Performance Engineer 与 Prometheus、SkyWalking、Datadog、Dynatrace 最大的区别。

因此我建议 IM-007 不叫 Engine Mapping，而正式定义为：

AI Runtime Mapping

因为我们要定义的是一套可持续运行的 AI 推理运行时。

下面是冻结版。

# AI Performance Engineer

# IM-007 AI Runtime Mapping

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
        ├── Domain Mapping
        ├── Database Mapping
        ├── ClickHouse Mapping
        ├── Graph Mapping
        ├── REST API Mapping
        ├── ★ AI Runtime Mapping ★
        ├── Connector Mapping
        ├── Repository Mapping
        └── Service Mapping
```

AI Runtime Mapping 定义 AI Performance Engineer 中所有 AI Engine 的生命周期、职责边界及协作关系。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Runtime 的统一架构。

目标：

建立统一 AI Runtime。

建立统一推理流水线。

建立统一知识沉淀机制。

建立统一优化执行机制。

AI Runtime 是整个 AI World 的大脑。

---

# Chapter 2 Design Philosophy（设计哲学）

AI Runtime：

永远消费 Domain。

永远不消费数据库。

统一原则：

Reality

↓

Fact

↓

Evidence

↓

Knowledge

↓

Recommendation

↓

Execution

↓

Optimization

↓

Learning

AI Runtime 负责：

理解世界。

而不是：

采集世界。

---

# Chapter 3 Runtime Pipeline

统一 Runtime：

```
Observation

↓

Timeline Builder

↓

Evidence Engine

↓

Verification Engine

↓

Knowledge Engine

↓

Recommendation Engine

↓

Execution Planner

↓

Optimization Engine

↓

Learning Engine
```

整个 Runtime：

严格单向流动。

禁止反向调用。

---

# Chapter 4 Engine Responsibilities

Timeline Builder

职责：

构建 Resource Timeline。

输入：

Observation。

输出：

Timeline。

---

Evidence Engine

职责：

发现异常。

形成 Evidence。

输入：

Timeline。

Topology。

输出：

Evidence。

---

Verification Engine

职责：

验证 Evidence 是否成立。

输入：

Evidence。

Knowledge。

输出：

Verified Evidence。

---

Knowledge Engine

职责：

形成 Knowledge。

输入：

Verified Evidence。

输出：

Knowledge。

---

Recommendation Engine

职责：

生成 Recommendation。

输入：

Knowledge。

输出：

Recommendation。

---

Execution Planner

职责：

生成执行计划。

输入：

Recommendation。

输出：

Execution Plan。

---

Optimization Engine

职责：

评估优化效果。

输入：

Execution。

Observation。

输出：

Optimization。

---

Learning Engine

职责：

持续更新 Knowledge。

输入：

Optimization。

输出：

Knowledge Version N+1。

---

# Chapter 5 Runtime Package Mapping

```
ai

├── timeline
├── evidence
├── verification
├── knowledge
├── recommendation
├── execution
├── optimization
├── learning
├── reasoning
├── memory
└── runtime
```

runtime：

统一协调所有 Engine。

---

# Chapter 6 Engine Interface

统一接口：

```
Engine<I,O>
```

统一方法：

```
initialize()

execute()

shutdown()

name()

version()

supports()

health()
```

所有 Engine：

必须实现统一生命周期。

---

# Chapter 7 Runtime Lifecycle

统一生命周期：

```
CREATED

↓

INITIALIZED

↓

READY

↓

RUNNING

↓

PAUSED

↓

STOPPING

↓

STOPPED

↓

FAILED
```

Runtime Manager：

统一管理生命周期。

---

# Chapter 8 Runtime Scheduling

统一调度：

```
Observation Arrival

↓

Timeline Trigger

↓

Evidence Trigger

↓

Knowledge Trigger

↓

Recommendation Trigger
```

支持：

事件驱动。

支持：

定时调度。

支持：

批量推理。

---

# Chapter 9 Context Model

统一 Runtime Context：

```
AIContext

├── Resource
├── Timeline
├── Topology
├── Observation
├── Knowledge
├── Memory
└── ExecutionHistory
```

Engine：

统一消费：

AIContext。

禁止：

直接访问 Repository。

---

# Chapter 10 Runtime Collaboration

统一协作：

```
Timeline

↓

Evidence

↓

Verification

↓

Knowledge

↓

Recommendation

↓

Execution

↓

Optimization
```

禁止：

Knowledge 调用 Timeline。

禁止：

Recommendation 修改 Evidence。

禁止：

Execution 修改 Knowledge。

---

# Chapter 11 Memory Strategy

AI Runtime：

维护：

Memory。

Memory：

包括：

Recent Memory。

Historical Memory。

Knowledge Memory。

Learning Memory。

Memory：

不得直接持久化。

Memory：

运行时管理。

---

# Chapter 12 AI Repository Mapping

AI：

只能访问：

```
ObservationRepository

RelationshipRepository

KnowledgeRepository

ExecutionRepository
```

禁止：

AI Engine：

直接写 SQL。

直接访问 ClickHouse Driver。

直接访问 Neo4j Driver。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

所有 Engine：

必须实现统一接口。

禁止：

Engine 互相引用。

统一通过 Runtime。

禁止：

Engine 保存状态。

Engine：

必须无状态。

状态：

统一进入 Runtime Context。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Runtime Pipeline 完整。

✔ Engine 生命周期统一。

✔ Engine 无状态。

✔ Runtime Context 唯一。

✔ AI 不直接访问数据库。

✔ AI 消费 Domain。

✔ Learning 闭环成立。

✔ Engine 可插拔。

✔ Runtime 可扩展。

✔ Pipeline 单向流动。

---

# Chapter 15 Freeze Statement

AI Runtime Mapping 是 AI Performance Engineer 唯一合法的 AI 推理运行时规范。

所有 AI Engine 必须运行于 Runtime。

所有推理必须遵循：

Observation

↓

Timeline

↓

Evidence

↓

Verification

↓

Knowledge

↓

Recommendation

↓

Execution

↓

Optimization

↓

Learning

任何 Runtime 修改必须通过 RFC。

Status：

Frozen.

我建议从 IM-007 开始冻结整个 AI Runtime 的四条最高法则（建议加入《AI Performance Engineer Constitution》）
AI Runtime Law-001：AI Never Reads Database Directly

AI 永远不能直接操作数据库：

❌ AI → ClickHouse

❌ AI → MySQL

❌ AI → Neo4j

正确方式：

AI

↓

Repository

↓

Domain
AI Runtime Law-002：All Reasoning Starts From Facts

所有 AI 推理必须从 Observation（事实）开始：

Observation

↓

Timeline

↓

Evidence

↓

Knowledge

禁止直接根据 Knowledge 推理新的 Knowledge。

AI Runtime Law-003：AI Engines Are Stateless

每个 Engine 都是无状态计算单元：

不保存缓存
不保存上下文
不保存历史

所有上下文统一放入 AIContext。

这样 Runtime 可以水平扩展、多线程执行，并支持未来分布式推理。

AI Runtime Law-004：Knowledge Is Produced, Never Edited

Knowledge 不允许被修改。

新的学习结果：

Knowledge v1

↓

Learning

↓

Knowledge v2

而不是：

UPDATE knowledge ...

Knowledge 永远是演化（Evolution），而不是覆盖（Overwrite）。

我还有一个建议（也是我认为 M2.5 最关键的一步）

从 IM-008 Connector Mapping 开始，我们不要再围绕"模块"设计，而要正式引入 Connector Runtime 的概念。

也就是说，Connector 不再只是一个采集插件，而是一套完整的运行时：

Connector 生命周期
Connector 调度
Connector 健康检查
Connector 配置热更新
Connector 注册发现
Connector 隔离
Connector 限流
Connector 错误恢复

这样，整个系统将形成两大运行时：

AI Runtime（负责理解世界）
Connector Runtime（负责感知世界）

二者通过 Observation 这一唯一事实模型连接。这会使整个 AI Performance Engineer 的架构更加完整，也更容易支撑未来几十种 Connector 的持续扩展。
