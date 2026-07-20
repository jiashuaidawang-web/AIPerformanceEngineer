# AI Performance Engineer

# M2 Gate

Version: v1.0

Status: Draft

Milestone: M2 – AI Domain Foundation

Gate Template: Gate Standard v1.0

---

# Milestone Overview

M2 AI Domain Foundation

目标：

建立完整的世界模型与工程映射基础。

包含：

Constitution v1.0

Architecture Law-000~002

World Model（M2-000~M2-013）

Implementation Mapping（IM-000~IM-011）

Engineering Standards（Laws / Specification / Blueprint / Gate）

---

# Entry Criteria

进入 M2 Gate 评审前必须满足：

□ Constitution v1.0 Frozen

□ Architecture Law-000~002 Frozen

□ World Model M2-000~M2-013 Frozen

□ IM-000~IM-011 Frozen

□ Specification Writing Standard Frozen

□ Blueprint Standard Frozen

□ Gate Standard Frozen

□ 所有文档已提交主分支

---

# Exit Criteria

## Functional Acceptance

□ AI World 完整定义（Resource / Observation / Evidence / Knowledge / Execution）

□ Observation 有统一 Fact 模型

□ Relationship 有独立领域模型

□ Timeline 有明确构建方式

□ AI Runtime 有完整推理流水线

□ Connector Runtime 有完整生命周期

## Technical Acceptance

□ 分层架构完整（API / Application / Domain / Infrastructure / Storage / Connector）

□ 依赖方向统一

□ Primary Storage 映射完整（MySQL / ClickHouse / Graph / Redis）

□ Repository 接口在 Domain、实现在 Infrastructure

□ Application Service 为唯一事务 Owner

## Integration Acceptance

□ Connector → Observation → ClickHouse 链路定义完整

□ Observation → Timeline → Evidence → Knowledge 链路定义完整

□ Repository → Domain → Application → API 链路定义完整

□ AI Runtime → Repository → Domain 链路定义完整

## Documentation Acceptance

□ Constitution Frozen

□ Architecture Laws Frozen

□ World Model Frozen

□ IM-000~IM-011 Frozen

□ 四大基础标准 Frozen

---

# Evaluation Process

Step 1 文档自检

所有文档作者自检 Frozen 状态。

Step 2 架构合规检查

验证所有 IM 与 Constitution / Law 对齐。

验证依赖方向正确。

验证命名统一。

Step 3 端到端链路审查

验证六大链路定义完整：

Connector → Observation

Observation → Timeline

Timeline → Evidence

Evidence → Knowledge

Knowledge → Recommendation

Recommendation → Execution

Step 4 Gate Review

架构评审会议。

Step 5 Verdict

最终判定。

---

# Verdict

当前状态：

Draft

待 M2 全部完成后正式评审。

判定选项：

Pass：M2 完成，允许进入 WP011 编码。

Fail：存在未完成项，修复后重新评审。

Conditional Pass：核心完成，次要问题有修复计划。
