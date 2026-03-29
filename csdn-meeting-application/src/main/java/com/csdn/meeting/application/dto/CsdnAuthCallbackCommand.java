package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CSDN扫码授权回调命令
 */
@Data
@Schema(description = "CSDN扫码授权回调请求")
public class CsdnAuthCallbackCommand {

    @Schema(description = "CSDN授权码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "授权码不能为空")
    private String authCode;

    @Schema(description = "登录密码（未注册用户需填写）", example = "password123")
    private String password;

    @Schema(description = "确认密码（未注册用户需填写）", example = "password123")
    private String confirmPassword;

    @Schema(description = "头像URL（可选，覆盖CSDN返回的头像）", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "是否同意用户协议（未注册用户必填）", example = "true")
    private Boolean agreementAccepted;

    @Schema(description = "是否同意隐私政策（未注册用户必填）", example = "true")
    private Boolean privacyAccepted;
}
