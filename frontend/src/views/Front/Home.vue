<template>
    <div class="front-home">
        <!-- Hero Carousel -->
        <div class="hero-carousel card" style="padding: 0; overflow: hidden;">
            <el-carousel :interval="5000" arrow="always" height="420px" indicator-position="outside">
                <el-carousel-item v-for="(item, index) in BannerList" :key="index">
                    <div class="carousel-slide">
                        <img :src="item.Cover" class="carousel-img" />
                        <div class="carousel-overlay">
                            <div class="overlay-content">
                                <h2>志高自习室</h2>
                                <p>安静 · 舒适 · 高效的学习空间</p>
                            </div>
                        </div>
                    </div>
                </el-carousel-item>
            </el-carousel>
        </div>

        <!-- Room List Section -->
        <div class="room-section card margin-top-lg">
            <div class="section-header">
                <span class="title">自习室列表</span>
                <span class="subtitle">选择您心仪的自习室开始学习之旅</span>
            </div>

            <Pagination url="/Room/List">
                <template v-slot:content="{ data }">
                    <div class="room-grid">
                        <div class="room-card" v-for="(item, index) in data" :key="index"
                            @click="ToRoom(item)" :style="{ animationDelay: (index * 0.06) + 's' }">
                            <div class="room-card-img">
                                <img :src="item.Cover" :alt="item.Name" />
                                <div class="room-card-overlay">
                                    <span class="view-btn">查看详情 <i class="el-icon-arrow-right"></i></span>
                                </div>
                            </div>
                            <div class="room-card-info">
                                <h3 class="room-name">{{ item.Name }}</h3>
                                <div class="room-meta">
                                    <span v-if="item.Address" class="room-addr">
                                        <i class="el-icon-location-outline"></i> {{ item.Address }}
                                    </span>
                                    <span class="room-stats">
                                        <i class="el-icon-star-on"></i>
                                        {{ item.AgvCommentScore || '暂无' }} 分
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </Pagination>
        </div>
    </div>
</template>

<script>
import Pagination from "@/components/Pagination/PaginationBox.vue"
export default {
    components: {
        Pagination
    },
    data() {
        return {
            BannerList: [],
            RoomList: []
        }
    },
    created() {
        this.BannerListApi();
    },
    methods: {
        async BannerListApi() {
            let { Data: { Items } } = await this.$Post("/Banner/List", {});
            this.BannerList = Items;
        },
        async ToRoom(item) {
            this.$router.push({
                path: "/Front/Room",
                query: { RoomId: item.Id }
            })
        }
    },
}
</script>

<style scoped>
/* ==================== Hero Carousel ==================== */
.carousel-slide {
    position: relative;
    width: 100%;
    height: 100%;
}

.carousel-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.carousel-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0,0,0,0.6));
    padding: 60px 40px 30px;
}

.overlay-content h2 {
    color: white;
    font-size: 32px;
    font-weight: 800;
    letter-spacing: 3px;
    margin-bottom: 6px;
}

.overlay-content p {
    color: rgba(255,255,255,0.85);
    font-size: 15px;
    letter-spacing: 1px;
}

/* ==================== Section Header ==================== */
.section-header {
    display: flex;
    align-items: baseline;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
}

.section-header .title {
    font-size: 20px;
    font-weight: 700;
    color: #1a1a2e;
    padding-left: 14px;
    border-left: 4px solid #28b2d4;
}

.section-header .subtitle {
    font-size: 13px;
    color: #999;
}

/* ==================== Room Grid ==================== */
.room-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    padding: 8px 0;
}

/* ==================== Room Card ==================== */
.room-card {
    background: white;
    border-radius: 14px;
    overflow: hidden;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    border: 1px solid #f0f0f0;
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    animation: cardFadeIn 0.5s ease forwards;
    opacity: 0;
}

@keyframes cardFadeIn {
    from {
        opacity: 0;
        transform: translateY(16px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.room-card:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(40,178,212,0.15), 0 4px 10px rgba(0,0,0,0.08);
    border-color: #28b2d4;
}

.room-card-img {
    position: relative;
    width: 100%;
    height: 220px;
    overflow: hidden;
}

.room-card-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.room-card:hover .room-card-img img {
    transform: scale(1.08);
}

.room-card-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(26,141,168,0);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 0.35s ease;
}

.room-card:hover .room-card-overlay {
    background: rgba(26,141,168,0.4);
}

.view-btn {
    color: white;
    font-weight: 600;
    font-size: 14px;
    padding: 8px 20px;
    border: 2px solid white;
    border-radius: 25px;
    opacity: 0;
    transform: translateY(10px);
    transition: all 0.35s ease;
    letter-spacing: 0.5px;
}

.room-card:hover .view-btn {
    opacity: 1;
    transform: translateY(0);
}

.room-card-info {
    padding: 16px 18px;
}

.room-name {
    font-size: 16px;
    font-weight: 700;
    color: #1a1a2e;
    margin-bottom: 8px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.room-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #999;
}

.room-addr {
    display: flex;
    align-items: center;
    gap: 3px;
    max-width: 60%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.room-addr i {
    color: #28b2d4;
    flex-shrink: 0;
}

.room-stats {
    display: flex;
    align-items: center;
    gap: 3px;
    color: #f7ad25;
    font-weight: 600;
    flex-shrink: 0;
}

/* ==================== Responsive ==================== */
@media (max-width: 1360px) {
    .room-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}

@media (max-width: 900px) {
    .room-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}
</style>
