package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户资料更新命令
 */
@Data
@Schema(description = "用户资料更新请求")
public class UserProfileUpdateCommand {

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "公司", example = "CSDN")
    private String company;

    @Schema(description = "职位", example = "高级工程师")
    private String jobTitle;

    @Schema(description = "行业（与会议侧产业枚举一致）", example = "云计算")
    private String industry;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "邮箱验证码（修改邮箱时需要）", example = "123456")
    private String emailCode;
}
