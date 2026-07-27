# AI Performance Engineer — Dashboard

> AI Native 全链路性能工程平台前端, 面向企业 B 端, 提供资源观测、AI 推理、知识沉淀、优化闭环的可视化 Dashboard。

---

## 技术栈

| 层 | 技术 | 版本 |
|----|------|------|
| 框架 | Vue 3 Composition API | ^3.4.0 |
| 构建 | Vite 5 | ^5.3.0 |
| 语言 | TypeScript | ^5.5.0 |
| UI 框架 | Element Plus | ^2.7.0 |
| 图表 | ECharts 5 (vue-echarts) | ^5.5.0 |
| 路由 | Vue Router 4 | ^4.3.0 |
| 状态管理 | Pinia | ^2.1.0 |
| HTTP | axios | ^1.7.0 |
| 工具 | dayjs | ^1.11.0 |

---

## 功能页面

| 页面 | 路径 | 说明 |
|------|------|------|
| 概览 | `/dashboard` | 资源统计卡片 + 趋势图 + 状态分布 |
| 资源 | `/resources` | 资源列表 (搜索/筛选/创建/删除) |
| 资源详情 | `/resources/:id` | 资源基本信息 + CPU Timeline 趋势图 |
| 时序 | `/timeline` | Timeline 查询 + 统计特征 (min/max/avg/stdDev/count) |
| 拓扑 | `/topology` | 服务拓扑力导向图 |
| 证据 | `/evidence` | Evidence 证据链 + 置信度 + 自然语言解释 |
| 知识库 | `/knowledge` | Knowledge 卡片视图 |
| 推荐 | `/recommendations` | Recommendation 列表 + 优先级 + 审批 |
| 执行 | `/executions` | Execution 时间线 + 优化效果评估 |

---

## 快速开始

### 前置条件

- Node.js >= 18.0.0
- npm >= 9.0.0 (或 pnpm / yarn)

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

打开浏览器访问: **http://localhost:5173**

开发服务器已配置代理, API 请求会自动转发到远程平台:
- `/api/*` → `http://104.207.143.198:8080`

如需修改 API 地址, 编辑 `vite.config.ts` 中的 `proxy.target`。

### 构建生产包

```bash
npm run build
```

构建产物在 `dist/` 目录。

### 本地预览生产包

```bash
npm run preview
```

---

## 部署

### 1. 构建

```bash
npm run build
```

### 2. 上传到服务器

```bash
scp -r dist/* root@<服务器IP>:/var/www/aipe-dashboard/
```

### 3. nginx 配置

```nginx
server {
    listen 80;
    server_name dashboard.yourcompany.com;

    root /var/www/aipe-dashboard;
    index index.html;

    # 前端路由 (Vue Router history 模式)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理到平台后端
    location /api/ {
        proxy_pass http://104.207.143.198:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 30s;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript;
    gzip_min_length 1000;
}
```

### 4. 重启 nginx

```bash
nginx -t && systemctl reload nginx
```

访问: `http://dashboard.yourcompany.com`

---

## 项目结构

```
aipe-dashboard/
├── package.json              ← 依赖管理
├── vite.config.ts            ← Vite 配置 (含 API 代理)
├── tsconfig.json             ← TypeScript 配置
├── nginx.conf                ← 生产环境 nginx 模板
├── index.html                ← 入口 HTML
├── .env.development          ← 本地开发环境变量
├── .env.production           ← 生产环境变量
└── src/
    ├── main.ts               ← 应用入口
    ├── App.vue               ← 根组件
    ├── router/
    │   └── index.ts          ← 路由配置 (8 条路由)
    ├── api/
    │   └── index.ts          ← API 封装 (8 模块接口)
    ├── utils/
    │   └── request.ts        ← axios 封装 + 拦截器
    └── views/
        ├── Layout.vue        ← 主布局 (侧边栏 + 头部 + 内容区)
        ├── Dashboard.vue     ← 概览页
        ├── ResourceList.vue  ← 资源列表
        ├── ResourceDetail.vue← 资源详情 + Timeline 图
        ├── TimelineView.vue  ← Timeline 查询
        ├── TopologyView.vue  ← 拓扑图
        ├── EvidenceList.vue  ← 证据链
        ├── KnowledgeBase.vue ← 知识库
        ├── RecommendationList.vue ← 推荐
        └── ExecutionList.vue ← 执行记录
```

---

## API 对接

本前端对接 AI Performance Engineer 平台后端 (8 个 Domain Engine):

| API 模块 | 基础路径 | 说明 |
|---------|---------|------|
| Resource | `/api/v1/resources` | 资源 CRUD |
| Observation | `/api/v1/observations` | 指标查询 / 批量入库 |
| Timeline | `/api/v1/timelines` | 时序查询 + 统计特征 |
| Relationship | `/api/v1/relationships` | 关系 CRUD |
| Evidence | `/api/v1/evidences` | 证据生成 / 解释 |
| Knowledge | `/api/v1/knowledge` | 知识库 CRUD |
| Recommendation | `/api/v1/recommendations` | 推荐生成 / 审批 |
| Execution | `/api/v1/executions` | 执行记录 |

详细 API 参考: [后端 API 文档](../../../docs/api/API文档.md)

---

## 开发指南

### 环境变量

| 文件 | 说明 |
|------|------|
| `.env.development` | 本地开发 (API 代理到远程) |
| `.env.production` | 生产构建 (API 绝对地址) |

### 新增页面

1. 在 `src/views/` 创建 `FooView.vue`
2. 在 `src/router/index.ts` 添加路由:
```ts
{
  path: 'foo',
  name: 'Foo',
  component: () => import('@/views/FooView.vue'),
  meta: { title: 'Foo', icon: 'IconName' },
}
```

### 新增 API

1. 在 `src/api/index.ts` 添加接口函数:
```ts
export const fooApi = {
  list: () => request.get('/v1/foos'),
  get: (id: string) => request.get(`/v1/foos/${id}`),
}
```

### 代码规范

- 使用 `<script setup lang="ts">` 语法
- 组件命名: PascalCase (如 `ResourceList.vue`)
- 变量命名: camelCase
- 接口返回: `ApiResponse<T>` 格式 (`{ code, message, data }`)

---

## 构建优化

- 路由懒加载 (所有页面 `() => import(...)`)
- Element Plus 按需导入 (auto-import 插件)
- ECharts 按需引入 (后续优化)
- 静态资源 CDN (生产环境)
- Gzip 压缩

---

## 浏览器支持

- Chrome >= 90
- Firefox >= 90
- Safari >= 15
- Edge >= 90

---

## License

Proprietary — AI Performance Engineer Team
