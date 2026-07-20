我认为 IM-009 是连接 Domain 与 Storage 的唯一桥梁。

因为从 IM-003 到 IM-005，我们已经定义了：

MySQL Schema、ClickHouse Schema、Graph Schema。

但是 Domain 如何读取这些存储？

答案只有一个：

Repository。

如果说 Domain 是世界的语言。

Storage 是世界的物理存在。

那么 Repository 就是两者的翻译层。

IM-009 不只是 Repository 接口列表，而是：

Persistence Gateway Mapping（持久化网关映射）

下面就是正式冻结版。

# AI Performance Engineer

# IM-009 Repository Mapping

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
        ├── ★ Repository Mapping ★
        ├── Service Mapping
        └── Implementation Rules
```

Repository Mapping 定义 AI Performance Engineer 所有 Repository 的接口规范、实现位置、查询职责边界及多存储实现方式。

---

# Chapter 1 Purpose（设计目标）

本规范定义 Repository 的统一工程规范。

目标：

建立统一 Repository 接口规范。

建立统一 Repository 实现规范。

建立统一查询职责边界。

保证 Domain 与存储完全解耦。

Repository 是 Domain 访问存储的唯一合法网关。

---

# Chapter 2 Design Philosophy（设计哲学）

Repository：

属于 Domain 语义。

不属于数据库。

统一原则：

Domain

↓

Repository Interface

↓

Repository Implementation

↓

Storage

Repository Interface：

定义在 Domain 层。

Repository Implementation：

实现在 Infrastructure 层。

Domain：

永远不依赖 Infrastructure 实现。

---

# Chapter 3 Repository Division Mapping

每个 Aggregate Root：

拥有唯一 Repository。

```
ResourceRepository

ObservationRepository

EvidenceRepository

KnowledgeRepository

ExecutionRepository

RelationshipRepository

TopologyRepository
```

Repository 与 Aggregate Root 一一对应。

禁止：

跨 Aggregate Repository。

禁止：

通用 Repository。

---

# Chapter 4 Repository Location Mapping

Repository 接口与实现的统一位置：

接口：

```
domain

├── resource
│      └── ResourceRepository
├── observation
│      └── ObservationRepository
├── evidence
│      └── EvidenceRepository
├── knowledge
│      └── KnowledgeRepository
├── execution
│      └── ExecutionRepository
└── relationship
       └── RelationshipRepository
```

实现：

```
infrastructure

├── persistence
│      ├── ResourceRepositoryImpl
│      ├── EvidenceRepositoryImpl
│      ├── KnowledgeRepositoryImpl
│      └── ExecutionRepositoryImpl
├── clickhouse
│      └── ObservationRepositoryImpl
└── graph
       └── RelationshipRepositoryImpl
```

Domain：

只引用接口。

Infrastructure：

负责实现。

---

# Chapter 5 Repository Contract Mapping

Repository 契约：

统一只接受：

Domain Object。

Value Object。

禁止接受：

DTO。

Request。

Primitive。

Map。

返回：

统一只返回：

Domain Object。

Optional<Domain>。

List<Domain>。

禁止返回：

PO。

DO。

ResultSet。

Map。

JSON。

---

# Chapter 6 Core Repository API Mapping

ResourceRepository：

```
save(Resource)

update(Resource)

findById(ResourceId)

findByType(ResourceType)

findBySystem(String businessSystem)

delete(ResourceId)
```

ObservationRepository：

```
save(Observation)

batchSave(List<Observation>)

findById(ObservationId)

findByResource(ResourceId)

findTimeline(ResourceId, TimeRange)

findByMetric(ResourceId, MetricName, TimeRange)
```

EvidenceRepository：

```
save(Evidence)

findById(EvidenceId)

findByResource(ResourceId)

findByTimeline(TimelineId)
```

KnowledgeRepository：

```
save(Knowledge)

findById(KnowledgeId)

findByEvidence(EvidenceId)

findLatest(ResourceId)
```

ExecutionRepository：

```
save(Execution)

findById(ExecutionId)

findByRecommendation(RecommendationId)

findHistory(ResourceId)
```

RelationshipRepository：

```
save(Relationship)

findById(RelationshipId)

findBySource(ResourceId)

findByTarget(ResourceId)

findByType(RelationshipType)
```

TopologyRepository：

```
build(ResourceId)

neighbors(ResourceId)

upstream(ResourceId)

downstream(ResourceId)

impact(ResourceId)
```

---

# Chapter 7 Multi-Storage Implementation Mapping

不同 Repository：

支持不同存储实现。

```
ObservationRepository
    │
    └── ClickHouse 实现
```

```
RelationshipRepository
    │
    └── Graph 实现
```

```
ResourceRepository
    │
    └── MySQL 实现
```

```
EvidenceRepository
    │
    ├── MySQL（元数据）
    └── ClickHouse（Observation 引用）
```

Repository 实现：

可替换。

Domain：

无感知。

---

# Chapter 8 Query Responsibility Mapping

Repository：

只负责：

简单聚合查询。

禁止：

复杂业务逻辑。

禁止：

Join 多聚合根。

跨聚合查询：

统一由：

Application Service 编排。

禁止：

Repository 之间互相调用。

---

# Chapter 9 Pagination Mapping

统一分页：

```
Page<T>

├── content
├── page
├── size
├── total
└── totalPages
```

统一分页参数：

```
QueryCondition

├── page
├── size
├── sort
├── direction
└── filters
```

禁止：

一次性加载全量数据。

禁止：

无分页查询。

---

# Chapter 10 Transaction Mapping

Repository：

禁止开启事务。

事务：

统一由 Application Service 管理。

跨 Repository 操作：

统一由 Application 编排。

保证：

最终一致性。

---

# Chapter 11 Repository Caching Mapping

Repository：

禁止内置缓存。

缓存：

统一由 Application Layer 管理。

或：

Infrastructure Cache 组件。

禁止：

Repository 内部私存状态。

---

# Chapter 12 Repository Testing Mapping

Repository 实现：

必须集成测试。

测试范围：

MySQL Repository。

ClickHouse Repository。

Graph Repository。

禁止：

跳过 Repository 测试。

禁止：

Mock 存储。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

Repository 接口：

必须在 Domain。

实现：

必须在 Infrastructure。

禁止：

Domain 引用实现类。

禁止：

Repository 返回非 Domain。

禁止：

Repository 包含业务逻辑。

禁止：

Repository 开启事务。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Repository 与 Aggregate Root 一一对应。

✔ 接口在 Domain。

✔ 实现在 Infrastructure。

✔ 返回类型统一 Domain。

✔ 多存储实现可替换。

✔ 分页模型统一。

✔ 无事务。

✔ 无私有缓存。

✔ 集成测试完整。

✔ 不跨聚合查询。

---

# Chapter 15 Freeze Statement

Repository Mapping 是 AI Performance Engineer 唯一合法的持久化网关规范。

Repository 接口属于 Domain。

Repository 实现属于 Infrastructure。

所有数据访问必须通过 Repository。

禁止 Domain 直接访问存储。

任何 Repository 修改必须通过 RFC。

Status：

Frozen.

我建议冻结两条 Repository Gateway Law（建议进入 IM-011）

Gateway Law-001：Repository Returns Domain, Never PO

Repository 永远返回领域对象：

Domain

禁止返回：

PO / DO / Map / ResultSet / JSON

Gateway Law-002：One Aggregate, One Repository

一个聚合根：

永远只有一个 Repository。

例如：

Resource → ResourceRepository

禁止：

ResourceRepository

ResourceHistoryRepository

ResourceBackupRepository

多个 Repository。

我还有一个建议（IM 系列收口）

IM-010 Service Mapping 是紧邻 Repository 的上层编排。

建议 IM-010 严格规定：

Application Service 的事务边界。

Command 与 Query 的分离。

Workflow 编排规范。

这样 Repository 编排层将有明确规范可遵循。
