package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 更换邮箱命令
 * 用于用户更换绑定邮箱
 */
@Data
@Schema(description = "更换邮箱请求")
public class ChangeEmailCommand {

    @Schema(description = "新邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "newemail@example.com")
    @NotBlank(message = "新邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String newEmail;

    @Schema(description = "新邮箱验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "新邮箱验证码不能为空")
    private String newEmailCode;

    @Schema(description = "原邮箱验证码（与原邮箱二选一）", example = "123456")
    private String oldEmailCode;

    @Schema(description = "短信验证码（与原邮箱二选一）", example = "123456")
    private String smsCode;
}
