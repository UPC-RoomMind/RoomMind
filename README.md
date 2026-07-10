# RoomMind - 自习室预约系统

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java17)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-2.x-orange.svg)](https://vuejs.org/)
[![Element UI](https://img.shields.io/badge/Element%20UI-2.x-blue.svg)](https://element.eleme.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-yellow.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-orange.svg)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-20.x-blue.svg)](https://www.docker.com/)

---

## 一、项目介绍

### 1.1 项目概述

RoomMind 是一个基于 Spring Boot + Vue 的自习室预约管理系统，旨在为高校或培训机构提供便捷的自习室座位预约服务。

### 1.2 功能特性

| 模块 | 功能 |
| :--- | :--- |
| 用户管理 | 用户注册、登录、个人信息管理 |
| 自习室管理 | 自习室列表展示、详情查看 |
| 座位预约 | 座位选择、时间段预约、预约记录管理 |
| 积分系统 | 积分获取、积分消费、积分记录 |
| 评论系统 | 预约评价、评分 |
| 轮播图管理 | 首页轮播图展示 |

### 1.3 技术栈

**后端技术栈**：
- Java 17
- Spring Boot 3.x
- MyBatis-Plus
- JWT 认证
- MySQL 8.0
- Redis 7.x
- RabbitMQ 3.x
- Maven

**前端技术栈**：
- Vue 2.x
- Element UI
- Vue Router
- Axios
- Vue CLI

**基础设施**：
- Docker / Docker Compose
- Nginx（反向代理）

### 1.4 项目结构

```
RoomMind/
├── backend/                           # Spring Boot 后端项目
│   ├── src/main/java/com/example/web/
│   │   ├── controller/               # REST API 控制器层
│   │   │   ├── AppUserController.java       # 用户管理接口
│   │   │   ├── AppointRecordController.java # 预约记录接口
│   │   │   ├── BannerController.java        # 轮播图管理接口
│   │   │   ├── FileController.java          # 文件上传接口
│   │   │   ├── IntegralController.java      # 积分管理接口
│   │   │   ├── RoomController.java          # 自习室管理接口
│   │   │   ├── SeatController.java          # 座位管理接口
│   │   │   ├── SelectController.java        # 下拉选择接口
│   │   │   └── VerificationController.java  # 验证码接口
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── impl/                        # 接口实现类
│   │   │   │   ├── AppUserServiceImpl.java
│   │   │   │   ├── AppointRecordServiceImpl.java
│   │   │   │   └── ...
│   │   │   ├── AppUserService.java          # 用户服务接口
│   │   │   ├── AppointRecordService.java    # 预约服务接口
│   │   │   └── ...
│   │   ├── mapper/                   # 数据访问层（MyBatis）
│   │   │   ├── AppUserMapper.java
│   │   │   ├── AppointRecordMapper.java
│   │   │   └── ...
│   │   ├── entity/                   # 数据库实体类
│   │   │   ├── AppUser.java                 # 用户实体
│   │   │   ├── AppointRecord.java           # 预约记录实体
│   │   │   ├── Room.java                    # 自习室实体
│   │   │   ├── Seat.java                    # 座位实体
│   │   │   ├── Integral.java                # 积分实体
│   │   │   ├── Banner.java                  # 轮播图实体
│   │   │   └── ...
│   │   ├── dto/                      # 数据传输对象
│   │   │   ├── query/                       # 查询参数DTO
│   │   │   │   ├── AppUserPagedInput.java
│   │   │   │   ├── AppointRecordPagedInput.java
│   │   │   │   └── ...
│   │   │   ├── AppUserDto.java              # 用户DTO
│   │   │   ├── AppointRecordDto.java        # 预约DTO
│   │   │   ├── RoomDto.java                 # 自习室DTO
│   │   │   └── ...
│   │   ├── rabbitqueue/              # RabbitMQ消息队列
│   │   │   ├── RabbitMQConfig.java          # RabbitMQ配置
│   │   │   ├── OrderConsumer.java           # 订单消费者
│   │   │   ├── Consumer.java                # 消费者基类
│   │   │   └── Producer.java                # 生产者基类
│   │   ├── tools/                    # 工具类与配置
│   │   │   ├── dto/                         # 通用DTO
│   │   │   │   ├── ResponseData.java        # 统一响应封装
│   │   │   │   ├── PagedResult.java         # 分页结果封装
│   │   │   │   ├── FileResultDto.java       # 文件上传结果
│   │   │   │   └── ...
│   │   │   ├── exception/                   # 异常处理
│   │   │   │   ├── CustomException.java     # 自定义异常
│   │   │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │   │   ├── BaseContext.java             # 上下文工具
│   │   │   ├── CurrentUserInterceptor.java  # 用户登录拦截器
│   │   │   ├── InterceptorConfig.java       # 拦截器配置
│   │   │   ├── JWTUtils.java                # JWT工具类
│   │   │   ├── MyMetaObjectHandler.java     # MyBatis自动填充
│   │   │   ├── MybatisPlusConfig.java       # MyBatis-Plus配置
│   │   │   ├── WebMvcConfig.java            # WebMvc配置
│   │   │   ├── Extension.java               # 扩展工具类
│   │   │   ├── GlobalResponseAdvice.java    # 全局响应处理
│   │   │   └── SysConst.java                # 系统常量
│   │   ├── job/                      # 定时任务
│   │   │   ├── AppointRecordJob.java        # 预约记录定时任务
│   │   │   └── IntegralMockJob.java         # 积分模拟任务
│   │   ├── sendcode/                 # 验证码发送
│   │   │   ├── emailService.java            # 邮件服务
│   │   │   ├── CodeStorage.java             # 验证码存储
│   │   │   └── VerificationController.java  # 验证码接口
│   │   └── WebApplication.java       # Spring Boot启动类
│   └── src/main/resources/
│       ├── application.yml           # 公共基础配置
│       ├── application-dev.yml       # 本地开发环境配置
│       ├── application-test.yml      # 服务器测试环境配置
│       ├── application-prod.yml      # 服务器生产环境配置
│       └── schema.sql                # 数据库初始化脚本
├── frontend/                          # Vue 前端项目
│   ├── src/
│   │   ├── components/               # 公共组件
│   │   │   ├── Buttons/                     # 按钮组件
│   │   │   │   └── ExportButton.vue
│   │   │   ├── Code/                        # 验证码组件
│   │   │   │   └── canvas.vue
│   │   │   ├── Pagination/                 # 分页组件
│   │   │   │   └── PaginationBox.vue
│   │   │   ├── RichText/                    # 富文本编辑器
│   │   │   │   └── QillRichText.vue
│   │   │   ├── Select/                      # 选择器组件
│   │   │   │   └── SigleSelect.vue
│   │   │   ├── Tables/                      # 表格组件
│   │   │   │   ├── PaginationTable.vue
│   │   │   │   ├── FilesLinkButton.vue
│   │   │   │   ├── AudioButton.vue
│   │   │   │   ├── RichButton.vue
│   │   │   │   └── VideoButton.vue
│   │   │   └── Upload/                      # 文件上传组件
│   │   │       ├── UploadImages.vue         # 图片上传
│   │   │       ├── UploadFiles.vue          # 文件上传
│   │   │       └── imagecropper.vue         # 图片裁剪
│   │   ├── views/                    # 页面视图
│   │   │   ├── Admin/                       # 管理后台页面
│   │   │   │   ├── Layout/
│   │   │   │   │   └── index.vue            # 后台布局
│   │   │   │   ├── Home.vue                 # 后台首页
│   │   │   │   ├── UserList.vue             # 用户列表
│   │   │   │   ├── RoomList.vue             # 自习室列表
│   │   │   │   ├── SeatList.vue             # 座位列表
│   │   │   │   ├── AppointRecordList.vue    # 预约记录
│   │   │   │   ├── IntegralList.vue         # 积分列表
│   │   │   │   ├── BannerList.vue           # 轮播图管理
│   │   │   │   └── ...
│   │   │   ├── Front/                       # 前端用户页面
│   │   │   │   ├── Layout/
│   │   │   │   │   └── index.vue            # 前端布局
│   │   │   │   ├── Home.vue                 # 首页
│   │   │   │   ├── Room.vue                 # 自习室详情
│   │   │   │   ├── ToOrder.vue              # 预约页面
│   │   │   │   ├── AppointRecordList.vue    # 我的预约
│   │   │   │   ├── IntegralList.vue         # 我的积分
│   │   │   │   └── UserPerson.vue           # 个人中心
│   │   │   ├── Login.vue                    # 登录页面
│   │   │   ├── Register.vue                 # 注册页面
│   │   │   └── 404.vue                      # 404页面
│   │   ├── router/                   # 路由配置
│   │   │   └── index.js                     # 路由定义
│   │   ├── store/                    # 状态管理（Vuex）
│   │   │   └── index.js                     # store配置
│   │   ├── utils/                    # 工具函数
│   │   │   ├── request.js                   # Axios封装
│   │   │   ├── cache.js                     # 缓存工具
│   │   │   └── comm.js                      # 通用工具
│   │   ├── api/                      # API接口定义
│   │   │   └── http.js                      # HTTP请求封装
│   │   ├── css/                      # 样式文件
│   │   │   ├── index.css                    # 全局样式
│   │   │   └── font/                        # 字体文件
│   │   ├── assets/                   # 静态资源
│   │   │   ├── logo.png                     # 网站Logo
│   │   │   ├── login1.png                   # 登录背景
│   │   │   └── ...
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── public/                       # 公共静态资源
│   │   ├── index.html                  # HTML模板
│   │   └── favicon.ico                 # 网站图标
│   ├── .env.development              # 开发环境变量
│   ├── .env.production               # 生产环境变量
│   ├── vue.config.js                 # Vue CLI配置
│   ├── babel.config.js               # Babel配置
│   ├── jsconfig.json                 # JS配置
│   ├── package.json                  # 依赖配置
│   └── yarn.lock                     # 依赖锁文件
├── docs/                             # 文档目录
│   ├── v1.0文档.md                   # v1.0版本文档（旧版）
│   ├── v1.1文档.md                   # v1.1版本文档（新版）
│   ├── Nginx配置说明.md              # Nginx配置说明
│   └── 服务器文件说明.md             # 服务器文件说明
├── middleware-dev.yml                # 服务器开发中间件容器配置
├── middleware-prod.yml               # 服务器生产中间件容器配置
├── roommind-dev.yml                  # 服务器测试业务容器配置
├── roommind.conf                     # Nginx站点配置
├── Dockerfile                        # 后端Docker镜像构建文件
├── .gitignore                        # Git忽略配置
└── README.md                         # 项目说明文档
```

### 1.5 文件夹说明

#### 后端文件夹说明（backend/）

| 文件夹 | 说明 | 核心文件 |
| :--- | :--- | :--- |
| `controller/` | REST API控制器，处理HTTP请求 | `FileController.java`、`AppUserController.java` |
| `service/` | 业务逻辑层，包含接口和实现 | `AppUserServiceImpl.java`、`AppointRecordServiceImpl.java` |
| `mapper/` | 数据访问层，MyBatis接口 | `AppUserMapper.java`、`AppointRecordMapper.java` |
| `entity/` | 数据库实体类，与表结构对应 | `AppUser.java`、`Room.java`、`Seat.java` |
| `dto/` | 数据传输对象，用于接口参数和返回 | `AppUserDto.java`、`query/AppUserPagedInput.java` |
| `rabbitqueue/` | RabbitMQ消息队列配置 | `RabbitMQConfig.java`、`OrderConsumer.java` |
| `tools/` | 工具类和配置类 | `JWTUtils.java`、`WebMvcConfig.java`、`GlobalExceptionHandler.java` |
| `job/` | 定时任务 | `AppointRecordJob.java` |
| `sendcode/` | 验证码发送服务 | `emailService.java` |

#### 前端文件夹说明（frontend/）

| 文件夹 | 说明 | 核心文件 |
| :--- | :--- | :--- |
| `components/` | 公共组件，可复用UI | `UploadImages.vue`、`PaginationTable.vue`、`QillRichText.vue` |
| `views/` | 页面视图，路由对应组件 | `Login.vue`、`Admin/Home.vue`、`Front/Home.vue` |
| `router/` | 路由配置，定义页面跳转 | `index.js` |
| `store/` | Vuex状态管理 | `index.js` |
| `utils/` | 工具函数 | `request.js`（Axios封装）、`cache.js`（缓存） |
| `api/` | API接口定义 | `http.js` |
| `css/` | 样式文件 | `index.css`、`font/iconfont.css` |
| `assets/` | 静态资源文件 | `logo.png`、`login1.png` |

#### 配置文件说明

| 文件 | 说明 |
| :--- | :--- |
| `application.yml` | Spring Boot公共配置 |
| `application-dev.yml` | 本地开发环境（连接服务器中间件） |
| `application-test.yml` | 服务器测试容器环境 |
| `application-prod.yml` | 服务器生产容器环境 |
| `.env.development` | 前端开发环境变量 |
| `.env.production` | 前端生产环境变量 |
| `vue.config.js` | Vue CLI开发服务器配置（代理、端口） |
| `roommind.conf` | Nginx站点配置（反向代理、SSL） |
| `middleware-dev.yml` | Docker Compose开发中间件配置 |
| `middleware-prod.yml` | Docker Compose生产中间件配置 |
| `roommind-dev.yml` | Docker Compose测试业务容器配置 |

### 1.6 快速开始

**本地开发环境**：

```bash
# 1. 启动后端（开发环境）
cd backend
mvn spring-boot:run "-Dspring.profiles.active=dev"

# 2. 启动前端（开发环境）
cd frontend
npm install
npm run serve
```

**访问地址**：
- 前端页面：http://localhost:5173/
- 后端API：http://localhost:8080/

**详细开发文档**：请查看 [docs/v1.1文档.md](docs/v1.1文档.md)

---

**项目版本**：v1.1  
**最后更新**：2026年7月