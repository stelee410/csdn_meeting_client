package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.CheckinCommand;
import com.csdn.meeting.application.dto.CheckinQrDTO;
import com.csdn.meeting.application.dto.CheckinResultDTO;
import com.csdn.meeting.application.service.MeetingCheckinUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 签到控制器
 * 提供签到码生成、二维码获取、扫码签到等API
 */
@Tag(name = "签到接口", description = "会议现场扫码签到相关接口")
@RestController
@RequestMapping("/api")
public class CheckinController {

    private final MeetingCheckinUseCase meetingCheckinUseCase;

    public CheckinController(MeetingCheckinUseCase meetingCheckinUseCase) {
        this.meetingCheckinUseCase = meetingCheckinUseCase;
    }

    /**
     * 主办方生成签到码
     * 为会议生成签到二维码的Token，长期有效
     */
    @Operation(
            summary = "生成签到码",
            description = "主办方为会议生成签到二维码的Token，长期有效便于打印或投屏展示。"
    )
    @PostMapping("/meetings/{meetingId}/checkin-code")
    public ResponseEntity<ApiResponse<CheckinQrDTO>> generateCheckinCode(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId) {
        CheckinQrDTO qrDTO = meetingCheckinUseCase.generateCheckinCode(meetingId);
        return ResponseEntity.ok(ApiResponse.success(qrDTO));
    }

    /**
     * 获取签到二维码数据
     * 用于主办方展示签到二维码
     */
    @Operation(
            summary = "获取签到二维码",
            description = "获取签到二维码的数据内容，用于投屏展示或生成二维码图片。"
    )
    @GetMapping("/meetings/{meetingId}/checkin-qr")
    public ResponseEntity<ApiResponse<CheckinQrDTO>> getCheckinQrData(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId) {
        CheckinQrDTO qrDTO = meetingCheckinUseCase.getCheckinQrData(meetingId);
        return ResponseEntity.ok(ApiResponse.success(qrDTO));
    }

    /**
     * 扫码签到
     * 参会者扫描二维码后调用此接口完成签到
     */
    @Operation(
            summary = "扫码签到",
            description = "参会者扫描二维码后调用此接口完成签到。"
    )
    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<CheckinResultDTO>> checkin(
            @RequestBody CheckinCommand command,
            HttpServletRequest request) {
        // 自动填充设备信息和IP地址
        if (command.getDeviceInfo() == null || command.getDeviceInfo().isEmpty()) {
            command.setDeviceInfo(extractDeviceInfo(request));
        }
        if (command.getIpAddress() == null || command.getIpAddress().isEmpty()) {
            command.setIpAddress(getClientIp(request));
        }
        if (command.getCheckinMethod() == null || command.getCheckinMethod().isEmpty()) {
            command.setCheckinMethod("QR_CODE");
        }

        CheckinResultDTO result = meetingCheckinUseCase.checkin(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 查询签到状态
     * 查询当前用户在指定会议的签到状态
     */
    @Operation(
            summary = "查询签到状态",
            description = "查询当前用户在指定会议的签到状态。"
    )
    @GetMapping("/checkin/status")
    public ResponseEntity<ApiResponse<CheckinStatusResponse>> getCheckinStatus(
            @Parameter(description = "会议ID", example = "M123456789")
            @RequestParam String meetingId,
            @Parameter(description = "用户ID", example = "12345")
            @RequestParam Long userId) {
        MeetingCheckinUseCase.CheckinStatusResult status =
                meetingCheckinUseCase.getCheckinStatus(meetingId, userId);

        CheckinStatusResponse response = new CheckinStatusResponse();
        response.setMeetingId(status.getMeetingId());
        response.setRegistrationStatus(status.getRegistrationStatus());
        response.setCheckedIn(status.isCheckedIn());
        response.setCheckinTime(status.getCheckinTime());
        response.setFound(status.isFound());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 从请求中提取设备信息
     */
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        // 简化处理，实际可解析更多设备信息
        if (userAgent.contains("iPhone")) {
            return "iPhone";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else {
            return "Other";
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 签到状态响应DTO
     */
    public static class CheckinStatusResponse {
        private String meetingId;
        private String registrationStatus;
        private boolean checkedIn;
        private java.time.LocalDateTime checkinTime;
        private boolean found;

        // Getters and Setters
        public String getMeetingId() { return meetingId; }
        public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
        public String getRegistrationStatus() { return registrationStatus; }
        public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
        public boolean isCheckedIn() { return checkedIn; }
        public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }
        public java.time.LocalDateTime getCheckinTime() { return checkinTime; }
        public void setCheckinTime(java.time.LocalDateTime checkinTime) { this.checkinTime = checkinTime; }
        public boolean isFound() { return found; }
        public void setFound(boolean found) { this.found = found; }
    }
}
