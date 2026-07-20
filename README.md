# AI Performance Engineer

> **AI Native 全链路性能工程平台 — 理解企业 IT 世界，自动推理性能瓶颈，持续优化并不断成长。**

不是 APM（监控），不是压测工具，而是**会思考的性能工程师**。

---

## 目录

- [产品概览](#产品概览)
- [核心价值](#核心价值)
- [适用企业](#适用企业)
- [已完成能力](#已完成能力)
- [系统架构](#系统架构)
- [快速开始](#快速开始)
- [接入指南](#接入指南)
- [配置说明](#配置说明)
- [API 参考](#api-参考)
- [开发路线图](#开发路线图)
- [贡献指南](#贡献指南)

---

## 产品概览

### 一句话定位

AI Performance Engineer 是面向企业的 **AI Native 性能工程平台**，能够自动理解企业的 IT 世界、推理性能瓶颈、沉淀优化知识，并持续自我成长。

### 解决什么问题

| 传统方式 | AI Performance Engineer |
|---------|------------------------|
| 性能问题发现慢（靠人肉排查） | **自动发现**：AI 沿 Timeline 自动识别异常 |
| 根因定位难（依赖专家经验） | **自动推理**：Evidence → Knowledge 链路定位根因 |
| 优化效果不确定（缺乏闭环验证） | **闭环验证**：Execution → New Observation 持续验证 |
| 知识随人员流失而丢失 | **知识沉淀**：Knowledge 持续积累、不断演化 |
| 压测编排复杂（人工配置） | **统一编排**：Scenario 编排整个压测/优化流程 |

### 工作方式

```
企业 IT 资源（Resource）
    │
    ▼
Agent 采集运行事实（Observation）──→ ClickHouse（Fact Store）
                                        │
                                        ▼
                              AI Runtime 推理流水线
                              Timeline → Evidence → Knowledge
                                        │
                                        ▼
                              Recommendation → Execution → Optimization
                                        │
                                        ▼
                                   New Observation（闭环成长）
```

---

## 核心价值

### 1. 自动发现瓶颈
AI 沿 Timeline 自动分析运行事实，识别异常模式，无需人工盯盘。

### 2. 智能根因定位
Evidence Engine 基于时间线和拓扑关系推理"为什么"，Knowledge 层持续沉淀经验。

### 3. 闭环优化验证
Recommendation → Execution → New Observation 验证优化效果，形成闭环。

### 4. 知识持续成长
Knowledge 是"已验证的 Evidence"，随使用不断演化（Evolution），而非覆盖（Overwrite）。

### 5. 统一资源视图
所有 IT 对象统一抽象为 Resource（业务域维度），摒弃传统机器视角。

### 6. 可解释、可审计
AI 所有输出必须 Explainable（回答"为什么"、"依据什么"），支持 Replay / Trace / Audit。

---

## 适用企业

### 适用场景

| 场景 | 说明 |
|------|------|
| **全链路压测** | 自动编排压测、采集指标、分析瓶颈 |
| **容量规划** | 基于历史趋势预测资源需求 |
| **根因定位** | 故障时快速定位根因，缩短 MTTR |
| **性能优化** | 持续发现优化空间，闭环验证效果 |
| **故障预测** | 趋势分析，提前发现潜在风险 |

### 适用的企业类型

| 企业类型 | 适配度 | 说明 |
|---------|--------|------|
| **互联网 / SaaS** | ⭐⭐⭐⭐⭐ | 微服务、容器化架构天然适配 |
| **电商** | ⭐⭐⭐⭐⭐ | 大促压测、全链路瓶颈定位核心场景 |
| **金融科技** | ⭐⭐⭐⭐ | 性能合规 + 稳定性保障（需补充安全能力）|
| **政企 / 信创** | ⭐⭐⭐⭐ | 国产化环境（需适配信创 CPU/OS/数据库）|
| **传统企业** | ⭐⭐⭐ | legacy 系统需要定制 Connector |

### 技术栈要求

企业只需具备以下环境：

| 组件 | 最低要求 | 说明 |
|------|---------|------|
| **MySQL** | 5.7+ / 8.0 | 元数据 / Resource / Knowledge 存储 |
| **ClickHouse** | 21+ | 时序指标 Fact Store |
| **Java** | 1.8+ | 后端运行环境 |
| **网络** | Agent → Backend 可达 | 采集器到服务的网络连通 |
| **操作系统** | Linux / macOS / Windows | JVM 跨平台 |

可选组件：

| 组件 | 说明 |
|------|------|
| **Redis** | 缓存 / AI Memory |
| **Neo4j** | 拓扑 / 关系（高级功能）|
| **Kubernetes** | 容器编排环境 |
| **Prometheus** | 已有监控系统数据接入 |
| **OpenTelemetry** | 已有链路追踪数据接入 |

---

## 已完成能力

### ✅ Phase 1: Agent + 数据采集（M1，已完成）

| 能力 | 状态 | 说明 |
|------|------|------|
| Agent Bootstrap | ✅ | Agent 生命周期、心跳、配置热更新 |
| Connector SDK | ✅ | 统一 SPI（init/collect/start/stop/destroy）|
| JVM Connector | ✅ | JMX 采集 Heap/Thread/GC/CPU |
| Linux Connector | ✅ | /proc 采集 CPU/Memory/Disk/Network |
| Redis Connector | ✅ | INFO 采集 Memory/Clients/Stats |
| MySQL Connector | ✅ | SHOW STATUS 采集 QPS/慢查询/连接数 |
| Observation Pipeline | ✅ | Agent → HTTP → Backend → ClickHouse |
| Storage Layer | ✅ | MySQL（元数据）+ ClickHouse（时序）|
| Scenario 管理 | ✅ | 压测场景编排入口 |

### ✅ Phase 2: Resource Domain（M2-WP011，已完成）

| 能力 | 状态 | 说明 |
|------|------|------|
| 统一 Resource 抽象 | ✅ | 所有 IT 对象统一为 Resource（业务域维度）|
| DDD 领域模型 | ✅ | Resource 聚合根 + 值对象 + 业务方法 |
| Resource 生命周期 | ✅ | 创建/更新/删除/状态流转（乐观锁）|
| Resource 发现 | ✅ | Connector/Agent 上报 → 幂等创建/更新 |
| 业务归属管理 | ✅ | businessSystem 必填（Law-001 禁止游离资源）|
| 父子关系绑定 | ✅ | parent_resource_id 支持层级结构 |
| 乐观锁 | ✅ | @Version 并发安全 |
| REST API | ✅ | CRUD + 查询 + 状态流转 + 资源上报 |
| 全局异常处理 | ✅ | 400/409/500 统一响应 |

### 🔲 Phase 3: AI Runtime（M2，待建设）

| 能力 | 状态 | 说明 |
|------|------|------|
| Observation Engine | 🔲 | WP012: Observation 领域模型 |
| Timeline Builder | 🔲 | WP014: 动态构建 Resource Timeline |
| Evidence Engine | 🔲 | WP015: 发现异常、形成 Evidence |
| Knowledge Engine | 🔲 | WP016: 形成/验证 Knowledge |
| Recommendation Engine | 🔲 | WP017: 生成优化建议 |
| Execution + Optimization | 🔲 | WP018: 执行优化、闭环验证 |

### 🔲 Phase 4: 企业能力（待建设）

| 能力 | 状态 | 说明 |
|------|------|------|
| 认证授权（RBAC）| 🔲 | 用户/角色/权限 |
| 多租户隔离 | 🔲 | 企业数据隔离 |
| 审计日志 | 🔲 | 操作可追溯 |
| 企业集成 | 🔲 | CMDB/Jenkins/K8s Adapter |
| 部署运维工具 | 🔲 | 一键安装/升级/监控 |

---

## 系统架构

### 逻辑架构（DDD 分层）

```
┌─────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Resource API │  │ Agent API    │  │ Observation API      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      Application Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Lifecycle    │  │ Discovery    │  │ Workflow             │  │
│  │ Manager      │  │ Service      │  │ Orchestrator         │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                        Domain Layer                              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────┐ │
│  │ Resource │ │Observatn │ │Relation- │ │Timeline  │ │Evidnc│ │
│  │          │ │          │ │ship      │ │          │ │      │ │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────┘ │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │
│  │Knowledge │ │Recommend │ │Execution │ │ Optimization     │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                     Infrastructure Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Repository   │  │ AI Runtime   │  │ Connector SDK        │  │
│  │ Impl         │  │              │  │                      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                       Storage Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ MySQL        │  │ ClickHouse   │  │ Neo4j (可选)         │  │
│  │ (元数据)     │  │ (时序)       │  │ (拓扑)               │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      Connector Layer                             │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌───────┐ ┌─────────────┐│
│  │ JVM  │ │Linux │ │Redis │ │MySQL │ │Prometh│ │ OTel        ││
│  └──────┘ └──────┘ └──────┘ └──────┘ └───────┘ └─────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### 数据流

```
Agent/Connector → Observation (Fact)
                    │
                    ▼
        ClickHouse（Fact Store）
                    │
                    ▼
        Timeline Builder（按 Resource + 时间聚合）
                    │
                    ▼
        Evidence Engine（异常检测 + 根因推理）
                    │
                    ▼
        Knowledge Engine（已验证 Evidence → Knowledge）
                    │
                    ▼
        Recommendation Engine（生成优化建议）
                    │
                    ▼
        Execution Planner（执行优化）
                    │
                    ▼
        New Observation（验证效果，闭环）
```

### 存储分工

| 存储 | 职责 | 对齐原则 |
|------|------|---------|
| **MySQL** | Resource / Knowledge / Execution / Configuration | 业务元数据、已验证知识 |
| **ClickHouse** | Observation / Timeline Facts / Raw Metrics | 时序事实（只存 Reality）|
| **Neo4j** | Relationship / Topology | 空间关系（可选）|
| **Redis** | Cache / AI Memory | 性能加速 |

---

## 快速开始

### 前置条件

- JDK 1.8+
- Maven 3.9+
- MySQL 5.7+ / 8.0
- ClickHouse 21+

### 方式一：Docker Compose（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/jiashuaidawang-web/AIPerformanceEngineer.git
cd AIPerformanceEngineer

# 2. 启动 MySQL + ClickHouse
docker-compose up -d

# 3. 构建项目
mvn clean install -DskipTests

# 4. 启动 Backend
java -jar aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar &

# 5. 启动 Resource 模块
java -jar aipe-resource/target/aipe-resource-1.0.0-SNAPSHOT.jar &

# 6. 启动 Agent（采集端）
java -jar aipe-agent/target/aipe-agent-1.0.0-SNAPSHOT.jar &
```

或使用一键脚本：

```bash
./start.sh          # 启动 backend + config + agent
```

### 方式二：本地部署

```bash
# 1. 配置数据库（修改 application.yml 中的连接信息）
# MySQL: localhost:3306/aipe_metadata
# ClickHouse: localhost:8123/metric_observation

# 2. 构建
mvn clean install -DskipTests

# 3. 启动（分别启动各模块）
java -jar aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar
java -jar aipe-resource/target/aipe-resource-1.0.0-SNAPSHOT.jar
java -jar aipe-agent/target/aipe-agent-1.0.0-SNAPSHOT.jar
```

### 验证启动

```bash
# Backend 健康检查（默认 8081 端口）
curl http://localhost:8081/api/v1/agents

# Resource 模块（默认 8082 端口）
curl http://localhost:8082/api/v1/resources
```

---

## 接入指南

### 1. 接入数据源（Connector）

AI Performance Engineer 通过 Connector 采集数据。内置 Connector：

| Connector | 数据源 | 采集内容 | 状态 |
|-----------|--------|---------|------|
| JVM Connector | Java 应用 | Heap / Thread / GC / CPU（JMX）| ✅ |
| Linux Connector | 主机 | CPU / Memory / Disk/ Network（/proc）| ✅ |
| Redis Connector | Redis | Memory / Clients / Stats（INFO）| ✅ |
| MySQL Connector | MySQL | QPS / 慢查询 / 连接数（SHOW STATUS）| ✅ |
| Prometheus Connector | Prometheus | 通用 Pull 指标 | 🔲 |
| OTel Connector | OpenTelemetry | Trace / Metric | 🔲 |

### 2. 自定义 Connector

实现 `Connector` 接口即可接入新数据源：

```java
public class MyConnector implements Connector {
    @Override
    public String getConnectorType() { return "MY_DB"; }

    @Override
    public List<ObservationData> collect() {
        // 实现采集逻辑
        return observations;
    }
    // ... 其他方法
}
```

### 3. 上报资源（Resource Discovery）

采集到的资源信息通过 REST API 上报：

```bash
# 单条上报
curl -X POST http://localhost:8082/api/v1/resources/discover \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": "order-service-001",
    "resourceName": "订单服务",
    "resourceType": "APPLICATION",
    "businessSystem": "订单系统",
    "host": "192.168.1.100",
    "port": 8080
  }'

# 批量上报
curl -X POST http://localhost:8082/api/v1/resources/discover/batch \
  -H "Content-Type: application/json" \
  -d '[{"resourceId":"r1","resourceType":"MYSQL","businessSystem":"订单系统"}]'
```

### 4. 管理 Resource

```bash
# 创建
curl -X POST http://localhost:8082/api/v1/resources \
  -H "Content-Type: application/json" \
  -d '{"resourceName":"订单服务","resourceType":"APPLICATION","businessSystem":"订单系统"}'

# 查询
curl http://localhost:8082/api/v1/resources/{id}

# 按业务域查询
curl "http://localhost:8082/api/v1/resources?business_system=订单系统"

# 更新状态
curl -X PATCH "http://localhost:8082/api/v1/resources/{id}/status?status=MAINTENANCE"

# 删除
curl -X DELETE http://localhost:8082/api/v1/resources/{id}
```

### 5. 数据流接入（M1 已支持）

```bash
# Agent 上报 Observation（已有）
curl -X POST http://localhost:8081/api/v1/observations/batch \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "agent-001",
    "observations": [
      {"resource_id": "order-service-001", "metric_name": "cpu_usage", "metric_value": 85.5, "timestamp": 1784554140445}
    ]
  }'

# 查询 Observation
curl "http://localhost:8081/api/v1/observations?resource_id=order-service-001&metric_name=cpu_usage&start_time=0&end_time=9999999999999"
```

---

## 配置说明

### 核心配置（application.yml）

```yaml
# MySQL 连接
spring:
  datasource:
    mysql:
      url: jdbc:mysql://localhost:3306/aipe_metadata?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
      driver-class-name: com.mysql.cj.jdbc.Driver
    clickhouse:
      url: jdbc:clickhouse://localhost:8123/metric_observation?compress=0
      username: default
      password: your_password

# MyBatis Plus
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
```

### 数据库准备

```sql
-- MySQL 数据库
CREATE DATABASE IF NOT EXISTS aipe_metadata DEFAULT CHARSET utf8mb4;

-- ClickHouse 数据库（首次启动自动创建）
CREATE DATABASE IF NOT EXISTS metric_observation;
```

系统启动时会自动执行 DDL 迁移（ALTER 列 + 创建索引）。

---

## API 参考

### Resource API

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/api/v1/resources` | 创建资源 | ✅ |
| GET | `/api/v1/resources/{id}` | 查询单个资源 | ✅ |
| PUT | `/api/v1/resources/{id}` | 更新资源 | ✅ |
| DELETE | `/api/v1/resources/{id}` | 删除资源（逻辑删除）| ✅ |
| GET | `/api/v1/resources` | 列表查询（?business_system=xxx）| ✅ |
| PATCH | `/api/v1/resources/{id}/status` | 更新状态 | ✅ |
| POST | `/api/v1/resources/discover` | 资源发现（Connector 上报）| ✅ |
| POST | `/api/v1/resources/discover/batch` | 批量资源发现 | ✅ |

### Observation API（M1 已有）

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/api/v1/observations/batch` | Agent 批量上报 Observation | ✅ |
| GET | `/api/v1/observations` | 查询 Observation | ✅ |
| GET | `/api/v1/observations/latest` | 查询最新 Observation | ✅ |

### Agent API（M1 已有）

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/api/v1/agents/register` | Agent 注册 | ✅ |
| POST | `/api/v1/agents/{id}/heartbeat` | Agent 心跳 | ✅ |

### 统一响应格式

```json
{
  "code": 0,
  "message": "success",
  "requestId": "uuid",
  "timestamp": 1784554140445,
  "data": { ... }
}
```

### 错误码

| HTTP | code | 场景 |
|------|------|------|
| 200 | 0 | 成功 |
| 400 | 400 | 参数校验失败 / 无 businessSystem |
| 409 | 409 | 非法状态流转 |
| 500 | 500 | 内部错误 |

---

## 开发路线图

### 已完成 ✅

- [x] M1: Agent + Connector + Observation Pipeline + Storage
- [x] WP011: Resource Domain（统一资源抽象 + DDD + REST API）
- [x] IM 体系：IM-000~IM-011（工程映射宪法）
- [x] 四大基础标准：Architecture Laws / Specification / Blueprint / Gate

### 进行中 🔲

- [ ] WP012: Observation Engine（Observation 领域模型）
- [ ] WP013: Relationship Model（资源关系 + 图数据库）
- [ ] WP014: Timeline Model（动态 Timeline 构建）

### 待建设 🔲

- [ ] WP015: Evidence Engine（异常检测 + 根因推理）
- [ ] WP016: Knowledge Engine（知识沉淀 + 验证）
- [ ] WP017: Recommendation Engine（优化建议生成）
- [ ] WP018: Execution + Optimization（执行 + 闭环验证）
- [ ] WP019~WP030: 高级能力（RCA / JMeter 集成 / 全链路压测编排）

### 未来方向 🔲

- [ ] 认证授权（RBAC + 多租户）
- [ ] 审计日志 + 合规
- [ ] 企业集成（CMDB / Jenkins / K8s / Jira）
- [ ] 部署工具（Helm Chart / 一键安装）
- [ ] SaaS 多租户

---

## 项目结构

```
AIPerformanceEngineer/
├── aipe-agent/              # Agent（采集端，部署在企业机器上）
├── aipe-backend/            # Backend（网关，接收 Agent 数据）
├── aipe-common/             # 公共模块（ObservationData 等）
├── aipe-config-manager/     # 配置管理
├── aipe-connectors/         # Connector 实现
│   ├── connector-sdk/       # Connector SPI
│   ├── connector-jvm/       # JVM 采集
│   ├── connector-linux/     # Linux 采集
│   ├── connector-redis/     # Redis 采集
│   └── connector-mysql/     # MySQL 采集
├── aipe-observation/        # Observation 处理管线
├── aipe-resource/           # Resource Domain（WP011 新增）
├── aipe-storage/            # 存储层（MySQL + ClickHouse）
├── docs/                    # 文档
│   └── architecture/        # 架构规范（Constitution / IM / Laws）
├── docker-compose.yml       # MySQL + ClickHouse 快速部署
├── start.sh                 # 一键启动脚本
└── pom.xml                  # 父 POM
```

---

## 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 运行环境 |
| Spring Boot | 2.7.18 | 应用框架 |
| MyBatis Plus | 3.5.3.1 | ORM |
| Druid | 1.2.20 | 连接池 |
| ClickHouse JDBC | 0.4.6 | ClickHouse 驱动 |
| Jedis | 4.4.6 | Redis 客户端 |
| Hutool | 5.8.22 | 工具库 |
| Lombok | 1.18.30 | 代码简化 |
| MySQL | 5.7+ / 8.0 | 元数据存储 |
| ClickHouse | 21+ | 时序存储 |

---

## 贡献指南

### 开发流程

1. 创建功能分支：`git checkout -b feature/wpXXX-name`
2. 实现功能，遵循 IM 规范
3. 编写集成测试（不允许 Mock）
4. 确保 `mvn clean install` 通过
5. 提交 PR

### 编码规范

- **DDD 分层**：Domain 保持 POJO（禁止 Spring/MyBatis 注解）
- **命名规则**：见 `docs/architecture/M2-AI-Domain-Foundation/M2-001-Architecture-Laws/`
- **真实实现**：禁止 TODO / Mock / 空方法
- **SLF4J 日志**：禁止使用 System.out
- **Lombok**：使用 @Data / @Builder /