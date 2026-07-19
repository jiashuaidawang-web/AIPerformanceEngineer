里面不要写代码。

写：

Test Case

例如：

TC001
启动Backend

Expected：

8081

Started

PASS / FAIL

TC002
启动Agent

Expected：

Agent注册成功

PASS / FAIL

TC003
JVM Connector

Expected：

HeapUsed > 0

PASS / FAIL

TC004
Redis Connector

Expected：

INFO返回成功

PASS / FAIL

TC005
MySQL Connector

Expected：

ProcessList获取成功

PASS / FAIL

TC006
Observation

Expected：

ClickHouse新增记录

PASS / FAIL

TC007
Scenario

Expected：

Observation关联Scenario

PASS / FAIL


----------------------
M1 验收目标

验证 WP001~WP010 已经形成一个完整、稳定、可扩展的 MVP。

验收流程：

工程验证
│
▼
部署验证
│
▼
Agent验证
│
▼
Connector验证
│
▼
Observation验证
│
▼
Storage验证
│
▼
Scenario验证
│
▼
端到端验证
│
▼
全部PASS
第一部分：Project Build（工程）
M1-001 Maven
目标

整个工程可编译

需要完成代码：

✔ parent pom

✔ dependencyManagement

✔ modules

✔ plugin

✔ java8

✔ spring boot 2.7.18

执行：

mvn clean install

PASS

BUILD SUCCESS
M1-002 IDE

检查：

所有Module无红线

没有Compile Error

没有Duplicate Bean

没有Missing Dependency

PASS

IDEA Compile Success
第二部分：Backend
M1-003 Backend启动

执行：

java -jar backend.jar

PASS

8081启动成功

Spring Started

Application Ready
M1-004 Config Manager

执行：

java -jar config-manager.jar

PASS

8080启动

配置中心初始化成功
第三部分：Agent
M1-005 Agent启动

必须完成代码：

AgentApplication

AgentBootstrap

LifecycleManager

HeartbeatManager

ConnectorManager

执行：

java -jar agent.jar

PASS

Agent Started
M1-006 Agent注册

必须实现：

POST

/api/agent/register

PASS

Backend看到：

Agent

ONLINE

Version

Host

IP

M1-007 Heartbeat

必须完成：

HeartbeatScheduler

HeartbeatClient

PASS

30秒一次

Backend收到Heartbeat
第四部分：Connector
M1-008 JVM Connector

必须采集：

Heap

NonHeap

GC

Thread

Class

CPU


PASS

Observation数量：

>10
M1-009 Linux Connector

必须实现：

CpuCollector

MemoryCollector

DiskCollector

NetworkCollector

LoadCollector

PASS：

真实读取：

/proc/stat

/proc/meminfo

/proc/loadavg

M1-010 Redis Connector

必须实现：

INFO

Memory

Clients

Stats

Replication

PASS

真正连接Redis。

M1-011 MySQL Connector

必须实现：

SHOW PROCESSLIST

SHOW GLOBAL STATUS

SHOW ENGINE INNODB STATUS

SHOW VARIABLES

information_schema

PASS

真正读取数据库。

第五部分：Observation Pipeline
M1-012 Observation创建

必须实现：

ObservationBuilder

ObservationFactory

PASS

Observation

resourceId

metric

value

timestamp

生成成功。

M1-013 Observation发送

必须完成：

ObservationSender

Queue

Retry

PASS

Backend收到Observation。

M1-014 Observation存储

PASS

MySQL：

Agent

Scenario

ClickHouse：

Observation

查询：

select *

limit 10

必须有数据。

第六部分：Scenario
M1-015 创建Scenario

API：

POST

/scenario

PASS

数据库：

Scenario新增
M1-016 启动Scenario

PASS

Scenario

RUNNING
M1-017 Observation绑定Scenario

PASS

Observation：

scenarioId

非空
第七部分：Configuration
M1-018 Connector配置

修改：

connector.yml

关闭：

Redis Connector

PASS

Redis停止采集。

无需重启Agent。

M1-019 动态配置

修改：

采集周期

5秒

↓

2秒

PASS

立即生效。

第八部分：Storage
M1-020 MySQL

检查：

agent

connector

scenario

heartbeat

PASS

全部存在。

M1-021 ClickHouse

检查：

observation

PASS

持续新增。

第九部分：End To End
M1-022 完整采集链路
Agent

↓

Connector

↓

Observation

↓

Storage

PASS

整条链路打通。

M1-023 JMeter

真正执行：

100 TPS

5分钟

PASS

Observation持续增加。

M1-024 Dashboard

即使没有UI。

也要：

SQL

查询

最新Observation

PASS

第十部分：稳定性
M1-025 Connector异常恢复

人为：

关闭Redis。

PASS

Connector ERROR

↓

Retry

↓

Recovery
M1-026 Agent异常恢复

重启Agent。

PASS

自动重新注册。

M1-027 Backend异常恢复

Backend重启。

PASS

Agent自动重新连接。

第十一部分：代码质量
M1-028 TODO检查

必须：

grep TODO

PASS

0
M1-029 Mock检查

PASS

Mock

Fake

Dummy

全部禁止
M1-030 JavaDoc

PASS

所有Public Class：

必须：

JavaDoc
第十二部分：最终验收

只有：

M1-001

↓

M1-030

全部：

PASS

才允许：

开始：

WP011 Resource Model