package com.csdn.meeting.interfaces.config;

import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import com.csdn.meeting.infrastructure.security.TokenRevocationStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 验证请求中的JWT令牌
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationStore tokenRevocationStore;

    public LoginInterceptor(JwtTokenProvider jwtTokenProvider,
                            TokenRevocationStore tokenRevocationStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRevocationStore = tokenRevocationStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取Authorization头
        String authHeader = request.getHeader("Authorization");

        // 2. 检查Authorization头格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求未携带有效的Authorization头: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }

        // 3. 提取token
        String token = authHeader.substring(7);

        // 4. 验证token
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("JWT令牌验证失败: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\"}");
            return false;
        }

        // 5. 检查token是否已被撤销（登出黑名单）
        if (tokenRevocationStore.isRevoked(token)) {
            log.warn("JWT令牌已被撤销（已登出）: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\"}");
            return false;
        }

        // 6. 从token中提取用户ID并设置到request属性中
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        request.setAttribute("currentUserId", userId);

        log.debug("用户[{}]请求[{}]通过验证", userId, request.getRequestURI());
        return true;
    }
}
