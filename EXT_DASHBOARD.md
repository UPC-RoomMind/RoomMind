# RoomAppoint v2.0 进阶扩展方案 - 可视化大屏

---

## 一、功能概述

提供数据可视化大屏，展示自习室运营数据和用户学习数据，支持实时监控和趋势分析。

### 1.1 大屏模块

| 模块 | 说明 | 优先级 |
| :--- | :--- | :--- |
| **实时数据** | 当前在线人数、使用座位数、实时趋势 | P0 |
| **学习统计** | 今日学习时长、平均专注度、学习排行榜 | P0 |
| **热门科目** | 各学科学习热度排行 | P1 |
| **用户画像** | 用户分布、活跃度分析 | P1 |
| **设备状态** | 硬件设备在线状态、门禁统计 | P2 |
| **AI洞察** | AI生成的学习分析和建议 | P1 |

---

## 二、前端页面设计

```vue
<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>RoomAppoint 智能学习数据中心</h1>
      <span class="update-time">数据更新: {{ updateTime }}</span>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card primary">
        <span class="card-value">{{ onlineCount }}</span>
        <span class="card-title">当前在线</span>
      </div>
      <div class="stat-card success">
        <span class="card-value">{{ todayStudyHours }}h</span>
        <span class="card-title">今日学习</span>
      </div>
      <div class="stat-card warning">
        <span class="card-value">{{ avgFocusRate }}%</span>
        <span class="card-title">平均专注度</span>
      </div>
      <div class="stat-card danger">
        <span class="card-value">{{ seatUsageRate }}%</span>
        <span class="card-title">座位使用率</span>
      </div>
    </div>
    
    <div class="charts-section">
      <div class="chart-panel large">
        <h3>学习时长趋势</h3>
        <div ref="studyTrendChart" class="chart"></div>
      </div>
      <div class="chart-panel">
        <h3>热门学科分布</h3>
        <div ref="subjectChart" class="chart"></div>
      </div>
      <div class="chart-panel">
        <h3>学习排行榜</h3>
        <div class="ranking-list">
          <div v-for="(user, index) in ranking" :key="user.Id" class="ranking-item">
            <span class="rank">{{ index + 1 }}</span>
            <span class="rank-name">{{ user.Name }}</span>
            <span class="rank-hours">{{ user.Hours }}h</span>
          </div>
        </div>
      </div>
      <div class="chart-panel large">
        <h3>AI学习洞察</h3>
        <div class="ai-insights">
          <div v-for="(insight, index) in aiInsights" :key="index" class="insight-item">
            <span class="insight-icon">{{ insight.Icon }}</span>
            <span class="insight-desc">{{ insight.Description }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      updateTime: new Date().toLocaleString('zh-CN'),
      onlineCount: 156,
      todayStudyHours: 328,
      avgFocusRate: 85,
      seatUsageRate: 78,
      ranking: [],
      aiInsights: []
    }
  },
  mounted() {
    this.initCharts()
    this.startAutoUpdate()
  },
  methods: {
    initCharts() {},
    startAutoUpdate() {
      setInterval(() => {
        this.updateTime = new Date().toLocaleString('zh-CN')
      }, 30000)
    }
  }
}
</script>
```

---

## 三、API接口

| API路径 | 方法 | 说明 |
| :--- | :--- | :--- |
| `/Dashboard/GetRealTimeData` | POST | 获取实时数据 |
| `/Dashboard/GetStudyStats` | POST | 获取学习统计 |
| `/Dashboard/GetRanking` | POST | 获取排行榜 |
| `/Dashboard/GetAIInsights` | POST | 获取AI洞察 |

---

**文档版本**：v2.0  
**适用项目**：RoomAppoint 自习室预约系统  
**最后更新**：2026年7月