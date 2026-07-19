原因：

我们的目标不是监控系统，而是：

大促压测 → 定位瓶颈

生产系统里第二大核心瓶颈通常就是：

Redis缓存击穿
Redis慢查询
连接池耗尽
集群节点异常

WP005 会开始进入真正的中间件集群识别能力。

WP005 是整个 AI Performance Engineer 中第一个中间件领域 Connector。

它的重要性非常高，因为前面的：

WP003 JVM Connector → 应用运行状态
WP004 Linux Connector → 主机资源状态

只能看到：

应用和机器发生了什么

但是企业大促场景真正的问题经常发生在：

Redis连接池耗尽
Redis QPS达到瓶颈
Master节点压力过高
Slave复制延迟
Big Key
Slow Query
Cluster Slot异常
网络阻塞

所以 WP005 开始引入：

Service / Middleware Domain

也是后面：

Topology
+
Cluster Discovery
+
Root Cause Analysis

的基础。

严格按照：

01-Blueprint-Template.md v1.0

# AI Performance Engineer
# WP005-Redis-Connector Blueprint v1.0


> Document Type: Work Package Blueprint
>
> Version: v1.0
>
> Status: Frozen
>
> Milestone: M1 - Agent MVP
>
> Priority: P0
>
> Owner: AI
>
> DependsOn:
> - WP001 Agent Bootstrap
> - WP002 Connector SDK
> - WP007 Observation Pipeline
>
> RequiredBy:
> - WP010 Resource Model
> - WP011 Topology Model
> - WP018 Root Cause Engine
> - WP020 Optimization Engine
>
> EstimatedJavaFiles: 50
>
> EstimatedWorkload: 7 Days
>
> Blueprint Template:
> 01-Blueprint-Template.md v1.0


---

# 1. Goal（目标）


# 1.1 Purpose


WP005实现 Redis 中间件性能采集能力。


目标：

通过 Redis Connector：

自动连接 Redis 实例。

识别 Redis 服务。

采集：

- Redis运行状态
- 性能指标
- 内存状态
- Client连接
- SlowLog
- Cluster状态


生成统一：

Observation。


---

# 1.2 Capability Added


Before:


系统只能感知：

Application

Host


After:


系统新增：

Middleware Domain


完整链路：

```
JVM

↓

Application

↓

Linux Host

↓

Redis Middleware
```


AI可以分析：

例如：

```
TPS下降

↓

JVM正常

↓

CPU正常

↓

Redis connected_clients暴涨

↓

Redis成为瓶颈
```


---

# 1.3 Scope


包含：

- Redis连接
- Redis实例识别
- Redis Cluster识别
- Redis指标采集
- Redis节点关系发现
- Observation生成


不包含：

- Redis自动优化执行
- Redis配置修改
- Redis扩容


---

# 2. Acceptance Criteria（验收标准）


# 2.1 Functional Acceptance


```
□ 支持Redis Standalone

□ 支持Redis Sentinel

□ 支持Redis Cluster

□ 自动识别Redis节点

□ 采集Redis INFO指标

□ 采集Client连接

□ 采集SlowLog

□ 生成Observation

□ 生命周期正常
```


---

# 2.2 Technical Acceptance


```
□ 使用Redis真实API

□ 禁止Mock

□ 禁止固定指标

□ 支持认证密码

□ 支持TLS配置

□ 支持异常隔离
```


---

# 2.3 Integration Acceptance


完整链路：

```
Agent

↓

Connector SDK

↓

RedisConnector

↓

RedisCollector

↓

ObservationEmitter
```


---

# 3. Package List（包结构）


```
com.aipe.connector.redis


├── RedisConnector


├── client


├── discovery


├── collector


│
├── info

│
├── memory

│
├── client

│
├── slowlog

│
├── cluster

│
├── replication


├── model


├── builder


├── config


└── support

```


---

## Package Responsibility


|Package|Responsibility|
|-|-|
|client|Redis连接|
|discovery|节点发现|
|collector|指标采集|
|model|Redis模型|
|builder|Observation转换|
|config|配置|
|support|工具|


---

# 4. Class List（类清单）


---

# 4.1 RedisConnector


Package:

```
com.aipe.connector.redis
```


Responsibility:


Redis Connector 主入口。


负责：

- 初始化Redis客户端
- 节点发现
- Collector管理


---

# 4.2 RedisClientFactory


Package:

```
client
```


Responsibility:


创建Redis连接。


支持：

- password
- timeout
- TLS


---

# 4.3 RedisConnection


Package:

```
client
```


Responsibility:


Redis连接封装。


---

# 4.4 RedisDiscovery


Package:

```
discovery
```


Responsibility:


Redis拓扑发现。


识别：

Standalone:

```
Single Node
```


Cluster:

```
Master

↓

Slave
```


Sentinel:

```
Sentinel

↓

Master Group
```


---

# 4.5 RedisInfoCollector


Package:

```
collector.info
```


Responsibility:


采集：

INFO SERVER

INFO STATS


指标：

- redis_version
- uptime
- ops/sec


---

# 4.6 RedisMemoryCollector


Package:

```
collector.memory
```


采集：

INFO MEMORY


指标：

- used_memory
- maxmemory
- fragmentation


---

# 4.7 RedisClientCollector


Package:

```
collector.client
```


采集：


CLIENT LIST


指标：

- connected_clients
- blocked_clients


---

# 4.8 RedisSlowLogCollector


Package:

```
collector.slowlog
```


采集：

SLOWLOG GET


指标：

- slow command count
- duration


---

# 4.9 RedisClusterCollector


Package:

```
collector.cluster
```


采集：

CLUSTER INFO

CLUSTER NODES


识别：

- slots
- node role
- fail status


---

# 4.10 RedisReplicationCollector


Package:

```
collector.replication
```


采集：

INFO REPLICATION


指标：

- master/slave
- replication offset
- lag


---

# 4.11 RedisObservationBuilder


Package:

```
builder
```


Responsibility:

转换统一Observation。


---

# 5. Method List（方法清单）


# RedisConnector


```java
init(ConnectorContext context)

初始化Redis环境
```


```java
start()

启动Redis采集
```


```java
stop()

释放Redis资源
```


---

# RedisDiscovery


```java
discover()

返回:

RedisTopology


作用:

发现Redis节点关系
```


---

# RedisInfoCollector


```java
collect()

返回:

RedisMetric


作用:

执行INFO命令采集
```


---

# RedisMemoryCollector


```java
collect()

返回:

MemoryMetric


作用:

采集Redis内存
```


---

# RedisSlowLogCollector


```java
collect()

返回:

SlowLogMetric


作用:

获取慢查询
```


---

# RedisClusterCollector


```java
collect()

返回:

ClusterMetric


作用:

分析Cluster状态
```


---

# RedisObservationBuilder


```java
build()

参数:

RedisMetric


返回:

ObservationData
```


---

# 6. Dependency（依赖关系）


## 6.1 Internal Dependency


```
WP001 Agent

↓

WP002 Connector SDK

↓

WP005 Redis Connector

↓

WP007 Observation Pipeline

↓

WP011 Topology Model
```


---

## 6.2 External Dependency


|Dependency|Purpose|
|-|-|
|Redis Protocol|数据采集|
|Lettuce Client|Redis连接|
|Jackson|配置|
|SLF4J|日志|


---

## 6.3 Dependency Rule


Redis Connector:

禁止：

依赖业务系统。


只能：

```
Connector SDK

↓

Redis Connector

↓

Redis Server
```


---

# 7. Physical File List（物理文件清单）


```
aipe-connector-redis/


src/main/java/com/aipe/connector/redis/RedisConnector.java


src/main/java/com/aipe/connector/redis/client/RedisClientFactory.java


src/main/java/com/aipe/connector/redis/client/RedisConnection.java


src/main/java/com/aipe/connector/redis/discovery/RedisDiscovery.java


src/main/java/com/aipe/connector/redis/collector/info/RedisInfoCollector.java


src/main/java/com/aipe/connector/redis/collector/memory/RedisMemoryCollector.java


src/main/java/com/aipe/connector/redis/collector/client/RedisClientCollector.java


src/main/java/com/aipe/connector/redis/collector/slowlog/RedisSlowLogCollector.java


src/main/java/com/aipe/connector/redis/collector/cluster/RedisClusterCollector.java


src/main/java/com/aipe/connector/redis/collector/replication/RedisReplicationCollector.java


src/main/java/com/aipe/connector/redis/builder/RedisObservationBuilder.java


src/main/resources/application.yml
```


---

# 8. Sequence Diagram（时序图）


## Startup


```
AgentRuntime

↓

ConnectorManager

↓

RedisConnector.init()

↓

RedisClientFactory

↓

RedisDiscovery

↓

Discover Nodes

↓

Register Collectors

↓

RUNNING
```


---

## Collection Flow


```
Scheduler

↓

RedisConnector

↓

RedisDiscovery

↓

RedisInfoCollector

↓

RedisMemoryCollector

↓

RedisSlowLogCollector

↓

RedisClusterCollector

↓

RedisObservationBuilder

↓

ObservationEmitter
```


---

# 9. State Machine（状态机）


RedisConnector:


```
CREATED

↓

INITIALIZED

↓

CONNECTING

↓

CONNECTED

↓

DISCOVERING

↓

RUNNING

↓

STOPPING

↓

STOPPED


异常:

↓

ERROR
```


---

## State Definition


|State|Description|
|-|-|
|CREATED|创建|
|INITIALIZED|初始化|
|CONNECTING|建立连接|
|CONNECTED|连接成功|
|DISCOVERING|拓扑发现|
|RUNNING|采集|
|STOPPING|停止|
|STOPPED|结束|
|ERROR|异常|


---

# 10. Implementation Constraints（实现约束）


# 10.1 Must Implement


必须真实调用：


Redis INFO:

```
INFO
```


Client:

```
CLIENT LIST
```


SlowLog:

```
SLOWLOG GET
```


Cluster:

```
CLUSTER INFO

CLUSTER NODES
```


Replication:

```
INFO REPLICATION
```


---

# 10.2 Forbidden


禁止：


```
Mock Redis数据

模拟Cluster

固定QPS

TODO

空Collector

假节点关系
```


---

# 10.3 Engineering Rules


必须：

- 支持密码认证
- 支持Cluster
- 支持连接超时
- 节点异常隔离
- 采集失败不中断Agent


---

# 11. Test & Verification（测试与验证）


# 11.1 Build


```bash
mvn clean install
```


---

# 11.2 Run


启动：

Redis实例。


配置：

RedisConnector。


启动Agent。


---

# 11.3 Verification Command


验证：


```
redis.ops

redis.memory.used

redis.clients

redis.slowlog

redis.cluster.nodes
```


---

# 11.4 Expected Result


输出：


```
Observation

resourceType:

REDIS_CLUSTER


nodes:

master/slave


metrics:

ops

memory

client
```


---

# 11.5 Troubleshooting


连接失败：

检查：

```
1. Redis地址

2. 密码

3. 网络权限

4. TLS配置

5. Redis版本
```


---

# END

WP005-Redis-Connector v1.0