# WebSocket 前端对接文档

## 概述

本文档描述前端如何与后端 WebSocket 服务对接，实现消息实时推送功能。WebSocket 用于接收新消息通知、未读数更新等实时事件，配合 HTTP API 拉取完整消息列表。

> **后端状态**: 已完成后端 WebSocket 消息格式标准化改造，所有推送消息格式统一。

## 连接信息

### WebSocket 地址

```
wss://your-domain.com/ws/messages
```

**注意**：
- 生产环境使用 `wss://`（WebSocket Secure）
- 开发环境可以使用 `ws://`
- 路径固定为 `/ws/messages`

### 认证方式

WebSocket 连接需要在握手阶段进行 JWT 认证，支持两种方式传递 Token：

#### 方式一：URL 参数（推荐）

```javascript
const token = localStorage.getItem('token');
const ws = new WebSocket(`wss://your-domain.com/ws/messages?token=${token}`);
```

#### 方式二：HTTP Header

```javascript
// 注意：浏览器 WebSocket API 不支持自定义 Header
// 此方式仅适用于移动端或特殊客户端
const ws = new WebSocket('wss://your-domain.com/ws/messages');
// 需要在握手阶段设置 Header: Authorization: Bearer {token}
```

**Token 要求**：
- Token 必须有效且未过期
- Token 不能已被撤销（登出黑名单）
- Token 必须包含用户身份信息

## 消息协议

### 上行消息（前端 → 后端）

#### 1. 心跳 Ping

保持连接活跃，防止被服务器断开。

```json
{
  "type": "ping"
}
```

**发送频率**：建议每 30-60 秒发送一次

**服务器响应**：
```json
{
  "type": "pong"
}
```

#### 2. 订阅消息（预留扩展）

用于指定只接收特定类型的消息（当前版本暂无需实现）。

```json
{
  "type": "subscribe",
  "filter": {
    "bizTypes": ["MEETING", "SYSTEM"]
  }
}
```

#### 3. 标记已读（预留扩展）

通过 WebSocket 实时通知后端标记消息已读。

```json
{
  "type": "mark_read",
  "messageId": "MSG202603290001"
}
```

### 下行消息（后端 → 前端）

#### 1. 连接成功通知

连接建立成功后立即收到。

```json
{
  "type": "connected",
  "userId": "user_123456",
  "timestamp": 1711699200000
}
```

#### 2. 新消息通知（含业务类型分类）

当有新消息时，后端会推送给在线用户。

```json
{
  "type": "NEW_MESSAGE",
  "messageType": "MEETING_PUBLISH",
  "bizType": "MEETING",
  "title": "AI技术峰会",
  "bizId": "MT202603290001",
  "unreadCount": 5,
  "timestamp": 1711699200000
}
```

**字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `type` | String | 固定为 `NEW_MESSAGE` |
| `messageType` | String | 消息类型编码：`MEETING_PUBLISH`, `REGISTRATION_APPROVED`, `REGISTRATION_REJECTED`, `SYSTEM_NOTICE`, `SYSTEM_UPDATE` |
| `bizType` | String | 业务类型（用于前端分类）：`MEETING`（会议提醒）、`REGISTRATION`（报名通知）、`SYSTEM`（系统通知） |
| `title` | String | 消息标题，用于显示在通知栏 |
| `bizId` | String | 关联业务ID（如会议ID），可用于跳转 |
| `unreadCount` | Number | 用户当前总未读消息数，用于更新消息角标 |
| `timestamp` | Number | 推送时间戳（毫秒） |

**前端处理逻辑**：

```javascript
function handleNewMessage(data) {
  // 1. 更新未读数角标
  updateUnreadBadge(data.unreadCount);
  
  // 2. 根据 bizType 将消息放入对应分类
  switch (data.bizType) {
    case 'MEETING':
    case 'REGISTRATION':
      addToMeetingMessages(data);
      break;
    case 'SYSTEM':
      addToSystemMessages(data);
      break;
  }
  
  // 3. 显示通知（如果不在消息中心页面）
  if (!isInMessageCenter()) {
    showNotification(data.title, data.messageType);
  }
}
```

#### 3. 未读数更新通知

当用户在其他端阅读消息后，本端会收到未读数更新。

```json
{
  "type": "UNREAD_COUNT_UPDATE",
  "unreadCount": 3,
  "timestamp": 1711699200000
}
```

## 消息分类处理

### 前端消息中心标签页映射

根据 `bizType` 字段将消息分类到不同标签页：

| 前端标签页 | bizType 值 | 说明 |
|-----------|-----------|------|
| 全部消息 | 全部 | 显示所有 bizType 的消息 |
| 会议提醒 | `MEETING`, `REGISTRATION` | 会议发布和报名审核通知 |
| 系统通知 | `SYSTEM` | 平台公告、服务协议更新等 |

### 示例：消息分类处理代码

```javascript
// 消息分类存储
const messageStore = {
  all: [],
  meeting: [],
  system: []
};

// 处理 WebSocket 推送的新消息
function handleWebSocketMessage(message) {
  if (message.type === 'NEW_MESSAGE') {
    const msg = {
      messageId: message.messageId,
      messageType: message.messageType,
      bizType: message.bizType,
      title: message.title,
      bizId: message.bizId,
      timestamp: message.timestamp
    };
    
    // 放入全部消息列表
    messageStore.all.unshift(msg);
    
    // 根据 bizType 放入对应分类
    if (message.bizType === 'MEETING' || message.bizType === 'REGISTRATION') {
      messageStore.meeting.unshift(msg);
    } else if (message.bizType === 'SYSTEM') {
      messageStore.system.unshift(msg);
    }
    
    // 更新 UI
    refreshMessageList();
  }
}
```

## 连接生命周期管理

### 1. 连接建立

```javascript
class MessageWebSocket {
  constructor() {
    this.ws = null;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.heartbeatInterval = null;
  }

  connect() {
    const token = localStorage.getItem('token');
    const wsUrl = `wss://your-domain.com/ws/messages?token=${token}`;
    
    this.ws = new WebSocket(wsUrl);
    
    // 连接建立
    this.ws.onopen = (event) => {
      console.log('WebSocket 连接建立');
      this.reconnectAttempts = 0;
      this.startHeartbeat();
    };
    
    // 收到消息
    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.handleMessage(data);
    };
    
    // 连接关闭
    this.ws.onclose = (event) => {
      console.log('WebSocket 连接关闭', event.code, event.reason);
      this.stopHeartbeat();
      this.tryReconnect();
    };
    
    // 连接错误
    this.ws.onerror = (error) => {
      console.error('WebSocket 错误', error);
    };
  }
}
```

### 2. 心跳机制

```javascript
// 启动心跳
startHeartbeat() {
  this.heartbeatInterval = setInterval(() => {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({ type: 'ping' }));
    }
  }, 30000); // 每 30 秒发送一次
}

// 停止心跳
stopHeartbeat() {
  if (this.heartbeatInterval) {
    clearInterval(this.heartbeatInterval);
    this.heartbeatInterval = null;
  }
}
```

### 3. 断线重连

```javascript
tryReconnect() {
  if (this.reconnectAttempts >= this.maxReconnectAttempts) {
    console.error('WebSocket 重连次数超限，停止重连');
    return;
  }
  
  const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
  
  setTimeout(() => {
    console.log(`WebSocket 尝试第 ${this.reconnectAttempts + 1} 次重连...`);
    this.reconnectAttempts++;
    this.connect();
  }, delay);
}
```

### 4. 连接关闭

```javascript
disconnect() {
  this.stopHeartbeat();
  if (this.ws) {
    this.ws.close(1000, '用户主动关闭');
    this.ws = null;
  }
}
```

## 完整示例代码

### Vue 3 Composition API 示例

```vue
<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const ws = ref(null);
const unreadCount = ref(0);
const messageList = ref({
  all: [],
  meeting: [],
  system: []
});
const currentTab = ref('all'); // 'all' | 'meeting' | 'system'

// 连接 WebSocket
const connectWebSocket = () => {
  const token = localStorage.getItem('token');
  const wsUrl = `wss://your-domain.com/ws/messages?token=${token}`;
  
  ws.value = new WebSocket(wsUrl);
  
  ws.value.onopen = () => {
    console.log('WebSocket 已连接');
    startHeartbeat();
  };
  
  ws.value.onmessage = (event) => {
    const data = JSON.parse(event.data);
    handleWebSocketMessage(data);
  };
  
  ws.value.onclose = () => {
    stopHeartbeat();
    // 可添加重连逻辑
  };
  
  ws.value.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };
};

// 处理 WebSocket 消息
const handleWebSocketMessage = (data) => {
  switch (data.type) {
    case 'connected':
      console.log('连接成功，用户ID:', data.userId);
      break;
      
    case 'NEW_MESSAGE':
      // 更新未读数
      unreadCount.value = data.unreadCount;
      
      // 构建消息对象
      const message = {
        messageId: data.messageId,
        messageType: data.messageType,
        bizType: data.bizType,
        title: data.title,
        bizId: data.bizId,
        timestamp: data.timestamp,
        isRead: false
      };
      
      // 分类存储
      messageList.value.all.unshift(message);
      
      if (data.bizType === 'MEETING' || data.bizType === 'REGISTRATION') {
        messageList.value.meeting.unshift(message);
      } else if (data.bizType === 'SYSTEM') {
        messageList.value.system.unshift(message);
      }
      
      // 显示通知（如果不在当前消息中心页面或不是当前分类）
      if (currentTab.value !== 'all' && 
          ((data.bizType === 'MEETING' && currentTab.value !== 'meeting') ||
           (data.bizType === 'SYSTEM' && currentTab.value !== 'system'))) {
        showNotification(data.title);
      }
      break;
      
    case 'UNREAD_COUNT_UPDATE':
      unreadCount.value = data.unreadCount;
      break;
      
    case 'pong':
      // 心跳响应，无需处理
      break;
  }
};

// 心跳定时器
let heartbeatTimer = null;

const startHeartbeat = () => {
  heartbeatTimer = setInterval(() => {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(JSON.stringify({ type: 'ping' }));
    }
  }, 30000);
};

const stopHeartbeat = () => {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer);
    heartbeatTimer = null;
  }
};

// 显示浏览器通知
const showNotification = (title) => {
  if ('Notification' in window && Notification.permission === 'granted') {
    new Notification('新消息', { body: title });
  }
};

// 切换标签页时重新拉取列表
const switchTab = async (tab) => {
  currentTab.value = tab;
  
  // 调用 HTTP API 获取对应分类的消息列表
  const bizType = tab === 'all' ? '' : tab.toUpperCase();
  const response = await fetch(`/api/messages?bizType=${bizType}&page=1&size=20`, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  const result = await response.json();
  
  if (result.code === 200) {
    messageList.value[tab] = result.data.list;
  }
};

onMounted(() => {
  connectWebSocket();
  // 请求浏览器通知权限
  if ('Notification' in window) {
    Notification.requestPermission();
  }
});

onUnmounted(() => {
  stopHeartbeat();
  if (ws.value) {
    ws.value.close();
  }
});
</script>

<template>
  <div class="message-center">
    <!-- 未读数角标 -->
    <div class="unread-badge" v-if="unreadCount > 0">
      {{ unreadCount }}
    </div>
    
    <!-- 标签页切换 -->
    <div class="tabs">
      <button 
        :class="{ active: currentTab === 'all' }"
        @click="switchTab('all')"
      >
        全部消息
      </button>
      <button 
        :class="{ active: currentTab === 'meeting' }"
        @click="switchTab('meeting')"
      >
        会议提醒
      </button>
      <button 
        :class="{ active: currentTab === 'system' }"
        @click="switchTab('system')"
      >
        系统通知
      </button>
    </div>
    
    <!-- 消息列表 -->
    <div class="message-list">
      <div 
        v-for="msg in messageList[currentTab]" 
        :key="msg.messageId"
        :class="['message-item', { unread: !msg.isRead }]"
      >
        <div class="message-title">{{ msg.title }}</div>
        <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
        <div class="message-type">{{ getMessageTypeLabel(msg.messageType) }}</div>
      </div>
    </div>
  </div>
</template>
```

## 错误处理

### 常见错误码

| 状态码 | 说明 | 处理建议 |
|--------|------|----------|
| 1000 | 正常关闭 | 无需特殊处理 |
| 1001 | 终端离开 | 尝试重连 |
| 1006 | 异常关闭 | 检查网络，尝试重连 |
| 1008 | 策略违规 | Token 无效，需重新登录 |
| 1011 | 服务器错误 | 稍后重试 |

### Token 过期处理

当 WebSocket 因 Token 过期而断开时（close code 1008）：

```javascript
ws.onclose = (event) => {
  if (event.code === 1008) {
    // Token 无效，引导用户重新登录
    logout();
    router.push('/login');
  } else {
    // 其他原因，尝试重连
    tryReconnect();
  }
};
```

## 性能优化建议

1. **避免频繁重连**：使用指数退避算法，最大重连间隔不超过 30 秒
2. **页面可见性**：当页面不可见时（`document.hidden`），可适当延长心跳间隔
3. **消息去重**：使用 `messageId` 进行消息去重，防止重复显示
4. **虚拟滚动**：消息列表较长时使用虚拟滚动，避免 DOM 过多
5. **防抖处理**：未读数更新使用防抖，避免频繁更新 UI

## 调试工具

### 浏览器开发者工具

- **Network → WS**：查看 WebSocket 连接和消息
- **Console**：查看连接状态和错误日志

### 测试脚本

```javascript
// 快速测试 WebSocket
const ws = new WebSocket('wss://your-domain.com/ws/messages?token=YOUR_TOKEN');

ws.onopen = () => console.log('Connected');
ws.onmessage = (e) => console.log('Received:', JSON.parse(e.data));
ws.onclose = (e) => console.log('Closed:', e.code, e.reason);

// 发送心跳
setInterval(() => ws.send(JSON.stringify({type: 'ping'})), 30000);
```

## 安全注意事项

1. **Token 安全**：不要在 URL 中明文传递 Token（虽然后端支持，但可能被记录到日志）
2. **消息验证**：收到消息后验证字段完整性，防止 XSS
3. **HTTPS/WSS**：生产环境必须使用 WSS（WebSocket Secure）
4. **CORS**：后端已配置 `AllowedOrigins`，前端无需额外处理
