# AI Performance Engineer
# Code Blueprint Template v1.0

> Document Type: Engineering Specification Template
>
> Version: v1.0
>
> Status: Frozen
>
> Scope: WP001 ~ WP030
>
> Owner: AI Performance Engineer Architecture Team
>
> This document defines the mandatory structure for all Work Package Blueprints.

---

# 0. Blueprint Metadata

Every Work Package MUST start with metadata.

Example:

```yaml
Blueprint:
  Id: WPXXX
  Name: Work Package Name
  Version: 1.0
  Status: Draft | Frozen | Completed

  Milestone:
    M1 | M2 | M3 | M4

  Priority:
    P0 | P1 | P2

  Owner:
    AI | Human | Mixed

  DependsOn:
    - WPXXX

  RequiredBy:
    - WPXXX

  EstimatedJavaFiles:
    number

  EstimatedWorkload:
    X Days

  CreatedDate:
    YYYY-MM-DD

  LastUpdated:
    YYYY-MM-DD
```

---

# 1. Goal（目标）

## 1.1 Purpose

说明：

- 为什么需要这个 Work Package。
- 解决什么业务问题。
- 在整个系统中的位置。

---

## 1.2 Capability Added

描述完成后系统新增能力。

格式：

```text
Before:

系统无法xxxx

After:

系统能够xxxx
```

---

## 1.3 Scope

明确：

包含：

- xxx
- xxx

不包含：

- xxx
- xxx

禁止范围蔓延。

---

# 2. Acceptance Criteria（验收标准）

必须采用 Checklist。

Example:

```text
□ Source code completed

□ All classes implemented

□ No TODO

□ No Mock data

□ Compile successful

□ Test successful

□ Demo verified
```

---

## 2.1 Functional Acceptance

功能验收。

---

## 2.2 Technical Acceptance

技术验收。

例如：

- 性能
- 稳定性
- 线程安全
- 资源释放

---

## 2.3 Integration Acceptance

集成验收。

说明：

与哪些模块联调。

---

# 3. Package List（包结构）

必须输出完整 Package Tree。

Example:

```text
com.aipe.xxx

├── package-a

├── package-b

└── package-c
```

---

要求：

每个 Package 必须说明：

|Package|Responsibility|
|-|-|
|collector|数据采集|
|builder|数据构建|

---

# 4. Class List（类清单）

每个 Class 必须包含：

---

## Class Template

```
Class Name:

Package:

Type:

Responsibility:

Dependencies:

Input:

Output:

Thread Safety:

Notes:
```

---

Example:

```
Class:

MemoryCollector


Package:

collector


Responsibility:

Collect JVM memory metrics.


Input:

JMX API


Output:

Observation


Dependencies:

MemoryMXBean
```

---

# 5. Method List（方法清单）

所有核心方法必须定义。

---

## Method Template

```
Method:

Parameters:

Return:

Description:

Exception:

Thread Safety:

Side Effect:
```

---

Example:

```java
collect()

Parameters:

none


Return:

List<Observation>


Description:

Collect JVM metrics.


Exception:

CollectException


Thread Safety:

Thread Safe
```

---

# 6. Dependency（依赖关系）

必须说明：

---

## 6.1 Internal Dependency

WP之间关系。

Example:

```
WP001

↓

WP002

↓

WP003
```

---

## 6.2 External Dependency

例如：

- JDK API
- Database
- Middleware
- Third-party library

---

## 6.3 Dependency Rule

必须遵守：

```
High Layer

↓

Low Layer
```

禁止：

循环依赖。

---

# 7. Physical File List（物理文件清单）

这是 AI Coding Agent 的执行清单。

必须列出：

所有需要创建：

```
.java

.yml

.sql

.xml

Dockerfile

configuration files
```

---

格式：

```
Create:

src/main/java/xxx/A.java

src/main/java/xxx/B.java

resources/application.yml
```

---

规则：

1. Blueprint列出的文件必须全部创建。
2. 禁止减少文件数量。
3. 新增文件必须更新Blueprint。

---

# 8. Sequence Diagram（时序图）

必须描述：

对象之间调用流程。

统一格式：

```
Actor

↓

Component

↓

Component

↓

Result
```

---

Example:

```
Agent

↓

ConnectorManager

↓

JvmConnector

↓

MemoryCollector

↓

ObservationBuilder

↓

ObservationSender
```

---

要求：

必须说明：

- 调用顺序
- 数据流向
- 异常处理路径

---

# 9. State Machine（状态机）

所有具有生命周期的对象必须定义状态机。

---

Template:

```
STATE_A

↓

STATE_B

↓

STATE_C
```

---

每个状态必须说明：

|State|Description|Entry|Exit|
|-|-|-|-|
|INIT|初始化|create|start|
|RUNNING|运行|start success|stop|

---

必须定义：

- 正常流程
- 异常流程
- 恢复流程

---

# 10. Implementation Constraints（实现约束）

这是最高优先级章节。

---

# 10.1 Must Implement

必须真实实现。

例如：

JVM:

```
ManagementFactory

MemoryMXBean

ThreadMXBean

GarbageCollectorMXBean
```

Redis:

```
INFO

CLIENT LIST

SLOWLOG
```

MySQL:

```
performance_schema

information_schema

SHOW PROCESSLIST
```

---

# 10.2 Forbidden

禁止：

```
Mock Data

Fake Data

Random Value

Hard Code Result

TODO

Empty Method

UnsupportedOperationException

Ignore Exception
```

---

# 10.3 Engineering Rules

必须：

- 资源释放
- 异常处理
- 日志完整
- 参数校验
- 单元测试

---

# 11. Test & Verification（测试与验证）

每个 WP 必须提供完整验证方案。

---

# 11.1 Build

Example:

```bash
mvn clean install
```

---

# 11.2 Run

Example:

```bash
java -jar xxx.jar
```

---

# 11.3 Verification Command

Example:

```bash
curl localhost:8080/api/status
```

---

# 11.4 Expected Result

描述：

正常情况下：

应该看到：

```
xxx success
```

---

# 11.5 Troubleshooting

必须提供：

问题：

↓

原因：

↓

解决方式：

Example:

```
No observation received

Possible:

1. Connector not started

2. Sender failed

3. Permission denied
```

---

# Final Development Rules

All AI Coding Agents MUST follow:

1. Read MDP first.

2. Read corresponding Blueprint.

3. Implement only current WP.

4. Do not modify other WP.

5. Do not skip classes.

6. Do not reduce implementation.

7. No mock.

8. No TODO.

9. Code must compile.

10. Verification must pass.

---

# Blueprint Lifecycle

```
Draft

↓

Review

↓

Frozen

↓

Development

↓

Verified

↓

Completed
```

---

END OF TEMPLATE