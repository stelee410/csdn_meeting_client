package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.UserSubscribeAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 用户订阅控制器
 * 提供标签订阅、取消订阅、订阅列表查询等功能
 */
@Slf4j
@Api(tags = "用户标签订阅接口")
@RestController
@RequestMapping("/api/user")
public class UserSubscribeController {

    private final UserSubscribeAppService userSubscribeAppService;

    public UserSubscribeController(UserSubscribeAppService userSubscribeAppService) {
        this.userSubscribeAppService = userSubscribeAppService;
    }

    /**
     * 获取当前用户ID（从请求头或Session中获取）
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 实际项目中应该从JWT token或Session中获取用户ID
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("用户未登录");
            }
        }
        throw new RuntimeException("用户未登录");
    }

    @ApiOperation(value = "订阅标签", notes = "用户订阅指定标签，订阅后可接收该标签下新会议的推送通知")
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<SubscribeResultDTO>> subscribe(
            @Valid @RequestBody SubscribeTagCommand command,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        SubscribeResultDTO result = userSubscribeAppService.subscribeTag(userId, command.getTagId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @ApiOperation(value = "取消订阅标签", notes = "取消订阅指定标签")
    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<SubscribeResultDTO>> unsubscribe(
            @Valid @RequestBody SubscribeTagCommand command,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        SubscribeResultDTO result = userSubscribeAppService.unsubscribeTag(userId, command.getTagId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @ApiOperation(value = "获取用户订阅列表", notes = "获取当前用户已订阅的所有标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataTypeClass = Integer.class, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小", dataTypeClass = Integer.class, paramType = "query", defaultValue = "20")
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponse<PageResult<UserSubscriptionDTO>>> getSubscriptions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        PageResult<UserSubscriptionDTO> result = userSubscribeAppService.getUserSubscribedTags(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @ApiOperation(value = "检查订阅状态", notes = "检查当前用户是否已订阅指定标签")
    @ApiImplicitParam(name = "tagId", value = "标签ID", required = true, dataTypeClass = Long.class, paramType = "query")
    @GetMapping("/subscriptions/check")
    public ResponseEntity<ApiResponse<SubscriptionCheckDTO>> checkSubscription(
            @NotNull @RequestParam Long tagId,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        SubscriptionCheckDTO result = userSubscribeAppService.checkUserSubscribed(userId, tagId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
