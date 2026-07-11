<template>
    <div class="ai-chat-widget">
        <!-- 悬浮气泡按钮 -->
        <transition name="bubble">
            <div
                v-show="!isOpen"
                class="float-bubble"
                @click="toggleChat"
            >
                <div class="bubble-glow"></div>
                <div class="bubble-ring"></div>
                <div class="bubble-icon">
                    <svg viewBox="0 0 64 64" class="robot-svg big">
                        <circle cx="32" cy="34" r="24" fill="#ffffff" fill-opacity="0.15"/>
                        <circle cx="32" cy="34" r="20" fill="#ffffff" fill-opacity="0.25"/>
                        <rect x="20" y="22" width="24" height="20" rx="10" fill="#ffffff"/>
                        <circle cx="26" cy="30" r="3" fill="#667eea"/>
                        <circle cx="38" cy="30" r="3" fill="#667eea"/>
                        <circle cx="26" cy="30" r="1.2" fill="#ffffff"/>
                        <circle cx="38" cy="30" r="1.2" fill="#ffffff"/>
                        <path d="M27 38 Q32 42 37 38" stroke="#667eea" stroke-width="2.5" fill="none" stroke-linecap="round"/>
                        <line x1="32" y1="10" x2="32" y2="16" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round"/>
                        <circle cx="32" cy="8" r="3.5" fill="#f093fb"/>
                        <line x1="18" y1="34" x2="14" y2="34" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round"/>
                        <line x1="46" y1="34" x2="50" y2="34" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round"/>
                        <circle cx="14" cy="34" r="3" fill="#ffffff"/>
                        <circle cx="50" cy="34" r="3" fill="#ffffff"/>
                    </svg>
                </div>
                <div class="bubble-pulse"></div>
                <span class="bubble-label">AI 助手</span>
            </div>
        </transition>

        <!-- 聊天弹窗 -->
        <transition name="panel">
            <div v-show="isOpen" class="chat-panel">
                <!-- 面板头部 -->
                <div class="panel-header">
                    <div class="header-left">
                        <div class="avatar-circle">
                            <svg viewBox="0 0 64 64" class="robot-svg medium">
                                <circle cx="32" cy="34" r="24" fill="#ffffff" fill-opacity="0.2"/>
                                <rect x="20" y="22" width="24" height="20" rx="10" fill="#ffffff"/>
                                <circle cx="26" cy="30" r="3" fill="#667eea"/>
                                <circle cx="38" cy="30" r="3" fill="#667eea"/>
                                <circle cx="26" cy="30" r="1.2" fill="#ffffff"/>
                                <circle cx="38" cy="30" r="1.2" fill="#ffffff"/>
                                <path d="M27 38 Q32 42 37 38" stroke="#667eea" stroke-width="2.5" fill="none" stroke-linecap="round"/>
                                <line x1="32" y1="10" x2="32" y2="16" stroke="#ffffff" stroke-width="2.5" stroke-linecap="round"/>
                                <circle cx="32" cy="8" r="3.5" fill="#f093fb"/>
                            </svg>
                            <span class="online-dot"></span>
                        </div>
                        <div class="header-info">
                            <div class="header-name">智能助手</div>
                            <div class="header-status">
                                <span class="status-indicator"></span>
                                在线为您服务
                            </div>
                        </div>
                    </div>
                    <div class="header-actions">
                        <div class="action-btn" @click="toggleChat">
                            <svg viewBox="0 0 24 24" class="action-icon">
                                <line x1="6" y1="12" x2="18" y2="12" stroke="#ffffff" stroke-width="3" stroke-linecap="round"/>
                            </svg>
                        </div>
                        <div class="action-btn close-btn" @click="closeChat">
                            <svg viewBox="0 0 24 24" class="action-icon">
                                <line x1="7" y1="7" x2="17" y2="17" stroke="#ffffff" stroke-width="3" stroke-linecap="round"/>
                                <line x1="17" y1="7" x2="7" y2="17" stroke="#ffffff" stroke-width="3" stroke-linecap="round"/>
                            </svg>
                        </div>
                    </div>
                </div>

                <!-- 消息区 -->
                <div class="chat-messages" ref="msgBody">
                    <div v-if="messages.length === 0" class="welcome-area">
                        <div class="welcome-icon">
                            <i class="el-icon-chat-dot-round"></i>
                        </div>
                        <div class="welcome-title">您好！</div>
                        <div class="welcome-subtitle">有什么可以帮您解答的？</div>
                        <div class="quick-list">
                            <div
                                v-for="(q, idx) in quickQuestions"
                                :key="idx"
                                class="quick-item"
                                @click="sendQuickQuestion(q.Text)"
                            >
                                <span class="quick-icon">{{ q.Icon }}</span>
                                <span class="quick-label">{{ q.Text }}</span>
                                <i class="el-icon-arrow-right quick-arrow"></i>
                            </div>
                        </div>
                    </div>

                    <transition-group name="msg-fade" tag="div" class="message-list">
                        <div
                            v-for="(msg, idx) in messages"
                            :key="msg.id || idx"
                            class="msg-row"
                            :class="{ 'msg-right': msg.role === 'user', 'msg-left': msg.role !== 'user' }"
                        >
                            <div class="msg-avatar" :class="{ 'user-av': msg.role === 'user' }">
                                <i v-if="msg.role === 'user'" class="el-icon-user-solid"></i>
                                <svg v-else viewBox="0 0 64 64" class="robot-svg small">
                                    <rect x="18" y="20" width="28" height="24" rx="8" fill="#ffffff"/>
                                    <circle cx="27" cy="30" r="2.5" fill="#667eea"/>
                                    <circle cx="37" cy="30" r="2.5" fill="#667eea"/>
                                    <path d="M26 37 Q32 41 38 37" stroke="#667eea" stroke-width="2" fill="none" stroke-linecap="round"/>
                                    <line x1="32" y1="12" x2="32" y2="18" stroke="#f093fb" stroke-width="2" stroke-linecap="round"/>
                                    <circle cx="32" cy="10" r="2.5" fill="#f093fb"/>
                                </svg>
                            </div>
                            <div class="msg-bubble" :class="{ 'bubble-user': msg.role === 'user' }">
                                <div class="msg-content">
                                    <span
                                        v-for="(part, pIdx) in parseRichText(msg.isTyping ? msg.displayContent : msg.content)"
                                        :key="pIdx"
                                    >
                                        <br v-if="part.newline" />
                                        <span v-else :class="{ 'bold-text': part.bold }">{{ part.text }}</span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div v-if="loading" class="msg-row msg-left" key="thinking">
                            <div class="msg-avatar">
                                <svg viewBox="0 0 64 64" class="robot-svg small">
                                    <rect x="18" y="20" width="28" height="24" rx="8" fill="#ffffff"/>
                                    <circle cx="27" cy="30" r="2.5" fill="#667eea"/>
                                    <circle cx="37" cy="30" r="2.5" fill="#667eea"/>
                                    <path d="M26 37 Q32 41 38 37" stroke="#667eea" stroke-width="2" fill="none" stroke-linecap="round"/>
                                    <line x1="32" y1="12" x2="32" y2="18" stroke="#f093fb" stroke-width="2" stroke-linecap="round"/>
                                    <circle cx="32" cy="10" r="2.5" fill="#f093fb"/>
                                </svg>
                            </div>
                            <div class="msg-bubble thinking">
                                <span class="think-dots">
                                    <span></span><span></span><span></span>
                                </span>
                            </div>
                        </div>
                    </transition-group>
                </div>

                <!-- 输入区 -->
                <div class="chat-input-area">
                    <textarea
                        v-model="inputMessage"
                        @keydown.enter.prevent="sendMessage"
                        :disabled="loading"
                        class="input-textarea"
                        placeholder="输入问题... (Enter发送, Shift+Enter换行)"
                        rows="2"
                    ></textarea>
                    <button
                        class="send-button"
                        :disabled="loading || !inputMessage.trim()"
                        @click="sendMessage"
                    >
                        <i v-if="!loading" class="el-icon-s-promotion"></i>
                        <i v-else class="el-icon-loading"></i>
                    </button>
                </div>

                <!-- 底部装饰 -->
                <div class="panel-deco"></div>
            </div>
        </transition>

        <!-- 遮罩（仅移动端显示） -->
        <transition name="fade">
            <div v-show="isOpen && isMobile" class="chat-mask" @click="toggleChat"></div>
        </transition>
    </div>
</template>

<script>
export default {
    name: 'AiChatWidget',
    data() {
        return {
            isOpen: false,
            messages: [],
            inputMessage: '',
            loading: false,
            isMobile: window.innerWidth < 768,
            quickQuestions: [
                { Icon: '📚', Text: '如何预约自习室？' },
                { Icon: '🏠', Text: '有哪些自习室？' },
                { Icon: '⭐', Text: '积分规则是什么？' },
                { Icon: '❌', Text: '如何取消预约？' }
            ]
        }
    },
    mounted() {
        this.loadHistory();
        window.addEventListener('resize', this.handleResize);
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.handleResize);
    },
    methods: {
        handleResize() {
            this.isMobile = window.innerWidth < 768;
        },
        toggleChat() {
            this.isOpen = !this.isOpen;
        },
        closeChat() {
            this.isOpen = false;
        },
        async sendMessage() {
            if (!this.inputMessage.trim() || this.loading) return;
            const userMsg = this.inputMessage.trim();
            this.inputMessage = '';

            const userObj = {
                id: Date.now(),
                role: 'user',
                content: userMsg,
                displayContent: userMsg,
                time: new Date(),
                isTyping: false
            };
            this.messages.push(userObj);
            this.saveHistory();
            this.scrollBottom();

            this.loading = true;
            try {
                const history = this.messages.slice(-10).filter(m => true).map(m => ({
                    Role: m.role,
                    Content: m.content
                }));
                history.pop();

                const { Data: response } = await this.$Post('/AI/Chat', {
                    Message: userMsg,
                    History: history,
                    UserId: this.$store.getters.UserId || null
                });

                const aiMsg = {
                    id: Date.now() + 1,
                    role: 'assistant',
                    content: response.Content,
                    displayContent: '',
                    time: new Date(),
                    isTyping: true
                };
                this.messages.push(aiMsg);
                this.loading = false;
                this.scrollBottom();

                this.typewriterEffect(aiMsg, response.Content);
            } catch (err) {
                this.loading = false;
                this.messages.push({
                    id: Date.now(),
                    role: 'assistant',
                    content: '抱歉，暂时无法回答您的问题，请稍后再试。',
                    displayContent: '抱歉，暂时无法回答您的问题，请稍后再试。',
                    time: new Date(),
                    isTyping: false
                });
                this.scrollBottom();
            }
        },
        sendQuickQuestion(q) {
            this.inputMessage = q;
            this.sendMessage();
        },
        typewriterEffect(msg, fullText) {
            let i = 0;
            const speed = 20;
            const timer = setInterval(() => {
                if (i < fullText.length) {
                    msg.displayContent = fullText.substring(0, i + 1);
                    i++;
                    this.scrollBottom();
                } else {
                    clearInterval(timer);
                    msg.isTyping = false;
                    msg.displayContent = fullText;
                    this.saveHistory();
                }
            }, speed);
        },
        parseRichText(content) {
            if (!content) return [];
            const result = [];
            const lines = content.split('\n');
            lines.forEach((line, li) => {
                const parts = line.split(/(\*\*[^*]+\*\*)/g);
                parts.forEach(part => {
                    if (part.startsWith('**') && part.endsWith('**')) {
                        result.push({ text: part.slice(2, -2), bold: true, newline: false });
                    } else {
                        result.push({ text: part, bold: false, newline: false });
                    }
                });
                if (li < lines.length - 1) {
                    result.push({ text: '', bold: false, newline: true });
                }
            });
            return result;
        },
        scrollBottom() {
            this.$nextTick(() => {
                const body = this.$refs.msgBody;
                if (body) body.scrollTop = body.scrollHeight;
            });
        },
        saveHistory() {
            try {
                const data = this.messages.map(m => ({
                    role: m.role,
                    content: m.content,
                    time: m.time
                }));
                localStorage.setItem('ai_widget_history', JSON.stringify(data));
            } catch (e) {}
        },
        loadHistory() {
            try {
                const saved = localStorage.getItem('ai_widget_history');
                if (saved) {
                    const parsed = JSON.parse(saved);
                    this.messages = parsed.map(m => ({
                        ...m,
                        id: Math.random().toString(36).slice(2),
                        displayContent: m.content,
                        isTyping: false
                    }));
                }
            } catch (e) {}
        }
    }
}
</script>

<style scoped>
.ai-chat-widget {
    position: fixed;
    right: 32px;
    bottom: 32px;
    z-index: 2000;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', sans-serif;
}

/* ========== 悬浮气泡 ========== */
.float-bubble {
    position: relative;
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 28px rgba(102, 126, 234, 0.5), 0 0 0 4px rgba(255, 255, 255, 0.8);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s ease;
}

.float-bubble:hover {
    transform: scale(1.1) rotate(-5deg);
    box-shadow: 0 12px 36px rgba(102, 126, 234, 0.6), 0 0 0 6px rgba(255, 255, 255, 0.6);
}

.float-bubble:active {
    transform: scale(0.95);
}

.bubble-glow {
    position: absolute;
    top: -8px;
    left: -8px;
    right: -8px;
    bottom: -8px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
    animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
    0%, 100% { transform: scale(1); opacity: 0.5; }
    50% { transform: scale(1.2); opacity: 0.8; }
}

.bubble-ring {
    position: absolute;
    top: -4px;
    left: -4px;
    right: -4px;
    bottom: -4px;
    border-radius: 50%;
    border: 2px solid rgba(102, 126, 234, 0.3);
    animation: ringPulse 2s ease-out infinite;
}

@keyframes ringPulse {
    0% { transform: scale(1); opacity: 1; }
    100% { transform: scale(1.5); opacity: 0; }
}

.bubble-icon {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    z-index: 2;
    animation: iconFloat 3s ease-in-out infinite;
}

.robot-svg {
    width: 100%;
    height: 100%;
    display: block;
}

.robot-svg.big {
    width: 40px;
    height: 40px;
}

.robot-svg.medium {
    width: 28px;
    height: 28px;
}

.robot-svg.small {
    width: 20px;
    height: 20px;
}

@keyframes iconFloat {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-3px); }
}

.bubble-pulse {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 12px;
    height: 12px;
    background: #52c41a;
    border-radius: 50%;
    border: 2px solid white;
    z-index: 3;
    animation: statusPulse 2s ease-in-out infinite;
    box-shadow: 0 0 8px #52c41a;
}

@keyframes statusPulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

.bubble-label {
    position: absolute;
    right: 76px;
    top: 50%;
    transform: translateY(-50%);
    background: white;
    padding: 8px 14px;
    border-radius: 12px;
    font-size: 13px;
    color: #333;
    white-space: nowrap;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    opacity: 0;
    pointer-events: none;
    transition: all 0.3s ease;
    font-weight: 500;
}

.bubble-label::after {
    content: '';
    position: absolute;
    right: -6px;
    top: 50%;
    transform: translateY(-50%);
    border: 6px solid transparent;
    border-left-color: white;
}

.float-bubble:hover .bubble-label {
    opacity: 1;
    transform: translateY(-50%) translateX(-8px);
}

/* ========== 过渡动画 ========== */
.bubble-enter-active, .bubble-leave-active {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.bubble-enter, .bubble-leave-to {
    opacity: 0;
    transform: scale(0.5) rotate(45deg);
}

.panel-enter-active {
    transition: all 0.35s cubic-bezier(0.17, 0.67, 0.3, 1.33);
}
.panel-leave-active {
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.panel-enter, .panel-leave-to {
    opacity: 0;
    transform: translateY(20px) scale(0.9);
    transform-origin: bottom right;
}

.fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
    opacity: 0;
}

/* ========== 聊天面板 ========== */
.chat-panel {
    width: 380px;
    height: 560px;
    background: white;
    border-radius: 20px;
    box-shadow:
        0 20px 60px rgba(102, 126, 234, 0.3),
        0 0 0 1px rgba(255, 255, 255, 0.9),
        0 8px 24px rgba(0, 0, 0, 0.12);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;
    margin-bottom: 12px;
}

.panel-header {
    padding: 18px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: white;
    flex-shrink: 0;
    position: relative;
    z-index: 2;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.avatar-circle {
    width: 44px;
    height: 44px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    position: relative;
    backdrop-filter: blur(10px);
}

.online-dot {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 12px;
    height: 12px;
    background: #52c41a;
    border-radius: 50%;
    border: 2px solid #764ba2;
    box-shadow: 0 0 6px rgba(82, 196, 26, 0.8);
}

.header-info .header-name {
    font-size: 16px;
    font-weight: 600;
    line-height: 1.2;
}

.header-info .header-status {
    font-size: 11px;
    opacity: 0.9;
    display: flex;
    align-items: center;
    gap: 5px;
    margin-top: 4px;
}

.status-indicator {
    width: 6px;
    height: 6px;
    background: #52c41a;
    border-radius: 50%;
    animation: statusPulse 2s ease-in-out infinite;
}

.header-actions {
    display: flex;
    gap: 8px;
}

.action-btn {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.25s ease;
    border: 1px solid rgba(255, 255, 255, 0.25);
}

.action-btn:hover {
    background: rgba(255, 255, 255, 0.35);
    transform: scale(1.08);
}

.action-btn:active {
    transform: scale(0.95);
}

.close-btn:hover {
    background: rgba(255, 77, 79, 0.4) !important;
    border-color: rgba(255, 120, 120, 0.5);
}

.action-icon {
    width: 20px;
    height: 20px;
    display: block;
    color: white;
}

/* ========== 消息区 ========== */
.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px 16px 16px;
    background: #f8f9fc;
    position: relative;
}

.chat-messages::-webkit-scrollbar {
    width: 5px;
}
.chat-messages::-webkit-scrollbar-track {
    background: transparent;
}
.chat-messages::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.3);
    border-radius: 3px;
}

.welcome-area {
    text-align: center;
    padding: 20px 10px;
    animation: fadeUp 0.5s ease;
}

@keyframes fadeUp {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

.welcome-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 32px;
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
    animation: iconFloat 3s ease-in-out infinite;
}

.welcome-title {
    font-size: 17px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
}

.welcome-subtitle {
    font-size: 13px;
    color: #888;
    margin-bottom: 18px;
}

.quick-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-top: 16px;
}

.quick-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 14px;
    background: white;
    border: 1px solid rgba(102, 126, 234, 0.15);
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.25s ease;
    font-size: 13px;
    color: #555;
    text-align: left;
}

.quick-item:hover {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    transform: translateX(4px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.quick-icon {
    font-size: 18px;
    flex-shrink: 0;
}

.quick-label {
    flex: 1;
}

.quick-arrow {
    color: #667eea;
    opacity: 0;
    transform: translateX(-8px);
    transition: all 0.25s ease;
    font-size: 12px;
}

.quick-item:hover .quick-arrow {
    opacity: 1;
    transform: translateX(0);
}

.message-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.msg-row {
    display: flex;
    align-items: flex-end;
    gap: 8px;
}

.msg-right {
    justify-content: flex-end;
}

.msg-avatar {
    width: 32px;
    height: 32px;
    border-radius: 10px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 16px;
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.25);
}

.user-av {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    box-shadow: 0 2px 8px rgba(245, 87, 108, 0.25);
}

.msg-bubble {
    max-width: 250px;
    padding: 10px 14px;
    border-radius: 14px;
    font-size: 13px;
    line-height: 1.7;
    background: white;
    color: #333;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    word-break: break-word;
}

.msg-left .msg-bubble {
    border-bottom-left-radius: 4px;
}

.bubble-user {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-bottom-right-radius: 4px;
}

.msg-content {
    white-space: pre-wrap;
}

.bold-text {
    font-weight: 600;
    color: #667eea;
}

.bubble-user .bold-text {
    color: #ffe4ec;
}

.msg-fade-enter-active, .msg-fade-leave-active {
    transition: all 0.3s ease;
}
.msg-fade-enter {
    opacity: 0;
    transform: translateY(10px);
}
.msg-fade-leave-to {
    opacity: 0;
}

.thinking {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    padding: 14px 18px;
}

.think-dots {
    display: flex;
    gap: 4px;
    padding: 4px 0;
}

.think-dots span {
    width: 8px;
    height: 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50%;
    animation: bounceDot 1.4s ease-in-out infinite;
}

.think-dots span:nth-child(2) { animation-delay: 0.2s; }
.think-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounceDot {
    0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
    40% { transform: scale(1.2); opacity: 1; }
}

/* ========== 输入区 ========== */
.chat-input-area {
    padding: 14px 16px;
    background: white;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
    display: flex;
    gap: 10px;
    align-items: flex-end;
    flex-shrink: 0;
}

.input-textarea {
    flex: 1;
    min-height: 40px;
    max-height: 80px;
    padding: 10px 12px;
    border: 1.5px solid rgba(102, 126, 234, 0.15);
    border-radius: 12px;
    font-size: 13px;
    line-height: 1.6;
    resize: none;
    outline: none;
    transition: all 0.2s ease;
    font-family: inherit;
    background: #fafbfe;
}

.input-textarea:focus {
    border-color: #667eea;
    background: white;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-textarea::placeholder {
    color: #aaa;
}

.input-textarea:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.send-button {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    border: none;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-size: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.send-button:active:not(:disabled) {
    transform: translateY(0);
}

.send-button:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    box-shadow: none;
}

/* ========== 底部装饰 ========== */
.panel-deco {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    z-index: 1;
}

/* ========== 遮罩 ========== */
.chat-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: -1;
    backdrop-filter: blur(2px);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
    .ai-chat-widget {
        right: 16px;
        bottom: 16px;
    }

    .float-bubble {
        width: 56px;
        height: 56px;
    }

    .bubble-icon {
        font-size: 24px;
    }

    .bubble-label {
        display: none;
    }

    .chat-panel {
        position: fixed;
        left: 16px;
        right: 16px;
        bottom: 16px;
        top: auto;
        width: auto;
        height: 70vh;
        max-height: 560px;
    }

    .msg-bubble {
        max-width: 200px;
    }
}

@media (max-width: 400px) {
    .chat-panel {
        left: 8px;
        right: 8px;
        bottom: 8px;
        height: 75vh;
    }

    .msg-bubble {
        max-width: 180px;
        font-size: 12px;
    }

    .quick-item {
        font-size: 12px;
    }
}
</style>