以后统一采用 15 章节模板：

Purpose（为什么存在）
Philosophy（设计哲学）
Definition（统一定义）
Characteristics（核心特征）
Classification（分类）
Lifecycle（生命周期）
Schema（统一结构）
Relationship（与其他模型关系）
Ownership（归属原则）
Constraints（架构约束）
Versioning（版本策略）
Examples（标准示例）
Reference Implementation（参考实现）
Acceptance Criteria（验收标准）
Freeze Statement（冻结声明）

这样以后无论是 Resource、Topology、Timeline、Evidence、Knowledge，全部保持一致的写作风格。

我建议把这套模板也冻结，命名为：

Specification Writing Standard v1.0（规范文档编写标准）

以后整个项目所有 Specification 都遵循这一模板，不再各写各的。这样不仅 Rowboat 更容易理解，我们自己未来维护几百页设计文档时也会非常统一。




1. Purpose（目的）

统一 AI Performance Engineer 所有 Specification 文档的结构、术语和编写规范，保证：

所有领域模型具有一致性
所有 RFC、Blueprint、代码实现具有统一依据
所有 AI（Rowboat、Claude Code、Codex、Cursor 等）能够按照同一套规则理解项目

本标准适用于所有 World Model、Domain Model、Protocol、Storage、AI、Deployment 等规范文档。

统一章节模板（15 Chapters）

以后所有 Specification 必须严格按照以下顺序编写，不允许随意增删章节。

Chapter 1：Purpose（设计目标）

回答：

为什么需要这份规范？

说明该模型在整个系统中的职责、目标以及解决的问题。

Chapter 2：Design Philosophy（设计哲学）

回答：

我们为什么这样设计？

说明设计原则、核心思想以及与传统方案的区别。

这一章节禁止出现代码。

Chapter 3：Definition（统一定义）

回答：

这个领域对象到底是什么？

必须给出唯一、明确、不可歧义的定义。

例如：

Observation 是 Resource 在某一时刻产生的一条不可变事实（Immutable Runtime Fact）。

Chapter 4：Characteristics（核心特征）

回答：

它必须具备哪些天然属性？

例如：

Immutable
Traceable
Versioned
Resource Scoped

这里描述的是领域特性，而不是实现方式。

Chapter 5：Classification（分类）

回答：

它有哪些类型？

必须采用树状结构或枚举说明。

例如：

Observation：

Metric
Log
Trace
Event
Snapshot
Chapter 6：Lifecycle（生命周期）

回答：

它从哪里来，又到哪里去？

建议采用状态图或流程图描述。

例如：

Produce
↓
Collect
↓
Store
↓
Analyze
↓
Archive
Chapter 7：Schema（统一结构）

回答：

它的数据结构必须是什么？

必须定义：

字段
类型
是否必填
含义

禁止直接绑定数据库实现。

Chapter 8：Relationship（关系模型）

回答：

它与其他领域对象之间是什么关系？

例如：

Observation：

Belongs To Resource
Enters Timeline
Supports Evidence

建议配套关系图。

Chapter 9：Ownership（归属原则）

回答：

谁拥有它？

必须明确：

Owner
Creator
Updater
Consumer

如果遵循 Architecture Law，应明确引用。

Chapter 10：Constraints（架构约束）

回答：

哪些事情绝对不能做？

例如：

禁止 Update
禁止多个 Owner
禁止绕过 Domain

这一章节属于架构红线。

Chapter 11：Versioning（版本策略）

回答：

如何演进而不破坏兼容性？

包括：

Schema Version
API Version
Payload Version

必须保证向后兼容策略。

Chapter 12：Examples（标准示例）

回答：

一个标准实例长什么样？

至少包含：

正确示例
错误示例（建议）
典型场景
Chapter 13：Reference Implementation（参考实现）

回答：

在代码层应该如何体现？

列出：

Package
Class
Interface
Enum
Repository
Service

这里只描述参考实现，不写业务逻辑。

Chapter 14：Acceptance Criteria（验收标准）

回答：

如何证明规范已经正确实现？

必须采用可验证条目。

例如：

✔ Resource 必须唯一
✔ Observation 必须不可变
✔ Connector 不得直接创建 Resource

所有条目都应该可以通过测试或代码检查验证。

Chapter 15：Freeze Statement（冻结声明）

说明：

当前版本状态
修改流程
是否允许直接修改

统一格式：

本规范自冻结后，不允许直接修改。任何新增、删除或变更必须通过 RFC（Request for Comments）流程，经评审通过后方可更新版本。

编写原则（Writing Principles）

所有 Specification 必须遵循以下原则：

Principle 1：Concept First

先定义概念，再定义实现。

禁止从代码反推领域模型。

Principle 2：Domain Before Technology

优先描述领域，而不是技术。

例如：

正确：

Observation 属于 Resource。

错误：

Observation 存储在 ClickHouse。

Principle 3：Implementation Independent

规范不得绑定具体语言、框架或数据库。

Java、Go、Rust 等实现均应遵循同一规范。

Principle 4：Single Definition

一个术语只能有一个定义。

不得在不同文档中出现冲突解释。

Principle 5：Traceable

规范中的每一项定义都应能够追溯到：

World Model
Architecture Law
RFC

形成完整的设计链路。

文档层级

以后整个项目文档体系固定如下：

Vision
↓
World Model
↓
Architecture Laws
↓
Specification（遵循本标准）
↓
RFC
↓
Blueprint
↓
Reference Implementation
↓
Code
↓
Gate
Freeze Policy

本标准属于整个 AI Performance Engineer 的基础标准。

优先级高于：

Blueprint
RFC
代码实现

所有 Specification 必须遵循本标准。

如需修改，必须通过 RFC，并升级版本号。

Status：Frozen

我建议再增加一条，并把它也冻结

经过这两天的梳理，我建议把整个项目的四个基础规范固定下来，后面不再轻易变动：

Architecture Laws —— 定义世界运行的基本法则（Law-000 ~ Law-002）。
Specification Writing Standard —— 定义所有规范文档的统一写法（15 章节模板）。
Blueprint Standard —— 定义所有 Work Package 的统一设计模板（已经冻结的 11 章节 Blueprint）。
Gate Standard —— 定义每个 Milestone 的质量门禁和验收方式（M1 Gate、M2 Gate……）。

这样以后无论是你、我还是 Rowboat，所有工作都会围绕这四个基础标准展开。它们相当于整个 AI Performance Engineer 项目的"ISO 标准"，后续新增内容只是在这些标准之上扩展，而不是反复调整基础规则。