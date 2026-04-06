package com.csdn.meeting.interfaces.websocket;

import com.csdn.meeting.infrastructure.websocket.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消息WebSocket处理器
 * 处理用户WebSocket连接、消息接收和发送
 */
@Slf4j
@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    // 心跳检测：存储最后一次收到消息的时间
    private final Map<String, Long> lastPingTimeMap = new ConcurrentHashMap<>();

    // 心跳超时时间（5分钟）
    private static final long HEARTBEAT_TIMEOUT_MS = 5 * 60 * 1000;

    // 心跳检测定时任务
    private final ScheduledExecutorService heartbeatExecutor;

    public MessageWebSocketHandler(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.objectMapper = new ObjectMapper();
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "websocket-heartbeat");
            t.setDaemon(true);
            return t;
        });

        // 启动定时心跳检测任务
        startHeartbeatCheck();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session属性中获取用户ID（由JwtHandshakeInterceptor设置）
        String userId = (String) session.getAttributes().get("userId");

        if (userId == null || userId.isEmpty()) {
            log.error("WebSocket连接建立失败: 缺少用户ID, sessionId={}", session.getId());
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // 注册会话
        sessionManager.registerSession(userId, session);
        lastPingTimeMap.put(session.getId(), System.currentTimeMillis());

        // 发送连接成功消息
        sendConnectionSuccessMessage(session, userId);

        log.info("WebSocket连接建立成功: userId={}, sessionId={}", userId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = session.getId();

        // 更新心跳时间
        lastPingTimeMap.put(sessionId, System.currentTimeMillis());

        // 处理心跳消息
        if ("ping".equalsIgnoreCase(payload) || "{\"type\":\"ping\"}".equals(payload)) {
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            return;
        }

        try {
            // 解析消息
            Map<String, Object> msgMap = objectMapper.readValue(payload, Map.class);
            String type = (String) msgMap.get("type");

            // 处理不同类型的消息
            if ("subscribe".equals(type)) {
                // 订阅消息通知（可以用于指定只接收特定类型的消息）
                handleSubscribeMessage(session, msgMap);
            } else if ("mark_read".equals(type)) {
                // 标记消息已读（通过WebSocket实时反馈）
                handleMarkReadMessage(session, msgMap);
            } else {
                log.debug("收到未知类型消息: type={}, sessionId={}", type, sessionId);
            }

        } catch (Exception e) {
            log.error("处理WebSocket消息失败: sessionId={}, payload={}", sessionId, payload, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}, error={}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();

        // 注销会话
        sessionManager.unregisterSession(session);
        lastPingTimeMap.remove(sessionId);

        log.info("WebSocket连接关闭: sessionId={}, status={}", sessionId, status);
    }

    /**
     * 发送连接成功消息
     */
    private void sendConnectionSuccessMessage(WebSocketSession session, String userId) throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "connected");
        message.put("userId", userId);
        message.put("timestamp", System.currentTimeMillis());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    /**
     * 处理订阅消息
     */
    private void handleSubscribeMessage(WebSocketSession session, Map<String, Object> msgMap) {
        // 可以扩展：根据用户订阅的会议标签等，只推送相关消息
        log.debug("用户订阅消息: sessionId={}", session.getId());
    }

    /**
     * 处理标记已读消息
     */
    private void handleMarkReadMessage(WebSocketSession session, Map<String, Object> msgMap) {
        // 可以扩展：前端通过WebSocket通知后端标记已读，后端实时反馈
        log.debug("用户标记消息已读: sessionId={}", session.getId());
    }

    /**
     * 启动心跳检测定时任务
     */
    private void startHeartbeatCheck() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();

            for (Map.Entry<String, Long> entry : lastPingTimeMap.entrySet()) {
                String sessionId = entry.getKey();
                long lastPingTime = entry.getValue();

                // 检查是否超时
                if (now - lastPingTime > HEARTBEAT_TIMEOUT_MS) {
                    log.warn("WebSocket心跳超时: sessionId={}", sessionId);

                    // 关闭超时的连接
                    sessionManager.getUserSessions("").stream()
                            .filter(s -> s.getId().equals(sessionId))
                            .findFirst()
                            .ifPresent(s -> {
                                try {
                                    s.close(CloseStatus.GOING_AWAY);
                                } catch (IOException e) {
                                    log.error("关闭超时连接失败: sessionId={}", sessionId, e);
                                }
                            });
                }
            }
        }, 1, 1, TimeUnit.MINUTES);

        log.info("WebSocket心跳检测任务已启动");
    }

    /**
     * 发送新消息通知给指定用户
     *
     * @param userId       用户ID
     * @param messageId    消息ID
     * @param messageType  消息类型（如 MEETING_PUBLISH, SYSTEM_NOTICE 等）
     * @param bizType      业务类型（MEETING/REGISTRATION/SYSTEM）
     * @param title        消息标题
     * @param bizId        关联业务ID
     * @param unreadCount  未读消息数
     */
    public void sendNewMessageNotification(String userId, String messageId, String messageType,
                                           String bizType, String title, String bizId, long unreadCount) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "NEW_MESSAGE");
            message.put("messageId", messageId);
            message.put("messageType", messageType);
            message.put("bizType", bizType);
            message.put("title", title);
            message.put("bizId", bizId);
            message.put("unreadCount", unreadCount);
            message.put("timestamp", System.currentTimeMillis());

            String jsonMessage = objectMapper.writeValueAsString(message);
            boolean sent = sessionManager.sendMessageToUser(userId, jsonMessage);

            if (sent) {
                log.debug("发送新消息通知成功: userId={}, messageId={}", userId, messageId);
            } else {
                log.debug("用户不在线，消息通知未发送: userId={}", userId);
            }

        } catch (Exception e) {
            log.error("发送新消息通知失败: userId={}", userId, e);
        }
    }

    /**
     * 发送未读消息数更新通知
     *
     * @param userId      用户ID
     * @param unreadCount 未读消息数
     */
    public void sendUnreadCountUpdate(String userId, long unreadCount) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "UNREAD_COUNT_UPDATE");
            message.put("unreadCount", unreadCount);
            message.put("timestamp", System.currentTimeMillis());

            String jsonMessage = objectMapper.writeValueAsString(message);
            sessionManager.sendMessageToUser(userId, jsonMessage);

        } catch (Exception e) {
            log.error("发送未读数更新失败: userId={}", userId, e);
        }
    }
}
