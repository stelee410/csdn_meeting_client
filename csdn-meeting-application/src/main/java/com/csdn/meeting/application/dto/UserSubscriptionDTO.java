package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户标签订阅DTO
 */
@Data
@ApiModel("用户标签订阅信息")
public class UserSubscriptionDTO {

    @ApiModelProperty(value = "标签ID", example = "1")
    private Long tagId;

    @ApiModelProperty(value = "标签名称", example = "鸿蒙")
    private String tagName;

    @ApiModelProperty(value = "标签分类", example = "tech")
    private String tagCategory;

    @ApiModelProperty(value = "标签分类名称", example = "技术")
    private String tagCategoryName;

    @ApiModelProperty(value = "订阅时间", example = "2026-02-01 10:30:00")
    private LocalDateTime subscribeTime;

    @ApiModelProperty(value = "该标签下新发布的会议数量", example = "3")
    private Integer newMeetingCount;
}
