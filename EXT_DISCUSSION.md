# RoomAppoint v2.0 进阶扩展方案 - 研讨区

---

## 一、功能概述

研讨区提供学习交流平台，支持小组讨论、问答分享、学习资源共享，打造活跃的学习社区。

### 1.1 核心功能列表

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| **讨论话题** | 创建/参与讨论话题，支持Markdown编辑 | P0 |
| **问答社区** | 提问/回答，支持采纳最佳答案 | P0 |
| **资源共享** | 上传/下载学习资料，支持分类筛选 | P1 |
| **小组讨论** | 创建学习小组，私密讨论空间 | P1 |
| **评论互动** | 点赞、评论、@提及、收藏 | P0 |
| **热门排行** | 热门话题、热门回答、活跃用户排行 | P1 |

---

## 二、数据库设计

```sql
CREATE TABLE discussion_topic (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(200) NOT NULL,
    Content TEXT NOT NULL,
    UserId INT NOT NULL,
    Category VARCHAR(50),
    Tags VARCHAR(200),
    ViewCount INT DEFAULT 0,
    ReplyCount INT DEFAULT 0,
    LikeCount INT DEFAULT 0,
    IsTop TINYINT(1) DEFAULT 0,
    IsEssence TINYINT(1) DEFAULT 0,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE discussion_reply (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    TopicId INT NOT NULL,
    UserId INT NOT NULL,
    Content TEXT NOT NULL,
    ReplyToUserId INT,
    ReplyToReplyId INT,
    IsAccepted TINYINT(1) DEFAULT 0,
    LikeCount INT DEFAULT 0,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TopicId) REFERENCES discussion_topic(Id),
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE study_group (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    OwnerId INT NOT NULL,
    MemberCount INT DEFAULT 1,
    MaxMembers INT DEFAULT 20,
    IsPublic TINYINT(1) DEFAULT 1,
    Password VARCHAR(50),
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OwnerId) REFERENCES app_user(Id)
);

CREATE TABLE shared_resource (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(200) NOT NULL,
    Description TEXT,
    FileUrl VARCHAR(500),
    FileType VARCHAR(50),
    Category VARCHAR(50),
    UserId INT NOT NULL,
    DownloadCount INT DEFAULT 0,
    LikeCount INT DEFAULT 0,
    IsFree TINYINT(1) DEFAULT 1,
    IntegralCost INT DEFAULT 0,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

---

## 三、API接口

| API路径 | 方法 | 说明 |
| :--- | :--- | :--- |
| `/Discussion/Topic/List` | POST | 获取话题列表 |
| `/Discussion/Topic/Detail` | POST | 获取话题详情 |
| `/Discussion/Topic/Create` | POST | 创建话题 |
| `/Discussion/Topic/Update` | POST | 更新话题 |
| `/Discussion/Topic/Delete` | POST | 删除话题 |
| `/Discussion/Reply/Add` | POST | 添加评论 |
| `/Discussion/Reply/List` | POST | 获取评论列表 |
| `/Discussion/Reply/Accept` | POST | 采纳最佳答案 |
| `/Discussion/Group/List` | POST | 获取小组列表 |
| `/Discussion/Group/Create` | POST | 创建小组 |
| `/Discussion/Group/Join` | POST | 加入小组 |
| `/Discussion/Resource/List` | POST | 获取资源列表 |
| `/Discussion/Resource/Upload` | POST | 上传资源 |
| `/Discussion/Resource/Download` | POST | 下载资源 |

---

**文档版本**：v2.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月