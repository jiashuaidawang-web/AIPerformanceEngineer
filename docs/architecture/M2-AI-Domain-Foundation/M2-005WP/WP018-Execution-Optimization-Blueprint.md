# AI Performance Engineer

# WP018 Execution + Optimization Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation（收官 WP，闭环验证）

Priority: P0

DependsOn:

WP017 Recommendation Engine（输入来源）

M2-013 Optimization Model

IM-003 Persistence Mapping

IM-006 REST API Mapping

IM-010 Service Mapping

RequiredBy: 无（M2 收官）；M3 扩展 基础

EstimatedJavaFiles: 20

EstimatedWorkload: 3 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Execution + Optimization 引擎，让 Recommendation 可以被 执行（自动/手动 + 审批流程），并 验证 执行效果（通过 Observation 对比执行前后 → 是否真正优化变好 → 闭环更新 Knowledge），完成整个 AI 推理闭环。

核心解决问题：Recommendation 只是 建议，不改变世界。Execution 改变世界 → Optimization 验证 → New Observation → Knowledge Update。这是产品的 闭环价值（"我帮助企业改变了现实，并验证确实变好了"）。

Before：Recommendation 终 止于 建议，没有结果反馈

After：Recommendation → Execution（带上执行人 + 时间戳 + before/after 快照）→ 执行 → Observation（采集） → 对比 → Optimization（PASS / FAIL）→ 更新 Knowledge（successRate）→ 新的 Observation 开端

核心原则：Recommendation Does Not Change Reality（执行 才会 改变世界，Recommendation 只是 建议）；Optimization 来源于 真实 Execution；Knowledge 持续成长（Evolution，不是 Overwrite）；Reality Before Memory（相信 现实 数据，不是 自己相信 自己）。

Scope：

MVP 支持：Execution 领域模型（含 executionId / recommendationId / executor + executionType / Timestamp / before/after snapshot + rollback）+ ExecutionService（手动/半自动/自动 + 执行记录 + 回滚）+ OptimizationService（效果评估 + 前后对比 + 统计检验）+ Repository（MySQL 对齐 IM-003）+ 状态机 → EXECUTING → SUCCESS / FAILED → ROLLED_BACK + API + Knowledge 联动 + 执行报告生成

不包含：自动执行（Auto Execution，MVP 仅 手动 + 审批）；MCP Tool 调用；Kubernetes 自动操作（后续 M3 扩展）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：Execution 领域模型（对齐 M2-013：executionId / recommendationId / executor + type / startedAt / finishedAt / beforeSnapshot / afterSnapshot / improvementScore + rollback）

ExecutionService.execute() → Execution（带 beforeSnapshot + startedAt）

ExecutionService.complete() → 添加 afterSnapshot + improvementScore + status = SUCCESS / FAILED

ExecutionService.rollback() → 添加 rollback + status = ROLLED_BACK

OptimizationService.evaluate() → Optimization（对比 before/after Observation → 统计检验 → PASS / FAIL + improvement score）

OptimizationService.compareObservation(before, after) → ImprovementResult（各指标变化率 + 显著性验证）

Repository（MySQL save / findById / findByRecommendation / findByStatus / findByTimeRange + 状态机）

执行 报告生成（含 before/after 对比 数据可视化 基础）

Knowledge 联动（执行成功 → 更新 successRate）

    2.2 Technical Acceptance

必须满足：Repository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

Execution 落 MySQL（对齐 IM-003）

状态机合法流转：PENDING → EXECUTING → SUCCESS / FAILED → ROLLED_BACK（不允许 非法流转）

不可变（Immutable）- 执行完成 后不能修改，只能 回滚 + 新 beforeSnapshot 重新开始

执行前 快照 对齐 Observation（before Snapshot = Observation[L]；after Snapshot = Observation[L+Lindow]）；

优化 评分 improvementScore ∈ [0,100]（0=没变化，100=完全优化）

    2.3 Integration Acceptance

完整链路（AI World Evolution Chain 闭环）：

Knowledge → Recommendation → Approve → Execution → Observation采集 → Optimization 评估 → Knowledge 更新

---

## 3. Package List

com.aipe.execution

├── domain/                ← Execution + ExecutionType + ExecutionStatus + Repository + Optimization

├── application/           ← ExecutionService（执行编排 + 快照 + 状态机）+ OptimizationService（效果评估 + 统计检验）

├── infrastructure/        ← ExecutionRepositoryImpl + ExecutionPO + Mapper + Converter

└── api/                   ← ExecutionController + DTO + ExceptionHandler

---

## 4. Class List

    4.1 Execution（Domain 聚合根）

字段：executionId / recommendationId / executor（执行人）/ executionType（MANUAL / SEMI_AUTO / AUTO）/ status（PENDING / EXECUTING / SUCCESS / FAILED / ROLLED_BACK）/ beforeSnapshot（JSON 执行前 Observation 快照）/ afterSnapshot（JSON 执行后 Observation 快照）/ improvementScore（0~100）/ startedAt / finishedAt / rollbackInfo / createdAt / version

    4.2 ExecutionService（Application 核心）

核心方法：

- execute(ExecutionRequest) → Execution（带 beforeSnapshot）

- complete(ExecutionId) → 添加 afterSnapshot + improvementScore + 评估 Optimization

- rollback(ExecutionId) → 记录 rollback + status → ROLLED_BACK

- generateReport(ExecutionId) → ExecutionReport（含 before/after 对比 数据 + 可视化）

    4.3 OptimizationService（Application 核心）

核心方法：

- evaluate(Execution) → Optimization（对比 beforeSnapshot / afterSnapshot Observation → 统计检验 → PASS / FAIL + improvementScore）

- compareObservation(beforeObservations, afterObservations) → ImprovementResult（整体 变化率）

- statisticalTest(sampleBefore, sampleAfter) → Significance（简单 t - test 或 置信区间）

    4.4 ExecutionRepositoryImpl（Infrastructure）

职责：MySQL 实现 + save / findById / findByRecommendation / findByStatus / query

    4.5 ExecutionRepository（Domain 接口）

职责：save / findById / findByRecommendation / findByStatus / query / findByTimeRange

---

## 5. Method List + 物理文件 + MySQL Schema + API + Sequence + Constraints + Tests + Rowboat Rules

    5.1 Execution（Domain）

void validate() / boolean canExecute() / boolean canComplete() / boolean canRollback() / void markExecuting() / void markSuccess() / void markFailed() / void markRolledBack() / boolean isFinished() / double calculateImprovementScore()

    5.3 ExecutionService（Application）

Execution execute(ExecutionRequest req)（beforeSnapshot 采集 + PENDING → EXECUTING）

Execution complete(ExecutionId id)（afterSnapshot 采集 + evaluateOptimization + 触发 Knowledge successRate 更新）

Execution rollback(ExecutionId id)（rollback + status → ROLLED_BACK）ExecutionReport generateReport(ExecutionId id)（before/after 对比 数据可视化）

    5.4 OptimizationService（Application）

Optimization evaluate(Execution execution)（对比 before/after Observation → 统计检验 → PASS / FAIL）

ImprovementResult compareObservation(List<Observation> before, List<Observation> after)

StatisticalSignificance statisticalTest(List<Double> before, List<Double> after)（简化 t - test 或 置信区间）

    5.5 物理文件清单

aipe-execution/

├── pom.xml

├── src/main/java/com/aipe/execution/

│   ├── ExecutionApplication.java

│   ├── domain/ (Execution, ExecutionId, ExecutionType, ExecutionStatus, ExecutionRepository, RollbackInfo, ExecutionBuilder, ExecutionSpecification)

│   ├── application/ (ExecutionService, OptimizationService, SnapshotCollector, ImprovementCalculator, StatisticalTester)

│   ├── infrastructure/ (ExecutionRepositoryImpl, ExecutionPO, ExecutionMapper, ExecutionConverter)

│   └── api/ (ExecutionController, ExecutionDtoMapper, dto/, exception/)

    5.6 MySQL Schema（对齐 IM-003）

```sql
CREATE TABLE IF NOT EXISTS execution (
    id                  VARCHAR(64) PRIMARY KEY,
    recommendation_id   VARCHAR(64) NOT NULL COMMENT '来源 Recommendation',
    executor            VARCHAR(64) NOT NULL COMMENT '执行人（谁执行）',
    execution_type      VARCHAR(16) NOT NULL DEFAULT 'MANUAL' COMMENT 'MANUAL/SEMI_AUTO/AUTO',
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/EXECUTING/SUCCESS/FAILED/ROLLED_BACK',
    before_snapshot     JSON COMMENT '执行前 Observation 快照',
    after_snapshot      JSON COMMENT '执行后 Observation 快照',
    improvement_score   DOUBLE NOT NULL DEFAULT 0.0 COMMENT '优化评分 0~100',
    started_at          TIMESTAMP NULL COMMENT '开始时间',
    finished_at         TIMESTAMP NULL COMMENT '完成时间',
    rollback_info       JSON COMMENT '回滚信息',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1,
    INDEX idx_recommendation (recommendation_id),
    INDEX idx_status (status),
    INDEX idx_executor (executor),
    INDEX idx_time_range (started_at, finished_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

对齐检查

IM-003 规范	本 WP 实现

execution_id PK	✅ id VARCHAR(64) PK

recommendation_id	✅ recommendation_id（NOT NULL）

executor	✅ executor（NOT NULL - 谁执行）

execution_type	✅ execution_type（MANUAL/SEMI_AUTO/AUTO）✅ 对齐 M2-013 执行类型

status	✅ status 状态机 合法流转

before_snapshot	✅ before_snapshot（JSON）

after_snapshot	✅ after_snapshot（JSON）

improvement_score	✅ improvement_score 0~100

started_at / finished_at	✅ started_at / finished_at

rollback_info	✅ rollback_info（JSON）

version 乐观锁	✅ version

落 MySQL	✅ 对齐 IM-003

---

## 6. API 设计

方法	路径	说明	入参

POST	/api/v1/executions	创建 + 启动 Execution（beforeSnapshot 采集）	ExecutionRequest（recommendation_id + executor）

POST	/api/v1/executions/{id}/complete	完成 Execution（afterSnapshot 采集 + Optimization 评估）	id

POST	/api/v1/executions/{id}/rollback	回滚 Execution	id + reason

GET	/api/v1/executions/{id}	查询 Execution + 状态 + before/after	id

GET	/api/v1/executions?recommendation_id=xxx	按 Recommendation 查询	recommendation_id

GET	/api/v1/executions?status=SUCCESS&executor=xxx	按 状态 + 执行人 查询	status / executor

GET	/api/v1/executions/{id}/report	生成执行 报告（before/after 对比）	id

GET	/api/v1/executions/{id}/optimization	查询 Optimization 评估结果	id

---
## 7. Sequence Diagram

推荐 → 执行 → 评估 → 知识更新（完整闭环）

user ExecutionController ExecutionService OptimizationService ObservationRepository KnowledgeRepository MySQL

---

## 8. State Machine（Execution 合法流转）

```
PENDING
  ↓
EXECUTING
  ↓          ↓
SUCCESS     FAILED
  ↓
ROLLED_BACK（可选）
```

| 流转 | 是否允许 |
|------|---------|
| PENDING → EXECUTING | ✅ |
| EXECUTING → SUCCESS | ✅ |
| EXECUTING → FAILED | ✅ |
| SUCCESS → ROLLED_BACK | ✅（可选 - 发现问题 + 回滚）|
| FAILED → ROLLED_BACK | ✅（可选）|
| PENDING → SUCCESS | ❌（必须先 EXECUTING）|
| SUCCESS → EXECUTING | ❌ |
| ROLLED_BACK → * | ❌（已结束 + 重新开始 新 Execution）|

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Execution 引用合法 Recommendation（recommendationId + status=APPROVED）

必须实现：beforeSnapshot 执行前 采集（Observation[L] → JSON快照）

必须实现：afterSnapshot 执行后 采集（Observation[L+Window] → JSON快照 + 时间对齐）

必须实现：OptimizationService 评估（对比 before/after + 统计检验 + improvementScore 0~100 + PASS/FAIL）

必须实现：Repository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

必须实现：Execution 落 MySQL（对齐 IM-003）

必须实现：不可变（Immutable）- 完成 后 不能修改 + 回滚 = 新记录

必须实现：状态机 合法流转

    9.2 Forbidden

禁止：无 Recommendation（推荐才能执行）

禁止：非法状态流转（PENDING → SUCCESS）

禁止：修改已完成的 Execution（不可变 - 回滚 = 新记录）

禁止：Execution 落 ClickHouse（Persistence Law-002）

禁止：Optimization 统计检验 用 简单阈值（需用 t-test 或 置信区间）

    9.3 Engineering Rules

必须：Execution 前 validate()（recommendationId + executor + status=APPROVED）

必须：API 返回 ExecutionResponse（含 executionId + status + improvementScore）

必须：统一返回 ApiResponse

必须：集成测试必须覆盖 完整链路 + before/after 对比 + 状态机 非法流转 拒绝

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-execution -DskipTests

    10.2 Test Scenario

完整 AI 推理 闭环 验证

- Knowledge v1 + Resource → Recommendation（WP017，PENDING）→ 审批 → APPROVED → 触发 Execution ✓

- Execution 执行（采集 beforeSnapshot → 执行 → 采集 afterSnapshot）

- Optimization 评估（对比 前后 Observation + 统计检验 + improvementScore 0~100 + PASS/FAIL）

- Knowledge 更新（PASS → successRate 提升；FAIL → successRate 降低）

- 状态机 非法流转（PENDING → SUCCESS）→ 拒绝

- 回滚（SUCCESS → ROLLED_BACK + 记录 rollbackInfo）

- 执行 报告生成（含 before/after 对比 数据）

    10.3 Verification

检查：Execution 落 MySQL（不是 ClickHouse）

检查：beforeSnapshot + afterSnapshot 均非空（JSON 序列化 成功）

检查：improvementScore 计算合理（0~100 范围）

检查：Optimization PASS/FAIL 基于 统计检验（不是硬编码）

检查：Knowledge successRate 更新正确（PASS 提升；FAIL 降低）

检查：状态机 非法流转 被拒绝

检查：Execution 不可变（更新 + version 冲突 → 拒绝）

    10.4 Expected Result

返回 Execution（status=SUCCESS + improvementScore > 0）返回 Optimization 评估（PASS + improvementScore + before/after 对比）

返回 Knowledge successRate 更新后 数据

符合 M2-013：Execution + Optimization 闭环 成立

符合 AI World Evolution Chain：Knowledge → Recommendation → Execution → Observation → Knowledge

    10.5 Troubleshooting

Execution 无法创建：检查 Recommendation status（必须 APPROVED）+ recommendationId 引用合法

Optimization 评估失败：检查 before/after Snapshot 数据完整性 + 统计检验 参数

Knowledge successRate 不更新：检查 successRate 计算公式（PASS/FAIL 计数 / 总数）

状态机 非法流转 报错：检查 Service 层 方法 + 状态机 前置 校验

---
## 11. Rowboat Coding Rules

Rowboat 必须遵守：不得新增一级 Package（com.aipe.execution）；Execution 必须引用 APPROVED Recommendation；Execution 落 MySQL（对齐 IM-003）；不可变（Immutable）；状态机 合法流转；Repository 接口 Domain + 返回 Domain；所有新增类 必须放入规定 Package；任何违反 Persistence Law / Gateway Law / Architecture Law 的代码必须拒绝提交

Status: Draft

---

## 附录：M2 完成度检查清单

M2（AI Domain Foundation + Implementation）完成条件：

WP011 Resource ✅（已完成）

WP012 Observation Engine 🔲

WP013 Relationship + Topology 🔲

WP014 Timeline Engine 🔲

WP015 Evidence Engine 🔲

WP016 Knowledge Engine 🔲

WP017 Recommendation Engine 🔲

WP018 Execution + Optimization 🔲

M2 全部完成 → 进入 M3（生产化 + 自己公司验证 + B端 产品化）

M3 方向（预判，WP018 后 确认）

M3.1 自己公司部署验证（接入真实业务 + 收集验证数据）

M3.2 产品化（部署工具 + 前端 + 安全 RBAC + 审计）

M3.3 企业集成（CMDB/Jenkins/K8s）

M3.4 客户交付（标杆客户 + 技术支持 + SLA）