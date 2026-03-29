package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录结果DTO
 */
@Data
@Schema(description = "登录结果")
public class LoginResultDTO {

    @Schema(description = "访问令牌（JWT）", example = "eyJhbGciOiJIUzI1NiIs...")
    private String accessToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserProfileDTO userInfo;
}
