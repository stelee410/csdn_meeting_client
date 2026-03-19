package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.TrackEventCommand;
import com.csdn.meeting.application.service.analytics.AnalyticsTrackService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 埋点分析控制器
 * 提供统一的埋点事件上报接口
 */
@Tag(name = "埋点分析接口", description = "统一的埋点事件上报接口，前端通过此接口上报各类埋点事件")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsTrackService analyticsTrackService;

    public AnalyticsController(AnalyticsTrackService analyticsTrackService) {
        this.analyticsTrackService = analyticsTrackService;
    }

    /**
     * 单条上报埋点事件
     * @param command 埋点事件数据
     * @param request HTTP请求
     * @return 上报结果
     */
    @Operation(summary = "单条上报埋点事件",
            description = "前端调用此接口上报单个埋点事件，如：视图切换、按钮点击等")
    @PostMapping("/track")
    public ResponseEntity<ApiResponse<Void>> trackEvent(
            @Valid @RequestBody
            @Parameter(description = "埋点事件数据", required = true)
            TrackEventCommand command,
            HttpServletRequest request) {

        String ipAddress = extractIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        analyticsTrackService.trackEvent(command, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 批量上报埋点事件
     * @param commands 埋点事件列表
     * @param request HTTP请求
     * @return 上报结果
     */
    @Operation(summary = "批量上报埋点事件",
            description = "前端调用此接口批量上报多个埋点事件，用于批量上报场景减少请求次数")
    @PostMapping("/track/batch")
    public ResponseEntity<ApiResponse<Void>> trackEvents(
            @Valid @RequestBody
            @Parameter(description = "埋点事件列表", required = true)
            List<TrackEventCommand> commands,
            HttpServletRequest request) {

        String ipAddress = extractIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        analyticsTrackService.trackEvents(commands, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 提取客户端IP地址
     */
    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
