# RoomMind - 智能自习室预约系统

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.1-green.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.7-orange.svg" alt="MyBatis-Plus">
  <img src="https://img.shields.io/badge/Vue-2.6.x-purple.svg" alt="Vue">
  <img src="https://img.shields.io/badge/Element%20UI-2.15.x-blue.svg" alt="Element UI">
  <img src="https://img.shields.io/badge/MySQL-8.0-yellow.svg" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7-red.svg" alt="Redis">
  <img src="https://img.shields.io/badge/RabbitMQ-3-orange.svg" alt="RabbitMQ">
  <img src="https://img.shields.io/badge/Docker-20.x-blue.svg" alt="Docker">
  <img src="https://img.shields.io/badge/Nginx-1.24-green.svg" alt="Nginx">
</p>

<p align="center">
  <a href="#项目简介">项目简介</a> ·
  <a href="#技术架构">技术架构</a> ·
  <a href="#功能模块">功能模块</a> ·
  <a href="#团队分工">团队分工</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#部署方案">部署方案</a> ·
  <a href="#项目亮点">项目亮点</a>
</p>

---

## 项目简介

RoomMind 是一款基于 **Spring Boot + Vue + Docker** 技术栈构建的智能自习室预约管理系统，为高校和培训机构提供便捷的座位预约、积分管理、AI助手等服务。系统采用**三层环境隔离架构**（本地开发/服务器测试/服务器生产），通过**阿里云私有镜像仓库**实现轻量化CI/CD部署，支持**微信小程序端**和**Web管理端**双端访问。

---

## 技术架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        阿里云服务器 (CentOS 7)                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                     Nginx 反向代理层 (443/80)                    │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │   │
│  │  │  HTTPS   │  │  /api/   │  │  /ws     │  │  /uploads/   │   │   │
│  │  │  静态    │  │  代理    │  │  WebSocket│  │  静态映射    │   │   │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────┬───────┘   │   │
│  └───────┼─────────────┼─────────────┼───────────────┼───────────┘   │
│          │             │             │               │               │
│          ▼             ▼             ▼               ▼               │
│  ┌──────────────┐  ┌──────────────┐                              │   │
│  │   /var/www/  │  │ roommind-    │                              │   │
│  │   roommind/  │  │ prod         │                              │   │
│  │   (前端静态) │  │ (生产业务)   │                              │   │
│  │              │  │ 端口:28080   │                              │   │
│  └──────────────┘  └──────┬───────┘                              │   │
│                           │                                       │   │
│  ┌────────────────────────┼────────────────────────┐              │   │
│  │         prod_net       │                        │              │   │
│  │  ┌──────────────┐      │      ┌──────────────┐  │              │   │
│  │  │ mysql-prod   │◄─────┴─────►│ rabbitmq-    │  │              │   │
│  │  │ 3306(内网)   │              │ prod         │  │              │   │
│  │  └──────────────┘              │ 5672(内网)   │  │              │   │
│  │  ┌──────────────┐              └──────────────┘  │              │   │
│  │  │ redis-prod   │                                │              │   │
│  │  │ 6379(内网)   │                                │              │   │
│  │  └──────────────┘                                │              │   │
│  └──────────────────────────────────────────────────┘              │   │
│                                                                     │   │
│  ┌─────────────────────────────────────────────────────────────┐   │   │
│  │                       测试环境 (dev_net)                    │   │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐   │   │   │
│  │  │ roommind-    │  │ mysql-dev    │  │ redis-dev/rabbit │   │   │   │
│  │  │ dev          │  │ 3306(暴露)   │  │ mq-dev(暴露)     │   │   │   │
│  │  │ 端口:8080    │  └──────────────┘  └──────────────────┘   │   │   │
│  │  └──────────────┘                                           │   │   │
│  └─────────────────────────────────────────────────────────────┘   │   │
│                                                                     │   │
└─────────────────────────────────────────────────────────────────────┘   │
                                                                        │
┌─────────────────────────────────────────────────────────────────────────┐
│                        本地开发环境                                     │
│  ┌──────────────┐         ┌──────────────┐         ┌──────────────┐    │
│  │  Vue 前端    │         │  Spring      │         │  微信小程序  │    │
│  │  localhost:  │  HTTP   │  Boot后端    │         │  开发者工具  │    │
│  │  5173        │◄───────►│  localhost:  │         │              │    │
│  │  (Vite热更新)│         │  8080        │         │              │    │
│  └──────────────┘         └──────┬───────┘         └──────────────┘    │
│                                  │                                      │
│                                  ▼                                      │
│                     ┌───────────────────────┐                           │
│                     │   服务器开发中间件     │                           │
│                     │  (MySQL/Redis/Rabbit)│                           │
│                     │  端口开放直连         │                           │
│                     └───────────────────────┘                           │
└─────────────────────────────────────────────────────────────────────────┘
```

### 技术栈详情

| 层级 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.3.1 | 服务端核心框架 |
| **ORM框架** | MyBatis-Plus | 3.5.7 | 数据库操作 |
| **认证授权** | JWT | 3.19.2 | 用户身份认证 |
| **消息队列** | RabbitMQ | 3.x | 异步消息处理 |
| **缓存服务** | Redis | 7.x | 数据缓存 |
| **数据库** | MySQL | 8.0 | 主数据库 |
| **实时通信** | WebSocket | - | AI对话长连接 |
| **AI能力** | DeepSeek/百度API | - | 智能助手服务 |
| **前端框架** | Vue | 2.6.x | Web前端框架 |
| **UI组件** | Element UI | 2.15.x | Web界面组件 |
| **状态管理** | Vuex | 3.1.x | 前端状态管理 |
| **路由管理** | Vue Router | 3.0.x | 前端路由 |
| **图表库** | ECharts | 5.5.x | 数据可视化 |
| **小程序** | 微信小程序 | - | 移动端入口 |
| **容器化** | Docker | 20.x | 应用容器化 |
| **编排工具** | Docker Compose | - | 多容器编排 |
| **反向代理** | Nginx | 1.24 | HTTPS/负载均衡 |

---

## 功能模块

### 1. 用户管理模块
- 用户注册/登录（邮箱验证码）
- 个人信息管理
- 密码修改
- JWT认证授权

### 2. 自习室管理模块
- 自习室列表展示
- 座位可视化布局
- 房间状态实时监控

### 3. 预约系统模块
- 座位选择与预约
- 预约时间段管理
- 预约记录查询
- 取消预约

### 4. 积分系统模块
- 积分获取（签到/预约）
- 积分消费
- 积分记录查询
- 积分统计图表

### 5. AI助手模块
- 智能对话
- 自习室导航
- 预约提醒

### 6. 管理后台模块
- 用户管理
- 房间管理
- 座位管理
- 轮播图管理
- 预约记录审核

---

## 团队分工

| 成员 | 负责模块 | 具体职责 |
|------|---------|---------|
| **松** | 后端框架搭建+业务功能扩展 | Spring Boot框架搭建、核心业务逻辑开发、API接口设计、MyBatis-Plus数据层实现 |
| **马** | 小程序前端扩展+后端框架搭建 | 微信小程序端开发、页面设计与实现、小程序API对接、后端框架辅助搭建 |
| **延** | 前端页面设计+前端代码编写 | Web前端页面设计、UI组件开发、页面布局与交互、Element UI组件封装 |
| **泽** | 前端接口对接+前端代码编写 | 前端API接口对接、Axios封装、数据请求与响应处理、前端业务逻辑实现 |
| **Smallv** | 脚手架Docker搭建+服务器CI/CD部署 | Docker容器化配置、Docker Compose编排、阿里云服务器部署、CI/CD流程搭建、Nginx配置、多环境隔离架构设计 |
| **魏** | 前后端联调+测试bug+测试功能 | 接口联调测试、Bug修复、功能测试、测试用例编写、系统稳定性验证 |

---

## 快速开始

### 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | Spring Boot 3.x 必须 |
| Maven | 3.9+ | 后端构建工具 |
| Node.js | 18+ | 前端开发环境 |
| Docker | 20.x+ | 容器化部署 |
| 微信开发者工具 | 最新版 | 小程序开发 |

### 本地开发

```bash
# 1. 克隆项目
git clone https://github.com/smallv/RoomMind.git
cd RoomMind

# 2. 启动后端（连接服务器中间件）
cd backend
mvn spring-boot:run "-Dspring.profiles.active=dev"

# 3. 启动前端（新终端）
cd frontend
npm install
npm run serve

# 4. 访问地址
# 前端页面：http://localhost:5173/
# 后端API：http://localhost:8080/
```

### 微信小程序开发

1. 打开微信开发者工具
2. 导入 `miniprogram/` 目录
3. 配置小程序AppID
4. 开发模式下测试接口

---

## 部署方案

### 三层环境架构

| 环境 | 启动方式 | API路径 | 中间件连接 |
|------|---------|---------|-----------|
| **本地开发** | `mvn spring-boot:run -Dspring.profiles.active=dev` | http://localhost:8080/ | 直连服务器开发中间件 |
| **服务器测试** | `docker-compose -f roommind-dev.yml up -d` | https://smallv.fun/test-api/ | 容器内网连接开发中间件 |
| **服务器生产** | `docker-compose -f roommind-prod.yml up -d` | https://smallv.fun/api/ | 容器内网连接生产中间件 |

### CI/CD 部署流程

```
本地开发 → 代码提交 → GitHub PR审核 → 构建镜像 → 推送阿里云仓库 → 服务器拉取 → 自动部署
```

**一键部署命令：**

```bash
# 本地构建镜像
mvn clean package -DskipTests
docker build -t registry.cn-hangzhou.aliyuncs.com/smallv/roommind:latest .
docker push registry.cn-hangzhou.aliyuncs.com/smallv/roommind:latest

# 服务器一键更新
ssh root@47.94.202.210 "cd /root/roommind-prod && docker-compose pull && docker-compose up -d"
```

---

## 项目亮点

### 🏆 架构设计
- **三层环境严格隔离**：本地开发、服务器测试、服务器生产完全分离，双业务容器+双中间件容器架构
- **生产端口隐藏**：使用非标准端口（28080），通过Nginx反向代理暴露，提高安全性

### 🏆 工程化方案
- **多环境配置体系**：Spring Boot Profile + Maven Profile，敏感配置通过环境变量注入
- **MySQL多账号权限管理**：每个开发者独立账号，禁止root，最小权限原则
- **GitHub团队协作**：保护分支、PR审核、CI检查，严格控制代码质量

### 🏆 容器化部署
- **Docker Compose一体化编排**：MySQL/Redis/RabbitMQ/业务容器统一管理，一条指令完成全环境启停
- **轻量化镜像**：JRE17 Alpine基础镜像，构建体积小、拉取速度快
- **数据持久化**：Docker命名卷持久化，容器重启数据不丢失

### 🏆 安全合规
- **全站HTTPS加密**：HTTP2、TLS1.2/1.3、安全响应头
- **大文件上传限制**：Nginx配置上传大小限制，防止攻击
- **故障自愈机制**：Spring自动重连、容器自动重启

### 🏆 运维便捷
- **日志/文件挂载宿主机**：无需进入容器查看数据
- **前端静态资源分离**：更新页面仅替换dist文件夹，无需重构镜像
- **阿里云私有镜像仓库**：统一版本管理，一键迭代部署

---

## 目录结构

```
RoomMind/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/example/web/
│   │   ├── controller/              # REST API 控制器
│   │   ├── service/                 # 业务逻辑层
│   │   ├── mapper/                  # 数据访问层
│   │   ├── entity/                  # 实体类
│   │   ├── dto/                     # 数据传输对象
│   │   ├── rabbitqueue/             # RabbitMQ配置与消费者
│   │   ├── sendcode/                # 验证码服务
│   │   ├── tools/                   # 工具类与配置
│   │   └── WebApplication.java      # 启动类
│   └── src/main/resources/
│       ├── application.yml          # 公共配置
│       ├── application-dev.yml      # 本地开发环境
│       ├── application-test.yml     # 服务器测试容器环境
│       ├── application-prod.yml     # 服务器生产容器环境
│       └── init.sql                 # 数据库初始化脚本
├── frontend/                         # Vue 前端
│   ├── src/
│   │   ├── components/              # 公共组件
│   │   ├── views/                   # 页面视图
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # Vuex状态管理
│   │   ├── utils/                   # 工具函数
│   │   └── App.vue                  # 根组件
│   ├── .env.development             # 开发环境变量
│   └── .env.production              # 生产环境变量
├── miniprogram/                     # 微信小程序端
│   ├── pages/                       # 小程序页面
│   ├── components/                  # 小程序组件
│   ├── utils/                       # 小程序工具
│   └── app.js                       # 小程序入口
├── docs/                            # 文档目录
│   ├── v1.0文档.md                  # v1.0版本文档
│   ├── v1.1文档.md                  # v1.1版本文档
│   ├── Nginx配置说明.md             # Nginx配置说明
│   ├── 服务器文件说明.md            # 服务器文件说明
│   └── Smallv分工.md                # 工程化部署文档
├── roommind-dev.yml                 # 测试环境Docker Compose
├── roommind-prod.yml                # 生产环境Docker Compose
├── roommind.conf                    # Nginx站点配置
├── Dockerfile                       # 后端Docker镜像构建文件
└── README.md                        # 项目说明文档
```

---

## 许可证

MIT License

---

**项目版本**：v2.0  
**最后更新**：2026年7月  
**开发团队**：RoomMind Team