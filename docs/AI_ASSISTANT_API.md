# AI 智能助手 —— 接口文档

---

## 一、接口总览

| 接口 | 方法 | 路径 | 作用 |
|---|---|---|---|
| 聊天接口（含 RAG 上下文） | POST | `/AI/Chat` | 发送消息给 AI，获取基于系统数据的回复 |
| 系统提示词接口 | POST | `/AI/GetSystemPrompt` | 获取配置文件中的系统提示词（调试用） |

---

## 二、接口 1：发送消息给 AI（RAG 增强）

| 项 | 内容 |
|---|---|
| **请求方式** | POST |
| **接口路径** | `/AI/Chat` |
| **Content-Type** | `application/json` |
| **RAG 机制** | 后端会根据 `Message` 关键词和 `UserId` 自动查询数据库构建上下文 |

### 2.1 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| Message | String | 是 | 用户输入的问题内容 |
| History | Array | 否 | 历史对话记录，最多传最近 10 条，用于上下文理解 |
| History[n].Role | String | 否 | 消息角色：`user`（用户） / `assistant`（AI） |
| History[n].Content | String | 否 | 消息内容 |
| UserId | Integer | 否 | 当前登录用户 ID（传入后可查询用户积分、预约记录等个人数据） |
| Temperature | Double | 否 | 采样温度，默认 0.7（范围 0~2，越大越随机） |
| MaxTokens | Integer | 否 | 最大输出 token 数，默认 2048 |

### 2.2 请求示例

**示例 1：未登录用户的简单请求**
```json
{
    "Message": "有哪些自习室？"
}
```
> 后端行为：基于 Message 关键词检索 Room + Seat 表数据

**示例 2：已登录用户查询个人积分**
```json
{
    "Message": "我的积分有多少？",
    "UserId": 1001
}
```
> 后端行为：基于 UserId 从 Integral 表查询积分数据

**示例 3：带上下文的多轮对话**
```json
{
    "Message": "那积分怎么获得？",
    "UserId": 1001,
    "History": [
        { "Role": "user", "Content": "你好" },
        { "Role": "assistant", "Content": "您好！请问有什么可以帮助您？" },
        { "Role": "user", "Content": "积分规则是什么？" },
        { "Role": "assistant", "Content": "积分是预约和使用自习室获得的奖励..." }
    ],
    "Temperature": 0.7,
    "MaxTokens": 2048
}
```

### 2.3 响应参数

| 字段 | 类型 | 说明 |
|---|---|---|
| Success | Boolean | 请求是否成功 |
| Msg | String | 提示信息 |
| Data | Object | 响应数据 |
| Data.Role | String | 消息角色（固定为 `assistant`） |
| Data.Content | String | AI 回复的内容（可能包含换行符 `\n` 和加粗标记 `**文字**`） |
| Data.Time | String (DateTime) | 消息时间，格式 `yyyy-MM-ddTHH:mm:ss` |

### 2.4 响应示例

**正常响应（基于系统数据）：**
```json
{
    "Success": true,
    "Msg": "获取成功",
    "Data": {
        "Role": "assistant",
        "Content": "当前系统共有 3 个自习室：\n1. **阳光自习室**（位置：一楼东侧），共 20 个座位，可用 18 个\n2. **安静自习室**（位置：二楼），共 30 个座位，可用 25 个\n3. **深夜自习室**（位置：三楼），共 15 个座位，可用 15 个\n\n请进入自习室详情页查看座位图并预约。",
        "Time": "2026-07-11T14:20:30"
    }
}
```

**API Key 未配置的提示：**
```json
{
    "Success": true,
    "Msg": "获取成功",
    "Data": {
        "Role": "assistant",
        "Content": "⚠️ AI助手尚未配置。请联系管理员在 application-dev.yml 中设置有效的 DeepSeek API Key。\n\n配置路径：app.deepseek.api-key",
        "Time": "2026-07-11T14:20:30"
    }
}
```

**消息为空的失败响应：**
```json
{
    "Success": false,
    "Msg": "消息内容不能为空",
    "Data": null
}
```

---

## 三、接口 2：获取系统提示词

| 项 | 内容 |
|---|---|
| **请求方式** | POST |
| **接口路径** | `/AI/GetSystemPrompt` |
| **Content-Type** | `application/json` |

### 3.1 请求参数

无（不需要传任何参数）

请求体可写为空对象：
```json
{}
```

### 3.2 响应参数

| 字段 | 类型 | 说明 |
|---|---|---|
| Success | Boolean | 请求是否成功 |
| Msg | String | 提示信息 |
| Data | String | 系统提示词完整内容（来自 `application-dev.yml` 配置） |

### 3.3 响应示例

```json
{
    "Success": true,
    "Msg": "获取成功",
    "Data": "你是一个自习室预约系统的智能助手。你可以帮助用户：1. 查询和预约自习室座位；2. 解答系统使用问题；3. 提供学习建议。请使用简洁、友好的中文回答用户问题。如果涉及具体预约操作，请引导用户前往相关页面操作。"
}
```

> 注：此提示词仅作为配置返回值，实际对话已使用 RAG 动态上下文替代。

---

## 四、RAG 上下文的查询逻辑（内部说明）

后端 `AiContextServiceImpl.buildContext(question, userId)` 实现逻辑：

### 4.1 关键词匹配

| 触发关键词 | 查询的数据 |
|---|---|
| `自习室` / `哪些` / `介绍` / `room` / `有什么` / `列表` | `Room` 全表 + 每个自习室的座位数统计 |
| `座位` / `空闲` / `还有` / `预约` / `位置` | `Seat` 表按状态统计 |
| `积分` / `奖励` | `Integral` 表 + 用户个人积分（UserId 存在时） |
| `我的` / `预约记录` / `已经` / `之前` / `下次` | `AppointRecord` 表（UserId 过滤） |

### 4.2 系统固定上下文

无论查询何种问题，始终会附带：

- **系统介绍**：项目是一个基于 Spring Boot + Vue 的自习室预约管理系统
- **业务规则**：预约流程、取消规则、积分获得方式、座位状态说明、账号注册
- **当前时间**：系统时间（用于判断预约是否过期）

### 4.3 身份判断

- `UserId != null` → 已登录用户，可以查询个人数据（积分、预约记录）
- `UserId == null` → 未登录用户，仅可查询系统公共数据，含个人信息时提示登录

---

## 五、前端调用示例（Vue / this.$Post）

```javascript
// ============================================================
// 示例 1：最简单的单轮对话
// ============================================================
let { Data, Success, Msg } = await this.$Post('/AI/Chat', {
    Message: '有哪些自习室？'
});

if (Success) {
    console.log('AI 回复：', Data.Content);
} else {
    console.log('失败：', Msg);
}

// ============================================================
// 示例 2：已登录用户查询个人积分 + 上下文
// ============================================================
let history = [
    { Role: 'user', Content: '你好' },
    { Role: 'assistant', Content: '您好！请问有什么可以帮助您？' }
];

let { Data } = await this.$Post('/AI/Chat', {
    Message: '我的积分有多少？',
    UserId: this.$store.getters.UserId,   // ← 从 Vuex 获取当前用户 ID
    History: history,
    Temperature: 0.7,
    MaxTokens: 2048
});

console.log('AI 回复：', Data.Content);

// ============================================================
// 示例 3：获取系统提示词
// ============================================================
let { Data } = await this.$Post('/AI/GetSystemPrompt', {});
console.log('系统提示词：', Data);
```

---

## 六、字段命名约定

⚠️ **重要**：本项目的 JSON 字段名使用**首字母大写**的大驼峰命名（与项目其他 DTO 保持一致）。

| ❌ 不要这样写 | ✅ 请这样写 |
|---|---|
| `message` | `Message` |
| `content` | `Content` |
| `history` | `History` |
| `role` | `Role` |
| `userId` / `user_id` | `UserId` |
| `temperature` | `Temperature` |
| `max_tokens` | `MaxTokens` |

**错误示例**（会导致后端收到 `null`）：
```json
{
    "message": "你好"   // ❌ 小写，后端接收不到
}
```

**正确示例**：
```json
{
    "Message": "你好"   // ✅ 首字母大写
}
```

---

## 七、接口测试（curl / Postman）

### 使用 curl 测试

```bash
# 测试聊天接口
curl -X POST http://localhost:8080/AI/Chat \
  -H "Content-Type: application/json" \
  -d '{"Message":"有哪些自习室？"}'

# 测试聊天接口（带用户上下文）
curl -X POST http://localhost:8080/AI/Chat \
  -H "Content-Type: application/json" \
  -d '{"Message":"我的积分有多少？","UserId":1001}'

# 测试系统提示词接口
curl -X POST http://localhost:8080/AI/GetSystemPrompt \
  -H "Content-Type: application/json" \
  -d '{}'
```

### 使用 Postman 测试

1. 请求方法选 `POST`
2. URL 填 `http://localhost:8080/AI/Chat`
3. Headers 添加 `Content-Type: application/json`
4. Body 选 `raw` → `JSON`，填入：
   ```json
   {"Message": "有哪些自习室？", "UserId": 1001}
   ```
5. 点击 Send 查看响应

---

## 八、错误状态排查

| 现象 | 可能原因 | 解决方法 |
|---|---|---|
| 返回空内容或气泡无文字 | 字段名大小写不匹配 | 检查请求字段是否为 `Message`（首字母大写） |
| AI 回复"AI助手尚未配置" | `application-dev.yml` 中 `api-key` 仍是占位符 | 填入真实的 DeepSeek API Key 并重启后端 |
| AI 回复"服务调用失败：..." | API Key 无效或网络不通 | 检查 Key 是否正确，服务器能否访问 `api.deepseek.com` |
| AI 可以回答但回答的信息与系统数据不符 | RAG 检索失败或关键词不匹配 | 检查 `AiContextServiceImpl` 中的关键词列表；查看后端日志确认数据库查询是否有异常 |
| 无法查询个人积分/预约记录 | 请求未传 `UserId` 或用户未登录 | 前端从 `this.$store.getters.UserId` 获取用户 ID 并传入 |
| 请求超时 | 网络不稳定或 DeepSeek 服务繁忙 | 稍后重试，或增大超时时间（代码中已设 60 秒） |
| 前端弹窗按钮显示为圆形无图标 | 页面可能需要清理缓存后刷新 | Ctrl+Shift+R 强制刷新前端页面 |