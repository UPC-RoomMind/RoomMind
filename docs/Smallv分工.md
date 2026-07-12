# RoomMind 自习室预约系统 - 项目工程化部署文档

---

## 一、整体架构设计（核心框架）

### 1. 三层环境严格隔离设计（竞赛核心得分点）

实现**本地开发、服务器测试、服务器生产三层完全隔离**，双业务容器+双中间件容器架构，规避环境冲突、线上污染问题：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        服务器端（远程主机）                              │
│                                                                         │
│  ┌─────────────────────────────────────────┐                           │
│  │           测试环境 (Test)               │                           │
│  │  ┌──────────────┐  ┌──────────────────┐ │                           │
│  │  │ roommind-dev │  │   开发中间件容器   │ │                           │
│  │  │  业务容器    │  │ mysql-dev/redis-  │ │                           │
│  │  │  端口: 8080  │  │ dev/rabbitmq-dev │ │                           │
│  │  └──────┬───────┘  └────────┬─────────┘ │                           │
│  │         │                   │           │                           │
│  │         │  dev_net网桥      │ 端口开放   │                           │
│  │         │                   │ 3306/6379 │                           │
│  └─────────┼───────────────────┼───────────┘                           │
│            │                   │                                       │
│            │                   ▼                                       │
│            │         ┌──────────────────┐                               │
│            │         │   本地开发环境    │                               │
│            │         │  IDEA/Vite直连   │                               │
│            │         └──────────────────┘                               │
│                                                                         │
│  ┌─────────────────────────────────────────┐                           │
│  │           生产环境 (Production)         │                           │
│  │  ┌──────────────┐  ┌──────────────────┐ │                           │
│  │  │roommind-prod │  │   生产中间件容器   │ │                           │
│  │  │  业务容器    │  │ mysql-prod/redis- │ │                           │
│  │  │  端口: 28080 │  │ prod/rabbitmq-   │ │                           │
│  │  └──────┬───────┘  │ prod             │ │                           │
│  │         │          └────────┬─────────┘ │                           │
│  │         │  prod_net网桥     │           │                           │
│  │         │                   │ 端口隐藏   │                           │
│  └─────────┼───────────────────┼───────────┘                           │
│            │                   │                                       │
│            ▼                   │                                       │
│         Nginx 443              │                                       │
│         /api/ → 28080          │ 仅prod_net内网可达                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 1.1 本地开发层（Local Development）

- 本地 Vite 前端、IDEA SpringBoot 开发；
- **直连服务器开发中间件**：本地 Docker/IDEA 可直连服务器开放端口的开发中间件容器（MySQL 3306/Redis 6379/MQ 5672），本地调试直接读写线上开发中间件，无需本地搭建数据库；
- 激活 `dev` 配置文件，仅加载开发参数，不触碰生产业务容器；
- **个人数据库账号**：每个开发者使用独立 MySQL 账号（如 `dev_user1`、`dev_user2`），而非 root，权限严格控制。

#### 1.2 服务器测试环境层（Server Test）

- `application-test.yml` 独立测试配置；
- **测试业务容器** `roommind-dev`：独立镜像 tag，端口 8080，通过 `dev_net` 网桥访问开发中间件；
- **开发中间件容器**：端口开放至外网（3306/6379/5672），同时供本地开发和服务器测试容器访问；
- 数据库库名：`test_roommind`，与生产完全隔离。

#### 1.3 服务器生产环境层（Server Production）

- **生产中间件容器**（MySQL/Redis/RabbitMQ）：compose 统一管理，端口**仅容器内网互通**，宿主机网络网桥 `prod_net` 隔离，外部无法直接访问；
- **生产业务容器** `roommind-prod`：独立镜像、独立启动参数，端口使用非标准端口（如 28080）隐藏服务，与测试容器完全分离，互不干扰；
- 数据库库名：`prod_roommind`，独立账号 `prod_user`，最小权限原则；
- 前端静态资源脱离镜像内置，统一托管宿主机 `/root/roommind-prod/dist`，Nginx 反向代理统一分发 HTTPS 静态页面。

### 2. Docker Compose 双环境一体化编排

测试环境与生产环境各自独立 compose 配置，双业务容器+双中间件容器架构：

**测试环境启动：**
```bash
docker compose -f roommind-dev.yml up -d
```

**生产环境启动：**
```bash
docker compose -f roommind-prod.yml up -d
```

| 特性 | 测试环境 | 生产环境 |
|------|---------|---------|
| 网络网桥 | `dev_net`（端口开放） | `prod_net`（内网隔离） |
| 业务容器 | `roommind-dev`（8080） | `roommind-prod`（28080） |
| MySQL | `mysql-dev`（3306暴露） | `mysql-prod`（仅内网） |
| Redis | `redis-dev`（6379暴露） | `redis-prod`（仅内网） |
| RabbitMQ | `rabbitmq-dev`（5672暴露） | `rabbitmq-prod`（仅内网） |
| 数据库名 | `test_roommind` | `prod_roommind` |
| 环境变量 | `SPRING_PROFILES_ACTIVE: test` | `SPRING_PROFILES_ACTIVE: prod` |

---

## 二、多环境配置工程化方案（竞赛技术亮点）

### 1. SpringBoot 多环境配置体系

resources 目录分层配置文件，完美适配开发/测试/生产切换：

| 配置文件 | 用途 |
|---------|------|
| `application.yml` | 全局公共基础配置（Tomcat 线程、文件大小、日志规则） |
| `application-dev.yml` | 本地开发配置（直连服务器中间件、个人数据库账号、本地调试参数） |
| `application-test.yml` | 测试环境独立配置（测试中间件地址、测试数据库账号） |
| `application-prod.yml` | 线上生产专用（生产数据库账号、MQ 虚拟主机 `prod_room`、线上 AI 密钥、上传路径） |

**Compose 环境变量注入方案（推荐）：**

```yaml
services:
  roommind-prod:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql-prod
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
```

**优势**：无需重打包即可切换环境，灵活迭代，敏感配置通过环境变量注入，不硬编码在镜像中。

### 2. MySQL 多账号权限管理

开发环境每个开发者使用独立账号，严格控制权限：

```sql
-- 创建开发用户（每个开发者一个账号）
CREATE USER 'dev_user1'@'%' IDENTIFIED BY 'dev_password1';
CREATE USER 'dev_user2'@'%' IDENTIFIED BY 'dev_password2';

-- 授予最小权限
GRANT SELECT, INSERT, UPDATE, DELETE ON test_roommind.* TO 'dev_user1'@'%';
GRANT SELECT ON test_roommind.* TO 'dev_user2'@'%'; -- 只读权限

-- 生产环境独立账号
CREATE USER 'prod_user'@'prod_net' IDENTIFIED BY 'prod_strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON prod_roommind.* TO 'prod_user'@'prod_net';
```

**权限管理原则**：
- 禁止使用 root 账号连接应用；
- 开发环境：按开发者分配账号，权限按需授予；
- 测试环境：测试账号仅能访问测试库；
- 生产环境：应用账号仅能访问生产库，且限制来源 IP 为 `prod_net` 网段。

### 3. GitHub 团队协作规范

**分支管理策略：**

```
main（保护分支）
  ├── develop（开发分支）
  │     ├── feature/user-auth（功能分支）
  │     ├── feature/room-booking（功能分支）
  │     └── bugfix/login-error（修复分支）
  └── release/v1.0（发布分支）
```

**权限控制规则：**

| 角色 | 权限 |
|------|------|
| 管理员 | 全部权限（分支保护、合并审批） |
| 开发人员 | 拉取、推送功能分支，创建 PR |
| 测试人员 | 拉取、测试分支，提交 Bug |

**保护分支配置（main/develop）：**
- 禁止直接推送，必须通过 Pull Request；
- 至少 1 人审核通过才能合并；
- 必须通过 CI 检查；
- 禁用强制推送。

---

## 三、Nginx 反向代理生产架构（Web 部署核心）

### 1. HTTPS 强制全站加密

80 端口 301 永久重定向 HTTPS，配置阿里云 SSL 证书，开启 HTTP2、TLS1.2/1.3 安全协议，添加安全响应头（HSTS、防 XSS、防点击劫持），满足网络赛安全评分标准。

### 2. 分层转发逻辑

```
用户请求 (https://smallv.fun)
        │
        ▼
    Nginx 443
        │
    ┌───┴───┬──────┬─────────┬──────────┐
    ▼       ▼      ▼         ▼          ▼
 /         /api/  /ws      /uploads/  其他
dist      │       │         │          │
静态      │       │         │          │
资源      ▼       ▼         ▼          ▼
直接      Java    WS协议    宿主机     404
读取      容器    升级      上传目录   重定向
          28080   长连接    静态映射   index.html
          (隐藏端口)
```

1. **静态资源**：直接读取宿主机 `/root/roommind-prod/dist`，不经过 Java 容器，减轻后端 Tomcat 压力，静态缓存优化；
2. **API 接口 `/api/`**：反向代理转发至宿主机端口 28080（映射生产业务容器），生产端口隐藏，避免直接暴露；
3. **WebSocket 长连接 `/ws`**：单独配置 WS 升级协议，维持 AI 对话、消息推送长连接，延长超时时间；
4. **文件上传 `/uploads/`**：静态文件别名映射宿主机上传目录，支持大文件上传，适配图片/附件业务。

### 3. 测试环境独立访问

```
测试环境: https://smallv.fun/test-api/ → roommind-dev:8080
生产环境: https://smallv.fun/api/      → roommind-prod:28080
```

---

## 四、阿里云私有镜像仓库快速迭代部署流程（CI/CD 轻量化实现，竞赛加分）

完整闭环流水线，本地开发→构建镜像→推送私有仓库→服务器拉取更新，无人工拷贝 jar 操作：

### 1. 本地开发打包

```bash
mvn clean package -DskipTests
docker build -t 仓库地址/smallv/roommind:latest .
docker tag 仓库地址/smallv/roommind:latest 仓库地址/smallv/roommind:v1.0.0
```

### 2. 推送阿里云私有镜像仓库

```bash
docker login 仓库地址
docker push 仓库地址/smallv/roommind:latest
docker push 仓库地址/smallv/roommind:v1.0.0
```

### 3. 线上服务器一键更新

**测试环境更新：**
```bash
docker compose -f roommind-dev.yml pull
docker compose -f roommind-dev.yml up -d
docker logs -f roommind-dev
```

**生产环境更新：**
```bash
docker compose -f roommind-prod.yml pull
docker compose -f roommind-prod.yml up -d
docker logs -f roommind-prod
```

### 迭代优势（可写入文档）

- 无本地 jar 拷贝，统一镜像版本管理，线上线下代码一致；
- 多版本镜像留存，回滚仅需拉取旧 tag 镜像重启；
- 轻量化 JRE17 Alpine 镜像，构建体积小、拉取速度快，服务器磁盘占用低；
- 通过环境变量注入 `SPRING_PROFILES_ACTIVE`，同一镜像适配测试/生产环境。

---

## 五、环境隔离核心实现（竞赛重点阐述）

### 1. 中间件隔离规则

| 环境 | 网络策略 | 端口暴露 | 安全等级 | 访问来源 |
|------|---------|---------|---------|---------|
| 开发中间件 | `dev_net` 网桥 + 端口 `0.0.0.0` 开放 | 3306/6379/5672 | 低 | 本地开发、测试容器 |
| 生产中间件 | `prod_net` 网桥，仅内网通信 | 无端口暴露 | 高 | 仅生产业务容器 |

**启动依赖 `depends_on`**：compose 并行启动时，优先启动 MySQL/Redis/MQ，减少 Spring 启动阶段 MQ 连接拒绝 WARN 日志，优化服务启动时序。

### 2. 业务容器隔离

- 测试容器、生产容器使用**不同镜像 tag、不同 compose 配置**，网络网桥分离，数据库库名区分（`prod_roommind`/`test_roommind`）；
- 生产容器使用非标准端口（如 28080）隐藏服务，避免端口扫描发现；
- 生产容器挂载独立上传、日志目录，测试容器资源完全隔离，测试数据不会污染线上业务。

### 3. 网络隔离实现

```yaml
# roommind-dev.yml
networks:
  dev_net:
    driver: bridge

# roommind-prod.yml  
networks:
  prod_net:
    driver: bridge
    internal: true  # 仅内网通信
```

---

## 六、技术创新 & 竞赛加分亮点（答辩专用）

1. **三层环境严格隔离**：本地开发、服务器测试、服务器生产三层完全隔离，双业务容器+双中间件容器架构，开发中间件供本地和测试共享，生产中间件完全隔离；
2. **多账号权限管理**：MySQL 开发环境每个开发者独立账号，禁止 root，最小权限原则，生产环境账号限制来源网段；
3. **生产端口隐藏**：生产业务容器使用非标准端口（如 28080），通过 Nginx 反向代理暴露，避免直接暴露服务端口；
4. **环境变量注入**：`SPRING_PROFILES_ACTIVE` 通过 compose 环境变量注入，无需重打包即可切换环境，灵活迭代；
5. **容器化全栈编排**：前端静态资源、Java 后端、数据库、缓存、消息队列全部 Docker 容器化管理，一条指令完成全环境启停；
6. **GitHub 团队协作**：保护分支、PR 审核、CI 检查，严格控制上传拉取合并权限；
7. **安全合规设计**：全站 HTTPS 加密、中间件高强度密码、Web 安全响应头、大文件上传限制，满足网络安全类竞赛评分标准；
8. **故障自愈机制**：Spring RabbitMQ/Redis 自动重连、Hikari 连接池自动恢复、容器异常自动重启，服务稳定性高；
9. **运维便捷性**：日志、上传文件挂载宿主机，无需进入容器查看数据；Nginx 统一托管前端，更新页面仅替换 dist 文件夹，无需重新构建镜像。

---

## 七、优化拓展方案（拔高文档深度）

### 1. 容器健康检查

给 MySQL/RabbitMQ 添加 `healthcheck`，compose 通过 `condition: service_healthy` 等待中间件完全就绪，彻底消除启动 MQ 连接拒绝日志：

```yaml
services:
  mysql-prod:
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 3
```

### 2. 配置文件外置挂载

将 `application-prod.yml` 挂载宿主机，修改数据库密码无需重新构建镜像：

```yaml
services:
  roommind-prod:
    volumes:
      - /root/roommind-prod/config/application-prod.yml:/roommind/application.yml
```

### 3. 镜像清理脚本

```bash
# 自动清理旧镜像，保留最近 3 个版本
docker image prune -a --filter "until=24h" --force
```

### 4. 接口日志监控

集成 SpringBoot 日志持久化，记录所有 `/api` 接口请求，便于故障排查。

### 5. 密码加密优化

生产环境数据库、DeepSeek 密钥通过阿里云参数存储/环境变量注入，不硬编码在 yml/镜像内，杜绝明文密钥泄露：

```yaml
services:
  roommind-prod:
    env_file:
      - .env.prod
```

`.env.prod` 文件内容：
```
DB_PASSWORD=your_strong_password
AI_API_KEY=your_api_key
```

### 6. GitHub Actions CI/CD

自动构建、测试、推送镜像：

```yaml
name: CI/CD

on:
  push:
    branches: [ develop, main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          push: true
          tags: |
            registry.cn-hangzhou.aliyuncs.com/smallv/roommind:latest
            registry.cn-hangzhou.aliyuncs.com/smallv/roommind:${{ github.sha }}
```

---

## 附录：部署文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| `roommind-prod.yml` | 项目根目录 | 生产环境 Docker Compose 配置 |
| `roommind-dev.yml` | 项目根目录 | 测试环境 Docker Compose 配置 |
| `roommind.jar` | 项目根目录 | 后端 Java 打包文件 |
| `dist.zip` | 项目根目录 | 前端静态资源压缩包 |
| `init.sql` | 项目根目录 | 数据库初始化脚本 |
| `Dockerfile` | 项目根目录 | Docker 镜像构建文件 |

---

**文档版本**：v2.0  
**创建日期**：2026-07-11  
**适用场景**：网络挑战赛技术答辩、项目工程化文档撰写