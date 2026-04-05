package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送验证码命令
 */
@Data
@Schema(description = "发送验证码请求")
public class VerificationCodeSendCommand {

    @Schema(description = "目标（手机号/邮箱）", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "目标不能为空")
    private String target;

    @Schema(description = "验证码类型：SMS-短信, EMAIL-邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "SMS")
    @NotBlank(message = "类型不能为空")
    private String type;

    @Schema(description = "业务场景：REGISTER-注册, LOGIN-登录, RESET-重置密码, " +
            "PROFILE_UPDATE-资料更新场景（邮箱变更验证）,CHANGE_EMAIL_OLD-换绑原邮箱验证,CHANGE_EMAIL_NEW-换绑新邮箱验证," +
            "CANCEL_ACCOUNT-注销账号验证", requiredMode = Schema.RequiredMode.REQUIRED, example = "REGISTER")
    @NotBlank(message = "场景不能为空")
    private String scene;
}
