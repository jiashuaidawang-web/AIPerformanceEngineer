我认为，Evidence 是整个 AI Performance Engineer 与所有 APM、可观测性平台最大的分水岭。

这是整个产品最核心的护城河。

原因是：

目前所有监控平台都停留在：

Observation（事实）

最多做到：

Alert（告警）

少部分做到：

Correlation（关联分析）

而我们要做到的是：

Evidence（证据）

注意，这是完全不同的概念。

例如：

传统平台：

CPU=95%

Redis Timeout

GC 300ms

MySQL Slow SQL

它们只是告诉用户：

发生了什么。

AI Performance Engineer 要告诉用户：

Evidence #001

Root Resource:
Order Service

Evidence:

10:01
Redis Latency 持续升高

↓

10:02
连接池耗尽

↓

10:03
Order Service 请求等待

↓

10:04
TPS下降

↓

Confidence

97%

Reason

Redis Connection Saturation

这是可解释证据链。

AI 真正推理的是 Evidence，而不是 Observation。

所以我建议：

Evidence = AI 的"事实解释层（Explanation Layer）"。





# AI Performance Engineer

# M2-011 Evidence Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Evidence Model）

```text
                    AI World
                        │
                  Resource
                        │
                  Observation
                        │
                    Timeline
                        │
                        ▼
                 ★ Evidence ★
                        │
          ┌─────────────┼─────────────┐
          ▼             ▼             ▼
    Root Cause     AI Explain     Optimization
          │             │             │
          ▼             ▼             ▼
      Knowledge    Verification   Recommendation
```

Evidence 是 AI World 中唯一合法的推理结果（Reasoning Result）。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Evidence 的统一领域模型。

Evidence 是 AI 对 Observation 和 Timeline 进行分析后的解释结果。

Evidence 是：

Observation

↓

Timeline

↓

Reasoning

↓

Evidence

Evidence 是 Root Cause Analysis、Optimization、Knowledge Learning 的唯一输入。

---

# Chapter 2 Design Philosophy（设计哲学）

Observation 是事实。

Timeline 是历史。

Evidence 是解释。

AI 不应该直接输出结论。

AI 必须先生成：

Evidence。

Evidence 必须能够回答：

为什么？

依据是什么？

证据来自哪里？

可信度是多少？

没有 Evidence 的 AI 属于不可解释 AI（Black Box）。

AI Performance Engineer 坚持：

Explain Before Decision。

---

# Chapter 3 Definition（统一定义）

## Evidence

Evidence 是：

> AI 基于一个或多个 Observation，在 Timeline 和 Topology 上进行推理后形成的可解释证据链。

Evidence 不是原始数据。

Evidence 不是规则。

Evidence 是解释。

Evidence 必须能够引用所有参与推理的 Observation。

Evidence 必须可追溯。

Evidence 必须可验证。

---

# Chapter 4 Characteristics（核心特征）

Evidence 必须具备以下特征。

---

## 4.1 Explainable（可解释）

Evidence 必须能够回答：

为什么得到这个结论。

AI 推理路径必须完整保存。

---

## 4.2 Traceable（可追溯）

Evidence 必须引用：

Observation

Timeline

Resource

Relationship

Topology

任何 Evidence 都不能脱离事实。

---

## 4.3 Confidence（可信度）

Evidence 必须拥有：

Confidence Score

范围：

0~100

例如：

97%

代表 AI 对证据链可信度的评估。

---

## 4.4 Verifiable（可验证）

Evidence 必须允许：

压测验证

实验验证

生产验证

未经验证：

不得成为 Knowledge。

---

## 4.5 Immutable（不可修改）

Evidence 一旦生成：

不得修改。

新的推理：

生成新的 Evidence。

---

## 4.6 Composable（可组合）

多个 Evidence 可以形成：

Composite Evidence。

例如：

Redis Evidence

+

GC Evidence

+

Network Evidence

↓

System Bottleneck Evidence

---

# Chapter 5 Classification（分类）

Evidence 分为六类。

---

## Performance Evidence

性能瓶颈。

例如：

CPU Saturation

Memory Leak

GC Pause

---

## Dependency Evidence

依赖问题。

例如：

Redis Timeout

MySQL Slow Query

Kafka Backlog

---

## Deployment Evidence

部署问题。

例如：

Pod Restart

Rolling Update

Node Failure

---

## Business Evidence

业务异常。

例如：

Order Failure

Payment Timeout

Inventory Delay

---

## AI Evidence

AI 自动推理。

例如：

Likely Root Cause

Likely Impact

Likely Bottleneck

---

## Composite Evidence

多个 Evidence 聚合形成。

例如：

Order Performance Incident

---

# Chapter 6 Lifecycle（生命周期）

```text
Observation

↓

Timeline

↓

Evidence Generation

↓

Evidence Validation

↓

Verified

↓

Knowledge
```

Evidence 永远来源于 Observation。

---

# Chapter 7 Schema（统一结构）

| 字段 | 描述 |
|------|------|
| evidenceId | Evidence 唯一标识 |
| evidenceType | 类型 |
| title | 标题 |
| description | AI解释 |
| rootResourceId | 根 Resource |
| observationIds | Observation 引用 |
| relationshipIds | Relationship 引用 |
| timelineId | Timeline 引用 |
| confidence | AI可信度 |
| reasoningSteps | 推理过程 |
| status | NEW / VERIFIED / REJECTED |
| createdAt | 创建时间 |
| version | Schema Version |

Evidence 为逻辑模型。

---

# Chapter 8 Relationship（关系）

Evidence：

输入：

Observation

Timeline

Topology

Relationship

输出：

Knowledge

Optimization

Verification

Evidence 是 AI Layer 第一层对象。

---

# Chapter 9 Ownership（归属原则）

Owner：

Evidence Engine。

Producer：

AI Analysis Engine。

Consumer：

Knowledge Engine。

Optimization Engine。

Report Engine。

Connector 不允许创建 Evidence。

---

# Chapter 10 Constraints（架构约束）

禁止：

Evidence 不引用 Observation。

禁止：

Evidence 无 Confidence。

禁止：

Evidence 无推理过程。

禁止：

Evidence 绕过 Timeline。

禁止：

Evidence 直接成为 Knowledge。

---

# Chapter 11 Versioning（版本策略）

Evidence Schema：

独立维护。

支持新增：

Reasoning Strategy

Confidence Algorithm

Explain Template

---

# Chapter 12 Examples（标准示例）

Evidence：

```text
Title

Redis Connection Saturation

Reason

Connection Pool Exhausted

Observations

Redis Clients

Latency

Timeout

Timeline

09:00~09:08

Confidence

98%
```

AI Explain：

```text
Redis 已持续 8 分钟连接耗尽，
导致 Inventory Service 请求等待，
最终 Order TPS 降低。
```

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```text
com.aipe.domain.evidence

├── Evidence
├── EvidenceType
├── EvidenceStatus
├── EvidenceRepository
├── EvidenceBuilder
├── EvidenceEngine
├── EvidenceReasoner
├── ConfidenceCalculator
├── EvidenceValidator
└── EvidenceRenderer
```

Reference API：

```java
generate();

explain();

calculateConfidence();

query();

verify();

merge();
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ Evidence 引用 Observation。

✔ Evidence 引用 Timeline。

✔ Evidence 支持 AI Explain。

✔ Evidence 支持 Confidence。

✔ Evidence 支持 Verification。

✔ Evidence 支持组合。

✔ Evidence 可进入 Knowledge。

---

# Chapter 15 Freeze Statement（冻结声明）

Evidence 是 AI World 中唯一合法的推理结果。

任何 AI 输出必须首先生成 Evidence。

任何 Recommendation 必须引用 Evidence。

任何 Knowledge 必须来源于 Verified Evidence。

任何修改必须通过 RFC。

Status：Frozen

我建议在这里冻结一个新的 AI Principle（不是 Architecture Law）

到这里，我们已经把 Observation → Timeline → Evidence 建好了。

我建议增加一条 AI Principle，它以后会成为 AI Engine 的最高准则：

AI Principle-001：Evidence Before Conclusion
Observation
│
▼
Timeline
│
▼
Evidence
│
▼
Conclusion
│
▼
Recommendation
│
▼
Verification
│
▼
Knowledge

也就是说：

AI 永远不能直接给结论。
AI 必须先构建 Evidence。
Recommendation 必须引用 Evidence。
Knowledge 必须来源于 Verified Evidence。

这条原则会直接约束未来的 WP015 AI Analysis Engine、WP016 Root Cause Engine 和 WP017 Recommendation Engine 的实现，让整个系统始终保持可解释 AI（Explainable AI），而不是一个只能给答案、无法说明依据的黑盒。我建议将它纳入《AI World Principles》并冻结。

