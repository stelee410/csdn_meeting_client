package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订阅/取消订阅标签命令
 */
@Data
@ApiModel("订阅标签命令")
public class SubscribeTagCommand {

    @ApiModelProperty(value = "标签ID", required = true, example = "1")
    @NotNull(message = "标签ID不能为空")
    private Long tagId;
}
