我认为 IM-002 是整个 Implementation Mapping 最重要的一份文档。

原因很简单：

从这一份开始，我们不再讨论"世界是什么"，而是正式规定：

世界中的每一个对象，在 Java 里面到底长什么样。

它实际上会成为 WP011~WP020 的编码依据。

# AI Performance Engineer

# IM-002 Domain Model Mapping

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

```
AI World Foundation
        │
        ▼
Implementation Mapping
        │
        ├── Java Package
        ├── ★ Domain Model ★
        ├── Database
        ├── ClickHouse
        ├── Graph
        ├── API
        ├── AI Engine
        └── Connector
```

Domain Model Mapping 是整个 AI World 在 Java 中的唯一领域实现。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 所有 World Model 在 Java Domain Layer 中的统一映射。

目标：

建立统一领域模型。

所有业务对象必须来自 World Model。

禁止直接依据数据库建模。

禁止依据接口建模。

禁止依据前端建模。

Domain 永远以 AI World 为唯一来源。

---

# Chapter 2 Design Philosophy（设计哲学）

Domain 是：

AI World 的软件表达。

Domain：

不属于数据库。

不属于 Spring。

不属于 MyBatis。

不属于 REST。

Domain：

属于业务世界。

所有 Domain Object 必须保持：

Persistence Ignorance（持久化无感知）。

---

# Chapter 3 Domain Object Mapping

统一映射如下：

| AI World | Java Domain |
|------------|---------------------------|
| Resource | Resource |
| Observation | Observation |
| Relationship | Relationship |
| Topology | Topology |
| Timeline | Timeline |
| Evidence | Evidence |
| Verification | Verification |
| Knowledge | Knowledge |
| Recommendation | Recommendation |
| Execution | Execution |
| Optimization | Optimization |

禁止新增平行对象。

例如：

ObservationEntity。

ObservationModel。

ObservationDO。

全部禁止。

唯一对象：

Observation。

---

# Chapter 4 Aggregate Design（聚合设计）

采用 DDD Aggregate Root。

```
Resource
    │
    ├── Observation
    ├── Relationship
    └── Timeline
```

```
Evidence
    │
    ├── Verification
    └── Knowledge
```

```
Recommendation
    │
    └── Execution
            │
            └── Optimization
```

聚合根：

负责一致性。

子对象：

不得独立修改。

---

# Chapter 5 Value Object Mapping

统一 Value Object：

```
ResourceId

ObservationId

EvidenceId

KnowledgeId

ExecutionId

RelationshipId

TimelineId
```

统一 Value Object：

```
ResourceName

ObservationType

Timestamp

Confidence

Version

Status
```

禁止：

String id。

Long id。

全部使用 Value Object。

---

# Chapter 6 Entity Design

每个 Domain Entity 必须至少包含：

```
Id

Business Identity

Business Behavior

Version

CreatedTime

UpdatedTime
```

禁止：

贫血模型（Anemic Domain）。

Entity 必须拥有业务方法。

例如：

Observation：

```
attachToResource()

validate()

belongsTo()

isExpired()
```

而不是：

Getter/Setter。

---

# Chapter 7 Factory Design

每个 Aggregate 必须拥有 Factory。

例如：

```
ResourceFactory

ObservationFactory

EvidenceFactory

KnowledgeFactory

ExecutionFactory
```

Factory：

负责：

创建合法对象。

禁止：

Controller：

new Resource()

---

# Chapter 8 Repository Mapping

每个 Aggregate Root：

拥有唯一 Repository。

```
ResourceRepository

ObservationRepository

EvidenceRepository

KnowledgeRepository

ExecutionRepository
```

Repository：

返回：

Domain Object。

禁止：

DTO。

禁止：

EntityDO。

禁止：

Map。

---

# Chapter 9 Domain Service Mapping

Domain Service：

负责：

跨 Aggregate 行为。

例如：

```
TimelineBuilder

EvidenceReasoner

KnowledgeBuilder

ExecutionPlanner

OptimizationEvaluator
```

Domain Service：

禁止事务。

禁止 SQL。

禁止 HTTP。

---

# Chapter 10 Domain Event Mapping

统一 Domain Event：

```
ObservationCreatedEvent

EvidenceGeneratedEvent

KnowledgeVerifiedEvent

ExecutionFinishedEvent

OptimizationCompletedEvent
```

Domain Event：

只表达：

已经发生。

禁止：

Command。

---

# Chapter 11 Domain Package Structure

```
domain

├── shared
│
├── resource
│      ├── Resource
│      ├── ResourceFactory
│      ├── ResourceRepository
│      ├── ResourceSpecification
│      └── ResourceDomainService
│
├── observation
│
├── relationship
│
├── topology
│
├── timeline
│
├── evidence
│
├── verification
│
├── knowledge
│
├── recommendation
│
├── execution
│
└── optimization
```

所有 Domain 子目录：

保持一致结构。

---

# Chapter 12 Domain Dependency Rules

Domain：

不得依赖：

Spring。

不得依赖：

Controller。

不得依赖：

MyBatis。

不得依赖：

Redis。

不得依赖：

ClickHouse。

不得依赖：

Kafka。

Domain：

只依赖：

Java。

以及：

Shared Kernel。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

所有 Domain：

必须保持 POJO。

禁止：

@Entity。

@Mapper。

@Service。

@Repository。

@Transactional。

全部禁止。

Domain：

不得引用：

任何框架。

任何 Domain Object：

必须：

具有业务行为。

禁止：

只有 Getter。

Setter。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ World Object 与 Domain 一一对应。

✔ Domain 保持纯净。

✔ Aggregate Root 唯一。

✔ Value Object 独立。

✔ Entity 拥有业务行为。

✔ Factory 创建对象。

✔ Repository 返回 Domain。

✔ Domain Service 不访问数据库。

✔ Domain Event 表达事实。

✔ Domain 无框架依赖。

---

# Chapter 15 Freeze Statement

Domain Model Mapping 是 AI Performance Engineer 唯一合法的领域对象规范。

所有 Java Domain 必须严格按照本规范实现。

禁止：

Entity 与 Domain 混用。

禁止：

数据库模型驱动 Domain。

禁止：

Controller 驱动 Domain。

任何修改必须通过 RFC。

Status：

Frozen.

我建议对整个 Domain 再增加一条冻结规则（建议纳入 IM-011）

这是我刚刚发现的一个工程规范，非常重要。

Domain Law-001：One Concept, One Domain Object

意思是：

一个业务概念，在整个 Domain 中只能有一个官方对象。

例如：

Observation —— 只能有 Observation
不允许同时出现：
ObservationEntity
ObservationModel
ObservationDO
ObservationRecord
ObservationInfo

同样：

Resource 只能是 Resource
Evidence 只能是 Evidence

DTO、Request、Response 可以存在于 API 层，但 Domain 永远只有一个对象。

这条规则会极大降低后续工程复杂度，也是很多大型项目后期维护成本高的根源之一。我建议在 IM-011《Implementation Rules》 中把它升级为正式工程规范并冻结。