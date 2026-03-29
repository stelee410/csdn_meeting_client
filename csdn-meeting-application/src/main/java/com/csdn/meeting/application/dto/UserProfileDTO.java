package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（响应）
 */
@Data
@Schema(description = "用户信息")
public class UserProfileDTO {

    @Schema(description = "用户ID", example = "U1234567890ABCDEF")
    private String userId;

    @Schema(description = "手机号", example = "138****0000")
    private String mobile;

    @Schema(description = "用户类型：USER-普通用户, ADMIN-管理员, OPERATOR-运营人员", example = "USER")
    private String userType;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "邮箱是否已验证", example = "true")
    private Boolean emailVerified;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "公司", example = "CSDN")
    private String company;

    @Schema(description = "职位", example = "高级工程师")
    private String jobTitle;

    @Schema(description = "行业", example = "云计算")
    private String industry;

    @Schema(description = "账号状态：NORMAL-正常, FROZEN-冻结", example = "NORMAL")
    private String status;

    @Schema(description = "是否已同意用户协议", example = "true")
    private Boolean agreementAccepted;

    @Schema(description = "是否已同意隐私政策", example = "true")
    private Boolean privacyAccepted;

    @Schema(description = "注册时间", example = "2026-03-29T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "最后登录时间", example = "2026-03-29T12:00:00")
    private LocalDateTime lastLoginAt;
}
