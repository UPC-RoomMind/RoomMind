# RoomAppoint - 自习室预约系统

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java17)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-2.x-orange.svg)](https://vuejs.org/)
[![Element UI](https://img.shields.io/badge/Element%20UI-2.x-blue.svg)](https://element.eleme.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-yellow.svg)](https://www.mysql.com/)

---

## 一、项目介绍

### 1.1 项目概述

RoomAppoint 是一个基于 Spring Boot + Vue 的自习室预约管理系统，旨在为高校或培训机构提供便捷的自习室座位预约服务。

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
- Maven

**前端技术栈**：
- Vue 2.x
- Element UI
- Vue Router
- Axios
- Vue CLI

### 1.4 项目结构

```
roomappoint/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/example/web/
│   │   ├── controller/        # REST API 控制器
│   │   ├── service/           # 业务逻辑层
│   │   ├── mapper/            # 数据访问层
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   └── WebApplication.java # 启动类
│   └── src/main/resources/
│       ├── application.yml    # 公共配置
│       ├── application-dev.yml # 开发环境配置
│       └── application-prod.yml # 生产环境配置
├── frontend/                   # Vue 前端
│   ├── src/
│   │   ├── components/        # 公共组件
│   │   ├── views/             # 页面视图
│   │   ├── router/            # 路由配置
│   │   ├── utils/             # 工具函数
│   │   └── App.vue            # 根组件
│   ├── .env.development       # 开发环境变量
│   └── .env.production        # 生产环境变量
├── service/                   # 服务器启动脚本
│   ├── start_backend.sh       # 启动脚本
│   ├── stop_backend.sh        # 停止脚本
│   └── status_backend.sh      # 状态脚本
├── DEPLOYMENT.md              # 部署文档
├── v1.0.md                    # 版本变更记录
└── README.md                  # 项目说明文档
```

### 1.5 快速开始

**本地开发环境**：

```bash
# 1. 启动后端（开发环境）
cd backend
mvn spring-boot:run

# 2. 启动前端（开发环境）
cd frontend
npm install
npm run serve
```

**访问地址**：
- 前端页面：http://localhost:8080/
- 后端API：http://localhost:7245/

---

## 二、部署指南

### 2.1 前期准备：购买云服务器

#### 2.1.1 厂商选购
- **推荐厂商**：阿里云 / 腾讯云 / 华为云
- **配置建议**：2核4G起步（测试项目可使用2核2G低配）

#### 2.1.2 系统镜像
- **推荐**：CentOS 8 / CentOS Stream（本文命令基于CentOS）

#### 2.1.3 购买后操作
1. 重置 root 密码，保存登录账号密码
2. 记录公网IP、服务器内网IP

### 2.2 服务器初始化环境配置

#### 2.2.1 远程连接服务器

**工具**：Xshell / FinalShell / PowerShell

```bash
ssh root@服务器公网IP
```

#### 2.2.2 开放安全组端口（云平台控制台操作）

**放行端口**：
- `22` - SSH远程登录
- `80` - HTTP、`443` - HTTPS（前端Nginx）
- `3306` - MySQL数据库
- `7245` - SpringBoot后端业务端口

#### 2.2.3 服务器防火墙放行端口

```bash
# 放行80/443/3306/7245
firewall-cmd --add-port=80/tcp --permanent
firewall-cmd --add-port=443/tcp --permanent
firewall-cmd --add-port=3306/tcp --permanent
firewall-cmd --add-port=7245/tcp --permanent

# 重载防火墙
firewall-cmd --reload

# 查看放行端口
firewall-cmd --list-ports
```

#### 2.2.4 安装运行环境

##### 1）JDK 17（SpringBoot 3.x 必须）

```bash
dnf install java-17-openjdk-devel -y

# 验证版本
java -version
```

##### 2）MySQL 8.0

```bash
dnf install mysql-server -y

# 开机自启+启动
systemctl enable --now mysqld

# 查看状态
systemctl status mysqld

# 初始化数据库、设置root密码
mysql_secure_installation

# 登录数据库创建业务库
mysql -uroot -p
CREATE DATABASE RoomAppoint DEFAULT CHARACTER SET utf8mb4;
```

##### 3）Nginx（前端静态资源+反向代理）

```bash
dnf install nginx -y
systemctl enable --now nginx

# 校验配置
nginx -t
```

##### 4）Node.js（本地前端打包用，服务器无需安装）

本地Windows安装node，用于 `npm run build` 打包Vue

### 2.3 项目开发 & 多环境配置

#### 2.3.1 后端SpringBoot多环境yml

| 文件 | 用途 |
| :--- | :--- |
| `application.yml` | 公共基础配置 |
| `application-dev.yml` | 本地开发环境（localhost数据库） |
| `application-prod.yml` | 服务器生产环境（127.0.0.1数据库、7245端口、文件上传路径） |

**prod核心配置示例**：

```yaml
server:
  port: 7245

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/RoomAppoint?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: 数据库密码
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  web:
    upload-path: /opt/roomappoint/upload

app:
  upload:
    path: /opt/roomappoint/uploads/
    url-prefix: /uploads/
```

#### 2.3.2 前端Vue环境配置

修改 `.env.production` 生产接口地址：

```
VUE_APP_BASE_API = '/api'
VUE_APP_BASE_WS = 'wss://smallv.fun/ws'
```

### 2.4 本地打包前后端

#### 2.4.1 后端Maven打包

进入后端根目录（存在pom.xml文件夹）执行：

```bash
# 清理旧包
mvn clean

# 打包跳过单元测试
mvn package -DskipTests
```

**产物**：`target/web-0.0.1-SNAPSHOT.jar`

#### 2.4.2 前端Vue打包

进入前端工程目录：

```bash
# 安装依赖（首次执行）
npm install

# 生产打包
npm run build
```

**产物**：项目根目录 `dist` 文件夹（存放index.html、js、css、图片）

### 2.5 文件上传至服务器

#### 2.5.1 后端Jar上传

1. **服务器创建后端存放目录**

```bash
mkdir -p /opt/roomappoint
mkdir -p /opt/roomappoint/uploads

# 授权上传文件夹读写
chmod 755 /opt/roomappoint/uploads
```

2. **使用FTP工具上传**：将 `web-0.0.1-SNAPSHOT.jar` 上传到 `/opt/roomappoint`

#### 2.5.2 前端静态资源上传

1. **创建Nginx前端目录**

```bash
mkdir -p /var/www/roomappoint
chown nginx:nginx /var/www/roomappoint
chmod 755 /var/www/roomappoint
```

2. **清空旧前端文件**

```bash
rm -rf /var/www/roomappoint/*
```

3. **上传前端文件**：将本地 `dist` 内部全部文件上传（不要上传dist文件夹本身）

### 2.6 Nginx站点配置（反向代理+HTTPS）

#### 2.6.1 新建站点配置文件

```bash
vim /etc/nginx/conf.d/roomappoint.conf
```

**完整配置模板**：

```nginx
# HTTP 80端口：全部自动跳转到HTTPS
server {
    listen       80;
    server_name  smallv.fun www.smallv.fun;
    # 永久重定向到https
    return 301 https://$host$request_uri;
}

# HTTPS 443 正式网站服务
server {
    listen       443 ssl;
    server_name  smallv.fun www.smallv.fun;
    charset utf-8;

    ssl_certificate /etc/nginx/ssl/www.smallv.fun.pem;
    ssl_certificate_key /etc/nginx/ssl/www.smallv.fun.key;

    # SSL安全优化配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 安全防护请求头
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options SAMEORIGIN always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options nosniff always;

    # 前端SPA页面，刷新404重定向
    location / {
        root   /var/www/roomappoint;
        index  index.html;
        try_files $uri $uri/ /index.html;
        expires 1h;
        add_header Cache-Control "no-cache";
    }

    # 后端API反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:7245/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }

    # WebSocket实时推送
    location /ws {
        proxy_pass http://127.0.0.1:7245/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 300s;
    }

    # 上传图片静态资源
    location /uploads/ {
        alias /opt/roomappoint/uploads/;
        expires 30d;
        add_header Cache-Control public;
    }

    # 禁止访问隐藏文件
    location ~ /\. {
        deny all;
    }

    # 错误页面
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /var/www/roomappoint;
    }
}
```

#### 2.6.2 Nginx生效命令

```bash
# 校验配置语法
nginx -t

# 重载配置，不中断服务
systemctl reload nginx

# 设置开机自启（已安装nginx默认开启）
systemctl enable nginx

# 查看运行状态
systemctl status nginx
```

### 2.7 Systemd 配置SpringBoot后端开机自启动服务

#### 2.7.1 创建服务单元文件

```bash
vim /etc/systemd/system/roomappoint.service
```

**完整service模板（适配2核2G低配内存）**：

```ini
[Unit]
Description=RoomAppoint自习室预约后端服务
After=network.target mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/roomappoint
ExecStart=/usr/bin/java -Xms384m -Xmx768m -jar web-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

#### 2.7.2 Systemd 全套操作命令

```bash
# 1. 重载systemd识别新服务
systemctl daemon-reload

# 2. 设置开机自启
systemctl enable roomappoint

# 3. 立即启动后端服务
systemctl start roomappoint

# 4. 查看运行状态
systemctl status roomappoint

# 5. 实时查看后端运行日志
journalctl -u roomappoint -f

# 6. 重启后端（更新jar包后执行）
systemctl restart roomappoint

# 7. 关闭开机自启
systemctl disable roomappoint

# 8. 停止当前运行的后端
systemctl stop roomappoint
```

### 2.8 部署完成验证

#### 2.8.1 后端接口测试

```bash
curl http://localhost:7245/Banner/List -X POST -H "Content-Type: application/json" -d '{}'
```

返回JSON数据代表后端正常：

```json
{"Success":true,"Data":{"Items":[...],"TotalCount":3},"Code":200,"Msg":"操作成功"}
```

#### 2.8.2 前端页面测试

浏览器访问域名 `https://smallv.fun`，打开前端页面，接口无502/404即部署全部完成。

### 2.9 迭代更新流程

#### 2.9.1 更新流程概述

```
本地开发 → 本地测试 → 打包 → 上传服务器 → 重启服务 → 验证
```

#### 2.9.2 后端更新步骤

**步骤1：本地修改代码并测试**

```bash
# 进入后端目录
cd backend

# 本地运行测试
mvn spring-boot:run
```

**步骤2：重新打包**

```bash
mvn clean package -DskipTests
```

**步骤3：上传新Jar包到服务器**

```bash
# 使用SCP上传（推荐）
scp target/web-0.0.1-SNAPSHOT.jar root@服务器IP:/opt/roomappoint/

# 或使用FTP工具上传覆盖
```

**步骤4：重启后端服务**

```bash
# 重启后端服务（自动热部署）
systemctl restart roomappoint

# 查看状态确认启动成功
systemctl status roomappoint
```

#### 2.9.3 前端更新步骤

**步骤1：本地修改代码并测试**

```bash
# 进入前端目录
cd frontend

# 本地开发模式运行
npm run serve
```

**步骤2：重新打包**

```bash
npm run build
```

**步骤3：清空服务器旧文件并上传新文件**

```bash
# SSH登录服务器
ssh root@服务器IP

# 清空旧前端文件
rm -rf /var/www/roomappoint/*

# 退出SSH，上传新dist内容
exit
scp -r dist/* root@服务器IP:/var/www/roomappoint/
```

**步骤4：刷新浏览器缓存**

```bash
# 强制刷新浏览器（Ctrl+Shift+R）
```

#### 2.9.4 数据库更新步骤

**方式1：手动执行SQL（推荐）**

```bash
# 登录MySQL
mysql -uroot -pRoomAppoint

# 执行更新SQL
source /path/to/update.sql;
```

**方式2：代码中添加迁移脚本（可选）**

使用 Flyway 或 MyBatis-Plus 自动迁移工具。

#### 2.9.5 回滚方案

**后端回滚**

```bash
# 停止当前服务
systemctl stop roomappoint

# 备份当前jar包
cp web-0.0.1-SNAPSHOT.jar web-0.0.1-SNAPSHOT.jar.bak

# 上传旧版本jar包覆盖
scp backup/web-0.0.1-SNAPSHOT.jar root@服务器IP:/opt/roomappoint/

# 重启服务
systemctl start roomappoint
```

**前端回滚**

```bash
# 清空当前前端文件
rm -rf /var/www/roomappoint/*

# 上传旧版本dist内容
scp -r backup/dist/* root@服务器IP:/var/www/roomappoint/
```

#### 2.9.6 更新检查清单

| 检查项 | 说明 |
| :--- | :--- |
| 数据库连接 | 确认数据库密码、端口正确 |
| 文件上传目录 | 确认 `/opt/roomappoint/uploads` 权限正确 |
| Nginx配置 | 确认反向代理路径正确 |
| SSL证书 | 确认证书文件存在且未过期 |
| 防火墙端口 | 确认7245端口已放行 |
| 服务器内存 | 确认内存充足（可用 `free -h` 查看） |

---

## 附录：常用运维命令

### A.1 系统状态

```bash
# 查看系统负载
uptime

# 查看内存使用
free -h

# 查看磁盘使用
df -h

# 查看进程
top
```

### A.2 日志查看

```bash
# 后端日志
journalctl -u roomappoint -f

# Nginx访问日志
tail -f /var/log/nginx/access.log

# Nginx错误日志
tail -f /var/log/nginx/error.log
```

### A.3 服务管理

```bash
# 查看所有服务状态
systemctl list-units --type=service

# 查看某个服务状态
systemctl status nginx
systemctl status mysqld
systemctl status roomappoint
```

### A.4 网络相关

```bash
# 查看端口占用
ss -tlnp

# 测试端口连通性
telnet localhost 7245

# 测试外部访问
curl https://smallv.fun
```

---

**项目版本**：v1.0  
**最后更新**：2026年7月