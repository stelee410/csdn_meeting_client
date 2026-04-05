package com.csdn.meeting.interfaces.config;

import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import com.csdn.meeting.infrastructure.security.TokenRevocationStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * 登录拦截器
 * 验证请求中的JWT令牌
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationStore tokenRevocationStore;

    // 会议详情路径正则（单层ID路径，如 /api/meetings/25）
    private static final Pattern MEETING_DETAIL_PATTERN = Pattern.compile("^/api/meetings/[^/]+$");

    public LoginInterceptor(JwtTokenProvider jwtTokenProvider,
                            TokenRevocationStore tokenRevocationStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRevocationStore = tokenRevocationStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        // 1. 检查是否为会议详情路径（单层ID路径）
        // GET /api/meetings/{id} 允许游客访问
        // PUT/DELETE /api/meetings/{id} 需要登录
        if (MEETING_DETAIL_PATTERN.matcher(requestUri).matches()) {
            if ("GET".equalsIgnoreCase(method)) {
                // GET 请求允许游客访问，尝试解析token但不强制要求
                return handleOptionalAuth(request, response);
            } else {
                // PUT/DELETE 等其他方法需要强制登录
                return handleRequiredAuth(request, response);
            }
        }

        // 2. 其他路径强制登录验证
        return handleRequiredAuth(request, response);
    }

    /**
     * 处理可选认证（游客访问）
     * 有token则解析并设置userId，无token则放行
     */
    private boolean handleOptionalAuth(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 无token，放行（游客访问）
            return true;
        }

        String token = authHeader.substring(7);

        // token无效或已撤销，仍放行（视为游客）
        if (!jwtTokenProvider.validateToken(token) || tokenRevocationStore.isRevoked(token)) {
            return true;
        }

        // token有效，设置userId
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        request.setAttribute("currentUserId", userId);
        log.debug("用户[{}]以登录状态访问公开接口[{}]", userId, request.getRequestURI());
        return true;
    }

    /**
     * 处理强制认证（必须登录）
     */
    private boolean handleRequiredAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
