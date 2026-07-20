我认为 IM-000 不应该只是一个"Overview"。

它应该成为整个 Implementation Mapping 的总纲。

以后：

IM001~IM011
WP011~WP030
Rowboat
GPT
Claude

全部引用它。

它的作用就是回答一句话：

AI World 是如何映射到 Software World 的？

建议直接保存为：

04-implementation-mapping/IM-000-Implementation-Overview.md

# AI Performance Engineer

# IM-000 Implementation Overview

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
        ├──────────── Java
        ├──────────── Database
        ├──────────── ClickHouse
        ├──────────── Graph
        ├──────────── REST API
        ├──────────── AI Engine
        └──────────── Connector

↓

WP011~WP030 Coding
```

Implementation Mapping 是 AI World 与工程实现之间唯一合法的映射层。

---

# Chapter 1 Purpose（设计目标）

Implementation Mapping 定义：

AI World

↓

Software World

的统一映射关系。

它回答：

世界中的对象，

在软件中到底是什么。

Implementation Mapping 不定义业务。

Implementation Mapping 不定义领域。

Implementation Mapping 只定义：

工程实现。

所有编码必须遵循 Mapping。

禁止：

开发人员自行理解 Specification 后自由发挥。

---

# Chapter 2 Design Philosophy（设计哲学）

Architecture First

↓

World Model

↓

Implementation Mapping

↓

Code

代码不是设计。

代码只是世界模型的实现。

Implementation Mapping 是：

Architecture

与

Code

之间唯一桥梁。

---

# Chapter 3 AI World → Software World

统一映射如下：

| AI World | Software World |
|------------|----------------|
| Resource | Domain Entity |
| Observation | Fact Object |
| Timeline | Timeline Aggregate |
| Evidence | AI Explanation Object |
| Knowledge | Verified Knowledge Object |
| Recommendation | Recommendation Object |
| Execution | Execution Object |
| Optimization | Optimization Object |

所有 World Object：

都必须拥有唯一工程实现。

---

# Chapter 4 Layer Architecture

整个工程采用分层架构。

```text
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
```

AI Engine：

位于：

Application Layer。

Domain：

永远保持纯净。

---

# Chapter 5 Package Mapping

统一 Package：

```text
com.aipe

├── api
├── application
├── domain
├── infrastructure
├── repository
├── connector
├── ai
├── common
├── config
└── bootstrap
```

禁止：

跨层引用。

禁止：

循环依赖。

---

# Chapter 6 Domain Mapping

Domain：

只包含：

世界对象。

例如：

```text
domain

├── resource
├── observation
├── relationship
├── topology
├── timeline
├── evidence
├── verification
├── knowledge
├── recommendation
├── execution
└── optimization
```

Domain：

禁止：

Spring Annotation。

禁止：

MyBatis Annotation。

禁止：

JPA Annotation。

禁止：

Controller。

禁止：

SQL。

保持 POJO。

---

# Chapter 7 Storage Mapping

统一原则：

Observation

↓

ClickHouse

Relationship

↓

Graph

Configuration

↓

MySQL

Knowledge

↓

MySQL

Execution

↓

MySQL

Topology：

禁止持久化。

Topology：

动态计算。

---

# Chapter 8 AI Engine Mapping

AI Engine：

只处理：

Domain Object。

禁止：

直接读取：

数据库。

统一流程：

```text
Repository

↓

Timeline Builder

↓

Evidence Engine

↓

Knowledge Engine

↓

Recommendation Engine

↓

Execution Planner
```

AI：

禁止：

跨层。

---

# Chapter 9 API Mapping

REST：

只暴露：

Application Service。

禁止：

Controller

↓

Repository。

Controller：

禁止：

SQL。

Controller：

禁止：

AI。

Controller：

只负责：

DTO

↓

Application。

---

# Chapter 10 Data Flow Mapping

统一数据流：

```text
Connector

↓

Observation

↓

ObservationRepository

↓

ClickHouse

↓

TimelineBuilder

↓

EvidenceEngine

↓

KnowledgeEngine

↓

RecommendationEngine

↓

ExecutionPlanner
```

所有数据流：

必须遵循此链路。

---

# Chapter 11 Dependency Rules

统一依赖方向：

```text
API

↓

Application

↓

Domain

↓

Repository

↓

Storage
```

禁止：

反向依赖。

禁止：

循环依赖。

---

# Chapter 12 Naming Rules

统一命名：

Entity：

Resource

Observation

Evidence

Knowledge

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

禁止：

Model

Bean

Data

Temp

Manager

Util

Helper

等无意义命名。

---

# Chapter 13 Engineering Principles

所有代码必须遵循：

DDD

SOLID

Clean Architecture

Hexagonal Architecture

Composition Over Inheritance

Single Responsibility

禁止：

God Class。

禁止：

Anemic Domain。

禁止：

Hard Code。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ 每个 World Object 有唯一 Java 实现。

✔ 每个 Domain Object 有唯一 Repository。

✔ Observation 进入 ClickHouse。

✔ Relationship 支持 Graph。

✔ Topology 动态生成。

✔ AI Engine 不直接访问数据库。

✔ API 不直接访问 Repository。

✔ 所有依赖方向正确。

✔ Package 结构统一。

✔ 命名统一。

---

# Chapter 15 Freeze Statement

Implementation Mapping 是 AI World 与工程实现之间唯一合法映射。

任何 Java 工程必须首先遵循：

World Model。

其次遵循：

Implementation Mapping。

最后进行：

Code Implementation。

禁止：

通过修改代码改变世界模型。

禁止：

绕过 Mapping 直接编码。

任何修改：

必须通过 RFC。

Status：

Frozen.

M 系列的整体规划（建议冻结）

完成 IM-000 后，我们按下面顺序推进，每一份都采用相同的 15 章节模板，但内容聚焦到一个工程维度：

文档	核心回答的问题
IM-000	世界模型如何映射到软件世界（总纲）
IM-001 Java Package Mapping	每个领域对象放在哪个包，模块边界如何划分
IM-002 Domain Model Mapping	每个 World Object 对应哪些 Java 类、聚合、值对象
IM-003 Database Schema Mapping	MySQL 的表、字段、主外键、约束如何设计
IM-004 ClickHouse Mapping	Observation、Timeline、Evidence 等如何建表、分区、索引
IM-005 Graph Mapping	Resource、Relationship 在图模型中的节点和边如何表示
IM-006 REST API Mapping	API、DTO、版本策略、统一响应模型
IM-007 AI Engine Mapping	Timeline Builder、Evidence Engine、Knowledge Engine 等如何协作
IM-008 Connector Mapping	Connector SDK 与各 Connector 如何接入 Domain
IM-009 Repository Mapping	Repository 接口、实现、查询职责边界
IM-010 Service Mapping	Application Service 的编排与事务边界
IM-011 Implementation Rules	Rowboat 编码规范、代码约束、命名、测试、质量门禁

完成 IM-011 后，我们再正式启动 WP011《Unified Resource Model》的编码。

这样，后续所有 WP 都是在统一的工程规范之下实现，而不是一边写代码一边继续讨论架构。这也是从"架构设计阶段"自然过渡到"工程实现阶段"的最佳节点