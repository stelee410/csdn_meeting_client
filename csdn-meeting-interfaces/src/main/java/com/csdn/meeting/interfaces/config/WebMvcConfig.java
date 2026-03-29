package com.csdn.meeting.interfaces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置登录拦截器
        registry.addInterceptor(loginInterceptor)
                // 需要登录的路径
                .addPathPatterns(
                        "/api/user/profile",           // 用户资料
                        "/api/user/profile/**",        // 资料相关
                        "/api/registrations/**",       // 报名
                        "/api/favorites/**",           // 收藏
                        "/api/subscriptions/**"        // 订阅
                )
                // 放行的路径
                .excludePathPatterns(
                        "/api/auth/**",                // 认证相关接口
                        "/api/meetings/**",            // 会议浏览（公开）
                        "/api/tags/**",                // 标签浏览（公开）
                        "/swagger-ui/**",              // Swagger UI
                        "/v3/api-docs/**"              // OpenAPI文档
                );
    }
}
