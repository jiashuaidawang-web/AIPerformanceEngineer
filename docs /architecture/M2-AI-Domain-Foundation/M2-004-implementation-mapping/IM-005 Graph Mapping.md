我建议冻结 IM-004。

同时，我建议把 IM-005 再提升一个层级。

很多系统（CMDB、Neo4j 等）把 Graph 当数据库。

我们不要这么设计。

我们的 Graph 是：

AI World 的空间模型（Spatial Model）

ClickHouse 描述：

时间（Time）

Graph 描述：

空间（Space）

Timeline：

时间维度。

Topology：

空间维度。

AI：

就在：

Time + Space

上推理。

所以 IM-005 不只是 Neo4j Mapping，而是：

Space Mapping

这是 AI Performance Engineer 和传统 APM 最大区别之一。


# AI Performance Engineer

# IM-005 Graph Mapping

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
        ├── Database Mapping
        ├── ClickHouse Mapping
        ├── ★ Graph Mapping ★
        ├── REST API Mapping
        └── AI Engine Mapping
```

Graph Mapping 定义 AI World 中所有 Resource 与 Relationship 的空间表达方式。

---

# Chapter 1 Purpose（设计目标）

本规范定义 Resource、Relationship、Topology 在 Graph 中的统一建模方式。

目标：

- 建立统一 Resource Graph
- 建立统一 Topology
- 支撑 AI 路径推理
- 支撑依赖分析
- 支撑影响分析（Impact Analysis）
- 支撑 Root Cause Analysis（RCA）

Graph 是 AI World 的空间模型，而不是关系数据库。

---

# Chapter 2 Design Philosophy（设计哲学）

Graph：

表达空间关系。

ClickHouse：

表达时间事实。

MySQL：

表达业务元数据。

三者共同组成 AI World。

Graph 永远描述：

"谁与谁连接。"

而不是：

"发生了什么。"

---

# Chapter 3 Graph Scope

统一进入 Graph：

| Domain | Graph |
|----------|-------|
| Resource | ✔ |
| Relationship | ✔ |
| Topology | ✔（动态） |
| Dependency | ✔ |
| Cluster | ✔ |
| Business System | ✔ |
| Namespace | ✔ |
| Observation | ✘ |
| Evidence | ✘ |
| Knowledge | ✘ |

Graph 不保存 Observation。

---

# Chapter 4 Node Mapping

统一 Node：

```
BusinessSystem

Application

Cluster

Namespace

Host

VM

Container

Pod

Process

JVM

Redis

MySQL

Kafka

RocketMQ

ClickHouse

Nginx

Service

API
```

所有 Node：

继承：

```
Resource
```

统一 ResourceId。

---

# Chapter 5 Edge Mapping

统一 Edge：

```
BELONGS_TO

DEPLOYS_ON

RUNS_ON

CONNECTS_TO

DEPENDS_ON

CALLS

USES

HOSTS

MEMBER_OF

PART_OF
```

禁止：

自由命名。

禁止：

字符串关系。

统一采用枚举。

---

# Chapter 6 Topology Construction

Topology：

运行时动态生成。

来源：

```
Resource

+

Relationship
```

Topology：

禁止单独存储。

Graph：

只保存：

Node

Edge

Topology：

查询生成。

---

# Chapter 7 Graph Schema

统一 Node 属性：

```
resource_id

resource_type

resource_name

business_system

environment

cluster

namespace

status

version
```

统一 Edge 属性：

```
relationship_type

created_at

updated_at

confidence

source
```

---

# Chapter 8 Query Strategy

统一查询：

路径：

```
Shortest Path
```

依赖：

```
Upstream

Downstream
```

影响：

```
Impact Analysis
```

拓扑：

```
Neighborhood
```

禁止：

全图扫描。

---

# Chapter 9 Repository Mapping

统一 Repository：

```
RelationshipRepository

TopologyRepository
```

Repository：

负责：

Graph 查询。

返回：

Domain：

Relationship。

Topology。

禁止：

Graph Driver 暴露到 Domain。

---

# Chapter 10 AI Usage Mapping

AI Engine：

统一使用 Graph：

```
Timeline

+

Topology

↓

Evidence

↓

Knowledge
```

AI：

可以：

路径推理。

传播分析。

故障扩散分析。

依赖分析。

禁止：

AI 直接访问 Neo4j Driver。

---

# Chapter 11 Graph Synchronization

统一同步流程：

```
Discovery

↓

Resource

↓

Relationship

↓

Graph Repository

↓

Graph Database
```

Observation：

不会更新 Graph。

Graph：

只描述结构。

---

# Chapter 12 Storage Strategy

推荐：

Neo4j。

后续支持：

JanusGraph。

NebulaGraph。

Graph API：

必须抽象。

禁止：

Domain 绑定 Neo4j。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

Node：

统一来自 Resource。

Edge：

统一来自 Relationship。

Topology：

禁止建表。

禁止保存。

禁止缓存整个拓扑。

Graph：

必须通过 Repository。

禁止：

业务代码直接写 Cypher。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Resource 全部进入 Graph。

✔ Relationship 全部进入 Graph。

✔ Topology 动态生成。

✔ Observation 不进入 Graph。

✔ Node 类型统一。

✔ Edge 类型统一。

✔ Repository 抽象。

✔ AI Engine 使用 Graph Repository。

✔ 不依赖具体图数据库实现。

✔ Graph 可替换。

---

# Chapter 15 Freeze Statement

Graph Mapping 是 AI Performance Engineer 唯一合法的空间模型规范。

Graph 描述：

Resource。

Relationship。

Topology。

Graph 不保存 Observation。

Graph 不保存 Knowledge。

Topology 永远动态生成。

任何修改必须通过 RFC。

Status：

Frozen.

我建议同时冻结三条新的 Architecture Law（建议进入《AI Performance Engineer Constitution》）
Architecture Law-003：Graph Stores Space, Not Time
Graph
↓
Space

ClickHouse
↓
Time

Graph 永远描述对象之间的空间关系。

它不记录时间序列。

Architecture Law-004：Topology Is A View
Resource
+
Relationship
↓
Topology

Topology 是运行时生成的世界视图，不是数据库中的实体。

任何时候都禁止维护一张 topology 表。

Architecture Law-005：Relationship Is First-Class Citizen

这是我认为整个 M2 最重要的一条。

在很多系统里，关系只是一个字段。

而在 AI Performance Engineer 中：

Resource

Host

JVM

Redis

这些都是对象。

但是：

DEPENDS_ON

CALLS

RUNS_ON

BELONGS_TO

同样也是对象。

Relationship 拥有：

自己的 ID
自己的类型
自己的来源
自己的置信度（confidence）
自己的生命周期

AI 推理很多时候不是推理 Resource，而是推理 Relationship。

这也是我们整个 AI World 与传统 CMDB、APM 最大的区别之一。

我建议将这三条 Law 冻结进入项目宪法（Constitution），作为整个 M2 世界模型的正式组成部分。