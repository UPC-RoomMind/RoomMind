<template>
    <div class="ai-assistant-container">
        <div class="chat-header">
            <div class="header-info">
                <div class="avatar">
                    <i class="el-icon-cpu"></i>
                </div>
                <div class="header-text">
                    <div class="title">AI 智能助手</div>
                    <div class="subtitle">基于 DeepSeek · 在线为您服务</div>
                </div>
            </div>
            <el-button size="small" icon="el-icon-delete" type="text" @click="ClearHistory">清空对话</el-button>
        </div>

        <div class="chat-body" ref="chatBody">
            <div v-if="messages.length === 0" class="empty-state">
                <div class="empty-icon">
                    <i class="el-icon-chat-dot-round"></i>
                </div>
                <div class="empty-title">您好！我是您的智能助手</div>
                <div class="empty-subtitle">可以问我关于自习室预约、系统使用的任何问题</div>
                <div class="quick-questions">
                    <el-button
                        v-for="(q, idx) in quickQuestions"
                        :key="idx"
                        size="small"
                        plain
                        @click="SendQuickQuestion(q)"
                    >
                        {{ q }}
                    </el-button>
                </div>
            </div>

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
                    <div class="message-role">{{ msg.role === 'user' ? '我' : 'AI助手' }}</div>
                    <div class="message-bubble">
                        <div v-if="msg.role === 'assistant' && msg.content && msg.content.includes('###') || (msg.content && msg.content.includes('**'))" class="markdown-text">
                            <pre>{{ msg.content }}</pre>
                        </div>
                        <div v-else class="plain-text">
                            <span v-for="(paragraph, pIdx) in ParseParagraphs(msg.content)" :key="pIdx">
                                {{ paragraph }}<br v-if="pIdx < ParseParagraphs(msg.content).length - 1" />
                            </span>
                        </div>
                    </div>
                    <div class="message-time">{{ FormatTime(msg.time) }}</div>
                </div>
            </div>

            <div v-if="loading" class="message-wrapper ai-message">
                <div class="message-avatar">
                    <i class="el-icon-cpu"></i>
                </div>
                <div class="message-content">
                    <div class="message-bubble thinking">
                        <span class="dot"></span>
                        <span class="dot"></span>
                        <span class="dot"></span>
                    </div>
                </div>
            </div>
        </div>

        <div class="chat-footer">
            <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="2"
                maxlength="1000"
                show-word-limit
                placeholder="请输入您的问题... (按 Enter 发送，Shift + Enter 换行)"
                :disabled="loading"
                @keydown.native="HandleKeydown"
                resize="none"
            />
            <el-button
                type="primary"
                :loading="loading"
                :disabled="!inputMessage.trim()"
                icon="el-icon-s-promotion"
                class="send-btn"
                @click="SendMessage"
            >
                发送
            </el-button>
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
                '如何预约自习室？',
                '有哪些自习室可以选择？',
                '如何取消预约？',
                '积分规则是什么？'
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
                time: new Date()
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

                this.messages.push({
                    role: 'assistant',
                    content: response.Content,
                    time: new Date()
                });

                this.SaveHistory();

            } catch (error) {
                this.$message.error('AI服务暂时不可用，请稍后再试');
                this.messages.push({
                    role: 'assistant',
                    content: '抱歉，我暂时无法回答您的问题，请稍后再试。',
                    time: new Date()
                });
            } finally {
                this.loading = false;
            }
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
                type: 'warning'
            }).then(() => {
                this.messages = [];
                localStorage.removeItem('ai_chat_history');
                this.$message.success('对话已清空');
            }).catch(() => {});
        },

        SaveHistory() {
            try {
                localStorage.setItem('ai_chat_history', JSON.stringify(this.messages));
            } catch (e) {}
        },

        LoadHistory() {
            try {
                const saved = localStorage.getItem('ai_chat_history');
                if (saved) {
                    this.messages = JSON.parse(saved);
                }
            } catch (e) {}
        },

        ParseParagraphs(content) {
            if (!content) return [''];
            return content.split('\n').map(p => p.trim()).filter(p => p.length > 0);
        },

        FormatTime(time) {
            if (!time) return '';
            const t = new Date(time);
            const now = new Date();
            const diff = now - t;

            if (diff < 60000) return '刚刚';
            if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';

            return t.getFullYear() + '-' +
                String(t.getMonth() + 1).padStart(2, '0') + '-' +
                String(t.getDate()).padStart(2, '0') + ' ' +
                String(t.getHours()).padStart(2, '0') + ':' +
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
.ai-assistant-container {
    width: 100%;
    height: calc(100vh - 120px);
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.chat-header {
    padding: 20px 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-info {
    display: flex;
    align-items: center;
    gap: 14px;
}

.avatar {
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
}

.header-text .title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 4px;
}

.header-text .subtitle {
    font-size: 12px;
    opacity: 0.9;
}

.chat-header .el-button {
    color: white;
    background: rgba(255, 255, 255, 0.2);
    border-color: transparent;
}

.chat-header .el-button:hover {
    background: rgba(255, 255, 255, 0.3);
}

.chat-body {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background: #f7f9fc;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
}

.empty-icon {
    font-size: 64px;
    color: #667eea;
    margin-bottom: 20px;
}

.empty-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.empty-subtitle {
    font-size: 14px;
    color: #999;
    margin-bottom: 30px;
}

.quick-questions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    justify-content: center;
    max-width: 600px;
    margin: 0 auto;
}

.quick-questions .el-button {
    margin: 5px;
    border-color: #667eea;
    color: #667eea;
}

.quick-questions .el-button:hover {
    background: #667eea;
    color: white;
}

.message-wrapper {
    display: flex;
    margin-bottom: 24px;
    align-items: flex-start;
    gap: 12px;
}

.ai-message {
    flex-direction: row;
}

.user-message {
    flex-direction: row-reverse;
}

.message-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    flex-shrink: 0;
}

.user-avatar {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.message-content {
    max-width: 70%;
}

.user-message .message-content {
    text-align: right;
}

.message-role {
    font-size: 12px;
    color: #999;
    margin-bottom: 6px;
}

.message-bubble {
    padding: 14px 18px;
    border-radius: 12px;
    font-size: 14px;
    line-height: 1.7;
    word-wrap: break-word;
    white-space: pre-wrap;
    display: inline-block;
    text-align: left;
}

.ai-message .message-bubble {
    background: white;
    border: 1px solid #e8e8e8;
    color: #333;
    border-top-left-radius: 4px;
}

.user-message .message-bubble {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-top-right-radius: 4px;
}

.plain-text {
    font-size: 14px;
    line-height: 1.7;
}

.markdown-text pre {
    font-family: inherit;
    font-size: 14px;
    margin: 0;
    white-space: pre-wrap;
    word-wrap: break-word;
}

.message-time {
    font-size: 11px;
    color: #bbb;
    margin-top: 6px;
}

.thinking {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 20px;
}

.dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #667eea;
    animation: bounce 1.4s ease-in-out infinite;
}

.dot:nth-child(2) {
    animation-delay: 0.2s;
}

.dot:nth-child(3) {
    animation-delay: 0.4s;
}

@keyframes bounce {
    0%, 80%, 100% {
        transform: scale(0.6);
        opacity: 0.5;
    }
    40% {
        transform: scale(1);
        opacity: 1;
    }
}

.chat-footer {
    padding: 16px 24px;
    background: white;
    border-top: 1px solid #eee;
    display: flex;
    gap: 12px;
    align-items: flex-end;
}

.chat-footer .el-input {
    flex: 1;
}

.send-btn {
    height: 56px;
    min-width: 80px;
}

/* 滚动条样式 */
.chat-body::-webkit-scrollbar {
    width: 6px;
}

.chat-body::-webkit-scrollbar-track {
    background: transparent;
}

.chat-body::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 3px;
}

.chat-body::-webkit-scrollbar-thumb:hover {
    background: #bbb;
}

/* 响应式 */
@media (max-width: 768px) {
    .ai-assistant-container {
        height: calc(100vh - 100px);
    }

    .message-content {
        max-width: 85%;
    }

    .chat-header {
        padding: 16px;
    }

    .chat-body {
        padding: 16px;
    }

    .chat-footer {
        padding: 12px 16px;
    }
}
</style>