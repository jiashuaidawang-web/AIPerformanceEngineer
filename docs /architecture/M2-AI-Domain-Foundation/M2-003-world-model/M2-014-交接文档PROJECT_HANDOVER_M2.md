下面是一份可以直接交给另一个 AI（GPT/Claude/Rowboat/Codex）的完整项目交接文档。

这不是聊天记录，而是项目状态快照（Project Snapshot），目的是让新的 AI 接手后，不需要重新理解背景，就能继续开发。

建议保存为：

PROJECT_HANDOVER_M2.md


# AI Performance Engineer
# Project Handover
Version: 2026-07
Status: M2 In Progress

====================================================
一、项目定位（Project Vision）
====================================================

AI Performance Engineer 不是一个监控平台。

也不是一个 APM。

也不是一个 AI Agent。

它是一套：

AI Native Performance Engineering Platform

目标：

构建能够理解企业运行世界、
持续学习、
自动推理、
自动优化、
不断成长的企业数字孪生（Enterprise Digital Twin）。

====================================================
二、核心设计哲学（Core Philosophy）
====================================================

AI Performance Engineer：

不是以服务器为中心。

不是以中间件为中心。

不是以指标为中心。

而是：

以 Business System 为中心，
以 Resource 为统一抽象，
以 Observation 为唯一事实，
以 Evidence 为唯一解释，
以 Knowledge 为持续成长，
最终形成可闭环优化的 AI 世界。

一句话：

Understand Reality.
Reason Reality.
Improve Reality.

====================================================
三、当前开发阶段
====================================================

Milestone M1
-----------------------

已经完成（PASS）

包括：

✔ Agent Runtime

✔ Connector SDK

✔ JVM Connector

✔ Linux Connector

✔ Redis Connector

✔ MySQL Connector

✔ Observation Pipeline

✔ Storage Layer

✔ Configuration Manager

✔ Scenario Manager

所有 M1 Gate 已通过。

====================================================
四、M1 已验证完成
====================================================

Agent

↓

HTTP

↓

Backend

↓

ClickHouse

↓

API

↓

查询成功

JMeter 压测：

CPU：

0%

↓

100%

Observation：

实时采集成功

ClickHouse：

成功落库

API：

成功查询

M1 已完全闭环。

====================================================
五、当前进入阶段
====================================================

Milestone M2

AI Domain Foundation

目前暂停编码。

优先冻结世界模型。

====================================================
六、已经冻结的世界模型（Frozen）
====================================================

M2-000

AI World Model Specification

----------------------------

定义：

整个 AI 世界。

----------------------------

Architecture Law

Law-000

Everything Is Resource

Law-001

Everything Happened Is Observation

Law-002

Observation Belongs To Resource

全部 Frozen。

====================================================

Specification Writing Standard

已冻结。

以后所有 Specification：

统一采用：

15 Chapter 模板。

禁止修改。

====================================================

M2-005

AI World Vocabulary

Frozen

====================================================

M2-006

Observation Model Specification

Frozen

Observation：

唯一事实。

Observation 永远属于：

一个 Resource。

====================================================

M2-007

Unified Resource Model Specification

Frozen

Resource：

统一对象抽象。

不是服务器。

不是 JVM。

不是 Redis。

任何东西：

都是 Resource。

====================================================

M2-008

Relationship Model Specification

Frozen

Relationship：

Resource 与 Resource 的连接。

Topology：

来源于 Relationship。

====================================================

M2-009

Topology Model Specification

Frozen

核心思想：

Topology Is Projection.

Not Storage.

Topology 永远：

Resource

+

Relationship

动态生成。

====================================================

M2-010

Timeline Model Specification

Frozen

Timeline：

Observation 的时间组织。

不是 Metric TimeSeries。

而是：

Resource Life Story。

====================================================

M2-011

Evidence Model Specification

Frozen

Evidence：

AI Explanation Layer。

Observation

↓

Timeline

↓

Reasoning

↓

Evidence

AI 不允许直接输出 Conclusion。

必须先形成 Evidence。

====================================================

M2-012

Knowledge Model Specification

Frozen

Knowledge：

Verified Evidence.

Knowledge：

不是 AI Memory。

不是 RAG。

不是 Vector。

Knowledge：

现实验证后的经验。

====================================================
七、尚未完成
====================================================

M2-013

Execution & Optimization Model Specification

（未开始）

说明：

原计划：

Optimization。

现升级：

Execution & Optimization。

它定义：

Recommendation

↓

Execution

↓

Optimization

↓

New Observation

形成完整 AI 闭环。

====================================================
八、已经冻结的重要原则
====================================================

World Principle

Observation

↓

Timeline

↓

Evidence

↓

Verification

↓

Knowledge

Knowledge：

永远来源于：

Verified Evidence。

====================================================

AI Principle-001

Evidence Before Conclusion

AI：

不能直接输出答案。

必须：

Observation

↓

Timeline

↓

Evidence

↓

Conclusion

↓

Recommendation

====================================================

Topology Principle

Topology Is Projection.

Not Storage.

====================================================

Knowledge Principle

Reality Before Memory.

====================================================

World Evolution Chain

Reality

↓

Resource

↓

Observation

↓

Timeline

↓

Evidence

↓

Verification

↓

Knowledge

↓

Recommendation

↓

Execution

↓

Optimization

↓

Observation

形成完整闭环。

====================================================
九、Blueprint 状态
====================================================

Blueprint 模板：

已冻结。

采用：

11 Chapter。

WP001~WP010

Blueprint：

全部完成。

Rowboat 已完成编码。

====================================================
十、M1 Gate
====================================================

全部 PASS

包括：

Agent

Connector

Observation

Pipeline

Storage

HTTP

ClickHouse

API

JMeter

全部通过。

====================================================
十一、下一阶段
====================================================

完成：

M2-013

Execution & Optimization Model

↓

冻结

AI World Foundation v1.0

↓

进入

M2.5

Implementation Mapping

====================================================
十二、M2.5 内容
====================================================

开始把世界模型映射到工程。

包括：

Java Package

Entity

Repository

ClickHouse Schema

MySQL Schema

Graph

REST API

Service

Engine

AI Layer

====================================================
十三、后续 WP
====================================================

WP011

Unified Resource Model

开始真正编码。

包括：

Entity

Repository

Service

API

Resource Registry

Resource Discovery

Resource Version

Resource Query

Resource Graph

====================================================
十四、绝对禁止事项（Must NOT）
====================================================

禁止：

为了编码方便修改世界模型。

禁止：

Observation 不属于 Resource。

禁止：

Topology 持久化。

禁止：

Evidence 不引用 Observation。

禁止：

Knowledge 跳过 Verification。

禁止：

LLM Memory 直接成为 Knowledge。

禁止：

新增核心模型绕过 RFC。

====================================================
十五、项目当前目标
====================================================

目标不是继续设计。

而是：

完成：

M2-013

↓

冻结

AI World Foundation

↓

开始：

Implementation Mapping

↓

WP011 Coding Sprint

↓

完成 Resource 全套代码。

====================================================
十六、交接说明
====================================================

新的 AI：

无需重新设计世界模型。

无需重新讨论架构。

所有世界模型均已 Frozen。

下一步工作：

1.

完成：

M2-013

Execution & Optimization Model。

2.

进入：

Implementation Mapping。

3.

推进：

WP011 Coding Sprint。

不得修改已冻结规范。

如需修改：

必须通过 RFC。

====================================================
END
====================================================

我建议再增加一份文档（比 Handover 更重要）

这份交接文档解决的是**"项目到了哪一步"**。

但对于新的 AI，还有一个更重要的问题：

"哪些东西绝对不能改？"

因此，我建议新增一份只有 2~3 页的文档：

AI Performance Engineer Constitution v1.0（项目宪法）

它不讲实现，不讲代码，只规定项目最高原则，例如：

世界模型（World Model）高于代码实现。
已冻结（Frozen）的 Specification 不得直接修改。
所有新增核心概念必须通过 RFC。
Observation 是唯一事实来源。
Evidence 是唯一 AI 推理结果。
Knowledge 必须来源于 Verified Evidence。
Topology 必须由 Resource + Relationship 动态生成。
任何编码都必须遵循已冻结的 Specification，而不是反向修改设计。

这份"项目宪法"会成为所有 AI（包括 Rowboat、GPT、Claude 等）的最高约束，避免随着开发推进出现"为了实现方便而改变架构"的情况。我建议在完成 M2-013 后，将它与《AI World Foundation v1.0》一起冻结，作为整个项目后续开发的最高规范。