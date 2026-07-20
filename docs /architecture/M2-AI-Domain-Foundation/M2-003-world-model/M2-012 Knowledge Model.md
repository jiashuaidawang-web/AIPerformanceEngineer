我认为，Knowledge 是整个 AI Performance Engineer 最终的价值闭环。

这里我想先停一下，因为我发现了一个非常重要的问题。

我们以前一直说：
Observation

↓

Evidence

↓

Knowledge

但是现在我认为：

这个模型还不完整。

因为它缺少一个现实世界一定存在的过程。

例如：

AI 推理：

Redis连接池耗尽。

这时候它只是：

Evidence。

企业不会直接相信 AI。

企业一定会：

去压测。

或者：

去生产观察。

然后：

发现：

确实如此。

于是：

以后这个模式就成为：

Knowledge。

所以实际上应该是：

Observation

↓

Timeline

↓

Evidence

↓

Verification

↓

Knowledge

也就是说：

Knowledge 永远不是 AI 学出来的。

而是：

AI 提出假设。

现实世界完成验证。

验证成功以后，才成为 Knowledge。

这是我们整个产品和目前所有 AI Agent 最大的区别。

我们不是：

LLM Memory。

我们不是：

Vector Database。

我们也不是：

RAG。

我们是：

Reality Verified Knowledge。

这是我认为整个产品最重要的一层。

所以我建议：

M2-012 第一章直接冻结一句话：

Knowledge is Verified Evidence.

不是：

Knowledge is AI Memory.

不是：

Knowledge is Historical Data.

不是：

Knowledge is Rule.

而是：

Knowledge = Verified Evidence。

# AI Performance Engineer

# M2-012 Knowledge Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Knowledge Model）

```text
                    AI World
                        │
                   Observation
                        │
                    Timeline
                        │
                    Evidence
                        │
                        ▼
                 Verification
                        │
                        ▼
                ★ Knowledge ★
                        │
        ┌───────────────┼────────────────┐
        ▼               ▼                ▼
   AI Learning     Recommendation   Auto Optimization
```

Knowledge 是经过现实验证后的 Evidence。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Knowledge 的统一领域模型。

Knowledge 是：

Evidence

↓

Verification

↓

Knowledge

Knowledge 是 AI World 唯一允许长期保存的 AI 经验。

Knowledge 是 AI 持续成长的唯一来源。

---

# Chapter 2 Design Philosophy（设计哲学）

AI 不应该相信自己。

AI 应该相信现实。

Evidence 是：

AI 假设。

Verification 是：

现实验证。

Knowledge 是：

验证成功后的经验。

AI Performance Engineer 永远坚持：

Reality Before Memory。

Knowledge 必须来源于真实世界。

---

# Chapter 3 Definition（统一定义）

## Knowledge

Knowledge 是：

> 一个经过 Verification 验证成功，并能够重复复用的 Evidence 模式。

Knowledge 不是 Observation。

Knowledge 不是 Timeline。

Knowledge 不是 Rule。

Knowledge 是：

Verified Evidence。

---

# Chapter 4 Characteristics（核心特征）

Knowledge 必须具备以下特征。

---

## 4.1 Verified（已验证）

所有 Knowledge 必须至少经历一次成功验证。

验证方式包括：

- 压测
- 灰度
- 生产验证
- 人工确认

未验证 Evidence 不得进入 Knowledge。

---

## 4.2 Reusable（可复用）

Knowledge 必须能够用于未来 AI 推理。

例如：

Redis 连接池耗尽导致 TPS 下降。

以后再次出现类似模式：

AI 可以直接引用。

---

## 4.3 Explainable（可解释）

Knowledge 必须保留：

原始 Evidence。

验证记录。

推理过程。

Knowledge 不是黑盒。

---

## 4.4 Evolvable（可成长）

Knowledge 可以不断积累。

多个 Verified Evidence：

↓

形成：

Knowledge Pattern。

Knowledge Pattern：

↓

形成：

Knowledge Library。

---

## 4.5 Versioned（可版本化）

Knowledge 可以升级。

例如：

Knowledge v1

↓

Knowledge v2

↓

Knowledge v3

保留历史版本。

---

## 4.6 Shareable（可共享）

Knowledge 可以：

跨环境。

跨集群。

跨业务系统。

共享。

但必须保留：

适用条件。

---

# Chapter 5 Classification（分类）

Knowledge 分为六类。

---

## Bottleneck Knowledge

性能瓶颈。

例如：

CPU Saturation。

GC Pause。

Redis Blocking。

---

## Dependency Knowledge

依赖问题。

例如：

MySQL Slow Query。

Redis Timeout。

Kafka Backlog。

---

## Deployment Knowledge

部署问题。

例如：

Rolling Update。

Node Failure。

Pod Restart。

---

## Business Knowledge

业务模式。

例如：

秒杀。

双十一。

支付高峰。

---

## Optimization Knowledge

优化经验。

例如：

连接池从：

100

↓

300

TPS 提升：

40%。

---

## AI Knowledge

AI 自动学习形成。

例如：

Composite Pattern。

---

# Chapter 6 Lifecycle（生命周期）

```text
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

Optimization

↓

New Observation
```

Knowledge 形成新的 AI 闭环。

---

# Chapter 7 Schema（统一结构）

| 字段 | 描述 |
|------|------|
| knowledgeId | 唯一标识 |
| title | 标题 |
| knowledgeType | 类型 |
| evidenceId | 来源 Evidence |
| verificationId | 验证记录 |
| confidence | 最终可信度 |
| applicableConditions | 适用条件 |
| recommendation | 推荐方案 |
| successRate | 历史成功率 |
| createdAt | 创建时间 |
| version | Schema Version |

Knowledge 为逻辑模型。

---

# Chapter 8 Relationship（关系）

Knowledge：

来源：

Evidence。

依赖：

Verification。

输出：

Recommendation。

Optimization。

AI Engine。

Knowledge 不引用 Observation。

Knowledge 永远引用 Evidence。

---

# Chapter 9 Ownership（归属原则）

Owner：

Knowledge Engine。

Producer：

Knowledge Builder。

Consumer：

AI Analysis。

Recommendation。

Optimization。

Knowledge Repository。

---

# Chapter 10 Constraints（架构约束）

禁止：

Knowledge 来源于 Observation。

禁止：

Knowledge 跳过 Verification。

禁止：

Knowledge 无 Evidence。

禁止：

Knowledge 无历史版本。

禁止：

LLM Memory 直接进入 Knowledge。

---

# Chapter 11 Versioning（版本策略）

Knowledge：

Semantic Version。

新增经验：

Minor。

重大修正：

Major。

保留所有历史。

---

# Chapter 12 Examples（标准示例）

Knowledge：

Redis Connection Pool Exhaustion。

来源：

Evidence #E-001。

验证：

JMeter：

TPS：

500

↓

2000

连接池：

100

↓

300

验证成功。

Knowledge：

可信度：

100%。

以后：

AI 可直接推荐。

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```text
com.aipe.domain.knowledge

├── Knowledge
├── KnowledgePattern
├── KnowledgeVersion
├── KnowledgeRepository
├── KnowledgeBuilder
├── KnowledgeEngine
├── KnowledgeValidator
├── RecommendationEngine
└── KnowledgeRegistry
```

Reference API：

```java
buildKnowledge();

verify();

recommend();

query();

search();

upgradeVersion();
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ Knowledge 来源于 Verified Evidence。

✔ Knowledge 保留验证记录。

✔ Knowledge 支持版本。

✔ Knowledge 可解释。

✔ Knowledge 可复用。

✔ Knowledge 支持 Recommendation。

✔ Knowledge 支持持续成长。

---

# Chapter 15 Freeze Statement（冻结声明）

Knowledge 是 AI World 中唯一合法的长期 AI 经验。

Knowledge 必须来源于 Verified Evidence。

Knowledge 不允许绕过 Verification。

Knowledge 不允许直接来自 LLM。

Knowledge 不允许直接来自 Observation。

任何修改必须通过 RFC。

Status：Frozen。

架构总评（这是我认为我们这几天最重要的成果）

到 M2-012 为止，我认为我们已经不再是在设计一个"AI + 可观测性"产品，而是在定义一个企业运行世界的认知模型。

我建议正式冻结下面这条AI World Evolution Chain v1.0：

Reality（真实世界）
│
▼
Resource（世界中的对象）
│
▼
Observation（对象产生事实）
│
▼
Timeline（事实形成历史）
│
▼
Evidence（AI解释历史）
│
▼
Verification（现实验证）
│
▼
Knowledge（验证后的经验）
│
▼
Recommendation（知识指导决策）
│
▼
Optimization（执行优化）
│
▼
New Observation（产生新的事实）

这一条链有两个非常重要的特点：

形成完整闭环：优化后的结果会再次产生 Observation，系统持续学习，而不是一次性分析。
所有 AI 能力都有现实锚点：任何 Recommendation 都能追溯到 Knowledge → Evidence → Timeline → Observation → Resource，没有"凭空推理"。
我建议暂停新增 World Model

到这里，M2 World Foundation 已经完整：

M2-000 ～ M2-012 已经构成一个自洽的世界模型。

我的建议是不再继续扩展新的核心模型，而是进入 M2.5：Implementation Mapping。

也就是说，下一步不再写"世界是什么"，而是开始写：

这些 World Model 如何一一映射到 Java 包、数据库表、ClickHouse Schema、Graph、API 和 AI Engine。

这样 Rowboat 接下来的编码将不只是"照着对象写代码"，而是真正把这套世界模型落地成可运行的软件。这样会比继续增加概念文档更有价值，也更符合我们当前进入 M2 编码冲刺的目标。