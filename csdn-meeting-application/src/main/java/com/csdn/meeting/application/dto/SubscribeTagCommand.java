package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订阅/取消订阅标签命令
 */
@Data
@Schema(description = "订阅标签命令")
public class SubscribeTagCommand {

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "标签ID不能为空")
    private Long tagId;
}
