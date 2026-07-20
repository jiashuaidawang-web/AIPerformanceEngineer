# AI Performance Engineer

# IM-001 Java Package Mapping

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

```
AI World Foundation
        │
        ▼
Implementation Mapping
        │
        ├── IM-001 Java Package Mapping
        ├── IM-002 Domain Model Mapping
        ├── IM-003 Database Schema Mapping
        ├── IM-004 ClickHouse Mapping
        ├── IM-005 Graph Mapping
        ├── IM-006 REST API Mapping
        ├── IM-007 AI Engine Mapping
        ├── IM-008 Connector Mapping
        ├── IM-009 Repository Mapping
        ├── IM-010 Service Mapping
        └── IM-011 Implementation Rules
```

Java Package Mapping 是整个工程代码结构的唯一标准。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 的 Java 包结构及模块边界。

目标：

建立统一、稳定、可扩展的工程目录。

任何 Java 类必须归属于唯一 Package。

任何 Package 必须拥有唯一职责。

Package 的设计必须反映 AI World，而不是数据库或框架。

---

# Chapter 2 Design Philosophy（设计哲学）

Package 是世界模型的工程映射。

Package 的划分遵循：

Business

↓

World Model

↓

Engineering

而不是：

Controller

↓

Service

↓

DAO

AI Performance Engineer 采用：

DDD + Clean Architecture + Hexagonal Architecture。

---

# Chapter 3 Top Level Package

统一 Root Package：

```
com.aipe
```

一级 Package 固定如下：

```
com.aipe

├── bootstrap
├── common
├── config
├── api
├── application
├── domain
├── repository
├── infrastructure
├── connector
├── ai
├── scheduler
├── event
├── security
├── integration
└── test
```

一级 Package 禁止新增。

新增一级 Package 必须经过 RFC。

---

# Chapter 4 Package Responsibilities

## bootstrap

应用启动。

SpringBoot。

AutoConfiguration。

生命周期管理。

---

## common

公共组件。

工具类。

异常。

Result。

Constants。

禁止放业务逻辑。

---

## config

Spring 配置。

Bean 注册。

配置读取。

AutoConfiguration。

禁止业务代码。

---

## api

REST API。

Controller。

DTO。

Request。

Response。

OpenAPI。

禁止业务逻辑。

禁止 Repository。

---

## application

应用服务。

事务。

流程编排。

Command。

Query。

UseCase。

AI Workflow。

这里只负责：

协调。

禁止保存状态。

---

## domain

核心领域。

AI World。

纯业务对象。

不得依赖 Spring。

不得依赖 MyBatis。

不得依赖数据库。

---

## repository

Repository Interface。

Repository Implementation。

统一数据访问。

Application 永远通过 Repository 获取数据。

---

## infrastructure

基础设施。

MySQL。

ClickHouse。

Neo4j。

Redis。

Kafka。

HTTP。

SDK。

第三方组件。

---

## connector

Connector SDK。

JVM。

Linux。

Redis。

MySQL。

Kafka。

Prometheus。

OTel。

所有采集器。

---

## ai

AI Engine。

Reasoning。

Evidence。

Knowledge。

Recommendation。

Optimization。

Prompt。

Embedding。

LLM。

---

## scheduler

定时任务。

后台扫描。

清理。

同步。

重建。

---

## event

领域事件。

Event Bus。

Message。

Observer。

---

## security

认证。

授权。

RBAC。

Token。

API Security。

---

## integration

第三方系统集成。

Webhook。

CMDB。

Jenkins。

GitLab。

Kubernetes。

---

## test

测试。

Fixture。

Fake。

Benchmark。

---

# Chapter 5 Domain Package Mapping

domain 固定目录：

```
domain

├── resource
├── observation
├── relationship
├── topology
├── timeline
├── evidence
├── verification
├── knowledge
├── recommendation
├── execution
├── optimization
└── shared
```

shared：

只放：

Value Object。

Enum。

Specification。

Policy。

禁止业务流程。

---

# Chapter 6 Application Package Mapping

```
application

├── command
├── query
├── service
├── workflow
├── mapper
├── dto
└── facade
```

Application：

负责：

编排。

协调。

事务。

调用 Domain。

不得直接操作数据库。

---

# Chapter 7 Infrastructure Package Mapping

```
infrastructure

├── mysql
├── clickhouse
├── graph
├── redis
├── kafka
├── http
├── persistence
├── discovery
└── metrics
```

所有第三方依赖：

统一放入 Infrastructure。

禁止 Domain 引用 Infrastructure。

---

# Chapter 8 Connector Package Mapping

```
connector

├── sdk
├── runtime
├── jvm
├── linux
├── mysql
├── redis
├── kafka
├── prometheus
├── otel
└── extension
```

Connector：

统一输出：

Observation。

禁止 Connector 输出：

Evidence。

Knowledge。

Recommendation。

---

# Chapter 9 AI Package Mapping

```
ai

├── timeline
├── evidence
├── verification
├── knowledge
├── recommendation
├── execution
├── optimization
├── reasoning
├── prompt
├── memory
└── llm
```

AI：

只消费 Domain。

禁止直接访问 Connector。

禁止直接访问 Controller。

---

# Chapter 10 Dependency Rules

统一依赖方向：

```
API
    │
    ▼
Application
    │
    ▼
Domain
    │
    ▼
Repository
    │
    ▼
Infrastructure
```

Connector：

独立产生 Observation。

AI：

只能消费 Domain。

禁止：

Repository → API。

禁止：

Infrastructure → Domain。

禁止：

Domain → Spring。

禁止循环依赖。

---

# Chapter 11 Package Naming Rules

统一采用：

全部小写。

单数名词。

禁止：

util

manager

helper

misc

temp

common2

test1

demo

new

old

v2

禁止缩写。

例如：

observation

不要：

obs。

---

# Chapter 12 Example Package Layout

```
com.aipe

├── api
│     └── observation
│
├── application
│     └── observation
│
├── domain
│     └── observation
│
├── repository
│     └── observation
│
├── infrastructure
│     └── clickhouse
│
└── ai
      └── evidence
```

Observation 在所有层均保持统一命名。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package。

不得改变 Package 名称。

不得跨层引用。

所有新增类必须放入规定 Package。

任何违反 Package Mapping 的代码必须拒绝提交。

Package Mapping 高于代码实现。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Root Package 唯一。

✔ 一级 Package 固定。

✔ Domain 独立。

✔ Infrastructure 独立。

✔ Connector 独立。

✔ AI 独立。

✔ API 不包含业务。

✔ Package 无循环依赖。

✔ Package 命名统一。

✔ 所有新增代码均可归属唯一 Package。

---

# Chapter 15 Freeze Statement

Java Package Mapping 是 AI Performance Engineer 唯一合法的工程目录规范。

任何 Java 文件必须首先符合本规范。

任何 Package 调整必须通过 RFC。

禁止为了开发方便修改 Package 结构。

Status：

Frozen.

我对 IM 系列做一个升级建议（建议现在冻结）

随着 IM-001 完成，我发现 IM 的作用已经超出了"映射"。

它实际上在定义整个工程的"施工规范"。

因此，我建议后续 IM-002 ~ IM-011 统一采用下面的原则：

每一份 IM 文档都必须最终回答一个问题：

"如果 Rowboat 完全按照这份文档编码，它是否能够写出一致、可运行、可维护的代码，而无需再自行做架构决策？"

如果答案是否定的，那么说明这份 IM 还不够完整。

这也将成为我们后续所有 Implementation Mapping 文档的质量标准。