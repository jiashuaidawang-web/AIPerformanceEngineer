# AI Performance Engineer

# Blueprint Standard v1.0

Version: v1.0

Status: Frozen

Milestone: M2.5 – Implementation Mapping

Document Type: Engineering Standard

---

# （固定第一页）

# AI World Overview

（引用统一封面）

---

# （固定第二页）

# Document Position

```text
AI World Foundation
        │
        ▼
Engineering Standards
        │
        ├── Architecture Laws
        ├── Specification Writing Standard
        ├── ★ Blueprint Standard ★
        └── Gate Standard
```

Blueprint Standard 定义 AI Performance Engineer 所有 Work Package（WP）的统一设计模板。

所有 WP011~WP030 必须严格遵循本模板。

---

# Chapter 1 Purpose（目的）

本规范定义 Work Package Blueprint 的统一编写模板。

目标：

建立统一 Blueprint 结构。

保证所有 WP 设计深度一致。

保证 Rowboat 可直接依据 Blueprint 编码。

保证 WP 之间依赖清晰。

Blueprint 是 IM 与 Code 之间唯一合法设计文档。

---

# Chapter 2 Design Philosophy（设计哲学）

Blueprint：

不是设计文档。

是施工图纸。

Blueprint：

面向 Rowboat。

面向编码。

面向验证。

统一原则：

IM（工程映射）

↓

Blueprint（施工图纸）

↓

Rowboat Code（代码实现）

Blueprint：

必须精确到：

Package、Class、Method、Field。

禁止：

模糊描述。

禁止：

"具体实现由开发人员决定"。

---

# Chapter 3 Blueprint Metadata（元数据规范）

每个 Blueprint 必须包含以下元数据：

```
WP{编号}-{名称}.md

Document Type: Work Package Blueprint

Version: v1.0

Status: Draft → Frozen

Milestone: Mx

Priority: P0 / P1 / P2

DependsOn:
  - WPxxx
  - IM-xxx
  - Law-xxx

RequiredBy:
  - WPxxx

EstimatedJavaFiles: {数量}

EstimatedWorkload: {天数} Days

Blueprint Template: Blueprint Standard v1.0
```

元数据：

必须在 Blueprint 最顶部。

DependsOn：

必须列出所有上游依赖。

RequiredBy：

必须列出所有下游依赖。

---

# Chapter 4 Section 1 — Goal（目标）

每个 Blueprint 必须以 Goal 开头。

## 4.1 Purpose

回答：

这个 WP 为什么存在？

说明：

核心解决问题。

核心能力。

## 4.2 Capability Added

回答：

Before / After。

说明：

新增能力。

## 4.3 Core Principle

回答：

最高设计原则。

说明：

引用相关 Law。

## 4.4 Scope

回答：

包含什么。

不包含什么。

说明：

MVP 边界。

后续 WP 边界。

---

# Chapter 5 Section 2 — Acceptance Criteria（验收标准）

每个 Blueprint 必须定义验收标准。

## 5.1 Functional Acceptance

回答：

功能上必须支持什么？

采用：

□ 条目。

## 5.2 Technical Acceptance

回答：

技术上必须满足什么？

采用：

□ 条目。

## 5.3 Integration Acceptance

回答：

完整链路是什么？

采用：

端到端流程描述。

验收标准：

必须可测试。

必须可验证。

---

# Chapter 6 Section 3 — Package List（包结构）

每个 Blueprint 必须定义包结构。

```
com.aipe.{domain}

├── controller
├── service
├── repository
├── model
├── validator
├── lifecycle
└── support
```

必须包含：

Package Responsibility 表。

每个 Package：

一行职责。

禁止：

无 Package 描述。

---

# Chapter 7 Section 4 — Class List（类清单）

每个 Blueprint 必须列出所有类。

每个类必须包含：

```
类名

Package: com.aipe.{domain}.{package}

职责: 一句话

字段表:

字段    类型    说明
```

字段表：

必须对齐 IM Schema。

禁止：

字段与 Schema 不一致。

---

# Chapter 8 Section 5 — Method List（方法清单）

每个 Blueprint 必须列出关键方法。

每个方法必须包含：

```
// 方法注释
ReturnType methodName(ParameterType param)
```

方法签名：

必须完整。

必须包含：

参数类型。

返回类型。

---

# Chapter 9 Section 6 — Dependency（依赖关系）

每个 Blueprint 必须定义依赖。

## 9.1 Internal Dependency

回答：

WP 之间的依赖。

采用：

依赖图。

## 9.2 External Dependency

回答：

外部组件依赖。

采用：

依赖表。

## 9.3 Dependency Rule

回答：

依赖规则。

说明：

禁止循环依赖。

---

# Chapter 10 Section 7 — Physical File List（物理文件清单）

每个 Blueprint 必须列出物理文件。

```
{module}/
├── src/main/java/com/aipe/{domain}/
│   ├── controller/XxxController.java
│   ├── service/XxxService.java
│   └── repository/XxxRepository.java
├── src/main/resources/db/migration/V{nn}__{name}.sql
└── src/main/resources/application.yml
```

物理文件：

必须与 Class List 一一对应。

---

# Chapter 11 Section 8 — Sequence Diagram（时序图）

每个 Blueprint 必须包含核心流程时序图。

采用：

文本时序图。

```
Actor
↓ 调用
Service
↓ 调用
Repository
↓ 写入
Storage
↓ 返回
Service
↓ 返回
Actor
```

时序图：

必须覆盖核心场景。

---

# Chapter 12 Section 9 — State Machine（状态机）

涉及状态转换的 Blueprint 必须定义状态机。

```
STATE_A
↓
STATE_B
↓
STATE_C
```

状态机：

必须列出所有状态。

必须列出所有转换。

---

# Chapter 13 Section 10 — Implementation Constraints（实现约束）

每个 Blueprint 必须定义实现约束。

## 10.1 Must Implement

回答：

必须实现什么？

## 10.2 Forbidden

回答：

禁止什么？

## 10.3 Engineering Rules

回答：

工程规则是什么？

约束：

必须引用相关 Law。

必须引用相关 IM。

---

# Chapter 14 Section 11 — Test & Verification（测试与验证）

每个 Blueprint 必须定义测试方案。

## 14.1 Build

回答：

构建命令。

## 14.2 Test Scenario

回答：

测试场景。

## 14.3 Verification

回答：

验证点。

## 14.4 Expected Result

回答：

预期结果。

采用：

JSON / 文本。

## 14.5 Troubleshooting

回答：

常见问题。

---

# Chapter 15 Writing Principles（编写原则）

所有 Blueprint 必须遵循以下原则：

## Principle 1：Rowboat Executable

Blueprint 必须精确到 Rowboat 可直接编码。

禁止：

"具体实现由开发人员决定"。

禁止：

模糊描述。

## Principle 2：IM Traceable

每个设计决策：

必须能追溯到：

IM 或 Law。

禁止：

与 IM 冲突。

## Principle 3：Dependency Explicit

所有依赖：

必须显式列出。

禁止：

隐式依赖。

## Principle 4：Acceptance Verifiable

验收标准：

必须可测试。

必须可验证。

禁止：

主观判断。

## Principle 5：Scope Clear

MVP 边界：

必须清晰。

禁止：

范围蔓延。

---

# Chapter 16 Document Hierarchy（文档层级）

Blueprint 在整个文档体系中的位置：

```
IM（工程映射）
    │
    ▼
Blueprint（施工图纸）
    │
    ▼
Rowboat Code（代码实现）
    │
    ▼
Gate（验收门）
```

Blueprint：

高于 Code。

低于 IM。

---

# Chapter 17 Freeze Policy（冻结策略）

Blueprint 冻结后：

禁止直接修改。

修改流程：

RFC

↓

Architecture Review

↓

Approval

↓

New Version

冻结前：

Status：Draft。

冻结后：

Status：Frozen。

---

# Chapter 18 Acceptance Criteria（验收标准）

必须满足：

✔ 所有 Blueprint 遵循统一模板。

✔ 元数据完整。

✔ Goal 清晰。

✔ 验收标准可验证。

✔ Package 结构符合 IM-001。

✔ Class 字段符合 IM-003/004/005。

✔ Method 签名完整。

✔ 依赖关系清晰。

✔ 物理文件清单完整。

✔ 实现约束引用 Law/IM。

---

# Chapter 19 Freeze Statement（冻结声明）

Blueprint Standard 是 AI Performance Engineer 唯一合法的 Work Package 设计模板。

所有 WP011~WP030 必须严格遵循本模板。

任何模板修改必须通过 RFC。

Status：Frozen.

我建议正式冻结四大基础标准（M2.5 收口）

完成 Blueprint Standard 后，AI Performance Engineer 四大基础标准已全部就位：

Architecture Laws —— 世界运行的基本法则。

Specification Writing Standard —— 规范文档的统一写法。

Blueprint Standard —— Work Package 的统一设计模板。

Gate Standard —— 质量门禁和验收方式。

四大标准：

共同构成整个项目的"ISO 标准"。

后续所有工作：

都将在这套标准之下展开。
