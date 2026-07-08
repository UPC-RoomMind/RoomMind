<template>
    <div class="login-page">
        <!-- Aurora blobs -->
        <div class="aurora">
            <div class="aurora-blob blob-1"></div>
            <div class="aurora-blob blob-2"></div>
            <div class="aurora-blob blob-3"></div>
        </div>

        <!-- Starfield -->
        <div class="starfield">
            <span v-for="i in 80" :key="'s'+i" class="star" :style="starStyle(i)"></span>
        </div>

        <!-- Shooting stars -->
        <div class="shooting-star star-1"></div>
        <div class="shooting-star star-2"></div>
        <div class="shooting-star star-3"></div>

        <!-- Floating orbs -->
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>

        <div class="login-container">
            <!-- Left: Branding Panel -->
            <div class="login-brand">
                <!-- Hexagon mesh overlay -->
                <div class="hex-mesh"></div>
                <!-- Diagonal lines -->
                <div class="brand-lines">
                    <span v-for="i in 6" :key="i" class="brand-line" :style="{ left: (i*18-9)+'%', animationDelay: (i*0.4)+'s' }"></span>
                </div>

                <div class="brand-content">
                    <div class="brand-icon-wrap">
                        <!-- Rotating rings -->
                        <div class="orbit orbit-1"></div>
                        <div class="orbit orbit-2"></div>
                        <div class="orbit orbit-3"></div>
                        <!-- Ripple rings -->
                        <div class="icon-ring ring-1"></div>
                        <div class="icon-ring ring-2"></div>
                        <!-- Icon -->
                        <div class="brand-icon">
                            <i class="el-icon-reading"></i>
                        </div>
                    </div>

                    <h1 class="brand-title">
                        <span class="title-char" v-for="(char, i) in '志高自习室'" :key="'t'+i"
                            :style="{ animationDelay: (0.08 * i) + 's' }">{{ char }}</span>
                    </h1>

                    <div class="brand-divider">
                        <span class="divider-line"></span>
                        <span class="divider-dot"></span>
                        <span class="divider-line"></span>
                    </div>

                    <p class="brand-subtitle">安静的自习空间，高效的学习体验</p>

                    <div class="brand-features">
                        <div class="feature-item" style="animation-delay: 0.3s">
                            <div class="feature-icon-wrap">
                                <i class="el-icon-s-home"></i>
                            </div>
                            <span>全国多城市覆盖</span>
                        </div>
                        <div class="feature-item" style="animation-delay: 0.5s">
                            <div class="feature-icon-wrap">
                                <i class="el-icon-date"></i>
                            </div>
                            <span>灵活预约时段</span>
                        </div>
                        <div class="feature-item" style="animation-delay: 0.7s">
                            <div class="feature-icon-wrap">
                                <i class="el-icon-star-on"></i>
                            </div>
                            <span>积分奖励体系</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right: Login Form -->
            <div class="login-form-panel">
                <div class="form-header">
                    <div class="header-icon">
                        <i class="el-icon-s-custom"></i>
                    </div>
                    <h2>欢迎回来</h2>
                    <p>登录您的账户继续使用</p>
                </div>

                <el-form ref="loginForm" :model="formData" label-width="70px"
                    label-position="left" :rules="rules" class="login-form">

                    <el-form-item label="账号" prop="UserName">
                        <el-input type="text" v-model.trim="formData.UserName" placeholder="请输入账号"
                            prefix-icon="el-icon-user-solid">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="密码" prop="Password">
                        <el-input type="password" v-model.trim="formData.Password" placeholder="请输入密码"
                            prefix-icon="el-icon-lock" @keyup.enter.native="LoginBtn">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="角色" prop="RoleType">
                        <el-radio-group v-model="formData.RoleType" class="role-group">
                            <el-radio-button v-for="(item, index) in roleOptions" :key="item.Code"
                                :label="item.Code">{{ item.Label }}</el-radio-button>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="验证码" prop="Code">
                        <div class="captcha-row">
                            <el-input v-model.trim="formData.Code" placeholder="请输入验证码"
                                prefix-icon="el-icon-key" class="captcha-input">
                            </el-input>
                            <div class="captcha-box" @click="refreshCaptcha">
                                <ValidCode ref="ValidCode"></ValidCode>
                            </div>
                        </div>
                    </el-form-item>

                    <el-form-item class="form-actions">
                        <el-button class="login-btn" type="primary" @click="LoginBtn" :loading="loading">
                            <span class="btn-text">登 录</span>
                            <span class="btn-shimmer"></span>
                        </el-button>
                        <el-button class="home-btn" @click="ToHome">
                            访 客 浏 览
                        </el-button>
                    </el-form-item>
                </el-form>

                <div class="form-footer">
                    <span>还没有账号？</span>
                    <RouterLink :to="{ path: '/Register' }" class="register-link">
                        立即注册 <i class="el-icon-arrow-right"></i>
                    </RouterLink>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import ValidCode from '@/components/Code/canvas.vue';
import store from '@/store';
export default {
    components: { ValidCode },
    data() {
        return {
            loading: false,
            formData: {
                UserName: '', Password: '', RoleType: "", Code: ""
            },
            roleOptions: [],
            rules: {
                UserName: [ { required: true, message: '请输入账号', trigger: 'blur' } ],
                Password: [ { required: true, message: '请输入密码', trigger: 'blur' } ],
                RoleType: [ { required: true, message: '请选择角色', trigger: 'change' } ],
                Code: [
                    { required: true, message: '请输入验证码', trigger: 'blur' },
                    { validator: (rule, value, callback) => {
                        let identifyCode = this.$refs.ValidCode.getCode();
                        if (value != identifyCode) { callback(new Error('验证码不正确')); }
                        else { callback(); }
                    }, trigger: 'blur' }
                ]
            }
        }
    },
    created() { this.GetRoleTypeApi(); },
    methods: {
        starStyle(i) {
            const size = 1 + Math.sin(i * 7.3) * 1.5;
            return {
                left: (Math.sin(i * 3.7) * 50 + 50) + '%',
                top: (Math.cos(i * 5.1) * 50 + 50) + '%',
                width: Math.max(1, size) + 'px',
                height: Math.max(1, size) + 'px',
                animationDelay: (Math.sin(i) * 3) + 's',
                animationDuration: (2 + Math.cos(i * 2) * 2) + 's'
            };
        },
        refreshCaptcha() {
            if (this.$refs.ValidCode) this.$refs.ValidCode.refreshCode();
        },
        async GetRoleTypeApi() {
            let { Data: { Items } } = await this.$Post("/Select/RoleType");
            this.roleOptions = Items
        },
        LoginBtn() {
            this.$refs.loginForm.validate(async (valid) => {
                if (valid) {
                    this.loading = true;
                    let res = await store.dispatch("Login", this.formData);
                    this.loading = false;
                    if (res.Success) {
                        this.$message.success("登录成功!");
                        this.$router.push({ path: "/Admin" })
                    } else { this.$refs.ValidCode.refreshCode(); }
                } else {
                    this.$message.error("请完善登录信息")
                    this.$refs.ValidCode.refreshCode();
                }
            });
        },
        ToHome() { this.$router.push({ path: "/Front/Home" }) },
    }
}
</script>

<style scoped>
/* ============================================================
   PAGE - Aurora animated background
   ============================================================ */
.login-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #060b14;
    position: relative;
    overflow: hidden;
    font-family: 'Microsoft YaHei', sans-serif;
}

/* ---- Aurora blobs ---- */
.aurora {
    position: absolute; inset: 0;
    pointer-events: none; z-index: 0;
    filter: blur(100px);
}
.aurora-blob {
    position: absolute;
    border-radius: 50%;
    opacity: 0.45;
}
.blob-1 {
    width: 700px; height: 700px;
    background: radial-gradient(circle, #28b2d4 0%, transparent 70%);
    top: -20%; left: -15%;
    animation: auroraDrift 18s ease-in-out infinite;
}
.blob-2 {
    width: 600px; height: 600px;
    background: radial-gradient(circle, #7330a0 0%, transparent 70%);
    bottom: -25%; right: -15%;
    animation: auroraDrift 22s ease-in-out infinite reverse;
}
.blob-3 {
    width: 500px; height: 500px;
    background: radial-gradient(circle, #1a6da0 0%, transparent 70%);
    top: 45%; left: 40%;
    animation: auroraDrift 20s ease-in-out infinite 5s;
}
@keyframes auroraDrift {
    0%, 100% { transform: translate(0, 0) scale(1); }
    25% { transform: translate(60px, -40px) scale(1.1); }
    50% { transform: translate(-30px, 50px) scale(0.9); }
    75% { transform: translate(-50px, -30px) scale(1.05); }
}

/* ---- Starfield ---- */
.starfield {
    position: absolute; inset: 0;
    pointer-events: none; z-index: 0;
}
.star {
    position: absolute;
    background: #fff;
    border-radius: 50%;
    animation: twinkle 3s ease-in-out infinite;
}
@keyframes twinkle {
    0%, 100% { opacity: 0.2; box-shadow: 0 0 0 0 rgba(255,255,255,0); }
    50% { opacity: 1; box-shadow: 0 0 6px 2px rgba(180,220,255,0.8); }
}

/* ---- Shooting stars ---- */
.shooting-star {
    position: absolute;
    width: 2px; height: 2px;
    background: #fff;
    border-radius: 50%;
    opacity: 0;
    animation: shoot 6s linear infinite;
}
.shooting-star::after {
    content: '';
    position: absolute;
    top: 50%; right: 0;
    transform: translateY(-50%);
    width: 80px; height: 1px;
    background: linear-gradient(90deg, rgba(255,255,255,0.8), transparent);
}
.star-1 { top: 15%; left: 70%; animation-delay: 0s; }
.star-2 { top: 30%; left: 80%; animation-delay: 2s; animation-duration: 8s; }
.star-3 { top: 8%;  left: 60%; animation-delay: 4s; animation-duration: 7s; }
@keyframes shoot {
    0% { opacity: 0; transform: translate(0, 0) rotate(-30deg); }
    5% { opacity: 1; }
    10% { opacity: 0; transform: translate(-400px, 200px) rotate(-30deg); }
    100% { opacity: 0; transform: translate(-400px, 200px) rotate(-30deg); }
}

/* ---- Floating orbs ---- */
.orb {
    position: absolute;
    border-radius: 50%;
    pointer-events: none; z-index: 0;
    border: 1px solid rgba(255,255,255,0.08);
}
.orb-1 {
    width: 350px; height: 350px;
    top: 10%; left: 5%;
    animation: orbFloat 25s linear infinite;
}
.orb-2 {
    width: 250px; height: 250px;
    bottom: 15%; right: 8%;
    border-color: rgba(40,178,212,0.1);
    animation: orbFloat 20s linear infinite reverse;
}
.orb-3 {
    width: 200px; height: 200px;
    top: 50%; left: 50%;
    border-color: rgba(255,255,255,0.05);
    animation: orbFloat 30s linear infinite 5s;
}
@keyframes orbFloat {
    0% { transform: translate(0, 0) rotate(0deg); }
    25% { transform: translate(30px, -20px) rotate(90deg); }
    50% { transform: translate(-10px, -40px) rotate(180deg); }
    75% { transform: translate(-30px, 10px) rotate(270deg); }
    100% { transform: translate(0, 0) rotate(360deg); }
}

/* ============================================================
   CARD - Glass morphism + double glow
   ============================================================ */
.login-container {
    display: flex;
    width: 1050px;
    min-height: 580px;
    background: rgba(255,255,255,0.92);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 24px;
    overflow: hidden;
    position: relative;
    z-index: 1;
    box-shadow:
        0 0 0 1px rgba(255,255,255,0.15),
        0 30px 80px rgba(0,0,0,0.4),
        0 0 120px rgba(40,178,212,0.1);
    animation: cardEnter 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes cardEnter {
    from { opacity: 0; transform: translateY(40px) scale(0.97); }
    to { opacity: 1; transform: translateY(0) scale(1); }
}

/* Glowing outer ring */
.login-container::before {
    content: '';
    position: absolute;
    inset: -4px;
    border-radius: 26px;
    padding: 2px;
    background: conic-gradient(from 0deg, transparent, #28b2d4, #5ccce6, #7330a0, #28b2d4, transparent);
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    z-index: -1;
    animation: conicSpin 6s linear infinite;
    opacity: 0.5;
}
@keyframes conicSpin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* ============================================================
   LEFT - Brand Panel
   ============================================================ */
.login-brand {
    flex: 1;
    background: linear-gradient(160deg, #0d2b38 0%, #1a5d70 25%, #1a8da8 50%, #165060 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 50px 40px;
    position: relative;
    overflow: hidden;
    border-radius: 24px 0 0 24px;
}

/* Hexagon mesh overlay */
.hex-mesh {
    position: absolute; inset: 0;
    background-image:
        linear-gradient(30deg, rgba(255,255,255,0.03) 1px, transparent 1px),
        linear-gradient(-30deg, rgba(255,255,255,0.03) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
    background-size: 40px 70px, 40px 70px, 40px 40px;
    opacity: 0.6;
    animation: meshDrift 30s linear infinite;
}
@keyframes meshDrift {
    0% { background-position: 0 0, 0 0, 0 0; }
    100% { background-position: 40px 70px, -40px 70px, 0 40px; }
}

/* Diagonal animated lines */
.brand-line {
    position: absolute;
    top: -10%;
    width: 1px;
    height: 120%;
    background: linear-gradient(180deg, transparent, rgba(255,255,255,0.08), transparent);
    animation: lineSweep 8s ease-in-out infinite;
}
@keyframes lineSweep {
    0%, 100% { opacity: 0.1; }
    50% { opacity: 0.5; }
}

.brand-content {
    position: relative; z-index: 1;
    color: white; text-align: center;
}

/* ---- Icon with orbits ---- */
.brand-icon-wrap {
    position: relative;
    width: 120px; height: 120px;
    margin: 0 auto 28px;
}

/* Rotating elliptical orbits */
.orbit {
    position: absolute;
    top: 50%; left: 50%;
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.15);
    animation: orbitSpin 8s linear infinite;
}
.orbit-1 { width: 110px; height: 110px; margin: -55px 0 0 -55px; animation-duration: 8s; }
.orbit-2 { width: 130px; height: 80px; margin: -40px 0 0 -65px; animation-duration: 12s; animation-direction: reverse; transform: rotate(30deg); }
.orbit-3 { width: 130px; height: 80px; margin: -40px 0 0 -65px; animation-duration: 10s; transform: rotate(-30deg); }
@keyframes orbitSpin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* Ripple */
.icon-ring {
    position: absolute;
    top: 50%; left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 50%;
    border: 1px solid rgba(255,255,255,0.25);
    animation: ringExpand 3s ease-out infinite;
}
.ring-1 { width: 80px; height: 80px; animation-delay: 0s; }
.ring-2 { width: 80px; height: 80px; animation-delay: 1.5s; }
@keyframes ringExpand {
    0% { width: 80px; height: 80px; opacity: 0.5; border-width: 2px; }
    100% { width: 160px; height: 160px; opacity: 0; border-width: 0.5px; }
}

.brand-icon {
    position: absolute;
    top: 50%; left: 50%;
    transform: translate(-50%, -50%);
    width: 80px; height: 80px;
    background: rgba(255,255,255,0.15);
    border-radius: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 42px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255,255,255,0.3);
    z-index: 1;
    animation: iconGlow 3s ease-in-out infinite;
    transition: all 0.5s;
}
@keyframes iconGlow {
    0%, 100% { box-shadow: 0 0 20px rgba(255,255,255,0.1), 0 0 40px rgba(40,178,212,0.1); }
    50% { box-shadow: 0 0 40px rgba(255,255,255,0.2), 0 0 80px rgba(40,178,212,0.25); }
}
.brand-icon:hover {
    border-color: rgba(255,255,255,0.6);
    box-shadow: 0 0 60px rgba(255,255,255,0.3), 0 0 100px rgba(40,178,212,0.4) !important;
    transform: translate(-50%, -50%) scale(1.05);
}

/* ---- Animated title chars ---- */
.brand-title {
    font-size: 34px;
    font-weight: 900;
    letter-spacing: 6px;
    margin-bottom: 16px;
    display: flex;
    justify-content: center;
}
.title-char {
    display: inline-block;
    opacity: 0;
    animation: charBounce 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
    background: linear-gradient(180deg, #fff 0%, #c8e8f0 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    text-shadow: none;
}
@keyframes charBounce {
    0% { opacity: 0; transform: translateY(30px) scale(0.5); }
    100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* ---- Divider ---- */
.brand-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    margin-bottom: 16px;
}
.divider-line {
    width: 50px; height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent);
}
.divider-dot {
    width: 6px; height: 6px;
    background: #fff;
    border-radius: 50%;
    animation: dotPulse 2s ease-in-out infinite;
}
@keyframes dotPulse {
    0%, 100% { box-shadow: 0 0 4px rgba(255,255,255,0.5); }
    50% { box-shadow: 0 0 16px rgba(255,255,255,1); }
}

.brand-subtitle {
    font-size: 14px;
    opacity: 0.8;
    margin-bottom: 36px;
    letter-spacing: 2px;
    animation: fadeInUp 0.8s ease 0.6s both;
}
@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(15px); }
    to { opacity: 0.8; transform: translateY(0); }
}

/* ---- Feature items ---- */
.brand-features { text-align: left; }

.feature-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 0;
    font-size: 14px;
    opacity: 0;
    animation: slideRight 0.6s ease forwards;
    transition: all 0.35s;
}
.feature-icon-wrap {
    width: 36px; height: 36px;
    background: rgba(255,255,255,0.12);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    border: 1px solid rgba(255,255,255,0.15);
    transition: all 0.35s;
    flex-shrink: 0;
}
.feature-item:hover .feature-icon-wrap {
    background: rgba(255,255,255,0.25);
    border-color: rgba(255,255,255,0.4);
    box-shadow: 0 0 20px rgba(255,255,255,0.15);
    transform: scale(1.1);
}
.feature-item:hover {
    transform: translateX(8px);
}
.feature-item:hover span {
    text-shadow: 0 0 15px rgba(255,255,255,0.6);
}
@keyframes slideRight {
    from { opacity: 0; transform: translateX(-25px); }
    to { opacity: 0.9; transform: translateX(0); }
}

/* ============================================================
   RIGHT - Form Panel
   ============================================================ */
.login-form-panel {
    flex: 1;
    padding: 50px 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.form-header {
    margin-bottom: 32px;
    text-align: center;
}
.header-icon {
    width: 50px; height: 50px;
    margin: 0 auto 14px;
    background: linear-gradient(135deg, #e8f8fc, #c8eef6);
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
    color: #1a8da8;
    animation: iconBreath 3s ease-in-out infinite;
}
@keyframes iconBreath {
    0%, 100% { box-shadow: 0 0 0 0 rgba(40,178,212,0.3); }
    50% { box-shadow: 0 0 20px 6px rgba(40,178,212,0.12); }
}
.form-header h2 {
    font-size: 26px;
    font-weight: 800;
    color: #1a1a2e;
    margin-bottom: 4px;
    letter-spacing: 1px;
}
.form-header p {
    font-size: 13px;
    color: #999;
}

/* ---- Form ---- */
.login-form >>> .el-form-item {
    margin-bottom: 18px;
}
.login-form >>> .el-form-item__label {
    font-weight: 600;
    color: #555;
    font-size: 13px;
}
.login-form >>> .el-input__prefix {
    color: #28b2d4;
    transition: all 0.3s;
}
.login-form >>> .el-input__inner {
    border-radius: 10px;
    border: 1.5px solid #e8ecf2;
    height: 44px;
    line-height: 44px;
    font-size: 14px;
    background: #f9fafc;
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
.login-form >>> .el-input__inner:hover {
    border-color: #b8dce8;
    background: #fff;
}
.login-form >>> .el-input__inner:focus {
    border-color: #28b2d4;
    background: #fff;
    box-shadow: 0 0 0 4px rgba(40,178,212,0.06), 0 4px 16px rgba(40,178,212,0.08);
    transform: translateY(-1px);
}

/* ---- Role ---- */
.role-group { width: 100%; }
.role-group >>> .el-radio-button__inner {
    border-radius: 10px !important;
    padding: 10px 30px;
    font-weight: 600;
    border: 1.5px solid #e8ecf2 !important;
    background: #f9fafc;
    transition: all 0.3s;
}
.role-group >>> .el-radio-button:first-child .el-radio-button__inner {
    border-radius: 10px 0 0 10px !important;
}
.role-group >>> .el-radio-button:last-child .el-radio-button__inner {
    border-radius: 0 10px 10px 0 !important;
}
.role-group >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background: linear-gradient(135deg, #28b2d4, #1a8da8) !important;
    border-color: #28b2d4 !important;
    color: white !important;
    box-shadow: 0 4px 16px rgba(40,178,212,0.35);
    animation: selectPop 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes selectPop {
    0% { transform: scale(1); }
    40% { transform: scale(1.06); }
    100% { transform: scale(1); }
}

/* ---- Captcha ---- */
.captcha-row { display: flex; gap: 10px; align-items: center; }
.captcha-input { flex: 1; }
.captcha-box {
    cursor: pointer;
    border-radius: 10px;
    overflow: hidden;
    border: 1.5px solid #e8ecf2;
    transition: all 0.3s;
    height: 44px;
    flex-shrink: 0;
}
.captcha-box:hover {
    border-color: #28b2d4;
    box-shadow: 0 2px 12px rgba(40,178,212,0.12);
}
.captcha-box:active { transform: scale(0.96); }

/* ---- Buttons ---- */
.form-actions { margin-top: 4px; }
.form-actions >>> .el-form-item__content {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.login-btn {
    width: 100%;
    height: 48px;
    font-size: 17px;
    font-weight: 800;
    letter-spacing: 4px;
    border-radius: 12px;
    background: linear-gradient(135deg, #28b2d4 0%, #1a8da8 50%, #28b2d4 100%) !important;
    background-size: 200% 100% !important;
    border: none !important;
    box-shadow: 0 6px 24px rgba(40,178,212,0.35);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    color: white !important;
}
.btn-shimmer {
    position: absolute;
    inset: 0;
    background: linear-gradient(105deg,
        transparent 40%, rgba(255,255,255,0.2) 45%,
        rgba(255,255,255,0.4) 50%, rgba(255,255,255,0.2) 55%,
        transparent 60%);
    transform: translateX(-100%);
    transition: transform 0.6s;
}
.login-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 32px rgba(40,178,212,0.5), 0 0 0 4px rgba(40,178,212,0.08);
    background-position: 100% 0 !important;
}
.login-btn:hover .btn-shimmer {
    transform: translateX(100%);
}
.login-btn:active { transform: scale(0.97) translateY(0); }

.home-btn {
    width: 100%;
    height: 42px;
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 2px;
    border-radius: 10px;
    border: 2px solid #e8ecf2;
    color: #999;
    background: transparent;
    transition: all 0.35s;
}
.home-btn:hover {
    border-color: #28b2d4;
    color: #28b2d4;
    background: rgba(40,178,212,0.03);
    letter-spacing: 3px;
}

/* ---- Footer ---- */
.form-footer {
    text-align: center;
    margin-top: 24px;
    font-size: 13px;
    color: #aaa;
}
.register-link {
    color: #28b2d4;
    font-weight: 700;
    margin-left: 4px;
    transition: all 0.3s;
}
.register-link:hover { color: #1a8da8; }
.register-link i {
    font-size: 12px;
    transition: transform 0.3s;
}
.register-link:hover i { transform: translateX(3px); }

/* ============================================================
   Responsive
   ============================================================ */
@media (max-width: 1100px) {
    .login-container {
        width: 94%;
        flex-direction: column;
        min-height: auto;
    }
    .login-container::before { display: none; }
    .login-brand {
        border-radius: 24px 24px 0 0;
        padding: 35px 30px;
    }
    .hex-mesh, .brand-lines, .brand-features, .orbit, .icon-ring { display: none; }
    .brand-icon-wrap { width: 80px; height: 80px; margin-bottom: 16px; }
    .brand-icon { width: 60px; height: 60px; font-size: 30px; }
    .brand-title { font-size: 24px; letter-spacing: 3px; }
    .brand-divider { margin-bottom: 8px; }
    .brand-subtitle { margin-bottom: 0; font-size: 13px; }
    .login-form-panel { padding: 30px 28px; }
}
</style>
