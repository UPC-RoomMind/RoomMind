<template>
    <div class="page-404">
        <div class="notfound-card">
            <div class="error-code">404</div>
            <div class="error-divider"></div>
            <h1 class="error-title">页面未找到</h1>
            <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
            <div class="error-actions">
                <el-button type="primary" round size="large" @click="goHome">
                    <i class="el-icon-s-home"></i> 返回首页
                </el-button>
                <el-button round size="large" @click="goBack">
                    <i class="el-icon-back"></i> 返回上页
                </el-button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NotFound',
    methods: {
        goHome() {
            this.$router.push('/Front/Home');
        },
        goBack() {
            this.$router.go(-1);
        }
    }
}
</script>

<style scoped>
.page-404 {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f2027 0%, #203a43 40%, #2c5364 100%);
    position: relative;
    overflow: hidden;
}

.page-404::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    background: rgba(40,178,212,0.04);
    top: -200px;
    right: -100px;
    animation: float 20s ease-in-out infinite;
}

.page-404::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    border-radius: 50%;
    background: rgba(255,255,255,0.03);
    bottom: -100px;
    left: -80px;
    animation: float 15s ease-in-out infinite reverse;
}

@keyframes float {
    0%, 100% { transform: translate(0, 0); }
    50% { transform: translate(30px, -20px); }
}

.notfound-card {
    text-align: center;
    z-index: 1;
    animation: fadeInUp 0.6s ease;
}

@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}

.error-code {
    font-size: 140px;
    font-weight: 900;
    letter-spacing: 20px;
    background: linear-gradient(135deg, #28b2d4, #5ccce6, #28b2d4);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
    margin-bottom: 10px;
}

.error-divider {
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #28b2d4, #5ccce6);
    margin: 0 auto 24px;
    border-radius: 2px;
}

.error-title {
    font-size: 26px;
    font-weight: 700;
    color: white;
    margin-bottom: 10px;
    letter-spacing: 2px;
}

.error-desc {
    font-size: 15px;
    color: rgba(255,255,255,0.6);
    margin-bottom: 40px;
    letter-spacing: 0.5px;
}

.error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
}

.error-actions .el-button {
    font-weight: 600;
    letter-spacing: 0.5px;
    padding: 14px 32px;
    font-size: 15px;
}

.error-actions .el-button--default {
    background: rgba(255,255,255,0.1);
    border: 1px solid rgba(255,255,255,0.25);
    color: white;
}

.error-actions .el-button--default:hover {
    background: rgba(255,255,255,0.2);
    border-color: rgba(255,255,255,0.4);
}
</style>
