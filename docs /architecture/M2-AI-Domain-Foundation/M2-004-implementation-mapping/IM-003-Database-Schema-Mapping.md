IM-003-MySQL-Database-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-003 MySQL Database Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   无冗余：每张表都对应明确的核心业务场景，不建用不到的表
   贴业务：每个字段都有明确的业务用途，不搞花里胡哨的冗余字段
   可落地：单库单表，DDL直接可执行，满足当前及未来3年的业务规模
   对齐法则：所有设计严格遵循三大基础法则和Constitution v1.0
2. 存储划分
   存储	存放数据类型	对应业务场景
   MySQL	核心业务实体、元数据、状态流转类数据，需要强一致性、事务保障	资源管理、AI推理、知识沉淀、优化执行、效果验证
   ClickHouse	海量运行时时序数据，只追加不修改，按时间范围查询	指标采集、时间线回放、日志查询
3. 核心表结构设计
   所有表统一包含created_at、updated_at、version三个基础字段，所有字段非空，必须有默认值，单库单表，无分库分表。

3.1 resource（核心资源表）
字段名	类型	约束	业务用途
resource_id	varchar(64)	PK，UUID格式	唯一标识所有IT对象（应用、服务、Redis、主机等）
resource_name	varchar(128)	非空，同类型唯一	资源的名称，比如订单服务、用户Redis集群
resource_type	varchar(32)	非空，普通索引	资源类型，枚举：APPLICATION/SERVICE/JVM/REDIS/MYSQL/LINUX/K8S/API/MQ
resource_category	varchar(32)	非空	资源类别，枚举：BUSINESS（业务）/INFRA（基础设施）/PLATFORM（平台）
resource_status	varchar(32)	非空，普通索引	资源状态，枚举：RUNNING/STOPPED/MAINTENANCE/UNKNOWN
labels	json	非空，默认[]	业务标签，存部门、业务线、负责人等信息，用于分组查询
attributes	json	非空，默认{}	资源扩展属性，存IP、端口、集群规格等个性化信息
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
version	int	非空，默认1	乐观锁版本号，避免并发修改冲突
唯一索引：uk_resource_name_type (resource_name, resource_type)（同类型下资源名唯一）
普通索引：idx_resource_type_status (resource_type, resource_status)（高频按类型、状态查资源）

3.2 evidence（AI推理证据表）
字段名	类型	约束	业务用途
evidence_id	varchar(64)	PK，UUID格式	唯一标识AI推理出的证据
evidence_type	varchar(32)	非空，普通索引	证据类型，枚举：PERFORMANCE（性能）/DEPENDENCY（依赖）/DEPLOYMENT（部署）/BUSINESS（业务）
title	varchar(256)	非空	证据标题，比如「订单服务JVM堆内存使用率过高」
description	text	非空	AI生成的完整解释内容
root_resource_id	varchar(64)	非空，普通索引	证据关联的根资源ID
observation_ids	json	非空	引用的Observation ID列表，支撑证据的事实来源
relationship_ids	json	非空	引用的Relationship ID列表，支撑依赖关系推理
timeline_id	varchar(64)	普通索引	引用的Timeline ID，支撑时间线分析
confidence	int	非空，默认0	AI计算的可信度，0~100分
reasoning_steps	json	非空	AI推理的步骤列表，支撑可解释性
status	varchar(32)	非空，普通索引	证据状态，枚举：PENDING（待验证）/VERIFIED（已验证）/INVALID（无效）
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
version	int	非空，默认1	乐观锁版本号
普通索引：idx_evidence_root_type (root_resource_id, evidence_type, status)（高频按资源查证据）

3.3 knowledge（已沉淀知识表）
字段名	类型	约束	业务用途
knowledge_id	varchar(64)	PK，UUID格式	唯一标识已验证的知识
title	varchar(256)	非空	知识标题，比如「Redis集群内存不足扩容方案」
knowledge_type	varchar(32)	非空，普通索引	知识类型，枚举：PERFORMANCE/DEPENDENCY/DEPLOYMENT/BUSINESS/OPTIMIZATION
evidence_id	varchar(64)	非空，唯一索引	来源Evidence ID，一个知识只对应一个证据
verification_id	varchar(64)	非空，普通索引	关联的验证记录ID
confidence	int	非空，默认0	验证后的最终可信度，0~100分
applicable_conditions	text	非空	该知识的适用条件，比如「Redis内存使用率持续超过80%超过1小时」
recommendation	text	非空	知识对应的推荐解决方案
success_rate	decimal(5,2)	非空，默认0.00	历史推荐成功率，0~100，用于后续推荐的优先级排序
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
version	varchar(16)	非空，默认1.0.0	语义版本号，知识迭代时升级版本
普通索引：idx_knowledge_type_evidence (knowledge_type, evidence_id)（高频按类型查知识）

3.4 verification_record（效果验证记录表）
字段名	类型	约束	业务用途
verification_id	varchar(64)	PK，UUID格式	唯一标识验证记录
target_type	varchar(32)	非空，普通索引	验证对象类型，枚举：EVIDENCE（证据）/RECOMMENDATION（推荐）/EXECUTION（执行）
target_id	varchar(64)	非空，普通索引	验证对象ID
verification_type	varchar(32)	非空	验证方式，枚举：PRESSURE_TEST（压测）/GRAY（灰度）/PRODUCTION（生产）/ANALYSIS（人工分析）
verification_params	json	非空，默认{}	验证参数，比如压测的并发数、持续时间等
verification_result	varchar(32)	非空	验证结果，枚举：PASS（通过）/FAIL（失败）/INCONCLUSIVE（无结论）
verification_report	text	非空	验证的详细报告内容
operator	varchar(64)	非空	验证操作人
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
普通索引：idx_verification_target (target_type, target_id)（高频按对象查验证记录）

3.5 recommendation（优化推荐表）
字段名	类型	约束	业务用途
recommendation_id	varchar(64)	PK，UUID格式	唯一标识推荐条目
knowledge_id	varchar(64)	非空，普通索引	来源Knowledge ID
title	varchar(256)	非空	推荐标题，比如「扩容订单服务JVM堆内存到8G」
description	text	非空	推荐详细描述
target_resource_id	varchar(64)	非空，普通索引	目标资源ID
expected_benefit	text	非空	预期效果，比如「接口响应时间降低30%」
risk_level	varchar(32)	非空	风险等级，枚举：LOW（低）/MEDIUM（中）/HIGH（高）
status	varchar(32)	非空，普通索引	推荐状态，枚举：PENDING（待审批）/APPROVED（已通过）/REJECTED（已拒绝）/EXECUTED（已执行）
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
version	int	非空，默认1	乐观锁版本号
普通索引：idx_recommendation_knowledge_target (knowledge_id, target_resource_id, status)（高频查某个资源待执行的推荐）

3.6 execution_record（优化执行记录表）
字段名	类型	约束	业务用途
execution_id	varchar(64)	PK，UUID格式	唯一标识执行记录
recommendation_id	varchar(64)	非空，普通索引	来源Recommendation ID
executor	varchar(64)	非空	执行人
execution_time	datetime(3)	非空，普通索引	执行时间
execution_params	json	非空，默认{}	执行参数，比如扩容的配置参数
result	text	非空	执行结果描述
metric_before	varchar(64)	非空	优化前的指标值，比如接口RT=200ms
metric_after	varchar(64)	非空	优化后的指标值，比如接口RT=140ms
improvement	varchar(64)	非空	提升比例，比如「降低30%」
status	varchar(32)	非空，普通索引	执行状态，枚举：RUNNING（执行中）/SUCCESS（成功）/FAILED（失败）/ROLLED_BACK（已回滚）
rollback_reason	varchar(512)	默认null	回滚原因，回滚时必填
created_at	datetime(3)	非空，普通索引	创建时间
updated_at	datetime(3)	非空	更新时间
普通索引：idx_execution_recommendation (recommendation_id, status)（高频查某个推荐的执行结果）

4. 完整DDL（可直接执行）
   sql
   复制
   CREATE TABLE `resource` (
   `resource_id` varchar(64) NOT NULL,
   `resource_name` varchar(128) NOT NULL,
   `resource_type` varchar(32) NOT NULL,
   `resource_category` varchar(32) NOT NULL,
   `resource_status` varchar(32) NOT NULL,
   `labels` json NOT NULL DEFAULT '[]',
   `attributes` json NOT NULL DEFAULT '{}',
   `created_at` datetime(3) NOT NULL,
   `updated_at` datetime(3) NOT NULL,
   `version` int NOT NULL DEFAULT '1',
   PRIMARY KEY (`resource_id`),
   UNIQUE KEY `uk_resource_name_type` (`resource_name`, `resource_type`),
   KEY `idx_resource_type_status` (`resource_type`, `resource_status`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `evidence` (
`evidence_id` varchar(64) NOT NULL,
`evidence_type` varchar(32) NOT NULL,
`title` varchar(256) NOT NULL,
`description` text NOT NULL,
`root_resource_id` varchar(64) NOT NULL,
`observation_ids` json NOT NULL,
`relationship_ids` json NOT NULL,
`timeline_id` varchar(64) DEFAULT NULL,
`confidence` int NOT NULL DEFAULT '0',
`reasoning_steps` json NOT NULL,
`status` varchar(32) NOT NULL,
`created_at` datetime(3) NOT NULL,
`updated_at` datetime(3) NOT NULL,
`version` int NOT NULL DEFAULT '1',
PRIMARY KEY (`evidence_id`),
KEY `idx_evidence_root_type` (`root_resource_id`, `evidence_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `knowledge` (
`knowledge_id` varchar(64) NOT NULL,
`title` varchar(256) NOT NULL,
`knowledge_type` varchar(32) NOT NULL,
`evidence_id` varchar(64) NOT NULL,
`verification_id` varchar(64) NOT NULL,
`confidence` int NOT NULL DEFAULT '0',
`applicable_conditions` text NOT NULL,
`recommendation` text NOT NULL,
`success_rate` decimal(5,2) NOT NULL DEFAULT '0.00',
`created_at` datetime(3) NOT NULL,
`updated_at` datetime(3) NOT NULL,
`version` varchar(16) NOT NULL DEFAULT '1.0.0',
PRIMARY KEY (`knowledge_id`),
UNIQUE KEY `uk_knowledge_evidence` (`evidence_id`),
KEY `idx_knowledge_type_evidence` (`knowledge_type`, `evidence_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `verification_record` (
`verification_id` varchar(64) NOT NULL,
`target_type` varchar(32) NOT NULL,
`target_id` varchar(64) NOT NULL,
`verification_type` varchar(32) NOT NULL,
`verification_params` json NOT NULL DEFAULT '{}',
`verification_result` varchar(32) NOT NULL,
`verification_report` text NOT NULL,
`operator` varchar(64) NOT NULL,
`created_at` datetime(3) NOT NULL,
`updated_at` datetime(3) NOT NULL,
PRIMARY KEY (`verification_id`),
KEY `idx_verification_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `recommendation` (
`recommendation_id` varchar(64) NOT NULL,
`knowledge_id` varchar(64) NOT NULL,
`title` varchar(256) NOT NULL,
`description` text NOT NULL,
`target_resource_id` varchar(64) NOT NULL,
`expected_benefit` text NOT NULL,
`risk_level` varchar(32) NOT NULL,
`status` varchar(32) NOT NULL,
`created_at` datetime(3) NOT NULL,
`updated_at` datetime(3) NOT NULL,
`version` int NOT NULL DEFAULT '1',
PRIMARY KEY (`recommendation_id`),
KEY `idx_recommendation_knowledge_target` (`knowledge_id`, `target_resource_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `execution_record` (
`execution_id` varchar(64) NOT NULL,
`recommendation_id` varchar(64) NOT NULL,
`executor` varchar(64) NOT NULL,
`execution_time` datetime(3) NOT NULL,
`execution_params` json NOT NULL DEFAULT '{}',
`result` text NOT NULL,
`metric_before` varchar(64) NOT NULL,
`metric_after` varchar(64) NOT NULL,
`improvement` varchar(64) NOT NULL,
`status` varchar(32) NOT NULL,
`rollback_reason` varchar(512) DEFAULT NULL,
`created_at` datetime(3) NOT NULL,
`updated_at` datetime(3) NOT NULL,
PRIMARY KEY (`execution_id`),
KEY `idx_execution_recommendation` (`recommendation_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
5. 业务约束
   所有写操作必须通过对应域的Repository接口，禁止其他模块直接访问表，遵循Law-000 Single Source of Truth
   所有状态变更必须更新version字段，乐观锁校验，避免并发冲突
   Evidence、Knowledge、Recommendation、Execution的状态流转必须符合AI World Evolution Chain，禁止跳状态
   所有JSON字段必须校验合法性，不允许存储非法格式数据
6. 验收标准
   ✔ 所有表都对应明确的核心业务场景，无冗余表、无冗余字段
   ✔ 字段设计完全贴合Rowboat采集、AI推理、知识沉淀、优化执行、效果验证的核心流程
   ✔ DDL可直接执行，单库单表，满足当前业务规模
   ✔ 索引覆盖所有高频查询场景，无全表扫描风险
   ✔ 所有设计符合三大基础法则和Constitution v1.0要求
   确认IM-003的内容没有问题后，我们继续编写IM-004-ClickHouse-Mapping.md。