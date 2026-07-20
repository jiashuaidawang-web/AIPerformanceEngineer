# AI Performance Engineer

# M2-005 AI World Vocabulary Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

=========================================================
AI Performance Engineer
AI World Overview
Version: v1.0
Status: Frozen
=========================================================

                         Enterprise
                              │
            ┌─────────────────┴─────────────────┐
            │                                   │
     Business Domain                    Shared Platform
            │
     Business System
            │
         Resource
            │
     ┌──────┼──────────────┐
     │      │              │
Observation  Relationship  Topology
│             │             │
└─────────────┼─────────────┘
│
Timeline
│
Evidence
│
Knowledge
│
Optimization
│
Verification
│
Knowledge Growth

---------------------------------------------------------
Everything is Resource.
Everything Running is Observation.
Everything AI Explains is Evidence.
Everything Verified becomes Knowledge.
---------------------------------------------------------


=========================================================
Document Position
=========================================================

                     AI World
                         │
                         │
      ┌──────────────────┼────────────────────┐
      │                  │                    │
Architecture Laws   Vocabulary         Domain Models
▲
│
★ Current Document ★
│
┌──────────────────┼──────────────────┐
│                  │                  │
Observation          Resource         Relationship
│                  │                  │
└──────────────┬───┴──────────────────┘
│
Timeline
│
Evidence
│
Knowledge
│
Optimization

Purpose

Vocabulary defines the only legal language
used inside AI Performance Engineer.

All Specifications MUST reference
definitions from this document.

No Specification may redefine terminology.

Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 的统一领域语言（AI World Vocabulary）。

所有 Specification、RFC、Blueprint、Java Code、API、数据库模型以及 AI 推理必须使用本规范中的术语。

本规范是整个项目唯一合法的领域语言来源（Single Source of Terminology）。

任何文档不得重新定义已有术语。

Chapter 2 Design Philosophy（设计哲学）

语言决定概念。

概念决定模型。

模型决定实现。

如果领域语言不统一：

Connector 会有自己的术语；
Dashboard 会有自己的术语；
AI 会有自己的术语；
开发人员会有自己的理解。

最终导致整个系统失去统一的世界观。

因此：

AI Performance Engineer 建立统一领域语言。

所有模块只能使用本规范定义的 Vocabulary。

Chapter 3 Definition（Vocabulary 定义）

本规范定义的 Vocabulary 为整个 AI World 唯一合法术语。

Enterprise

企业。

AI World 中最大的管理边界。

一个 Enterprise 可以包含多个 Business Domain。

Business Domain

业务领域。

代表企业的一类核心业务能力。

例如：

Order
Payment
Inventory
Logistics

Business Domain 是 Business System 的容器。

Business System

业务系统。

企业真正运行的软件系统。

例如：

订单系统。

支付系统。

库存系统。

AI Performance Engineer 永远以 Business System 为分析中心。

Resource

资源。

AI World 中所有对象的统一抽象。

任何具有生命周期、身份和状态的对象都必须抽象为 Resource。

例如：

JVM
Redis
MySQL
Pod
Host
Kafka
Order Service

都是 Resource。

Observation

观察事实。

Resource 在某一时刻产生的一条不可变运行事实（Immutable Runtime Fact）。

Observation 永远属于一个 Resource。

Observation 是 AI 唯一认可的运行时事实。

Relationship

关系。

定义 Resource 与 Resource 之间的连接方式。

例如：

BELONGS_TO
RUNS_ON
DEPENDS_ON
MEMBER_OF
CALLS

Relationship 构成 AI World 的连接网络。

Topology

拓扑。

由 Resource 与 Relationship 共同组成的数字世界结构。

Topology 描述系统"如何连接"，而不是"如何运行"。

Timeline

时间轴。

按照时间顺序组织 Observation 的连续序列。

Timeline 是 Evidence 推理的基础。

Evidence

证据。

多个 Observation 经过关联分析形成的可解释事实链。

Evidence 必须引用 Observation。

Evidence 不允许凭空生成。

Knowledge

知识。

经过 Verification 验证成功的 Evidence 模式。

Knowledge 可持续成长。

Knowledge 是 AI 的长期经验。

Optimization

优化方案。

AI 基于 Evidence 和 Knowledge 生成的优化建议。

Optimization 必须能够验证。

Verification

验证。

通过压测或真实运行验证 Optimization 是否有效。

未经 Verification 的 Optimization 不得进入 Knowledge。

Scenario

场景。

一次完整业务行为的描述。

例如：

双十一下单。

秒杀。

支付。

压测。

Scenario 是整个 AI 分析的入口。

Connector

连接器。

负责采集 Observation。

Connector 不拥有 Observation。

Connector 不拥有 Resource。

Connector 仅负责采集。

Agent

Agent Runtime。

运行 Connector。

负责采集、缓存、发送 Observation。

Agent 不负责 AI 推理。

World Model

世界模型。

AI 对企业 IT 系统的统一数字表达。

World Model 是所有领域模型的根。

Chapter 4 Characteristics（核心特征）

Vocabulary 必须满足：

唯一性（Unique）
不歧义（Unambiguous）
可扩展（Extensible）
与实现无关（Implementation Independent）
长期稳定（Stable）
Chapter 5 Classification（分类）

Vocabulary 分为五类：

Business Vocabulary
Enterprise
Business Domain
Business System
Scenario
Resource Vocabulary
Resource
Relationship
Topology
Runtime Vocabulary
Observation
Timeline
AI Vocabulary
Evidence
Knowledge
Optimization
Verification
Runtime Component Vocabulary
Agent
Connector
Chapter 6 Lifecycle（生命周期）

Vocabulary 生命周期：

Define
↓
Freeze
↓
Reference
↓
RFC
↓
Version Upgrade

Vocabulary 一旦冻结，任何修改必须通过 RFC。

Chapter 7 Schema（统一结构）

每一个 Vocabulary 必须包含：

Name
Definition
Owner
Parent
Related Terms
Version

统一 Schema 适用于所有 Vocabulary。

Chapter 8 Relationship（关系）

Vocabulary 之间存在明确引用关系。

例如：

Business System

↓

Resource

↓

Observation

↓

Timeline

↓

Evidence

↓

Knowledge

↓

Optimization

↓

Verification

Chapter 9 Ownership（归属）

Vocabulary 唯一 Owner：

AI World Model。

所有 Specification 必须引用 Vocabulary。

禁止复制定义。

禁止重新命名。

Chapter 10 Constraints（架构约束）

禁止：

一个术语拥有多个定义。

禁止：

不同文档定义冲突。

禁止：

代码创造新的领域术语。

新增 Vocabulary 必须经过 RFC。

Chapter 11 Versioning（版本策略）

Vocabulary 独立维护版本。

允许：

新增术语。

禁止：

修改已有定义。

如必须修改，升级 Major Version。

Chapter 12 Examples（标准示例）

正确：

Order System
↓
Resource
↓
Observation
↓
Evidence

错误：

Redis Metric
↓
直接生成 Knowledge

原因：

缺少 Observation、Evidence、Verification。

Chapter 13 Reference Implementation（参考实现）

对应实现：

Domain Enum
Vocabulary Registry
Documentation Generator
Validation Rule
AI Prompt Dictionary

Java 实现只能引用 Vocabulary，不得重新定义。

Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ 所有术语唯一。
✔ 所有 Specification 引用 Vocabulary。
✔ 所有 Blueprint 使用 Vocabulary。
✔ 所有代码命名符合 Vocabulary。
✔ 不存在重复定义。
✔ 不存在歧义定义。
Chapter 15 Freeze Statement（冻结声明）

本规范定义 AI Performance Engineer 唯一合法领域语言。

本规范冻结后：

禁止直接修改术语定义；
禁止在其他文档重复定义术语；
所有新增术语必须通过 RFC 审核；
所有实现必须引用本规范。

Status：Frozen

我最后再提一个建议（我认为这是整个 M2 的最后一块拼图）

到这里，我认为 M2 的基础已经完整了：

✅ M2-000 AI World Model（世界是什么）
✅ Architecture Laws（世界遵循什么法则）
✅ Specification Writing Standard（规范怎么写）
✅ M2-005 AI World Vocabulary（世界使用什么语言）

现在终于可以开始 M2-006《Observation Model Specification》了。

因为从这一刻开始，我们已经回答了四个根本问题：

世界是什么（World Model）
世界如何运行（Architecture Laws）
世界如何描述（Vocabulary）
规范如何表达（Writing Standard）

接下来要回答的才是：

世界里真正发生了什么？

这就是 Observation Model，也是整个 AI 推理链（Observation → Timeline → Evidence → Knowledge）的真正起点。