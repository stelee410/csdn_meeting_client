package com.csdn.meeting.interfaces.interceptor;

import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import com.csdn.meeting.infrastructure.security.TokenRevocationStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket JWT握手拦截器
 * 在WebSocket握手阶段验证JWT Token，将用户ID存入session属性
 */
@Slf4j
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationStore tokenRevocationStore;

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider,
                                    TokenRevocationStore tokenRevocationStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRevocationStore = tokenRevocationStore;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从URL参数或Header中获取Token
        String token = extractToken(request);

        if (token == null || token.isEmpty()) {
            log.warn("WebSocket握手失败: 缺少Token");
            return false;
        }

        // 验证Token
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("WebSocket握手失败: Token无效");
            return false;
        }

        // 检查Token是否已被撤销（登出黑名单）
        if (tokenRevocationStore.isRevoked(token)) {
            log.warn("WebSocket握手失败: Token已被撤销（已登出）");
            return false;
        }

        // 获取用户ID
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null || userId.isEmpty()) {
            log.warn("WebSocket握手失败: 无法从Token获取用户ID");
            return false;
        }

        // 将用户ID存入session属性
        attributes.put("userId", userId);
        log.info("WebSocket握手成功: userId={}", userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        // 握手后的处理，无需额外操作
    }

    /**
     * 从请求中提取Token
     * 支持URL参数(token=xxx)和Header(Authorization: Bearer xxx)
     */
    private String extractToken(ServerHttpRequest request) {
        // 尝试从URL参数获取
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String tokenParam = servletRequest.getServletRequest().getParameter("token");
            if (tokenParam != null && !tokenParam.isEmpty()) {
                return tokenParam;
            }
        }

        // 尝试从Header获取
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
