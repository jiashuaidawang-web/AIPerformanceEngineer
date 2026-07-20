IM-006-REST-API-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-006 REST API Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   贴合业务：每个API都对应明确的核心业务场景，无冗余接口，覆盖Rowboat采集、AI推理、知识沉淀、优化执行、效果验证全流程
   严格RESTful：统一路径前缀/api/v1，语义清晰，HTTP方法符合资源操作语义
   权限可控：分角色权限，不同角色可访问的API范围不同，符合Constitution Article 12权限管控要求
   统一规范：请求/响应格式统一，错误码清晰，支持版本兼容，符合三大基础法则要求
2. 通用约定
   2.1 统一响应格式
   json
   复制
   {
   "code": 0, // 0成功，非0失败，具体含义见错误码表
   "message": "success", // 提示信息
   "data": {} // 业务数据，对象/列表/空
   }
   2.2 通用错误码
   错误码	含义
   0	成功
   1001	参数校验失败
   1002	资源不存在
   1003	权限不足
   1004	状态流转非法
   1005	操作冲突（乐观锁版本不一致）
   5000	服务内部错误
   2.3 通用规则
   认证：所有API必须携带Authorization: Bearer {token}请求头，token由统一认证服务颁发
   分页：列表类API统一支持pageNum（默认1）、pageSize（默认20，最大100）参数，响应返回total总条数
   过滤：查询类API支持对应实体的属性作为过滤参数，多条件为AND关系
   排序：查询类API支持sortField、sortOrder（asc/desc）参数，默认按创建时间倒序
   版本兼容：API路径中的版本号v1为大版本，后续升级不兼容时升级到v2，旧版本保留至少6个月
3. 核心API设计
   所有API严格对齐IM-002领域服务接口，每个API标注对应的世界模型和遵循的架构法则。

3.1 Resource API（对应M2-007，遵循Law-001）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/resources	POST	创建资源	OPERATOR/ADMIN	resource_name、resource_type、resource_category、labels、attributes	Resource实体全部字段
/api/v1/resources/{resourceId}	PUT	更新资源基础信息	OPERATOR/ADMIN	resource_name、labels、attributes	Resource实体全部字段
/api/v1/resources/{resourceId}/status	PATCH	更新资源状态	OPERATOR/ADMIN/AI	resource_status	Resource实体全部字段
/api/v1/resources/{resourceId}	GET	查询资源详情	所有角色	无	Resource实体全部字段
/api/v1/resources	GET	分页查询资源列表	所有角色	resource_type、resource_category、resource_status、keyWord（名称模糊查）、pageNum、pageSize	{total, list: [Resource实体]}
3.2 Observation API（对应M2-006，遵循Law-002）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/observations/batch	POST	批量上报采集数据	AI/CONNECTOR/AGENT	[{resource_id, observation_type, metric_name, metric_value, unit, source, labels}]	成功/失败列表
/api/v1/observations	GET	分页查询Observation	所有角色	resource_id、observation_type、metric_name、startTime、endTime、pageNum、pageSize	{total, list: [Observation实体]}
/api/v1/observations/metric	GET	查询指标时序数据	所有角色	resource_id、metric_name、startTime、endTime、interval（聚合间隔：1m/5m/1h）	[{timestamp, avg_value, max_value, min_value}]
3.3 Relationship API（对应M2-008，遵循Law-000）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/relationships	POST	创建资源关系	OPERATOR/ADMIN/AI	source_resource_id、target_resource_id、relationship_type、direction、properties	Relationship实体全部字段
/api/v1/relationships	GET	分页查询关系列表	所有角色	resource_id（查和该资源相关的所有关系）、relationship_type、status、pageNum、pageSize	{total, list: [Relationship实体]}
/api/v1/relationships/downstream	GET	查询资源的所有下游依赖	所有角色	resource_id、depth（查询深度，默认3）	下游资源树结构
/api/v1/relationships/path	GET	查询两个资源之间的调用路径	所有角色	source_resource_id、target_resource_id、max_depth（默认5）	[路径列表]
3.4 Timeline API（对应M2-010，遵循Constitution Article 7）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/timelines	GET	查询资源时间线	所有角色	resource_id、startTime、endTime	Timeline实体全部字段
/api/v1/timelines/replay	GET	回放指定时间窗口的时间线	所有角色	resource_id、startTime、endTime、speed（回放速度：1x/2x/5x）	按时间排序的事件流
3.5 Evidence API（对应M2-011，遵循Constitution Article 8）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/evidences	POST	生成AI推理证据	AI	resource_id、evidence_type、observation_ids、relationship_ids、timeline_id	Evidence实体全部字段
/api/v1/evidences/{evidenceId}/verify	PATCH	验证证据有效性	OPERATOR/ADMIN	verification_result（PASS/FAIL）、verification_report	Evidence实体全部字段
/api/v1/evidences	GET	分页查询证据列表	所有角色	resource_id、evidence_type、status、pageNum、pageSize	{total, list: [Evidence实体]}
3.6 Knowledge API（对应M2-012，遵循Constitution Article 9）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/knowledge	GET	分页查询知识列表	所有角色	knowledge_type、keyWord、pageNum、pageSize	{total, list: [Knowledge实体]}
/api/v1/knowledge/recommend	GET	查询资源的推荐优化方案	所有角色	resource_id、knowledge_type	[Recommendation实体]
3.7 Execution API（对应M2-013，遵循Constitution Article 10）
路径	方法	业务描述	权限	请求参数	响应字段
/api/v1/executions	POST	执行优化推荐	OPERATOR/ADMIN	recommendation_id、execution_params	ExecutionRecord实体全部字段
/api/v1/executions/{executionId}/rollback	POST	回滚优化操作	OPERATOR/ADMIN	rollback_reason	ExecutionRecord实体全部字段
/api/v1/executions	GET	分页查询执行记录	所有角色	recommendation_id、status、pageNum、pageSize	{total, list: [ExecutionRecord实体]}
/api/v1/executions/{executionId}/result	GET	查询执行结果	所有角色	无	OptimizationResult实体全部字段
4. 权限控制
   角色	权限范围
   ADMIN	所有API的增删改查权限
   OPERATOR	资源/关系/证据/执行的状态变更、创建、回滚权限，所有查询权限
   VIEWER	只读权限，只能调用GET类查询API
   AI	内部调用权限，仅可调用Observation上报、Resource状态更新、Evidence生成、Relationship创建四类API
   CONNECTOR/AGENT	仅可调用Observation批量上报API
5. 约束
   所有API必须严格对齐IM-002的领域服务接口，禁止实现规范外的冗余接口
   状态变更类API必须校验当前状态是否允许流转，禁止跳状态操作
   写操作API必须记录审计日志，包含操作人、操作时间、操作内容、影响范围
   所有API的响应字段必须和IM-002的领域实体字段保持一致，避免字段冗余或缺失
   权限校验必须在API网关层统一实现，不允许在业务代码中单独校验
6. 验收标准
   ✔ 覆盖所有核心业务场景，无冗余接口，每个API都有明确的业务用途
   ✔ 严格符合RESTful规范，路径、HTTP方法、请求/响应格式统一
   ✔ 权限控制清晰，不同角色的访问权限明确，符合安全要求
   ✔ 所有API都标注了对应的世界模型和遵循的架构法则，无偏离
   ✔ 通用约定完善，支持版本兼容、分页、过滤、排序等通用能力，可落地开发