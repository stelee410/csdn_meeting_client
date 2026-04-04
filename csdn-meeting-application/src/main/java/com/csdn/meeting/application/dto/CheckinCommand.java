package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 签到命令DTO
 * 扫码签到时携带的数据
 */
@Data
@Schema(description = "签到命令")
public class CheckinCommand {

    @Schema(description = "会议ID", example = "M123456789", required = true)
    private String meetingId;

    @Schema(description = "用户ID", example = "U927CFE0E0D2F4A65", required = true)
    private String userId;

    @Schema(description = "签到码Token（从二维码解析）", required = true)
    private String checkinToken;

    @Schema(description = "签到方式：QR_CODE扫码/MANUAL手动", example = "QR_CODE")
    private String checkinMethod;

    @Schema(description = "设备信息", example = "iPhone 15 Pro / iOS 17.0 / CSDN App 6.5.0")
    private String deviceInfo;

    @Schema(description = "IP地址")
    private String ipAddress;
}
