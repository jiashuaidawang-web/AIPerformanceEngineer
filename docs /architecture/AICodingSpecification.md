0000.AICodingSpecification.md
建议再进一步。

其实我们现在不是在写 Blueprint。

我们是在制定：

AI Coding Specification

以后：

Rowboat
Claude Code
Codex CLI
Cursor
Windsurf
RooCode

全部按照这一套来开发。

所以我建议Blueprint 模板一旦冻结，以后任何 WP 都必须遵守。

AI Performance Engineer
Code Blueprint Template v1.0（冻结版）

以后所有 WP 必须严格按照下面 10 个章节 输出。

1. Goal（目标）

回答三个问题：

为什么开发这个 WP？
它解决什么问题？
完成后产品能力增加什么？

例如：

实现 JVM Connector。

让 Agent 具备 JVM 指标采集能力。

成为后续 Redis、MySQL Connector 的参考实现。
2. Acceptance Criteria（验收标准）

必须全部可以验证。

例如：

√ Agent能够加载Connector

√ 每5秒采集一次

√ Observation正确生成

√ 能正常停止

√ 无内存泄漏

禁止：

基本完成

差不多

支持

这种模糊描述。

3. Package List（Package列表）

必须列出：

Package

↓

子Package

例如：

com.aipe.connector.jvm

↓

collector

↓

builder

↓

resource

↓

metric

↓

config
4. Class List（Class列表）

每个 Class：

必须说明：

Class

职责

依赖

输出

例如：

MemoryCollector

职责：

采集Heap

依赖：

MemoryMXBean

输出：

Observation

不能只有名字。

5. Method List（Method列表）

必须列：

Method

作用

参数

返回值

例如：

collectHeap()

参数：

无

返回：

List<Observation>

作用：

采集Heap指标。

以后 Rowboat：

必须实现全部Method。

6. Dependency（依赖关系）

明确：

依赖：

哪些 WP。

输出：

给哪些 WP。

例如：

WP001

↓

WP002

↓

WP003

↓

WP007

不能出现循环依赖。

7. Physical File List（物理文件清单）

这是最重要的一项。

以后：

每个 WP：

最后必须列：

需要物理创建：

哪些.java

哪些yaml

哪些xml

哪些sql

例如：

JvmConnector.java

MemoryCollector.java

GcCollector.java

ThreadCollector.java

...

AI：

必须全部创建。

不能省。

8. Sequence Diagram（时序图）

新增。

目的：

告诉 AI：

这些类如何协作。

例如：

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

Sender

这样：

AI：

不会乱调用。

以后：

每个 WP：

必须有。

9. State Machine（状态机）

新增。

目的：

告诉 AI：

生命周期。

例如：

INIT

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

以后：

所有：

Connector

Service

Pressure Session

都必须有。

10. Implementation Constraints（实现约束）【新增】

这一章我建议叫：

Implementation Constraints

因为它不仅仅是：

JDK。

以后：

Redis。

MySQL。

Kafka。

都会有。

所以：

统一叫：

实现约束。

例如：

JVM：

必须：

ManagementFactory

MemoryMXBean

ThreadMXBean

GarbageCollectorMXBean

OperatingSystemMXBean

禁止：

Mock

TODO

随机数

固定值

必须：

真实采集。

例如：

Redis：

必须：

INFO

CONFIG GET

SLOWLOG

CLIENT LIST

不能：

模拟Redis。

例如：

MySQL：

必须：

performance_schema

information_schema

show processlist

show engine innodb status

不能：

自己构造SQL。

例如：

Linux：

必须：

/proc

/sys

top

vmstat

iostat

netstat

不能：

写死。

所以：

这一章以后会越来越重要。

最终冻结模板（建议）

以后任何一个 WP，必须严格按照下面 10 个章节 输出：

章节	内容
1	Goal（目标）
2	Acceptance Criteria（验收标准）
3	Package List（包结构）
4	Class List（类清单）
5	Method List（方法清单）
6	Dependency（依赖关系）
7	Physical File List（物理文件清单）
8	Sequence Diagram（时序图）
9	State Machine（状态机）
10	Implementation Constraints（实现约束）
我还有一个建议（建议直接冻结到 Blueprint 模板）

除了这 10 项，我建议再增加一个固定章节，作为第 11 项：

11. Test & Verification（测试与验证）

这是为了避免 Rowboat 写完代码却不知道如何证明它真的可用。

这一章固定要求：

启动方式（如何启动这个 WP）
验证命令（例如 curl、JMX、SQL）
预期结果（应该看到什么日志、接口返回、数据库记录）
失败排查点（最常见的错误及检查顺序）

例如 WP003（JVM Connector）可以规定：

启动 Agent 后，每 5 秒输出一次采集日志。
Observation Service 能收到 jvm.heap.used、jvm.thread.count 等指标。
ClickHouse 中能查询到对应记录。
如果没有数据，优先检查 JMX、Connector 注册、ObservationSender。

这样，每个 WP 不仅有开发规范，还有验收脚本，AI 编码完成后可以按照这一节自检，你也能直接照着验证，而不是靠人工猜测是否实现正确。

我建议把 Blueprint 模板最终冻结为 11 个章节，并从 WP004 开始严格执行，不再变更。