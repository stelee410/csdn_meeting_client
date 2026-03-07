package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订阅状态检查DTO
 */
@Data
@Schema(description = "订阅状态检查")
public class SubscriptionCheckDTO {

    @Schema(description = "标签ID", example = "1")
    private Long tagId;

    @Schema(description = "标签名称", example = "鸿蒙")
    private String tagName;

    @Schema(description = "是否已订阅", example = "true")
    private Boolean subscribed;

    @Schema(description = "订阅时间（如果已订阅）", example = "2026-02-01 10:30:00")
    private LocalDateTime subscribeTime;
}
