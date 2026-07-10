# AI 智能助手 —— 接口文档

---

## 接口总览

| 接口 | 方法 | 路径 | 作用 |
|---|---|---|---|
| 聊天接口 | POST | `/AI/Chat` | 发送消息给 AI，获取回复 |
| 系统提示词接口 | POST | `/AI/GetSystemPrompt` | 获取当前使用的系统提示词 |

---

## 接口 1：发送消息给 AI

| 项 | 内容 |
|---|---|
| **请求方式** | POST |
| **接口路径** | `/AI/Chat` |
| **Content-Type** | `application/json` |

### 1.1 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| Message | String | 是 | 用户输入的问题内容 |
| History | Array | 否 | 历史对话记录，用于上下文理解，最多传最近 10 条 |
| History[n].Role | String | 否 | 消息角色：`user`（用户） / `assistant`（AI） |
| History[n].Content | String | 否 | 消息内容 |
| Temperature | Double | 否 | 采样温度，默认 0.7（范围 0~2，越大越随机） |
| MaxTokens | Integer | 否 | 最大输出 token 数，默认 2048 |

### 1.2 请求示例

**简单请求（无历史）：**
```json
{
    "Message": "如何预约自习室？"
}
```

**带上下文的多轮对话：**
```json
{
    "Message": "那积分怎么获得？",
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

### 1.3 响应参数

| 字段 | 类型 | 说明 |
|---|---|---|
| Success | Boolean | 请求是否成功 |
| Msg | String | 提示信息 |
| Data | Object | 响应数据 |
| Data.Role | String | 消息角色（固定为 `assistant`） |
| Data.Content | String | AI 回复的内容（可能包含换行符 `\n`） |
| Data.Time | String (DateTime) | 消息时间，格式 `yyyy-MM-ddTHH:mm:ss` |

### 1.4 响应示例

**正常响应：**
```json
{
    "Success": true,
    "Msg": "获取成功",
    "Data": {
        "Role": "assistant",
        "Content": "预约自习室步骤如下：\n1. 登录账号后进入首页\n2. 选择心仪的自习室点击进入详情\n3. 选择预约时间和座位\n4. 点击预约下单即可完成",
        "Time": "2026-07-10T15:30:00"
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
        "Time": "2026-07-10T15:30:00"
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

## 接口 2：获取系统提示词

| 项 | 内容 |
|---|---|
| **请求方式** | POST |
| **接口路径** | `/AI/GetSystemPrompt` |
| **Content-Type** | `application/json` |

### 2.1 请求参数

无（不需要传任何参数）

请求体可写为空对象：
```json
{}
```

### 2.2 响应参数

| 字段 | 类型 | 说明 |
|---|---|---|
| Success | Boolean | 请求是否成功 |
| Msg | String | 提示信息 |
| Data | String | 系统提示词完整内容 |

### 2.3 响应示例

```json
{
    "Success": true,
    "Msg": "获取成功",
    "Data": "你是一个自习室预约系统的智能助手。你可以帮助用户：1. 查询和预约自习室座位；2. 解答系统使用问题；3. 提供学习建议。请使用简洁、友好的中文回答用户问题。如果涉及具体预约操作，请引导用户前往相关页面操作。"
}
```

---

## 前端调用示例（Vue / this.$Post）

```javascript
// ============================================================
// 示例 1：最简单的单轮对话
// ============================================================
let { Data, Success, Msg } = await this.$Post('/AI/Chat', {
    Message: '如何预约自习室？'
});

if (Success) {
    console.log('AI 回复：', Data.Content);
} else {
    console.log('失败：', Msg);
}

// ============================================================
// 示例 2：带历史上下文的多轮对话
// ============================================================
let history = [
    { Role: 'user', Content: '你好' },
    { Role: 'assistant', Content: '您好！请问有什么可以帮助您？' }
];

let { Data } = await this.$Post('/AI/Chat', {
    Message: '积分规则是什么？',
    History: history,
    Temperature: 0.8,
    MaxTokens: 1024
});

this.aiReply = Data.Content;

// ============================================================
// 示例 3：获取系统提示词
// ============================================================
let { Data } = await this.$Post('/AI/GetSystemPrompt', {});
console.log('系统提示词：', Data);
```

---

## 字段命名约定

⚠️ **重要**：本项目的 JSON 字段名使用**首字母大写**的大驼峰命名（与项目其他 DTO 保持一致）。

| ❌ 不要这样写 | ✅ 请这样写 |
|---|---|
| `message` | `Message` |
| `content` | `Content` |
| `history` | `History` |
| `role` | `Role` |
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

## 接口测试（curl / Postman）

### 使用 curl 测试

```bash
# 测试接口 1
curl -X POST http://localhost:8080/AI/Chat \
  -H "Content-Type: application/json" \
  -d '{"Message":"如何预约自习室？"}'

# 测试接口 2
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
   {"Message": "如何预约自习室？"}
   ```
5. 点击 Send

---

## 错误状态排查

| 现象 | 可能原因 | 解决方法 |
|---|---|---|
| 返回空内容或气泡无文字 | 字段名大小写不匹配 | 检查请求字段是否为 `Message`（首字母大写） |
| AI 回复"API Key 未配置" | `application-dev.yml` 中 `api-key` 仍是占位符 | 填入真实的 DeepSeek API Key 并重启后端 |
| AI 回复"服务调用失败：..." | API Key 无效或网络不通 | 检查 Key 是否正确，服务器能否访问 `api.deepseek.com` |
| 请求超时 | 网络不稳定或 DeepSeek 服务繁忙 | 稍后重试，或增大超时时间（代码中已设 60 秒） |