<template>
    <div class="register-page">
        <!-- Decorative background shapes -->
        <div class="bg-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
            <div class="shape shape-4"></div>
        </div>

        <div class="register-container">
            <!-- Left: Brand Panel -->
            <div class="register-brand">
                <div class="brand-content">
                    <div class="brand-icon">
                        <i class="el-icon-user-solid"></i>
                    </div>
                    <h1 class="brand-title">加入我们</h1>
                    <p class="brand-subtitle">注册成为志高自习室的会员</p>
                    <div class="brand-features">
                        <div class="feature-item">
                            <i class="el-icon-circle-check"></i>
                            <span>在线预约，随时选座</span>
                        </div>
                        <div class="feature-item">
                            <i class="el-icon-circle-check"></i>
                            <span>积分奖励，畅享优惠</span>
                        </div>
                        <div class="feature-item">
                            <i class="el-icon-circle-check"></i>
                            <span>全国多城市覆盖</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right: Register Form -->
            <div class="register-form-panel">
                <div class="form-header">
                    <h2>创建账户</h2>
                    <p>填写信息完成注册</p>
                </div>

                <el-form ref="loginForm" :model="formData" label-width="90px"
                    label-position="left" :rules="rules" class="register-form">

                    <el-form-item label="账号" prop="UserName">
                        <el-input type="text" v-model.trim="formData.UserName" placeholder="请输入账号"
                            prefix-icon="el-icon-user">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="密码" prop="Password">
                        <el-input type="password" v-model.trim="formData.Password"
                            placeholder="请输入密码" prefix-icon="el-icon-lock">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="邮箱" prop="Email">
                        <el-input v-model.trim="formData.Email" placeholder="请输入邮箱"
                            prefix-icon="el-icon-message">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="联系电话" prop="PhoneNumber">
                        <el-input v-model.trim="formData.PhoneNumber" placeholder="请输入联系方式"
                            prefix-icon="el-icon-phone">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="姓名" prop="Name">
                        <el-input v-model.trim="formData.Name" placeholder="请输入姓名"
                            prefix-icon="el-icon-edit-outline">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="验证码" prop="Code">
                        <div class="captcha-row">
                            <el-input v-model.trim="formData.Code" placeholder="请输入邮箱验证码"
                                prefix-icon="el-icon-key" class="captcha-input">
                            </el-input>
                            <el-button class="send-code-btn" @click="sendCode" :loading="sending">
                                {{ sending ? '发送中...' : '获取验证码' }}
                            </el-button>
                        </div>
                    </el-form-item>

                    <el-form-item class="form-actions">
                        <el-button class="register-btn" type="primary" @click="RegisterBtn" :loading="loading">
                            注 册
                        </el-button>
                    </el-form-item>
                </el-form>

                <div class="form-footer">
                    <span>已有账号？</span>
                    <RouterLink :to="{ path: '/Login' }" class="login-link">
                        去登录 <i class="el-icon-arrow-right"></i>
                    </RouterLink>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import store from '@/store';
import axios from 'axios'
export default {
    data() {
        return {
            loading: false,
            sending: false,
            formData: {
                UserName: '',
                Password: '',
                RoleType: "2",
                Code: "",
                Email: '',
                PhoneNumber: '',
                Name: ''
            },
            roleOptions: [],
            rules: {
                UserName: [
                    { required: true, message: '请输入账号', trigger: 'blur' },
                ],
                Password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码至少6位', trigger: 'blur' }
                ],
                Email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            var reg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
                            if (!value || !reg.test(value)) {
                                callback(new Error('请输入正确的邮箱格式'));
                            } else {
                                callback();
                            }
                        }, trigger: 'blur'
                    },
                ],
                Name: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                ],
                PhoneNumber: [
                    { required: true, message: '请输入联系方式', trigger: 'blur' },
                    {
                        validator: (rule, value, callback) => {
                            var reg = /^1[34578]\d{9}$/;
                            if (!value || !reg.test(value)) {
                                callback(new Error('请输入正确的手机号'));
                            } else {
                                callback();
                            }
                        }, trigger: 'blur'
                    },
                ],
                Code: [
                    { required: true, message: '请输入验证码', trigger: 'blur' }
                ]
            }
        }
    },
    created() {
        this.GetRoleTypeApi();
    },
    methods: {
        async GetRoleTypeApi() {
            let { Data: { Items } } = await this.$Post("/Select/RoleType");
            this.roleOptions = Items
        },
        async sendCode() {
            if (!this.formData.Email) {
                this.$message.error("请先输入邮箱地址");
                return;
            }
            const emailReg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
            if (!emailReg.test(this.formData.Email)) {
                this.$message.error("请输入正确的邮箱格式");
                return;
            }
            this.sending = true;
            try {
                const res = await axios.post('http://localhost:7245/auth/sendCode', {
                    email: this.formData.Email
                });
                if (res.data.Data.success) {
                    this.$message.success(res.data.Data.message || '验证码已发送到您的邮箱');
                } else {
                    this.$message.error(res.data.Data.message || '验证码发送失败');
                }
            } catch (error) {
                console.error('发送验证码失败', error);
                this.$message.error('验证码请求失败，请检查网络或邮箱');
            } finally {
                this.sending = false;
            }
        },

        RegisterBtn() {
            this.$refs.loginForm.validate(async (valid) => {
                if (valid) {
                    this.loading = true;
                    let res = await this.$Post("/User/Register", this.formData)
                    this.loading = false;
                    if (res.Success) {
                        if (res.Data.Name == "128") {
                            this.$message.error('注册失败，请重试');
                        } else {
                            this.$message.success("注册成功!");
                            this.$router.push({ path: "/Login" })
                        }
                    }
                } else {
                    this.$message.error("请完善注册信息")
                    return false;
                }
            });
        }
    }
}
</script>

<style scoped>
/* ==================== Page Layout ==================== */
.register-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f2027 0%, #203a43 40%, #2c5364 100%);
    position: relative;
    overflow: hidden;
    font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== Background Shapes ==================== */
.bg-shapes {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 0;
}

.shape {
    position: absolute;
    border-radius: 50%;
    background: rgba(255,255,255,0.03);
}

.shape-1 {
    width: 500px;
    height: 500px;
    top: -150px;
    left: -100px;
    animation: floatShape 20s ease-in-out infinite;
}

.shape-2 {
    width: 350px;
    height: 350px;
    bottom: -80px;
    right: -60px;
    animation: floatShape 15s ease-in-out infinite reverse;
}

.shape-3 {
    width: 180px;
    height: 180px;
    top: 40%;
    right: 20%;
    background: rgba(40,178,212,0.08);
    animation: floatShape 18s ease-in-out infinite 2s;
}

.shape-4 {
    width: 250px;
    height: 250px;
    bottom: 25%;
    left: 25%;
    background: rgba(40,178,212,0.05);
    border-radius: 50% 50% 40% 60% / 40% 60% 50% 50%;
    animation: morphShape 14s ease-in-out infinite;
}

@keyframes floatShape {
    0%, 100% { transform: translate(0, 0) rotate(0deg); }
    33% { transform: translate(25px, -25px) rotate(4deg); }
    66% { transform: translate(-15px, 15px) rotate(-3deg); }
}

@keyframes morphShape {
    0%, 100% { border-radius: 50% 50% 40% 60% / 40% 60% 50% 50%; }
    50% { border-radius: 40% 60% 50% 50% / 50% 50% 40% 60%; }
}

/* ==================== Register Container ==================== */
.register-container {
    display: flex;
    width: 1000px;
    min-height: 620px;
    background: rgba(255,255,255,0.95);
    border-radius: 20px;
    overflow: hidden;
    box-shadow:
        0 25px 60px rgba(0,0,0,0.3),
        0 0 0 1px rgba(255,255,255,0.1);
    z-index: 1;
    animation: fadeInUp 0.6s ease;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* ==================== Left Brand Panel ==================== */
.register-brand {
    flex: 1;
    background: linear-gradient(160deg, #1a8da8 0%, #28b2d4 40%, #1a6b80 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 50px 40px;
    position: relative;
    overflow: hidden;
}

.register-brand::before {
    content: '';
    position: absolute;
    bottom: -80px;
    right: -80px;
    width: 300px;
    height: 300px;
    border-radius: 50%;
    background: rgba(255,255,255,0.05);
}

.register-brand::after {
    content: '';
    position: absolute;
    top: -30px;
    left: -30px;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    background: rgba(255,255,255,0.06);
}

.brand-content {
    position: relative;
    z-index: 1;
    color: white;
    text-align: center;
}

.brand-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 24px;
    background: rgba(255,255,255,0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40px;
    backdrop-filter: blur(4px);
    border: 1px solid rgba(255,255,255,0.25);
}

.brand-title {
    font-size: 32px;
    font-weight: 800;
    margin-bottom: 10px;
    letter-spacing: 2px;
}

.brand-subtitle {
    font-size: 15px;
    opacity: 0.85;
    margin-bottom: 40px;
    letter-spacing: 1px;
}

.brand-features {
    text-align: left;
}

.feature-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 0;
    font-size: 14px;
    opacity: 0.9;
    transition: opacity 0.3s;
}

.feature-item i {
    font-size: 20px;
}

.feature-item:hover {
    opacity: 1;
}

/* ==================== Right Form Panel ==================== */
.register-form-panel {
    flex: 1;
    padding: 40px 45px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.form-header {
    margin-bottom: 24px;
}

.form-header h2 {
    font-size: 26px;
    font-weight: 700;
    color: #1a1a2e;
    margin-bottom: 6px;
}

.form-header p {
    font-size: 14px;
    color: #888;
}

/* ==================== Form Styling ==================== */
.register-form >>> .el-form-item {
    margin-bottom: 16px;
}

.register-form >>> .el-form-item__label {
    font-weight: 600;
    color: #555;
    font-size: 13px;
}

.register-form >>> .el-input__prefix {
    color: #28b2d4;
}

.register-form >>> .el-input__inner {
    border-radius: 8px;
    border: 1px solid #e0e5ec;
    transition: all 0.3s;
    height: 40px;
    line-height: 40px;
}

.register-form >>> .el-input__inner:focus {
    border-color: #28b2d4;
    box-shadow: 0 0 0 3px rgba(40,178,212,0.1);
}

/* ==================== Captcha ==================== */
.captcha-row {
    display: flex;
    gap: 8px;
    align-items: center;
}

.captcha-input {
    flex: 1;
}

.send-code-btn {
    height: 40px;
    background: linear-gradient(135deg, #28b2d4, #1a8da8);
    color: white;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    font-size: 13px;
    padding: 0 16px;
    white-space: nowrap;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(40,178,212,0.3);
}

.send-code-btn:hover {
    box-shadow: 0 4px 14px rgba(40,178,212,0.45);
    transform: translateY(-1px);
}

/* ==================== Register Button ==================== */
.form-actions {
    margin-top: 12px;
}

.register-btn {
    width: 100%;
    height: 46px;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 2px;
    border-radius: 10px;
    background: linear-gradient(135deg, #28b2d4, #1a8da8) !important;
    border: none !important;
    box-shadow: 0 4px 16px rgba(40,178,212,0.4);
    transition: all 0.3s;
}

.register-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(40,178,212,0.55);
}

.register-btn:active {
    transform: translateY(0);
}

/* ==================== Form Footer ==================== */
.form-footer {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: #999;
}

.login-link {
    color: #28b2d4;
    font-weight: 700;
    margin-left: 4px;
    transition: all 0.3s;
}

.login-link:hover {
    color: #1a8da8;
}

.login-link i {
    font-size: 12px;
    transition: transform 0.3s;
}

.login-link:hover i {
    transform: translateX(3px);
}

/* ==================== Responsive ==================== */
@media (max-width: 1050px) {
    .register-container {
        width: 92%;
        flex-direction: column;
        min-height: auto;
    }

    .register-brand {
        padding: 30px 30px;
    }

    .brand-features {
        display: none;
    }

    .brand-title {
        font-size: 24px;
    }

    .brand-icon {
        width: 56px;
        height: 56px;
        font-size: 28px;
        margin-bottom: 14px;
    }

    .brand-subtitle {
        margin-bottom: 0;
    }

    .register-form-panel {
        padding: 30px 28px;
    }
}
</style>
