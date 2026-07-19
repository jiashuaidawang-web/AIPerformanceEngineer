因为企业大促场景：

Redis更多负责缓存。

真正最终瓶颈很多会落在：

SQL慢查询
连接池耗尽
锁等待
Buffer Pool
CPU
IO

WP006 完成后，我们才真正覆盖：

Java
+
Linux
+
Redis
+
MySQL

=
80%的企业性能瓶颈来源

然后才进入：

Observation / Topology / AI Root Cause Engine。


WP006 是 MVP 阶段最后一个核心采集 Connector。

完成：

JVM
Linux
Redis
MySQL

之后，我们已经覆盖企业生产系统性能瓶颈的主要来源：

用户请求

↓

JVM

↓

Linux

↓

Redis

↓

MySQL

这也是为什么 MySQL Connector 必须设计得比普通数据库监控更深入。

传统监控：

看 QPS、连接数、CPU

AI Performance Engineer：

结合压测时间线，判断：

"TPS下降发生在14:03:20，MySQL CPU正常，但是锁等待增长，慢SQL集中在订单查询接口，因此数据库锁竞争是根因"

所以 WP006 不只是采集，而是为后面的：

Topology
Evidence
Root Cause Engine

提供基础数据。

下面严格按照冻结模板。


# AI Performance Engineer
# WP006-MySQL-Connector Blueprint v1.0


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
> EstimatedJavaFiles: 60
>
> EstimatedWorkload: 10 Days
>
> Blueprint Template:
> 01-Blueprint-Template.md v1.0


---

# 1. Goal（目标）


# 1.1 Purpose


WP006实现 MySQL 数据库性能采集能力。


目标：

通过 MySQL Connector：

自动连接数据库。

采集：

- Server状态
- SQL性能
- 连接池状态
- 锁等待
- Buffer Pool
- Index情况
- 慢SQL


生成统一：

Observation。


---

# 1.2 Capability Added


Before:


系统只能感知：

Application

Host

Cache


After:


新增：

Database Domain


完整链路：


```
JMeter Request

↓

Application

↓

JVM

↓

Linux

↓

Redis

↓

MySQL
```


AI可以判断：

```
TPS下降

↓

JVM正常

↓

Redis正常

↓

MySQL lock_wait增长

↓

数据库锁竞争
```


---

# 1.3 Scope


包含：


- MySQL连接管理
- 数据库实例发现
- Server状态采集
- Performance Schema采集
- Slow SQL采集
- Lock采集
- Buffer Pool采集
- Index信息采集


不包含：


- 自动修改SQL
- 自动创建索引
- 自动调整参数


---

# 2. Acceptance Criteria（验收标准）


# 2.1 Functional Acceptance


```
□ 支持MySQL 5.7

□ 支持MySQL 8.x

□ 支持只读账号

□ 自动采集实例信息

□ 采集慢SQL

□ 采集锁等待

□ 采集连接状态

□ 生成Observation

□ Connector生命周期正常
```


---

# 2.2 Technical Acceptance


```
□ 禁止Mock数据

□ 禁止固定SQL结果

□ 使用真实SQL查询

□ 支持连接池

□ 查询超时控制

□ 异常隔离

□ 不影响数据库业务
```


---

# 2.3 Integration Acceptance


完整链路：


```
Agent

↓

Connector SDK

↓

MySQLConnector

↓

MySQLCollector

↓

ObservationEmitter
```


---

# 3. Package List（包结构）


```
com.aipe.connector.mysql


├── MySQLConnector


├── client


├── discovery


├── collector


│
├── server

│
├── connection

│
├── processlist

│
├── slowquery

│
├── lock

│
├── bufferpool

│
├── index

│
├── transaction


├── permission


├── model


├── builder


├── config


└── support

```


---

## Package Responsibility


|Package|Responsibility|
|-|-|
|client|数据库连接|
|discovery|实例发现|
|collector|指标采集|
|permission|权限检查|
|model|数据模型|
|builder|Observation构建|
|support|工具|


---

# 4. Class List（类清单）


---

# 4.1 MySQLConnector


Package:

```
com.aipe.connector.mysql
```


Responsibility:


MySQL Connector 主入口。


负责：

- 初始化连接
- 权限检测
- Collector管理


---

# 4.2 MySQLClientFactory


Package:

```
client
```


Responsibility:


创建数据库连接。


支持：

- JDBC URL
- username
- password
- timeout


---

# 4.3 MySQLConnection


Package:

```
client
```


Responsibility:


数据库连接封装。


---

# 4.4 MySQLPermissionChecker


Package:

```
permission
```


Responsibility:


检查只读账号权限。


验证：


- PROCESS
- SELECT
- performance_schema


---

# 4.5 MySQLInstanceDiscovery


Package:

```
discovery
```


Responsibility:


发现MySQL实例信息。


采集：


- version
- server_id
- hostname


---

# 4.6 ServerStatusCollector


Package:

```
collector.server
```


采集：

SHOW GLOBAL STATUS


指标：


- Threads_connected
- Questions
- QPS
- TPS


---

# 4.7 ConnectionCollector


Package:

```
collector.connection
```


采集连接状态。


来源：

SHOW PROCESSLIST


指标：

- active connection
- waiting connection


---

# 4.8 ProcessListCollector


Package:

```
collector.processlist
```


采集：


```sql
SHOW FULL PROCESSLIST
```


获取：

- SQL
- state
- time


---

# 4.9 SlowQueryCollector


Package:

```
collector.slowquery
```


采集：

慢SQL。


来源：

```
performance_schema
```


获取：

- sql_text
- count
- avg_time
- max_time


---

# 4.10 LockCollector


Package:

```
collector.lock
```


采集：

锁等待。


来源：

```
performance_schema.data_lock_waits
```


获取：

- waiting transaction
- blocking transaction


---

# 4.11 BufferPoolCollector


Package:

```
collector.bufferpool
```


采集：

InnoDB Buffer Pool。


来源：

```
information_schema.innodb_buffer_pool_stats
```


---

# 4.12 IndexCollector


Package:

```
collector.index
```


采集：

索引信息。


来源：

```
information_schema.statistics
```


---

# 4.13 TransactionCollector


Package:

```
collector.transaction
```


采集：

事务状态。


---

# 4.14 MySQLObservationBuilder


Package:

```
builder
```


Responsibility:


转换Observation。


---

# 5. Method List（方法清单）


# MySQLConnector


```java
init(ConnectorContext context)

初始化MySQL环境
```


```java
start()

启动采集任务
```


```java
stop()

释放数据库资源
```


---

# MySQLPermissionChecker


```java
check()

返回:

PermissionResult


作用:

检查账号权限
```


---

# MySQLInstanceDiscovery


```java
discover()

返回:

MySQLInstance


作用:

发现数据库实例
```


---

# ServerStatusCollector


```java
collect()

返回:

ServerMetric


执行:

SHOW GLOBAL STATUS
```


---

# ProcessListCollector


```java
collect()

返回:

ProcessMetric


执行:

SHOW FULL PROCESSLIST
```


---

# SlowQueryCollector


```java
collect()

返回:

SlowQueryMetric


读取:

performance_schema
```


---

# LockCollector


```java
collect()

返回:

LockMetric


读取:

performance_schema
```


---

# BufferPoolCollector


```java
collect()

返回:

BufferPoolMetric
```


---

# IndexCollector


```java
collect()

返回:

IndexMetric
```


---

# MySQLObservationBuilder


```java
build()

参数:

MySQLMetric


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

WP006 MySQL Connector

↓

WP007 Observation Pipeline

↓

WP018 AI Root Cause
```


---

## 6.2 External Dependency


|Dependency|Purpose|
|-|-|
|JDBC Driver|Database connection|
|MySQL Protocol|Data access|
|SLF4J|Logging|
|HikariCP|Connection pool|


---

## 6.3 Dependency Rule


MySQL Connector：

禁止：

修改数据库。


只能：

读取。


---

# 7. Physical File List（物理文件清单）


```
aipe-connector-mysql/


src/main/java/com/aipe/connector/mysql/MySQLConnector.java


src/main/java/com/aipe/connector/mysql/client/MySQLClientFactory.java


src/main/java/com/aipe/connector/mysql/client/MySQLConnection.java


src/main/java/com/aipe/connector/mysql/permission/MySQLPermissionChecker.java


src/main/java/com/aipe/connector/mysql/discovery/MySQLInstanceDiscovery.java


src/main/java/com/aipe/connector/mysql/collector/server/ServerStatusCollector.java


src/main/java/com/aipe/connector/mysql/collector/connection/ConnectionCollector.java


src/main/java/com/aipe/connector/mysql/collector/processlist/ProcessListCollector.java


src/main/java/com/aipe/connector/mysql/collector/slowquery/SlowQueryCollector.java


src/main/java/com/aipe/connector/mysql/collector/lock/LockCollector.java


src/main/java/com/aipe/connector/mysql/collector/bufferpool/BufferPoolCollector.java


src/main/java/com/aipe/connector/mysql/collector/index/IndexCollector.java


src/main/java/com/aipe/connector/mysql/collector/transaction/TransactionCollector.java


src/main/java/com/aipe/connector/mysql/builder/MySQLObservationBuilder.java


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

MySQLConnector.init()

↓

MySQLClientFactory

↓

PermissionChecker

↓

InstanceDiscovery

↓

RegisterCollectors

↓

RUNNING
```


---

## Collection Flow


```
Scheduler

↓

MySQLConnector

↓

ServerStatusCollector

↓

ProcessListCollector

↓

SlowQueryCollector

↓

LockCollector

↓

BufferPoolCollector

↓

MySQLObservationBuilder

↓

ObservationEmitter
```


---

# 9. State Machine（状态机）


MySQLConnector:


```
CREATED

↓

INITIALIZED

↓

CONNECTING

↓

CONNECTED

↓

CHECKING_PERMISSION

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

# 10. Implementation Constraints（实现约束）


# 10.1 Must Implement


必须真实执行：


## Process


```sql
SHOW FULL PROCESSLIST
```


## Status


```sql
SHOW GLOBAL STATUS
```


## Index


```sql
information_schema.statistics
```


## Lock


```sql
performance_schema.data_lock_waits
```


## Slow SQL


```sql
performance_schema.events_statements_summary_by_digest
```


## Buffer Pool


```sql
information_schema.innodb_buffer_pool_stats
```


---

# 10.2 Forbidden


禁止：


```
Mock SQL结果

模拟慢SQL

固定QPS

修改数据库

写入业务表

TODO
```


---

# 10.3 Engineering Rules


必须：

- 只读账号
- 查询超时
- SQL白名单
- 连接释放
- 大表查询限制
- 采集频率控制


---

# 11. Test & Verification（测试与验证）


# 11.1 Build


```bash
mvn clean install
```


---

# 11.2 Run


准备：

MySQL实例。


创建：

只读账号。


启动：

Agent。


---

# 11.3 Verification Command


检查：

```
mysql.qps

mysql.connections

mysql.slow_sql

mysql.lock_wait

mysql.buffer_pool
```


---

# 11.4 Expected Result


生成：


```
Observation

resourceType:

MYSQL_INSTANCE


metrics:

qps

connection

slow_query

lock
```


---

# 11.5 Troubleshooting


连接失败：

检查：


```
1. JDBC地址

2. 用户权限

3. performance_schema

4. 网络策略

5. 查询权限
```


---

# END

WP006-MySQL-Connector v1.0