M2-008 Relationship Model Specification（定义"对象之间如何连接"）

我认为 M2-008 Relationship Model Specification 是整个 M2 中最容易被低估，但实际上决定 AI 能否真正"推理"的规范。

Observation 告诉 AI："发生了什么。"

Resource 告诉 AI："世界里有什么。"

Relationship 才告诉 AI："它们之间为什么会互相影响。"

没有 Relationship，AI 永远只能做单点分析；有了 Relationship，AI 才能做真正的因果推理和影响分析

# AI Performance Engineer

# M2-008 Relationship Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Relationship Model）

```text
                     AI World
                         │
                         │
                   World Vocabulary
                         │
                         │
                    Resource Model
                         │
                         ▼
                 ★ Relationship ★
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
    Topology        Observation      AI Reasoning
        │                                 │
        ▼                                 ▼
    Timeline                        Evidence
                                        │
                                        ▼
                                   Knowledge
```

Relationship 定义 AI 世界中 Resource 与 Resource 之间的所有连接关系。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Relationship 的统一领域模型。

Relationship 用于描述 Resource 与 Resource 之间的逻辑、运行和依赖关系。

Relationship 是构建 Topology、进行 Root Cause Analysis、Impact Analysis 和 AI 推理的基础。

所有 Resource 必须通过 Relationship 构建统一数字世界。

---

# Chapter 2 Design Philosophy（设计哲学）

传统监控平台通常将资源关系固化在配置中：

- JVM 属于某台机器
- Redis 属于某个集群
- Pod 属于 Deployment

这种方式：

- 不统一
- 不可扩展
- 无法推理

AI Performance Engineer 认为：

> Resource 本身不知道世界。

世界是由 Relationship 连接出来的。

因此：

Topology 不是对象。

Topology 是 Relationship 的结果。

Relationship 是 AI World 的"神经网络"。

---

# Chapter 3 Definition（统一定义）

## Relationship

Relationship 是：

> 两个 Resource 之间具有明确语义、方向和生命周期的一条连接。

Relationship 不属于 Source Resource。

也不属于 Target Resource。

Relationship 是独立领域对象。

Relationship 自身拥有：

- Identity
- Type
- Direction
- Lifecycle
- Metadata

---

# Chapter 4 Characteristics（核心特征）

Relationship 必须具备以下特征：

## 4.1 Directed（有方向）

Relationship 必须定义：

Source Resource

↓

Target Resource

例如：

Order Service

CALLS

Inventory Service

不能省略方向。

---

## 4.2 Typed（有类型）

Relationship 必须拥有唯一类型。

例如：

CALLS

DEPENDS_ON

RUNS_ON

MEMBER_OF

USES

HOSTS

BELONGS_TO

---

## 4.3 Independent（独立对象）

Relationship 拥有独立生命周期。

删除 Resource 不代表立即删除 Relationship。

Relationship 可独立管理。

---

## 4.4 Observable（可观察）

Relationship 本身也可以产生 Observation。

例如：

HTTP Dependency

Latency

Error Rate

Availability

这些 Observation 属于：

Relationship Resource。

---

## 4.5 Versioned（可演进）

Relationship Schema 独立维护版本。

允许未来新增 Relationship Type。

---

# Chapter 5 Classification（关系分类）

Relationship 分为六大类。

---

## Ownership Relationship（归属）

表示上下级关系。

例如：

BELONGS_TO

MEMBER_OF

PART_OF

---

## Deployment Relationship（部署）

表示部署关系。

例如：

RUNS_ON

HOSTS

DEPLOYED_ON

---

## Dependency Relationship（依赖）

表示依赖关系。

例如：

DEPENDS_ON

USES

CONNECTS_TO

---

## Invocation Relationship（调用）

表示业务调用关系。

例如：

CALLS

INVOKES

REQUESTS

---

## Network Relationship（网络）

表示网络连接。

例如：

CONNECTED_TO

ROUTES_TO

LOAD_BALANCES

---

## AI Relationship（推理）

AI 自动推导产生。

例如：

IMPACTS

CORRELATED_WITH

LIKELY_CAUSE_OF

DERIVED_FROM

AI Relationship 不属于 Discovery。

属于 AI Analysis。

---

# Chapter 6 Lifecycle（生命周期）

Relationship 生命周期：

```text
Discover
      ↓
Register
      ↓
Validate
      ↓
Active
      ↓
Changed
      ↓
Inactive
      ↓
Archived
```

Discovery 可以来自：

Connector

Kubernetes

CMDB

AI Discovery

人工配置

---

# Chapter 7 Schema（统一结构）

统一逻辑模型：

| 字段 | 必填 | 描述 |
|------|------|------|
| relationshipId | ✔ | 全局唯一标识 |
| relationshipType | ✔ | Relationship 类型 |
| sourceResourceId | ✔ | Source Resource |
| targetResourceId | ✔ | Target Resource |
| direction | ✔ | SINGLE / BIDIRECTIONAL |
| discoveredBy | ✔ | Discovery 来源 |
| confidence | ✔ | AI/Discovery 可信度 |
| labels | | 扩展标签 |
| status | ✔ | 生命周期状态 |
| discoveredAt | ✔ | 首次发现时间 |
| updatedAt | ✔ | 最近更新时间 |
| version | ✔ | Schema Version |

Relationship 不绑定数据库。

---

# Chapter 8 Relationship（关系之间的关系）

Relationship 自身也允许形成关系。

例如：

```
Service A

↓

CALLS

↓

Service B

↓

DEPENDS_ON

↓

Redis
```

AI 可以沿着 Relationship 图进行传播分析。

Relationship 是 Graph，而不是 Tree。

---

# Chapter 9 Ownership（归属原则）

Owner：

Relationship Repository。

Producer：

Discovery Engine

AI Analysis Engine

Consumer：

Topology

Timeline

Evidence

Knowledge

Optimization

Connector 不直接维护 Relationship。

---

# Chapter 10 Constraints（架构约束）

禁止：

Resource 保存完整拓扑。

禁止：

Relationship 保存在 Connector。

禁止：

循环创建非法 Relationship。

禁止：

Relationship 无方向。

禁止：

Relationship 引用不存在的 Resource。

---

# Chapter 11 Versioning（版本策略）

Relationship Schema：

Semantic Version。

新增类型：

Minor Version。

删除类型：

Major Version。

保持向后兼容。

---

# Chapter 12 Examples（标准示例）

示例一：

```
Order Service

CALLS

Inventory Service
```

---

示例二：

```
Inventory Service

USES

Redis Cluster
```

---

示例三：

```
Redis Cluster

RUNS_ON

Linux Host
```

---

AI 推理：

```
Linux CPU High

↓

IMPACTS

↓

Redis

↓

IMPACTS

↓

Inventory Service

↓

IMPACTS

↓

Order Service
```

AI 可以沿着 Relationship 自动完成影响分析。

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```
com.aipe.domain.relationship

├── Relationship
├── RelationshipType
├── RelationshipDirection
├── RelationshipStatus
├── RelationshipRepository
├── RelationshipDiscoveryService
├── RelationshipValidator
├── RelationshipGraph
├── RelationshipRegistry
└── RelationshipFactory
```

Reference API：

```
register()

remove()

query()

queryChildren()

queryParents()

queryDependencies()

queryImpactGraph()
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ 所有 Relationship 都引用合法 Resource。

✔ 所有 Relationship 都具有方向。

✔ Relationship 类型统一管理。

✔ Topology 能由 Relationship 自动生成。

✔ AI 能基于 Relationship 进行影响分析。

✔ Connector 不直接维护 Relationship。

✔ Relationship Schema 支持扩展。

---

# Chapter 15 Freeze Statement（冻结声明）

Relationship 是 AI World 中唯一合法的资源连接模型。

本规范冻结后：

- 禁止在 Resource 中直接保存拓扑；
- 禁止新增未审批的 Relationship Type；
- 所有资源连接必须通过 Relationship 建模；
- 所有 Topology 必须由 Relationship 自动构建。

任何修改必须通过 RFC 审核。

Status：Frozen

架构评审（Architecture Review）

到这里，我建议我们暂停一下编码，先冻结一个世界模型原则。

因为写完 Resource 和 Relationship 后，一个设计思想已经完全清晰：

Resource
│
│ 通过 Relationship 相连
▼
Graph
│
▼
Topology
│
▼
Observation 在 Graph 上流动
│
▼
Timeline
│
▼
Evidence
│
▼
Knowledge

这里有一个非常重要的结论：

Topology 不是一张静态图。

Topology 是 Resource + Relationship 在某一时刻的投影（Projection）。

也就是说：

Resource 是节点（Node）。
Relationship 是边（Edge）。
Topology 是当前时刻整个 Graph 的视图（View）。

因此我建议在下一份 M2-009 Topology Model Specification 中正式冻结一个新的理念：

Topology Is a Projection, Not Storage.

即：Topology 不应该单独存储一份"拓扑数据"，而应该由 Resource 和 Relationship 动态构建出来。这样可以避免拓扑与真实世界脱节，也能支持任意时间点的历史拓扑重建，为后续 Timeline、Evidence 和 AI 推理打下非常坚实的基础。