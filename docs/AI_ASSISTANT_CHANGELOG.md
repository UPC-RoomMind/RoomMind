# AI 智能助手功能 —— 新增文档

---

## 一、新增功能概览

| 项目 | 内容 |
|---|---|
| **功能名称** | AI 智能助手（基于 DeepSeek 大模型） |
| **功能描述** | 用户可通过聊天窗口向 AI 提问，获取关于自习室预约、系统使用等问题的智能回答 |
| **支持上下文** | 支持多轮对话，保留最近 10 条历史消息作为上下文 |
| **快捷问题** | 内置 4 个常见问题快捷按钮，一键发送 |
| **对话记录** | 本地保存对话历史，刷新页面后自动恢复 |
| **入口位置** | 前台导航栏 → AI助手 / 管理员后台导航栏 → AI助手 |

---

## 二、新增代码文件

### 2.1 后端（Java / Spring Boot）

| 路径 | 文件 | 作用 |
|---|---|---|
| `backend/.../controller/` | **AIController.java** | AI聊天接口控制器，接收前端请求并返回响应 |
| `backend/.../service/` | **DeepSeekService.java** | AI服务接口定义 |
| `backend/.../service/impl/` | **DeepSeekServiceImpl.java** | AI服务实现，调用 DeepSeek API 并解析响应 |
| `backend/.../dto/` | **ChatInput.java** | 聊天请求输入数据传输对象（用户消息+历史记录） |
| `backend/.../dto/` | **ChatMessageDto.java** | 聊天消息数据传输对象（角色+内容+时间） |

### 2.2 前端（Vue / ElementUI）

| 路径 | 文件 | 作用 |
|---|---|---|
| `frontend/.../views/Front/` | **AiAssistant.vue** | AI助手聊天页面（前后台共用） |

### 2.3 路由注册（修改已有文件）

| 文件 | 新增路由 |
|---|---|
| `frontend/src/router/index.js` | `/Front/AiAssistant` → 前台 AI 助手页面 |
| `frontend/src/router/index.js` | `/Admin/AiAssistant` → 管理员后台 AI 助手页面 |

### 2.4 导航入口（修改已有文件）

| 文件 | 新增内容 |
|---|---|
| `frontend/src/views/Front/Layout/index.vue` | 导航栏新增 "AI助手" 菜单项（`el-icon-cpu` 图标） |

### 2.5 配置文件（修改已有文件）

| 文件 | 新增配置 |
|---|---|
| `backend/src/main/resources/application-dev.yml` | 新增 `app.deepseek` 配置节点（api-key、base-url、model、temperature、max-tokens、system-prompt） |

---

## 三、接口文档

详细接口文档已拆分至独立文件，请查看：

📄 **`docs/AI_ASSISTANT_API.md`**

包含内容：
- `/AI/Chat` — 聊天接口（请求/响应参数、JSON 示例）
- `/AI/GetSystemPrompt` — 系统提示词接口
- 前端调用代码示例（Vue）
- 字段命名约定（⚠️ 重要：首字母大写）
- curl / Postman 测试方式
- 错误状态排查表

---

## 四、DeepSeek API 配置说明

### 4.1 获取 API Key

1. 访问 https://platform.deepseek.com
2. 注册/登录账号
3. 进入「API Keys」页面生成新的 Key
4. 复制 Key（格式类似 `sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx`）

### 4.2 填入配置文件

编辑文件：`backend/src/main/resources/application-dev.yml`

```yaml
app:
  deepseek:
    api-key: sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx    # ← 你的真实 API Key
    base-url: https://api.deepseek.com
    model: deepseek-chat
    temperature: 0.7
    max-tokens: 2048
    system-prompt: 你是一个自习室预约系统的智能助手...
```

### 4.3 字段说明

| 配置项 | 说明 |
|---|---|
| api-key | DeepSeek 平台的 API 密钥（必填） |
| base-url | DeepSeek API 基础地址，默认 `https://api.deepseek.com` |
| model | 使用的模型名称，默认 `deepseek-chat` |
| temperature | 输出随机性，范围 0~2，默认 0.7 |
| max-tokens | 单次回复最大 token 数，默认 2048 |
| system-prompt | 系统提示词，定义 AI 的角色和行为 |

---

## 五、文件位置总览（便于查找）

```
D:\net_race_new\RoomMind\
├── backend\src\main\java\com\example\web\
│   ├── controller\
│   │   └── AIController.java                     ← 新增
│   ├── service\
│   │   ├── DeepSeekService.java                 ← 新增
│   │   └── impl\
│   │       └── DeepSeekServiceImpl.java         ← 新增
│   └── dto\
│       ├── ChatInput.java                       ← 新增
│       └── ChatMessageDto.java                  ← 新增
│
├── frontend\src\
│   ├── views\Front\
│   │   ├── AiAssistant.vue                      ← 新增
│   │   └── Layout\index.vue                     ← 已修改（新增导航入口）
│   └── router\index.js                          ← 已修改（新增路由）
│
├── backend\src\main\resources\
│   └── application-dev.yml                      ← 已修改（新增 deepseek 配置）
│
└── docs\
    └── AI_ASSISTANT_CHANGELOG.md                ← 本文档（新增）
```