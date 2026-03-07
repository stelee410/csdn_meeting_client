package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订阅操作结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订阅操作结果")
public class SubscribeResultDTO {

    @Schema(description = "是否已订阅", example = "true")
    private Boolean subscribed;

    @Schema(description = "提示消息", example = "订阅成功，该标签下新会议发布时将第一时间通知您")
    private String message;
}
