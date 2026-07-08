# RoomAppoint v2.0 进阶扩展方案 - 算法深度文档

---

## 一、算法引擎架构

### 1.1 算法层架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                      算法引擎层                                     │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │
│  │  专注度计算   │  │   推荐算法    │  │  遗忘曲线    │             │
│  │   模型        │  │  (协同过滤)   │  │ (SM-2改进)   │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                 │                 │                       │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐             │
│  │  搜索排序    │  │  异常检测    │  │  学习路径    │             │
│  │ (TF-IDF+BERT)│  │   (LSTM)     │  │ (遗传算法)   │             │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘             │
│         │                 │                 │                       │
│  ┌──────▼───────┐  ┌──────▼───────┐                              │
│  │  内容识别    │  │  人脸检测CNN  │                              │
│  │   (OCR)      │  │              │                              │
│  └──────────────┘  └──────────────┘                              │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 算法分类与用途

| 算法类别 | 算法名称 | 应用场景 | 核心价值 |
| :--- | :--- | :--- | :--- |
| **机器学习** | 专注度计算模型 | 实时监测学习专注度 | 量化学习状态 |
| **推荐系统** | 协同过滤+内容推荐 | 学习资源推荐 | 个性化推荐 |
| **时序算法** | SM-2改进遗忘曲线 | 复习时间点计算 | 科学复习调度 |
| **信息检索** | TF-IDF+BERT混合排序 | 搜索排序 | 精准搜索结果 |
| **深度学习** | LSTM异常检测 | 异常行为识别 | 智能预警 |
| **进化算法** | 遗传算法学习路径 | 学习路径规划 | 最优学习顺序 |
| **计算机视觉** | CNN人脸检测 | 人脸签到、表情识别 | 智能身份验证 |
| **OCR** | 文本识别 | 笔记内容识别 | 智能笔记分析 |

---

## 二、专注度计算模型

### 2.1 模型定义

专注度计算模型综合考虑用户的面部表情、头部姿态、注视方向和持续时间，输出一个0-100的专注度分数。

### 2.2 数学公式

```
FocusScore = ω₁ × EmotionScore + ω₂ × PoseScore + ω₃ × GazeScore + ω₄ × DurationScore

其中：
- ω₁ + ω₂ + ω₃ + ω₄ = 1（权重归一化）
- 默认权重：ω₁=0.35, ω₂=0.25, ω₃=0.25, ω₄=0.15
```

**EmotionScore（表情分数）**：
```
EmotionScore = Σ(emotion_weight[e] × confidence[e])

emotion_weight = {
    "concentrated": 1.0,
    "neutral": 0.7,
    "happy": 0.5,
    "sad": 0.3,
    "angry": 0.2,
    "surprised": 0.3,
    "sleepy": 0.1
}
```

**PoseScore（姿态分数）**：
```
PoseScore = 1 - (|pitch| + |yaw| + |roll|) / 180

其中pitch、yaw、roll为头部偏转角（度）
```

**GazeScore（注视分数）**：
```
GazeScore = exp(-d² / (2σ²))

其中d为视线偏离屏幕中心的距离，σ为标准差参数
```

**DurationScore（持续分数）**：
```
DurationScore = min(t / T, 1)

其中t为连续专注时间，T为目标持续时间（默认30分钟）
```

### 2.3 伪代码实现

```python
def calculate_focus_score(emotion_data, pose_data, gaze_data, duration):
    # 表情分数计算
    emotion_weights = {"concentrated": 1.0, "neutral": 0.7, "happy": 0.5, 
                       "sad": 0.3, "angry": 0.2, "surprised": 0.3, "sleepy": 0.1}
    emotion_score = sum(emotion_weights.get(e, 0) * c for e, c in emotion_data.items())
    
    # 姿态分数计算
    pitch, yaw, roll = pose_data["pitch"], pose_data["yaw"], pose_data["roll"]
    pose_score = 1 - (abs(pitch) + abs(yaw) + abs(roll)) / 180
    
    # 注视分数计算
    d = gaze_data["deviation"]
    sigma = 50  # 像素
    gaze_score = math.exp(-d**2 / (2 * sigma**2))
    
    # 持续分数计算
    T = 30 * 60  # 30分钟
    duration_score = min(duration / T, 1)
    
    # 加权求和
    weights = [0.35, 0.25, 0.25, 0.15]
    scores = [emotion_score, pose_score, gaze_score, duration_score]
    focus_score = sum(w * s for w, s in zip(weights, scores))
    
    return min(100, max(0, focus_score * 100))
```

### 2.4 复杂度分析

| 维度 | 复杂度 | 说明 |
| :--- | :--- | :--- |
| **时间复杂度** | O(1) | 固定数量的特征计算 |
| **空间复杂度** | O(1) | 常数空间存储权重和参数 |
| **实时性** | <100ms | 可满足实时监测需求 |

---

## 三、推荐算法

### 3.1 混合推荐架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    混合推荐系统                                  │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐    ┌──────────────────┐                  │
│  │   协同过滤        │    │   内容推荐        │                  │
│  │ (User-Item)      │    │ (Content-Based)  │                  │
│  └────────┬─────────┘    └────────┬─────────┘                  │
│           │                       │                            │
│           ▼                       ▼                            │
│  ┌──────────────────────────────────────────┐                  │
│  │           结果融合层                      │                  │
│  │   Weighted Hybrid / Rank Fusion          │                  │
│  └────────────────┬─────────────────────────┘                  │
│                   │                                            │
│                   ▼                                            │
│  ┌──────────────────────────────────────────┐                  │
│  │           排序输出                        │                  │
│  │   Top-K推荐结果                          │                  │
│  └──────────────────────────────────────────┘                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 协同过滤算法

**用户-物品评分矩阵**：

```
        Resource1  Resource2  Resource3  Resource4
User1     5          4          0          3
User2     0          5          4          0
User3     4          0          5          4
User4     3          4          0          5
```

**基于用户的协同过滤（User-Based CF）**：

```
相似度计算（余弦相似度）：
sim(u, v) = Σ(r_ui × r_vi) / (||r_u|| × ||r_v||)

预测评分：
r_uj = r_u_avg + Σ(sim(u, v) × (r_vj - r_v_avg)) / Σ|sim(u, v)|
```

**伪代码**：

```python
def user_based_cf(user_id, item_id, rating_matrix, top_k=5):
    # 计算目标用户与其他用户的相似度
    user_ratings = rating_matrix[user_id]
    similarities = {}
    
    for other_user in rating_matrix:
        if other_user == user_id:
            continue
        other_ratings = rating_matrix[other_user]
        
        # 余弦相似度
        dot_product = sum(a * b for a, b in zip(user_ratings, other_ratings) if a > 0 and b > 0)
        norm_u = math.sqrt(sum(a**2 for a in user_ratings if a > 0))
        norm_v = math.sqrt(sum(b**2 for b in other_ratings if b > 0))
        
        if norm_u > 0 and norm_v > 0:
            similarities[other_user] = dot_product / (norm_u * norm_v)
    
    # 取Top-K相似用户
    top_users = sorted(similarities.items(), key=lambda x: x[1], reverse=True)[:top_k]
    
    # 预测评分
    numerator = sum(sim * rating_matrix[u][item_id] for u, sim in top_users if rating_matrix[u][item_id] > 0)
    denominator = sum(sim for u, sim in top_users if rating_matrix[u][item_id] > 0)
    
    return numerator / denominator if denominator > 0 else 0
```

### 3.3 内容推荐算法

**基于内容的推荐**：

```
物品特征向量：
Resource_i = [category_score, difficulty_score, subject_score, popularity_score]

用户偏好向量（基于历史行为）：
User_u = Σ(Resource_i × interaction_weight) / Σ(interaction_weight)

相似度计算：
sim(u, i) = cos(User_u, Resource_i)
```

### 3.4 混合策略

```
最终评分 = α × CF_score + (1-α) × CB_score

其中α ∈ [0, 1]，可根据冷启动情况动态调整：
- 新用户（冷启动）：α = 0.3（侧重内容推荐）
- 活跃用户：α = 0.7（侧重协同过滤）
```

### 3.5 复杂度分析

| 算法 | 时间复杂度 | 空间复杂度 | 适用场景 |
| :--- | :--- | :--- | :--- |
| **User-Based CF** | O(n × m) | O(n × m) | 用户相似度高 |
| **Item-Based CF** | O(m²) | O(m²) | 物品相似度高 |
| **Content-Based** | O(m × k) | O(m × k) | 冷启动用户 |
| **矩阵分解** | O(n × m × f) | O((n+m) × f) | 大规模数据 |

---

## 四、遗忘曲线复习算法

### 4.1 SM-2算法改进

**原始SM-2算法流程**：

```
1. 初始化：
   - EF = 2.5（易用因子）
   - I = 1（间隔天数）
   - R = 0（重复次数）

2. 每次复习后：
   - 如果评分 < 3：重置 EF=2.5, I=1, R=0
   - 如果评分 ≥ 3：
     EF = EF + (0.1 - (5 - score) × (0.08 + (5 - score) × 0.02))
     如果 EF < 1.3：EF = 1.3
     
     如果 R = 0：I = 1
     如果 R = 1：I = 6
     如果 R > 1：I = I × EF
     
     R = R + 1

3. 返回下次复习间隔 I
```

### 4.2 改进策略

**改进1：引入专注度因子**

```
EF_new = EF_old × (1 + 0.05 × (FocusScore - 50) / 100)
```

**改进2：引入学习难度因子**

```
difficulty_factor = 1 / (1 + exp(-(difficulty - 3)))
EF_new = EF_old × difficulty_factor
```

**改进3：引入时间衰减**

```
实际间隔与计划间隔的比值：ratio = actual_interval / planned_interval
adjustment = exp(-(ratio - 1)² / 2)
EF_new = EF_old × adjustment
```

### 4.3 伪代码实现

```python
def sm2_algorithm(score, ef, interval, repetitions, focus_score=None, difficulty=3):
    if score < 3:
        return {"ef": 2.5, "interval": 1, "repetitions": 0}
    
    # 基础EF调整
    ef = ef + (0.1 - (5 - score) * (0.08 + (5 - score) * 0.02))
    ef = max(1.3, ef)
    
    # 专注度因子调整
    if focus_score:
        ef = ef * (1 + 0.05 * (focus_score - 50) / 100)
    
    # 难度因子调整
    difficulty_factor = 1 / (1 + math.exp(-(difficulty - 3)))
    ef = ef * difficulty_factor
    
    # 计算新间隔
    if repetitions == 0:
        new_interval = 1
    elif repetitions == 1:
        new_interval = 6
    else:
        new_interval = interval * ef
    
    return {
        "ef": ef,
        "interval": round(new_interval),
        "repetitions": repetitions + 1,
        "next_review_date": datetime.now() + timedelta(days=round(new_interval))
    }
```

### 4.4 算法评估

| 指标 | 说明 | 目标值 |
| :--- | :--- | :--- |
| **记忆保留率** | 复习后的知识记忆率 | >85% |
| **最优复习间隔** | 理论最优与实际间隔的偏差 | <20% |
| **计算复杂度** | 单次复习计算时间 | O(1) |
| **自适应能力** | 根据用户表现动态调整 | 强 |

---

## 五、搜索排序算法

### 5.1 TF-IDF + BERT混合排序

```
┌─────────────────────────────────────────────────────────────────┐
│                    搜索排序流程                                  │
├─────────────────────────────────────────────────────────────────┤
│  输入查询 → 分词 → TF-IDF匹配 → BERT语义匹配 → 结果融合 → 输出  │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 TF-IDF算法

**词频（TF）**：

```
tf(t, d) = count(t, d) / count(w, d)

其中count(t, d)为词t在文档d中的出现次数
```

**逆文档频率（IDF）**：

```
idf(t) = log(N / (1 + df(t)))

其中N为总文档数，df(t)为包含词t的文档数
```

**TF-IDF分数**：

```
tfidf(t, d) = tf(t, d) × idf(t)
```

### 5.3 BERT语义匹配

**使用预训练BERT模型计算查询与文档的语义相似度**：

```python
def bert_semantic_match(query, document):
    # 构建输入
    input_ids = tokenizer.encode(query, document, max_length=512, truncation=True)
    
    # 获取[CLS]向量
    outputs = bert_model(torch.tensor([input_ids]))
    cls_vector = outputs.last_hidden_state[:, 0, :]
    
    # 计算余弦相似度（可选：使用专门的匹配层）
    # 这里使用Cross-Encoder架构
    similarity = cross_encoder.predict([(query, document)])[0]
    
    return similarity
```

### 5.4 混合排序公式

```
FinalScore = α × TFIDF_Score + (1-α) × BERT_Score + β × BM25_Score

其中：
- α = 0.4（TF-IDF权重）
- 1-α = 0.4（BERT语义权重）
- β = 0.2（BM25权重）
```

### 5.5 BM25算法

```
BM25(d, q) = Σ( idf(t) × (tf(t,d) × (k₁ + 1)) / (tf(t,d) + k₁ × (1 - b + b × |d|/avgdl)) )

其中：
- k₁：词频饱和参数，默认1.2
- b：文档长度归一化参数，默认0.75
- |d|：文档长度
- avgdl：平均文档长度
```

### 5.6 复杂度分析

| 算法 | 时间复杂度 | 空间复杂度 | 说明 |
| :--- | :--- | :--- | :--- |
| **TF-IDF** | O(n × m) | O(n × m) | n文档数，m词汇数 |
| **BM25** | O(n × m) | O(n × m) | 比TF-IDF更优的相关性 |
| **BERT** | O(L² × d) | O(L × d) | L序列长度，d隐藏层维度 |
| **混合排序** | O(n × m + L² × d) | O(n × m + L × d) | 综合复杂度 |

---

## 六、异常行为检测算法

### 6.1 LSTM时序异常检测

**模型架构**：

```
┌─────────────────────────────────────────────────────────────────┐
│                    LSTM异常检测模型                              │
├─────────────────────────────────────────────────────────────────┤
│  Input: [focus_score, duration, emotion, pose]                 │
│           │                                                    │
│           ▼                                                    │
│  ┌──────────────────┐                                          │
│  │   LSTM Layer 1   │                                          │
│  │  (hidden_size=64)│                                          │
│  └────────┬─────────┘                                          │
│           │                                                    │
│           ▼                                                    │
│  ┌──────────────────┐                                          │
│  │   LSTM Layer 2   │                                          │
│  │  (hidden_size=32)│                                          │
│  └────────┬─────────┘                                          │
│           │                                                    │
│           ▼                                                    │
│  ┌──────────────────┐                                          │
│  │   Dense Layer    │                                          │
│  │  (output_size=4) │                                          │
│  └────────┬─────────┘                                          │
│           │                                                    │
│           ▼                                                    │
│  Output: [reconstructed_focus, reconstructed_duration, ...]   │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 训练目标

**自编码器重构误差**：

```
Loss = Σ|x_i - x̂_i|²

其中x_i为输入特征，x̂_i为重构特征
```

**异常检测阈值**：

```
anomaly_score = ||x - x̂||₂

如果 anomaly_score > threshold，则判定为异常
```

### 6.3 伪代码实现

```python
class LSTMAnomalyDetector(nn.Module):
    def __init__(self, input_dim, hidden_dim, num_layers):
        super().__init__()
        self.lstm = nn.LSTM(input_dim, hidden_dim, num_layers, batch_first=True)
        self.fc = nn.Linear(hidden_dim, input_dim)
    
    def forward(self, x):
        lstm_out, _ = self.lstm(x)
        lstm_out = lstm_out[:, -1, :]  # 取最后一个时间步
        recon = self.fc(lstm_out)
        return recon

# 训练
model = LSTMAnomalyDetector(input_dim=4, hidden_dim=64, num_layers=2)
criterion = nn.MSELoss()
optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

for epoch in range(epochs):
    for batch in dataloader:
        recon = model(batch)
        loss = criterion(recon, batch[:, -1, :])
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

# 异常检测
def detect_anomaly(model, data, threshold):
    recon = model(data)
    anomaly_score = torch.norm(data[:, -1, :] - recon, dim=1).item()
    return anomaly_score > threshold, anomaly_score
```

### 6.4 异常类型识别

| 异常类型 | 特征模式 | 触发条件 |
| :--- | :--- | :--- |
| **离开座位** | 人脸消失，专注度骤降 | 专注度<20持续30秒 |
| **打瞌睡** | 闭眼，头部下垂 | 连续5帧闭眼 |
| **分心** | 注视偏离，表情变化 | 注视偏离>阈值持续10秒 |
| **长时间学习** | 学习时长>阈值 | 连续学习>2小时 |

### 6.5 LSTM模型训练配置

**训练数据要求**：

| 要求 | 说明 |
| :--- | :--- |
| **数据格式** | 时间序列数据，每5秒采样一次 |
| **特征维度** | 4维（专注度、时长、表情、姿态） |
| **序列长度** | 30个时间步（2.5分钟） |
| **样本数量** | 至少10000个训练样本 |
| **异常比例** | 正常样本:异常样本 = 4:1 |

**超参数配置**：

| 参数 | 值 | 说明 |
| :--- | :--- | :--- |
| **输入维度** | 4 | 特征数量 |
| **隐藏层维度** | 64 | LSTM隐藏单元数 |
| **层数** | 2 | LSTM层数 |
| **批量大小** | 32 | 训练批量 |
| **学习率** | 0.001 | Adam优化器学习率 |
| **训练轮数** | 100 | 训练周期 |
| **Dropout** | 0.2 | 防止过拟合 |

**评估指标**：

| 指标 | 公式 | 目标值 |
| :--- | :--- | :--- |
| **AUC-ROC** | - | >0.95 |
| **F1-Score** | 2×Precision×Recall/(Precision+Recall) | >0.9 |
| **Precision** | TP/(TP+FP) | >0.85 |
| **Recall** | TP/(TP+FN) | >0.85 |

---

## 七、学习路径规划算法

### 7.1 遗传算法学习路径规划

**问题定义**：在知识图谱中寻找最优学习路径，最大化知识掌握效率。

**染色体编码**：

```
染色体 = [知识点1, 知识点2, 知识点3, ..., 知识点n]

其中每个基因表示一个知识点的学习顺序
```

**适应度函数**：

```
fitness(route) = Σ(knowledge_gain[i] × prior_knowledge[i]) / total_time

其中：
- knowledge_gain[i]：学习第i个知识点的知识增益
- prior_knowledge[i]：学习第i个知识点前的先验知识
- total_time：总学习时间
```

**遗传操作**：

```python
def genetic_algorithm(population_size, generations, mutation_rate):
    # 初始化种群
    population = initialize_population(population_size)
    
    for gen in range(generations):
        # 计算适应度
        fitness_scores = [calculate_fitness(ind) for ind in population]
        
        # 选择（轮盘赌选择）
        selected = selection(population, fitness_scores)
        
        # 交叉（两点交叉）
        offspring = crossover(selected)
        
        # 变异（随机交换）
        offspring = mutate(offspring, mutation_rate)
        
        # 替换
        population = replace(population, offspring)
    
    # 返回最优个体
    best_ind = max(population, key=calculate_fitness)
    return best_ind
```

### 7.2 知识图谱路径搜索

**基于Dijkstra的学习路径搜索**：

```
节点：知识点
边：知识点之间的依赖关系（有向边）
权重：学习难度 × (1 - 先验知识掌握度)

路径代价 = Σ(weight[i])

目标：找到从起点到目标知识点的最小代价路径
```

---

## 八、人脸检测与表情识别

### 8.1 CNN人脸检测

**轻量级人脸检测模型（基于MobileNet）**：

```python
class FaceDetector(nn.Module):
    def __init__(self):
        super().__init__()
        self.backbone = MobileNetV2()
        self.classifier = nn.Sequential(
            nn.Conv2d(1280, 256, kernel_size=3, padding=1),
            nn.ReLU(),
            nn.Conv2d(256, 2 * 4, kernel_size=1)  # 2类（人脸/背景）× 4个坐标
        )
    
    def forward(self, x):
        features = self.backbone(x)
        output = self.classifier(features)
        return output
```

### 8.2 表情识别模型

**7分类表情识别**：

```
表情类别：中性、开心、悲伤、愤怒、惊讶、厌恶、恐惧

模型架构：
Input(48×48灰度图) → Conv → Pool → Conv → Pool → Conv → Pool → FC → Output(7)
```

**详细模型结构**：

| 层级 | 类型 | 参数 | 输出尺寸 |
| :--- | :--- | :--- | :--- |
| **Input** | 输入层 | 48×48×1 | 48×48 |
| **Conv1** | 卷积层 | 64@3×3, ReLU | 48×48×64 |
| **Pool1** | 池化层 | MaxPool 2×2 | 24×24×64 |
| **Conv2** | 卷积层 | 128@3×3, ReLU | 24×24×128 |
| **Pool2** | 池化层 | MaxPool 2×2 | 12×12×128 |
| **Conv3** | 卷积层 | 256@3×3, ReLU | 12×12×256 |
| **Pool3** | 池化层 | MaxPool 2×2 | 6×6×256 |
| **FC1** | 全连接层 | 1024, ReLU, Dropout(0.5) | 1024 |
| **FC2** | 全连接层 | 7, Softmax | 7 |

### 8.3 CNN模型训练配置

**训练数据要求**：

| 要求 | 人脸检测 | 表情识别 |
| :--- | :--- | :--- |
| **数据集** | WIDER Face / FDDB | FER-2013 / CK+ |
| **样本数量** | 至少50000张 | 至少35000张 |
| **图像尺寸** | 可变 | 48×48灰度 |
| **标注格式** | bounding box | 类别标签 |
| **数据增强** | 旋转、翻转、裁剪 | 旋转、平移、亮度调整 |

**超参数配置**：

| 参数 | 人脸检测 | 表情识别 |
| :--- | :--- | :--- |
| **学习率** | 0.001 | 0.0001 |
| **批量大小** | 64 | 128 |
| **训练轮数** | 50 | 200 |
| **优化器** | Adam | Adam |
| **损失函数** | Smooth L1 + BCE | Cross Entropy |
| **权重衰减** | 0.0005 | 0.0005 |

**评估指标**：

| 指标 | 人脸检测 | 表情识别 | 目标值 |
| :--- | :--- | :--- | :--- |
| **AP (Average Precision)** | >0.85 | - | - |
| **Recall** | >0.8 | - | - |
| **准确率** | - | >0.75 | - |
| **F1-Score** | - | >0.73 | - |
| **推理时间** | <50ms | <10ms | - |

### 8.4 复杂度分析

| 模型 | 参数数量 | 推理时间 | 适用场景 |
| :--- | :--- | :--- | :--- |
| **MobileNetV2人脸检测** | ~3.5M | <50ms | 实时人脸检测 |
| **CNN表情识别** | ~1M | <10ms | 实时表情分析 |
| **MTCNN人脸检测** | ~1M | <30ms | 高精度人脸检测 |

---

## 九、算法部署架构

### 9.1 算法服务化架构

```
┌─────────────────────────────────────────────────────────────────┐
│                    算法服务化部署                                │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │  专注度服务  │    │   推荐服务    │    │  搜索服务    │      │
│  │  (gRPC)      │    │  (gRPC)      │    │  (HTTP)      │      │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘      │
│         │                   │                   │                │
│         ▼                   ▼                   ▼                │
│  ┌───────────────────────────────────────────────┐               │
│  │              API网关 (Spring Cloud Gateway)   │               │
│  └────────────────────────┬──────────────────────┘               │
│                           │                                      │
│                           ▼                                      │
│              ┌──────────────────────┐                            │
│              │      主应用           │                            │
│              │   (Spring Boot)      │                            │
│              └──────────────────────┘                            │
└─────────────────────────────────────────────────────────────────┘
```

### 9.2 模型缓存策略

```python
# Redis缓存推荐结果
def get_recommendations(user_id):
    cache_key = f"rec:{user_id}"
    cached = redis.get(cache_key)
    
    if cached:
        return json.loads(cached)
    
    # 计算推荐
    recommendations = recommendation_service.get(user_id)
    
    # 缓存30分钟
    redis.set(cache_key, json.dumps(recommendations), ex=1800)
    
    return recommendations
```

---

## 十、算法评估指标

### 10.1 推荐系统评估

| 指标 | 公式 | 目标值 |
| :--- | :--- | :--- |
| **Precision@K** | TP / (TP + FP) | >0.7 |
| **Recall@K** | TP / (TP + FN) | >0.6 |
| **NDCG@K** | Σ(rel_i / log2(i+1)) / Σ(ideal_rel_i / log2(i+1)) | >0.75 |
| **MAP** | (1/n) × Σ(AP_i) | >0.65 |

### 10.2 搜索算法评估

| 指标 | 说明 | 目标值 |
| :--- | :--- | :--- |
| **MAP** | 平均精度均值 | >0.8 |
| **MRR** | 平均倒数排名 | >0.7 |
| **P@10** | 前10结果准确率 | >0.75 |
| **响应时间** | 搜索响应时间 | <200ms |

### 10.3 专注度模型评估

| 指标 | 说明 | 目标值 |
| :--- | :--- | :--- |
| **准确率** | 专注度分类准确率 | >85% |
| **F1-Score** | 综合评估指标 | >0.8 |
| **MAE** | 平均绝对误差 | <10 |
| **实时性** | 单次计算时间 | <100ms |

---

**文档版本**：v2.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月