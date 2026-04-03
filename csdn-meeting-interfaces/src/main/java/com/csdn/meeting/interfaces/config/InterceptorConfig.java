package com.csdn.meeting.interfaces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public InterceptorConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置登录拦截器
        registry.addInterceptor(loginInterceptor)
                // 需要登录的路径
                .addPathPatterns(
                        "/api/users/profile",           // 用户资料
                        "/api/users/profile/**",        // 资料相关
                        "/api/registrations/**",       // 报名
                        "/api/favorites/**",           // 收藏
                        "/api/subscriptions/**",      // 订阅
                        "/api/v1/messages/**",        // 消息中心
                        "/api/meetings/*/favorite",   // 会议收藏（Toggle接口）
                        "/api/meetings/*/favorite/**" // 收藏相关操作
                )
                // 放行的路径
                .excludePathPatterns(
                        "/api/auth/**",                // 认证相关接口
                        "/api/meetings",             // 会议列表（公开）
                        "/api/meetings/{id}",        // 会议详情（公开）
                        "/api/meetings/{id}/**",     // 其他会议公开接口
                        "/api/tags/**",              // 标签浏览（公开）
                        "/swagger-ui/**",            // Swagger UI
                        "/v3/api-docs/**"            // OpenAPI文档
                );
    }
}
