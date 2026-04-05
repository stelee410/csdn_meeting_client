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
                        "/api/users/password/change",   // 修改密码
                        "/api/users/email/change",        // 更换邮箱
                        "/api/users/cancel",              // 注销账号
                        "/api/registrations/**",       // 报名
                        "/api/favorites/**",           // 收藏
                        "/api/subscriptions/**",      // 订阅
                        "/api/messages/**",        // 消息中心
                        "/api/meetings/*/favorite",   // 会议收藏（Toggle接口）
                        "/api/meetings/*/favorite/**", // 收藏相关操作
                        // 会议管理 - 需要登录的接口
                        "/api/meetings",               // 创建会议草稿（POST）
                        "/api/meetings/my-registered",      // 我报名的会议
                        "/api/meetings/my-favorites",       // 我收藏的会议
                        "/api/meetings/my-created",         // 我创建的会议
                        "/api/meetings/*/join",             // 报名/加入会议
                        "/api/meetings/*/leave",            // 取消报名/离开会议
                        "/api/meetings/*/rights/purchase",  // 购买高阶权益
                        // 会议状态操作 - 需要登录且验证创建者
                        "/api/meetings/*",             // PUT更新会议
                        "/api/meetings/*/submit",      // 提交审核
                        "/api/meetings/*/withdraw",    // 撤回审核
                        "/api/meetings/*/start",       // 开始会议
                        "/api/meetings/*/end",         // 结束会议
                        "/api/meetings/*/cancel",      // 取消会议
                        "/api/meetings/*/takedown",    // 下架会议
                        "/api/meetings/*",             // DELETE删除会议
                        // 签到相关 - 需要登录
                        "/api/checkin/**"                    // 签到相关接口
                )
                // 放行的路径
                .excludePathPatterns(
                        "/api/auth/**",                // 认证相关接口
                        "/api/meetings/{id:[^/]+}",        // 会议详情（公开）- 限制单层路径
                        "/api/meetings/{id:[^/]+}/detail-page",     // 会议详情页（公开，支持游客）
                        "/api/meetings/{id:[^/]+}/registration-status", // 报名状态查询（公开）
                        "/api/meetings/{id:[^/]+}/checkin-code",   // 生成签到码
                        "/api/meetings/{id:[^/]+}/checkin-qr",      // 获取签到二维码
                        "/api/meetings/list",          // 会议列表查询（公开）
                        "/api/meetings/filter-options", // 筛选选项（公开）
                        "/api/meetings/hot-tags",       // 热门标签（公开）
                        "/api/meetings/actions/**",    // AI相关操作（公开）
                        "/api/meetings/creator/**",    // 按创建者查询（公开）
                        "/api/meetings/*/approve",     // 审核通过（管理员）
                        "/api/meetings/*/reject",      // 审核拒绝（管理员）
                        "/api/tags/**",                // 标签浏览（公开）
                        "/swagger-ui/**",              // Swagger UI
                        "/v3/api-docs/**"              // OpenAPI文档
                );
    }
}
