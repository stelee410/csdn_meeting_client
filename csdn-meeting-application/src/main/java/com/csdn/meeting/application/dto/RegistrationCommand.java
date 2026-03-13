package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 报名提交命令DTO
 * 用户提交报名时携带的数据
 */
@Data
@Schema(description = "报名提交命令")
public class RegistrationCommand {

    @Schema(description = "会议ID", example = "M123456789", required = true)
    private String meetingId;

    @Schema(description = "用户ID", example = "12345", required = true)
    private Long userId;

    @Schema(description = "表单字段值（name/phone/email/company/position/industry/purpose）")
    private Map<String, String> formData;
}
