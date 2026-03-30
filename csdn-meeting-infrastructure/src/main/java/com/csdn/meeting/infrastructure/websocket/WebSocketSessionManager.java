package com.csdn.meeting.infrastructure.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket会话管理器
 * 管理用户ID与WebSocketSession的映射关系
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    // 用户ID -> Session列表（一个用户可能有多个连接，如多端登录）
    private final Map<String, List<WebSocketSession>> userSessionMap = new ConcurrentHashMap<>();

    // Session ID -> 用户ID
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * 注册WebSocket连接
     *
     * @param userId  用户ID
     * @param session WebSocket会话
     */
    public void registerSession(String userId, WebSocketSession session) {
        if (userId == null || session == null) {
            return;
        }

        userSessionMap.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(session);
        sessionUserMap.put(session.getId(), userId);

        log.info("WebSocket连接注册: userId={}, sessionId={}, 当前该用户连接数={}",
                userId, session.getId(), getUserSessionCount(userId));
    }

    /**
     * 注销WebSocket连接
     *
     * @param session WebSocket会话
     */
    public void unregisterSession(WebSocketSession session) {
        if (session == null) {
            return;
        }

        String sessionId = session.getId();
        String userId = sessionUserMap.remove(sessionId);

        if (userId != null) {
            List<WebSocketSession> sessions = userSessionMap.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessionMap.remove(userId);
                }
            }
            log.info("WebSocket连接注销: userId={}, sessionId={}, 剩余连接数={}",
                    userId, sessionId, getUserSessionCount(userId));
        }
    }

    /**
     * 获取用户的所有Session
     *
     * @param userId 用户ID
     * @return Session列表
     */
    public List<WebSocketSession> getUserSessions(String userId) {
        return userSessionMap.getOrDefault(userId, new ArrayList<>());
    }

    /**
     * 获取用户的Session数量
     *
     * @param userId 用户ID
     * @return 连接数
     */
    public int getUserSessionCount(String userId) {
        List<WebSocketSession> sessions = userSessionMap.get(userId);
        return sessions == null ? 0 : sessions.size();
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        return getUserSessionCount(userId) > 0;
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     * @return 是否发送成功（至少一个连接成功）
     */
    public boolean sendMessageToUser(String userId, String message) {
        List<WebSocketSession> sessions = getUserSessions(userId);
        if (sessions.isEmpty()) {
            return false;
        }

        boolean success = false;
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                    success = true;
                } catch (IOException e) {
                    log.error("发送WebSocket消息失败: userId={}, sessionId={}", userId, session.getId(), e);
                }
            }
        }

        return success;
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public void broadcastMessage(String message) {
        int count = 0;
        for (List<WebSocketSession> sessions : userSessionMap.values()) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                        count++;
                    } catch (IOException e) {
                        log.error("广播WebSocket消息失败: sessionId={}", session.getId(), e);
                    }
                }
            }
        }
        log.info("WebSocket广播消息完成: 发送{}个连接", count);
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    public int getOnlineUserCount() {
        return userSessionMap.size();
    }

    /**
     * 获取总连接数
     *
     * @return 总连接数
     */
    public int getTotalConnectionCount() {
        return sessionUserMap.size();
    }
}
