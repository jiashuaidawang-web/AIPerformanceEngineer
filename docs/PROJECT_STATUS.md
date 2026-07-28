# AI Performance Engineer — 项目进度报告

> **最后更新**: 2026-07-28  
> **状态**: M2 完成, Dashboard MVP 进行中

---

## 一、已完成工作

### 1. M2 Domain Engine (WP011-WP018) ✅

| WP | 模块 | 文件数 | 测试 | 状态 |
|----|------|--------|------|------|
| WP011 | aipe-resource (统一资源) | 32 | 7/7 PASS | ✅ |
| WP012 | aipe-observation (观察事实) | 28 | 7/7 PASS | ✅ |
| WP013 | aipe-relationship (关系+拓扑) | 34 | 6/6 PASS | ✅ |
| WP014 | aipe-timeline (时序构建) | 17 | 7/7 PASS | ✅ |
| WP015 | aipe-evidence (证据推理) | 22 | 5/5 PASS | ✅ |
| WP016 | aipe-knowledge (知识沉淀) | 20 | 6/6 PASS | ✅ |
| WP017 | aipe-recommendation (优化推荐) | 19 | 7/7 PASS | ✅ |
| WP018 | aipe-execution (执行+闭环) | 22 | 7/7 PASS | ✅ |

**关键成果**:
- 全量真实 MySQL + ClickHouse 集成测试通过 (无 Mock)
- 8 模块独立 Spring Boot 应用, 端口 8082-80889
- REST API 全覆盖 (对齐 IM-006)
- 工程法则 100% 落地 (Architecture Law / Persistence Law / Gateway Law)

### 2. 部署与文档 ✅

| 文档 | 路径 | 说明 |
|------|------|------|
| 部署方案 | `docs/deployment/SaaS-Agent-部署方案.md` | 414 行, 完整 B 端方案 |
| 全链路映射 | `docs/deployment/全链路组件-部署映射.md` | 222 行, 组件 × 采集方式 |
| 使用手册 | `docs/压测平台使用手册.md` | 258 行, 部署+压测+观测 |
| 压测报告 | `docs/reports/压测报告-压爆.md` | 250 并发实测 |
| 演示数据 | `aipe-dashboard/demo-data/` | 24 Resource + 完整链路 |
| 架构法则 | `docs/architecture/M2-001-Architecture-Laws/` | Law-000~002 |

### 3. 前端 Dashboard ✅ (部分)

| 模块 | 状态 | 说明 |
|------|------|------|
| 项目脚手架 | ✅ | Vue 3 + Vite 5 + Element Plus |
| 路由 + 布局 | ✅ | 8 个页面路由 |
| 概览页 | ✅ | 统计卡片 + ECharts 图表 |
| 资源列表 | ✅ | 表格 + 搜索/筛选 |
| 资源详情 | ⚠️ | 基础信息 OK, Timeline 图待接 |
| 时序查询 | ⚠️ | 表单 OK, 动态图表待完善 |
| 拓扑图 | ⚠️ | 静态示例, 待接真实数据 |
| 证据链 | ⚠️ | 列表 OK, 解释弹窗待接 |
| 知识库 | ⚠️ | 卡片视图 OK |
| 推荐 | ⚠️ | 列表 OK, 审批流待接 |
| 执行 | ⚠️ | 时间线 OK |
| 编译 | ✅ | `vite build` 通过 |

---

## 二、进行中 / 待完成

### P0: 前后端联调 (当前)

| 任务 | 状态 | 优先级 |
|------|------|--------|
| Resource 详情页 Timeline 图 | 🔴 待做 | P0 |
| 资源创建表单动态化 (按类型) | 🔴 待做 | P0 |
| 拓扑图接真实数据 | 🔴 待做 | P0 |
| Evidence 解释弹窗 | 🟡 待做 | P1 |
| Knowledge 关联 Evidence | 🟡 待做 | P1 |
| Recommendation 审批流 | 🟡 待做 | P1 |
| Execution 报告生成 | 🟡 待做 | P2 |

### P1: 功能完善

| 任务 | 说明 |
|------|------|
| 时序 Timeline 交互 | 缩放/拖拽/选时间范围 |
| 实时数据刷新 | WebSocket / 轮询 |
| 告警通知 | 企业微信/钉钉集成 |
| 多租户 | RBAC + 数据隔离 |
| 移动端适配 | 响应式布局 |

### P2: 性能与运维

| 任务 | 说明 |
|------|------|
| API Gateway | Kong / Nginx 统一入口 |
| 容器化部署 | Helm Chart (K8s) |
| 监控集成 | Prometheus + Grafana |
| 日志聚合 | ELK / Loki |
| CI/CD | GitLab CI / ArgoCD |

---

## 三、当前运行状态

### 本地启动 (开发模式)

```bash
# 启动全部 8 个 Domain Engine
bash start-all.sh

# 启动前端 (另一个终端)
cd aipe-dashboard
npm run dev
# → http://localhost:5173
```

### 访问地址

| 服务 | 地址 |
|------|------|
| Resource | http://localhost:8082 |
| Observation | http://localhost:8083 |
| Relationship | http://localhost:8084 |
| Timeline | http://localhost:8085 |
| Evidence | http://localhost:8086 |
| Knowledge | http://localhost:8087 |
| Recommendation | http://localhost:8088 |
| Execution | http://localhost:8089 |
| Dashboard | http://localhost:5173 |

### 数据存储

| 存储 | 地址 | 数据库 |
|------|------|--------|
| MySQL | 124.223.220.245:3306 | aipe_metadata |
| ClickHouse | 124.223.220.245:8123 | metric_observation |

---

## 四、关键设计决策

### 已确认

| 决策 | 选择 | 原因 |
|------|------|------|
| 模块拆分 | 8 个独立 Spring Boot | 对齐 WP, 可独立部署扩展 |
| 前端框架 | Vue 3 + Element Plus | 后端开发者友好, 学习曲线平缓 |
| 时序存储 | ClickHouse MergeTree | 列式存储, 聚合性能高 |
| 元数据存储 | MySQL + MyBatis Plus | 事务性, 强一致 |
| 部署模式 | SaaS + Agent (混合) | 数据留客户, AI 用云端 |
| 模块通信 | HTTP REST (非 RPC) | 简单, 跨语言, 易调试 |
| Agent 部署 | 每台业务机器 1 个 | 采集 JVM 指标, 零侵入 |
| 中间件采集 | 网络方式 (不装 Agent) | INFO/JMX/API, 无侵入 |
| Topology | 实时投影 (不存储) | Architecture Law-004 |
| Timeline | 运行时构建 (不存储) | Persistence Law-004 |
| 数据库外键 | 禁止物理外键 | Persistence Law-005 |

---

## 五、下一步行动

### 本周目标 (2026-07-28 ~ 2026-08-03)

1. **完成前后端联调**: 8 个页面全部对接真实 API
2. **实现动态表单**: 按 Resource Type 切换表单字段
3. **Timeline 交互**: ECharts 图表 + 时间范围选择
4. **拓扑图真实数据**: 从 Relationship API 加载

### 里程碑

| 日期 | 目标 |
|------|------|
| 2026-07-28 | Dashboard 8 页面全部可演示 |
| 2026-08-01 | Docker Compose 全套部署 |
| 2026-08-05 | 自家公司部署验证 |
| 2026-08-10 | 标杆客户 POC |

---

*此文档随项目进展持续更新*
