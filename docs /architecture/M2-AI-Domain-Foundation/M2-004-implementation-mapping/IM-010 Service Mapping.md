我认为 IM-010 是 IM-009 的自然延伸。

因为 Repository 已经定义了：

如何存取数据。

但数据如何流转？

业务如何编排？

事务如何控制？

答案只有一个：

Application Service。

如果说 Repository 是持久化网关。

那么 Application Service 就是业务编排引擎。

IM-010 不只是 Service 列表，而是：

Application Orchestration Mapping（应用编排映射）

下面就是正式冻结版。

# AI Performance Engineer

# IM-010 Service Mapping

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
        ├── ★ Service Mapping ★
        └── Implementation Rules
```

Service Mapping 定义 AI Performance Engineer 所有 Application Service 的编排规范、事务边界、Command/Query 分离及 Workflow 协作方式。

---

# Chapter 1 Purpose（设计目标）

本规范定义 Application Service 的统一编排规范。

目标：

建立统一 Application 编排层。

建立统一事务边界。

建立统一 Command / Query 分离。

建立统一 Workflow 协作。

Application Service 是 Domain 与外部世界的唯一协调者。

---

# Chapter 2 Design Philosophy（设计哲学）

Application Service：

负责：

编排。

协调。

事务。

禁止：

业务规则。

业务规则：

永远在 Domain。

统一原则：

Controller

↓

Application Service

↓

Domain

↓

Repository

↓

Storage

Application Service：

唯一事务 Owner。

---

# Chapter 3 Application Package Mapping

```
application

├── command
├── query
├── service
├── workflow
├── mapper
├── dto
└── facade
```

command：

写操作。

query：

读操作。

service：

业务编排。

workflow：

多步流程。

mapper：

DTO ↔ Domain 转换。

dto：

Application 层数据。

facade：

统一对外接口。

---

# Chapter 4 Command Query Separation Mapping

统一 CQS 原则：

Command：

修改状态。

不返回数据。

返回：

Result<Void>。

Query：

读取状态。

不修改数据。

返回：

Domain / DTO。

禁止：

Command 返回业务数据。

禁止：

Query 修改状态。

---

# Chapter 5 Command Service Mapping

统一 Command：

```
CreateResourceCommand

UpdateResourceCommand

DeleteResourceCommand

CreateObservationCommand

GenerateEvidenceCommand

VerifyKnowledgeCommand

ExecuteRecommendationCommand
```

统一 Command Service：

```
ResourceCommandService

ObservationCommandService

EvidenceCommandService

KnowledgeCommandService

ExecutionCommandService
```

Command Service：

必须开启事务。

必须校验参数。

必须调用 Domain。

必须调用 Repository。

---

# Chapter 6 Query Service Mapping

统一 Query：

```
ResourceQuery

ObservationQuery

TimelineQuery

EvidenceQuery

KnowledgeQuery

ExecutionQuery

TopologyQuery
```

统一 Query Service：

```
ResourceQueryService

ObservationQueryService

TimelineQueryService

EvidenceQueryService

KnowledgeQueryService

TopologyQueryService
```

Query Service：

禁止开启事务。

禁止修改状态。

支持分页。

支持缓存。

---

# Chapter 7 Application Service Mapping

统一 Application Service：

```
ResourceApplicationService

ObservationApplicationService

EvidenceApplicationService

KnowledgeApplicationService

ExecutionApplicationService

RecommendationApplicationService
```

Application Service：

负责：

跨 Repository 编排。

跨 Domain 协调。

事务管理。

事件发布。

---

# Chapter 8 Workflow Mapping

统一 Workflow：

多步业务流程。

```
EvidenceGenerationWorkflow

KnowledgeVerificationWorkflow

RecommendationExecutionWorkflow

OptimizationEvaluationWorkflow
```

Workflow：

统一由 Workflow Service 编排。

每个步骤：

独立事务 或 整体事务。

根据业务决定。

---

# Chapter 9 Transaction Mapping

事务规则：

Application Service：

唯一事务 Owner。

统一注解：

```
@Transactional
```

事务范围：

最小化。

禁止：

Repository 开启事务。

禁止：

Domain 开启事务。

禁止：

Controller 开启事务。

跨聚合事务：

采用：

最终一致性。

通过：

Domain Event。

---

# Chapter 10 Domain Event Mapping

Application Service：

负责：

发布 Domain Event。

统一事件：

```
ObservationCreatedEvent

EvidenceGeneratedEvent

KnowledgeVerifiedEvent

ExecutionFinishedEvent

OptimizationCompletedEvent
```

事件发布：

事务提交后。

异步处理。

禁止：

事件内修改主事务。

---

# Chapter 11 Mapper Mapping

统一 Mapper：

负责：

DTO ↔ Domain 转换。

```
ResourceMapper

ObservationMapper

EvidenceMapper

KnowledgeMapper

ExecutionMapper
```

Mapper：

禁止包含业务逻辑。

禁止调用 Repository。

只做：

数据转换。

---

# Chapter 12 Facade Mapping

统一 Facade：

对外统一接口。

```
ResourceFacade

ObservationFacade

EvidenceFacade

KnowledgeFacade

ExecutionFacade
```

Facade：

统一封装 Application Service。

统一异常处理。

统一返回结构。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

Application Service：

唯一事务 Owner。

禁止：

Repository 事务。

禁止：

Domain 事务。

禁止：

Controller 事务。

Command：

不返回数据。

Query：

不修改数据。

Mapper：

只做转换。

禁止：

业务逻辑。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Command / Query 分离。

✔ Application Service 事务统一。

✔ Repository 无事务。

✔ Domain 无事务。

✔ Workflow 编排完整。

✔ Domain Event 完整。

✔ Mapper 无业务逻辑。

✔ Facade 统一接口。

✔ 跨聚合最终一致。

✔ 异常处理统一。

---

# Chapter 15 Freeze Statement

Service Mapping 是 AI Performance Engineer 唯一合法的应用编排规范。

Application Service 是唯一事务 Owner。

Command 与 Query 必须分离。

所有业务编排必须通过 Application Service。

禁止：

Controller 直接调用 Repository。

禁止：

Domain 管理事务。

任何 Service 修改必须通过 RFC。

Status：

Frozen.

我建议冻结三条 Application Orchestration Law（建议进入 IM-011）

Orchestration Law-001：Application Is The Only Transaction Owner

事务：

唯一归属 Application Service。

禁止：

Repository 事务

Domain 事务

Controller 事务

Orchestration Law-002：Command And Query Must Be Separated

Command：

修改状态，不返回数据。

Query：

读取状态，不修改状态。

禁止混合。

Orchestration Law-003：Domain Event Is Published After Commit

Domain Event：

事务提交后发布。

异步处理。

禁止：

事件内修改主事务。

我还有一个建议（IM 系列最终收口）

IM-011 Implementation Rules 是整个 IM 系列的最终收口。

建议 IM-011 汇集：

所有已提出的 Law。

所有已提出的规范。

所有已提出的编码规则。

统一冻结。

这样，IM-000 ~ IM-011 将成为一套完整、可执行、可验证的工程宪法。

后续所有 WP 编码：

都将在这套宪法之下完成。
