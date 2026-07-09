# Nginx 配置说明文档

## 一、Nginx 文件位置

### 1.1 主配置文件

| 文件路径 | 说明 |
|---------|------|
| `/etc/nginx/nginx.conf` | Nginx 主配置文件，包含全局配置（user、worker_processes、events、http等） |
| `/etc/nginx/conf.d/*.conf` | 额外配置文件目录，被主配置文件 include 到 http 块内部 |

### 1.2 SSL 证书

| 文件路径 | 说明 |
|---------|------|
| `/etc/nginx/ssl/www.smallv.fun.pem` | SSL 证书文件（公钥） |
| `/etc/nginx/ssl/www.smallv.fun.key` | SSL 私钥文件 |

### 1.3 前端静态文件

| 文件路径 | 说明 |
|---------|------|
| `/var/www/roommind/` | Vue 前端打包后的 dist 文件存放目录 |

### 1.4 日志文件

| 文件路径 | 说明 |
|---------|------|
| `/var/log/nginx/access.log` | 访问日志，记录所有请求 |
| `/var/log/nginx/error.log` | 错误日志，记录配置错误和运行时错误 |

### 1.5 上传文件目录

| 文件路径 | 说明 |
|---------|------|
| `/root/roommind-prod/roommind-prod-upload/` | 生产环境上传文件目录 |
| `/root/roommind-dev/roommind-dev-upload/` | 测试环境上传文件目录 |

---

## 二、Nginx 配置结构

### 2.1 配置文件层次

```
/etc/nginx/nginx.conf          ← 主配置文件
├── user root;                 ← 运行用户
├── worker_processes auto;     ← 工作进程数
├── events { ... }             ← 事件配置（连接数等）
└── http {                     ← HTTP 协议配置
    ├── include /etc/nginx/conf.d/*.conf;  ← 包含额外配置文件
    └── ...
}

/etc/nginx/conf.d/roommind.conf  ← 项目配置文件（只包含 server 块）
├── server {                   ← HTTP 80端口，重定向到HTTPS
└── server {                   ← HTTPS 443端口，正式服务
    ├── ssl_certificate ...    ← SSL证书配置
    ├── location / { ... }     ← 前端页面
    ├── location /api/ { ... } ← 生产环境API代理
    ├── location /test-api/ { ... } ← 测试环境API代理
    ├── location /uploads/ { ... } ← 生产环境上传文件
    ├── location /test-uploads/ { ... } ← 测试环境上传文件
    ├── location /ws { ... }   ← 生产环境WebSocket
    └── location /test-ws { ... } ← 测试环境WebSocket
}
```

### 2.2 路径映射关系

| 访问路径 | Nginx 处理方式 | 实际目标 |
|---------|--------------|---------|
| `https://smallv.fun/` | `location /` | `/var/www/roommind/index.html`（前端页面） |
| `https://smallv.fun/api/xxx` | `location /api/` → `proxy_pass http://127.0.0.1:8090/` | 生产后端容器（端口8090） |
| `https://smallv.fun/test-api/xxx` | `location /test-api/` → `proxy_pass http://127.0.0.1:8080/` | 测试后端容器（端口8080） |
| `https://smallv.fun/uploads/xxx` | `location /uploads/` → `alias /root/roommind-prod/roommind-prod-upload/` | 生产环境上传文件 |
| `https://smallv.fun/test-uploads/xxx` | `location /test-uploads/` → `alias /root/roommind-dev/roommind-dev-upload/` | 测试环境上传文件 |
| `https://smallv.fun/ws` | `location /ws` → `proxy_pass http://127.0.0.1:8090/ws` | 生产环境WebSocket |
| `https://smallv.fun/test-ws` | `location /test-ws` → `proxy_pass http://127.0.0.1:8080/ws` | 测试环境WebSocket |

---

## 三、关键配置说明

### 3.1 HTTP 转 HTTPS

```nginx
server {
    listen 80;
    server_name smallv.fun www.smallv.fun;
    return 301 https://$host$request_uri;
}
```

- 监听 80 端口
- 匹配域名 smallv.fun 和 www.smallv.fun
- 将所有 HTTP 请求永久重定向到 HTTPS

### 3.2 SSL 证书配置

```nginx
ssl_certificate /etc/nginx/ssl/www.smallv.fun.pem;
ssl_certificate_key /etc/nginx/ssl/www.smallv.fun.key;

ssl_protocols TLSv1.2 TLSv1.3;
ssl_prefer_server_ciphers on;
ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:...;
ssl_session_cache shared:SSL:10m;
ssl_session_timeout 10m;
```

- 指定 SSL 证书和私钥路径
- 启用 TLSv1.2 和 TLSv1.3 协议
- 使用安全的加密套件
- 配置 SSL 会话缓存，提升性能

### 3.3 安全请求头

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Frame-Options SAMEORIGIN always;
add_header X-XSS-Protection "1; mode=block" always;
add_header X-Content-Type-Options nosniff always;
```

- **HSTS**：强制浏览器使用 HTTPS 访问，有效期1年
- **X-Frame-Options**：防止点击劫持攻击
- **X-XSS-Protection**：启用浏览器内置的 XSS 防护
- **X-Content-Type-Options**：防止 MIME 类型嗅探

### 3.4 前端 SPA 页面

```nginx
location / {
    root /var/www/roommind;
    index index.html;
    try_files $uri $uri/ /index.html;
    expires 1h;
    add_header Cache-Control "no-cache";
}
```

- `root`：指定前端文件根目录
- `index`：默认首页文件
- `try_files`：尝试匹配文件，匹配不到则返回 index.html（SPA 路由需要）
- `expires`：缓存时间
- `Cache-Control`：不缓存（前端开发模式）

### 3.5 API 反向代理

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:8090/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_connect_timeout 60s;
    proxy_read_timeout 60s;
    proxy_send_timeout 60s;
}
```

- `proxy_pass http://127.0.0.1:8090/`：转发请求到后端服务
- `proxy_set_header`：设置请求头，传递原始请求信息
- `proxy_connect_timeout`：连接超时时间
- `proxy_read_timeout`：读取超时时间

### 3.6 静态资源映射

```nginx
location /uploads/ {
    alias /root/roommind-prod/roommind-prod-upload/;
    expires 30d;
    add_header Cache-Control public;
}
```

- `alias`：将 `/uploads/` 路径映射到服务器本地目录
- `expires`：图片缓存30天
- `Cache-Control`：允许公共缓存

### 3.7 WebSocket 代理

```nginx
location /ws {
    proxy_pass http://127.0.0.1:8090/ws;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_read_timeout 300s;
}
```

- `proxy_http_version 1.1`：使用 HTTP/1.1 协议
- `Upgrade` 和 `Connection`：升级协议为 WebSocket
- `proxy_read_timeout`：长连接超时时间（300秒）

---

## 四、Nginx 常用命令

### 4.1 基本命令

```bash
# 验证配置文件是否正确
nginx -t

# 重新加载配置（不重启服务）
nginx -s reload

# 停止服务
nginx -s stop

# 启动服务
nginx

# 查看进程
ps aux | grep nginx
```

### 4.2 部署命令

```bash
# 上传配置文件
scp nginx.conf root@47.94.202.210:/etc/nginx/conf.d/roommind.conf

# 创建上传目录
mkdir -p /root/roommind-dev/roommind-dev-upload
mkdir -p /root/roommind-prod/roommind-prod-upload

# 验证并加载配置
nginx -t && nginx -s reload
```

---

## 五、目录结构总结

```
服务器根目录
├── etc/
│   └── nginx/
│       ├── nginx.conf               ← 主配置文件
│       ├── conf.d/
│       │   └── roommind.conf        ← 项目配置文件
│       └── ssl/
│           ├── www.smallv.fun.pem   ← SSL证书
│           └── www.smallv.fun.key   ← SSL私钥
├── usr/
│   └── share/nginx/html/            ← 默认静态文件目录（未使用）
├── var/
│   ├── www/roommind/                ← 前端dist文件
│   └── log/nginx/
│       ├── access.log               ← 访问日志
│       └── error.log                ← 错误日志
└── root/
    ├── roommind-dev/
    │   ├── roommind-dev-upload/     ← 测试环境上传文件
    │   ├── roommind-dev-logs/       ← 测试环境日志
    │   ├── roommind.jar
    │   ├── Dockerfile
    │   └── roommind-dev.yml
    └── roommind-prod/
        ├── roommind-prod-upload/    ← 生产环境上传文件
        ├── roommind-prod-logs/      ← 生产环境日志
        ├── roommind.jar
        ├── Dockerfile
        └── roommind-prod.yml
```

---

## 六、环境区分

### 6.1 生产环境

| 项目 | 值 |
|------|----|
| 后端端口 | 8090 |
| API路径 | `/api/` |
| 上传文件路径 | `/uploads/` → `/root/roommind-prod/roommind-prod-upload/` |
| WebSocket路径 | `/ws` |

### 6.2 测试环境

| 项目 | 值 |
|------|----|
| 后端端口 | 8080 |
| API路径 | `/test-api/` |
| 上传文件路径 | `/test-uploads/` → `/root/roommind-dev/roommind-dev-upload/` |
| WebSocket路径 | `/test-ws` |
