package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CSDN授权回调命令
 * TODO【需与CSDN对接】：确认实际的回调参数格式
 */
@Data
@Schema(description = "CSDN扫码授权回调请求")
public class CsdnAuthCallbackCommand {

    @NotBlank(message = "授权码不能为空")
    @Schema(description = "TODO【需与CSDN对接】CSDN授权码，用户扫码后CSDN回调返回", required = true)
    private String authCode;

    @Schema(description = "头像URL（可选，覆盖CSDN头像）")
    private String avatarUrl;

    @Schema(description = "【新用户必填】登录密码")
    private String password;

    @Schema(description = "【新用户必填】确认密码")
    private String confirmPassword;

    @NotNull(message = "请同意用户协议")
    @Schema(description = "【新用户必填】是否同意用户协议", required = true)
    private Boolean agreementAccepted;

    @NotNull(message = "请同意隐私政策")
    @Schema(description = "【新用户必填】是否同意隐私政策", required = true)
    private Boolean privacyAccepted;
}
