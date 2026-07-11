<template>
    <div class="ai-page">
        <div class="ai-hero">
            <div class="hero-bg"></div>
            <div class="hero-content">
                <div class="hero-badge">
                    <span class="badge-dot"></span>
                    AI 智能助手 · 实时在线
                </div>
                <h1 class="hero-title">
                    您好<span class="wave">👋</span>，我是您的智能伙伴
                </h1>
                <p class="hero-subtitle">可以问我关于自习室预约、系统使用、学习建议的任何问题</p>
            </div>
        </div>

        <div class="ai-content">
            <div class="chat-card" :class="{ 'chat-active': messages.length > 0 }">
                <div class="chat-card-header">
                    <div class="header-left">
                        <div class="header-icon">
                            <i class="el-icon-cpu"></i>
                            <span class="status-dot"></span>
                        </div>
                        <div class="header-text">
                            <div class="header-title">智能对话</div>
                            <div class="header-desc">基于 DeepSeek 大模型 · 支持上下文理解</div>
                        </div>
                    </div>
                    <div class="header-right">
                        <span class="message-count">
                            <i class="el-icon-chat-line-square"></i>
                            {{ messages.length }} 条消息
                        </span>
                        <el-button
                            size="mini"
                            icon="el-icon-delete"
                            type="text"
                            class="clear-btn"
                            @click="ClearHistory"
                        >清空对话</el-button>
                    </div>
                </div>

                <div class="chat-card-body" ref="chatBody">
                    <transition-group name="message-fade">
                        <div
                            v-for="(msg, idx) in messages"
                            :key="idx"
                            class="message-wrapper"
                            :class="{ 'user-message': msg.role === 'user', 'ai-message': msg.role === 'assistant' }"
                        >
                            <div class="message-avatar" :class="{ 'user-avatar': msg.role === 'user' }">
                                <i v-if="msg.role === 'user'" class="el-icon-user-solid"></i>
                                <i v-else class="el-icon-cpu"></i>
                            </div>
                            <div class="message-content">
                                <div class="message-bubble">
                                    <div class="message-text">
                                        <span
                                            v-for="(part, pIdx) in ParseRichText(msg.isTyping ? msg.displayContent : msg.content)"
                                            :key="pIdx"
                                        >
                                            <br v-if="part.newline" />
                                            <span v-else :class="{ 'highlight-text': part.bold }">{{ part.text }}</span>
                                        </span>
                                    </div>
                                </div>
                                <div class="message-meta">
                                    <span class="message-time">
                                        <i class="el-icon-time"></i>
                                        {{ FormatTime(msg.time) }}
                                    </span>
                                    <span
                                        v-if="msg.role === 'assistant' && !msg.isTyping"
                                        class="copy-btn"
                                        @click="CopyContent(msg.content)"
                                    >
                                        <i class="el-icon-document-copy"></i>复制
                                    </span>
                                </div>
                            </div>
                        </div>
                    </transition-group>

                    <div v-if="loading" class="message-wrapper ai-message" key="loading">
                        <div class="message-avatar">
                            <i class="el-icon-cpu"></i>
                        </div>
                        <div class="message-content">
                            <div class="message-bubble thinking-bubble">
                                <div class="thinking-text">正在思考</div>
                                <div class="thinking-dots">
                                    <span></span><span></span><span></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div v-if="messages.length === 0 && !loading" class="welcome-panel">
                    <div class="welcome-icon">
                        <i class="el-icon-s-custom"></i>
                    </div>
                    <h3 class="welcome-title">开始新的对话</h3>
                    <p class="welcome-desc">选择下方快捷问题，或在输入框中自由提问</p>
                    <div class="quick-cards">
                        <div
                            v-for="(q, idx) in quickQuestions"
                            :key="idx"
                            class="quick-card"
                            :style="{ animationDelay: (idx * 0.1) + 's' }"
                            @click="SendQuickQuestion(q.Text)"
                        >
                            <div class="quick-icon">{{ q.Icon }}</div>
                            <div class="quick-text">{{ q.Text }}</div>
                            <i class="el-icon-arrow-right quick-arrow"></i>
                        </div>
                    </div>
                </div>

                <div class="chat-card-footer">
                    <div class="input-wrapper">
                        <el-input
                            v-model="inputMessage"
                            type="textarea"
                            :rows="2"
                            maxlength="1000"
                            placeholder="输入您的问题，按 Enter 发送，Shift + Enter 换行"
                            :disabled="loading"
                            @keydown.native="HandleKeydown"
                            resize="none"
                            class="chat-input"
                        />
                        <el-button
                            type="primary"
                            :loading="loading"
                            :disabled="!inputMessage.trim()"
                            class="send-btn"
                            @click="SendMessage"
                        >
                            <i class="el-icon-s-promotion"></i>
                            <span>发送</span>
                        </el-button>
                    </div>
                    <div class="footer-tips">
                        <span class="tip-item"><i class="el-icon-info"></i>AI 生成的内容仅供参考</span>
                        <span class="tip-item"><i class="el-icon-lock"></i>对话记录仅保存在本地</span>
                    </div>
                </div>
            </div>

            <div class="side-info">
                <div class="info-card">
                    <div class="info-header">
                        <i class="el-icon-s-order"></i>
                        <span>功能特点</span>
                    </div>
                    <ul class="feature-list">
                        <li><span class="feature-icon">🎯</span>精准解答预约问题</li>
                        <li><span class="feature-icon">🧠</span>上下文多轮对话</li>
                        <li><span class="feature-icon">⚡</span>实时响应</li>
                        <li><span class="feature-icon">💡</span>学习建议推荐</li>
                        <li><span class="feature-icon">📝</span>本地历史记录</li>
                    </ul>
                </div>

                <div class="info-card">
                    <div class="info-header">
                        <i class="el-icon-edit-outline"></i>
                        <span>提问技巧</span>
                    </div>
                    <ul class="tip-list">
                        <li>✓ 问题描述越详细，回答越精准</li>
                        <li>✓ 可以连续追问深入了解</li>
                        <li>✓ 试试问学习计划建议</li>
                        <li>✓ 不确定时可以多试几个问法</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            messages: [],
            inputMessage: '',
            loading: false,
            quickQuestions: [
                { Icon: '📚', Text: '如何预约自习室？' },
                { Icon: '🏠', Text: '有哪些自习室可以选择？' },
                { Icon: '❌', Text: '如何取消预约？' },
                { Icon: '⭐', Text: '积分规则是什么？' },
                { Icon: '🎓', Text: '推荐一个学习计划' },
                { Icon: '💬', Text: '忘记密码怎么办？' }
            ]
        }
    },
    created() {
        this.LoadHistory();
    },
    updated() {
        this.ScrollToBottom();
    },
    methods: {
        async SendMessage() {
            if (!this.inputMessage.trim() || this.loading) return;

            const userMessage = this.inputMessage.trim();
            this.inputMessage = '';

            this.messages.push({
                role: 'user',
                content: userMessage,
                displayContent: userMessage,
                time: new Date(),
                isTyping: false
            });

            this.SaveHistory();

            this.loading = true;
            try {
                const history = this.messages.slice(-10).filter(m => m.role !== 'assistant' || true).map(m => ({
                    Role: m.role,
                    Content: m.content
                }));

                history.pop();

                let { Data: response } = await this.$Post('/AI/Chat', {
                    Message: userMessage,
                    History: history
                });

                const aiMsg = {
                    role: 'assistant',
                    content: response.Content,
                    displayContent: '',
                    time: new Date(),
                    isTyping: true
                };
                this.messages.push(aiMsg);

                this.TypewriterEffect(aiMsg, response.Content);
                this.loading = false;

            } catch (error) {
                this.$message.error('AI服务暂时不可用，请稍后再试');
                this.messages.push({
                    role: 'assistant',
                    content: '抱歉，我暂时无法回答您的问题，请稍后再试。',
                    displayContent: '抱歉，我暂时无法回答您的问题，请稍后再试。',
                    time: new Date(),
                    isTyping: false
                });
                this.loading = false;
            }
        },

        TypewriterEffect(msg, fullContent) {
            let index = 0;
            const speed = 15;
            const timer = setInterval(() => {
                if (index < fullContent.length) {
                    msg.displayContent = fullContent.substring(0, index + 1);
                    index++;
                    this.ScrollToBottom();
                } else {
                    clearInterval(timer);
                    msg.isTyping = false;
                    msg.displayContent = fullContent;
                    this.SaveHistory();
                }
            }, speed);
        },

        SendQuickQuestion(question) {
            this.inputMessage = question;
            this.SendMessage();
        },

        HandleKeydown(event) {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                this.SendMessage();
            }
        },

        ClearHistory() {
            this.$confirm('确定要清空所有对话记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
                customClass: 'ai-confirm'
            }).then(() => {
                this.messages = [];
                localStorage.removeItem('ai_chat_history');
                this.$message.success('对话已清空');
            }).catch(() => {});
        },

        CopyContent(content) {
            const textarea = document.createElement('textarea');
            textarea.value = content;
            document.body.appendChild(textarea);
            textarea.select();
            document.execCommand('copy');
            document.body.removeChild(textarea);
            this.$message.success('已复制到剪贴板');
        },

        SaveHistory() {
            try {
                const toSave = this.messages.map(m => ({
                    role: m.role,
                    content: m.content,
                    time: m.time
                }));
                localStorage.setItem('ai_chat_history', JSON.stringify(toSave));
            } catch (e) {}
        },

        LoadHistory() {
            try {
                const saved = localStorage.getItem('ai_chat_history');
                if (saved) {
                    const parsed = JSON.parse(saved);
                    this.messages = parsed.map(m => ({
                        ...m,
                        displayContent: m.content,
                        isTyping: false
                    }));
                }
            } catch (e) {}
        },

        ParseRichText(content) {
            if (!content) return [];
            const result = [];
            // 处理 \n
            const lines = content.split('\n');
            lines.forEach((line, lineIdx) => {
                // 处理 **加粗**
                const parts = line.split(/(\*\*[^*]+\*\*)/g);
                parts.forEach((part, partIdx) => {
                    if (part.startsWith('**') && part.endsWith('**')) {
                        result.push({ text: part.slice(2, -2), bold: true });
                    } else {
                        result.push({ text: part, bold: false });
                    }
                });
                if (lineIdx < lines.length - 1) {
                    result.push({ text: '\n', bold: false, newline: true });
                }
            });
            return result.filter(p => p.text.length > 0 || p.newline);
        },

        FormatTime(time) {
            if (!time) return '';
            const t = new Date(time);
            const now = new Date();
            const diff = now - t;

            if (diff < 60000) return '刚刚';
            if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';

            return String(t.getHours()).padStart(2, '0') + ':' +
                String(t.getMinutes()).padStart(2, '0');
        },

        ScrollToBottom() {
            this.$nextTick(() => {
                const body = this.$refs.chatBody;
                if (body) {
                    body.scrollTop = body.scrollHeight;
                }
            });
        }
    }
}
</script>

<style scoped>
.ai-page {
    min-height: calc(100vh - 120px);
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    padding: 0;
    position: relative;
    overflow: hidden;
}

.ai-hero {
    padding: 60px 40px 100px;
    text-align: center;
    position: relative;
    color: white;
}

.hero-bg {
    position: absolute;
    top: -50%;
    left: -10%;
    width: 120%;
    height: 200%;
    background:
        radial-gradient(circle at 20% 30%, rgba(255,255,255,0.15) 0%, transparent 40%),
        radial-gradient(circle at 80% 70%, rgba(255,255,255,0.1) 0%, transparent 40%);
    pointer-events: none;
    animation: heroFloat 20s ease-in-out infinite;
}

@keyframes heroFloat {
    0%, 100% { transform: translateY(0) rotate(0deg); }
    50% { transform: translateY(-20px) rotate(2deg); }
}

.hero-content {
    position: relative;
    z-index: 2;
}

.hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    padding: 8px 20px;
    border-radius: 20px;
    font-size: 13px;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.badge-dot {
    width: 8px;
    height: 8px;
    background: #4ade80;
    border-radius: 50%;
    animation: pulse 2s ease-in-out infinite;
    box-shadow: 0 0 10px #4ade80;
}

@keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.6; transform: scale(1.2); }
}

.hero-title {
    font-size: 38px;
    font-weight: 700;
    margin: 20px 0 12px;
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.wave {
    display: inline-block;
    animation: wave 2s ease-in-out infinite;
}

@keyframes wave {
    0%, 100% { transform: rotate(0deg); }
    20% { transform: rotate(-15deg); }
    40% { transform: rotate(10deg); }
    60% { transform: rotate(-10deg); }
    80% { transform: rotate(15deg); }
}

.hero-subtitle {
    font-size: 16px;
    opacity: 0.95;
    max-width: 500px;
    margin: 0 auto;
    line-height: 1.6;
}

.ai-content {
    display: flex;
    gap: 24px;
    padding: 0 40px 60px;
    max-width: 1400px;
    margin: -60px auto 0;
    position: relative;
    z-index: 3;
}

.chat-card {
    flex: 1;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 24px;
    box-shadow:
        0 20px 60px rgba(102, 126, 234, 0.3),
        0 0 0 1px rgba(255, 255, 255, 0.5) inset;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 600px;
    transition: all 0.4s ease;
}

.chat-card:hover {
    box-shadow:
        0 30px 80px rgba(102, 126, 234, 0.35),
        0 0 0 1px rgba(255, 255, 255, 0.6) inset;
}

.chat-card-header {
    padding: 24px 32px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(240, 147, 251, 0.08) 100%);
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-icon {
    width: 52px;
    height: 52px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
    color: white;
    position: relative;
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.status-dot {
    position: absolute;
    right: 4px;
    bottom: 4px;
    width: 12px;
    height: 12px;
    background: #4ade80;
    border-radius: 50%;
    border: 3px solid white;
    box-shadow: 0 0 8px #4ade80;
}

.header-text .header-title {
    font-size: 17px;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 4px;
}

.header-text .header-desc {
    font-size: 12px;
    color: #8b8b9e;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.message-count {
    font-size: 13px;
    color: #8b8b9e;
    display: flex;
    align-items: center;
    gap: 6px;
}

.clear-btn {
    color: #999;
    font-size: 13px;
}

.clear-btn:hover {
    color: #f5576c;
}

.chat-card-body {
    flex: 1;
    padding: 32px;
    overflow-y: auto;
    background: transparent;
    max-height: 500px;
}

.chat-card-body::-webkit-scrollbar {
    width: 6px;
}

.chat-card-body::-webkit-scrollbar-track {
    background: transparent;
}

.chat-card-body::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.3);
    border-radius: 3px;
}

.chat-card-body::-webkit-scrollbar-thumb:hover {
    background: rgba(102, 126, 234, 0.5);
}

.welcome-panel {
    text-align: center;
    padding: 40px 20px;
    animation: fadeInUp 0.6s ease;
}

@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}

.welcome-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40px;
    color: white;
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.3);
    animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-8px); }
}

.welcome-title {
    font-size: 22px;
    font-weight: 600;
    color: #1a1a2e;
    margin: 0 0 8px;
}

.welcome-desc {
    font-size: 14px;
    color: #8b8b9e;
    margin: 0 0 32px;
}

.quick-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    max-width: 700px;
    margin: 0 auto;
}

.quick-card {
    padding: 18px 20px;
    background: white;
    border: 2px solid rgba(102, 126, 234, 0.15);
    border-radius: 16px;
    cursor: pointer;
    text-align: left;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    display: flex;
    align-items: center;
    gap: 12px;
    opacity: 0;
    animation: cardFadeIn 0.5s ease forwards;
}

@keyframes cardFadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

.quick-card:hover {
    border-color: #667eea;
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(102, 126, 234, 0.2);
}

.quick-icon {
    font-size: 24px;
    flex-shrink: 0;
}

.quick-text {
    flex: 1;
    font-size: 14px;
    color: #333;
    font-weight: 500;
}

.quick-arrow {
    color: #667eea;
    font-size: 14px;
    opacity: 0;
    transform: translateX(-8px);
    transition: all 0.3s ease;
}

.quick-card:hover .quick-arrow {
    opacity: 1;
    transform: translateX(0);
}

.message-wrapper {
    display: flex;
    margin-bottom: 28px;
    align-items: flex-start;
    gap: 14px;
}

.ai-message {
    flex-direction: row;
}

.user-message {
    flex-direction: row-reverse;
}

.message-fade-enter-active {
    transition: all 0.4s ease;
}

.message-fade-leave-active {
    transition: all 0.2s ease;
}

.message-fade-enter {
    opacity: 0;
    transform: translateY(20px);
}

.message-fade-leave-to {
    opacity: 0;
}

.message-avatar {
    width: 42px;
    height: 42px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    flex-shrink: 0;
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

.user-avatar {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    box-shadow: 0 6px 16px rgba(245, 87, 108, 0.3);
}

.message-content {
    max-width: 75%;
}

.user-message .message-content {
    text-align: right;
}

.message-bubble {
    padding: 16px 20px;
    border-radius: 18px;
    font-size: 14px;
    line-height: 1.8;
    word-wrap: break-word;
    display: inline-block;
    text-align: left;
    max-width: 100%;
}

.ai-message .message-bubble {
    background: white;
    border: 1px solid rgba(102, 126, 234, 0.15);
    color: #333;
    border-top-left-radius: 6px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.user-message .message-bubble {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-top-right-radius: 6px;
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}

.message-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-top: 8px;
    padding: 0 4px;
}

.user-message .message-meta {
    justify-content: flex-end;
}

.message-time {
    font-size: 11px;
    color: #bbb;
    display: flex;
    align-items: center;
    gap: 4px;
}

.copy-btn {
    font-size: 11px;
    color: #999;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: color 0.2s ease;
}

.copy-btn:hover {
    color: #667eea;
}

.thinking-bubble {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px 24px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(240, 147, 251, 0.08) 100%);
    border: 1px dashed rgba(102, 126, 234, 0.3);
}

.thinking-text {
    font-size: 14px;
    color: #667eea;
    font-weight: 500;
}

.thinking-dots {
    display: flex;
    gap: 4px;
}

.thinking-dots span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #667eea;
    animation: thinkingBounce 1.2s ease-in-out infinite;
}

.thinking-dots span:nth-child(2) {
    animation-delay: 0.15s;
}

.thinking-dots span:nth-child(3) {
    animation-delay: 0.3s;
}

@keyframes thinkingBounce {
    0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
    40% { transform: scale(1.2); opacity: 1; }
}

.chat-card-footer {
    padding: 20px 32px 24px;
    background: linear-gradient(180deg, transparent 0%, rgba(102, 126, 234, 0.03) 100%);
    border-top: 1px solid rgba(102, 126, 234, 0.08);
}

.input-wrapper {
    display: flex;
    gap: 12px;
    align-items: flex-end;
}

.chat-input {
    flex: 1;
}

.chat-input .el-textarea__inner {
    border-radius: 16px;
    padding: 14px 18px;
    border: 2px solid rgba(102, 126, 234, 0.15);
    font-size: 14px;
    line-height: 1.7;
    resize: none;
    transition: all 0.3s ease;
    background: white;
}

.chat-input .el-textarea__inner:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.chat-input .el-textarea__inner::placeholder {
    color: #bbb;
}

.message-text {
    font-size: 14px;
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-word;
}

.highlight-text {
    color: #667eea;
    font-weight: 600;
}

.user-message .message-bubble .highlight-text {
    color: #ffe4ec;
}

.send-btn {
    height: 56px;
    min-width: 100px;
    border-radius: 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
    transition: all 0.3s ease;
}

.send-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.45);
}

.send-btn:active {
    transform: translateY(0);
}

.footer-tips {
    margin-top: 12px;
    display: flex;
    justify-content: center;
    gap: 24px;
}

.tip-item {
    font-size: 11px;
    color: #aaa;
    display: flex;
    align-items: center;
    gap: 4px;
}

.side-info {
    width: 280px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.info-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 20px;
    padding: 24px;
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
}

.info-header {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 15px;
    font-weight: 600;
    color: #1a1a2e;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(102, 126, 234, 0.1);
}

.info-header i {
    color: #667eea;
    font-size: 18px;
}

.feature-list, .tip-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.feature-list li, .tip-list li {
    padding: 10px 0;
    font-size: 13px;
    color: #555;
    display: flex;
    align-items: center;
    gap: 10px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.05);
}

.feature-list li:last-child, .tip-list li:last-child {
    border-bottom: none;
}

.feature-icon {
    font-size: 16px;
}

@media (max-width: 1024px) {
    .ai-content {
        flex-direction: column;
    }

    .side-info {
        width: 100%;
        flex-direction: row;
    }

    .info-card {
        flex: 1;
    }
}

@media (max-width: 768px) {
    .ai-hero {
        padding: 40px 20px 80px;
    }

    .hero-title {
        font-size: 26px;
    }

    .ai-content {
        padding: 0 16px 40px;
    }

    .quick-cards {
        grid-template-columns: repeat(2, 1fr);
    }

    .chat-card-header {
        padding: 16px 20px;
        flex-wrap: wrap;
        gap: 12px;
    }

    .chat-card-body {
        padding: 20px;
    }

    .chat-card-footer {
        padding: 16px 20px 20px;
    }

    .message-content {
        max-width: 80%;
    }

    .side-info {
        flex-direction: column;
    }

    .footer-tips {
        flex-direction: column;
        gap: 6px;
        align-items: center;
    }
}
</style>