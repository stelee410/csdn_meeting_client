package com.csdn.meeting.interfaces.dto.internal;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 会议状态变更通知 DTO（统一接口）
 * 用于接收 operation 服务发送的会议状态变更通知
 * 支持：发布、审核通过、审核拒绝、强制下架
 */
@Data
public class MeetingStatusChangeNotifyDTO {

    /**
     * 操作类型
     * PUBLISH - 会议发布（通知订阅者）
     * AUDIT_APPROVED - 审核通过（通知创建者）
     * AUDIT_REJECTED - 审核拒绝（通知创建者）
     * TAKEDOWN - 强制下架（通知创建者）
     */
    @NotBlank(message = "操作类型不能为空")
    private String actionType;

    /**
     * 会议业务ID
     */
    @NotBlank(message = "会议ID不能为空")
    private String meetingId;

    /**
     * 会议标题
     */
    @NotBlank(message = "会议标题不能为空")
    private String title;

    /**
     * 会议创建者ID
     */
    @NotBlank(message = "创建者ID不能为空")
    private String creatorId;

    /**
     * 关联的标签ID列表（仅 PUBLISH 时需要）
     */
    private List<Long> tagIds;

    /**
     * 审核/操作人姓名
     */
    private String operatorName;

    /**
     * 审核/下架原因说明（AUDIT_REJECTED、TAKEDOWN 时需要）
     */
    private String reason;

    /**
     * 违规标签列表（AUDIT_REJECTED、TAKEDOWN 时需要）
     */
    private List<String> violationTags;

    /**
     * 操作类型枚举
     */
    public enum ActionType {
        PUBLISH,
        AUDIT_APPROVED,
        AUDIT_REJECTED,
        TAKEDOWN
    }
}
