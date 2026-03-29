package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户注册命令（表单注册）
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterCommand {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String smsCode;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "邮箱验证码", example = "123456")
    private String emailCode;

    @Schema(description = "是否同意用户协议", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "必须同意用户协议")
    private Boolean agreementAccepted;

    @Schema(description = "是否同意隐私政策", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "必须同意隐私政策")
    private Boolean privacyAccepted;
}
