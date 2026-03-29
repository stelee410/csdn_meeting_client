package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.UserSubscribeAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户订阅控制器
 * 提供标签订阅、取消订阅、订阅列表查询等功能
 */
@Slf4j
@Tag(name = "用户标签订阅接口")
@RestController
@RequestMapping("/api/subscriptions")
public class UserSubscribeController {

    private final UserSubscribeAppService userSubscribeAppService;

    public UserSubscribeController(UserSubscribeAppService userSubscribeAppService) {
        this.userSubscribeAppService = userSubscribeAppService;
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

    @Operation(
            summary = "订阅标签",
            description = "用户订阅指定标签，订阅成功后将接收该标签下新会议的站内信通知。" +
                    "如果用户已订阅该标签，则返回已订阅提示"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "订阅标签请求参数，包含标签ID",
            required = true
    )
    @PostMapping
    public ResponseEntity<ApiResponse<SubscribeResultDTO>> subscribe(
            @Valid @RequestBody SubscribeTagCommand command,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        SubscribeResultDTO result = userSubscribeAppService.subscribeTag(userId, command.getTagId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "取消订阅标签",
            description = "取消对指定标签的订阅，取消后将不再接收该标签下新会议的通知"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "取消订阅请求参数，包含标签ID",
            required = true
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse<SubscribeResultDTO>> unsubscribe(
            @Valid @RequestBody SubscribeTagCommand command,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        SubscribeResultDTO result = userSubscribeAppService.unsubscribeTag(userId, command.getTagId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "获取用户订阅列表",
            description = "分页获取当前用户已订阅的所有标签列表，包含标签信息、订阅时间以及该标签下新会议数量"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<UserSubscriptionDTO>>> getSubscriptions(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        PageResult<UserSubscriptionDTO> result = userSubscribeAppService.getUserSubscribedTags(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "检查订阅状态",
            description = "检查当前用户是否已订阅指定标签，返回订阅状态及订阅时间（如已订阅）"
    )
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<SubscriptionCheckDTO>> checkSubscription(
            @Parameter(description = "标签ID", required = true, example = "123")
            @NotNull @RequestParam Long tagId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        SubscriptionCheckDTO result = userSubscribeAppService.checkUserSubscribed(userId, tagId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "获取用户订阅的所有标签ID列表",
            description = "获取当前用户已订阅的所有标签ID列表，不分页，返回所有标签ID"
    )
    @GetMapping("/tag-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getSubscribedTagIds(HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        List<Long> tagIds = userSubscribeAppService.getUserSubscribedTagIds(userId);
        return ResponseEntity.ok(ApiResponse.success(tagIds));
    }
}
