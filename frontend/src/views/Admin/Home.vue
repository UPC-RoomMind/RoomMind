<template>
    <div class="admin-dashboard">
        <!-- Welcome Banner -->
        <div class="welcome-banner card margin-top-lg">
            <div class="welcome-content">
                <h2>欢迎回来，{{ UserInfo.Name }}</h2>
                <p>以下是系统的实时数据概览</p>
            </div>
            <div class="welcome-date">
                <span class="date-badge">{{ currentDate }}</span>
            </div>
        </div>

        <!-- Stat Cards -->
        <div class="stat-grid" v-if="DataCollect != null">
            <div class="stat-card stat-teal">
                <div class="stat-icon">
                    <i class="el-icon-office-building"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.RoomCount }}</div>
                    <div class="stat-label">自习室个数</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-office-building"></i>
                </div>
            </div>

            <div class="stat-card stat-purple">
                <div class="stat-icon">
                    <i class="el-icon-s-grid"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.SeatCount }}</div>
                    <div class="stat-label">总座位数</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-s-grid"></i>
                </div>
            </div>

            <div class="stat-card stat-warm">
                <div class="stat-icon">
                    <i class="el-icon-s-order"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.AppointCount }}</div>
                    <div class="stat-label">总预约人次</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-s-order"></i>
                </div>
            </div>

            <div class="stat-card stat-green">
                <div class="stat-icon">
                    <i class="el-icon-time"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.WaitAppointCount }}</div>
                    <div class="stat-label">待使用次数</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-time"></i>
                </div>
            </div>

            <div class="stat-card stat-pink">
                <div class="stat-icon">
                    <i class="el-icon-warning-outline"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.OverdueAppointCount }}</div>
                    <div class="stat-label">逾期人次</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-warning-outline"></i>
                </div>
            </div>

            <div class="stat-card stat-red">
                <div class="stat-icon">
                    <i class="el-icon-medal"></i>
                </div>
                <div class="stat-info">
                    <div class="stat-value">{{ DataCollect.TotalIntegral }}</div>
                    <div class="stat-label">总剩余积分</div>
                </div>
                <div class="stat-bg-icon">
                    <i class="el-icon-medal"></i>
                </div>
            </div>
        </div>

        <!-- Chart Card -->
        <el-card class="chart-card margin-top-lg">
            <div slot="header" class="chart-header">
                <span class="chart-title">
                    <i class="el-icon-data-analysis"></i>
                    今日各时段使用率统计 (%)
                </span>
            </div>
            <div class="echart" id="echartDiv" style="width: 100%; height: 420px;"></div>
        </el-card>

        <!-- Calendar Card -->
        <el-card class="chart-card margin-top-lg margin-bottom-lg">
            <div slot="header" class="chart-header">
                <span class="chart-title">
                    <i class="el-icon-date"></i>
                    日历
                </span>
            </div>
            <el-calendar></el-calendar>
        </el-card>
    </div>
</template>

<script>
import * as echarts from "echarts";
import { mapGetters } from "vuex";

export default {
    name: 'Home',
    computed: {
        ...mapGetters(["UserInfo"]),
        currentDate() {
            const d = new Date();
            const weekDays = ['日','一','二','三','四','五','六'];
            const w = weekDays[d.getDay()];
            return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 星期${w}`;
        }
    },
    data() {
        return {
            DataCollect: null
        };
    },
    mounted() {
        this.GetAppointRoomUseRate();
        this.GetDataCollect();
    },
    methods: {
        async GetAppointRoomUseRate() {
            let { Data } = await this.$Post('/AppointRecord/GetAppointRoomUseRate', {});
            this.$nextTick(() => {
                let myChart = echarts.init(document.getElementById("echartDiv"));
                const labelOption = {
                    show: true,
                    position: 'insideTop',
                    distance: 12,
                    fontSize: 12,
                    fontWeight: 'bold',
                    color: '#333',
                    formatter: '{c}%'
                };
                let option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: { type: 'shadow' },
                        valueFormatter: (value) => value + '%'
                    },
                    legend: {
                        data: ['上午', '下午', '夜晚'],
                        top: 10,
                        textStyle: { fontSize: 13 }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            magicType: { show: true, type: ['line', 'bar', 'stack'] },
                            restore: { show: true },
                            saveAsImage: { show: true }
                        }
                    },
                    xAxis: {
                        type: 'category',
                        axisTick: { show: false },
                        data: Data.map(x => x.Room.Name),
                        axisLabel: { rotate: 15, fontSize: 12 }
                    },
                    yAxis: {
                        type: 'value',
                        name: '使用率 (%)',
                        axisLabel: { formatter: '{value}%' }
                    },
                    series: [
                        {
                            name: '上午', type: 'bar', barGap: '5%',
                            label: labelOption,
                            emphasis: { focus: 'series' },
                            data: Data.map(x => x.AmUseRate),
                            itemStyle: {
                                borderRadius: [6, 6, 0, 0],
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#5ccce6' },
                                    { offset: 1, color: '#28b2d4' }
                                ])
                            }
                        },
                        {
                            name: '下午', type: 'bar',
                            label: labelOption,
                            emphasis: { focus: 'series' },
                            data: Data.map(x => x.PmUseRate),
                            itemStyle: {
                                borderRadius: [6, 6, 0, 0],
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#a78bfa' },
                                    { offset: 1, color: '#7330a0' }
                                ])
                            }
                        },
                        {
                            name: '夜晚', type: 'bar',
                            label: labelOption,
                            emphasis: { focus: 'series' },
                            data: Data.map(x => x.NmUseRate),
                            itemStyle: {
                                borderRadius: [6, 6, 0, 0],
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#fbbf24' },
                                    { offset: 1, color: '#f7ad25' }
                                ])
                            }
                        },
                    ]
                };
                myChart.setOption(option);
                window.addEventListener("resize", () => myChart.resize());
            });
        },
        async GetDataCollect() {
            let { Data } = await this.$Post('/AppointRecord/GetDataCollect', {});
            this.DataCollect = Data;
        }
    }
}
</script>

<style scoped>
/* ==================== Welcome Banner ==================== */
.welcome-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, #1a2530, #2c5364);
    color: white;
    border-radius: 14px;
    padding: 28px 32px;
    border: none;
}

.welcome-content h2 {
    font-size: 22px;
    font-weight: 700;
    margin-bottom: 4px;
}

.welcome-content p {
    font-size: 14px;
    opacity: 0.75;
}

.date-badge {
    background: rgba(255,255,255,0.15);
    padding: 8px 18px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid rgba(255,255,255,0.2);
}

/* ==================== Stat Grid ==================== */
.stat-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 18px;
    margin-top: 20px;
}

/* ==================== Stat Cards ==================== */
.stat-card {
    background: white;
    border-radius: 14px;
    padding: 22px 20px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    border: 1px solid #f0f2f5;
    display: flex;
    align-items: center;
    gap: 14px;
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: default;
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 28px rgba(0,0,0,0.1);
}

.stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
}

/* Teal */
.stat-teal .stat-icon {
    background: linear-gradient(135deg, #e0f7fa, #b2ebf2);
    color: #00838f;
}

.stat-teal { border-left: 3px solid #00838f; }

/* Purple */
.stat-purple .stat-icon {
    background: linear-gradient(135deg, #ede7f6, #d1c4e9);
    color: #5e35b1;
}

.stat-purple { border-left: 3px solid #5e35b1; }

/* Warm */
.stat-warm .stat-icon {
    background: linear-gradient(135deg, #fff8e1, #ffecb3);
    color: #f57f17;
}

.stat-warm { border-left: 3px solid #f57f17; }

/* Green */
.stat-green .stat-icon {
    background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
    color: #2e7d32;
}

.stat-green { border-left: 3px solid #2e7d32; }

/* Pink */
.stat-pink .stat-icon {
    background: linear-gradient(135deg, #fce4ec, #f8bbd0);
    color: #ad1457;
}

.stat-pink { border-left: 3px solid #ad1457; }

/* Red */
.stat-red .stat-icon {
    background: linear-gradient(135deg, #ffebee, #ffcdd2);
    color: #c62828;
}

.stat-red { border-left: 3px solid #c62828; }

.stat-info {
    flex: 1;
    min-width: 0;
}

.stat-value {
    font-size: 26px;
    font-weight: 800;
    color: #1a1a2e;
    line-height: 1.2;
    letter-spacing: -0.5px;
}

.stat-label {
    font-size: 12px;
    color: #999;
    margin-top: 2px;
    font-weight: 500;
    letter-spacing: 0.3px;
}

.stat-bg-icon {
    position: absolute;
    right: -10px;
    bottom: -15px;
    font-size: 70px;
    opacity: 0.04;
    pointer-events: none;
}

/* ==================== Chart Card ==================== */
.chart-card {
    border-radius: 14px !important;
    overflow: hidden;
}

.chart-header {
    display: flex;
    align-items: center;
}

.chart-title {
    font-weight: 700;
    font-size: 15px;
    color: #1a1a2e;
    display: flex;
    align-items: center;
    gap: 8px;
}

.chart-title i {
    color: #28b2d4;
    font-size: 18px;
}

/* ==================== Responsive ==================== */
@media (max-width: 1600px) {
    .stat-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}

@media (max-width: 900px) {
    .stat-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .welcome-banner {
        flex-direction: column;
        gap: 14px;
        text-align: center;
    }
}
</style>
