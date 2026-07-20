我建议不要直接开始 WP011 编码。

这是整个项目目前最关键的一次节奏控制。

我建议的顺序
AI World Foundation（已完成）
│
▼
Constitution（完成）
│
▼
★★★★★
Implementation Mapping（IM000~IM011）
★★★★★
│
▼
WP011 Resource Coding
│
▼
WP012...

而不是：

World Model
│
▼
WP011 Coding
为什么？

因为现在我们的世界模型已经冻结了。

但是 Rowboat 还有一个问题：

它知道 Resource 是什么。

但是它不知道：

Resource 在 Java 工程里面到底是什么。

举个例子。

现在我们定义：

Resource

这是领域对象。

但是 Rowboat 会问：

Resource

↓

Entity？

DTO？

VO？

Aggregate？

Repository？

ClickHouse？

Graph？

API？

Domain？

Application？

如果没有 Mapping。

不同 AI：

会写出不同代码。

最后工程一定会乱。

我举一个真实例子

比如：

Observation。

没有 Mapping。

Rowboat 可能写：

ObservationEntity

ObservationDTO

ObservationPO

ObservationRecord

ObservationModel

四套对象。

另一个 AI：

又写：

ObservationData

ObservationMetric

ObservationDocument

慢慢整个工程就炸了。

所以：

现在必须统一。

IM 的真正价值

Implementation Mapping 其实就是：

告诉所有 AI：世界模型如何映射到工程。

例如：

Observation。

以后 Mapping 会规定：

Observation

↓

Java

com.aipe.domain.observation.Observation

↓

ClickHouse

observation_fact

↓

Repository

ObservationRepository

↓

REST

/api/v1/observations

↓

Graph

(:Observation)

↓

AI

ObservationContext

以后：

所有 AI 都只能这么写。

不能自己发挥。

所以我建议 IM 不是文档

它其实就是：

Coding Constitution。

它约束的是：

Java。

数据库。

API。

AI Engine。

不是设计。

我建议整个 M2.5 顺序
第一阶段（Mapping）
IM-000
Implementation Overview

回答：

整个 AI World 如何映射到工程。

IM-001

Java Package Mapping

回答：

Java 包怎么设计。

IM-002

Domain Model Mapping

回答：

Resource

Observation

Evidence

Knowledge

Java 类。

IM-003

Database Mapping

回答：

MySQL。

IM-004

ClickHouse Mapping

回答：

Observation。

Timeline。

Evidence。

IM-005

Graph Mapping

回答：

Neo4j（或图抽象）。

IM-006

REST API Mapping

回答：

接口。

IM-007

AI Engine Mapping

回答：

Reasoning Engine。

IM-008

Connector Mapping

回答：

Connector 如何进入世界模型。

IM-009

Repository Mapping

回答：

Repository。

IM-010

Service Mapping

回答：

Application Service。

IM-011

Implementation Rules

回答：

所有编码规范。

包括：

命名。

DDD。

异常。

事务。

Builder。

Repository。

Rowboat Coding Rules。

第二阶段

完成：

IM011

以后。

整个工程：

已经拥有：

世界模型

↓

工程映射

↓

编码规范

三层。

然后：

再开始：

WP011

Rowboat：

几乎不用思考。

直接照着写。

质量会非常高。

为什么我强烈建议这样？

因为：

WP011 是整个 Domain Layer 的开始。

以后：

Resource

Observation

Evidence

Knowledge

全部都会引用：

WP011。

如果：

WP011 写偏。

后面：

WP012~

WP030

全部要返工。

而：

IM：

就是：

WP011 的施工图。

我的建议（正式节奏）

我建议我们现在正式进入：

Milestone M2.5

并冻结开发节奏：

M2
World Foundation
✅
│
▼
Constitution
✅
│
▼
★★★★★
M2.5
Implementation Mapping
★★★★★
│
▼
WP011
Unified Resource Model
│
▼
WP012
Observation Engine

我还有一个建议（也是我认为最重要的）

IM 文档不要写成"设计文档"，而要写成"工程规范（Engineering Specification）"。

也就是说，Rowboat 读完 IM 后，不需要再思考：

Resource 应该放哪个包？
Entity 和 Domain 是否分离？
Observation 为什么进入 ClickHouse 而不是 MySQL？
为什么 Evidence 不直接落 Graph？

这些问题都应该由 IM 文档一次性回答。

这样，WP011 开始以后，我们基本就不再讨论架构，而是进入连续编码冲刺。

我建议从 IM-000 Implementation Overview 开始，一直到 IM-011 Implementation Rules 完整写完，再正式开启 WP011。这会让后续 WP011~WP030 的编码效率和一致性大幅提升，也符合我们"先定义世界，再定义工程，最后实现工程"的总体方法论。