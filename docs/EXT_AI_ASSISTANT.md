# RoomMind v2.0 进阶扩展方案 - AI学习助手

---

## 一、功能概�?
AI学习助手是RoomMind v2.0的核心扩展模块，通过集成豆包、Kimi、ChatGPT等AI接口，为用户提供智能问答、学习规划、笔记分析、错题本和知识图谱等全方位AI辅助学习功能�?
### 1.1 功能架构

```
┌─────────────────────────────────────────────────────────�?�?                     AI学习助手                         �?├─────────────────────────────────────────────────────────�?�? ┌──────────�? ┌──────────�? ┌──────────�?            �?�? �?智能问答 �? �?学习规划 �? �?笔记分析 �?            �?�? └────┬─────�? └────┬─────�? └────┬─────�?            �?�?      �?            �?            �?                   �?�? ┌────▼─────�? ┌────▼─────�?                          �?�? �? 错题�? �? �?知识图谱 �?                          �?�? └──────────�? └──────────�?                          �?└──────────────────────────┬────────────────────────────�?                           �?                           �?┌─────────────────────────────────────────────────────────�?�?                     AI接口�?                          �?�? ┌──────────�?┌──────────�?┌──────────�?┌──────────�?  �?�? �?豆包API  �?�?Kimi API �?�?ChatGPT  �?�?本地模型  �?  �?�? └──────────�?└──────────�?└──────────�?└──────────�?  �?└─────────────────────────────────────────────────────────�?```

### 1.2 核心功能列表

| 功能 | 说明 | 优先�?|
| :--- | :--- | :--- |
| **智能问答** | 用户随时提问，AI提供专业解答和资源推�?| P0 |
| **学习规划** | 根据目标和截止日期，AI生成个性化学习计划 | P0 |
| **笔记分析** | 上传笔记图片，AI识别文字、提取关键点 | P1 |
| **智能错题�?* | 自动收集错题，AI分析错误原因和改进方�?| P0 |
| **知识图谱** | 构建学科知识图谱，支持智能关�?| P2 |

---

## 二、智能问�?
### 2.1 API接口

```
POST /AI/AskQuestion
Content-Type: application/json
```

**请求参数**�?
| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | �?| 用户ID |
| Question | String | �?| 问题内容 |
| Subject | String | �?| 学科（如：数学、英语） |
| Context | String | �?| 上下文信�?|
| History | Array | �?| 历史对话记录 |

**响应**�?
```json
{
    "Success": true,
    "Data": {
        "Answer": "这道题的解题思路如下...",
        "RelatedKnowledge": ["导数的定�?, "极限运算"],
        "RecommendedResources": ["https://xxx", "https://yyy"],
        "FollowUpQuestions": ["相关知识点还有哪些？", "如何应用到实际问题？"],
        "Difficulty": "medium",
        "TimeToUnderstand": "5分钟"
    }
}
```

### 2.2 前端组件设计

```vue
<template>
  <div class="ai-chat-container">
    <div class="chat-header">
      <h3>AI学习助手</h3>
      <el-select v-model="aiProvider" style="width: 120px">
        <el-option label="豆包" value="doubao"></el-option>
        <el-option label="Kimi" value="kimi"></el-option>
        <el-option label="ChatGPT" value="openai"></el-option>
      </el-select>
    </div>
    
    <div class="chat-messages">
      <div 
        v-for="(msg, index) in messages" 
        :key="index"
        :class="['message-item', msg.type]"
      >
        <div class="avatar">{{ msg.type === 'user' ? '👤' : '🤖' }}</div>
        <div class="message-content">
          <p>{{ msg.content }}</p>
          <div v-if="msg.resources && msg.resources.length" class="resources">
            <span>推荐资源�?/span>
            <a v-for="(res, i) in msg.resources" :key="i" :href="res.Url">{{ res.Name }}</a>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-select v-model="currentSubject" placeholder="选择学科">
        <el-option label="全部" value="all"></el-option>
        <el-option label="数学" value="数学"></el-option>
        <el-option label="英语" value="英语"></el-option>
        <el-option label="编程" value="编程"></el-option>
      </el-select>
      <el-input 
        v-model="inputMessage" 
        placeholder="输入问题..." 
        @keyup.enter="sendMessage"
      ></el-input>
      <el-button type="primary" @click="sendMessage">发�?/el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AIChat',
  data() {
    return {
      messages: [],
      inputMessage: '',
      aiProvider: 'doubao',
      currentSubject: 'all'
    }
  },
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      
      this.messages.push({
        type: 'user',
        content: this.inputMessage
      })
      
      const loadingId = this.$loading({ text: 'AI思考中...' })
      
      try {
        const res = await this.$http.post('/AI/AskQuestion', {
          UserId: this.$store.getters.userId,
          Question: this.inputMessage,
          Subject: this.currentSubject === 'all' ? '' : this.currentSubject
        })
        
        this.messages.push({
          type: 'ai',
          content: res.data.Data.Answer,
          resources: res.data.Data.RecommendedResources
        })
      } catch (e) {
        this.messages.push({
          type: 'ai',
          content: '抱歉，AI服务暂时不可用，请稍后重试�?
        })
      } finally {
        loadingId.close()
        this.inputMessage = ''
      }
    }
  }
}
</script>
```

---

## 三、学习规�?
### 3.1 API接口

```
POST /AI/GenerateStudyPlan
Content-Type: application/json
```

**请求参数**�?
| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | �?| 用户ID |
| Goal | String | �?| 学习目标（如：考研数学、英语四级） |
| Deadline | String | �?| 截止日期 |
| CurrentLevel | String | �?| 当前水平（初�?中级/高级�?|
| DailyAvailableHours | Int | �?| 每日可用学习时间（小时） |

**响应**�?
```json
{
    "Success": true,
    "Data": {
        "PlanId": 1,
        "Goal": "考研数学",
        "Deadline": "2026-12-20",
        "DurationDays": 165,
        "Phases": [
            {
                "PhaseName": "基础阶段",
                "StartDate": "2026-07-08",
                "EndDate": "2026-09-30",
                "Focus": ["高等数学", "线性代�?],
                "DailyHours": 4
            },
            {
                "PhaseName": "强化阶段",
                "StartDate": "2026-10-01",
                "EndDate": "2026-11-30",
                "Focus": ["真题演练", "专题突破"],
                "DailyHours": 6
            },
            {
                "PhaseName": "冲刺阶段",
                "StartDate": "2026-12-01",
                "EndDate": "2026-12-20",
                "Focus": ["模拟考试", "查漏补缺"],
                "DailyHours": 8
            }
        ],
        "DailyTasks": [
            {"Task": "复习导数章节", "Duration": 90, "Priority": "high"},
            {"Task": "�?0道极限习�?, "Duration": 60, "Priority": "medium"},
            {"Task": "整理错题�?, "Duration": 30, "Priority": "low"}
        ],
        "WeeklyReviewTime": "每周日下�?�?
    }
}
```

---

## 四、笔记分�?
### 4.1 API接口

```
POST /AI/AnalyzeNotes
Content-Type: multipart/form-data
```

**请求参数**�?
| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | �?| 用户ID |
| ImageFile | File | �?| 笔记照片 |

**响应**�?
```json
{
    "Success": true,
    "Data": {
        "TextContent": "导数的定义：当自变量的增量趋于零�?..",
        "KeyPoints": ["导数定义", "求导法则", "高阶导数"],
        "Summary": "本节主要介绍了导数的基本概念和运算方�?,
        "SuggestedTags": ["数学", "微积�?, "导数"],
        "RelatedQuestions": ["什么是导数�?, "导数的应用场景有哪些�?],
        "KnowledgeGraph": [
            {"Node": "导数", "Relation": "属于", "Target": "微积�?},
            {"Node": "导数", "Relation": "用于", "Target": "求极�?}
        ]
    }
}
```

---

## 五、智能错题本

### 5.1 数据库设�?
```sql
CREATE TABLE ai_wrong_question (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    QuestionContent TEXT NOT NULL,
    UserAnswer TEXT,
    CorrectAnswer TEXT,
    ErrorType VARCHAR(50) COMMENT '概念不清/计算错误/审题错误/粗心',
    Subject VARCHAR(50),
    KnowledgePoint VARCHAR(100),
    AnalyzeResult TEXT,
    ImprovementSuggestion TEXT,
    ReviewCount INT DEFAULT 0,
    LastReviewTime DATETIME,
    MasteryLevel DECIMAL(3,1) DEFAULT 0,
    NextReviewDate DATE,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

### 5.2 API接口

| API路径 | 方法 | 说明 |
| :--- | :--- | :--- |
| `/AI/WrongQuestion/Add` | POST | 添加错题 |
| `/AI/WrongQuestion/List` | POST | 获取错题列表 |
| `/AI/WrongQuestion/Analyze` | POST | AI分析错题 |
| `/AI/WrongQuestion/Review` | POST | 标记已复�?|
| `/AI/WrongQuestion/Delete` | POST | 删除错题 |

---

## 六、AI配置

### 6.1 配置文件

```yaml
app:
  ai:
    provider: doubao
    timeout: 30000
    retry-count: 3
    doubao:
      api-key: your-doubao-api-key
      api-url: https://api.doubao.com/v1/chat/completions
    kimi:
      api-key: your-kimi-api-key
      api-url: https://api.moonshot.cn/v1/chat/completions
    openai:
      api-key: your-openai-api-key
      api-url: https://api.openai.com/v1/chat/completions
    local:
      model-path: ./models/
      model-name: qwen2-7b-instruct
```

### 6.2 AI客户端实�?
```java
public interface AIClient {
    String chat(String prompt, String subject);
    String generateStudyPlan(String goal, String deadline, String level, int hours);
    String analyzeNotes(String imageBase64);
    String analyzeWrongQuestion(String question, String userAnswer, String correctAnswer);
}

@Component
public class DoubaoAIClient implements AIClient {
    @Value("${app.ai.doubao.api-key}")
    private String apiKey;
    
    @Value("${app.ai.doubao.api-url}")
    private String apiUrl;
    
    @Override
    public String chat(String prompt, String subject) {
        // 调用豆包API
        return callAPI(prompt);
    }
    
    // ... 其他方法实现
}
```

---

**文档版本**：v2.0  
**适用项目**：RoomMind 自习室预约系�? 
**最后更�?*�?026�?�


