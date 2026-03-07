package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议标签DTO（包含订阅状态）
 */
@Data
@Schema(description = "会议标签（含订阅状态）")
public class MeetingTagDTO {

    @Schema(description = "标签ID", example = "1")
    private Long tagId;

    @Schema(description = "标签名称", example = "鸿蒙")
    private String tagName;

    @Schema(description = "标签分类", example = "tech")
    private String tagCategory;

    @Schema(description = "标签分类名称", example = "技术")
    private String tagCategoryName;

    @Schema(description = "用户是否已订阅", example = "true")
    private Boolean subscribed;

    @Schema(description = "订阅时间（如果已订阅）", example = "2026-02-01 10:30:00")
    private LocalDateTime subscribeTime;
}
