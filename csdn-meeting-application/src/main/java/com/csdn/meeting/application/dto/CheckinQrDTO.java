package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 签到二维码数据DTO
 */
@Data
@Schema(description = "签到二维码数据")
public class CheckinQrDTO {

    @Schema(description = "会议ID", example = "M123456789")
    private String meetingId;

    @Schema(description = "会议标题", example = "2024 AI技术创新峰会")
    private String meetingTitle;

    @Schema(description = "签到码Token", example = "a1b2c3d4e5f6g7h8")
    private String checkinToken;

    @Schema(description = "APP二维码内容（扫码跳转URL或Scheme）", 
            example = "app://csdn.meeting/checkin?m=M123456789&t=a1b2c3d4e5f6g7h8")
    private String qrContent;

    @Schema(description = "微信小程序二维码内容（扫码跳转页面路径）", 
            example = "/pages/meeting/checkin?m=M123456789&t=a1b2c3d4e5f6g7h8")
    private String miniappQrContent;

    @Schema(description = "二维码图片URL（如服务器生成）")
    private String qrImageUrl;

    @Schema(description = "是否启用签到", example = "true")
    private Boolean checkinEnabled;
}
