M2-007 Unified Resource Model Specification（定义"世界中的对象"）
我认为 M2-007 是整个 AI Performance Engineer 最重要的一份规范。

甚至我认为它的重要性 高于 Observation。

因为我们真正的创新不是：

JVM、Redis、MySQL 的采集。

真正的创新其实是：

整个企业世界，统一抽象成 Resource。

这一层一旦设计好，后面 Kubernetes、Docker、Redis、MySQL、RocketMQ、Nacos、ES、业务系统……全部都变成同一种对象。

这是整个 AI World 的核心。

所以，我建议这份文档写得比 Observation 更严格。

# AI Performance Engineer

# M2-007 Unified Resource Model Specification

Version: v1.0

Status: Frozen

Milestone: M2 – AI Domain Foundation

---

# （固定第一页）
AI World Overview（引用统一模板）

---

# （固定第二页）
Document Position（Current Document：Unified Resource Model）

```text
AI World
    │
    ├── Vocabulary
    │
    ├──────────────► Resource ◄──────────────┐
    │                  │                     │
    │                  ▼                     │
    │             Observation               │
    │                  ▼                     │
    │             Relationship              │
    │                  ▼                     │
    │               Topology                │
    │                  ▼                     │
    │               Timeline                │
    │                  ▼                     │
    │               Evidence                │
    │                  ▼                     │
    │               Knowledge               │
    └───────────────────────────────────────┘
```

Resource 是 AI World 中所有对象的统一抽象。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 中 Resource 的统一领域模型。

Resource 是 AI World 中唯一合法的对象（Object）。

企业中的所有可识别实体，都必须抽象为 Resource。

任何 Connector、Agent、Pipeline、AI Engine、Storage、Topology 均不得绕过 Resource 建模。

---

# Chapter 2 Design Philosophy（设计哲学）

传统监控平台采用"类型驱动"建模：

- Host
- JVM
- Redis
- MySQL
- Pod
- Kafka
- MQ

每一种对象拥有不同的数据模型。

这种设计导致：

- AI 无法统一理解对象；
- Connector 与平台深度耦合；
- 新增中间件需要新增一套模型。

AI Performance Engineer 采用统一对象模型：

> **Everything Is Resource.**

任何具有身份（Identity）、生命周期（Lifecycle）和状态（State）的对象，都统一抽象为 Resource。

Resource 是 AI World 唯一的对象类型。

---

# Chapter 3 Definition（统一定义）

## Resource

Resource 是：

> 企业数字世界中具有唯一身份、生命周期、状态和关系的统一对象抽象。

一个 Resource 必须满足：

- 可以被唯一识别；
- 可以被 Observation 描述；
- 可以参与 Relationship；
- 可以进入 Topology；
- 可以形成 Timeline；
- 可以参与 AI 推理。

---

# Chapter 4 Characteristics（核心特征）

Resource 必须具备以下特征：

### 4.1 Identity

拥有全局唯一 ResourceId。

ResourceId 在整个 Enterprise 内唯一。

---

### 4.2 Type

拥有明确类型。

例如：

- JVM
- Redis
- MySQL
- Kubernetes Pod
- Linux Host
- Business Service

类型用于分类，不影响统一模型。

---

### 4.3 Lifecycle

拥有完整生命周期。

例如：

```
Discovered
    ↓
Registered
    ↓
Running
    ↓
Offline
    ↓
Archived
```

---

### 4.4 Observable

必须能够产生 Observation。

不能产生 Observation 的对象，不属于 Resource。

---

### 4.5 Relational

必须能够建立 Relationship。

Resource 不允许孤立存在。

---

### 4.6 Evolvable

允许未来扩展属性。

新增属性不得影响已有模型。

---

# Chapter 5 Classification（资源分类）

Resource 按职责划分为六大类：

## Business Resource

业务对象：

- Business System
- Service
- API
- Job

---

## Compute Resource

计算资源：

- Host
- VM
- Container
- Pod

---

## Runtime Resource

运行时资源：

- JVM
- Python Runtime
- Node.js Runtime

---

## Middleware Resource

中间件：

- Redis
- MySQL
- Kafka
- RocketMQ
- Elasticsearch
- Nacos

---

## Infrastructure Resource

基础设施：

- Kubernetes Cluster
- Namespace
- Node
- LoadBalancer

---

## External Resource

外部依赖：

- OSS
- S3
- Third Party API
- Payment Gateway

未来允许新增新的 Resource Category。

---

# Chapter 6 Lifecycle（生命周期）

```
Discovery
    ↓
Register
    ↓
Observe
    ↓
Relate
    ↓
Analyze
    ↓
Optimize
    ↓
Offline
    ↓
Archive
```

说明：

Discovery 负责发现。

Observation 描述状态。

Relationship 建立连接。

Topology 描述整体结构。

---

# Chapter 7 Schema（统一结构）

统一逻辑结构：

| 字段 | 必填 | 描述 |
|------|------|------|
| resourceId | ✔ | 全局唯一标识 |
| resourceType | ✔ | Resource 类型 |
| name | ✔ | 资源名称 |
| category | ✔ | 六大资源分类 |
| parentId | | 父资源 |
| businessSystemId | | 所属业务系统 |
| labels | | 标签 |
| attributes | | 扩展属性 |
| status | ✔ | 生命周期状态 |
| discoveredAt | ✔ | 首次发现时间 |
| updatedAt | ✔ | 最近更新时间 |
| version | ✔ | Resource Schema Version |

Schema 为逻辑模型，不绑定数据库。

---

# Chapter 8 Relationship（关系模型）

每个 Resource 可以拥有多个关系：

```
BELONGS_TO
RUNS_ON
DEPENDS_ON
CALLS
MEMBER_OF
CONNECTS_TO
HOSTS
USES
EXPOSES
```

Relationship 独立维护。

Resource 不保存完整拓扑。

---

# Chapter 9 Ownership（归属原则）

遵循：

Law-001

> Everything Is Resource.

唯一 Owner：

Resource Repository。

创建：

Discovery Engine。

更新：

Discovery Engine。

消费：

Observation

Relationship

Topology

Timeline

Evidence

AI Engine

Connector 不允许直接创建 Resource。

---

# Chapter 10 Constraints（架构约束）

禁止：

Connector new Resource。

禁止：

Observation 不引用 Resource。

禁止：

Resource 没有生命周期。

禁止：

Resource 没有唯一 ID。

禁止：

Resource 自己保存 Observation。

Observation 必须独立维护。

---

# Chapter 11 Versioning（版本策略）

Schema 独立版本。

新增属性：

Minor Version。

删除字段：

Major Version。

必须保证向后兼容。

---

# Chapter 12 Examples（标准示例）

正确：

```
Business System

↓

Order Service

↓

JVM

↓

Redis

↓

MySQL
```

每一个对象都是 Resource。

---

正确：

```
Redis Cluster

ResourceId:

redis-cluster-01
```

Observation：

```
resourceId:

redis-cluster-01
```

---

错误：

```
Connector

↓

new Redis()
```

错误原因：

Connector 不拥有 Resource。

---

# Chapter 13 Reference Implementation（参考实现）

建议实现：

```
com.aipe.domain.resource

├── Resource
├── ResourceType
├── ResourceCategory
├── ResourceStatus
├── ResourceLabel
├── ResourceAttribute
├── ResourceRepository
├── ResourceDiscoveryService
├── ResourceLifecycleManager
├── ResourceRegistry
└── ResourceValidator
```

Reference API：

```
register()

merge()

update()

offline()

archive()

findById()

findByBusinessSystem()

findByCategory()
```

---

# Chapter 14 Acceptance Criteria（验收标准）

必须满足：

✔ 所有对象统一抽象为 Resource。

✔ ResourceId 全局唯一。

✔ Resource 必须具有生命周期。

✔ Resource 必须能够产生 Observation。

✔ Resource 必须能够建立 Relationship。

✔ Connector 不允许直接创建 Resource。

✔ Resource Schema 支持未来扩展。

✔ AI Engine 仅面向 Resource 工作。

---

# Chapter 15 Freeze Statement（冻结声明）

Resource 是 AI Performance Engineer 中唯一合法的领域对象。

本规范冻结后：

- 禁止新增新的对象模型；
- 禁止绕过 Resource 建模；
- 禁止 Connector 直接维护 Resource；
- 所有对象必须首先注册为 Resource，再进入 AI World。

任何修改必须通过 RFC 审核。

Status：Frozen