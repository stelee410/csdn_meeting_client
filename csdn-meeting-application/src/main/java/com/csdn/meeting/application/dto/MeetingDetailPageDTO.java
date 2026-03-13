package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议详情页DTO
 * 包含完整会议信息、用户报名状态、收藏状态、按钮状态等
 */
@Data
@Schema(description = "会议详情页数据")
public class MeetingDetailPageDTO {

    @Schema(description = "会议基础信息")
    private MeetingDetailDTO meeting;

    @Schema(description = "当前用户报名状态")
    private MyRegistrationStatusDTO myRegistration;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "底部报名按钮状态")
    private ButtonStateDTO buttonState;

    @Schema(description = "报名表单配置")
    private List<FormFieldConfigDTO> formConfig;

    /**
     * 我的报名状态DTO
     */
    @Data
    @Schema(description = "我的报名状态")
    public static class MyRegistrationStatusDTO {
        @Schema(description = "报名ID")
        private Long registrationId;

        @Schema(description = "报名状态：PENDING/APPROVED/REJECTED/CANCELLED/CHECKED_IN/NOT_REGISTERED")
        private String status;

        @Schema(description = "报名状态显示名称")
        private String statusName;

        @Schema(description = "报名时间")
        private LocalDateTime registeredAt;

        @Schema(description = "签到时间")
        private LocalDateTime checkinAt;

        @Schema(description = "审核备注（拒绝时显示）")
        private String auditRemark;

        @Schema(description = "是否可以取消报名")
        private Boolean canCancel;

        @Schema(description = "是否可以签到")
        private Boolean canCheckin;
    }

    /**
     * 报名按钮状态DTO
     */
    @Data
    @Schema(description = "报名按钮状态")
    public static class ButtonStateDTO {
        @Schema(description = "按钮类型：REGISTER/FULL/ENDED/NOT_STARTED/ALREADY_REGISTERED/VIEW_TICKET/LOGIN_REQUIRED/CLOSED")
        private String type;

        @Schema(description = "按钮显示文案")
        private String text;

        @Schema(description = "按钮是否可点击")
        private Boolean enabled;

        @Schema(description = "按钮点击动作")
        private String action;

        @Schema(description = "提示信息（如名额已满时的提示）")
        private String tip;

        @Schema(description = "剩余名额（-1表示不限）")
        private Integer remainingSpots;
    }
}
