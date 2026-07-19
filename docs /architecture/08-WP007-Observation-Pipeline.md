因为现在：

JVM Connector
Linux Connector
Redis Connector
MySQL Connector

        ↓

各种数据产生了

但是还缺：

所有数据如何统一进入 AI 世界模型？

WP007 会定义：

Observation对象
Metric标准
Resource关联
时间窗口
数据采集流水线
存储入口

这是后面：

Topology / Evidence / AI Root Cause 的地基。

WP007 是整个 AI Performance Engineer 数据架构的第一个核心基础设施 Work Package。

前面 WP001~WP006：

完成的是：

Agent
+
Connector
+
Collector

解决：

数据怎么采集

但是企业级产品真正困难的是：

不同来源的数据如何变成 AI 可以理解的统一世界模型？

例如：

JVM：

jvm.heap.used=8GB

Linux：

host.memory.used=14GB

Redis：

redis.connected_clients=5000

MySQL：

mysql.lock_wait=120

如果只是存指标：

AI无法知道：

14:03:20

订单接口TPS下降

↓

哪个服务

↓

依赖哪个Redis

↓

连接哪个MySQL

↓

哪个指标异常

所以 WP007 建立：

Observation Model

+
Observation Pipeline

+
Resource Binding

+
Time Series Foundation

这是后续：

WP010 Resource Model
WP011 Topology Model
WP013 Evidence Engine
WP018 Root Cause Engine

的基础。

WP007 是整个 AI Performance Engineer 数据架构的第一个核心基础设施 Work Package。

前面 WP001~WP006：

完成的是：

Agent
+
Connector
+
Collector

解决：

数据怎么采集

但是企业级产品真正困难的是：

不同来源的数据如何变成 AI 可以理解的统一世界模型？

例如：

JVM：

jvm.heap.used=8GB

Linux：

host.memory.used=14GB

Redis：

redis.connected_clients=5000

MySQL：

mysql.lock_wait=120

如果只是存指标：

AI无法知道：

14:03:20

订单接口TPS下降

↓

哪个服务

↓

依赖哪个Redis

↓

连接哪个MySQL

↓

哪个指标异常

所以 WP007 建立：

Observation Model

+
Observation Pipeline

+
Resource Binding

+
Time Series Foundation

这是后续：

WP010 Resource Model
WP011 Topology Model
WP013 Evidence Engine
WP018 Root Cause Engine

的基础。

严格按照冻结模板：

AI Performance Engineer
WP007-Observation-Pipeline Blueprint v1.0
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

WP001 Agent Runtime
WP002 Connector SDK
WP003 JVM Connector
WP004 Linux Connector
WP005 Redis Connector
WP006 MySQL Connector


RequiredBy:

WP010 Resource Model
WP011 Topology Model
WP013 Evidence Engine
WP018 Root Cause Engine


EstimatedJavaFiles:
45


EstimatedWorkload:
8 Days
1. Goal（目标）
   1.1 Purpose

建立统一性能观测数据管道。

负责：

接收所有 Connector 输出的数据。

包括：

JVM Observation

Linux Observation

Redis Observation

MySQL Observation

统一转换为：

Observation
1.2 Capability Added

Before:

Connector

↓

各自产生数据

After:

Connector


↓

Observation Pipeline


↓

统一数据模型


↓

Storage


↓

AI Engine
1.3 Core Design Principle

Observation 是 AI Performance Engineer 的：

"事实层"

它只描述：

发生了什么。

例如：

14:03:20

resource:
redis-node-01


metric:

connected_clients


value:

5200


severity:

warning

不负责：

为什么发生。

原因分析交给：

Evidence Engine。

2. Acceptance Criteria（验收标准）
   2.1 Functional Acceptance
   □ 接收所有Connector数据

□ 统一Observation格式

□ 自动绑定Resource

□ 自动生成时间戳

□ 支持批量发送

□ 支持失败重试

□ 支持数据压缩
2.2 Technical Acceptance
□ 高吞吐

□ 异步处理

□ 不阻塞Connector

□ 数据顺序保证

□ 消息丢失保护

□ 支持扩展字段
2.3 Integration Acceptance

完整链路：

Connector

↓

ObservationEmitter

↓

ObservationQueue

↓

ObservationProcessor

↓

StorageWriter
3. Package List（包结构）
   com.aipe.observation


├── model

│
├── Observation

│
├── Metric

│
├── ResourceReference


├── collector


├── emitter


├── queue


├── processor


├── validator


├── storage


├── serializer


└── support

Package Responsibility
Package	职责
model	数据模型
emitter	接收数据
queue	缓冲
processor	处理
validator	校验
storage	持久化
serializer	序列化
4. Class List（类清单）
   4.1 Observation

Package:

model

核心领域对象。

表示：

一次观测事实。

字段：

observationId

timestamp

resourceId

resourceType

metrics

labels

source
4.2 Metric

Package:

model

指标对象。

字段：

name

value

unit

type

timestamp
4.3 ResourceReference

Package:

model

关联资源。

例如：

redis-node-01

mysql-prod-master

order-service
4.4 ObservationEmitter

Package:

emitter

职责：

Connector数据入口。

所有Connector调用：

emit()
4.5 ObservationQueue

Package:

queue

职责：

异步缓冲。

实现：

BlockingQueue

4.6 ObservationProcessor

Package:

processor

职责：

处理Observation。

流程：

validate

↓

enrich

↓

serialize

↓

store
4.7 ObservationValidator

Package:

validator

职责：

数据合法性检查。

检查：

resource存在
timestamp合法
metric格式
4.8 ObservationSerializer

Package:

serializer

职责：

JSON序列化。

4.9 ObservationStorageWriter

Package:

storage

职责：

写入存储。

MVP:

MySQL/ClickHouse

4.10 ObservationBatcher

Package:

processor

职责：

批量发送。

5. Method List（方法清单）
   ObservationEmitter
   emit(Observation observation)

作用：

接收Connector产生的数据
ObservationQueue
offer(Observation observation)

加入队列
poll()

获取待处理数据
ObservationProcessor
process(Observation observation)

处理单条Observation
ObservationValidator
validate(Observation observation)

返回:

ValidationResult
ObservationSerializer
serialize(Observation observation)

返回:

String
ObservationStorageWriter
write(List<Observation> list)

批量写入
ObservationBatcher
batch()

生成批量数据
6. Dependency（依赖关系）
   6.1 Internal Dependency
   WP003 JVM Connector

        |

WP004 Linux Connector

        |

WP005 Redis Connector

        |

WP006 MySQL Connector


        ↓


WP007 Observation Pipeline


        ↓


WP010 Resource Model
6.2 External Dependency
依赖	用途
Jackson	序列化
Kafka(Optional)	消息
ClickHouse	时序存储
MySQL	元数据
6.3 Design Rule

Connector：

不知道：

Storage
AI
Database

只知道：

ObservationEmitter
7. Physical File List（物理文件清单）

必须创建：

aipe-observation/


src/main/java/com/aipe/observation/model/Observation.java


src/main/java/com/aipe/observation/model/Metric.java


src/main/java/com/aipe/observation/model/ResourceReference.java


src/main/java/com/aipe/observation/emitter/ObservationEmitter.java


src/main/java/com/aipe/observation/queue/ObservationQueue.java


src/main/java/com/aipe/observation/processor/ObservationProcessor.java


src/main/java/com/aipe/observation/processor/ObservationBatcher.java


src/main/java/com/aipe/observation/validator/ObservationValidator.java


src/main/java/com/aipe/observation/serializer/ObservationSerializer.java


src/main/java/com/aipe/observation/storage/ObservationStorageWriter.java


src/main/resources/application.yml
8. Sequence Diagram（时序图）
   Connector数据流
   JvmConnector


↓

ObservationEmitter


↓

ObservationQueue


↓

ObservationProcessor


↓

ObservationValidator


↓

ObservationSerializer


↓

StorageWriter
多Connector并发
JvmConnector

LinuxConnector

RedisConnector

MySQLConnector


        ↓


ObservationEmitter


        ↓


Queue


        ↓


Processor


        ↓


Storage

9. State Machine（状态机）
   Observation生命周期
   CREATED


↓

EMITTED


↓

QUEUED


↓

PROCESSING


↓

VALIDATED


↓

STORED


↓

COMPLETED


异常:

FAILED
ObservationProcessor状态
INIT

↓

RUNNING

↓

DRAINING

↓

STOPPED
10. Implementation Constraints（实现约束）
    10.1 Must Implement

必须实现：

Observation唯一ID

规则：

UUID
+
timestamp
时间统一

所有Observation：

必须包含：

eventTime
receiveTime
Resource绑定

必须包含：

resourceId
resourceType
Metric标准

例如：

JVM:

jvm.heap.used.bytes

Linux:

host.cpu.usage.percent

Redis:

redis.connected.clients

MySQL:

mysql.lock.wait.count
10.2 Forbidden

禁止：

直接Connector写数据库

Mock Observation

无Resource

无Timestamp

字段硬编码
10.3 Engineering Rules

必须：

异步处理
背压机制
批处理
异常隔离
可扩展字段
11. Test & Verification（测试与验证）
    11.1 Build
    mvn clean install
    11.2 Integration Test

启动：

JvmConnector

LinuxConnector

RedisConnector

MySQLConnector
11.3 Verification

检查：

Observation数量

时间连续性

Resource关联

Metric完整性
11.4 Expected Result

产生：

Observation:


{
id:"",
resource:{
type:"MYSQL"
},
metrics:[
{
name:"mysql.qps",
value:1200
}
],
timestamp:""
}
11.5 Troubleshooting

无数据：

检查：

1. Connector是否RUNNING

2. Emitter是否收到

3. Queue是否阻塞

4. Processor是否启动

5. Storage是否成功
   END

WP007-Observation-Pipeline v1.0



---

## 当前 MVP 架构进度

已经形成第一条完整闭环：

```
                    AI Performance Engineer


                         Agent


                           |
        ------------------------------------
        |          |          |            |
       JVM      Linux       Redis       MySQL
        |          |          |            |
        ------------------------------------
                         |
                 Observation Pipeline
                         |
                    Storage
```


下一步：

## WP008 Storage Layer

因为现在：

```
Connector
    ↓
Observation
```

已经有了。


但是：

> 数据必须可靠保存，才能做历史分析、趋势分析、AI推理。


WP008 将设计：

- MySQL 元数据存储
- ClickHouse 时序存储
- Observation表
- Metric表
- 索引设计
- 数据生命周期
- 查询模型

这一步之后，AI Performance Engineer 才真正拥有“记忆”。