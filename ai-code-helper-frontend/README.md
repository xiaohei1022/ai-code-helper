# AI编程小助手前端

一个基于Vue3的AI编程助手聊天应用，帮助用户解答编程学习和求职面试相关的问题。

## 功能特性

- 🤖 AI智能对话：通过SSE实时流式响应
- 💬 聊天室界面：用户消息在右侧，AI回复在左侧
- 🎨 现代化UI：渐变背景、毛玻璃效果、响应式设计
- 📱 移动端适配：支持手机和平板设备
- ⌨️ 智能输入：支持多行输入，自动调整高度
- 🔄 实时流式：AI回复实时显示，带有打字动画效果
- 📝 代码高亮：支持代码块和行内代码格式化

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 快速的前端构建工具
- **Axios** - HTTP客户端库
- **Server-Sent Events (SSE)** - 实时数据流

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
ai-code-helper-frontend/
├── src/
│   ├── App.vue          # 主应用组件
│   ├── main.js          # 应用入口
│   └── style.css        # 全局样式
├── index.html           # HTML模板
├── package.json         # 项目配置
├── vite.config.js       # Vite配置
└── README.md           # 项目说明
```

## 后端接口

应用需要配合SpringBoot后端使用，接口地址：`http://localhost:8081/api`

### 聊天接口

- **URL**: `/ai/chat`
- **方法**: GET
- **参数**: 
  - `memoryId`: 聊天室ID（整数）
  - `message`: 用户消息（字符串）
- **响应**: Server-Sent Events流式数据

## 主要功能

### 1. 聊天室管理
- 自动生成唯一的聊天室ID
- 支持多会话隔离
- 实时消息同步

### 2. 消息处理
- 用户消息实时发送
- AI回复流式显示
- 消息格式化（支持代码块、换行等）
- 错误处理和重试机制

### 3. 用户体验
- 自动滚动到最新消息
- 输入框自动调整高度
- 加载状态指示器
- 响应式设计适配各种设备

## 开发说明

### 核心组件

`App.vue` 是应用的主要组件，包含：

- **聊天界面布局**：头部、消息区域、输入区域
- **消息管理**：消息存储、格式化、显示
- **SSE连接**：与后端实时通信
- **状态管理**：加载状态、输入状态等

### 关键功能实现

1. **SSE连接管理**
   ```javascript
   const eventSource = new EventSource(url)
   eventSource.onmessage = (event) => {
     // 处理流式数据
   }
   ```

2. **消息格式化**
   ```javascript
   const formatMessage = (content) => {
     // 支持代码块、换行等格式化
   }
   ```

3. **自动滚动**
   ```javascript
   const scrollToBottom = async () => {
     await nextTick()
     messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
   }
   ```

## 浏览器兼容性

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## 许可证

MIT License 