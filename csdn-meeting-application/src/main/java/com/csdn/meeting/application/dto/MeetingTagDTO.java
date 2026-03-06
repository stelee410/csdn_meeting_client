package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议标签DTO（包含订阅状态）
 */
@Data
@ApiModel("会议标签（含订阅状态）")
public class MeetingTagDTO {

    @ApiModelProperty(value = "标签ID", example = "1")
    private Long tagId;

    @ApiModelProperty(value = "标签名称", example = "鸿蒙")
    private String tagName;

    @ApiModelProperty(value = "标签分类", example = "tech")
    private String tagCategory;

    @ApiModelProperty(value = "标签分类名称", example = "技术")
    private String tagCategoryName;

    @ApiModelProperty(value = "用户是否已订阅", example = "true")
    private Boolean subscribed;

    @ApiModelProperty(value = "订阅时间（如果已订阅）", example = "2026-02-01 10:30:00")
    private LocalDateTime subscribeTime;
}
