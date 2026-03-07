package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订阅状态检查DTO
 */
@Data
@ApiModel("订阅状态检查")
public class SubscriptionCheckDTO {

    @ApiModelProperty(value = "标签ID", example = "1")
    private Long tagId;

    @ApiModelProperty(value = "标签名称", example = "鸿蒙")
    private String tagName;

    @ApiModelProperty(value = "是否已订阅", example = "true")
    private Boolean subscribed;

    @ApiModelProperty(value = "订阅时间（如果已订阅）", example = "2026-02-01 10:30:00")
    private LocalDateTime subscribeTime;
}
