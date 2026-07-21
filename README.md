# AI Performance Engineer

> **AI Native 全链路性能工程平台 — 理解企业 IT 世界，自动推理性能瓶颈，持续优化并不断成长。**

不是 APM（监控），不是压测工具，而是**会思考的性能工程师**。

---

## 目录

- [产品概览](#产品概览)
- [核心价值](#核心价值)
- [系统架构](#系统架构)
- [M2 已完成模块](#m2-已完成模块)
- [快速开始](#快速开始)
- [存储架构](#存储架构)
- [工程法则](#工程法则)
- [开发路线图](#开发路线图)

---

## 产品概览

### 目标客户

| 维度 | 说明 |
|------|------|
| **谁** | 有 IT 系统的企业（互联网/电商/金融/政企/SaaS）|
| **痛点** | 性能问题发现慢、根因定位难、优化效果不确定、压测编排复杂 |
| **买单决策者** | CTO / 技术总监 / SRE 负责人 |

### 核心价值主张

> **用 AI 替代人肉排查，自动发现瓶颈、定位根因、沉淀知识、闭环验证。**

| 企业痛点 | 我们的价值 | 对应能力 |
|---------|-----------|---------|
| 性能问题发现慢 | 自动发现瓶颈（AI 推理）| Evidence Engine + Timeline |
| 根因定位难 | 定位根因（Timeline + Evidence）| Evidence Engine + Knowledge |
| 优化效果不确定 | 沉淀知识（Knowledge 持续成长）| Knowledge Engine + Optimization |
| 压测编排复杂 | 闭环验证（Execution → New Observation）| Execution Engine |

---

## 系统架构

### AI World Evolution Chain（核心主线）

```
Reality（真实世界）—— 企业 IT 系统运行产生的海量事实
    │
    ▼
Resource（世界中的对象）—— 所有 IT 对象统一抽象为 Resource
    │   物理机 / 应用 / 服务 / 中间件 / 数据库 / 集群 / JVM / ...
    ▼
Observation（对象产生事实）—— 某时刻产生的一条不可变运行事实
    │   Type: METRIC / LOG / TRACE / EVENT / SNAPSHOT
    │   Storage: ClickHouse observation_fact（MergeTree, TTL 365天）
    ▼
Timeline（事实形成历史）—— Resource 在时间维度上的完整运行轨迹
    │   运行时计算, 不存储（Persistence Law-004）
    │   含统计特征: min/max/avg/stdDev + 趋势方向
    ▼
Evidence（AI 解释历史）—— 多个 Observation 关联分析形成的可解释证据链
    │   Confidence 0~100 + ReasoningSteps
    │   Storage: MySQL evidence 表
    ▼
Knowledge（验证后的经验）—— Verified Evidence 沉淀的复用知识
    │   Type: BOTTLENECK/DEPENDENCY/DEPLOYMENT/BUSINESS/OPTIMIZATION/AI
    │   版本管理（升级 = 新版本新记录）
    │   Storage: MySQL knowledge 表（pk_id 自增）
    ▼
Recommendation（知识指导决策）—— Knowledge 应用于具体 Resource 的建议
    │   Priority: HIGH/MEDIUM/LOW + ExecutionPlan + RollbackPlan
    │   Status: PENDING → APPROVED → EXECUTED / REJECTED
    │   Storage: MySQL recommendation 表
    ▼
Execution（执行优化）—— 执行 Recommendation 并验证效果
    │   beforeSnapshot / afterSnapshot JSON + improvementScore 0~100
    │   Status: PENDING → EXECUTING → SUCCESS/FAILED → ROLLED_BACK
    │   Storage: MySQL execution 表
    ▼
New Observation → 闭环 ♻️
```

### 模块关系图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     AI Performance Engineer                              │
│                 B端全链路自动压测 + AI 推理平台                           │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
          ┌─────────────────────────┼─────────────────────────┐
          │                         │                         │
 ┌────────▼────────┐       ┌────────▼────────┐       ┌────────▼────────┐
 │  Connector       │       │  AI Domain      │       │  存储层          │
 │  Runtime (M1)    │       │  Foundation     │       │                 │
 │                  │       │  (M2)           │       │  MySQL 8.0.33   │
 │  aipe-agent      │       │                 │       │  ClickHouse 23.8│
 │  aipe-connectors │       │  ┌─ resource ──┐│       │                 │
 │  aipe-storage    │       │  │observation  ││       │  Docker Compose  │
 └────────────────┘       │  │relationship ││       │  124.223.220.245│
                           │  │timeline     ││       └─────────────────┘
                           │  │evidence     ││
                           │  │knowledge    ││
                           │  │recommend    ││
                           │  │execution    ││
                           │  └─────────────┘│
                           └─────────────────┘
```

### 8 大 Domain Engine 模块

| WP | 模块 | 端口 | 核心 Domain 对象 | 存储 | 依赖 |
|----|------|------|-----------------|------|------|
| WP011 | aipe-resource | 8082 | Resource (聚合根) | MySQL resource | — |
| WP012 | aipe-observation | 8083 | Observation (不可变, 5 类) | ClickHouse observation_fact | resource (String ref) |
| WP013 | aipe-relationship | 8084 | Relationship (独立ID/置信度) | MySQL relationship | resource (JdbcLookupPort) |
| WP014 | aipe-timeline | 8085 | Timeline (运行时, 不存储) | 无 (计算生成) | observation (ObservationQueryPort) |
| WP015 | aipe-evidence | 8086 | Evidence (推理链) | MySQL evidence | observation (ObservationQueryPort) |
| WP016 | aipe-knowledge | 8087 | Knowledge (版本管理) | MySQL knowledge | evidence (String ref) |
| WP017 | aipe-recommendation | 8088 | Recommendation (状态机) | MySQL recommendation | knowledge (String ref) |
| WP018 | aipe-execution | 8089 | Execution (快照对比) | MySQL execution | recommendation (String ref) |

---

## M2 已完成模块

### DDD 分层（每模块统一）

```
com.aipe.{domain}
├── domain/                ← 纯 POJO（禁止 Spring/MyBatis 注解）
│   ├── Xxx.java           ← 聚合根（validate / belongsTo / upgrade ...）
│   ├── XxxId.java         ← 值对象（final + 不可变）
│   ├── XxxType/Status     ← 枚举
│   ├── XxxRepository.java ← 接口（返回 Domain, Never PO）
│   ├── XxxBuilder.java    ← 工厂
│   └── XxxSpecification.java ← 校验规格
├── application/           ← 事务编排（唯一事务 Owner）
│   ├── XxxApplicationService.java
│   └── XxxCalculator/Producer.java
├── infrastructure/        ← 持久化
│   ├── XxxRepositoryImpl.java
│   ├── XxxPO.java         ← @TableName + @TableField
│   ├── XxxMapper.java     ← MyBatis Plus
│   ├── XxxConverter.java  ← PO↔Domain
│   └── XxxDataSourceConfig / XxxSchemaInitializer
└── api/                   ← REST
    ├── XxxController.java
    ├── XxxDtoMapper.java
    ├── dto/XxxRequest/Response.java
    └── exception/XxxExceptionHandler.java
```

### 工程法则落地

| 法则 | 实现方式 |
|------|---------|
| Law-001 Everything Is Resource | 所有 IT 对象统一抽象为 `Resource` |
| Law-002 Observation Belongs to Resource | `resourceId` 强必填 |
| Persistence Law-001 Right Data, Right Storage | Resource→MySQL, Observation→ClickHouse |
| Persistence Law-002 ClickHouse Stores Facts | Evidence/Knowledge/Recommendation 禁止落 CH |
| Persistence Law-004 Timeline Is Computed | Timeline 永不存储，运行时由 Observation 构建 |
| Architecture Law-004 Topology Is A View | Topology 实时投影，不建表 |
| Architecture Law-005 Relationship First-Class | Relationship 有独立 ID/类型/置信度 |
| Gateway Law-001 Repository Returns Domain | 所有 Repository 禁止返回 PO |
| AI Principle-001 Evidence Before Conclusion | 必须先生成 Evidence 才能给 Recommendation |

---

## 快速开始

### 前置条件

- JDK 1.8
- Maven 3.6+
- Docker & Docker Compose（用于启动 MySQL + ClickHouse）

### 启动依赖

```bash
docker-compose up -d
# MySQL :3306  (aipe_metadata / root / root)
# ClickHouse :8123 :9000  (metric_observation / default / pamirs@123)
```

### 构建 & 测试

```bash
mvn clean install        # 构建全部 8 个模块
mvn test                 # 运行 55 个真实 DB/CH 集成测试（全绿）
```

### 启动任一模块

```bash
# 方式 1: Maven
mvn -pl aipe-execution -am spring-boot:run

# 方式 2: Jar
java -jar aipe-execution/target/aipe-execution-1.0.0-SNAPSHOT.jar
```

端口分配：resource=8082, observation=8083, relationship=8084, timeline=8085, evidence=8086, knowledge=8087, recommendation=8088, execution=8089。

---

## 存储架构

### MySQL `aipe_metadata`（元数据 / 知识 / 执行）

| 表 | 主键 | 用途 |
|----|------|------|
| resource | resource_id VARCHAR(64) PK | IT 对象统一抽象 |
| relationship | id VARCHAR(64) PK | Resource 间关系（有向/置信度/来源）|
| evidence | id VARCHAR(64) PK | 可解释证据链 |
| knowledge | pk_id BIGINT AUTO + id+version UK | 版本化知识（升级 = 新记录）|
| recommendation | id VARCHAR(64) PK | 优化建议（状态机）|
| execution | pk_id BIGINT AUTO + id+version UK | 执行记录（快照对比）|

### ClickHouse `metric_observation`（事实 / 时序）

| 表 | 引擎 | 用途 |
|----|------|------|
| observation_fact | MergeTree (PARTITION BY toYYYYMM(timestamp), ORDER BY (resource_id, metric_name, timestamp), TTL 365天) | 不可变运行事实 |

---

## API 参考

| 模块 | 核心 API |
|------|---------|
| **resource** | POST/GET/PUT/DELETE `/api/v1/resources[/{id}]` + `PATCH /{id}/status` |
| **observation** | POST `/api/v1/observations` + `/batch` + GET `/api/v1/observations?resource_id=...` + `trend?interval=1m` |
| **relationship** | POST/GET/DELETE `/api/v1/relationships[/{id}]` + `/upstream` + `/downstream` + `/neighbors` |
| **topology** | GET `/api/v1/topology/current` + `/neighbors` + `/dependencies` + `/impact` + `/path` |
| **timeline** | GET `/api/v1/timelines?resource_id=&metric_name=&start_time=&end_time=` + `/batch` + `/all` + `/enhanced` |
| **evidence** | POST `/api/v1/evidences/generate` + GET `/{id}` + `/{id}/explain` + POST `/{id}/verify` |
| **knowledge** | POST `/api/v1/knowledge` + GET `/{id}` + `/{id}/versions` + POST `/{id}/upgrade` + `/{id}/recommend` |
| **recommendation** | POST `/api/v1/recommendations/generate` + GET `/{id}` + POST `/{id}/approve` + `/reject` + `/execute` |
| **execution** | POST `/api/v1/executions` + POST `/{id}/complete` + `/{id}/rollback` + GET `/{id}` + `/{id}/report` + `/{id}/optimization` |

---

## 部署架构（Docker Compose）

```
┌──────────────────────────────────────────────────────────────┐
│  docker-compose.yml                                           │
│  ┌─────────────────┐  ┌─────────────────────┐               │
│  │ aipe-mysql       │  │ aipe-clickhouse      │               │
│  │ mysql:8.0.33     │  │ clickhouse-server:23.8│              │
│  │ :3306            │  │ :8123 / :9000        │              │
│  │ root/root        │  │ default/pamirs@123   │              │
│  └─────────────────┘  └─────────────────────┘               │
│         │                     │                              │
│         └──────────┬──────────┘                              │
│                    │ aipe-network                            │
└──────────────────────────────────────────────────────────────┘
          │
          ▼
┌──────────────────────────────────────────────────────────────┐
│  8 个独立 Spring Boot 应用 (可独立部署 / 扩缩容)               │
│  resource(8082)  observation(8083)  relationship(8084)       │
│  timeline(8085)  evidence(8086)  knowledge(8087)             │
│  recommendation(8088)  execution(8089)                      │
└──────────────────────────────────────────────────────────────┘
```

---

## 开发路线图

### M1（已完成）
- Agent Bootstrap + Heartbeat
- Connector SDK + JVM/Linux/Redis/MySQL 采集器
- Observation Pipeline（序列化/验证/批处理/队列）
- MySQL + ClickHouse 双存储

### M2（已完成 — AI Domain Foundation）

| WP | 模块 | 状态 |
|----|------|------|
| WP011 Unified Resource Model | 统一资源抽象 | ✅ |
| WP012 Observation Engine | 运行时构建 + ClickHouse 落地 | ✅ |
| WP013 Relationship + Topology | 关系管理 + 拓扑投影 | ✅ |
| WP014 Timeline Engine | 运行时统计特征 + 趋势检测 | ✅ |
| WP015 Evidence Engine | 异常检测 + 因果推理 + 置信度 | ✅ |
| WP016 Knowledge Engine | Verified Evidence → Knowledge + 版本管理 | ✅ |
| WP017 Recommendation Engine | Knowledge → Resource 建议 + 优先级 | ✅ |
| WP018 Execution + Optimization | 执行 + 效果验证 + Knowledge 更新闭环 | ✅ |

### M3（规划中）
- M3.1 自己公司部署验证（接入真实业务 + 收集验证数据）
- M3.2 产品化（部署工具 + 前端 Dashboard + 安全 RBAC + 审计日志）
- M3.3 企业集成（CMDB / Jenkins / Kubernetes 自动操作）
- M3.4 客户交付（标杆客户 + 技术支持 + SLA）

---

*Generated with [Claude Code](https://claude.com/claude-code)*
