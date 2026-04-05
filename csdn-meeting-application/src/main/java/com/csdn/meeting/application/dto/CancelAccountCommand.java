package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 注销账号命令
 * 用于用户主动注销账号
 */
@Data
@Schema(description = "注销账号请求")
public class CancelAccountCommand {

    @Schema(description = "邮箱验证码（与短信验证码二选一）", example = "123456")
    private String emailCode;

    @Schema(description = "短信验证码（与邮箱验证码二选一）", example = "123456")
    private String smsCode;
}
