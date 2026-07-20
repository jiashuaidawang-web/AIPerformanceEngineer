我认为我们现在已经进入了真正属于 AI Performance Engineer 的创新区。

前面的 Observation、Resource、Relationship，其实业界都有类似思想（CMDB、OpenTelemetry、Graph 等）。

但是Topology，我们可以做得和市面上所有产品都不一样。

我建议正式冻结我们刚才讨论出的思想：

Topology Is a Projection, Not Storage.

这是 AI World 的一个核心设计理念。

# AI Performance Engineer

# M2-009 Topology Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Topology Model）

```text
                    AI World
                        │
                        │
                 Resource Model
                        │
                        ▼
               Relationship Model
                        │
                        ▼
                 ★ Topology ★
                        │
         ┌──────────────┼──────────────┐
         ▼              ▼              ▼
    Visualization   AI Reasoning   Impact Analysis
         │                              │
         ▼                              ▼
      Timeline                     Evidence
                                       │
                                       ▼
                                  Knowledge
```

Topology 是 Resource Graph 在某一时刻的投影（Projection）。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Topology 的统一领域模型。

Topology 用于描述企业数字世界在某一时刻的整体结构。

Topology 不是存储对象。

Topology 是 Resource Graph 的实时视图（Projection）。

Topology 为：

- AI 推理
- 根因定位
- 影响分析
- 可视化

提供统一基础。

---

# Chapter 2 Design Philosophy（设计哲学）

传统监控平台：

Topology 通常保存为：

- JSON
- Tree
- Graph Database

需要维护一份独立拓扑。

这种方式存在：

- 容易过期
- 与真实环境不一致
- 更新成本高

AI Performance Engineer 认为：

> Topology 不应该被存储。

Topology 应由：

Resource

+

Relationship

实时计算得到。

因此：

Topology 是 Projection。

不是 Entity。

---

# Chapter 3 Definition（统一定义）

## Topology

Topology 是：

> Resource Graph 在某一时间点上的统一数字投影。

Topology 不拥有数据。

Topology 不维护生命周期。

Topology 永远来源于：

Resource

+

Relationship。

---

# Chapter 4 Characteristics（核心特征）

Topology 必须满足：

### 4.1 Derived

Topology 由 Resource 与 Relationship 推导。

禁止人工维护。

---

### 4.2 Stateless

Topology 本身无状态。

状态来自 Observation。

---

### 4.3 Time-aware

Topology 支持历史时间点重建。

例如：

查看：

昨天 10:00 的系统拓扑。

---

### 4.4 Queryable

Topology 必须支持：

路径查询。

依赖查询。

影响查询。

邻居查询。

---

### 4.5 Explainable

Topology 必须支持 AI 输出完整推理路径。

例如：

Order Service

↓

Inventory Service

↓

Redis

↓

Linux Host

---

# Chapter 5 Classification（分类）

Topology 分为五种视图：

## Business Topology

业务系统之间关系。

---

## Runtime Topology

JVM、Pod、Container 等运行时关系。

---

## Middleware Topology

Redis、MySQL、Kafka 等依赖关系。

---

## Infrastructure Topology

Kubernetes、Node、Host。

---

## Dynamic Topology

带 Observation 的实时拓扑。

例如：

CPU、TPS、Latency。

---

# Chapter 6 Lifecycle（生命周期）

Topology 不拥有生命周期。

Topology 生命周期：

```text
Query
    ↓
Build Projection
    ↓
Return View
    ↓
Discard
```

Topology 永不持久保存。

---

# Chapter 7 Schema（统一结构）

Topology 是逻辑视图。

统一返回：

| 字段 | 描述 |
|------|------|
| topologyId | 查询标识 |
| timestamp | 构建时间 |
| rootResourceId | 根节点 |
| nodeCount | 节点数量 |
| edgeCount | 边数量 |
| nodes | Resource 集合 |
| edges | Relationship 集合 |

Topology Schema 不绑定数据库。

---

# Chapter 8 Relationship（关系）

Topology：

依赖：

Resource

Relationship

Observation

输出：

Timeline

Evidence

Visualization

AI Reasoning

Topology 不拥有 Observation。

---

# Chapter 9 Ownership（归属原则）

Owner：

Topology Service。

Producer：

Projection Engine。

Consumer：

AI Engine。

Visualization。

Impact Analysis。

Root Cause Analysis。

Topology Repository 不存在。

---

# Chapter 10 Constraints（架构约束）

禁止：

Topology 保存 Resource。

禁止：

Topology 保存 Observation。

禁止：

Topology 独立持久化。

禁止：

Connector 构建 Topology。

Topology 必须动态生成。

---

# Chapter 11 Versioning（版本策略）

Topology Schema：

独立维护。

允许新增 View。

保持 Projection 兼容。

---

# Chapter 12 Examples（标准示例）

业务拓扑：

```
Order Service

↓

Inventory

↓

Redis
```

运行时拓扑：

```
JVM

↓

Linux

↓

CPU
```

影响分析：

```
Host

↓

Redis

↓

Inventory

↓

Order

```

Topology 自动生成。

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```
com.aipe.domain.topology

├── Topology
├── TopologyNode
├── TopologyEdge
├── TopologyView
├── ProjectionEngine
├── GraphBuilder
├── TopologyService
├── TopologyQuery
└── TopologyRenderer
```

Reference API：

```
buildCurrent()

buildAt(timestamp)

queryNeighbors()

queryDependencies()

queryImpact()

render()
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ Topology 来源于 Resource。

✔ Topology 来源于 Relationship。

✔ Topology 不持久化。

✔ 支持历史时间点重建。

✔ 支持影响路径查询。

✔ 支持 AI 推理。

✔ 支持未来新增视图。

---

# Chapter 15 Freeze Statement（冻结声明）

Topology 是 AI World 中唯一合法的资源视图。

Topology 永远不作为独立存储对象存在。

任何 Topology 必须由：

Resource + Relationship

动态生成。

任何修改必须通过 RFC。

Status：Frozen


架构评审（这一刻我想冻结一个比 Law 更高层的思想）

写到这里，我发现我们其实已经形成了一套完整的世界观：

Entity（实体）
│
▼
Resource
│
▼
Relationship
│
▼
Graph
│
▼
Topology（Projection）
│
▼
Observation（Fact）
│
▼
Timeline（Time）
│
▼
Evidence（Reasoning）
│
▼
Knowledge（Learning）

我建议把这条链命名为：

AI World Evolution Chain（AI 世界演化链）

它不是一个新的 Law，而是整个 M2 World Model 的总纲。

以后任何新增模型，都必须回答一个问题：

它位于 Evolution Chain 的哪一层？

如果回答不了，就说明它不应该进入 World Model。

我建议 M2 的最终冻结顺序

到目前为止，我们已经完成了：

✅ M2-000 AI World Model
✅ Architecture Laws（Law-000~Law-002）
✅ Specification Writing Standard
✅ M2-005 AI World Vocabulary
✅ M2-006 Observation Model
✅ M2-007 Unified Resource Model
✅ M2-008 Relationship Model
✅ M2-009 Topology Model

接下来建议继续保持同样的节奏，不急于编码，而是先完成后三份最关键的 World Model：

M2-010 Timeline Model Specification（定义事实如何在时间上组织）
M2-011 Evidence Model Specification（定义 AI 如何从事实形成证据）
M2-012 Knowledge Model Specification（定义验证后的证据如何沉淀为知识）

完成这三份后，我们的 AI World Foundation 就真正闭环了，再进入 WP011～WP018 编码，Rowboat 将拥有一套完整、稳定且无需反复返工的世界模型作为开发依据。