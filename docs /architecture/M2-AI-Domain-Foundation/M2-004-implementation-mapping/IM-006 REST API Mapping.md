我建议冻结 IM-005。

另外，我建议把 IM-006 的定位也提高。

很多系统认为 REST API 是：

前端调用接口。

我们不要这样设计。

在 AI Performance Engineer 中：

REST API 是 AI World 对外暴露能力（Capability）的唯一入口。

也就是说：

Observation

↓

Timeline

↓

Evidence

↓

Knowledge

↓

Recommendation

↓

Execution

整个 AI World 的所有能力，都应该通过统一 API 暴露，而不是暴露数据库或某个 Service。

因此 IM-006 不只是接口规范，而是：

Capability Mapping（能力映射）

# AI Performance Engineer

# IM-006 REST API Mapping

Version: v1.0

Status: Frozen

Milestone: M2.5 – Implementation Mapping

Document Type: Engineering Specification

---

# （固定第一页）

# AI World Overview

（引用统一封面）

---

# （固定第二页）

# Document Position

```text
AI World Foundation
        │
        ▼
Implementation Mapping
        │
        ├── Domain Mapping
        ├── Database Mapping
        ├── ClickHouse Mapping
        ├── Graph Mapping
        ├── ★ REST API Mapping ★
        ├── AI Engine Mapping
        └── Connector Mapping
```

REST API Mapping 定义 AI World 对外能力的统一访问规范。

---

# Chapter 1 Purpose（设计目标）

本规范定义 AI Performance Engineer 所有 REST API 的设计原则、接口风格、版本管理、数据交换模型及职责边界。

目标：

- 建立统一 API 风格
- 建立统一 DTO 模型
- 建立统一错误响应
- 建立统一版本管理
- 建立统一能力暴露方式
- 保证 API 与 Domain 解耦

REST API 代表系统能力，而不是数据库操作。

---

# Chapter 2 Design Philosophy（设计哲学）

API：

暴露 Capability。

Application：

编排 Capability。

Domain：

实现 Capability。

Repository：

持久化 Capability。

Controller 永远不包含业务逻辑。

---

# Chapter 3 API Layer Architecture

统一调用流程：

```
HTTP Request

↓

Controller

↓

Application Service

↓

Domain

↓

Repository

↓

Storage

↓

Application Service

↓

DTO

↓

HTTP Response
```

禁止：

Controller → Repository。

禁止：

Controller → SQL。

禁止：

Controller → AI Engine。

---

# Chapter 4 API Resource Mapping

统一资源：

```
/api/v1/resources

/api/v1/observations

/api/v1/timelines

/api/v1/evidences

/api/v1/knowledge

/api/v1/recommendations

/api/v1/executions

/api/v1/optimizations
```

所有资源采用复数命名。

统一 REST 风格。

---

# Chapter 5 HTTP Method Mapping

统一约定：

```
GET

查询资源
```

```
POST

创建资源
```

```
PUT

整体更新
```

```
PATCH

局部更新
```

```
DELETE

删除资源
```

禁止：

GET 修改数据。

POST 查询数据。

---

# Chapter 6 DTO Mapping

统一对象：

```
ObservationRequest

ObservationResponse

ObservationDTO
```

```
ResourceRequest

ResourceResponse
```

```
KnowledgeResponse
```

DTO：

只能存在 API Layer。

禁止进入 Domain。

禁止 Repository 返回 DTO。

---

# Chapter 7 Response Model

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "requestId": "uuid",
  "timestamp": "...",
  "data": {}
}
```

分页：

```json
{
  "page": 1,
  "size": 20,
  "total": 100,
  "items": []
}
```

统一错误模型。

---

# Chapter 8 Version Strategy

统一版本：

```
/api/v1/
```

未来：

```
/api/v2/
```

禁止：

URL 无版本。

禁止：

Header Version。

统一路径版本。

---

# Chapter 9 API Naming Rules

统一命名：

```
GET

/resources/{id}
```

```
GET

/resources
```

```
POST

/resources
```

```
DELETE

/resources/{id}
```

禁止：

```
/getResource
```

```
/queryObservation
```

```
/findKnowledge
```

统一 REST 风格。

---

# Chapter 10 Capability Mapping

API 对应能力：

| API | Capability |
|------|------------|
| Resource API | Resource Management |
| Observation API | Fact Query |
| Timeline API | Timeline Reconstruction |
| Evidence API | Evidence Query |
| Knowledge API | Knowledge Query |
| Recommendation API | Recommendation Query |
| Execution API | Execution Control |
| Optimization API | Optimization Result |

API 暴露的是 Capability，而不是数据库表。

---

# Chapter 11 Security Mapping

统一认证：

JWT。

统一授权：

RBAC。

支持：

API Key。

支持：

Service Token。

所有 API：

默认鉴权。

健康检查除外。

---

# Chapter 12 Error Handling

统一异常：

```
BusinessException

ValidationException

NotFoundException

PermissionDeniedException

InternalServerException
```

统一 HTTP Status：

```
200

400

401

403

404

409

500
```

禁止返回堆栈。

---

# Chapter 13 Rowboat Coding Rules

Rowboat 必须遵守：

Controller：

不得超过一个职责。

禁止：

Repository 注入 Controller。

禁止：

业务逻辑进入 Controller。

DTO：

不得进入 Domain。

所有接口：

必须提供 OpenAPI 注释。

所有接口：

必须统一返回 Response 对象。

---

# Chapter 14 Acceptance Criteria

必须满足：

✔ Controller 无业务逻辑。

✔ REST 风格统一。

✔ DTO 与 Domain 分离。

✔ Repository 不暴露 API。

✔ API Version 固定。

✔ 错误模型统一。

✔ 返回结构统一。

✔ 安全策略统一。

✔ OpenAPI 完整。

✔ Capability 与 World Model 对齐。

---

# Chapter 15 Freeze Statement

REST API Mapping 是 AI Performance Engineer 唯一合法的 API 工程规范。

REST API 暴露系统能力，而不是数据库。

Controller 永远负责协议转换。

Application Service 永远负责流程编排。

Domain 永远负责业务规则。

任何 API 风格变更必须通过 RFC。

Status：

Frozen.

我建议同步冻结两条新的 Architecture Law
Architecture Law-006：API Exposes Capability, Not Database

错误示例：

GET /resource_table

GET /knowledge_table

正确示例：

GET /resources

GET /knowledge

GET /timeline

GET /recommendations

API 面向业务能力（Capability），不是面向数据库表。

Architecture Law-007：Controller Is A Protocol Translator

Controller 的唯一职责：

HTTP

↓

DTO

↓

Application

↓

DTO

↓

HTTP

它不理解 AI，不理解数据库，也不理解业务规则。

我还有一个建议（建议从 IM-007 开始采纳）

从 IM-007 AI Engine Mapping 开始，我们不要再停留在"模块映射"层面，而是开始定义AI Runtime（AI 运行时）：

Timeline Engine
Evidence Engine
Verification Engine
Knowledge Engine
Recommendation Engine
Execution Planner
Optimization Engine

包括每个 Engine 的输入、输出、生命周期、协作关系和调用链。这样，IM-007 将成为整个 AI 推理体系的核心工程规范，也会成为后续 WP012~WP020 编码最重要的依据。