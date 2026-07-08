# RoomMind v2.0 进阶扩展方案 - 虚拟自习�?
---

## 一、功能概�?
虚拟自习室是RoomMind v2.0的核心扩展模块，用户无需物理空间，在线即可进入虚拟自习室学习，模拟真实自习环境，支持视频连线、专注监测、学习统计等功能�?
### 1.1 核心功能列表

| 功能 | 说明 | 优先�?|
| :--- | :--- | :--- |
| **虚拟座位** | 在线选择座位，可视化展示自习室布局 | P0 |
| **视频连线** | 可选开启摄像头，与同桌视频学习 | P1 |
| **专注模式** | 屏蔽通知，进入沉浸式学习状�?| P0 |
| **番茄�?* | 内置番茄工作法，25分钟专注+5分钟休息 | P0 |
| **学习统计** | 实时显示学习时长、专注度、学习内�?| P0 |
| **虚拟签到** | 线上签到签退，记录学习时�?| P0 |
| **同桌匹配** | 智能匹配学习伙伴 | P2 |
| **环境音效** | 模拟图书馆白噪音 | P2 |

---

## 二、API接口

| API路径 | 方法 | 说明 |
| :--- | :--- | :--- |
| `/VirtualRoom/Enter` | POST | 进入虚拟自习�?|
| `/VirtualRoom/Leave` | POST | 离开虚拟自习�?|
| `/VirtualRoom/GetSeats` | POST | 获取座位列表 |
| `/VirtualRoom/SelectSeat` | POST | 选择座位 |
| `/VirtualRoom/StartVideo` | POST | 开启视�?|
| `/VirtualRoom/StopVideo` | POST | 关闭视频 |
| `/VirtualRoom/GetStudyStats` | POST | 获取学习统计 |
| `/VirtualRoom/StartPomodoro` | POST | 开始番茄钟 |
| `/VirtualRoom/StopPomodoro` | POST | 停止番茄�?|

---

## 三、数据库设计

```sql
CREATE TABLE virtual_room (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    TotalSeats INT DEFAULT 50,
    CurrentUsers INT DEFAULT 0,
    Status TINYINT(1) DEFAULT 1,
    CreationTime DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE virtual_seat (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    RoomId INT NOT NULL,
    SeatNo INT NOT NULL,
    Row INT,
    Col INT,
    IsOccupied TINYINT(1) DEFAULT 0,
    UserId INT,
    StartTime DATETIME,
    FOREIGN KEY (RoomId) REFERENCES virtual_room(Id),
    FOREIGN KEY (UserId) REFERENCES app_user(Id)
);

CREATE TABLE room_session (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT NOT NULL,
    RoomId INT NOT NULL,
    SeatId INT,
    EnterTime DATETIME,
    LeaveTime DATETIME,
    Duration INT COMMENT '学习时长(分钟)',
    FocusRate DECIMAL(5,2),
    PomodoroCount INT DEFAULT 0,
    FOREIGN KEY (UserId) REFERENCES app_user(Id),
    FOREIGN KEY (RoomId) REFERENCES virtual_room(Id)
);
```

---

## 四、前端页面设�?
```vue
<template>
  <div class="virtual-room-container">
    <div class="room-header">
      <h2>{{ roomName }}</h2>
      <div class="header-stats">
        <span><i class="el-icon-user"></i> 在线 {{ onlineCount }}</span>
        <span><i class="el-icon-clock"></i> 今日学习 {{ todayStudyHours }}h</span>
      </div>
    </div>
    
    <div class="room-content">
      <div class="seat-layout">
        <div 
          v-for="seat in seats" 
          :key="seat.Id"
          :class="['seat-item', { occupied: seat.IsOccupied, selected: seat.IsSelected, me: seat.IsMe }]"
          @click="selectSeat(seat)"
        >
          <div class="seat-number">{{ seat.No }}</div>
          <div v-if="seat.IsOccupied" class="seat-user">
            <img :src="seat.UserAvatar" class="avatar" />
            <span class="username">{{ seat.UserName }}</span>
            <span class="study-status" :class="seat.FocusStatus">
              {{ seat.FocusStatus === 'focused' ? '专注�? : seat.FocusStatus === 'resting' ? '休息�? : '离开' }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="study-panel">
        <div class="pomodoro-section">
          <div class="timer-display" :class="{ active: isPomodoroRunning }">
            {{ formatTime(pomodoroTime) }}
          </div>
          <div class="timer-controls">
            <el-button @click="togglePomodoro" :type="isPomodoroRunning ? 'warning' : 'primary'">
              {{ isPomodoroRunning ? '暂停' : '开始专�? }}
            </el-button>
            <el-button @click="resetPomodoro">重置</el-button>
          </div>
        </div>
        
        <div class="stats-section">
          <div class="stat-card">
            <span class="stat-value">{{ focusRate }}%</span>
            <span class="stat-label">专注�?/span>
          </div>
          <div class="stat-card">
            <span class="stat-value">{{ sessionTime }}</span>
            <span class="stat-label">本次学习</span>
          </div>
          <div class="stat-card">
            <span class="stat-value">{{ streakDays }}</span>
            <span class="stat-label">连续打卡</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VirtualRoom',
  data() {
    return {
      roomName: '考研冲刺自习�?,
      onlineCount: 24,
      todayStudyHours: 3.5,
      seats: [],
      pomodoroTime: 25 * 60,
      isPomodoroRunning: false,
      focusRate: 87,
      sessionTime: '02:35',
      streakDays: 12
    }
  },
  methods: {
    formatTime(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    togglePomodoro() {
      this.isPomodoroRunning = !this.isPomodoroRunning
    },
    resetPomodoro() {
      this.isPomodoroRunning = false
      this.pomodoroTime = 25 * 60
    },
    selectSeat(seat) {
      if (!seat.IsOccupied) {
        // 选择座位逻辑
      }
    }
  }
}
</script>
```

---

**文档版本**：v2.0  
**适用项目**：RoomMind 自习室预约系�? 
**最后更�?*�?026�?�


