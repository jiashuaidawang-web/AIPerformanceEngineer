# AI Performance Engineer

# WP013 Relationship + Topology Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP011 Resource Domain

IM-005 Graph Mapping

IM-003 Persistence Mapping（Parent_storage=MySQL）

M2-008 Relationship Model

M2-009 Topology Model

RequiredBy:

WP015 Evidence Engine

WP018 Execution + Optimization

EstimatedJavaFiles: 30

EstimatedWorkload: 5 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Relationship（资源间关系）领域模型 和运行时 Domain，支持资源间的依赖、调用、部署等关系管理，并实现 Topology 投影引擎（从 Relationship 资源关系拓扑实时计算，不存储，每次查询生成）。

核心解决问题：Resource 不维护世界，世界是由 Relationship 连接出来的。这张蓝图把 Relationship 提升到正式 Domain Object，并赋予 Topology 投影能力。

Before：Resource 之间没有显式关系，无法回答“Redis 故障会影响谁”

After：Relationship 是 Domain Object（有 ID/类型/来源/置信度），Topology 实时投影

核心原则：Relationship Is First-Class Citizen（Architecture Law-005），它有自己的 ID/类型/置信度/生命周期。Topology 不是 Storage，是 Projection（Architecture Law-004），永远从 Relationship 查询生成。

Scope：

MVP 支持：Relationship 领域模型 + Repository（MySQL 实现）+ 查询 / 新建 / 删除 API + Topology 实时生成 + 邻居查询 + 上下游依赖查询 + 影响路径分析

不包含：Graph Database 存储（WP011 只定义 Repository 接口，用 MySQL 实现，后续 WP 可替换为 Neo4j）；关系自动发现（只从 API 或 Discovery 上报维护）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：

Relationship 领域模型（含 RelationshipId / 类型 / source / target / 置信度 / 来源 / 类型枚举）

Relationship 的新建 / 删除 / 查询（按 source/target/类型）

Topology 实时生成（指定根 Resource 生成影响拓扑 + 上下游依赖查询 + 邻居查询 + 最短路径查询）

支持层级拓扑查询（Business / Runtime / Infrastructure 视图，IM-005 4 种视图）

支持影响分析（单 Resource 故障会影响哪些 Resource）

支持可分析的 API（/api/v1/topology/impact?resource_id=xxx、/api/v1/topology/neighbors?resource_id=xxx）

    2.2 Technical Acceptance

必须满足：

Repository 接口在 Domain（RelationshipRepository），实现在 Infrastructure（RelationshipRepositoryImpl）

Repository 返回 Domain，禁止返回 ResultSet

Relationship 使用 MySQL 对齐 IM-005（relationship 表）

Topology 不存储（Architecture Law-004），每次查询实时从 Relationship 计算

邻接表查询性能 < 100ms（万级 Relationship）

支持图遍历：BFS / DFS / 上游 / 下游 / 邻居（最小实现 BFS 即可）

    2.3 Integration Acceptance

完整链路：

用户上报 Relationship → Backend 校验 + 写入 MySQL → Topology 时实时从 MySQL 查询 → 生成视图 → 返回

完整链路：

用户查询影响分析 → TopologyService 从 RelationshipRepository 加载边 → 图遍历 → 返回影响节点列表

---

## 3. Package List

com.aipe.relationship

├── domain/                ← Relationship + RelationshipType + RelationshipStatus + Repository 接口 + Topology 值对象

├── application/           ← RelationshipApplicationService（管理）+ TopologyService（投影）

├── infrastructure/        ← RelationshipRepositoryImpl + RelationshipPO + Mapper + Converter

└── api/                   ← RelationshipController + TopologyController + DTO + ExceptionHandler

Package Responsibility

Package	职责

domain	Relationship 聚合根 + 值对象（RelationshipType/Status/Direction）+ RelationshipRepository 接口 + Topology 值对象

application	RelationshipApplicationService（CRUD）+ TopologyService（投影构建 + 图遍历）

infrastructure	RelationshipRepositoryImpl（MySQL）+ RelationshipPO + RelationshipMapper + Converter

api	RelationshipController + RelationshipDtoMapper + TopologyController

---

## 4. Class List

    4.1 Relationship（Domain 聚合根）

Package: domain

职责：表示 Resource 之间的一条有向 关联，自身拥有独立 ID 和 生命周期

字段：

字段	类型	说明

relationshipId	RelationshipId	全局唯一标识（值对象）

relationshipType	RelationshipType	DEPENDS_ON / CALLS / RUNS_ON / BELONGS_TO / …（IM-005 Edge 枚举）

sourceResourceId	RelationshipId	源 Resource

targetResourceId	RelationshipId	目标 Resource

direction	RelationshipDirection	SINGLE / BIDIRECTIONAL

confidence	double	置信度（0~100，AI 或 Discovery 判定）

discoveredBy	String	发现来源（Connector / Discovery / 人工 / AI）

status	RelationshipStatus	ACTIVE / INACTIVE / ARCHIVED

labels	Map<String, String>	版本约束、合约类型等

 LocalDateTime discoveredAt	首次发现时间

 LocalDateTime updatedAt	最近更新时间

    4.2 RelationshipRepository（Domain 接口）

职责：Relationship 仓储接口（save / remove / findById / findBySource / findByTarget / findByType / findNeighbors）

    4.3 Topology（Domain 值对象）

职责：指定 Resource 的 拓扑视图（纯内存值对象，不存储）

字段：TopologyView（rootResourceId + nodes: List<ResourceNode> + edges: List<ResourceEdge> + nodeCount + edgeCount + builtAt）

    4.4 RelationshipApplicationService（Application）

职责：Relationship 业务编排（CRUD + 新建关系 + 删除关系 + 校验不引用不存在的 Resource）

    4.5 TopologyService（Application）

职责：从 RelationshipRepository 加载边 → 内存图遍历 → 返回 Topology 值对象

实现：BFS / DFS / 上游 / 下游 / 邻居 / 影响（Impact Analysis）/ 最短路径（Dijkstra 备用）

    4.6 RelationshipRepositoryImpl（Infrastructure）

职责：MySQL 实现 + Relationship ↔ Domain 转换

---

## 5. Method List

Relationship（Domain 业务方法）

// 校验 Relationship 是否合法（source + target + type 必填 + 不同 Resource）

void validate()

// 是否涉及指定 Resource

boolean involves(ResourceId resourceId)

// 是否是有向关系

boolean isDirected()

// 反转关系（source ↔ target）

Relationship reverse()

RelationshipApplicationService（Application）

// 新建 Relationship（source/target 必须已存在）

Relationship createRelationship(CreateRelationshipRequest req)

// 删除 Relationship

boolean removeRelationship(RelationshipId id)

// 查询 Resource 的所有 Relationships（source + target 双方）

List<Relationship> findRelationships(ResourceId resourceId)

// 查询 Resource 的上游依赖

List<Relationship> findUpstream(ResourceId resourceId)

Service 的核心方法

// 构建指定 Resource 的实时拓扑（对齐 M2-009 4 种视图）

TopologyView buildCurrent(ResourceId rootResourceId, RelationshipType... types)

// 构建指定 Resource 在历史时间点的拓扑

TopologyView buildAt(ResourceId rootResourceId, long timestamp, RelationshipType... types)

// 查询 Resource 的邻居（一度关系）

List<ResourceNode> queryNeighbors(ResourceId resourceId, int degree)

// 查询 Resource 的上下游依赖（全路径）

List<ResourceNode> queryDependencies(ResourceId resourceId, DependencyDirection direction)

// 影响分析：Resource 故障 → 影响哪些 Resource

ImpactResult queryImpact(ResourceId resourceId)

// 最短路径查询

List<PathNode> shortestPath(ResourceId from, ResourceId to)
    5.1 物理文件清单

aipe-relationship/

├── pom.xml

├── src/main/java/com/aipe/relationship/

│   ├── RelationshipApplication.java

│   ├── domain/

│   │   ├── Relationship.java

│   │   ├── RelationshipId.java

│   │   ├── RelationshipType.java          ← 10 种边类型（IM-005 Edge 枚举）

│   │   ├── RelationshipDirection.java

│   │   ├── RelationshipStatus.java

│   │   ├── RelationshipRepository.java

│   │   ├── RelationshipSpecification.java

│   │   ├── TopologyView.java

│   │   ├── ResourceNode.java

│   │   └── ResourceEdge.java

│   ├── application/

│   │   ├── RelationshipApplicationService.java

│   │   ├── RelationshipValidator.java

│   │   ├── TopologyService.java          ← 核心：BFS / DFS / 影响分析

│   │   └── GraphTraversal.java           ← 图遍历工具（BFS）

│   ├── infrastructure/

│   │   ├── RelationshipRepositoryImpl.java

│   │   ├── RelationshipPO.java

│   │   ├── RelationshipMapper.java

│   │   └── RelationshipConverter.java

│   └── api/
│       ├── RelationshipController.java

│       ├── TopologyController.java

│       ├── RelationshipDtoMapper.java

│       ├── dto/

│       │   ├── CreateRelationshipRequest.java

│       │   ├── RelationshipResponse.java

│       │   ├── TopologyResponse.java

│       │   └── ImpactResponse.java

│       └── exception/

│           └── RelationshipExceptionHandler.java

---

## 6. MySQL Schema（对齐 IM-005 / IM-003）

```sql
CREATE TABLE IF NOT EXISTS relationship (
    id              VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    relationship_type VARCHAR(32) NOT NULL COMMENT 'DEPENDS_ON/CALLS/RUNS_ON/...',
    source_resource_id VARCHAR(64) NOT NULL COMMENT '源 Resource',
    target_resource_id VARCHAR(64) NOT NULL COMMENT '目标 Resource',
    direction       VARCHAR(16) NOT NULL DEFAULT 'SINGLE' COMMENT 'SINGLE/BIDIRECTIONAL',
    confidence      DOUBLE NOT NULL DEFAULT 100.0 COMMENT '置信度 0~100',
    discovered_by   VARCHAR(64) NOT NULL DEFAULT 'MANUAL' COMMENT 'Discovery/AI/人工',
    status          VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE/ARCHIVED',
    labels          JSON COMMENT '扩展属性',
    discovered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_source (source_resource_id),
    INDEX idx_target (target_resource_id),
    INDEX idx_type (relationship_type),
    INDEX idx_status (status),
    FOREIGN KEY (source_resource_id) REFERENCES resource(resource_id),
    FOREIGN KEY (target_resource_id) REFERENCES resource(resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

对齐检查

IM-005 规范	本 WP 实现

10 种 Edge 类型枚举	✅ 全部支持

有向边	✅ direction 字段

置信度	✅ confidence 字段

来源追踪	✅ discovered_by 字段

独立生命周期	✅ status + discovered_at + updated_at

不进入 ClickHouse	✅ 只落 MySQL

禁止维护 Topology 表	✅ Topology 实时计算

---

## 7. API 设计

方法	路径	说明	入参

POST	/api/v1/relationships	新建 Relationship	CreateRelationshipRequest

GET	/api/v1/relationships/{id}	查询	id

DELETE	/api/v1/relationships/{id}	删除	id

GET	/api/v1/relationships?resource_id=xxx	查询 Resource 的关系	resource_id / type

GET	/api/v1/topology/current?resource_id=xxx	当前拓扑	root + types

GET	/api/v1/topology/neighbors?resource_id=xxx&degree=1	邻居查询	resource_id / degree

GET	/api/v1/topology/dependencies?resource_id=xxx&direction=downstream	依赖查询	resource_id / direction（upstream/downstream）

GET	/api/v1/topology/impact?resource_id=xxx	影响分析	resource_id

GET	/api/v1/topology/path?from=xxx&to=xxx	最短路径	from / to

---
## 8. Sequence Diagram

新建 Relationship

user RelationshipController RelationshipApplicationService RelationshipRepository MySQL

 关系查询 + Topology 投影

user TopologyController TopologyService RelationshipRepository MySQL

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Relationship 引用合法 Resource（source/target 必须存在 - 新建时校验）

必须实现：Relationship 类型 10 种枚举（IM-005）

必须实现：Repository 接口在 Domain + 实现在 Infrastructure + 返回 Domain

必须实现：Topology 值对象每次查询新建，不存储（Architecture Law-004）

必须实现：影响分析（BFS 从 source 遍历下游，返回所有 影响节点）

    9.2 Forbidden

禁止：Topology 建表 / 存储

禁止：Relationship 引用不存在的 Resource

禁止：Circular Relationship（A→B→C→A）（新建时检测）

禁止：RelationshipRepository 返回 Map / ResultSet

    9.3 Engineering Rules

必须：所有 Relationship 创建前 validate()

必须：api 返回 Domain（RelationshipResponse 必须含 relationshipId）

必须：confidence 默认 100.0（人工）/ 动态调整（AI 推导）

必须：统一返回 ApiResponse

必须：集成测试必须覆盖 + 邻居查询 + 影响分析

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-relationship -DskipTests

    10.2 Test Scenario

Order Service CALLS Inventory Service Order Service

Order Service DEPENDS_ON Redis Cluster

Inventory Service RUNS_ON Linux Host

Redis Cluster RUNS_ON Linux Host

Query Order Service → neighbors 返回 [Inventory Service, Redis Cluster]

Query Order Service → impact 返回 [Inventory Service, Redis Cluster, Linux Host]

Query Order Service → dependencies = downstream 返回 [Inventory Service, Redis Cluster]

Illegal Relationship（引用不存在的 Resource）→ 返回 400

Circular 依赖检测（A→B→C→A）→ 拒绝最后一个

    10.3 Verification

检查：Relationship 落 MySQL，source/target/resource_id 正确

检查：TopologyView 不存储，每次新建

检查：BFS 影响分析返回完整下游节点

检查：illegal Relationship 被拒绝，返回 400

    10.4 Expected Result

返回 Order Service 的 neighbors：[Inventory Service, Redis Cluster]

返回 Order Service 的 impact：[Inventory Service, Redis Cluster, Linux Host]

符合 Law-005：Relationship 有独立 ID + 类型 + 置信度 + 生命周期

    10.5 Troubleshooting

邻居查询慢：检查 source_resource_id + target_resource_id 联合索引

影响分析不完整：检查 BFS 实现（入边 + 出边视为无向）

Topology 重复 build：检查是否存在 status=INACTIVE 的 Relationship 需要过滤

---

## 11. Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package（com.aipe.relationship）

不得改变 Package 名称

Relationship 新建前必须校验 source/target Resource 存在（查询 ResourceRepository）

Relationship 是 undirected 或 directed，代码统一视为有向边，查询 neighbors 时同时查 source + target

TopologyView 不存储，每次 buildCurrent 调用新建返回

所有新增类必须放入规定 Package

任何违反 Gateway Law / Persistence Law / Architecture Law 的代码必须拒绝提交

Status: Draft