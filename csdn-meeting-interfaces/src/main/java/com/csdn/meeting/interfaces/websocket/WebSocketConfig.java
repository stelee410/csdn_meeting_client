package com.csdn.meeting.interfaces.websocket;

import com.csdn.meeting.interfaces.interceptor.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 注册WebSocket处理器和拦截器
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageWebSocketHandler messageWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfig(MessageWebSocketHandler messageWebSocketHandler,
                          JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.messageWebSocketHandler = messageWebSocketHandler;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler, "/ws/messages")
                // 添加JWT握手拦截器进行身份认证
                .addInterceptors(jwtHandshakeInterceptor)
                // 允许跨域（生产环境应配置具体域名）
                .setAllowedOrigins("*");
    }
}
