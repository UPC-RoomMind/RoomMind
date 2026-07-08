<template>
    <div v-if="RoomDetail" class="room-detail">
        <!-- Page Header -->
        <el-page-header class="card card-accent margin-top-lg" @back="goBack" :content="RoomDetail.Name">
            <template slot="content">
                <span class="room-header-title">{{ RoomDetail.Name }}</span>
            </template>
        </el-page-header>

        <!-- Detail Card -->
        <div class="detail-card card margin-top-lg">
            <!-- Cover Image -->
            <div class="cover-area">
                <img :src="RoomDetail.Cover" :alt="RoomDetail.Name" />
                <div class="cover-info">
                    <span class="addr-tag" v-if="RoomDetail.Address">
                        <i class="el-icon-location-information"></i> {{ RoomDetail.Address }}
                    </span>
                </div>
            </div>

            <!-- Tabs -->
            <div class="tabs-area">
                <el-tabs v-model="activeName">
                    <el-tab-pane label="详情介绍" name="详情介绍">
                        <div class="content" v-html="RoomDetail.Content"></div>
                    </el-tab-pane>

                    <!-- Seat Selection Tab -->
                    <el-tab-pane label="选座列表" name="选座列表">
                        <div class="seat-section">
                            <div class="date-selector">
                                <h4><i class="el-icon-date"></i> 选择日期</h4>
                                <el-tabs v-model="SelectDate" @tab-click="DateHandleClick" type="card">
                                    <el-tab-pane :label="item" :name="item" v-for="(item, index) in DateList"></el-tab-pane>
                                </el-tabs>
                            </div>

                            <template v-if="SeatArrange">
                                <!-- 上午 -->
                                <div class="time-slot">
                                    <div class="slot-header">
                                        <span class="slot-title">
                                            <i class="el-icon-sunny"></i> 上午时段
                                        </span>
                                        <em class="slot-tip">如有提前离开，座位将被释放</em>
                                    </div>
                                    <el-empty v-if="SeatArrange.AmSeatDtoList.length == 0" description="暂无可用数据"></el-empty>
                                    <div class="seat-grid" v-else>
                                        <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.AmSeatDtoList" :key="'am-r'+rowIndex">
                                            <div class="seat-cell" v-for="(col, colIndex) in row" :key="'am-c'+colIndex">
                                                <div v-if="col.Id" class="seat-item" :class="{
                                                    'seat-available': !col.IsOccupy && !col.IsMaintain,
                                                    'seat-occupied': col.IsOccupy || col.IsMaintain
                                                }" @click="ToAppoint(col, 1)">
                                                    <div class="seat-icon-area">
                                                        <i v-if="!col.IsOccupy && !col.IsMaintain" class="el-icon-s-chair seat-free-icon"></i>
                                                        <i v-else class="el-icon-s-chair seat-used-icon"></i>
                                                    </div>
                                                    <div class="seat-no">{{ col.No }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- 下午 -->
                                <div class="time-slot">
                                    <div class="slot-header">
                                        <span class="slot-title">
                                            <i class="el-icon-cloudy"></i> 下午时段
                                        </span>
                                        <em class="slot-tip">如有提前离开，座位将被释放</em>
                                    </div>
                                    <el-empty v-if="SeatArrange.PmSeatDtoList.length == 0" description="暂无可用数据"></el-empty>
                                    <div class="seat-grid" v-else>
                                        <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.PmSeatDtoList" :key="'pm-r'+rowIndex">
                                            <div class="seat-cell" v-for="(col, colIndex) in row" :key="'pm-c'+colIndex">
                                                <div v-if="col.Id" class="seat-item" :class="{
                                                    'seat-available': !col.IsOccupy && !col.IsMaintain,
                                                    'seat-occupied': col.IsOccupy || col.IsMaintain
                                                }" @click="ToAppoint(col, 2)">
                                                    <div class="seat-icon-area">
                                                        <i v-if="!col.IsOccupy && !col.IsMaintain" class="el-icon-s-chair seat-free-icon"></i>
                                                        <i v-else class="el-icon-s-chair seat-used-icon"></i>
                                                    </div>
                                                    <div class="seat-no">{{ col.No }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- 夜晚 -->
                                <div class="time-slot">
                                    <div class="slot-header">
                                        <span class="slot-title">
                                            <i class="el-icon-moon"></i> 夜晚时段
                                        </span>
                                        <em class="slot-tip">如有提前离开，座位将被释放</em>
                                    </div>
                                    <el-empty v-if="SeatArrange.NmSeatDtoList.length == 0" description="暂无可用数据"></el-empty>
                                    <div class="seat-grid" v-else>
                                        <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.NmSeatDtoList" :key="'nm-r'+rowIndex">
                                            <div class="seat-cell" v-for="(col, colIndex) in row" :key="'nm-c'+colIndex">
                                                <div v-if="col.Id" class="seat-item" :class="{
                                                    'seat-available': !col.IsOccupy && !col.IsMaintain,
                                                    'seat-occupied': col.IsOccupy || col.IsMaintain
                                                }" @click="ToAppoint(col, 3)">
                                                    <div class="seat-icon-area">
                                                        <i v-if="!col.IsOccupy && !col.IsMaintain" class="el-icon-s-chair seat-free-icon"></i>
                                                        <i v-else class="el-icon-s-chair seat-used-icon"></i>
                                                    </div>
                                                    <div class="seat-no">{{ col.No }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </div>
                    </el-tab-pane>

                    <!-- Comments Tab -->
                    <el-tab-pane label="使用评价" name="使用评价">
                        <Pagination url="/AppointRecord/List" :where="{ RoomId: RoomId, AppointStatus: 3 }">
                            <template v-slot:content="{ data }">
                                <div class="comment-list">
                                    <div class="comment-card" v-for="(item, index) in data" :key="index">
                                        <div class="comment-avatar">
                                            <img :src="item.UserDto.ImageUrls ? item.UserDto.ImageUrls : require('@/assets/emptyheadimg.png')" />
                                        </div>
                                        <div class="comment-body">
                                            <div class="comment-header">
                                                <span class="comment-name">{{ item.UserDto.Name }}</span>
                                                <span class="comment-seat">
                                                    <i class="el-icon-s-mark"></i> {{ item.SeatDto.No }}号座
                                                </span>
                                                <span class="comment-time">{{ item.EndTime | YMDHMSFormat }}</span>
                                            </div>
                                            <el-rate disabled v-model="item.CommentScore" class="comment-rate"></el-rate>
                                            <div class="comment-text" v-if="item.Comment">
                                                {{ item.Comment }}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </Pagination>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </div>
    </div>
</template>

<script>
import store from '@/store';
import { mapGetters } from 'vuex';
import Pagination from "@/components/Pagination/PaginationBox.vue"
export default {
    components: {
        Pagination
    },
    name: "Room",
    computed: {
        ...mapGetters(["UserInfo", 'Token', 'UserId'])
    },
    data() {
        return {
            activeName: "选座列表",
            RoomId: undefined,
            RoomDetail: null,
            DateList: [],
            SeatArrange: null,
            SelectDate: null,
        }
    },
    created() {
        this.RoomId = this.$route.query.RoomId
        this.GetRoomApi()
        this.GetSevenDaysApi()
    },
    methods: {
        async GetRoomApi() {
            let { Data } = await this.$Post("/Room/Get", { Id: this.RoomId })
            this.RoomDetail = Data;
        },
        async GetSevenDaysApi() {
            let { Data } = await this.$Post("/Seat/GetSevenDays", {})
            this.DateList = Data;
            this.SelectDate = this.DateList[0]
            this.GetArrange();
        },
        async GetArrange() {
            let { Data } = await this.$Post("/Seat/GetArrange", { RoomId: this.RoomId, SelectDate: this.SelectDate + " 00:00:00" })
            this.SeatArrange = Data;
        },
        async DateHandleClick(e) {
            this.GetArrange();
        },
        goBack() {
            this.$router.go(-1)
        },
        async ToAppoint(col, type) {
            if (col.IsOccupy && col.IsMaintain) {
                return;
            }
            if (!this.Token) {
                this.$message.warning("请先登录后,在操作");
                return;
            }
            let body = {
                RoomId: this.RoomId,
                UserId: this.UserId,
                SeatId: col.Id,
                AppointDateType: type,
                AppointDate: this.SelectDate + " 00:00:00",
                isMaintain: col.IsMaintain
            };
            let { Success } = await this.$Post("/AppointRecord/CheckIsAbleAppoint", body)
            let tick = new Date().getTime();
            localStorage.setItem(tick, JSON.stringify(body));
            if (Success) {
                this.$router.push({
                    path: "/Front/ToOrder",
                    query: { tick: tick }
                })
            }
        },
    }
}
</script>

<style scoped>
/* ==================== Room Header ==================== */
.room-header-title {
    font-size: 20px;
    font-weight: 700;
    color: #1a1a2e;
}

/* ==================== Cover Area ==================== */
.cover-area {
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 8px;
}

.cover-area img {
    width: 100%;
    max-height: 380px;
    object-fit: cover;
    border-radius: 12px;
}

.cover-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 40px 20px 16px;
    background: linear-gradient(transparent, rgba(0,0,0,0.5));
}

.addr-tag {
    background: rgba(255,255,255,0.2);
    backdrop-filter: blur(8px);
    color: white;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

/* ==================== Content ==================== */
.content {
    font-family: 'Microsoft YaHei', Arial, sans-serif;
    font-size: 15px;
    line-height: 2.2;
    color: #444;
    padding: 10px 0;
}

.content >>> img {
    max-width: 100%;
    border-radius: 8px;
}

/* ==================== Seat Section ==================== */
.seat-section {
    padding: 10px 0;
}

.date-selector h4 {
    font-size: 15px;
    font-weight: 700;
    color: #1a1a2e;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 6px;
}

.date-selector h4 i {
    color: #28b2d4;
}

.date-selector >>> .el-tabs--card .el-tabs__item {
    border-radius: 6px 6px 0 0;
    font-weight: 500;
}

.date-selector >>> .el-tabs--card .el-tabs__item.is-active {
    background: #28b2d4;
    color: white;
    font-weight: 700;
}

/* Time slot */
.time-slot {
    margin-bottom: 32px;
}

.slot-header {
    display: flex;
    align-items: baseline;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;
}

.slot-title {
    font-size: 16px;
    font-weight: 700;
    color: #1a1a2e;
    display: flex;
    align-items: center;
    gap: 6px;
}

.slot-title i {
    color: #28b2d4;
}

.slot-tip {
    font-size: 12px;
    color: #e74c3c;
    font-style: normal;
}

/* ==================== Seat Grid ==================== */
.seat-grid {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
}

.seat-row {
    display: flex;
    gap: 10px;
}

.seat-cell {
    width: 62px;
    height: 74px;
}

.seat-item {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-size: 12px;
    font-weight: 600;
    user-select: none;
    gap: 2px;
}

/* Available seat */
.seat-available {
    background: linear-gradient(180deg, #e8f8fc, #d4f0f8);
    border: 2px solid #b8e4f0;
    color: #1a8da8;
}

.seat-available:hover {
    background: linear-gradient(180deg, #d0f2fa, #b8e8f5);
    border-color: #28b2d4;
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(40,178,212,0.25);
}

.seat-free-icon {
    font-size: 22px;
    color: #28b2d4;
}

/* Occupied / Maintenance seat */
.seat-occupied {
    background: linear-gradient(180deg, #fef0ef, #fde8e8);
    border: 2px solid #f5c6cb;
    color: #c0392b;
    cursor: not-allowed;
}

.seat-used-icon {
    font-size: 22px;
    color: #c0392b;
    opacity: 0.6;
}

.seat-no {
    font-size: 11px;
    letter-spacing: 0.3px;
}

/* ==================== Comments ==================== */
.comment-list {
    padding: 10px 0;
}

.comment-card {
    display: flex;
    gap: 16px;
    padding: 18px 0;
    border-bottom: 1px solid #f0f2f5;
    transition: background 0.3s;
}

.comment-card:last-child {
    border-bottom: none;
}

.comment-card:hover {
    background: #fafbfc;
    margin: 0 -8px;
    padding-left: 8px;
    padding-right: 8px;
    border-radius: 8px;
}

.comment-avatar {
    flex-shrink: 0;
}

.comment-avatar img {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #f0f0f0;
}

.comment-body {
    flex: 1;
    min-width: 0;
}

.comment-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 6px;
    flex-wrap: wrap;
}

.comment-name {
    font-weight: 700;
    color: #1a1a2e;
    font-size: 14px;
}

.comment-seat {
    font-size: 12px;
    color: #28b2d4;
    background: #e8f8fc;
    padding: 2px 10px;
    border-radius: 12px;
    display: inline-flex;
    align-items: center;
    gap: 3px;
}

.comment-time {
    font-size: 12px;
    color: #bbb;
    margin-left: auto;
}

.comment-rate {
    margin-bottom: 6px;
}

.comment-text {
    font-size: 14px;
    color: #555;
    line-height: 1.8;
}

/* ==================== Responsive ==================== */
@media (max-width: 768px) {
    .seat-grid {
        gap: 6px;
    }

    .seat-row {
        gap: 6px;
    }

    .seat-cell {
        width: 50px;
        height: 62px;
    }
}
</style>
