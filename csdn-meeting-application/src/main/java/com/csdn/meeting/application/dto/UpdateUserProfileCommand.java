package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 更新用户资料命令
 * 用于完善身份信息和职业信息
 */
@Data
@Schema(description = "更新用户资料请求")
public class UpdateUserProfileCommand {

    @Schema(description = "昵称", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    // ========== 身份信息 ==========

    @Schema(description = "真实姓名（用于报名、签约等场景）", example = "张三")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @Schema(description = "邮箱（可选填，填写后需验证）", example = "user@example.com")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "邮箱验证码（填写邮箱时必填）", example = "123456")
    private String emailCode;

    // ========== 职业信息 ==========

    @Schema(description = "公司名称", example = "CSDN")
    @Size(max = 100, message = "公司名称长度不能超过100个字符")
    private String company;

    @Schema(description = "职位", example = "高级工程师")
    @Size(max = 50, message = "职位长度不能超过50个字符")
    private String jobTitle;

    @Schema(description = "行业（与会议侧所属产业对齐）", example = "AI人工智能",
            allowableValues = {"AI人工智能", "云计算", "开源", "出海", "鸿蒙", "游戏", "金融"})
    private String industry;

    @Schema(description = "是否立即完善资料（true:完善后返回完整资料，false:仅保存）", example = "true")
    private Boolean returnFullProfile = true;
}
