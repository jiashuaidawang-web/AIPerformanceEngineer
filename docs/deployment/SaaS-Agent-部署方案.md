# AI Performance Engineer — SaaS + Agent 部署方案

> **目标客户**: B 端企业, 微服务架构, 多台应用服务器  
> **部署模式**: SaaS (平台在我们云) + Agent (部署在客户环境)  
> **文档版本**: v1.0 | 2026-07-26

---

## 目录

1. [架构概览](#1-架构概览)
2. [客户环境需求](#2-客户环境需求)
3. [Agent 部署位置](#3-agent-部署位置)
4. [应用服务器接入指南](#4-应用服务器接入指南)
5. [用户需要改什么](#5-用户需要改什么)
6. [网络与安全](#6-网络与安全)
7. [运维与监控](#7-运维与监控)

---

## 1. 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│  Platform SaaS (我们运维)                                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Web Dashboard (React)                                   │  │
│  │  API Gateway (Kong/Nginx)                                │  │
│  │  ┌─ Resource Service     :8082                          │  │
│  │  ├─ Observation Service  :8083                          │  │
│  │  ├─ Relationship Service :8084                          │  │
│  │  ├─ Timeline Service     :8085                          │  │
│  │  ├─ Evidence Service     :8086                          │  │
│  │  ├─ Knowledge Service    :8087                          │  │
│  │  ├─ Recommendation Service :8088                        │  │
│  │  └─ Execution Service    :8089                          │  │
│  │  Storage: MySQL + ClickHouse + Redis                    │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                           ↑
                    加密通道 (HTTPS/WSS)
                    单向: Agent → Platform
                           ↑
┌─────────────────────────────────────────────────────────────────┐
│  Customer Infrastructure (客户环境)                              │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Collector Agent (我们提供, 每个机房/集群 1 个)         │   │
│  │  ├─ Metric Collector (JVM/系统/应用指标)                │   │
│  │  ├─ Log Collector (可选)                                │   │
│  │  ├─ Trace Collector (可选)                              │   │
│  │  └─ Resource Discovery (自动发现微服务/主机/中间件)     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ App Server 1 │  │ App Server 2 │  │ App Server N │          │
│  │ (业务 Pod)   │  │ (业务 Pod)   │  │ (业务 Pod)   │          │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 客户环境需求

### 2.1 机器分配

| 用途 | 数量 | 配置要求 | 备注 |
|------|------|---------|------|
| **Collector Agent** | 1 台/集群 | 2C4G, 50GB SSD | **建议客户单独提供** |
| **业务应用服务器** | N 台 | 客户现有 | Agent 以 Sidecar 或 DaemonSet 部署 |

> **关键决策**: Collector Agent 建议客户单独申请一台机器, 原因:
> - 不占用业务资源
> - 网络隔离更安全
> - 便于权限管控

### 2.2 如果客户不能提供专用机器

备选方案: Collector Agent 部署在**任意一台**业务机器上 (资源限制 1C2G):

```yaml
# docker-compose.agent.yml
services:
  aipe-collector:
    image: aipe/collector:latest
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 2G
    environment:
      PLATFORM_URL: https://platform.aipe.io
      API_KEY: ${AIPE_API_KEY}
      CLUSTER_ID: ${CLUSTER_ID}
```

---

## 3. Agent 部署位置

### 3.1 Collector Agent (必须)

```
┌──────────────────────────────────────────┐
│  客户 K8s 集群 / 虚拟机                   │
│                                          │
│  ┌─ Namespace: aipe-system (我们创建)    │
│  │                                       │
│  │  ┌─ Deployment: aipe-collector       │
│  │  │  replicas: 1                      │
│  │  │  image: aipe/collector:latest     │
│  │  │  resources: 2C4G, 50GB            │
│  │  │                                   │
│  │  │  ┌─ Metric Collector Container    │
│  │  │  │  采集: CPU/Mem/GC/Thread/TPS   │
│  │  │  │  方式: JMX + /proc + JFR       │
│  │  │  │                                   │
│  │  │  ├─ Resource Discovery Container  │
│  │  │  │  自动发现: Service/Pod/Node/DB │
│  │  │  │  方式: K8s API + CMDB + 探针   │
│  │  │  │                                   │
│  │  │  ├─ Log Collector Container (可选)│
│  │  │  │  采集: stdout/stderr + 文件    │
│  │  │  │  方式: Fluent Bit               │
│  │  │  │                                   │
│  │  │  └─ Trace Collector Container (可选)
│  │  │     采集: OpenTelemetry Trace     │
│  │  │     方式: OTel Collector           │
│  │  └───────────────────────────────────│
│  │                                       │
│  └─ ServiceAccount: aipe-collector-sa    │
│     RBAC: get/list/watch nodes,pods,svcs │
└──────────────────────────────────────────┘
```

### 3.2 业务应用服务器上的 Agent (轻量)

**方案 A: K8s Sidecar (推荐)**

```yaml
# 客户 Deployment 模板 (我们在业务 Pod 旁加一个容器)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  template:
    spec:
      containers:
        # 客户原有容器 (不变)
        - name: order-service
          image: customer/order-service:1.2.0
          ports:
            - containerPort: 8080
        
        # 我们新增的 Sidecar (采集指标)
        - name: aipe-agent
          image: aipe/agent:latest
          resources:
            limits:
              cpus: '0.5'
              memory: 256M
          env:
            - name: AIPE_PLATFORM_URL
              value: "https://platform.aipe.io"
            - name: AIPE_API_KEY
              valueFrom:
                secretKeyRef:
                  name: aipe-credentials
                  key: api-key
            - name: JAVA_AGENT_TARGET
              value: "localhost:8080"  # 业务端口
```

**方案 B: DaemonSet (每台机器一个)**

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: aipe-agent
  namespace: aipe-system
spec:
  selector:
    matchLabels:
      app: aipe-agent
  template:
    metadata:
      labels:
        app: aipe-agent
    spec:
      hostNetwork: true        # 采集主机网络指标
      hostPID: true            # 采集主机进程
      containers:
        - name: agent
          image: aipe/agent:latest
          securityContext:
            privileged: true   # 需要采集 /proc, /sys
          volumeMounts:
            - name: proc
              mountPath: /host/proc
              readOnly: true
            - name: docker
              mountPath: /var/run/docker.sock
      volumes:
        - name: proc
          hostPath:
            path: /proc
        - name: docker
          hostPath:
            path: /var/run/docker.sock
```

---

## 4. 应用服务器接入指南

### 4.1 用户需要改什么

| 改动项 | 必须/可选 | 具体内容 |
|--------|---------|---------|
| **K8s Deployment** | 必须 | 加一个 Sidecar 容器 OR 加 DaemonSet |
| **启动参数** | 否 | 不需要改业务启动命令 |
| **代码** | 否 | 不需要改业务代码 |
| **网络** | 必须 | 开放 Collector → Platform 的 443 端口 |
| **ServiceAccount** | 必须 | 授权 Agent 读取 K8s API |
| **Prometheus 配置** | 可选 | 如果已有 Prometheus, 对接 Remote Write |

### 4.2 具体接入步骤

**Step 1: 客户创建 Namespace + RBAC**

```bash
# 一次性执行
kubectl create namespace aipe-system
kubectl apply -f https://install.aipe.io/rbac.yaml
```

**Step 2: 客户创建 API Key Secret**

```bash
kubectl create secret generic aipe-credentials \
  --from-literal-api-key=YOUR_API_KEY \
  -n aipe-system
```

**Step 3: 部署 Collector Agent**

```bash
# 一键安装
kubectl apply -f https://install.aipe.io/collector.yaml
```

**Step 4: 验证 Agent 连通**

```bash
kubectl get pods -n aipe-system -l app=aipe-collector
# 应看到 Running 状态
```

### 4.3 如果客户没有 K8s (纯 VM 环境)

```bash
# 一键脚本安装 Collector Agent
curl -fsSL https://install.aipe.io/install.sh | bash -s \
  --api-key=YOUR_API_KEY \
  --platform=https://platform.aipe.io \
  --cluster=prod-cluster-01
```

安装脚本自动完成:
- 下载 Agent 二进制
- 配置 systemd 服务
- 自动发现本机 JVM 进程
- 启动采集

---

## 5. 用户需要改什么 (汇总)

### 5.1 必须改

| # | 改动 | 影响范围 | 说明 |
|---|------|---------|------|
| 1 | 部署 Collector Agent | 每个集群 1 台 | 采集 + 上报 |
| 2 | 配置网络白名单 | 防火墙 | Collector → platform.aipe.io:443 |
| 3 | K8s RBAC (如有 K8s) | 一次性 | 授权 Agent 读取元数据 |

### 5.2 可选改

| # | 改动 | 收益 |
|---|------|------|
| 1 | 配置 Prometheus Remote Write | 复用已有指标 |
| 2 | 配置 Log Forward (Fluentd/Vector) | 日志采集 |
| 3 | 配置 OpenTelemetry Exporter | 链路追踪 |
| 4 | 开启 JMX Remote | 更详细的 JVM 指标 |
| 5 | 配置自定义指标端点 | 业务指标采集 |

### 5.3 不需要改

| 项 | 说明 |
|----|------|
| 业务代码 | 零侵入 |
| 业务启动命令 | 不需要加 `-javaagent` |
| 数据库 | 不需要Agent 通过日志/网络采集 |
| 中间件 | 同上 |

---

## 6. 网络与安全

### 6.1 网络拓扑

```
┌─────────────────────────┐
│  Customer Network       │
│                         │
│  ┌─ Collector Agent     │
│  │  ↑                   │
│  │  │ 内网采集 (localhost)│
│  │  │                   │
│  ├─ Business App :8080  │ ← Agent 连接业务 (只读 JMX/HTTP)
│  ├─ MySQL :3306         │ ← Agent 慢查询日志 (可选)
│  ├─ Redis :6379         │ ← Agent INFO 命令 (可选)
│  └─ Node Exporter :9100 │ ← 系统指标
│                         │
│  ↓                      │
│  防火墙                  │
│  ↓                      │
└──┬──────────────────────┘
   │ 仅允许出向 443
   ↓
┌─────────────────────────┐
│  Internet               │
│  ↓                      │
│  platform.aipe.io:443   │ ← Collector 上报 (mTLS)
└─────────────────────────┘
```

### 6.2 安全措施

| 层面 | 措施 |
|------|------|
| **传输** | mTLS (双向证书), 禁止明文 |
| **认证** | API Key + 签名, 无密码存储 |
| **数据** | 敏感字段 (IP/密码) 脱敏/哈希 |
| **权限** | Collector 只读, 不修改业务配置 |
| **隔离** | Agent 运行在独立 Namespace/网络策略 |

### 6.3 客户防火墙配置

```
# 客户只需要开放一条出向规则:
Source: Collector Agent IP/Subnet
Destination: platform.aipe.io (或客户专属域名)
Port: 443 (TCP)
Direction: OUTBOUND
```

---

## 7. 运维与监控

### 7.1 Agent 自身监控

Collector Agent 暴露自身指标:

```
http://collector:9090/metrics

# 指标示例
aipe_agent_up 1
aipe_collected_observations_total 12345
aipe_collected_bytes_total 6789012
aipe_platform_upload_errors_total 0
```

### 7.2 告警规则

| 条件 | 级别 | 动作 |
|------|------|------|
| Agent 离线 > 5min | P1 | 通知客户 + 运维 |
| 采集失败率 > 10% | P2 | 自动重试 + 告警 |
| 上报失败 > 3 次 | P2 | 本地缓存 + 告警 |
| 磁盘使用 > 80% | P3 | 自动清理旧数据 |

### 7.3 Agent 升级

```bash
# 滚动升级, 不影响业务
kubectl set image deployment/aipe-collector \
  collector=aipe/collector:v1.2.0 \
  -n aipe-system
```

---

## 附录: 模块部署对应表

| 模块 | 部署位置 | 数据采集方式 | 用户接入成本 |
|------|---------|-------------|-------------|
| **Resource** | SaaS | Agent 自动发现 | 部署 Collector |
| **Observation** | SaaS | Agent 实时推送 | 部署 Collector |
| **Relationship** | SaaS | Agent + 流量分析 | 部署 Collector |
| **Timeline** | SaaS | 运行时计算 | 无 |
| **Evidence** | SaaS | AI 推理引擎 | 无 |
| **Knowledge** | SaaS | 验证后沉淀 | 无 |
| **Recommendation** | SaaS | 规则 + LLM | 无 |
| **Execution** | SaaS | 可选执行器 | 可选 |

---

*本文档由 AI Performance Engineer 自动生成*
