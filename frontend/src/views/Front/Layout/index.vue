<template>
    <div class="front-layout">
        <el-container>
            <!-- Header -->
            <el-header class="front-header" height="64px">
                <div class="header-inner main-container">
                    <!-- Logo -->
                    <div class="logo-area" @click="ToPath('/Front/Home')">
                        <img class="logo-img" :src="require('@/assets/logo.jpg')" alt="Logo" />
                        <span class="logo-text">志高自习室预约</span>
                    </div>

                    <!-- Navigation -->
                    <div class="nav-area">
                        <el-menu :default-active="active"
                            mode="horizontal"
                            text-color="#444"
                            active-text-color="#28b2d4"
                            class="front-nav">

                            <el-menu-item index="/Front/Home" @click="ToPath('/Front/Home')">
                                <i class="el-icon-s-home"></i>
                                <span>首页</span>
                            </el-menu-item>

                            <el-menu-item v-if="Token" index="/Front/AppointRecordList" @click="ToPath('/Front/AppointRecordList')">
                                <i class="el-icon-document"></i>
                                <span>我的预约</span>
                            </el-menu-item>

                            <el-menu-item v-if="Token" index="/Front/IntegralList" @click="ToPath('/Front/IntegralList')">
                                <i class="el-icon-star-on"></i>
                                <span>我的积分</span>
                            </el-menu-item>

                            <!-- User Menu or Login/Register -->
                            <el-submenu v-if="Token" index="user" class="user-submenu">
                                <template slot="title">
                                    <i class="el-icon-user-solid"></i>
                                    <span>{{ UserInfo.UserName }}</span>
                                </template>
                                <el-menu-item @click="ToUserInfo()">
                                    <i class="el-icon-user"></i> 个人信息
                                </el-menu-item>
                                <el-menu-item @click="ToEditPassword()">
                                    <i class="el-icon-lock"></i> 修改密码
                                </el-menu-item>
                                <el-menu-item @click="LoginOut()" class="logout-item">
                                    <i class="el-icon-switch-button"></i> 退出登录
                                </el-menu-item>
                            </el-submenu>

                            <template v-if="!Token">
                                <el-menu-item class="auth-btn-item" index="login" @click="ToLogin()">
                                    <el-button class="nav-auth-btn login-btn-nav" size="small" round>登 录</el-button>
                                </el-menu-item>
                                <el-menu-item class="auth-btn-item" index="register" @click="ToRegister()">
                                    <el-button class="nav-auth-btn register-btn-nav" size="small" round>注 册</el-button>
                                </el-menu-item>
                            </template>
                        </el-menu>
                    </div>
                </div>
            </el-header>

            <!-- Main Content -->
            <el-main class="front-main">
                <div class="main-container">
                    <router-view></router-view>
                </div>
            </el-main>

            <!-- Footer -->
            <el-footer class="front-footer" height="auto">
                <div class="footer-inner">
                    <div class="footer-content main-container">
                        <div class="footer-col">
                            <h4>志高自习室</h4>
                            <p>为您提供安静、舒适、高效的自习空间</p>
                        </div>
                        <div class="footer-col">
                            <h4>服务项目</h4>
                            <ul>
                                <li>在线预约</li>
                                <li>座位选择</li>
                                <li>积分商城</li>
                            </ul>
                        </div>
                        <div class="footer-col">
                            <h4>联系我们</h4>
                            <ul>
                                <li><i class="el-icon-phone"></i> 400-123-4567</li>
                                <li><i class="el-icon-message"></i> service@zhigao.com</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer-bottom">
                        <span>© 2024 志高自习室预约系统 版权所有</span>
                    </div>
                </div>
            </el-footer>
        </el-container>

        <!-- AI 智能助手悬浮弹窗 -->
        <AiChatWidget />
    </div>
</template>

<script>
import * as echarts from "echarts";
import store from '@/store'
import { mapGetters } from 'vuex'
import AiChatWidget from '@/components/AiChatWidget.vue'

export default {
    components: {
        AiChatWidget
    },
    data() {
        return {
            active: 'home',
        }
    },
    computed: {
        ...mapGetters(["UserInfo", "Token"])
    },
    watch: {
        $route(to) {
            this.active = to.path;
        }
    },
    methods: {
        ToLogin() {
            this.$router.push({ path: "/Login" });
        },
        ToRegister() {
            this.$router.push({ path: "/Register" });
        },
        async LoginOut() {
            await this.$store.dispatch('Logout');
            this.$router.push('/Login');
            this.$message.success('已退出登录');
        },
        async ToPath(url) {
            this.$router.push({ path: url });
        },
        async ToUserInfo() {
            this.$router.push({ path: "/Front/UserPerson" });
        },
        async ToEditPassword() {
            this.$router.push({ path: "/Front/PasswordEdit" });
        },
        goBack() {
            this.$router.go(-1);
        }
    }
}
</script>

<style scoped>
/* ==================== Layout ==================== */
.front-layout {
    min-height: 100vh;
    background: #f0f4f8;
}

.main-container {
    width: 1300px;
    margin: 0 auto;
    padding: 0 20px;
}

/* ==================== Header ==================== */
.front-header {
    background: rgba(255,255,255,0.95) !important;
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    box-shadow: 0 1px 12px rgba(0,0,0,0.06);
    position: sticky;
    top: 0;
    z-index: 100;
    padding: 0 !important;
    border-bottom: 1px solid #f0f0f0;
}

.header-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
}

/* Logo */
.logo-area {
    display: flex;
    align-items: center;
    cursor: pointer;
    gap: 10px;
    user-select: none;
}

.logo-img {
    height: 40px;
    width: 40px;
    object-fit: contain;
    border-radius: 8px;
}

.logo-text {
    font-size: 18px;
    font-weight: 700;
    color: #1a1a2e;
    letter-spacing: 1px;
}

/* Navigation */
.nav-area {
    flex: 1;
    display: flex;
    justify-content: flex-end;
}

.front-nav {
    background: transparent !important;
    border-bottom: none !important;
    display: flex;
    align-items: center;
}

.front-nav >>> .el-menu-item {
    font-size: 14px;
    font-weight: 500;
    height: 64px;
    line-height: 64px;
    border-bottom: 2px solid transparent !important;
    transition: all 0.3s;
}

.front-nav >>> .el-menu-item:hover {
    background: #f5f9fc !important;
    color: #28b2d4 !important;
}

.front-nav >>> .el-menu-item.is-active {
    color: #28b2d4 !important;
    border-bottom-color: #28b2d4 !important;
    font-weight: 700;
}

/* User submenu */
.user-submenu >>> .el-submenu__title {
    font-size: 14px;
    font-weight: 500;
    height: 64px;
    line-height: 64px;
    border-bottom: 2px solid transparent !important;
}

.user-submenu >>> .el-submenu__title:hover {
    background: #f5f9fc !important;
    color: #28b2d4 !important;
}

.logout-item {
    color: #e74c3c !important;
}

.logout-item:hover {
    background: #fef0ef !important;
    color: #e74c3c !important;
}

/* Auth buttons in nav */
.auth-btn-item {
    height: 64px;
    line-height: 64px;
}

.auth-btn-item >>> .el-menu-item {
    padding: 0 6px !important;
}

.nav-auth-btn {
    font-weight: 600;
    letter-spacing: 0.5px;
    padding: 8px 22px !important;
    transition: all 0.3s;
}

.login-btn-nav {
    background: transparent !important;
    border: 2px solid #28b2d4 !important;
    color: #28b2d4 !important;
}

.login-btn-nav:hover {
    background: rgba(40,178,212,0.08) !important;
}

.register-btn-nav {
    background: linear-gradient(135deg, #28b2d4, #1a8da8) !important;
    border: 2px solid transparent !important;
    color: white !important;
    box-shadow: 0 2px 8px rgba(40,178,212,0.3);
}

.register-btn-nav:hover {
    box-shadow: 0 4px 16px rgba(40,178,212,0.45);
    transform: translateY(-1px);
}

/* ==================== Main Content ==================== */
.front-main {
    min-height: calc(100vh - 64px - 200px);
    padding: 24px 0;
}

/* ==================== Footer ==================== */
.front-footer {
    background: linear-gradient(180deg, #1a2530 0%, #16202a 100%);
    color: #b0bec5;
    padding: 0 !important;
}

.footer-inner {
    padding-top: 40px;
}

.footer-content {
    display: flex;
    gap: 60px;
    padding-bottom: 32px;
    border-bottom: 1px solid rgba(255,255,255,0.08);
}

.footer-col {
    flex: 1;
}

.footer-col h4 {
    color: white;
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 14px;
    letter-spacing: 0.5px;
}

.footer-col p {
    font-size: 13px;
    line-height: 1.8;
    color: #90a4ae;
}

.footer-col ul {
    list-style: none;
    padding: 0;
}

.footer-col ul li {
    font-size: 13px;
    line-height: 2.2;
    color: #90a4ae;
    transition: color 0.3s;
}

.footer-col ul li:hover {
    color: white;
}

.footer-col ul li i {
    margin-right: 6px;
    color: #28b2d4;
}

.footer-bottom {
    text-align: center;
    padding: 20px 0;
    font-size: 12px;
    color: #607d8b;
}

/* ==================== Responsive ==================== */
@media (max-width: 1360px) {
    .main-container {
        width: 100%;
    }
}
</style>