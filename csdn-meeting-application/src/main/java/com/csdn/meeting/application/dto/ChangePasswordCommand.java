package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 修改密码命令
 * 用于用户主动修改密码
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordCommand {

    @Schema(description = "原密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "oldPassword123")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "newPassword456")
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码至少8位，需包含字母和数字")
    private String newPassword;

    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "newPassword456")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
