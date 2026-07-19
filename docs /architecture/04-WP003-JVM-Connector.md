这个会是第一个真实采集实现，重点会包含：

JVM Connector 主控
MemoryCollector
GC Collector
Thread Collector
CPU Collector
ClassLoader Collector
JMX API约束
Observation生成

并且严格禁止：

Mock
TODO
固定值

会开始进入真正代码落地阶段。

它验证整个架构是否成立：

Agent Runtime

↓

Connector SDK

↓

JVM Connector

↓

JDK/JMX API

↓

Observation

↓

AI Performance Engineer Data Pipeline

如果 WP003 成功，后续：

Linux Connector
Redis Connector
MySQL Connector

都是同一种模式扩展。

严格按照：

01-Blueprint-Template.md v1.0

11章节输出。


# AI Performance Engineer
# WP003-JVM-Connector Blueprint v1.0


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
>
> RequiredBy:
> - WP007 Observation Pipeline
> - WP013 Evidence Engine
> - WP018 Root Cause Engine
>
> EstimatedJavaFiles: 35
>
> EstimatedWorkload: 5 Days
>
> Blueprint Template:
> 01-Blueprint-Template.md v1.0


---

# 1. Goal（目标）


# 1.1 Purpose


WP003实现第一个真实生产级 Connector。

目标：

通过 JVM Connector 自动采集 Java 应用运行状态。


采集来源：

JDK标准管理接口：

- MemoryMXBean
- ThreadMXBean
- GarbageCollectorMXBean
- ClassLoadingMXBean
- RuntimeMXBean
- OperatingSystemMXBean


生成统一：

Observation。


---

# 1.2 Capability Added


Before:


Agent只能运行Connector。


After:


Agent具备：

Java应用性能观测能力。


可以获取：

- Heap使用情况
- NonHeap使用情况
- GC情况
- Thread情况
- CPU情况
- ClassLoader情况
- JVM运行信息


---

# 1.3 Scope


包含：


- JVM Connector
- JVM Collector Framework
- JVM Metric Collector
- JMX数据采集
- Observation生成


不包含：


- JVM故障诊断
- GC调优建议
- AI分析


---

# 2. Acceptance Criteria（验收标准）


# 2.1 Functional Acceptance


```
□ JVM Connector可以被Agent加载

□ Connector生命周期正常

□ 每固定周期采集一次

□ 真实调用JDK/JMX API

□ 生成Observation

□ Observation成功发送

□ 支持停止释放资源
```


---

# 2.2 Technical Acceptance


```
□ 禁止Mock数据

□ 禁止固定返回值

□ 无TODO

□ 无空实现

□ 线程安全

□ 定时任务可关闭
```


---

# 2.3 Integration Acceptance


完整链路：


```
Agent

↓

ConnectorManager

↓

JvmConnector

↓

Collector

↓

ObservationEmitter
```


必须运行成功。


---

# 3. Package List（包结构）


```
com.aipe.connector.jvm


├── JvmConnector

│
├── collector

│   ├── memory

│   ├── gc

│   ├── thread

│   ├── cpu

│   ├── classloader

│   └── runtime


├── config


├── model


├── builder


└── support

```


---

## Package Responsibility


|Package|Responsibility|
|-|-|
|collector|指标采集|
|builder|Observation构建|
|config|JVM配置|
|model|JVM数据模型|
|support|工具|


---

# 4. Class List（类清单）


---

# 4.1 JvmConnector


Package:

```
com.aipe.connector.jvm
```


Type:

Connector实现类


Responsibility:


JVM Connector总控。


负责：

- 初始化Collector
- 定时采集
- 发送Observation


Dependencies:

Connector SDK


---

# 4.2 JvmCollector


Package:

```
collector
```


Type:

Interface


Responsibility:

所有JVM采集器标准接口。


---

# 4.3 MemoryCollector


Package:

```
collector.memory
```


Responsibility:

采集JVM内存。


真实API:

```java
ManagementFactory.getMemoryMXBean()
```


采集：

- Heap
- NonHeap


---

# 4.4 GcCollector


Package:

```
collector.gc
```


Responsibility:

采集GC。


真实API:


```java
ManagementFactory.getGarbageCollectorMXBeans()
```


采集：

- collectionCount
- collectionTime


---

# 4.5 ThreadCollector


Package:

```
collector.thread
```


Responsibility:

线程指标。


真实API:


```java
ManagementFactory.getThreadMXBean()
```


采集：

- active thread
- peak thread
- daemon thread


---

# 4.6 CpuCollector


Package:

```
collector.cpu
```


Responsibility:

CPU指标。


真实API:


```java
OperatingSystemMXBean
```


采集：

- cpu load
- process cpu


---

# 4.7 ClassLoaderCollector


Package:

```
collector.classloader
```


Responsibility:


Class加载情况。


API:


```java
ClassLoadingMXBean
```


---

# 4.8 RuntimeCollector


Package:

```
collector.runtime
```


Responsibility:


JVM运行信息。


API:


```java
RuntimeMXBean
```


---

# 4.9 JvmObservationBuilder


Package:

```
builder
```


Responsibility:


将采集结果转换Observation。


---

# 4.10 JvmCollectorManager


Package:

```
collector
```


Responsibility:


管理所有Collector。


---

# 5. Method List（方法清单）


# JvmConnector


```java
init(ConnectorContext context)

作用:

初始化JVM采集环境。
```


```java
start()

作用:

启动定时采集任务。
```


```java
stop()

作用:

关闭采集线程。
```


---

# MemoryCollector


```java
collect()

返回:

JvmMetric

作用:

通过MemoryMXBean采集Heap数据。
```


---

# GcCollector


```java
collect()

返回:

JvmMetric

作用:

通过GarbageCollectorMXBean采集GC数据。
```


---

# ThreadCollector


```java
collect()

返回:

JvmMetric

作用:

采集线程指标。
```


---

# CpuCollector


```java
collect()

返回:

JvmMetric

作用:

采集CPU指标。
```


---

# JvmObservationBuilder


```java
build()

参数:

JvmMetric


返回:

ObservationData


作用:

转换统一数据结构。
```


---

# 6. Dependency（依赖关系）


## 6.1 Internal Dependency


```
WP001 Agent

↓

WP002 Connector SDK

↓

WP003 JVM Connector

↓

WP007 Observation Pipeline
```


---

## 6.2 External Dependency


|Dependency|Purpose|
|-|-|
|JDK Management API|JVM数据|
|SLF4J|日志|
|Jackson|配置|


---

## 6.3 Dependency Rule


JVM Connector：

只能依赖：

Connector SDK。


禁止：

直接访问：

Agent内部实现。


---

# 7. Physical File List（物理文件清单）


必须创建：


```
aipe-connector-jvm/


src/main/java/com/aipe/connector/jvm/JvmConnector.java


src/main/java/com/aipe/connector/jvm/collector/JvmCollector.java


src/main/java/com/aipe/connector/jvm/collector/JvmCollectorManager.java


src/main/java/com/aipe/connector/jvm/collector/memory/MemoryCollector.java


src/main/java/com/aipe/connector/jvm/collector/gc/GcCollector.java


src/main/java/com/aipe/connector/jvm/collector/thread/ThreadCollector.java


src/main/java/com/aipe/connector/jvm/collector/cpu/CpuCollector.java


src/main/java/com/aipe/connector/jvm/collector/classloader/ClassLoaderCollector.java


src/main/java/com/aipe/connector/jvm/collector/runtime/RuntimeCollector.java


src/main/java/com/aipe/connector/jvm/builder/JvmObservationBuilder.java


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

JvmConnector.init()

↓

JvmCollectorManager

↓

Register Collectors

↓

JvmConnector.start()

↓

RUNNING
```


---

## Collection


```
Scheduler

↓

JvmConnector

↓

JvmCollectorManager

↓

MemoryCollector

↓

GC Collector

↓

ThreadCollector

↓

JvmObservationBuilder

↓

ObservationEmitter

↓

Backend
```


---

# 9. State Machine（状态机）


JvmConnector生命周期：


```
CREATED

↓

INITIALIZED

↓

STARTING

↓

RUNNING

↓

COLLECTING

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
|CREATED|实例创建|
|INITIALIZED|初始化完成|
|STARTING|启动|
|RUNNING|等待采集|
|COLLECTING|采集中|
|STOPPING|停止|
|STOPPED|完成|
|ERROR|异常|


---

# 10. Implementation Constraints（实现约束）


# 10.1 Must Implement


必须真实调用：


## Memory


```java
ManagementFactory
.getMemoryMXBean()
```


## GC


```java
ManagementFactory
.getGarbageCollectorMXBeans()
```


## Thread


```java
ManagementFactory
.getThreadMXBean()
```


## CPU


```java
ManagementFactory
.getOperatingSystemMXBean()
```


## ClassLoader


```java
ManagementFactory
.getClassLoadingMXBean()
```


## Runtime


```java
ManagementFactory
.getRuntimeMXBean()
```


---

# 10.2 Forbidden


禁止：

```
Mock JVM数据

随机数字

固定CPU值

固定内存值

TODO

空Collector

return null
```


---

# 10.3 Engineering Rules


必须：

- ScheduledExecutorService
- 正确关闭线程池
- Collector异常隔离
- 日志记录
- 指标带timestamp
- 指标带resource信息


---

# 11. Test & Verification（测试与验证）


# 11.1 Build


```bash
mvn clean install
```


---

# 11.2 Run


启动：

Java Demo应用。


启动：

Agent。


---

# 11.3 Verification Command


查看日志：


```
jvm.heap.used

jvm.gc.time

jvm.thread.count

jvm.cpu.load
```


---

# 11.4 Expected Result


每5秒产生：


```
Observation

resource:

JAVA_APPLICATION


metrics:

heap.used

gc.count

thread.count
```


---

# 11.5 Troubleshooting


无数据：


检查：


```
1. Connector是否RUNNING

2. JMX API是否可用

3. Scheduler是否启动

4. ObservationEmitter是否正常
```


---

# END

WP003-JVM-Connector v1.0