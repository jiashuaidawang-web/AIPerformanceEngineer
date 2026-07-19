我觉得从 WP011 开始，我们的 Blueprint 要达到一个新的标准。

WP001~WP010 是工程 Blueprint。

WP011 开始是领域 Blueprint（Domain Blueprint）。

它会决定以后所有 Connector、Observation、Evidence、Timeline、Topology、Knowledge 的数据组织方式。

所以这份 Blueprint 我建议直接作为整个 AI 世界模型的"宪法"来设计。

# AI Performance Engineer
# WP011 Blueprint - Unified Resource Model (URM)
Version：v1.0
Status：Draft
Milestone：M2 - AI Domain Foundation

---

# 01. Goal（目标）

建立 AI Performance Engineer 的统一资源模型（Unified Resource Model，URM）。

Resource 是 AI 理解世界的最小对象。

所有 Connector、Observation、Evidence、Timeline、Topology、Knowledge、Optimization 都必须围绕 Resource 展开。

WP011 完成后：

- Observation 不再是孤立指标，而是属于某一个 Resource。
- AI 不再分析 CPU，而是分析 Resource。
- Resource 可以组成业务树、集群树、部署树。
- Resource 成为整个系统唯一的领域对象。

---

# 02. Acceptance Criteria（验收标准）

完成后必须满足：

## AC-001

支持统一 Resource 对象。

## AC-002

支持 Resource Tree。

例如：

Business

↓

Application

↓

Service

↓

Instance

↓

Host

↓

JVM

↓

CPU

---

## AC-003

支持 Cluster。

例如：

Redis Cluster

↓

Redis Node

↓

Host

---

## AC-004

所有 Observation 必须绑定 ResourceId。

禁止存在孤立 Observation。

---

## AC-005

支持 Resource 生命周期。

UNKNOWN

↓

DISCOVERED

↓

ONLINE

↓

OFFLINE

↓

DELETED

---

## AC-006

支持 Resource 查询。

支持：

按：

- 类型
- 名称
- Cluster
- Parent
- Label
- Tag

查询。

---

## AC-007

Connector 可以自动创建 Resource。

---

## AC-008

所有 Resource 必须具有 UUID。

---

## AC-009

支持标签。

例如：

environment=prod

team=order

region=hangzhou

---

## AC-010

支持扩展 Resource Type。

新增 Connector 不允许修改 Resource 核心代码。

---

# 03. Package List

```
aipe-domain
└── resource
    ├── model
    ├── service
    ├── repository
    ├── discovery
    ├── lifecycle
    ├── relation
    ├── query
    └── event
```

---

# 04. Class List

## Core

```
Resource
```

统一资源对象。

---

```
ResourceType
```

资源类型。

---

```
ResourceStatus
```

生命周期状态。

---

```
ResourceRelation
```

资源关系。

---

```
ResourceLabel
```

标签。

---

```
ResourceTag
```

AI标签。

---

```
ResourceProperty
```

扩展属性。

---

## Discovery

```
ResourceDiscoveryService
```

负责创建 Resource。

---

```
ConnectorResourceMapper
```

Connector → Resource

---

## Repository

```
ResourceRepository
```

---

```
ResourceQueryService
```

---

```
ResourceLifecycleManager
```

---

```
ResourceRelationManager
```

---

# 05. Method List

## Resource

```java
UUID getId();

ResourceType getType();

String getName();

UUID getParentId();

UUID getClusterId();

Map<String,String> getLabels();

Map<String,Object> getProperties();
```

---

## Discovery

```java
discover()

create()

merge()

update()

delete()
```

---

## Repository

```java
save()

update()

delete()

findById()

findByType()

findChildren()

findCluster()

findAll()
```

---

## Lifecycle

```java
online()

offline()

deleted()

heartbeat()

refresh()
```

---

## Query

```java
queryByLabel()

queryByTag()

queryTree()

queryCluster()

queryTopology()
```

---

# 06. Dependency（依赖关系）

依赖：

```
WP001 Agent Runtime

WP002 Connector SDK

WP003 JVM Connector

WP004 Linux Connector

WP005 Redis Connector

WP006 MySQL Connector

WP007 Observation Pipeline

WP008 Storage Layer

WP009 Configuration Manager

WP010 Scenario Manager
```

被依赖：

```
WP012 Topology Model

WP013 Timeline

WP014 Evidence

WP015 Knowledge

WP016 AI Analysis

WP017 Optimization Engine
```

---

# 07. Java File List（Rowboat 必须物理创建）

```
Resource.java

ResourceType.java

ResourceStatus.java

ResourceRelation.java

ResourceLabel.java

ResourceTag.java

ResourceProperty.java

ResourceRepository.java

ResourceService.java

ResourceQueryService.java

ResourceDiscoveryService.java

ResourceLifecycleManager.java

ResourceRelationManager.java

ConnectorResourceMapper.java

ResourceController.java

ResourceDTO.java

ResourceEntity.java

ResourceMapper.xml

ResourceMapper.java

ResourceEvent.java

ResourceTree.java

ClusterResource.java

HostResource.java

JvmResource.java

RedisClusterResource.java

RedisNodeResource.java

MysqlClusterResource.java

MysqlInstanceResource.java

ApplicationResource.java

ServiceResource.java

InstanceResource.java
```

预计：

30+ Java 文件。

禁止：

TODO。

禁止：

Mock。

---

# 08. Sequence（时序图）

```
Connector

↓

collect()

↓

ResourceDiscoveryService

↓

ConnectorResourceMapper

↓

ResourceRepository

↓

Resource Saved

↓

ObservationBuilder

↓

Observation(ResourceId)

↓

Storage

↓

Backend Query
```

自动发现流程：

```
Redis Connector

↓

INFO

↓

发现：

Master

↓

发现：

Slave

↓

生成：

Redis Cluster

↓

生成：

Redis Node

↓

建立：

MEMBER_OF
```

---

# 09. State Machine（状态机）

```
UNKNOWN

↓

DISCOVERED

↓

INITIALIZING

↓

ONLINE

↓

UNHEALTHY

↓

OFFLINE

↓

RECOVERING

↓

ONLINE

↓

DELETED
```

生命周期规则：

- Connector 首次发现 → DISCOVERED
- 心跳正常 → ONLINE
- 连续失败 → UNHEALTHY
- 超时 → OFFLINE
- 再次发现 → RECOVERING
- 删除 → DELETED

Observation 不允许写入 DELETED Resource。

---

# 10. Real Implementation Requirement（真实实现要求）

禁止：

- Mock Resource
- Fake UUID
- TODO
- 空实现

必须：

- Resource 自动生成 UUID。
- Connector 自动发现 Resource。
- Resource 自动更新状态。
- Observation 必须绑定 ResourceId。
- Repository 必须真实落 MySQL。
- Resource 查询接口必须可运行。

所有 Connector 必须通过 ConnectorResourceMapper 建立 Resource。

不得绕过 ResourceRepository。

---

# 11. Deliverables（交付物）

完成后必须交付：

√ Resource Domain Model

√ Resource Repository

√ Resource REST API

√ Resource Discovery Engine

√ Resource Lifecycle Manager

√ Resource Query Engine

√ Resource Relation Manager

√ Resource Tree

√ Resource Cluster

√ Resource Entity

√ MyBatis Mapper

√ 数据库初始化 SQL

√ 单元测试

√ 集成测试

√ M2 Gate 测试报告

完成标准：

Resource 成为 AI 世界唯一统一资源对象。

后续所有 Observation、Evidence、Topology、Knowledge、AI Analysis 均以 Resource 为唯一入口。

Milestone：

M2 AI Domain Foundation（Part 1）完成。

我建议对 WP011 再增加一个要求（这会成为我们产品最大的护城河）

从 WP011 开始，任何 Connector 都不允许直接创建 Resource。

统一要求：

Connector
│
▼
ConnectorResourceMapper
│
▼
ResourceDiscoveryService
│
▼
ResourceRepository
│
▼
Resource

这样做有几个关键好处：

Connector 完全不知道 Resource 的内部结构，可以独立开发和升级。
新增 Kafka、RocketMQ、Elasticsearch、Oracle 等 Connector 时，只需要新增对应的 ConnectorResourceMapper，无需修改核心领域模型。
Resource 的创建、合并、去重、生命周期管理都集中在 ResourceDiscoveryService，避免不同 Connector 各自实现导致数据混乱。
后续做 AI 自动发现、数字孪生拓扑、知识图谱时，都可以复用这一套统一入口，而不需要修改 Connector。

我建议把这一条正式写入《Engineering Standards》，作为整个 AI Performance Engineer 的核心架构原则之一，以后所有 Connector 都必须遵守。