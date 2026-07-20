我建议这份文档最后再增加一页（我认为会成为整个项目的"封面"）
AI World Overview（世界总览）
Enterprise
│
├── Business Domain
│      │
│      ├── Business System
│      │      │
│      │      ├── Resource
│      │      │      │
│      │      │      ├── Observation
│      │      │      │      │
│      │      │      │      ├── Timeline
│      │      │      │      │      │
│      │      │      │      │      ├── Evidence
│      │      │      │      │      │      │
│      │      │      │      │      │      ├── Knowledge
│      │      │      │      │      │      │      │
│      │      │      │      │      │      │      └── Optimization
│      │      │      │      │      │      │
│      │      │      │      │      │      └── Verification

我建议以后所有 World Model 文档（Observation、Resource、Relationship、Timeline……）第一页都引用这张总览图。

这样，无论是 Rowboat 还是以后加入项目的新开发者，在阅读任何一份规范时，都能立刻知道当前文档在整个 AI 世界中的位置。这会大大降低理解成本，也是 Kubernetes、OpenTelemetry 等大型项目常见的文档组织方式。




# AI Performance Engineer

# M2-000 AI World Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 对企业 IT 系统的统一世界模型（AI World Model）。

AI 不直接理解：

- CPU
- JVM
- Redis
- MySQL
- Kubernetes

AI 首先理解的是：

企业（Enterprise）。

随后逐层理解：

业务、系统、资源、事实、证据、知识。

本规范是整个 AI World 的最高领域模型。

所有 Domain、Specification、RFC、Blueprint、Reference Implementation 必须遵守本规范。

---

# Chapter 2 Design Philosophy（设计哲学）

AI Performance Engineer 不以服务器为中心。

不以中间件为中心。

不以监控指标为中心。

而是：

始终以业务系统（Business System）为中心。

以 Resource 为统一抽象。

以 Observation 为统一事实。

以 Evidence 为推理依据。

以 Knowledge 为持续积累。

构建一个能够理解、推理、优化并不断成长的企业数字孪生世界（Enterprise Digital Twin）。

整个产品遵循：

Business First

而不是：

Infrastructure First。

---

# Chapter 3 Definition（统一定义）

AI World 定义如下：

> AI World 是企业 IT 系统在 AI Performance Engineer 中的统一数字表达（Unified Digital Representation）。

世界中的一切对象均表示为 Resource。

世界中的一切运行事实均表示为 Observation。

世界中的一切推理均建立在 Evidence 之上。

世界中的一切经验最终沉淀为 Knowledge。

---

# Chapter 4 Characteristics（核心特征）

AI World 必须具备以下特征：

- Business Driven（业务驱动）
- Resource Unified（统一资源抽象）
- Fact Based（基于事实）
- Relationship Connected（关系连接）
- Time Continuous（连续时间）
- Explainable（可解释）
- Evolvable（可演进）

任何新增能力不得破坏上述特征。

---

# Chapter 5 Classification（世界组成）

AI World 由以下核心领域组成：

- Enterprise
- Business Domain
- Business System
- Resource
- Observation
- Relationship
- Topology
- Timeline
- Evidence
- Knowledge
- Optimization
- Verification

上述领域共同构成完整 AI World。

---

# Chapter 6 Lifecycle（世界生命周期）

企业系统运行过程统一抽象为：

Business Scenario

↓

Load Test / Production Traffic

↓

Resource Produce Observation

↓

Observation Pipeline

↓

Timeline

↓

Evidence

↓

Optimization

↓

Verification

↓

Knowledge Growth

世界持续循环演进。

---

# Chapter 7 Schema（统一结构）

AI World 的统一领域结构：

Enterprise

└── Business Domain

    └── Business System

        └── Resource

            └── Observation

                └── Timeline

                    └── Evidence

                        └── Knowledge

                            └── Optimization

---

# Chapter 8 Relationship（关系模型）

核心关系包括：

BELONGS_TO

RUNS_ON

DEPENDS_ON

CALLS

MEMBER_OF

CONNECTS_TO

PRODUCES

GENERATES

USES

所有关系均具有明确方向。

---

# Chapter 9 Ownership（归属原则）

遵循：

Law-000

Single Source of Truth

Law-001

Everything Is Resource

Law-002

Observation Belongs To Resource

各领域对象拥有唯一 Owner。

禁止多个模块共同维护同一事实。

---

# Chapter 10 Constraints（架构约束）

禁止：

直接以服务器作为业务对象。

禁止：

绕过 Resource 创建 Observation。

禁止：

修改已产生的 Observation。

禁止：

Evidence 不引用 Observation。

禁止：

Knowledge 未经过 Verification。

---

# Chapter 11 Versioning（版本策略）

World Model 独立维护版本。

新增领域对象允许。

删除已有领域对象禁止。

所有重大修改必须通过 RFC。

保持向后兼容。

---

# Chapter 12 Examples（标准示例）

正确：

Order Service（Business System）

↓

Redis Cluster（Resource）

↓

Memory Usage（Observation）

↓

Timeline

↓

Evidence

↓

Optimization

错误：

Redis CPU

↓

直接生成优化建议

（缺少 Observation、Evidence、Verification）

---

# Chapter 13 Reference Implementation（参考实现）

对应实现模块：

- Resource Domain
- Observation Domain
- Timeline Domain
- Evidence Domain
- Knowledge Domain
- Optimization Domain

各模块实现必须符合本规范。

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ 世界中的对象统一抽象为 Resource。

✔ 世界中的事实统一抽象为 Observation。

✔ Observation 必须属于 Resource。

✔ 所有推理必须经过 Evidence。

✔ 所有优化必须经过 Verification。

✔ 所有经验最终沉淀为 Knowledge。

✔ World Model 能支持未来领域扩展。

---

# Chapter 15 Freeze Statement（冻结声明）

本规范为 AI Performance Engineer 的最高世界模型定义。

任何新增领域对象、修改定义或调整关系，必须通过 RFC 流程审批。

未经审批，不允许直接修改本规范。

Status：Frozen