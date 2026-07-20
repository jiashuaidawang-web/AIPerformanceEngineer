IM-010-Service-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-010 Service Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   职责清晰：Service层只做跨领域业务编排，不做数据访问、不做业务规则校验，数据访问调用Repository，规则校验调用领域Validator，严格对齐分层架构
   贴合业务：每个Service都对应明确的核心业务场景，覆盖资源管理、AI推理、优化执行、查询分析全流程，无冗余接口
   边界明确：不越权操作其他模块数据，不直接调用其他域的Service，跨域逻辑通过领域事件驱动，符合Law-000 Single Source of Truth
   事务可控：核心状态流转操作添加事务注解，控制事务范围，避免大事务，保证数据一致性
2. 核心职责映射
   职责	对应业务场景	核心作用
   资源全生命周期编排	资源创建、状态变更、关系绑定	串联资源校验、存储、缓存、事件发布全流程
   AI推理全流程编排	证据生成、知识沉淀、推荐生成	串联数据采集、AI引擎调用、结果存储、事件触发全流程
   优化执行全流程编排	推荐审批、执行、验证、回滚	串联推荐生成、执行调用、效果验证、知识更新全流程
   查询分析编排	资源拓扑、时间线、指标查询	聚合多存储层数据，返回统一格式的查询结果
3. 核心Service设计
   所有Service接口定义在com.aipe.service包下，实现类定义在com.aipe.service.impl包下，严格对齐IM-006 API和IM-002领域服务定义。

3.1 ResourceManageService（资源管理编排）
方法	业务逻辑	调用链路
createResource	1. 调用ResourceValidator校验参数合法性 2. 校验同类型下资源名唯一 3. 调用ResourceRepository写入MySQL 4. 写入Redis缓存 5. 发布ResourceCreatedEvent领域事件	Validator→Repository→Cache→EventPublisher
updateResourceStatus	1. 校验资源是否存在 2. 校验状态流转是否合法 3. 调用ResourceRepository更新状态和版本 4. 删除Redis缓存 5. 发布ResourceStatusChangedEvent领域事件	Validator→Repository→Cache→EventPublisher
bindRelationship	1. 校验源/目标资源是否存在 2. 校验关系类型是否合法 3. 调用RelationshipRepository写入图数据库 4. 更新MySQL resource表的relationship_ids字段	Validator→GraphRepository→MysqlRepository
3.2 EvidenceProcessService（AI推理编排）
方法	业务逻辑	调用链路
generateEvidence	1. 接收资源ID和证据类型 2. 调用ObservationRepository查询关联的Observation数据 3. 调用RelationshipRepository查询关联的关系数据 4. 调用TimelineRepository查询关联的时间线数据 5. 调用AI引擎生成Evidence 6. 调用EvidenceRepository写入MySQL 7. 发布EvidenceGeneratedEvent领域事件	Repository→AI Engine→Repository→EventPublisher
verifyEvidence	1. 校验证据是否存在 2. 校验当前状态是否为PENDING 3. 更新证据状态和验证报告 4. 如果验证通过，触发KnowledgeProcessService沉淀知识	Validator→Repository→KnowledgeProcessService
3.3 KnowledgeProcessService（知识沉淀编排）
方法	业务逻辑	调用链路
depositKnowledge	1. 校验证据是否验证通过 2. 调用AI引擎生成标准化知识 3. 调用KnowledgeRepository写入MySQL 4. 发布KnowledgeDepositedEvent领域事件	Validator→AI Engine→Repository→EventPublisher
updateSuccessRate	1. 接收执行结果 2. 查询关联的Knowledge 3. 重新计算历史成功率 4. 更新Knowledge的成功率字段	Repository→Repository
3.4 OptimizationProcessService（优化执行编排）
方法	业务逻辑	调用链路
generateRecommendation	1. 接收资源ID 2. 查询匹配的Knowledge列表 3. 调用AI引擎生成推荐列表 4. 过滤高风险低成功率的推荐 5. 调用RecommendationRepository写入MySQL	Repository→AI Engine→Repository
executeOptimization	1. 校验推荐是否存在、状态是否为APPROVED 2. 调用ExecutionService执行操作 3. 写入ExecutionRecord到MySQL 4. 写入执行日志到ClickHouse 5. 触发效果验证流程	Validator→ExecutionService→Repository→ClickHouseRepository
rollbackOptimization	1. 校验执行记录是否存在、状态是否为SUCCESS 2. 调用ExecutionService执行回滚操作 3. 更新执行记录状态和回滚原因 4. 触发成功率更新流程	Validator→ExecutionService→Repository→KnowledgeProcessService
3.5 QueryService（查询分析编排）
方法	业务逻辑	调用链路
queryTopology	1. 接收资源ID和查询深度 2. 调用RelationshipRepository查询多跳关系 3. 调用ResourceRepository查询关联资源的基础属性 4. 拼接生成TopologyView返回	GraphRepository→MysqlRepository
queryTimeline	1. 接收资源ID和时间范围 2. 调用ObservationRepository查询时间范围内的Observation 3. 调用TimelineEventRepository查询时间范围内的事件 4. 按时间排序拼接返回	ClickHouseRepository→ClickHouseRepository
queryMetricTrend	1. 接收资源ID、指标名称、时间范围 2. 调用MetricDataRepository查询时序指标 3. 按聚合间隔计算平均值、最大值、最小值 4. 返回趋势数据	ClickHouseRepository
4. 约束
   禁止在Service中直接操作数据库，所有数据访问必须通过Repository接口
   禁止Service跨域直接调用其他域的Service，跨域逻辑通过领域事件异步解耦
   事务注解只添加在状态流转、写操作的方法上，查询方法不加事务，避免性能浪费
   禁止在Service中做参数校验、规则判断，所有校验逻辑调用对应域的Validator实现
   新增Service必须对齐本规范，禁止随意新增冗余方法
5. 性能要求
   资源类操作响应时间<500ms，支持每秒1000次并发操作
   查询类操作响应时间<2秒，支持每秒5000次并发查询
   跨存储聚合查询响应时间<3秒，支持每秒1000次并发查询
6. 验收标准
   ✔ 覆盖所有核心业务场景，无冗余接口，每个方法都有明确的业务用途
   ✔ 分层清晰，不越权操作其他模块数据，符合三大基础法则要求
   事务控制合理，数据一致性有保障，无大事务风险
   性能满足业务峰值需求，可支撑大规模集群业务场景
   约束清晰，可落地开发，无技术风险