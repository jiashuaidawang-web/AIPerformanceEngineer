我认为 IM-003 是整个工程的数据基石。

但这里我想纠正一个以后会影响整个项目几十万行代码的设计。

我建议 Database Mapping 先冻结一个原则

不是所有数据都放 MySQL。

而是：

每一种数据，放到最适合它的存储。

所以 Database Mapping 不应该理解成：

MySQL Schema

而应该理解成：

Persistence Mapping（持久化映射）

因此 Database Schema 只是其中一部分。

整个 Persistence Layer：

Observation
↓
ClickHouse

Relationship
↓
Graph

Knowledge
↓
MySQL

Configuration
↓
MySQL

Resource
↓
MySQL

Timeline
↓
ClickHouse（逻辑构建）

Evidence
↓
MySQL（元数据）
+
ClickHouse（Observation引用）

Execution
↓
MySQL

所以 IM-003 其实定义的是：

哪些对象应该进入关系数据库。

ClickHouse 和 Graph 我们放到 IM-004、IM-005。

我建议冻结：

One Domain → One Primary Storage

一个 Domain Object 只能有一个 Primary Storage。

例如：

Domain	Primary Storage
Resource	MySQL
Observation	ClickHouse
Relationship	Graph
Evidence	MySQL
Knowledge	MySQL
Execution	MySQL

以后：

任何 AI 都不能自己决定。

下面就是正式版本。
# AI Performance Engineer

# IM-003 Database Schema Mapping

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
        ├── Domain Model
        ├── ★ Database Schema ★
        ├── ClickHouse
        ├── Graph
        └── AI Engine
```

Database Schema Mapping 定义 AI World 在关系型数据库中的唯一持久化规范。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 在关系型数据库中的统一 Schema。

目标：

- 建立统一的数据模型
- 保证 Domain 与数据库解耦
- 明确哪些对象进入 MySQL
- 明确哪些对象不允许进入 MySQL
- 为 Repository 提供唯一持久化规范

数据库不是领域模型。

数据库只是领域模型的持久化载体。

---

# Chapter 2 Design Philosophy（设计哲学）

遵循：

Domain First

↓

Persistence Later

数据库永远不能驱动 Domain。

Schema 必须来源于 World Model。

禁止：

先建表，再建领域模型。

---

# Chapter 3 Primary Storage Mapping

统一 Primary Storage：

| Domain Object | Primary Storage |
|---------------|----------------|
| Resource | MySQL |
| Observation | ClickHouse |
| Relationship | Graph |
| Topology | Dynamic |
| Timeline | ClickHouse（逻辑构建） |
| Evidence | MySQL |
| Verification | MySQL |
| Knowledge | MySQL |
| Recommendation | MySQL |
| Execution | MySQL |
| Optimization | MySQL |

Topology 禁止持久化。

---

# Chapter 4 Database Selection

关系数据库：

MySQL 8.x

职责：

- Resource
- Configuration
- Metadata
- Knowledge
- Recommendation
- Execution
- Verification

禁止保存：

Observation。

Topology。

---

# Chapter 5 Schema Design Principles

统一原则：

- UUID 作为业务主键
- bigint 自增作为内部主键（可选）
- created_at
- updated_at
- version
- status

所有业务表必须支持：

乐观锁。

逻辑删除（如适用）。

审计字段。

---

# Chapter 6 Core Tables

Resource：

```
resource
```

主要字段：

- resource_id
- resource_type
- resource_name
- parent_resource_id
- business_system
- cluster
- namespace
- environment
- status
- version
- created_at
- updated_at

---

Evidence：

```
evidence
```

主要字段：

- evidence_id
- title
- root_resource_id
- confidence
- timeline_id
- reasoning_summary
- status
- created_at

---

Verification：

```
verification
```

主要字段：

- verification_id
- evidence_id
- verification_type
- result
- operator
- verified_at

---

Knowledge：

```
knowledge
```

主要字段：

- knowledge_id
- evidence_id
- verification_id
- title
- recommendation
- success_rate
- version
- created_at

---

Recommendation：

```
recommendation
```

主要字段：

- recommendation_id
- knowledge_id
- target_resource_id
- recommendation_type
- content
- priority
- status

---

Execution：

```
execution
```

主要字段：

- execution_id
- recommendation_id
- executor
- execution_plan
- execution_result
- started_at
- finished_at

---

Optimization：

```
optimization
```

主要字段：

- optimization_id
- execution_id
- before_snapshot
- after_snapshot
- improvement_score
- created_at

---

# Chapter 7 Relationship Mapping

统一采用：

外键使用业务 ID。

例如：

```
Knowledge

↓

Evidence

↓

Verification
```

采用：

```
evidence_id
verification_id
```

避免数据库级强外键约束。

由 Domain 保证一致性。

---

# Chapter 8 Index Strategy

统一索引：

主键：

```
*_id
```

业务索引：

```
resource_type

business_system

environment

status

created_at
```

Evidence：

增加：

```
(root_resource_id, created_at)
```

Knowledge：

增加：

```
(success_rate)
```

Execution：

增加：

```
(recommendation_id)
```

---

# Chapter 9 Version Strategy

所有核心表：

必须包含：

```
version
```

用于：

乐观锁。

历史追踪。

Schema Evolution。

Knowledge：

支持版本升级。

---

# Chapter 10 Repository Mapping

Repository 与表一一对应：

```
ResourceRepository

↓

resource
```

```
KnowledgeRepository

↓

knowledge
```

Repository：

返回 Domain。

禁止返回：

PO。

DO。

Map。

---

# Chapter 11 Transaction Rules

事务：

仅 Application Layer 开启。

Repository：

禁止开启事务。

Domain：

禁止事务。

跨 Aggregate：

通过 Application Service 编排。

---

# Chapter 12 Migration Strategy

统一采用：

Flyway。

禁止：

手工改库。

所有 Schema：

必须版本化。

例如：

```
V001__init.sql

V002__resource.sql

V003__knowledge.sql
```

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

不得新增业务表。

不得修改 Frozen Schema。

不得直接写 SQL 到 Service。

Repository：

唯一访问数据库。

Schema：

必须通过 Flyway 演进。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Domain 与 Schema 解耦。

✔ Observation 不进入 MySQL。

✔ Topology 不持久化。

✔ Resource 使用 MySQL。

✔ Knowledge 使用 MySQL。

✔ Repository 一一对应。

✔ Flyway 管理 Schema。

✔ Schema 支持版本。

✔ Application 管理事务。

✔ Repository 不包含业务逻辑。

---

# Chapter 15 Freeze Statement

Database Schema Mapping 是 AI Performance Engineer 唯一合法的关系数据库规范。

任何数据库设计必须来源于 World Model。

任何 Schema 修改必须通过 RFC。

禁止数据库驱动领域模型。

Status：

Frozen.
我建议在 IM 系列增加一个工程级铁律（建议放入 IM-011）

Persistence Law-001：Right Data, Right Storage（正确的数据放到正确的存储）

Configuration
↓
MySQL

Resource
↓
MySQL

Observation
↓
ClickHouse

Relationship
↓
Graph

Binary Files
↓
Object Storage

Cache
↓
Redis

这意味着我们不会把所有东西都塞进 MySQL，也不会把 ClickHouse 当数据库万能使用，更不会把 Graph 当查询数据库。每一种存储都有明确职责，这会让整个 AI Performance Engineer 在未来面对海量 Observation 和复杂 AI 推理时，依然保持清晰、稳定且易于扩展。这条原则我建议作为整个 Persistence Layer 的最高规范并冻结。

