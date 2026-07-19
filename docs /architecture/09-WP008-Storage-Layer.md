WP008 是 AI Performance Engineer 数据基础设施阶段的核心。

前面：

WP001 Agent Runtime
WP002 Connector SDK
WP003 JVM Connector
WP004 Linux Connector
WP005 Redis Connector
WP006 MySQL Connector
WP007 Observation Pipeline

解决：

数据如何产生 → 如何统一 → 如何进入系统

但是企业级性能工程产品必须解决：

数据如何长期保存、快速查询、支持AI分析？

例如：

压测时间：

2026-07-19 14:00 ~ 14:30

AI需要回答：

14:12 TPS开始下降

↓

14:12:05 MySQL lock_wait增加

↓

14:12:08 JVM线程阻塞增加

↓

14:12:10 Redis命中率下降

↓

最终根因:
MySQL锁竞争

没有历史存储：

AI无法推理。

所以 WP008 建立：

Observation

↓

Storage Layer

↓

Historical Performance Knowledge


严格按照冻结模板：

# AI Performance Engineer
# WP008-Storage-Layer Blueprint v1.0


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


RequiredBy:

- WP010 Resource Model
- WP011 Topology Model
- WP013 Evidence Engine
- WP018 Root Cause Engine
- WP030 Knowledge System


EstimatedJavaFiles:
55


EstimatedWorkload:
10 Days


Blueprint Template:
01-Blueprint-Template.md v1.0
1. Goal（目标）
   1.1 Purpose

建立统一性能数据存储层。

负责存储：

Observation

Metric

Resource

Topology Metadata

Analysis Result

支持：

实时查询
历史查询
聚合分析
AI推理
1.2 Capability Added

Before:

Observation

↓

临时数据

After:

Connector


↓

Observation Pipeline


↓

Storage Layer


↓

Historical Performance Database


↓

AI Engine
1.3 Storage Design Principle

采用：

冷热分离。

                    Storage


          ----------------------

          |                    |

      Metadata DB        TimeSeries DB


          |                    |

        MySQL             ClickHouse
1.4 Storage Responsibility
MySQL

存：

业务实体。

例如：

Resource

Connector

Agent

Topology

User

Project
ClickHouse

存：

高频指标。

例如：

CPU

Memory

TPS

QPS

GC

Latency

Lock
2. Acceptance Criteria（验收标准）
   2.1 Functional Acceptance
   □ Observation可以持久化

□ 支持批量写入

□ 支持时间范围查询

□ 支持Resource查询

□ 支持Metric聚合

□ 支持历史压测回放
2.2 Technical Acceptance
□ 高写入吞吐

□ 时间字段索引

□ 分区设计

□ 数据生命周期管理

□ 写入失败重试

□ 存储异常隔离
2.3 Integration Acceptance

完整链路：

ObservationProcessor

↓

StorageWriter

↓

StorageRouter

↓

MySQL/ClickHouse

↓

QueryService
3. Package List（包结构）
   com.aipe.storage


├── datasource


├── mysql


├── clickhouse


├── repository


├── entity


├── mapper


├── writer


├── query


├── retention


├── partition


├── converter

└── support

Package Responsibility
Package	职责
datasource	数据源管理
entity	数据库对象
repository	数据访问
writer	写入
query	查询
retention	生命周期
partition	分区
4. Class List（类清单）
   4.1 StorageService

Package:

com.aipe.storage

职责：

统一存储入口。

4.2 StorageRouter

Package:

writer

职责：

根据数据类型选择存储。

规则：

Resource

↓

MySQL


Metric

↓

ClickHouse
4.3 ObservationWriter

Package:

writer

职责：

写Observation。

4.4 MetricWriter

Package:

writer

职责：

写指标。

4.5 MySQLDataSource

Package:

mysql

职责：

管理元数据数据库连接。

4.6 ClickHouseDataSource

Package:

clickhouse

职责：

管理时序数据库连接。

4.7 ResourceRepository

Package:

repository

职责：

资源查询。

4.8 ObservationRepository

Package:

repository

职责：

Observation查询。

4.9 MetricRepository

Package:

repository

职责：

Metric查询。

4.10 StorageQueryService

Package:

query

职责：

提供查询接口。

支持：

time range

resource

metric
4.11 RetentionManager

Package:

retention

职责：

数据生命周期。

例如：

7天原始数据

90天聚合数据
4.12 PartitionManager

Package:

partition

职责：

管理ClickHouse分区。

4.13 StorageConverter

Package:

converter

职责：

Observation转换数据库模型。

5. Method List（方法清单）
   StorageService
   save(Observation observation)

保存观测数据
batchSave(List<Observation> list)

批量保存
StorageRouter
route(Object data)

选择存储
ObservationWriter
write(Observation observation)

写Observation
MetricWriter
write(List<Metric> metrics)

批量写指标
QueryService
queryMetrics(
resourceId,
metricName,
startTime,
endTime
)

查询历史指标
RetentionManager
cleanExpiredData()

清理历史数据
PartitionManager
createPartition()

创建时间分区
6. Dependency（依赖关系）
   6.1 Internal Dependency
   WP007 Observation Pipeline


        ↓


WP008 Storage Layer


        ↓


WP010 Resource Model

WP011 Topology Model

WP018 Root Cause Engine
6.2 External Dependency
依赖	用途
MySQL	Metadata
ClickHouse	Time Series
MyBatis Plus	ORM
JDBC	连接
Flyway	数据库迁移
6.3 Dependency Rule

上层：

AI Engine

不能：

直接访问数据库。

必须：

QueryService
7. Physical File List（物理文件清单）
   aipe-storage/


src/main/java/com/aipe/storage/StorageService.java


src/main/java/com/aipe/storage/writer/StorageRouter.java


src/main/java/com/aipe/storage/writer/ObservationWriter.java


src/main/java/com/aipe/storage/writer/MetricWriter.java


src/main/java/com/aipe/storage/mysql/MySQLDataSource.java


src/main/java/com/aipe/storage/clickhouse/ClickHouseDataSource.java


src/main/java/com/aipe/storage/repository/ObservationRepository.java


src/main/java/com/aipe/storage/repository/MetricRepository.java


src/main/java/com/aipe/storage/repository/ResourceRepository.java


src/main/java/com/aipe/storage/query/StorageQueryService.java


src/main/java/com/aipe/storage/retention/RetentionManager.java


src/main/java/com/aipe/storage/partition/PartitionManager.java


src/main/java/com/aipe/storage/converter/StorageConverter.java


src/main/resources/db/migration/V1__storage.sql


src/main/resources/application.yml
8. Sequence Diagram（时序图）
   Write Flow
   Connector


↓

ObservationEmitter


↓

ObservationQueue


↓

ObservationProcessor


↓

StorageWriter


↓

StorageRouter


        |
        |
        +---- Resource

        |
        +---- Metric


              ↓


        MySQL / ClickHouse

Query Flow
AI Engine


↓

StorageQueryService


↓

MetricRepository


↓

ClickHouse


↓

Historical Metrics

9. State Machine（状态机）
   StorageWriter
   CREATED


↓

INITIALIZED


↓

READY


↓

WRITING


↓

FLUSHING


↓

COMPLETED



异常:


FAILED

Batch Lifecycle
COLLECTING

↓

FULL

↓

WRITING

↓

SUCCESS

↓

CLEAR

10. Implementation Constraints（实现约束）
    10.1 Must Implement

必须实现：

ClickHouse Metric Table

表：

metric_observation

字段：

id

timestamp

resource_id

resource_type

metric_name

metric_value

labels
MySQL Metadata Table

必须：

agent

resource

connector

topology
Query

支持：

resource + metric + time range
10.2 Forbidden

禁止：

所有数据存MySQL

所有数据存JSON文件

无时间索引

无分区

无生命周期
10.3 Engineering Rules

必须：

批量写入
异步flush
时间分区
数据压缩
查询优化
11. Test & Verification（测试与验证）
    11.1 Build
    mvn clean install
    11.2 Integration Test

启动：

JVM Connector

Linux Connector

Redis Connector

MySQL Connector
11.3 Verification

查询：

过去10分钟:

mysql.qps

jvm.gc.time

redis.ops

host.cpu
11.4 Expected Result

返回：

{
"resource":"mysql-prod-master",
"metric":"mysql.qps",
"points":[
{
"time":"14:00",
"value":1200
}
]
}
11.5 Troubleshooting

无数据：

检查：

1. Writer状态

2. ClickHouse连接

3. 分区创建

4. Batch flush

5. SQL权限
   END

WP008-Storage-Layer v1.0


---

## 当前 MVP 架构进度

现在已经形成：

             AI Performance Engineer


                     Agent

                       |
    --------------------------------

    JVM     Linux     Redis     MySQL

                       |

              Observation Pipeline

                       |

                Storage Layer

                       |

          Historical Performance Data


下一步：

## WP009 Configuration & Deployment Manager


原因：

现在：

- Agent会采集
- Connector会运行
- 数据会保存


但是企业客户第一问：

> 我有500台机器，怎么安装？怎么配置？怎么管理？


WP009 将设计：

- Agent注册
- 配置中心
- Connector动态启停
- 多环境管理
- 企业部署模型

这是进入真正产品化的关键一步。