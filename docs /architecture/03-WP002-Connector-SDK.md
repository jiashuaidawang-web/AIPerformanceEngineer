WP002 是整个产品非常关键的基础设施。

这里必须强调：

Connector SDK 不是某一个采集器，而是整个 AI Performance Engineer 的插件生态标准。

后续：

JVM Connector
Linux Connector
Redis Connector
MySQL Connector
Kafka Connector
Nginx Connector
Tomcat Connector
Kubernetes Connector

全部必须实现这个 SDK。

因此 WP002 的设计目标不是“写一个接口”，而是定义：

Connector生命周期
Connector能力模型
Connector配置模型
Connector运行上下文
Connector数据输出标准
Connector扩展机制

下面严格按照冻结的 11 章节模板输出。
# AI Performance Engineer
# WP002-Connector-SDK Blueprint v1.0

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
>
> RequiredBy:
> - WP003 JVM Connector
> - WP004 Linux Connector
> - WP005 Redis Connector
> - WP006 MySQL Connector
> - Future Connectors
>
> EstimatedJavaFiles: 35
>
> EstimatedWorkload: 3 Days
>
> Blueprint Template:
> 01-Blueprint-Template.md v1.0


---

# 1. Goal（目标）


# 1.1 Purpose


WP002定义 AI Performance Engineer 的 Connector 标准。


解决：

不同技术栈：

- JVM
- Redis
- MySQL
- Linux
- MQ
- Kubernetes

如何统一接入 Agent。


Connector SDK 提供：

统一生命周期。

统一配置。

统一数据模型。

统一错误处理。

统一扩展方式。


---

# 1.2 Capability Added


Before:


Agent不知道如何加载外部采集能力。


After:


系统支持：

```
Agent

↓

Connector SDK

↓

任意Connector插件
```


实现：

- 插拔式扩展
- 动态加载
- 生命周期管理
- 标准Observation输出


---

# 1.3 Scope


包含：


- Connector接口规范
- Connector生命周期
- Connector上下文
- Connector配置
- Connector状态管理
- Connector异常体系
- Connector注册机制


不包含：


- JVM指标采集
- Redis采集
- MySQL采集


---

# 2. Acceptance Criteria（验收标准）


## 2.1 Functional Acceptance


```
□ 定义Connector标准接口

□ 支持init

□ 支持start

□ 支持stop

□ 支持destroy

□ 支持状态管理

□ 支持配置传入

□ 支持Observation输出

□ 支持异常上报
```


---

## 2.2 Technical Acceptance


```
□ Connector之间完全隔离

□ 一个Connector失败不会影响其他Connector

□ 生命周期可控

□ 支持线程安全

□ 支持未来插件扩展
```


---

## 2.3 Integration Acceptance


必须支持：

```
Agent

↓

ConnectorManager

↓

Connector

↓

ObservationSender
```


---

# 3. Package List（包结构）


```
com.aipe.connector.sdk


├── api

│
├── lifecycle

│
├── context

│
├── config

│
├── model

│
├── exception

│
├── registry

│
├── loader

│
└── support

```


---

## Package Responsibility


|Package|Responsibility|
|-|-|
|api|核心接口|
|lifecycle|生命周期|
|context|运行上下文|
|config|配置|
|model|数据模型|
|exception|异常定义|
|registry|注册|
|loader|加载|
|support|工具类|


---

# 4. Class List（类清单）


---

# 4.1 Connector


Package:

```
com.aipe.connector.sdk.api
```


Type:

Interface


Responsibility:

所有Connector必须实现的标准接口。


Dependencies:

None


Input:

ConnectorContext


Output:

ObservationData


---

# 4.2 AbstractConnector


Package:

```
com.aipe.connector.sdk.api
```


Type:

Abstract Class


Responsibility:

提供Connector基础能力。


实现：

- 状态管理
- 日志
- 异常处理


---

# 4.3 ConnectorContext


Package:

```
com.aipe.connector.sdk.context
```


Responsibility:


Connector运行上下文。


包含：


- agentId
- serverId
- config
- observationSender


---

# 4.4 ConnectorConfig


Package:

```
com.aipe.connector.sdk.config
```


Responsibility:


Connector配置。


包含：

```
connectorId

type

enabled

interval

properties
```


---

# 4.5 ConnectorMetadata


Package:

```
com.aipe.connector.sdk.model
```


Responsibility:


描述Connector能力。


包含：

```
name

version

author

supportedResource
```


---

# 4.6 ConnectorState


Package:

```
com.aipe.connector.sdk.lifecycle
```


Type:

Enum


状态：


```
CREATED

INITIALIZED

STARTING

RUNNING

STOPPING

STOPPED

ERROR
```


---

# 4.7 ConnectorLifecycle


Package:

```
com.aipe.connector.sdk.lifecycle
```


Responsibility:

生命周期控制。


---

# 4.8 ObservationEmitter


Package:

```
com.aipe.connector.sdk.context
```


Responsibility:


Connector数据发送。


---

# 4.9 ConnectorException


Package:

```
com.aipe.connector.sdk.exception
```


Responsibility:


统一异常。


---

# 4.10 ConnectorRegistry


Package:

```
com.aipe.connector.sdk.registry
```


Responsibility:


Connector注册。


---

# 4.11 ConnectorLoader


Package:

```
com.aipe.connector.sdk.loader
```


Responsibility:


Connector加载。


---

# 5. Method List（方法清单）


# Connector


```java
init(ConnectorContext context)

参数:

ConnectorContext


作用:

初始化Connector


```


```java
start()

作用:

启动Connector
```


```java
stop()

作用:

停止Connector
```


```java
destroy()

作用:

销毁资源
```


```java
metadata()

返回:

ConnectorMetadata
```


---

# AbstractConnector


```java
changeState()

作用:

修改Connector状态
```


```java
getState()

返回:

ConnectorState
```


---

# ConnectorContext


```java
getAgentId()

返回:

String
```


```java
emit()

参数:

ObservationData


作用:

发送采集数据
```


---

# ConnectorRegistry


```java
register()

参数:

Connector


作用:

注册Connector
```


```java
get()

参数:

connectorId


返回:

Connector
```


---

# ConnectorLoader


```java
load()

参数:

ConnectorConfig


返回:

Connector
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

WP004 Linux Connector

WP005 Redis Connector

WP006 MySQL Connector
```


---

## 6.2 External Dependency


|Dependency|Purpose|
|-|-|
|Java SPI|Plugin Loading|
|Jackson|Configuration|
|SLF4J|Logging|


---

## 6.3 Dependency Rule


SDK：

不能依赖：

任何具体Connector。


错误：

```
Connector SDK

↓

JVM Connector
```


正确：

```
JVM Connector

↓

Connector SDK
```


---

# 7. Physical File List（物理文件清单）


必须创建：


```
aipe-connector-sdk/


src/main/java/com/aipe/connector/sdk/api/Connector.java


src/main/java/com/aipe/connector/sdk/api/AbstractConnector.java


src/main/java/com/aipe/connector/sdk/context/ConnectorContext.java


src/main/java/com/aipe/connector/sdk/context/ObservationEmitter.java


src/main/java/com/aipe/connector/sdk/config/ConnectorConfig.java


src/main/java/com/aipe/connector/sdk/model/ConnectorMetadata.java


src/main/java/com/aipe/connector/sdk/lifecycle/ConnectorState.java


src/main/java/com/aipe/connector/sdk/lifecycle/ConnectorLifecycle.java


src/main/java/com/aipe/connector/sdk/exception/ConnectorException.java


src/main/java/com/aipe/connector/sdk/registry/ConnectorRegistry.java


src/main/java/com/aipe/connector/sdk/loader/ConnectorLoader.java
```


---

# 8. Sequence Diagram（时序图）


Connector启动流程：


```
AgentRuntime

↓

ConnectorManager

↓

ConnectorLoader

↓

Connector

↓

init(Context)

↓

start()

↓

RUNNING
```


---

数据采集流程：


```
Collector

↓

Connector

↓

ObservationEmitter

↓

Agent

↓

Observation Pipeline
```


---

# 9. State Machine（状态机）


Connector生命周期：


```
CREATED

↓

INITIALIZED

↓

STARTING

↓

RUNNING

↓

STOPPING

↓

STOPPED


异常:

任何状态

↓

ERROR
```


---

## State Definition


|State|Meaning|
|-|-|
|CREATED|对象创建|
|INITIALIZED|初始化完成|
|STARTING|启动中|
|RUNNING|运行|
|STOPPING|停止|
|STOPPED|停止完成|
|ERROR|异常|


---

# 10. Implementation Constraints（实现约束）


## 10.1 Must Implement


必须：

- 使用接口隔离
- 支持插件扩展
- 支持生命周期
- 支持真实状态转换
- 支持异常传播


---

## 10.2 Forbidden


禁止：

```
直接new具体Connector

硬编码Connector列表

Mock状态

空生命周期方法

吞异常
```


---

## 10.3 Engineering Rules


必须：

- 所有Connector线程安全
- stop必须释放资源
- 异常必须记录
- 生命周期状态必须准确


---

# 11. Test & Verification（测试与验证）


# 11.1 Build


```bash
mvn clean install
```


---

# 11.2 Run


启动：

Agent。


加载：

测试Connector。


---

# 11.3 Verification Command


验证：

```
ConnectorRegistry

ConnectorState

Lifecycle
```


---

# 11.4 Expected Result


输出：

```
Connector loaded

State RUNNING

Observation emitted
```


---

# 11.5 Troubleshooting


问题：

Connector无法启动。


检查：


```
1. SPI配置

2. Connector实现

3. Context初始化

4. Lifecycle状态
```


---

# END

WP002-Connector-SDK v1.0