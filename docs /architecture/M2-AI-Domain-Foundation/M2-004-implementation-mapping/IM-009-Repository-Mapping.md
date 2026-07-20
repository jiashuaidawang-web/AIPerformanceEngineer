IM-009-Repository-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-009 Repository Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   职责单一：Repository只负责数据访问封装，不做任何业务逻辑处理，严格隔离领域层和存储层，符合依赖倒置原则
   对齐存储划分：每个Repository对应明确的存储介质，不跨存储操作，严格对齐IM-003/004/005的存储归属规则
   无冗余：仅实现领域层需要的数据访问方法，不实现规范外的冗余接口，Rowboat可直接落地
   对齐法则：所有设计严格遵循Law-000 Single Source of Truth，每个数据源只有唯一的Repository入口
2. 核心职责映射
   职责	对应存储	核心作用
   封装存储访问细节	MySQL/ClickHouse/Graph/Redis	给领域层提供统一的数据访问接口，领域层不感知具体存储实现
   数据格式转换	所有存储	将存储层的原始数据转换为领域实体，将领域实体转换为存储层格式
   存储约束落地	所有存储	统一实现逻辑删除过滤、乐观锁校验、写入批次控制等通用规则
3. Repository映射表
   所有接口定义在com.aipe.domain.{实体包}.repository包下，实现类定义在com.aipe.infrastructure.storage.{存储包}包下，严格对齐IM-002领域实体定义：

Repository接口	实现类	对应存储	职责说明
ResourceRepository	MysqlResourceRepository	MySQL	resource表的CRUD，支持按类型、状态、名称查询
EvidenceRepository	MysqlEvidenceRepository	MySQL	evidence表的CRUD，支持按资源ID、类型、状态查询
KnowledgeRepository	MysqlKnowledgeRepository	MySQL	knowledge表的CRUD，支持按类型、EvidenceID查询
VerificationRecordRepository	MysqlVerificationRecordRepository	MySQL	verification_record表的CRUD，支持按对象类型、对象ID查询
RecommendationRepository	MysqlRecommendationRepository	MySQL	recommendation表的CRUD，支持按资源ID、状态查询
ExecutionRecordRepository	MysqlExecutionRepository	MySQL	execution_record表的CRUD，支持按推荐ID、状态查询
ObservationRepository	ClickhouseObservationRepository	ClickHouse	observation表的写入、按时间范围/资源ID查询
TimelineEventRepository	ClickhouseTimelineEventRepository	ClickHouse	timeline_event表的写入、按时间范围/资源ID查询
ExecutionLogRepository	ClickhouseExecutionLogRepository	ClickHouse	execution_log表的写入、按执行ID查询
MetricDataRepository	ClickhouseMetricDataRepository	ClickHouse	metric_data表的写入、按时间范围/资源ID聚合查询
RelationshipRepository	GraphRelationshipRepository	图数据库	Relationship的CRUD、上下游关系查询、路径查询
CacheRepository	RedisCacheRepository	Redis	Resource、Topology等高频查询数据的缓存读写
4. 实现规则
   4.1 通用规则
   所有MySQL Repository的查询方法必须默认添加is_deleted = 0条件，禁止返回已删除数据
   所有MySQL Repository的更新方法必须校验version字段，版本不一致抛出OptimisticLockException，提示并发冲突
   所有ClickHouse Repository禁止实现更新、删除方法，只支持写入和查询操作，符合时序数据不可变要求
   所有图数据库Repository禁止实现非关系类的查询，只支持节点/边的CRUD、关系路径查询
   所有Repository接口不依赖其他Repository的领域实体，避免循环依赖，跨存储查询在Service层聚合实现
   禁止直接拼接SQL，必须使用MyBatis-Plus等ORM框架，防止SQL注入
   4.2 性能规则
   ClickHouse Repository的写入方法必须支持批量写入，单次批量写入不少于1000条，禁止单条写入
   ClickHouse Repository的查询方法必须强制要求传入时间范围条件，禁止全表扫描
   Redis缓存的过期时间默认5分钟，资源状态变更、关系变更时必须主动删除对应缓存，保证数据一致性
   图数据库Repository的路径查询默认最大深度为5层，避免深查导致性能问题
5. 约束
   禁止在Repository中实现业务逻辑，只做数据访问的封装，所有业务逻辑在Service层实现
   禁止业务层直接调用Repository，必须通过对应域的Service接口调用
   禁止Repository跨存储操作，比如MySQL Repository不能操作ClickHouse数据
   禁止Repository返回存储层的原始数据结构，必须转换为IM-002定义的领域实体返回
   新增Repository必须对齐本规范，禁止随意新增接口方法
6. 验收标准
   ✔ 所有领域实体都有对应的Repository映射，接口和实现分离，符合依赖倒置原则
   ✔ 每个Repository的职责清晰，对应明确的存储介质，无越权操作，符合Law-000要求
   ✔ 通用规则覆盖所有存储类型，满足数据一致性、安全性要求
   ✔ 性能规则可落地，满足海量时序数据写入/查询、高频缓存访问的性能要求
   ✔ 约束清晰，无冗余设计，Rowboat可直接落地开发