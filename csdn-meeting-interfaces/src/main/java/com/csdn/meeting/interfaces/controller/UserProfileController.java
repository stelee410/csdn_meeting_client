package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.UserProfileDTO;
import com.csdn.meeting.application.dto.UserProfileUpdateCommand;
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
 * 处理用户信息查询和更新
 */
@Slf4j
@Tag(name = "用户资料接口")
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserProfileAppService userProfileAppService;

    public UserProfileController(UserProfileAppService userProfileAppService) {
        this.userProfileAppService = userProfileAppService;
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细资料")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        UserProfileDTO profile = userProfileAppService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "更新用户资料", description = "更新当前登录用户的资料信息")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @Valid @RequestBody UserProfileUpdateCommand command,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        UserProfileDTO profile = userProfileAppService.updateProfile(userId, command);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * 从请求中获取当前用户ID（由LoginInterceptor设置）
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        throw new RuntimeException("用户未登录");
    }
}
