package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CSDN授权回调命令
 * 用户扫码授权后，前端调用此接口完成登录/注册
 *
 * TODO【需与CSDN对接】: 确认实际的回调参数格式
 * 包括：授权码字段名、是否返回用户信息、回调方式等
 */
@Data
@Schema(description = "CSDN扫码授权回调请求。TODO【需与CSDN对接】: 确认实际参数格式")
public class CsdnAuthCallbackCommand {

    @NotBlank(message = "授权码不能为空")
    @Schema(description = "CSDN授权码，用户扫码后CSDN回调返回。TODO【需与CSDN对接】: 确认字段名", required = true, example = "auth_code_xxx")
    private String authCode;

    @Schema(description = "二维码ID（可选，用于扫码登录流程）", example = "qr_abc123")
    private String qrId;

    @Schema(description = "头像URL（可选，覆盖CSDN头像）", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "【新用户必填】登录密码", example = "password123")
    private String password;

    @Schema(description = "【新用户必填】确认密码", example = "password123")
    private String confirmPassword;

    @NotNull(message = "请同意用户协议")
    @Schema(description = "【新用户必填】是否同意用户协议", required = true, example = "true")
    private Boolean agreementAccepted;

    @NotNull(message = "请同意隐私政策")
    @Schema(description = "【新用户必填】是否同意隐私政策", required = true, example = "true")
    private Boolean privacyAccepted;
}
