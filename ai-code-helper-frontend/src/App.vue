<template>
  <div class="chat-container">
    <!-- 头部 -->
    <header class="chat-header">
      <h1>🤖 AI编程小助手</h1>
      <p>帮助您解答编程学习和求职面试相关问题</p>
    </header>

    <!-- 聊天记录区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message', message.type]"
      >
        <div class="message-content">
          <div class="message-avatar">
            {{ message.type === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-text">
            <div v-if="message.type === 'ai' && message.isStreaming" class="streaming-indicator">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
            <div v-html="formatMessage(message.content)"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-container">
      <div class="input-wrapper">
        <textarea
          v-model="inputMessage"
          @keydown.enter.prevent="sendMessage"
          placeholder="请输入您的问题..."
          class="chat-input"
          :disabled="isLoading"
          rows="1"
          ref="inputRef"
        ></textarea>
        <button 
          @click="sendMessage" 
          class="send-button"
          :disabled="!inputMessage.trim() || isLoading"
        >
          <span v-if="!isLoading">发送</span>
          <span v-else class="loading-spinner"></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'App',
  setup() {
    const messages = ref([])
    const inputMessage = ref('')
    const isLoading = ref(false)
    const messagesContainer = ref(null)
    const inputRef = ref(null)
    const memoryId = ref(null)
    const eventSource = ref(null)

    // 生成随机的聊天室ID
    const generateMemoryId = () => {
      return Math.floor(Math.random() * 1000000)
    }

    // 格式化消息内容（支持换行和代码块）
    const formatMessage = (content) => {
      if (!content) return ''
      
      // 将换行符转换为<br>标签
      let formatted = content.replace(/\n/g, '<br>')
      
      // 简单的代码块格式化（用```包围的内容）
      formatted = formatted.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
      
      // 行内代码格式化（用`包围的内容）
      formatted = formatted.replace(/`([^`]+)`/g, '<code>$1</code>')
      
      return formatted
    }

    // 滚动到底部
    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 自动调整输入框高度
    const adjustTextareaHeight = () => {
      if (inputRef.value) {
        inputRef.value.style.height = 'auto'
        inputRef.value.style.height = inputRef.value.scrollHeight + 'px'
      }
    }

    // 发送消息
    const sendMessage = async () => {
      const message = inputMessage.value.trim()
      if (!message || isLoading.value) return

      // 添加用户消息
      messages.value.push({
        type: 'user',
        content: message,
        timestamp: new Date()
      })

      // 清空输入框
      inputMessage.value = ''
      adjustTextareaHeight()

      // 添加AI消息占位符
      const aiMessageIndex = messages.value.length
      messages.value.push({
        type: 'ai',
        content: '',
        isStreaming: true,
        timestamp: new Date()
      })

      isLoading.value = true

      try {
        // 关闭之前的连接
        if (eventSource.value) {
          eventSource.value.close()
        }

        // 创建SSE连接
        const url = `http://localhost:8081/api/ai/chat?memoryId=${memoryId.value}&userMessage=${encodeURIComponent(message)}`
        eventSource.value = new EventSource(url)

        eventSource.value.onmessage = (event) => {
          const chunk = event.data
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].content += chunk
            messages.value[aiMessageIndex].isStreaming = true
          }
        }

        eventSource.value.onerror = (error) => {
          console.error('SSE连接错误:', error)
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].isStreaming = false
            if (!messages.value[aiMessageIndex].content) {
              messages.value[aiMessageIndex].content = '抱歉，连接出现错误，请重试。'
            }
          }
          eventSource.value.close()
          isLoading.value = false
        }

        eventSource.value.addEventListener('end', () => {
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].isStreaming = false
          }
          eventSource.value.close()
          isLoading.value = false
        })

      } catch (error) {
        console.error('发送消息失败:', error)
        if (messages.value[aiMessageIndex]) {
          messages.value[aiMessageIndex].content = '抱歉，发送消息失败，请重试。'
          messages.value[aiMessageIndex].isStreaming = false
        }
        isLoading.value = false
      }
    }

    // 监听输入框变化，自动调整高度
    watch(inputMessage, () => {
      adjustTextareaHeight()
    })

    // 监听消息变化，自动滚动
    watch(messages, () => {
      scrollToBottom()
    }, { deep: true })

    onMounted(() => {
      // 生成聊天室ID
      memoryId.value = generateMemoryId()
      
      // 添加欢迎消息
      messages.value.push({
        type: 'ai',
        content: '你好！我是AI编程小助手，很高兴为您服务！\n\n我可以帮助您：\n• 解答编程相关问题\n• 提供学习建议\n• 模拟面试问题\n• 代码审查和优化建议\n\n请告诉我您需要什么帮助？',
        timestamp: new Date()
      })

      // 聚焦输入框
      nextTick(() => {
        if (inputRef.value) {
          inputRef.value.focus()
        }
      })
    })

    return {
      messages,
      inputMessage,
      isLoading,
      messagesContainer,
      inputRef,
      sendMessage,
      formatMessage
    }
  }
}
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.chat-header {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.chat-header h1 {
  color: #333;
  margin-bottom: 5px;
  font-size: 24px;
  font-weight: 600;
}

.chat-header p {
  color: #666;
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  max-width: 80%;
}

.message.user {
  align-self: flex-end;
}

.message.ai {
  align-self: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: rgba(255, 255, 255, 0.95);
  padding: 16px;
  border-radius: 18px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  max-width: 100%;
}

.message.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-avatar {
  font-size: 24px;
  flex-shrink: 0;
  margin-top: 2px;
}

.message-text {
  flex: 1;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-text pre {
  background: #f4f4f4;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
  border: 1px solid #e0e0e0;
}

.message-text code {
  background: #f4f4f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.message.user .message-text pre,
.message.user .message-text code {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.streaming-indicator {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: pulse 1.4s ease-in-out infinite both;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes pulse {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input-container {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  max-width: 800px;
  margin: 0 auto;
}

.chat-input {
  flex: 1;
  border: 2px solid #e0e0e0;
  border-radius: 24px;
  padding: 12px 20px;
  font-size: 16px;
  resize: none;
  outline: none;
  transition: border-color 0.3s ease;
  max-height: 120px;
  min-height: 48px;
  font-family: inherit;
}

.chat-input:focus {
  border-color: #667eea;
}

.chat-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.send-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 24px;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 80px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message {
    max-width: 90%;
  }
  
  .chat-header h1 {
    font-size: 20px;
  }
  
  .chat-header p {
    font-size: 12px;
  }
  
  .input-wrapper {
    flex-direction: column;
    gap: 8px;
  }
  
  .send-button {
    width: 100%;
  }
}
</style> 