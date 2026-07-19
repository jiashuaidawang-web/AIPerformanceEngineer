11-WP010-Scenario-Manager.md
# AI Performance Engineer
# WP010-Scenario-Manager Blueprint v1.0


Document Type:
Work Package Blueprint


Version:
v1.0


Status:
Frozen


Milestone:
M1 - Agent MVP


Priority:
P0



DependsOn:

- WP007 Observation Pipeline
- WP008 Storage Layer
- WP009 Configuration & Deployment Manager



RequiredBy:

- WP011 Resource Model
- WP012 Topology Model
- WP013 Evidence Engine
- WP014 Performance Analysis Engine
- WP018 Root Cause Engine
- WP020 JMeter Integration
- WP030 Knowledge System



EstimatedJavaFiles:

60



EstimatedWorkload:

10 Days



Blueprint Template:

01-Blueprint-Template.md v1.0



---

# 1. Goal（目标）


## 1.1 Purpose


建立 AI Performance Engineer 的性能测试场景管理能力。


Scenario Manager 负责定义：

一次完整性能验证任务的上下文。


包括：

- 测试目标
- 压测入口
- 压测脚本
- 流量模型
- 目标TPS
- 参与资源
- 采集范围
- 分析范围



核心解决问题：


用户告诉系统：

> 我要模拟大促前订单系统压力。


系统需要知道：


什么业务

↓

什么接口

↓

多少流量

↓

涉及哪些服务

↓

采集哪些指标

↓

如何判断成功




---

# 1.2 Capability Added


Before:



Agent

↓

采集系统指标

↓

保存Observation




After:



Scenario

↓

Load Test

↓

Observation Timeline

↓

AI Analysis

↓

Performance Report




---

# 1.3 Core Principle


Scenario 是 AI Performance Engineer 的：

"性能分析上下文"


所有Observation必须能够关联Scenario。



例如：



Scenario:

618-order-payment-test

Time:

14:00-14:30

Traffic:

500 TPS

Observation:

mysql.lock.wait

jvm.gc.time

redis.hit.rate




AI分析时：

不是分析全部历史数据。


而是：

分析当前Scenario产生的数据。



---

# 1.4 Scope


MVP支持：


- 手工创建Scenario
- JMeter任务绑定
- 压测时间窗口管理
- 指标采集范围定义
- 目标指标定义
- Scenario生命周期管理



不包含：


- 自动生成压测脚本
- 自动扩容
- 自动调流量



---

# 2. Acceptance Criteria（验收标准）


## 2.1 Functional Acceptance


必须支持：



□ 创建Scenario

□ 修改Scenario

□ 删除Scenario

□ 启动Scenario

□ 停止Scenario

□ 关联JMeter任务

□ 定义目标TPS

□ 定义成功标准

□ 关联Observation

□ 查询Scenario历史结果




---

## 2.2 Technical Acceptance


必须满足：



□ Scenario唯一ID

□ Scenario状态管理

□ 时间窗口准确

□ 多Agent关联

□ 多服务关联

□ 多次执行支持

□ 历史回放支持




---

## 2.3 Integration Acceptance


完整链路：



User

↓

Scenario Manager

↓

JMeter Executor

↓

Agent Fleet

↓

Observation Pipeline

↓

Storage

↓

AI Analysis Engine




---

# 3. Package List（包结构）



com.aipe.scenario

├── controller

├── model

├── service

├── executor

├── scheduler

├── binding

├── timeline

├── metric

├── validation

├── repository

└── support




---

## Package Responsibility


|Package|职责|
|-|-|
|controller|API入口|
|model|领域对象|
|service|业务逻辑|
|executor|执行控制|
|scheduler|任务调度|
|binding|资源绑定|
|timeline|时间管理|
|metric|目标指标|
|validation|校验|
|repository|数据访问|



---

# 4. Class List（类清单）


# 4.1 Scenario


Package:


model



职责：

性能测试场景核心对象。



字段：



scenarioId

name

description

environment

status

startTime

endTime

targetTPS

creator

createdTime




---

# 4.2 ScenarioExecution


Package:


model



职责：

一次Scenario执行实例。



字段：



executionId

scenarioId

startTime

endTime

status

result




---

# 4.3 ScenarioManager


Package:


service



职责：

Scenario生命周期管理。



负责：

- 创建
- 更新
- 启动
- 停止



---

# 4.4 ScenarioExecutor


Package:


executor



职责：

执行性能测试任务。



调用：

JMeter执行器。



---

# 4.5 ScenarioScheduler


Package:


scheduler



职责：

定时执行Scenario。



---

# 4.6 ScenarioResourceBinding


Package:


binding



职责：

绑定参与压测资源。



例如：



order-service

mysql-cluster

redis-cluster




---

# 4.7 ScenarioMetricProfile


Package:


metric



职责：

定义关注指标。



例如：



TPS

RT

CPU

GC

DB Lock

Redis QPS




---

# 4.8 ScenarioTimeline


Package:


timeline



职责：

管理Scenario时间线。



包含：



Prepare

Warmup

Running

Cooldown

Finished




---

# 4.9 ScenarioValidator


Package:


validation



职责：

Scenario合法性检查。



---

# 4.10 ScenarioRepository


Package:


repository



职责：

数据库访问。



---

# 5. Method List（方法清单）


# ScenarioManager


```java
createScenario(Scenario scenario)

创建性能场景
updateScenario(Scenario scenario)

更新场景
startScenario(String scenarioId)

启动场景
stopScenario(String scenarioId)

停止场景
getScenario(String scenarioId)

查询场景
ScenarioExecutor
execute(
ScenarioExecution execution
)

执行压测
stopExecution(
String executionId
)

停止执行
ScenarioResourceBinding
bindResource(
scenarioId,
resourceId
)

绑定资源
unbindResource(
scenarioId,
resourceId
)

解绑资源
ScenarioMetricProfile
addMetric(
scenarioId,
metric
)

增加关注指标
getMetrics(
scenarioId
)

获取指标集合
ScenarioTimeline
startPhase(
phase
)

开始阶段
finishPhase(
phase
)

结束阶段
ScenarioValidator
validate(
Scenario scenario
)

校验场景
6. Dependency（依赖关系）
6.1 Internal Dependency
WP009 Configuration Manager


        ↓


WP010 Scenario Manager


        ↓


WP007 Observation Pipeline


        ↓


WP008 Storage Layer


        ↓


WP014 Performance Analysis Engine

6.2 External Dependency
依赖	用途
JMeter	压测执行
MySQL	Scenario存储
Redis	任务状态缓存
Scheduler	任务调度
6.3 Dependency Rule

Scenario：

不能直接采集数据。

只能：

Scenario

↓

Observation Timeline

↓

Analysis Engine

7. Physical File List（物理文件清单）

必须创建：

aipe-scenario/


src/main/java/com/aipe/scenario/model/Scenario.java


src/main/java/com/aipe/scenario/model/ScenarioExecution.java


src/main/java/com/aipe/scenario/service/ScenarioManager.java


src/main/java/com/aipe/scenario/executor/ScenarioExecutor.java


src/main/java/com/aipe/scenario/scheduler/ScenarioScheduler.java


src/main/java/com/aipe/scenario/binding/ScenarioResourceBinding.java


src/main/java/com/aipe/scenario/metric/ScenarioMetricProfile.java


src/main/java/com/aipe/scenario/timeline/ScenarioTimeline.java


src/main/java/com/aipe/scenario/validation/ScenarioValidator.java


src/main/java/com/aipe/scenario/repository/ScenarioRepository.java


src/main/resources/db/migration/V10__scenario.sql


src/main/resources/application.yml

8. Sequence Diagram（时序图）
Scenario启动流程
User


↓

ScenarioController


↓

ScenarioManager


↓

ScenarioValidator


↓

ScenarioExecutor


↓

JMeter


↓

Agent Runtime


↓

Observation Pipeline


↓

Storage

Observation关联流程
Connector


↓

Observation


↓

ObservationProcessor


↓

ScenarioTimeline


↓

ScenarioExecution


↓

AI Analysis

9. State Machine（状态机）
Scenario生命周期
CREATED


↓

READY


↓

STARTING


↓

RUNNING


↓

STOPPING


↓

FINISHED



异常:

FAILED

ScenarioExecution生命周期
INIT


↓

PREPARING


↓

RUNNING


↓

ANALYZING


↓

COMPLETED



异常:

FAILED

10. Implementation Constraints（实现约束）
10.1 Must Implement

必须实现：

Scenario状态：

CREATED

READY

RUNNING

FINISHED

FAILED

必须支持：

Scenario

绑定:

Application

Resource

Agent

MetricProfile


必须保存：

Scenario开始时间

Scenario结束时间

Observation时间范围

10.2 Forbidden

禁止：

没有Scenario直接压测

Observation无法关联测试任务

没有时间窗口

没有目标TPS

测试结果无法复现

10.3 Engineering Rules

必须：

场景唯一化
执行实例化
时间线管理
数据可回放
支持多次执行
11. Test & Verification（测试与验证）
11.1 Build
mvn clean install
11.2 Test Scenario

创建：

Scenario:

order-system-large-promotion


Target TPS:

500


Duration:

30min


绑定：

order-service

mysql-prod

redis-prod

11.3 Verification

检查：

Scenario状态

Execution状态

JMeter任务状态

Observation关联

11.4 Expected Result

返回：

{
 "scenario":"order-system-large-promotion",
 "status":"FINISHED",
 "targetTPS":500,
 "actualTPS":520,
 "observationCount":120000
}
11.5 Troubleshooting

Scenario无数据：

检查：

1. Scenario是否RUNNING

2. Agent是否在线

3. 时间窗口是否匹配

4. Observation是否绑定Scenario

5. JMeter是否执行成功

END

WP010-Scenario-Manager v1.0


---

说明一下当前路线：

到 WP010 完成后，MVP 已经具备：


压测任务
|
Scenario
|
Agent采集
|
Observation
|
Storage


下一步 **WP011 Resource Model** 非常关键，因为它会解决你之前一直强调的问题：

> 不看机器维度，而看业务域、应用、集群、中间件域。


会正式定义：


Host

Application

Service

Instance

Cluster

Database

Middleware

Resource Relationship


这一步完成后，AI 才能知道：

"这个 MySQL Master 属于订单系统，不属于支付系统"

也是后续自动拓扑发现的基础。