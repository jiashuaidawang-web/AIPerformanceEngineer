IM-007-AI-Engine-Mapping.md
AI Performance Engineer
#07-AI-Engine-Mapping.md

AI Performance Engineer
M2-004 Implementation Mapping
IM-007 AI Engine Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   严格对齐世界模型：仅实现M2-011 Evidence生成、M2-012 Knowledge沉淀、M2-013 Recommendation生成三个核心能力，不实现规范外的冗余AI功能
   边界清晰：AI引擎只做推理、学习、推荐，不直接操作业务数据、不直接执行优化操作，所有操作通过对应域的Service接口实现，符合Law-000
   完全可解释：所有AI输出必须包含完整的推理步骤、事实来源，可信度计算逻辑透明，符合Constitution Article 8/9/10要求
   可落地：不依赖大模型自训练，基于规则引擎+轻量机器学习模型实现，输入输出明确，Rowboat可直接编码
2. 核心能力映射
   AI能力	对应世界模型	输入	输出	触发条件
   Evidence生成引擎	M2-011 Evidence Model	资源相关Observation、Relationship、Timeline数据	带推理步骤、可信度的Evidence	新增Observation事件、资源状态变更事件
   Knowledge沉淀引擎	M2-012 Knowledge Model	验证通过的Evidence、历史执行结果	带适用条件、推荐方案的Knowledge	Evidence验证通过事件
   Recommendation生成引擎	M2-013 Execution & Optimization Model	Knowledge、资源当前状态、历史推荐效果	带风险等级、预期效果的Recommendation	资源状态变化事件、用户主动查询
3. Evidence生成引擎（M2-011）
   3.1 实现规则
   输入校验：必须传入至少1条Observation ID作为事实来源，禁止无事实依据生成Evidence
   推理逻辑：
   第一步：根据Observation数据识别异常指标，匹配预设的问题规则（如「接口RT>100ms且持续5分钟」匹配性能问题规则）
   第二步：查询关联的Relationship数据，分析上下游影响，定位根因资源
   第三步：查询对应Timeline数据，分析问题出现的时间规律，排除偶发因素
   第四步：生成推理步骤列表，每个步骤标注对应的支撑数据ID
   可信度计算：
   基础分60分：有完整的Observation支撑
   加分项：有Relationship支撑根因+10分、有Timeline证明非偶发+10分、同类问题历史验证成功率>80%+20分
   减分项：Observation数据不足3条-10分、无法定位根因-20分
   输出要求：必须填充observation_ids、relationship_ids、timeline_id、reasoning_steps、confidence字段，不允许为空
   3.2 约束
   禁止生成和其他Evidence完全重复的证据，生成前必须做去重校验
   可信度低于30分的Evidence直接标记为INVALID，不进入验证流程
   所有推理步骤必须对应实际存在的数据ID，禁止编造支撑依据
4. Knowledge沉淀引擎（M2-012）
   4.1 实现规则
   输入校验：必须传入验证结果为PASS的Evidence ID，禁止将未验证/验证失败的Evidence沉淀为Knowledge
   沉淀逻辑：
   第一步：提取Evidence中的根因、解决方案，生成标准化的知识标题和描述
   第二步：分析同类Evidence的历史出现场景，生成适用条件（如「Redis内存使用率持续>80%超过1小时」）
   第三步：提取推荐方案，初始化历史成功率为0，后续根据执行结果更新
   第四步：生成初始语义版本号1.0.0
   版本升级规则：
   同类Knowledge每新增3次验证通过的版本，小版本号+1（如1.0.0→1.0.1）
   推荐方案有重大调整时，大版本号+1（如1.0.0→2.0.0）
   成功率更新：每次关联的Recommendation执行完成后，根据执行结果更新成功率：
   成功率 = (历史成功次数 + 当前执行结果?1:0) / (历史总执行次数 + 1) * 100
   4.2 约束
   一个Evidence只能生成一条Knowledge，禁止重复沉淀
   适用条件必须明确可量化，禁止模糊描述（如「内存不足」→ 必须改为「内存使用率>80%」）
   版本升级必须记录变更说明，保留所有历史版本
5. Recommendation生成引擎（M2-013）
   5.1 实现规则
   输入校验：必须传入目标资源ID和对应的Knowledge ID，禁止无匹配Knowledge生成推荐
   生成逻辑：
   第一步：校验目标资源当前状态是否满足Knowledge的适用条件，不满足则不生成推荐
   第二步：根据Knowledge的推荐方案，生成具体的执行步骤、执行参数
   第三步：评估风险等级：
   LOW：只影响单台实例、可快速回滚的操作（如配置调整）
   MEDIUM：影响部分实例、回滚需要10分钟内的操作（如扩容）
   HIGH：影响全部实例、回滚需要30分钟以上的操作（如架构变更）
   第四步：计算预期效果，基于同类Knowledge的历史成功率估算
   优先级排序：同个资源的多条推荐按优先级排序：
   优先级 = 可信度 * 0.4 + 历史成功率 * 0.4 + (100 - 风险等级对应分值) * 0.2
   风险等级分值：LOW=0，MEDIUM=30，HIGH=60
   5.2 约束
   禁止生成风险等级为HIGH且历史成功率低于50%的推荐
   推荐方案必须包含具体的执行参数、回滚方案，禁止模糊描述
   执行完成后必须将执行结果反馈给Knowledge引擎，更新成功率
6. 通用约束
   AI引擎不直接操作任何数据库，所有数据读取通过对应域的Repository接口，所有结果写入通过对应域的Service接口
   AI引擎不直接触发优化执行，所有执行操作通过ExecutionService实现
   所有AI能力都通过IM-006对应的API对外暴露，不单独对外提供接口
   模型迭代、规则调整必须经过RFC流程审批，禁止直接修改上线
   AI引擎的推理过程日志必须存入ClickHouse的execution_log表，保留6个月，便于问题溯源
7. 性能要求
   Evidence生成响应时间：<3秒，支持每秒100个资源的并发推理
   Knowledge沉淀响应时间：<1秒
   Recommendation生成响应时间：<2秒，支持每秒200次查询
   规则引擎支持动态更新，更新后无需重启服务即可生效
8. 验收标准
   ✔ 三个AI能力完全对齐对应的世界模型，输入输出明确，无冗余功能
   ✔ 所有AI输出都有完整的推理步骤、事实来源，可信度计算逻辑透明，符合可解释要求
   边界清晰，不越权操作其他模块数据，符合三大基础法则要求
   性能满足业务峰值需求，推理过程可溯源
   所有约束可落地，无安全风险
