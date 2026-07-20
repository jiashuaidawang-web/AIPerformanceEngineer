# AI Performance Engineer

# WP017 Recommendation Engine Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP016 Knowledge Engine

IM-003 Persistence Mapping

RequiredBy:

WP018 Execution + Optimization（Recommendation 引用）

EstimatedJavaFiles: 18

EstimatedWorkload: 3 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Recommendation Engine（推荐引擎），把 Knowledge 转化为针对具体 Resource 的优化建议（Recommendation）。Recommendation 不改变世界（Recommendation != Execution），只是建议；但可以生成 执行计划 交给 Execution 执行。

核心解决问题：Knowledge 是 通用经验 ，Recommendation 是 经验应用于具体 Resource 的 建议。WP017 解决 经验到推荐 + 优先级 + 执行计划。

Before：Knowledge 无法 落地为 操作建议，需要 人工 理解并操作

After：Knowledge 自动 生成针对 具体 Resource 的 Recommendation（内容 + 优先级 + 置信度 + 预期效果），支撑 Execution 或 人工执行

核心原则：Recommendation 只是建议，不改变世界；Analytics Law-001（Query 不修改数据）；Architecture Law-007（Controller 只做协议转换）。

Scope：

MVP 支持：Recommendation 领域模型（含 recommendationId / knowledgeId / targetResourceId / 内容 / priority / confidence / 执行计划 + status）+ RecommendationEngine.generate(Knowledge, Resource) → Recommendation + 优先级排序（置信度 × 紧急度 × 难度）+ 执行计划 + RecommendationRepository（MySQL）+ 查询 API + 可视化 + 追踪 + 审批 + 转 Execution（可选）

不包含：自动执行（WP018）；LLM 推理（rule-based only）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：Recommendation 领域模型（对齐 M2-013 Schema）

RecommendationEngine.generate(Knowledge, Resource) → Recommendation（自动匹配 applicableConditions → 生成 具体建议）

Recommendation.calculatePriority() → HIGH / MEDIUM / LOW（置信度 × 紧急度 × 难度）

Recommendation.generateExecutionPlan() → 执行计划 步骤 + 回滚方案

RecommendationRepository（MySQL save / findById / findByResource / findByStatus / query + 状态机 APPROVED / REJECTED / PENDING / EXECUTED）

Recommendation 落 MySQL（对齐 IM-003）

查询 /api/v1/recommendations?resource_id=xxx + 审批 API /api/v1/recommendations/{id}/approve + /reject

    2.2 Technical Acceptance

必须满足：Repository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

Recommendation 落 MySQL（对齐 IM-003）

Recommendation 状态机：PENDING → APPROVED → EXECUTED（合法流转）

不可变（Immutable）- 拒收 + 生成新 Recommendation

    2.3 Integration Acceptance

完整链路：

Knowledge → RecommendationEngine.generate() → Recommendation（PENDING）→ 人工/系统 审批 → APPROVED → 转 Execution（WP018）或 人工执行

---

## 3. Package List

com.aipe.recommendation

├── domain/                ← Recommendation + RecommendationStatus + Priority + Repository + RecommendationBuilder

├── application/           ← RecommendationEngine + PriorityCalculator + ExecutionPlanGenerator

├── infrastructure/        ← RecommendationRepositoryImpl + RecommendationPO + Mapper + Converter

└── api/                   ← RecommendationController + DTO + ExceptionHandler

---

## 4. Class List

    4.1 Recommendation（Domain 聚合根）

字段：recommendationId / knowledgeId / targetResourceId / title / description / priority（HIGH/MEDIUM/LOW）/ confidence / expectedOutcome / executionPlan / rollbackPlan / status（PENDING/APPROVED/REJECTED/EXECUTED）/ createdAt / updatedAt / version

    4.2 RecommendationEngine（Application 核心）

核心方法：

- generateFromKnowledge(Knowledge, ResourceId) → Recommendation（从 Knowledge 生成针对 Resource 的建议）

- calculatePriority(Recommendation) → Priority

- generateExecutionPlan(Recommendation) → 执行步骤 + 回滚方案

- recommendBatch(List<Knowledge>, ResourceId) → List<Recommendation>（批量 + 排序）

    4.3 RecommendationRepositoryImpl（Infrastructure）

职责：MySQL + findById / findByResource / findByStatus / query / save / updateStatus

---

## 5. Method List + 物理文件 + MySQL Schema + API + Sequence + Constraints + Tests + Rowboat Rules

    5.1 Recommendation（Domain）

void validate() / boolean isActionable() / boolean canApprove() / void approve() / void reject() / void markExecuted() / LocalDateTime getCreatedAt()

    5.2 RecommendationEngine（Application）

Recommendation generateFromKnowledge(Knowledge knowledge, ResourceId resourceId)

Priority calculatePriority(Recommendation recommendation)

ExecutionPlan generateExecutionPlan(Recommendation recommendation)

List<Recommendation> recommendBatch(List<Knowledge> knowledges, ResourceId resourceId)

    5.3 RecommendationRepositoryImpl（Infrastructure）

Recommendation save(Recommendation recommendation)

Optional<Recommendation> findById(RecommendationId id)

List<Recommendation> findByResource(ResourceId resourceId)

List<Recommendation> findByStatus(RecommendationStatus status)

List<Recommendation> query(RecommendationQuery query)

    5.4 物理文件清单

aipe-recommendation/

├── pom.xml

├── src/main/java/com/aipe/recommendation/

│   ├── RecommendationApplication.java

│   ├── domain/ (Recommendation, RecommendationId, RecommendationStatus, Priority, ExecutionPlan, RecommendationRepository, RecommendationBuilder, RecommendationSpecification)

│   ├── application/ (RecommendationEngine, PriorityCalculator, ExecutionPlanGenerator)

│   ├── infrastructure/ (RecommendationRepositoryImpl, RecommendationPO, RecommendationMapper, RecommendationConverter)

│   └── api/ (RecommendationController, RecommendationDtoMapper, dto/, exception/)

    5.5 MySQL Schema（对齐 IM-003）

```sql
CREATE TABLE IF NOT EXISTS recommendation (
    id                  VARCHAR(64) PRIMARY KEY,
    knowledge_id        VARCHAR(64) NOT NULL COMMENT '来源 Knowledge（对齐 Law-001）',
    target_resource_id  VARCHAR(64) NOT NULL COMMENT '目标 Resource',
    title               VARCHAR(256) NOT NULL,
    description         TEXT,
    priority            VARCHAR(16) NOT NULL DEFAULT 'MEDIUM' COMMENT 'HIGH/MEDIUM/LOW',
    confidence          DOUBLE NOT NULL DEFAULT 50.0,
    expected_outcome    TEXT COMMENT '预期效果',
    execution_plan      JSON COMMENT '执行计划',
    rollback_plan       JSON COMMENT '回滚方案',
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/EXECUTED',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1,
    INDEX idx_knowledge (knowledge_id),
    INDEX idx_target_resource (target_resource_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

    5.6 API 设计

方法	路径	说明

POST	/api/v1/recommendations/generate	从 Knowledge 触发推荐 生成

GET	/api/v1/recommendations/{id}	查询 Recommendation

GET	/api/v1/recommendations?resource_id=xxx	按 Resource 查询

GET	/api/v1/recommendations?status=PENDING&priority=HIGH	按 状态 + 优先级 查询

POST	/api/v1/recommendations/{id}/approve	审批 通过

POST	/api/v1/recommendations/{id}/reject	审批 拒绝

POST	/api/v1/recommendations/{id}/execute	标记 已执行（转 Execution + 对接 WP018）

    5.7 Implementation Constraints

Must: Knowledge 来源 + targetResourceId + priority + executionPlan；Recommendation 状态机合法流转；落 MySQL；Repository 接口 Domain + 返回 Domain

Forbidden: Recommendation 直接执行（必须审批 + WP018 执行）；无 Knowledge；无 targetResourceId；落 ClickHouse

    5.8 Test Scenarios

Knowledge → 生成 Recommendation（PENDING + priority + executionPlan）；优先级计算（HIGH priority + confidence >= 80）；审批 流转（PENDING → APPROVED / REJECTED）；Illegal Recommendation（无 Knowledge + 无 Resource）→ 拒绝；状态机 非法流转（PENDING → EXECUTED）→ 拒绝

    5.9 Rowboat Rules

不得新增一级 Package；Knowledge 必填；targetResourceId 必填；落 MySQL；不可变；Repository 接口 Domain + 返回 Domain；所有新增类 必须放入规定 Package