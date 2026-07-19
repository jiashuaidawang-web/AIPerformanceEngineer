我正式采纳这个设计，并且我建议从今天开始把它升级为整个产品的第一条架构铁律（Architecture Law No.1）。

这不是一个实现细节，而是整个 AI Performance Engineer 最大的技术护城河之一。

Architecture Law-001
Resource Ownership Principle（资源统一归属原则）
Rule 1

任何 Connector 禁止直接创建、修改、删除 Resource。

只能负责：

Collect Observation
│
▼
Discover Resource Information

例如：

JVM Connector：

发现：

HostName

PID

JVM Name

Java Version

Heap

GC

Redis Connector：

发现：

Master

Slave

Role

Replication

Memory

MySQL Connector：

发现：

Version

Instance

Schema

Replication

Thread

Connector 只负责：

发现（Discovery）

永远不负责：

建模（Modeling）

Rule 2

Connector 必须统一调用：

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

这是唯一允许的入口。

禁止：

Connector

↓

new Resource()

禁止：

Connector

↓

save(Resource)

禁止：

Connector

↓

update(Resource)

以后 Code Review：

看到：

new Resource()

直接：

❌ Reject。

Rule 3

ConnectorResourceMapper

这是整个产品新增的一层。

以后：

每一个 Connector：

都必须拥有：

JvmConnector

↓

JvmResourceMapper

Redis：

RedisConnector

↓

RedisResourceMapper

MySQL：

MysqlConnector

↓

MysqlResourceMapper

Linux：

LinuxConnector

↓

LinuxResourceMapper

以后：

Kafka：

KafkaConnector

↓

KafkaResourceMapper

Oracle：

OracleConnector

↓

OracleResourceMapper

Elastic：

ElasticConnector

↓

ElasticResourceMapper

全部一样。

所以：

Connector：

以后永远不会膨胀。

Rule 4

ResourceDiscoveryService

成为：

整个 AI 世界模型唯一入口。

负责：

UUID生成

↓

Resource Merge

↓

Resource Compare

↓

Cluster Merge

↓

Relationship

↓

Lifecycle

↓

Save

以后：

AI：

如果：

发现：

Redis Master

Redis Slave

DiscoveryService：

负责：

自动：

生成：

Redis Cluster

Connector：

不知道：

Cluster。

Rule 5

Observation

以后：

统一要求：

Observation

↓

resourceId

禁止：

Observation

↓

hostId

禁止：

Observation

↓

redisId

禁止：

Observation

↓

mysqlId

统一：

resourceId

以后：

AI：

永远：

围绕：

Resource。

Rule 6

ResourceRepository

以后：

只有一个。

例如：

Connector

×

ResourceRepository

禁止。

允许：

DiscoveryService

↓

ResourceRepository

统一管理。

Rule 7

Resource 生命周期统一管理

以后：

不是：

Connector：

维护：

ONLINE。

而是：

Lifecycle：

统一维护。

例如：

Connector

↓

Heartbeat

↓

LifecycleManager

↓

ONLINE

例如：

Redis：

失联：

Heartbeat

↓

Timeout

↓

OFFLINE

Connector：

不知道：

OFFLINE。

Rule 8

Cluster

统一：

DiscoveryService：

自动合并。

例如：

Redis：

Master

↓

Slave

↓

Sentinel

Discovery：

自动：

生成：

Redis Cluster

MySQL：

也是。

K8S：

也是。

Rule 9

AI

以后：

全部：

分析：

Resource

不是：

CPU

例如：

AI：

不会说：

CPU高。

而是：

Order System

↓

Redis Cluster

↓

CPU高

↓

导致：

Order API

TPS下降

这是：

整个产品：

最大的区别。

Rule 10（新增，也是我认为未来最值钱的一条）
Connector 永远无状态（Stateless Connector）

以后：

Connector：

永远：

不知道：

Business

Topology

Knowledge

Timeline

Evidence

Optimization

Connector：

唯一职责：

Collect

↓

Discover

结束。

这样：

以后：

Connector：

升级：

不会：

影响：

AI。

AI：

升级：

不会：

影响：

Connector。

完全解耦。

我建议新增一个永久文档

以后：

Blueprint：

不用写。

放：

docs

└── standards

      └──

Architecture-Laws.md

里面：

永久维护：

例如：

Law-001

Resource Ownership Principle

以后：

Law-002

Observation Immutability
Law-003

Everything Is Resource
Law-004

Evidence Never Modify Observation
Law-005

Timeline Is Time Ordered

以后：

一直：

增加。

我建议再冻结一个长期原则（Law-000）

在所有架构法则之前，再定义一条总原则：

Law-000：Single Source of Truth（唯一事实来源）

任何事实（CPU、GC、Redis、MySQL、JMeter、业务链路等）都只能有一个权威来源：

Observation 是指标事实的唯一来源。
Resource 是资源事实的唯一来源。
Timeline 是时间事实的唯一来源。
Evidence 是诊断事实的唯一来源。
Knowledge 是经验事实的唯一来源。

所有 AI 推理、优化建议和报告，都必须基于这些统一事实源，而不能依赖 Connector 私有状态或临时缓存。

我建议把 Law-000 和 Law-001 一起冻结到 docs/standards/Architecture-Laws.md 中。以后所有 Blueprint、代码评审、Gate 验收都必须检查是否违反这些架构法则。这样，我们整个 AI Performance Engineer 就有了长期稳定、可持续演进的架构根基。