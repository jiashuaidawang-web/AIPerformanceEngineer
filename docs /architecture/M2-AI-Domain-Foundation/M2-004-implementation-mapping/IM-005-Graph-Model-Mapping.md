IM-005-Graph-Model-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-005 Graph Database Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   严格对齐存储划分：图数据库只存Relationship关系数据，不存其他任何业务数据，符合Law-000 Single Source of Truth
   贴合图数据库特性：重点支撑多跳关系查询、路径分析、拓扑投影等场景，这类查询在MySQL中需要多次JOIN，图数据库毫秒级即可返回
   无冗余设计：节点只存常用查询属性，Resource完整属性在MySQL，避免数据冗余，降低一致性维护成本
2. 存储范围
   存储内容	对应世界模型	选择图数据库的原因
   Resource节点（仅常用属性）	M2-007 Unified Resource Model	作为关系的端点，仅存储查询需要的核心属性，完整属性在MySQL
   Relationship边（全量属性）	M2-008 Relationship Model	支持多跳查询、路径分析、环路检测，是图数据库最核心的应用场景
   不存储其他数据	-	Observation、Evidence、Knowledge等数据在对应存储，不越界
3. 数据模型映射
   3.1 节点（Resource节点）
   仅存储关系查询需要的核心属性，完整属性通过resource_id关联MySQL查询：

属性名	类型	业务用途
resource_id	String	主键，和MySQL的resource_id一一对应
resource_name	String	关系查询时直接返回名称，避免回表MySQL
resource_type	String	关系查询时按类型过滤
resource_category	String	关系查询时按类别过滤
3.2 边（Relationship边）
全量存储M2-008 Relationship Model的所有属性：

属性名	类型	业务用途
relationship_id	String	主键，和MySQL的relationship_id一一对应
source_resource_id	String	关系起点，对应Resource节点ID
target_resource_id	String	关系终点，对应Resource节点ID
relationship_type	String	关系类型：DEPENDENCY/CALL/CONTAIN/COMMUNICATE/MANAGE
direction	String	关系方向：UNIDIRECTIONAL/BIDIRECTIONAL
status	String	关系状态：ACTIVE/INACTIVE/BREAKING
properties	Map(String, String)	关系扩展属性，存权重、超时时间等个性化信息
created_at	DateTime	关系创建时间
updated_at	DateTime	关系最后更新时间
4. 核心查询场景
   图数据库重点支撑以下高频业务场景，性能比MySQL提升10~100倍：

上下游关系查询：查询某个资源的所有直接/间接上下游依赖，用于故障影响面评估
路径分析：查询两个资源之间的所有调用链路，用于故障传播分析、问题溯源
环路检测：检测资源之间的循环依赖，避免变更时出现级联故障
拓扑投影：作为M2-009 Topology Model的数据源，动态生成不同层级的拓扑视图
关系聚合：统计某类关系的数量、分布，用于架构治理
5. 约束
   图数据库只存关系数据，禁止存储Observation、Evidence、Knowledge等其他业务数据
   Resource节点只存4个核心属性，完整属性通过resource_id回查MySQL，避免数据冗余，符合Law-000
   禁止直接修改/删除历史关系数据，关系变更通过新增关系记录+更新status状态实现，保留全量历史记录
   所有关系操作必须通过RelationshipRepository接口，禁止业务直接访问图数据库
   新增/变更关系时，必须同步更新MySQL resource表的relationship_ids字段，保证两个存储的数据一致性
6. 验收标准
   ✔ 存储划分清晰，只存关系数据，无越界存储
   ✔ 节点属性设计无冗余，完整数据只在MySQL，符合Single Source of Truth
   ✔ 核心查询场景覆盖所有关系类业务需求，多跳查询性能达标
   ✔ 数据一致性约束完善，MySQL和图数据库的联动机制可落地
   ✔ 所有设计符合三大基础法则和Constitution v1.0要求