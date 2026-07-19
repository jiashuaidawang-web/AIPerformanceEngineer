我认为这是一个非常重要的分界点。

WP001~WP010 是"工程世界"。

从 WP011 开始，我们进入的是 AI 的世界模型（AI World Model）。

这里我不建议 Rowboat 直接写 Java Bean，因为那样以后一定会推倒重来。

我建议从 WP011 开始，我们固定采用下面的流程：

RFC（为什么这样设计）
↓
Blueprint（如何实现）
↓
Coding
↓
Gate
↓
Freeze

以后 WP011~WP030 都按照这个流程。

AI Performance Engineer
RFC-011 Unified Resource Model (URM)

Version: v1.0 (Draft)

1. RFC Goal（设计目标）
   为什么需要 Resource Model？

目前（M1），Observation 已经能够成功采集并存储：

system_cpu_load = 100
heap_used = 512MB
redis_memory = 1.2GB
mysql_threads_running = 36

但是这些 Observation 仍然是孤立的指标。

AI 无法回答真正关心的问题，例如：

"订单系统 CPU 为什么高？"
"Redis 集群是否已经成为瓶颈？"
"哪个业务域压力最大？"
"哪个 Kubernetes Cluster 资源不足？"

原因是：

Observation 没有"归属对象（Owner）"。

因此必须建立统一资源模型（Unified Resource Model），让 AI 理解：

所有 Observation 都属于某一个 Resource。

2. Design Principles（设计原则）

URM 必须满足以下原则：

P1. Everything is a Resource

一切都是 Resource。

例如：

Host
Application
Service
JVM
Redis
Redis Cluster
MySQL
MySQL Cluster
Kafka
Nginx
Kubernetes Cluster
Business Scenario

全部都是 Resource。

P2. Observation 必须绑定 Resource

禁止：

cpu_usage=80

必须：

Resource=Host-01

cpu_usage=80
P3. Resource 可以组成树

例如：

Business

└── Order System

      ├── Order Service

      │      ├── JVM-1

      │      └── JVM-2

      └── Redis Cluster

             ├── Redis Master

             └── Redis Slave

AI 将基于这棵树进行推理。

P4. Resource 必须唯一

每一个 Resource 必须拥有：

ResourceId (UUID)

任何 Observation、Evidence、Timeline、Topology 都引用 ResourceId。

P5. Resource 与部署无关

不能假设：

一台机器 = 一个 Redis

必须支持：

Redis Cluster

↓

多个 Redis Instance

↓

多个 Host

因此：

Deployment 与 Resource 解耦。

3. Domain Model

统一资源对象：

Resource
│
├── BusinessDomain
│
├── BusinessSystem
│
├── Application
│
├── Service
│
├── Instance
│
├── Host
│
├── JVM
│
├── MySQLCluster
│
├── MySQLInstance
│
├── RedisCluster
│
├── RedisNode
│
├── KafkaCluster
│
├── KafkaBroker
│
├── KubernetesCluster
│
├── Pod
│
├── Container
│
└── Scenario
4. Resource Attributes

每一个 Resource 至少包含：

resourceId

resourceType

name

displayName

parentId

clusterId

status

labels

tags

properties

createdTime

updatedTime

说明：

字段	作用
resourceId	全局唯一 ID
resourceType	HOST、REDIS_CLUSTER 等
parentId	组成树
labels	K8s 风格标签
tags	AI 分类标签
properties	Connector 发现的属性
5. Resource Hierarchy

统一层级：

Business Domain

↓

Business System

↓

Application

↓

Service

↓

Instance

↓

Infrastructure

Infrastructure：

Host

↓

JVM

↓

Redis

↓

MySQL

↓

Kafka

↓

Nginx
6. Discovery Principles

Resource 来源只有三种：

Connector Discovery

例如：

Redis Connector：

INFO REPLICATION

↓

Master

↓

Slave

↓

生成 Redis Cluster
Configuration Discovery

用户配置：

application.yml

指定：

Business Domain

Application
AI Discovery（未来）

AI 自动识别：

JMeter

↓

Order API

↓

推断：

Order Service

MVP 不实现，仅保留扩展点。

7. Relationship Model

关系采用统一模型：

BELONGS_TO

DEPENDS_ON

CONNECTS_TO

RUNS_ON

MEMBER_OF

USES

EXPOSES

例如：

JVM

RUNS_ON

Host
Redis Node

MEMBER_OF

Redis Cluster
8. Out of Scope（M2 不实现）

以下内容不属于 WP011：

Evidence
Root Cause
Timeline
Topology Graph
Knowledge Graph
AI Analysis

这些将在后续 WP 中实现。

9. Acceptance Criteria

WP011 完成后必须满足：

所有 Observation 必须绑定 Resource。
Resource 支持树结构。
Resource 支持 Cluster。
Resource 支持多 Host。
Connector 可以创建 Resource。
Resource 可查询。
Resource 生命周期可管理。
10. Impact Analysis

WP011 将成为以下模块的基础：

WP012 Topology Model

↓

WP013 Timeline

↓

WP014 Evidence

↓

WP015 Knowledge

↓

WP016 AI Analysis

↓

WP017 Optimization Engine

任何 Resource Model 的变更，都必须通过 RFC，并保持向后兼容。

RFC 结论

统一资源模型（Unified Resource Model，URM） 将作为 AI Performance Engineer 的核心世界模型。

从 WP011 开始：

Connector 不再直接产生"孤立指标"，而是产生属于 Resource 的 Observation。
AI 不再分析"CPU、内存"本身，而是分析业务系统、应用、集群、中间件资源之间的关系。
下一步

如果这份 RFC 确认冻结，我们再进入：

WP011 Blueprint：《Unified Resource Model》

仍然严格按照已经冻结的 11 章节 Blueprint 模板输出，然后再交给 Rowboat 开发。这样 WP011 就会成为整个 AI 世界模型的稳定基石。


