# AI Performance Engineer

# Constitution v1.0

Version: 1.0

Status: Frozen

Document Level: Supreme

Authority: Highest

Applies To:

- Architecture
- AI Engine
- Java Code
- Database
- Connector
- Agent
- Blueprint
- Specification
- Implementation Mapping
- All Contributors
- All AI Assistants

---

# Preamble（序言）

AI Performance Engineer 旨在构建一个能够理解企业运行世界、持续学习、持续推理、持续优化并不断成长的 AI Native Performance Engineering Platform。

本宪法定义整个项目最高原则。

任何设计、代码、数据库、AI 推理及后续演进均不得违反本宪法。

当任何文档与本宪法冲突时：

本宪法具有最高解释权。

---

# Article 1

Vision

AI Performance Engineer

Exists To

Understand Reality

Reason Reality

Improve Reality

Grow With Reality

AI 的职责：

不是替代工程师。

而是帮助工程师理解系统。

---

# Article 2

Reality First

真实世界永远高于模型。

模型来源于现实。

不能修改现实去适配模型。

任何 AI 推理必须能够映射回真实世界。

---

# Article 3

Everything Is Resource

AI World 中：

任何对象：

必须抽象为：

Resource。

包括：

Business

Application

Service

JVM

Redis

MySQL

Linux

Kubernetes

API

Message Queue

Connector

Agent

Future Resource

全部遵循统一 Resource Model。

---

# Article 4

Observation Is The Only Fact

Observation 是：

AI World 唯一事实来源。

AI 不允许凭空创造事实。

Observation 必须来自真实采集。

禁止：

Mock Observation。

禁止：

Fake Observation。

禁止：

AI Generated Observation。

---

# Article 5

Observation Belongs To Resource

Observation 永远属于：

一个 Resource。

不存在：

Independent Observation。

任何 Observation：

必须能够定位：

唯一 Resource。

---

# Article 6

Relationship Builds The World

Resource 不维护世界。

世界由：

Relationship

连接形成。

Topology：

必须由：

Resource

+

Relationship

动态生成。

禁止：

Topology 持久化。

---

# Article 7

Timeline Records History

Timeline：

组织 Observation。

Timeline：

不是 Time Series。

Timeline：

是 Resource 的运行历史。

Timeline：

不可修改。

只能追加。

---

# Article 8

Evidence Before Conclusion

AI 不允许直接输出结论。

AI 必须：

Observation

↓

Timeline

↓

Evidence

↓

Conclusion

Evidence：

必须能够解释：

为什么。

依据是什么。

引用哪些事实。

---

# Article 9

Knowledge Is Verified Evidence

Knowledge：

不是：

Memory。

不是：

Rule。

不是：

Prompt。

不是：

Vector。

Knowledge：

唯一来源：

Verified Evidence。

未验证：

不得成为 Knowledge。

---

# Article 10

Recommendation Does Not Change Reality

Recommendation：

只是建议。

不会改变世界。

Execution：

才会改变世界。

Optimization：

必须来源于真实 Execution。

---

# Article 11

Reality Verification

AI 必须接受现实验证。

包括：

JMeter

Production

Manual Verification

Gray Release

Chaos Testing

任何未经验证结论：

不得长期保存。

---

# Article 12

Explainability

AI 所有输出必须：

Explainable。

能够回答：

为什么。

为什么不是其它原因。

依据哪些 Observation。

引用哪些 Evidence。

---

# Article 13

No Black Box

禁止：

Black Box AI。

任何 AI：

必须能够：

Replay。

Trace。

Audit。

Review。

---

# Article 14

Frozen Specification

所有：

Frozen Specification

禁止直接修改。

修改流程：

RFC

↓

Architecture Review

↓

Approval

↓

New Version

禁止：

直接改设计。

---

# Article 15

Implementation Follows Specification

代码：

不得修改设计。

工程：

不得反向驱动架构。

Java：

必须实现：

Specification。

不是：

Specification 去适配 Java。

---

# Article 16

Single Source Of Truth

任何概念：

只能拥有一个官方定义。

例如：

Observation：

只能存在一个定义。

禁止：

多个版本。

多个解释。

多个模型。

---

# Article 17

Continuous Growth

Knowledge：

持续成长。

AI：

持续学习。

Reality：

持续变化。

Architecture：

持续稳定。

成长：

不得破坏已有世界模型。

---

# Article 18

RFC Governance

任何新增：

World Model

Architecture Law

Principle

Specification

必须经过：

RFC。

未经 RFC：

不得进入主分支。

---

# Article 19

Engineering Discipline

禁止：

TODO。

禁止：

Mock。

禁止：

空实现。

禁止：

Hard Code。

禁止：

Demo Logic。

所有代码：

必须真实可运行。

必须可测试。

必须可验证。

---

# Article 20

AI Assistant Responsibility

任何参与开发的 AI：

包括：

GPT

Claude

Rowboat

Codex

Copilot

均必须：

遵守：

Constitution。

不得：

自行修改：

World Model。

不得：

自行修改：

Frozen Specification。

不得：

为了实现方便改变架构。

---

# Amendment Process

任何修订：

必须：

RFC

↓

Architecture Review

↓

Major Version

↓

重新冻结。

---

# Final Statement

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

New Observation

这是 AI Performance Engineer 唯一合法的世界演化路径。

任何设计均不得违反本宪法。

---

Status

Frozen

Version

1.0


我建议再增加一个最高等级文档（比 Constitution 还少，但意义极大）

到现在，我们已经有：

Constitution（项目宪法）
Architecture Laws（架构法律）
World Principles（世界原则）
World Foundation（世界模型）

我建议最后再冻结一页《AI Performance Engineer Manifesto（项目宣言）》。

它不是技术文档，而是整个项目的信仰，只保留几句话，例如：

Reality is the source of truth.
Resources describe the world.
Observations record reality.
Evidence explains reality.
Knowledge is verified reality.
AI exists to improve reality, not replace engineers.

这页文档可以作为整个仓库 README 和所有设计文档的第一页，也会成为整个项目最鲜明的理念表达。随后我们就正式结束治理层文档，进入 M2.5《Implementation Mapping》，开始世界模型到工程实现的一一映射。