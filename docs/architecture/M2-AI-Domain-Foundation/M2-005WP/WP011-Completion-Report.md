# WP011 Unified Resource Model — 完成报告

| 字段 | 值 |
|------|-----|
| **WP 编号** | WP011 |
| **名称** | Unified Resource Model |
| **状态** | ✅ 已完成 |
| **Milestone** | M2.5 → M3 过渡 |
| **优先级** | P0（所有后续 WP 的基石）|
| **代码行数** | ~4100 行（32 Java 文件）|
| **完成日期** | 2026-07-20 |

---

## 1. 完成目标

建立 AI Performance Engineer 的**统一资源抽象模型**，将所有 IT 对象（物理机、应用、服务、中间件、数据库、集群等）统一抽象为 `Resource`，解决资源业务归属、分类管理、关系映射的问题，彻底摒弃传统的机器维度视角，从业务域维度统一管理资源。

核心价值：让 AI 能基于**业务域维度**分析性能问题，而不是零散的机器指标。

---

## 2. 架构概览

采用 DDD 严格分层架构（对齐 IM-001 Package Mapping）：

```
com.aipe.resource
├── domain/                ← 纯 POJO（Resource + 值对象 + 枚举 + 工厂 + 接口）零框架依赖
├── application/           ← 事务编排（唯一事务 Owner）
├── infrastructure/        ← 持久化（PO + Mapper + RepositoryImpl + Converter）
├── api/                   ← REST（Controller + DTO + Mapper + ExceptionHandler）
└── config/                ← 配置（DataSource / MyBatis Plus / Migration）
```

### 依赖方向（对齐 IM-001 / IM-011）

```
API ↓ Application ↓ Domain ↓ Repository ↓ Infrastructure（MySQL）
```

禁止跨层引用，禁止循环依赖。

---

## 3. 文件清单

### Domain 层（9 文件）— 纯 POJO，零框架依赖

| 文件 | 职责 |
|------|------|
| `Resource.java` | 聚合根，含业务方法（validate / belongsTo / transitionStatus / updateInfo / setAttribute / addLabel）|
| `ResourceId.java` | 值对象（不可变，UUID，禁止 String/Long id）|
| `ResourceName.java` | 值对象（不可变，长度限制 256）|
| `ResourceType.java` | 枚举（APPLICATION/SERVICE/CLUSTER/DATABASE/REDIS/MQ/JVM/KAFKA...）|
| `ResourceCategory.java` | 枚举（BUSINESS/INFRA/PLATFORM）|
| `ResourceStatus.java` | 枚举（RUNNING/STOPPED/MAINTENANCE/UNKNOWN + canTransitionTo 规则）|
| `ResourceFactory.java` | 工厂（create / reconstruct，分别用于新建和从 DB 重建）|
| `ResourceRepository.java` | 仓储接口（save/update/findById/findByBusinessSystem/...）|
| `ResourceSpecification.java` | 规格校验（validateForCreate/validateForUpdate/isValidTransition）|

### Infrastructure 层（5 文件）

| 文件 | 职责 |
|------|------|
| `ResourcePO.java` | 持久化对象（MyBatis Plus `@TableName("resource")`，`@Version` 乐观锁）|
| `ResourceMapper.java` | Mapper 接口（自定义 SQL：按业务系统/类型/状态/父 ID 查询）|
| `ResourceRepositoryImpl.java` | 仓储实现（PO↔Domain 转换，返回 Domain 对象）|
| `ResourceConverter.java` | Resource ↔ ResourcePO 双向转换 |
| `JsonConverter.java` | Map ↔ JSON String 转换（labels/attributes）|

### Application 层（5 文件）

| 文件 | 职责 |
|------|------|
| `ResourceLifecycleManager.java` | 生命周期管理（创建/更新/删除/状态流转/绑定业务域），唯一事务 Owner |
| `ResourceDiscoveryService.java` | 资源发现（接收 Connector/Agent 上报，幂等创建/更新）|
| `ResourceBindingService.java` | 关系绑定（父子关系，WP011 用 MySQL，后续联动图数据库）|
| `ResourceQueryService.java` | 查询服务（按 ID/业务系统/类型/状态/父 ID 查询）|
| `ResourceValidator.java` | 校验器（创建/更新/状态流转/业务系统名校验）|

### API 层（7 文件）

| 文件 | 职责 |
|------|------|
| `ResourceController.java` | REST API（POST/GET/PUT/DELETE /api/v1/resources）|
| `ResourceReportController.java` | 资源上报端点（POST /api/v1/resources/discover + batch）|
| `GlobalExceptionHandler.java` | 全局异常处理（400/409/500 统一响应）|
| `ResourceRequest.java` | 创建/更新请求 DTO（@Valid 校验）|
| `ResourceResponse.java` | 响应 DTO |
| `ResourceReport.java` | 资源上报 DTO（Connector → Backend）|
| `ApiResponse.java` | 统一响应包装（code/message/requestId/timestamp/data）|
| `ResourceDtoMapper.java` | DTO ↔ Domain 映射（纯转换，无业务逻辑）|

### Config 层（5 文件）

| 文件 | 职责 |
|------|------|
| `DataSourceConfig.java` | Druid 数据源配置（@ConfigurationProperties）|
| `DatabaseMigrationConfig.java` | 自动数据库迁移（Spring DataSourceInitializer + Java 索引创建）|
| `DruidConfig.java` | Druid 监控 Servlet + Filter |
| `MyBatisPlusConfig.java` | MyBatis Plus 乐观锁拦截器 |
| `ResourceApplication.java` | Spring Boot 启动类 |

---

## 4. 数据库设计

### MySQL `resource` 表（对齐 IM-003 Persistence Mapping）

| 列名 | 类型 | 说明 |
|------|------|------|
| `resource_id` | VARCHAR(64) PK | 业务主键（UUID）|
| `resource_name` | VARCHAR(256) | 资源名称 |
| `resource_type` | VARCHAR(32) | 资源类型（对齐 ResourceType 枚举）|
| `resource_category` | VARCHAR(32) | 资源分类（BUSINESS/INFRA/PLATFORM）|
| `resource_status` | VARCHAR(16) | 资源状态（RUNNING/STOPPED/MAINTENANCE/UNKNOWN）|
| `parent_resource_id` | VARCHAR(64) | 父资源 ID（支持层级结构）|
| `business_system` | VARCHAR(128) | 业务系统（必填，禁止游离资源）|
| `cluster` | VARCHAR(128) | 集群标识 |
| `namespace` | VARCHAR(128) | 命名空间 |
| `environment` | VARCHAR(64) | 环境标识（prod/staging/test）|
| `version` | INT | 乐观锁版本号 |
| `labels` | JSON | 业务标签 |
| `attributes` | JSON | 扩展属性 |
| `created_at` | TIMESTAMP | 创建时间 |
| `updated_at` | TIMESTAMP | 更新时间 |
| `deleted` | TINYINT | 逻辑删除标识 |

### 索引

- `idx_resource_business_system` (business_system)
- `idx_resource_type` (resource_type)
- `idx_resource_status` (resource_status)
- `idx_resource_created` (created_at)
- `idx_resource_parent` (parent_resource_id)

---

## 5. API 设计（对齐 IM-006 REST API Mapping）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/resources` | 创建资源 |
| GET | `/api/v1/resources/{id}` | 查询单个资源 |
| PUT | `/api/v1/resources/{id}` | 更新资源 |
| DELETE | `/api/v1/resources/{id}` | 删除资源（逻辑删除）|
| GET | `/api/v1/resources?business_system=xxx` | 列表查询（支持按业务系统/类型/状态过滤）|
| PATCH | `/api/v1/resources/{id}/status?status=xxx` | 更新资源状态 |
| POST | `/api/v1/resources/discover` | 资源发现（Connector/Agent 上报）|
| POST | `/api/v1/resources/discover/batch` | 批量资源发现 |

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
| 400 | 400 | 参数校验失败 / 无 businessSystem |
| 409 | 409 | 非法状态流转 |
| 500 | 500 | 内部错误 |

---

## 6. 验收结果

| 场景 | 期望 | 实际 | 结果 |
|------|------|------|------|
| 创建 3 个资源（订单服务/MySQL/Redis）| 成功，状态 RUNNING，版本 1 | ✅ | PASS |
| 查询业务域返回 3 个资源 | 返回 3 个 | 返回 3 个 | PASS |
| 状态流转 RUNNING→MAINTENANCE | 状态 MAINTENANCE，版本 2 | ✅ | PASS |
| 状态流转 MAINTENANCE→RUNNING | 状态 RUNNING，版本 3 | ✅ | PASS |
| 非法流转 STOPPED→MAINTENANCE | 返回 409 | 返回 409 | PASS |
| 无 businessSystem | 返回 400 | 返回 400 | PASS |
| `mvn clean package` | BUILD SUCCESS | ✅ | PASS |
| Spring Boot 启动 + MySQL 连接 | 成功 | ✅ | PASS |

---

## 7. 核心原则遵循

| 原则 | 实现方式 |
|------|---------|
| **Law-001** Everything Is Resource | 所有操作走 `ResourceLifecycleManager` 统一入口 |
| **Law-002** Observation Belongs to Resource | resourceId 必填（后续 WP012 关联）|
| **Gateway Law-001** Repo returns Domain | RepositoryImpl 通过 Converter 返回 Domain，禁止返回 PO |
| **Gateway Law-002** One Aggregate One Repository | Resource → ResourceRepository（唯一）|
| **Gateway Law-003** Interface in Domain, Impl in Infrastructure | 接口在 domain，实现在 infrastructure |
| **Domain Law-001** One Concept One Object | Domain 层只有 `Resource` 一个对象 |
| **Orchestration Law-001** App唯一事务 Owner | `@Transactional` 只在 LifecycleManager |
| **Architecture Law-007** Controller Is Protocol Translator | Controller 只做 HTTP↔DTO 转换，无业务逻辑 |

---

## 8. 与 M1 的集成点

| M1 模块 | 对接方式 | 状态 |
|---------|---------|------|
| `ObservationController` | 后续 WP012 改造 `targetResource` → `resourceId` 关联 Resource | 待 WP012 |
| `Connector/Agent` | 通过 `POST /api/v1/resources/discover` 上报资源信息 | ✅ 端点已就绪 |
| `Storage` 模块 | 替换原 `ResourceRepository` 空实现，使用新 DDD Repository | 待 WP012 |

---

## 9. 已知限制 & 后续 WP 计划

| 限制 | 后续 WP 解决 |
|------|-------------|
| 关系绑定仅用 MySQL parent_resource_id | WP013 联动图数据库（Neo4j）|
| Observation→Resource 关联未建立 | WP012 Observation Engine |
| 无自动资源发现（仅手动上报）| WP012+ 自动发现 |
| 无拓扑动态生成 | WP013 Topology Model |
| 无 AI 推理能力 | WP015~018 Evidence/Knowledge/Recommendation |

---

## 10. 如何运行

```bash
# 1. 编译
mvn clean package -pl aipe-resource -DskipTests

# 2. 启动（连接真实 MySQL 124.223.220.245:3306/aipe_metadata）
java -jar aipe-resource/target/aipe-resource-1.0.0-SNAPSHOT.jar

# 3. 测试 API
curl -X POST http://localhost:8082/api/v1/resources \
  -H "Content-Type: application/json" \
  -d '{"resourceName":"订单服务","resourceType":"APPLICATION","businessSystem":"测试系统"}'

# 4. 查询
curl "http://localhost:8082/api/v1/resources?business_system=测试系统"
```

---

## 11. 总结

WP011 是 M2.5 → M3 的关键转折点。它建立了整个 Domain Layer 的基础：

- **Resource 聚合根** 是所有后续 WP（Observation/Relationship/Timeline/Evidence/Knowledge）的基石
- **DDD 分层架构** 保证了代码的可维护性和可扩展性
- **真实 MySQL 集成** 验证了架构可行性
- **完整的 API 层** 为前端和 AI Engine 提供了统一入口

完成 WP011 后，项目正式进入连续编码冲刺阶段。下一份文档：**WP012 Observation Engine**。
