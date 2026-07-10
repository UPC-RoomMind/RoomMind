<template>
    <div class="admin-layout">
        <el-container style="height: 100vh">
            <!-- Header -->
            <el-header class="admin-header">
                <div class="header-left">
                    <div class="logo-box">
                        <img :src="require('@/assets/logo2.png')" alt="Logo" />
                        <span class="system-name">自习室管理端</span>
                    </div>
                </div>
                <div class="header-right">
                    <el-avatar :size="36" :src="UserInfo.ImageUrls" class="header-avatar">
                        <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
                    </el-avatar>
                    <el-dropdown trigger="click" @command="handleCommand">
                        <span class="user-dropdown-link">
                            {{ UserInfo.Name }}
                            <i class="el-icon-arrow-down el-icon--right"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="profile" icon="el-icon-user">
                                个人信息
                            </el-dropdown-item>
                            <el-dropdown-item command="password" icon="el-icon-lock">
                                修改密码
                            </el-dropdown-item>
                            <el-dropdown-item divided command="logout" icon="el-icon-switch-button">
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </el-header>

            <el-container class="body-container">
                <!-- Sidebar -->
                <el-aside class="admin-sidebar" width="220px">
                    <el-menu class="sidebar-menu"
                        :default-active="activeMenu"
                        :router="true"
                        active-text-color="#28b2d4"
                        background-color="#1a2530"
                        text-color="#a0b0c0">

                        <el-menu-item index="/Admin/Home">
                            <i class="el-icon-s-data"></i>
                            <span>控制台</span>
                        </el-menu-item>

                        <el-submenu index="user-mgmt">
                            <template slot="title">
                                <i class="el-icon-user-solid"></i>
                                <span>用户管理</span>
                            </template>
                            <el-menu-item index="/Admin/UserList">
                                <i class="el-icon-s-custom"></i>
                                <span>用户信息</span>
                            </el-menu-item>
                        </el-submenu>

                        <el-menu-item index="/Admin/RoomList">
                            <i class="el-icon-office-building"></i>
                            <span>自习室</span>
                        </el-menu-item>

                        <el-menu-item index="/Admin/SeatList">
                            <i class="el-icon-s-grid"></i>
                            <span>座位管理</span>
                        </el-menu-item>

                        <el-menu-item index="/Admin/AppointRecordList">
                            <i class="el-icon-s-order"></i>
                            <span>预约记录</span>
                        </el-menu-item>

                        <el-menu-item index="/Admin/BannerList">
                            <i class="el-icon-picture-outline"></i>
                            <span>封面管理</span>
                        </el-menu-item>

                        <el-menu-item index="/Admin/IntegralList">
                            <i class="el-icon-medal"></i>
                            <span>积分管理</span>
                        </el-menu-item>

                        <el-menu-item index="/Admin/AiAssistant">
                            <i class="el-icon-cpu"></i>
                            <span>AI助手</span>
                        </el-menu-item>
                    </el-menu>
                </el-aside>

                <!-- Main Content -->
                <el-main class="admin-main">
                    <!-- Breadcrumb -->
                    <div class="breadcrumb-bar">
                        <el-breadcrumb separator-class="el-icon-arrow-right">
                            <el-breadcrumb-item v-for="(item, index) in breadList" :key="index"
                                :to="item.path">{{ item.meta.title }}</el-breadcrumb-item>
                        </el-breadcrumb>
                    </div>

                    <!-- Page Content -->
                    <transition name="fade-slide" mode="out-in">
                        <router-view></router-view>
                    </transition>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
import { adminRouters } from "@/router/index"
import { mapGetters } from "vuex";
export default {
    name: 'Layout',
    data() {
        return {
            routerMenu: [],
            breadList: [],
        }
    },
    computed: {
        ...mapGetters(["UserInfo"]),
        activeMenu() {
            return this.$route.path;
        }
    },
    watch: {
        $route() {
            this.getBreadcrumb();
        },
    },
    created() {
        this.routerMenu = adminRouters;
        this.getBreadcrumb();
    },
    methods: {
        handleCommand(command) {
            if (command === 'logout') {
                this.LoginOut();
            } else if (command === 'profile') {
                this.$router.push({ path: '/Admin/UserPerson' });
            } else if (command === 'password') {
                this.$router.push({ path: '/Admin/PasswordEdit' });
            }
        },
        async LoginOut() {
            await this.$store.dispatch('Logout')
            this.$router.push('/Login');
        },
        isHome(route) {
            return route.path === "/Admin";
        },
        getBreadcrumb() {
            let matched = this.$route.matched;
            if (!this.isHome(matched[0])) {
                matched = [{ path: "/Admin", meta: { title: "控制台" } }].concat(matched);
            }
            this.breadList = matched;
        }
    }
}
</script>

<style scoped>
/* ==================== Layout ==================== */
.admin-layout {
    height: 100vh;
    overflow: hidden;
}

/* ==================== Header ==================== */
.admin-header {
    background: linear-gradient(135deg, #1a2530 0%, #203a43 50%, #1a3a45 100%);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px !important;
    box-shadow: 0 2px 14px rgba(0,0,0,0.2);
    z-index: 100;
    height: 60px !important;
}

.header-left .logo-box {
    display: flex;
    align-items: center;
    gap: 10px;
}

.header-left .logo-box img {
    height: 30px;
    object-fit: contain;
}

.header-left .system-name {
    color: white;
    font-weight: 600;
    font-size: 16px;
    letter-spacing: 1px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 10px;
}

.header-avatar {
    border: 2px solid rgba(255,255,255,0.3);
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.user-dropdown-link {
    color: white;
    font-weight: 600;
    font-size: 14px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 8px;
    transition: background 0.3s;
    display: flex;
    align-items: center;
}

.user-dropdown-link:hover {
    background: rgba(255,255,255,0.12);
}

/* ==================== Body ==================== */
.body-container {
    height: calc(100vh - 60px);
}

/* ==================== Sidebar ==================== */
.admin-sidebar {
    background: #1a2530 !important;
    overflow-y: auto;
    overflow-x: hidden;
    box-shadow: 2px 0 12px rgba(0,0,0,0.08);
}

.sidebar-menu {
    border-right: none !important;
    padding-top: 8px;
}

.sidebar-menu >>> .el-menu-item {
    font-size: 14px;
    font-weight: 500;
    height: 50px;
    line-height: 50px;
    margin: 2px 8px;
    border-radius: 8px;
    transition: all 0.3s;
    letter-spacing: 0.5px;
}

.sidebar-menu >>> .el-menu-item:hover {
    background: rgba(40,178,212,0.12) !important;
    color: #5ccce6 !important;
}

.sidebar-menu >>> .el-menu-item.is-active {
    background: linear-gradient(135deg, rgba(40,178,212,0.2), rgba(40,178,212,0.08)) !important;
    color: #28b2d4 !important;
    font-weight: 700;
    box-shadow: inset 3px 0 0 #28b2d4;
}

.sidebar-menu >>> .el-menu-item i {
    font-size: 18px;
    margin-right: 8px;
}

/* Submenu */
.sidebar-menu >>> .el-submenu__title {
    font-size: 14px;
    font-weight: 500;
    height: 50px;
    line-height: 50px;
    margin: 2px 8px;
    border-radius: 8px;
    transition: all 0.3s;
    letter-spacing: 0.5px;
}

.sidebar-menu >>> .el-submenu__title:hover {
    background: rgba(40,178,212,0.12) !important;
    color: #5ccce6 !important;
}

.sidebar-menu >>> .el-submenu__title i {
    font-size: 18px;
    margin-right: 8px;
}

/* Submenu children */
.sidebar-menu >>> .el-menu--inline {
    background: rgba(0,0,0,0.15) !important;
}

.sidebar-menu >>> .el-menu--inline .el-menu-item {
    padding-left: 56px !important;
    font-size: 13px;
    height: 44px;
    line-height: 44px;
}

/* ==================== Main Content ==================== */
.admin-main {
    background: #f0f4f8;
    padding: 0 24px 24px !important;
    overflow-y: auto;
    height: calc(100vh - 60px);
}

/* Breadcrumb bar */
.breadcrumb-bar {
    padding: 16px 0 12px;
}

/* ==================== Transition ==================== */
.fade-slide-enter-active,
.fade-slide-leave-active {
    transition: all 0.3s ease;
}

.fade-slide-enter {
    opacity: 0;
    transform: translateX(20px);
}

.fade-slide-leave-to {
    opacity: 0;
    transform: translateX(-20px);
}

/* ==================== Scrollbar for main ==================== */
.admin-main::-webkit-scrollbar {
    width: 6px;
}

.admin-main::-webkit-scrollbar-track {
    background: transparent;
}

.admin-main::-webkit-scrollbar-thumb {
    background: #c8d6e5;
    border-radius: 3px;
}

.admin-main::-webkit-scrollbar-thumb:hover {
    background: #a0b3c6;
}

.admin-sidebar::-webkit-scrollbar {
    width: 4px;
}

.admin-sidebar::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.1);
    border-radius: 2px;
}
</style>