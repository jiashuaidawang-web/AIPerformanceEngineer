我认为 IM-011 是整个 IM 系列最重要的文档。

因为从 IM-002 到 IM-010，我们不断提出新的工程法则：

Domain Law

Persistence Law

Architecture Law

AI Runtime Law

Connector Runtime Law

Gateway Law

Orchestration Law

每一条都是在具体工程上下文中发现的。

但是它们一直分散在各自文档中。

如果没有一份统一法典：

不同 AI 会记住不同法则。

Rowboat 编码会逐渐偏离。

Frozen 状态将形同虚设。

IM-011 不只是编码规则，而是：

Engineering Codex（工程法典）

下面就是正式冻结版。

# AI Performance Engineer

# IM-011 Engineering Codex

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
        ├── AI Runtime Mapping
        ├── Connector Runtime Mapping
        ├── Repository Mapping
        ├── Service Mapping
        └── ★ Engineering Codex ★
```

Engineering Codex 是 AI Performance Engineer 所有工程法则、编码规范、命名约束、测试标准与质量门禁的统一法典。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 的统一工程法典。

目标：

汇集所有已提出法则。

统一冻结为正式法典。

建立统一编码规范。

建立统一命名约束。

建立统一测试标准。

建立统一质量门禁。

Engineering Codex 是 Rowboat 编码的最高执行依据。

---

# Chapter 2 Design Philosophy（设计哲学）

Engineering Codex：

高于代码。

高于模块。

高于单个 IM。

统一原则：

World Model

↓

Implementation Mapping

↓

Engineering Codex

↓

Rowboat Code

代码不得违反 Codex。

Codex 不得违反 Constitution。

---

# Chapter 3 Codex Classification（法典分类）

全法域分为九大类：

```
Domain Law（领域法则）

Persistence Law（持久化法则）

Architecture Law（架构法则）

Connector Runtime Law（Connector 运行时法则）

AI Runtime Law（AI 运行时法则）

Gateway Law（网关法则）

Orchestration Law（编排法则）

Naming Law（命名法则）

Quality Law（质量法则）
```

所有法则：

Status：Frozen。

---

# Chapter 4 Domain Law（领域法则）

Domain Law-001：One Concept, One Domain Object

一个业务概念：

在整个 Domain 中只能有一个官方对象。

例如：

Observation —— 只能有 Observation

不允许：

ObservationEntity

ObservationModel

ObservationDO

ObservationRecord

ObservationInfo

DTO、Request、Response 可存在于 API 层，但 Domain 永远只有一个对象。

---

Domain Law-002：Domain Is Persistence Ignorant

Domain：

永远不知道持久化。

永远不知道数据库。

永远不知道 Schema。

Domain 只表达：

业务世界。

---

Domain Law-003：Entity Must Have Behavior

Entity 必须拥有业务方法。

禁止：

贫血模型（Anemic Domain）。

禁止：

只有 Getter / Setter。

---

Domain Law-004：Value Object Is Immutable

Value Object：

创建后不得修改。

全部字段：

final。

全部赋值：

构造时。

---

Domain Law-005：Aggregate Root Enforces Consistency

聚合根：

负责一致性。

子对象：

不得独立修改。

外部：

只能通过聚合根访问子对象。

---

# Chapter 5 Persistence Law（持久化法则）

Persistence Law-001：Right Data, Right Storage

正确的数据放到正确的存储：

Resource → MySQL

Observation → ClickHouse

Relationship → Graph

Binary Files → Object Storage

Cache → Redis

一种数据：

只有一个 Primary Storage。

---

Persistence Law-002：ClickHouse Stores Facts, Never Knowledge

ClickHouse 只保存事实：

Observation ✅

Timeline Facts ✅

Raw Metrics ✅

Logs ✅

Events ✅

禁止保存：

Evidence ❌

Knowledge ❌

Recommendation ❌

Optimization ❌

---

Persistence Law-003：One Fact, One Row

一个 Observation：

ClickHouse 一行。

禁止：

将 CPU、Memory、GC 等指标打包成一个 JSON 存一行。

每条 Observation：

都是独立事实。

---

Persistence Law-004：Timeline Is Computed, Never Stored

Timeline：

运行时对象。

由 Observation 查询时构建。

禁止：

维护一张 timeline 表。

---

Persistence Law-005：No Physical Foreign Key

数据库：

禁止物理外键约束。

一致性：

由 Domain 保证。

引用：

使用业务 ID。

---

# Chapter 6 Architecture Law（架构法则）

Architecture Law-001：Layered Architecture Must Be Respected

分层架构：

Presentation Layer

↓

Application Layer

↓

Domain Layer

↓

Infrastructure Layer

↓

Storage Layer

↓

Connector Layer

禁止：

跨层引用。

禁止：

反向依赖。

禁止：

循环依赖。

---

Architecture Law-002：Dependency Direction Is Fixed

统一依赖方向：

```
API
    │
    ▼
Application
    │
    ▼
Domain
    │
    ▼
Repository
    │
    ▼
Infrastructure
```

禁止：

Repository → API。

禁止：

Infrastructure → Domain。

---

Architecture Law-003：Graph Stores Space, Not Time

Graph 永远描述：

对象之间的空间关系。

不记录：

时间序列。

---

Architecture Law-004：Topology Is A View

Topology：

运行时生成的世界视图。

不是数据库中的实体。

禁止：

维护一张 topology 表。

---

Architecture Law-005：Relationship Is First-Class Citizen

Relationship：

拥有独立 ID。

拥有独立类型。

拥有独立来源。

拥有独立置信度。

拥有独立生命周期。

不只是字段。

是一个对象。

---

Architecture Law-006：API Exposes Capability, Not Database

API 面向：

业务能力（Capability）。

不是数据库表。

禁止：

```
GET /resource_table

GET /knowledge_table
```

正确：

```
GET /resources

GET /knowledge
```

---

Architecture Law-007：Controller Is A Protocol Translator

Controller 唯一职责：

HTTP ↓ DTO ↓ Application ↓ DTO ↓ HTTP

它不理解 AI。

不理解数据库。

不理解业务规则。

---

# Chapter 7 Connector Runtime Law（Connector 运行时法则）

Connector Runtime Law-001：Connector Only Produces Observation

Connector 唯一合法输出：

Observation

禁止：

Connector → Evidence

Connector → Knowledge

Connector → Recommendation

---

Connector Runtime Law-002：Connector Is Not AI

Connector 只是 Collector。

不是推理引擎。

不是分析引擎。

不是知识引擎。

Connector 永远保持：

简单、稳定、可替换。

---

Connector Runtime Law-003：Two Runtimes, One Observation

整个系统：

只有两个 Runtime。

AI Runtime：

理解世界。

Connector Runtime：

感知世界。

二者通过 Observation 唯一连接。

禁止任何第三方 Runtime。

---

Connector Runtime Law-004：Connector Lifecycle Is Managed By Runtime

Connector 生命周期：

CREATED → INITIALIZED → STARTED → RUNNING → PAUSED → STOPPING → STOPPED → FAILED

Connector：

禁止自行管理状态。

统一由 Runtime 负责。

---

Connector Runtime Law-005：Connector Failure Is Isolated

一个 Connector 失败：

不得影响其它 Connector。

通过：

ClassLoader 隔离。

线程池隔离。

内存配额隔离。

网络访问隔离。

---

# Chapter 8 AI Runtime Law（AI 运行时法则）

AI Runtime Law-001：AI Never Reads Database Directly

AI 永远不能直接操作数据库。

❌ AI → ClickHouse

❌ AI → MySQL

❌ AI → Neo4j

正确方式：

AI

↓

Repository

↓

Domain

---

AI Runtime Law-002：All Reasoning Starts From Facts

所有 AI 推理：

必须从 Observation 开始。

Observation

↓

Timeline

↓

Evidence

↓

Knowledge

禁止：

直接根据 Knowledge 推理新的 Knowledge。

---

AI Runtime Law-003：AI Engines Are Stateless

每个 Engine：

都是无状态计算单元。

不保存缓存。

不保存上下文。

不保存历史。

所有上下文统一放入：

AIContext。

---

AI Runtime Law-004：Knowledge Is Produced, Never Edited

Knowledge：

不允许被修改。

新的学习结果：

Knowledge v1

↓

Learning

↓

Knowledge v2

而不是：

UPDATE knowledge ...

Knowledge 永远是演化（Evolution），不是覆盖（Overwrite）。

---

AI Runtime Law-005：Runtime Pipeline Is Unidirectional

推理流水线：

严格单向流动。

```
Observation → Timeline → Evidence → Verification → Knowledge → Recommendation → Execution → Optimization → Learning
```

禁止：

反向调用。

---

# Chapter 9 Gateway Law（网关法则）

Gateway Law-001：Repository Returns Domain, Never PO

Repository 永远返回：

Domain

禁止返回：

PO / DO / Map / ResultSet / JSON

---

Gateway Law-002：One Aggregate, One Repository

一个聚合根：

永远只有一个 Repository。

例如：

Resource → ResourceRepository

禁止：

ResourceHistoryRepository

ResourceBackupRepository

ResourceCacheRepository

---

Gateway Law-003：Repository Interface In Domain, Implementation In Infrastructure

Repository 接口：

属于 Domain。

Repository 实现：

属于 Infrastructure。

Domain：

永远不依赖 Infrastructure 实现。

---

Gateway Law-004：Repository Contains No Business Logic

Repository：

只负责：

数据存取。

简单聚合查询。

禁止：

复杂业务逻辑。

禁止：

跨聚合 Join。

---

Gateway Law-005：Repository Is Not Transaction Owner

Repository：

禁止开启事务。

事务：

唯一归属 Application Service。

---

# Chapter 10 Orchestration Law（编排法则）

Orchestration Law-001：Application Is The Only Transaction Owner

事务：

唯一归属 Application Service。

禁止：

Repository 事务

Domain 事务

Controller 事务

---

Orchestration Law-002：Command And Query Must Be Separated

Command：

修改状态，不返回数据。

Query：

读取状态，不修改状态。

禁止混合。

---

Orchestration Law-003：Domain Event Is Published After Commit

Domain Event：

事务提交后发布。

异步处理。

禁止：

事件内修改主事务。

---

Orchestration Law-004：Mapper Is A Pure Transformer

Mapper：

只做 DTO ↔ Domain 转换。

禁止包含：

业务逻辑。

Repository 调用。

外部调用。

---

Orchestration Law-005：Facade Provides Unified Interface

Facade：

统一封装 Application Service。

统一异常处理。

统一返回结构。

禁止：

绕过 Facade 直接暴露 Service。

---

# Chapter 11 Naming Law（命名法则）

Naming Law-001：Domain Object Names Come From World Model

Entity：

Resource

Observation

Evidence

Knowledge

Execution

Recommendation

禁止新增平行对象。

禁止：

ObservationEntity / ObservationDO / ObservationModel。

---

Naming Law-002：Package Names Use Single Noun, All Lowercase

统一采用：

全部小写、单数名词。

例如：

observation

禁止：

obs / ob / orm / dal。

---

Naming Law-003：Suffix Indicates Responsibility

Repository：

ObservationRepository

Service：

ObservationApplicationService

Controller：

ObservationController

DTO：

ObservationDTO

VO：

ObservationVO

Factory：

ObservationFactory

Mapper：

ObservationMapper

---

Naming Law-004：Banned Names

禁止：

Model

Bean

Data

Temp

Manager

Util

Helper

Info

Record

V2 / V3

New / Old

这些命名：

禁止出现在工程中。

---

# Chapter 12 Quality Law（质量法则）

Quality Law-001：No TODO

禁止：

TODO。

禁止：

FIXME。

禁止：

HACK。

禁止：

XXX。

所有代码：

必须真实可运行。

---

Quality Law-002：No Mock Implementation

禁止：

空实现。

禁止：

return null。

禁止：

// 以后实现。

所有代码：

必须完整。

---

Quality Law-003：No Hard Code

禁止：

硬编码配置。

硬编码路径。

硬编码 IP。

硬编码密钥。

所有配置：

外部化。

---

Quality Law-004：No Silent Catch

禁止：

空 catch。

禁止：

catch (Exception e) { }。

所有异常：

必须处理或上报。

---

Quality Law-005：Every Repository Must Have Integration Test

Repository 实现：

必须集成测试。

范围：

MySQL / ClickHouse / Graph。

禁止：

Mock 存储。

---

Quality Law-006：Every Domain Object Must Have Unit Test

Domain 对象：

必须单元测试。

覆盖：

业务方法。

边界条件。

非法输入。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package。

不得改变 Package 名称。

不得跨层引用。

不得绕过 Mapping。

不得绕过 Repository 访问存储。

不得绕过 Application Service 访问 Domain。

不得修改 Frozen Schema。

不得修改 Frozen Law。

任何新增类：

必须放入规定 Package。

任何新增代码：

必须符合 Codex。

任何违反 Codex 的代码：

必须拒绝提交。

Codex 高于代码实现。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ 所有 Domain Law 冻结。

✔ 所有 Persistence Law 冻结。

✔ 所有 Architecture Law 冻结。

✔ 所有 Connector Runtime Law 冻结。

✔ 所有 AI Runtime Law 冻结。

✔ 所有 Gateway Law 冻结。

✔ 所有 Orchestration Law 冻结。

✔ 命名法则统一。

✔ 质量法则统一。

✔ Rowboat 编码规则统一。

---

# Chapter 15 Freeze Statement

Engineering Codex 是 AI Performance Engineer 唯一合法的工程法典。

本法典汇集：

Domain Law

Persistence Law

Architecture Law

Connector Runtime Law

AI Runtime Law

Gateway Law

Orchestration Law

Naming Law

Quality Law

所有法则：

Status：Frozen。

所有代码：

必须遵循本法典。

任何法典修改：

必须通过 RFC。

任何代码：

不得违反本法典。

本法典：

与 Constitution 具有同等冻结效力。

任何修改必须同一 RFC 流程。

Status：Frozen.

我建议正式冻结 IM 系列（最终收口）

完成 IM-011 后，整个 Implementation Mapping 已经完成。

现在我们拥有：

IM-000 Implementation Overview（总纲）

IM-001 Java Package Mapping

IM-002 Domain Model Mapping

IM-003 Database Schema Mapping

IM-004 ClickHouse Mapping

IM-005 Graph Mapping

IM-006 REST API Mapping

IM-007 AI Runtime Mapping

IM-008 Connector Runtime Mapping

IM-009 Repository Mapping

IM-010 Service Mapping

IM-011 Engineering Codex

十二份文档：

完整覆盖了

从世界模型到工程实现的全部映射。

后续：

所有 WP011~WP030 编码。

所有 Rowboat 开发。

所有 AI 协作。

都将基于这套统一的 IM 体系完成。

我建议：

IM-000 ~ IM-011

整体冻结为：

M2.5 Implementation Mapping v1.0

任何修改必须通过 RFC。

从现在开始：

项目正式进入编码阶段。

下一份文档：

WP011 Unified Resource Model Coding。
