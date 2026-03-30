package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.UpdateUserProfileCommand;
import com.csdn.meeting.application.dto.UserProfileDTO;
import com.csdn.meeting.application.service.UserProfileAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户资料控制器
 * 处理用户资料查询、更新等接口
 */
@Slf4j
@Tag(name = "用户资料接口", description = "用户资料管理（身份信息、职业信息）")
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileAppService userProfileAppService;

    public UserProfileController(UserProfileAppService userProfileAppService) {
        this.userProfileAppService = userProfileAppService;
    }

    /**
     * 获取当前用户ID（从请求属性中获取，由LoginInterceptor设置）
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        throw new RuntimeException("用户未登录");
    }

    @Operation(summary = "获取当前用户资料",
            description = "获取当前登录用户的完整资料，包括身份信息（姓名、手机、邮箱）和职业信息（公司、职位、行业）")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile(HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        log.debug("获取用户资料: userId={}", userId);

        UserProfileDTO profile = userProfileAppService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "更新用户资料",
            description = "更新当前登录用户的资料，包括：\n" +
                    "- 基础信息：昵称、头像\n" +
                    "- 身份信息：真实姓名、邮箱（变更需验证码）\n" +
                    "- 职业信息：公司、职位、行业\n" +
                    "行业枚举值与会议侧所属产业对齐：AI人工智能、云计算、开源、出海、鸿蒙、游戏、金融")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileCommand command,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        log.info("更新用户资料: userId={}, nickname={}, industry={}",
                userId, command.getNickname(), command.getIndustry());

        UserProfileDTO profile = userProfileAppService.updateUserProfile(userId, command);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
