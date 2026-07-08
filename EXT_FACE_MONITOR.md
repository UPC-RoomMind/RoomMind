# RoomAppoint v2.0 进阶扩展方案 - 人脸识别与智能监测

---

## 一、功能概述

可选开启人脸识别功能，实现自动签到签退、专注度监测、学习内容识别和异常行为检测。

### 1.1 核心功能列表

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| **人脸签到** | 通过摄像头自动识别用户进行签到 | P1 |
| **专注度监测** | 通过面部表情分析专注程度，实时反馈 | P0 |
| **学习内容识别** | 通过摄像头识别学习内容（书籍、屏幕） | P2 |
| **异常检测** | 检测离开座位、打瞌睡、分心等行为 | P1 |
| **AI分析报告** | 生成学习行为分析报告 | P1 |
| **隐私保护** | 用户可自主开启/关闭，数据本地处理 | P0 |

---

## 二、API接口

```
POST /Face/StartMonitoring
Content-Type: application/json
```

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| UserId | Int | 是 | 用户ID |
| RoomId | Int | 是 | 自习室ID |
| EnableFocusMonitor | Bool | 否 | 是否开启专注度监测 |
| EnableContentRecognition | Bool | 否 | 是否开启内容识别 |
| EnableFaceSign | Bool | 否 | 是否开启人脸签到 |

**响应**：

```json
{
    "Success": true,
    "Data": {
        "MonitorId": "MONITOR_001",
        "Message": "监测已开始",
        "PrivacyNotice": "监测数据仅用于学习分析，不会上传至服务器"
    }
}
```

### 2.1 WebSocket实时推送

```json
{
    "Type": "focus_update",
    "Data": {
        "UserId": 1,
        "FocusLevel": 92,
        "Emotion": "concentrated",
        "Duration": 1800,
        "Suggestion": "继续保持，当前状态很好",
        "Alert": null
    }
}
```

---

## 三、数据库设计

```sql
CREATE TABLE focus_record (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    RoomId INT NOT NULL,
    MonitorId VARCHAR(50),
    StartTime DATETIME,
    EndTime DATETIME,
    TotalDuration INT,
    FocusDuration INT,
    AverageFocusLevel DECIMAL(5,2),
    DistractionCount INT DEFAULT 0,
    AbsenceCount INT DEFAULT 0,
    SleepCount INT DEFAULT 0,
    RecognizedContent TEXT,
    AnalysisResult TEXT,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE face_sign_record (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    RoomId INT NOT NULL,
    SignType VARCHAR(20),
    FaceImageUrl VARCHAR(500),
    Confidence DECIMAL(5,2),
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

---

**文档版本**：v2.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月