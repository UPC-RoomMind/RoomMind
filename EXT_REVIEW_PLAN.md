# RoomAppoint v2.0 进阶扩展方案 - AI学习复习方案

---

## 一、功能概述

基于用户的学习数据、AI分析和艾宾浩斯遗忘曲线，自动生成个性化的学习复习方案。

### 1.1 复习流程

```
学习内容 → 记录学习时间 → AI分析掌握度 → 计算复习时间点 → 推送复习提醒 → 用户完成复习 → 更新掌握程度
```

---

## 二、API接口

### 2.1 生成复习计划

```
POST /AI/GenerateReviewPlan
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | 是 | 用户ID |
| Scope | String | 否 | 范围(全部/本周/指定科目) |
| Subject | String | 否 | 指定科目 |

**响应**：

```json
{
    "Success": true,
    "Data": {
        "PlanId": 1001,
        "Date": "2026-07-08",
        "ReviewItems": [
            {
                "Id": 1,
                "Content": "导数的定义",
                "Subject": "数学",
                "MasteryLevel": 6.5,
                "NextReviewTime": "2026-07-08 14:00",
                "Priority": "high",
                "ReviewMethod": "重做相关习题",
                "EstimatedTime": 30
            }
        ],
        "DailySummary": {
            "TotalItems": 15,
            "HighPriority": 5,
            "CompletedItems": 8,
            "CompletionRate": 53
        },
        "AIAdvice": "建议先复习掌握度较低的线性代数内容。"
    }
}
```

### 2.2 获取学习报告

```
POST /AI/GetStudyReport
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | 是 | 用户ID |
| Period | String | 是 | 周期(week/month/year) |

**响应**：

```json
{
    "Success": true,
    "Data": {
        "UserId": 1,
        "TotalStudyHours": 28,
        "AverageDailyHours": 4,
        "FocusRate": 85,
        "MostStudiedSubject": "数学",
        "SubjectDistribution": [
            {"Subject": "数学", "Hours": 12, "Percentage": 43},
            {"Subject": "英语", "Hours": 8, "Percentage": 29}
        ],
        "AIAdvice": "本周学习状态良好，建议继续保持。"
    }
}
```

---

## 三、数据库设计

```sql
CREATE TABLE study_plan (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    Goal VARCHAR(200),
    Deadline DATE,
    CurrentLevel VARCHAR(20),
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE review_plan (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    Content VARCHAR(500),
    Subject VARCHAR(50),
    MasteryLevel DECIMAL(3,1) DEFAULT 0,
    LastStudyTime DATETIME,
    NextReviewTime DATETIME,
    ReviewInterval INT,
    ReviewCount INT DEFAULT 0,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

---

**文档版本**：v2.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月