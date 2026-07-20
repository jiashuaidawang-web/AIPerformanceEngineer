我认为 IM-008 应该成为整个工程两大运行时之一。

因为从这份文档开始，系统将第一次完整呈现：

AI Runtime

负责：

理解世界。

Connector Runtime

负责：

感知世界。

二者通过：

Observation

这一唯一事实模型连接。

IM-008 不只是 Connector 工程映射，而是：

Connector Runtime Mapping

下面就是正式冻结版。

# AI Performance Engineer

# IM-008 Connector Runtime Mapping

Version: v1.0

Status: Frozen

Milestone: M2.5 – Implementation Mapping

Document Type: Engineering Specification

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
Implementation Mapping
        │
        ├── Domain Mapping
        ├── Database Mapping
        ├── ClickHouse Mapping
        ├── Graph Mapping
        ├── REST API Mapping
        ├── AI Runtime Mapping
        ├── ★ Connector Runtime Mapping ★
        ├── Repository Mapping
        ├── Service Mapping
        └── Implementation Rules
```

Connector Runtime Mapping 定义 AI Performance Engineer 中所有 Connector 的统一运行时、生命周期、调度机制及与 Domain 的接入规范。

---

# Chapter 1 Purpose（设计目标）

本规范定义 Connector Runtime 的统一架构。

目标：

建立统一 Connector Runtime。

建立统一 Connector 生命周期。

建立统一配置热更新机制。

建立统一健康检查机制。

建立统一错误恢复机制。

Connector Runtime 是 AI World 感知现实世界的唯一入口。

---

# Chapter 2 Design Philosophy（设计哲学）

Connector Runtime：

永远只生产 Observation。

永远不生产 Evidence。

永远不生产 Knowledge。

永远不生产 Recommendation。

统一原则：

Reality

↓

Resource

↓

Observation

↓

Connector Collect

↓

Observation Output

Connector 是 Collector。

不是 Producer。

不是 AI。

不是 Database。

---

# Chapter 3 Runtime Architecture（运行时架构）

整个系统包含两大运行时：

```
AI Runtime
    │
    │ 理解世界
    │
    ▼
Observation  ────────  ──────── Observation
    │                            │
    │                            │
    ▼                            ▼
Domain Object            Connector Runtime
                                   │
                                   │ 感知世界
                                   │
                                   ▼
                              Resource
```

AI Runtime：

消费 Observation。

Connector Runtime：

生产 Observation。

二者通过 Observation 唯一连接。

禁止：

Connector 直接调用 AI。

禁止：

AI 直接调用 Connector。

---

# Chapter 4 Connector Package Mapping

```
connector

├── sdk
├── runtime
│
├── jvm
├── linux
├── mysql
├── redis
├── kafka
├── prometheus
├── otel
│
└── extension
```

sdk：

Connector 统一 SDK。

所有 Connector 必须基于 SDK 开发。

runtime：

统一运行时管理。

负责：

生命周期、调度、健康、配置、隔离。

jvm / linux / mysql / redis / kafka / prometheus / otel：

具体采集实现。

extension：

第三方扩展。

---

# Chapter 5 Connector SDK Mapping

统一 SDK 接口：

```
Connector

├── id()
├── name()
├── version()
├── type()
├── capabilities()
├── initialize()
├── start()
├── stop()
├── pause()
├── resume()
├── health()
├── configure()
└── collect()
```

所有 Connector：

必须实现统一 SDK。

禁止：

绕过 SDK 开发。

---

# Chapter 6 Connector Lifecycle Mapping

统一生命周期：

```
CREATED

↓

INITIALIZED

↓

STARTED

↓

RUNNING

↓

PAUSED

↓

STOPPING

↓

STOPPED

↓

FAILED
```

生命周期管理：

统一由 Runtime 负责。

Connector：

禁止自行管理状态。

---

# Chapter 7 Connector Scheduling Mapping

统一调度方式：

支持：

定时采集。

支持：

事件触发。

支持：

手动触发。

统一接口：

```
Scheduler

├── schedule()
├── cancel()
├── pause()
├── resume()
└── list()
```

调度策略：

频率可配。

并发可控。

资源隔离。

---

# Chapter 8 Connector Observation Mapping

Connector：

唯一合法输出：

Observation。

统一输出：

```
Observation

├── observationId
├── resourceId
├── type
├── name
├── value
├── timestamp
├── connectorId
├── version
└── payload
```

禁止：

Connector 输出 Evidence。

禁止：

Connector 输出 Knowledge。

禁止：

Connector 输出 Recommendation。

---

# Chapter 9 Connector Configuration Mapping

统一配置：

```
ConnectorConfiguration

├── connectorId
├── schedule
├── timeout
├── retryPolicy
├── rateLimit
├── parameters
└── version
```

配置：

支持热更新。

支持版本管理。

支持环境差异。

禁止：

硬编码配置。

---

# Chapter 10 Connector Isolation Mapping

统一隔离：

ClassLoader 隔离。

线程池隔离。

内存配额隔离。

网络访问隔离。

一个 Connector 失败：

不得影响其它 Connector。

---

# Chapter 11 Connector Resilience Mapping

统一容错：

```
RetryPolicy

├── maxRetries
├── backoff
├── timeout
└── circuitBreaker
```

统一错误恢复：

失败重试。

熔断保护。

降级运行。

告警上报。

禁止：

无限重试。

禁止：

静默吞错。

---

# Chapter 12 Connector Registration Mapping

统一注册：

```
ConnectorRegistry

├── register()
├── unregister()
├── discover()
├── get()
├── list()
└── match()
```

Connector 启动：

自动注册。

Connector 下线：

自动注销。

Runtime：

统一管理注册表。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

所有 Connector：

必须基于 SDK。

禁止：

绕过 Runtime。

禁止：

输出非 Observation 数据。

禁止：

跨 Connector 调用。

禁止：

静态单例。

所有错误：

必须上报 Runtime。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Connector SDK 统一。

✔ 生命周期统一。

✔ 调度机制统一。

✔ 配置热更新。

✔ 健康检查完整。

✔ 错误恢复完整。

✔ 隔离机制完整。

✔ 注册发现完整。

✔ 仅输出 Observation。

✔ Runtime 管理统一。

---

# Chapter 15 Freeze Statement

Connector Runtime Mapping 是 AI Performance Engineer 唯一合法的 Connector 运行时规范。

所有 Connector 必须运行于 Runtime。

所有采集必须输出 Observation。

Connector 永远不得直接输出 AI 推理结果。

任何 Runtime 修改必须通过 RFC。

Status：

Frozen.

我建议从 IM-008 开始冻结整个 Connector Runtime 的三条最高法则（建议加入《AI Performance Engineer Constitution》）

Connector Runtime Law-001：Connector Only Produces Observation

Connector 唯一合法输出：

Observation

禁止：

Connector → Evidence

Connector → Knowledge

Connector → Recommendation

Connector Runtime Law-002：Connector Is Not AI

Connector 只是 Collector。

不是推理引擎。

不是分析引擎。

不是知识引擎。

Connector 永远保持：

简单、稳定、可替换。

Connector Runtime Law-003：Two Runtimes, One Observation

整个 AI Performance Engineer：

只有两个 Runtime。

AI Runtime：

理解世界。

Connector Runtime：

感知世界。

二者通过 Observation 唯一连接。

禁止任何第三方 Runtime。

我还有一个建议（也是 IM 系列真正的收口）

从 IM-009 Repository Mapping 开始，我们正式进入工程最核心的三份文档：

IM-009 Repository Mapping

IM-010 Service Mapping

IM-011 Implementation Rules

这三份文档将直接决定：

Rowboat 编码是否一致。

工程质量是否可控。

架构是否真正落地。

因此建议这三份文档：

不再以"映射"为主，而是直接给出 Rowboat 可以直接遵照执行的工程规范。
