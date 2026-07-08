# RoomMind v2.0 进阶扩展方案 - 成就系统+资源商城+智能提醒+学习组队

---

## 一、学习成就系�?
### 1.1 功能概述

完成特定学习目标获得成就徽章，激励用户保持学习动力�?
### 1.2 成就类型

| 类型 | 成就示例 | 条件 |
| :--- | :--- | :--- |
| **时长成就** | 学习达人、学霸养�?| 累计学习时长达成目标 |
| **连续成就** | 坚持不懈、持之以�?| 连续打卡天数达成目标 |
| **专注成就** | 心无旁骛、极致专�?| 专注度达到指定水�?|
| **学科成就** | 数学小王子、英语达�?| 特定学科学习时长达标 |
| **互动成就** | 乐于助人、知识分享�?| 在研讨区积极互动 |

### 1.3 数据库设�?
```sql
CREATE TABLE achievement (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description VARCHAR(200),
    ConditionType VARCHAR(50),
    ConditionValue INT,
    Points INT DEFAULT 0,
    Level VARCHAR(20),
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_achievement (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    AchievementId INT NOT NULL,
    UnlockTime DATETIME,
    CurrentProgress INT DEFAULT 0,
    Status TINYINT(1) DEFAULT 0,
    FOREIGN KEY (UserId) REFERENCES app_user(Id),
    FOREIGN KEY (AchievementId) REFERENCES achievement(Id)
);
```

---

## 二、学习资源商�?
### 2.1 功能概述

使用积分兑换学习资源，打造学习生态闭环�?
### 2.2 资源类型

| 类型 | 说明 | 示例 |
| :--- | :--- | :--- |
| **视频课程** | 名师视频讲解 | 考研数学视频 |
| **电子书籍** | PDF格式电子�?| 教材、辅导书 |
| **题库资源** | 练习题和答案 | 历年真题 |
| **学习工具** | 实用学习辅助 | 思维导图模板 |
| **会员服务** | 增值服�?| VIP自习�?|

### 2.3 数据库设�?
```sql
CREATE TABLE resource_store (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(200) NOT NULL,
    Description TEXT,
    Type VARCHAR(50),
    Category VARCHAR(50),
    IntegralCost INT NOT NULL,
    Url VARCHAR(500),
    Downloads INT DEFAULT 0,
    IsFree TINYINT(1) DEFAULT 0,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_resource (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    ResourceId INT NOT NULL,
    ObtainTime DATETIME,
    ObtainType VARCHAR(20),
    ExpireTime DATETIME,
    FOREIGN KEY (UserId) REFERENCES app_user(Id),
    FOREIGN KEY (ResourceId) REFERENCES resource_store(Id)
);
```

---

## 三、智能提醒系�?
### 3.1 功能概述

基于用户行为和AI分析，推送个性化提醒�?
### 3.2 提醒类型

| 提醒类型 | 触发条件 |
| :--- | :--- |
| **复习提醒** | 到达预设复习时间�?|
| **休息提醒** | 连续学习超过60分钟 |
| **目标提醒** | 目标完成度达到特定比�?|
| **预约提醒** | 预约时间�?0分钟 |
| **成就提醒** | 达成成就条件 |

### 3.3 数据库设�?
```sql
CREATE TABLE notification (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    Type VARCHAR(50),
    Title VARCHAR(200),
    Content TEXT,
    IsRead TINYINT(1) DEFAULT 0,
    PushTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE reminder_setting (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    ReviewReminder TINYINT(1) DEFAULT 1,
    RestReminder TINYINT(1) DEFAULT 1,
    RestInterval INT DEFAULT 60,
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

---

## 四、学习组�?
### 4.1 功能概述

用户可以创建或加入学习小组，共同学习、互相监督�?
### 4.2 核心功能

| 功能 | 说明 |
| :--- | :--- |
| **创建小组** | 设置小组名称、目标、人数限�?|
| **加入小组** | 通过搜索或邀请码加入小组 |
| **小组目标** | 设置共同学习目标，追踪进�?|
| **组队学习** | 同时进入虚拟自习室学�?|

### 4.3 数据库设�?
```sql
CREATE TABLE study_team (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Goal VARCHAR(200),
    OwnerId INT NOT NULL,
    MemberCount INT DEFAULT 1,
    MaxMembers INT DEFAULT 20,
    InviteCode VARCHAR(20),
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OwnerId) REFERENCES app_user(Id)
);

CREATE TABLE study_team_member (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    TeamId INT NOT NULL,
    UserId INT NOT NULL,
    Role VARCHAR(20) DEFAULT 'member',
    JoinTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TeamId) REFERENCES study_team(Id),
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);
```

---

**文档版本**：v2.0  
**适用项目**：RoomMind 自习室预约系�? 
**最后更�?*�?026�?�


