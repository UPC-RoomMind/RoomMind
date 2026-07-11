# AI 智能助手功能 —— 新增/修改文档

---

## 一、功能概览（当前实现状态）

| 项目 | 内容 |
|---|---|
| **功能名称** | AI 智能助手（基于 DeepSeek 大模型 + RAG 检索增强） |
| **功能描述** | 用户通过页面右下角悬浮气泡呼出聊天窗口，AI 基于**系统数据库的真实数据**回答问题 |
| **RAG 检索** | 根据用户问题关键词动态查询：自习室列表、座位状态、用户积分、用户预约记录 |
| **上下文支持** | 保留最近 10 条历史消息作为多轮对话上下文 |
| **快捷问题** | 6 个常见问题卡片式快捷按钮（📚 如何预约 / 🏠 有哪些自习室 / ⭐ 积分规则 / ❌ 取消预约 / 🎓 学习计划 / 💬 忘记密码） |
| **对话记录** | localStorage 本地保存，刷新页面自动恢复 |
| **打字机效果** | AI 回复逐字显示，模拟真实对话体验 |
| **前端入口** | 页面 **右下角悬浮气泡**（右下角动画图标，点击滑出聊天窗口；再点击最小化） |
| **形象设计** | 自定义 SVG 机器人图标（有眼睛、笑脸、天线），替换原来的芯片图标 |

---

## 二、新增/修改代码文件

### 2.1 后端（Java / Spring Boot）

| 路径 | 文件 | 状态 | 作用 |
|---|---|---|---|
| `backend/.../controller/` | **AIController.java** | ✨ 已有 | 接口入口（无需修改，自动兼容新字段） |
| `backend/.../service/` | **DeepSeekService.java** | ✨ 已有 | AI 服务接口 |
| `backend/.../service/impl/` | **DeepSeekServiceImpl.java** | ✏ 大幅修改 | 核心：将固定 system prompt 改为**动态 RAG 上下文**；调用 `aiContextService` 构建系统数据 |
| `backend/.../service/` | **AiContextService.java** | ✨ 新增 | RAG 上下文检索接口定义 |
| `backend/.../service/impl/` | **AiContextServiceImpl.java** | ✨ 新增 | **核心 RAG 实现**：关键词匹配 → 查数据库 → 构建结构化上下文 |
| `backend/.../dto/` | **ChatInput.java** | ✏ 修改 | 新增字段 `UserId`（用于查询用户个人数据） |
| `backend/.../dto/` | **ChatMessageDto.java** | ✨ 已有 | 聊天消息 DTO |

### 2.2 前端（Vue / ElementUI）

| 路径 | 文件 | 状态 | 作用 |
|---|---|---|---|
| `frontend/src/components/` | **AiChatWidget.vue** | ✨ 新增 | **悬浮气泡 + 弹窗聊天组件**（380px 弹窗 + SVG 机器人头像 + 打字机效果） |
| `frontend/src/views/Front/` | **AiAssistant.vue** | ✏ 保留 | 全屏聊天页面（路由仍注册，但当前不再作为导航入口） |

### 2.3 路由注册（修改已有文件）

| 文件 | 说明 |
|---|---|
| `frontend/src/router/index.js` | 已有路由 `/Front/AiAssistant`（保留，方便调试） |

### 2.4 前端布局（修改已有文件）

| 文件 | 修改内容 |
|---|---|
| `frontend/src/views/Front/Layout/index.vue` | ✅ 移除导航栏 "AI助手" 菜单项<br>✅ 在 `<router-view>` 后挂载 `<AiChatWidget />` 组件（全局悬浮） |

### 2.5 配置文件（修改已有文件）

| 文件 | 配置说明 |
|---|---|
| `backend/src/main/resources/application-dev.yml` | `app.deepseek` 配置节点（api-key、base-url、model、temperature、max-tokens、system-prompt） |

---

## 三、RAG（检索增强生成）工作原理

### 3.1 数据流

```
用户输入问题
    ↓
前端发送请求 →  { Message, History, UserId }
    ↓
AIController.chat()
    ↓
DeepSeekServiceImpl.chat()
    ├── 1. 校验 API Key
    ├── 2. ↓ RAG 检索 ↓
    │    ├── AiContextService.buildContext(问题, UserId)
    │    │    ├── 关键词"自习室/介绍" → roomService.list()
    │    │    ├── 关键词"座位/空闲" → seatService.count()
    │    │    ├── 关键词"积分" → integralService.list(UserId)
    │    │    ├── 关键词"我的/预约" → appointRecordService.list(UserId)
    │    │    └── 拼接：系统介绍 + 业务规则 + 真实数据
    │    └── 构建动态 system prompt（角色定位 + 上下文 + 输出格式）
    ├── 3. 调用 DeepSeek API（请求体含 system + history + user）
    ├── 4. 解析 JSON 响应
    └── 5. 返回 ChatMessageDto
    ↓
前端 → 打字机效果显示回复
```

### 3.2 支持的问答场景

| 用户问题类型 | 检索的数据 | 示例 |
|---|---|---|
| 自习室查询 | `Room` 表 + 各 `Seat` 统计 | "有哪些自习室？" → 列出真实自习室及座位数 |
| 座位状态 | `Seat` 表（按可用/维修分类统计） | "还有空座位吗？" → 返回总座位数和可用数 |
| 积分查询 | `Integral` 表（UserId 过滤） | "我的积分有多少？" → 返回用户真实积分 |
| 预约记录 | `AppointRecord` 表（UserId 过滤） | "我预约了哪些？" → 返回最近 3 条真实预约 |
| 系统使用说明 | 固定业务规则模板 | "怎么预约座位？" → 基于规则模板 + 实际数据指引 |

---

## 四、接口文档

详细接口文档已拆分至独立文件，请查看：

📄 **`docs/AI_ASSISTANT_API.md`**

**⚠️ 本次改动需注意**：`/AI/Chat` 接口请求体新增字段 `UserId`（Integer，可选）

---

## 五、DeepSeek API 配置说明

### 5.1 获取 API Key

1. 访问 https://platform.deepseek.com
2. 注册/登录账号
3. 进入「API Keys」页面生成新的 Key
4. 复制 Key（格式类似 `sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx`）

### 5.2 填入配置文件

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

> 注：`system-prompt` 目前已在代码中被动态 RAG 上下文覆盖，仅作为 GetSystemPrompt 接口的返回值保留。

### 5.3 字段说明

| 配置项 | 说明 |
|---|---|
| api-key | DeepSeek 平台的 API 密钥（必填） |
| base-url | DeepSeek API 基础地址，默认 `https://api.deepseek.com` |
| model | 使用的模型名称，默认 `deepseek-chat` |
| temperature | 输出随机性，范围 0~2，默认 0.7（越低越稳定，适合问答类） |
| max-tokens | 单次回复最大 token 数，默认 2048 |
| system-prompt | 系统提示词（保留字段，已被 RAG 动态上下文替代） |

---

## 六、文件位置总览

```
D:\net_race_new\RoomMind\
├── backend\src\main\java\com\example\web\
│   ├── controller\
│   │   └── AIController.java                     ← 接口入口（已存在）
│   ├── service\
│   │   ├── DeepSeekService.java                 ← AI 服务接口（已存在）
│   │   ├── AiContextService.java                ← ✨ 新增：RAG 检索接口
│   │   └── impl\
│   │       ├── DeepSeekServiceImpl.java         ← ✏ 修改：改为 RAG 动态上下文
│   │       └── AiContextServiceImpl.java        ← ✨ 新增：RAG 检索实现（关键词匹配 + 查库）
│   └── dto\
│       ├── ChatInput.java                       ← ✏ 修改：新增 UserId 字段
│       └── ChatMessageDto.java                  ← 已存在
│
├── frontend\src\
│   ├── components\
│   │   └── AiChatWidget.vue                     ← ✨ 新增：悬浮气泡弹窗聊天组件
│   ├── views\Front\
│   │   ├── AiAssistant.vue                      ← 保留：全屏聊天页（调试用）
│   │   └── Layout\index.vue                     ← ✏ 修改：移除导航项 + 挂载悬浮组件
│   └── router\index.js                          ← 保留：/Front/AiAssistant 路由
│
├── backend\src\main\resources\
│   └── application-dev.yml                      ← 已修改：app.deepseek 配置
│
└── docs\
    ├── AI_ASSISTANT_CHANGELOG.md                ← 本文档（本次更新）
    └── AI_ASSISTANT_API.md                      ← 接口文档（本次更新）
```