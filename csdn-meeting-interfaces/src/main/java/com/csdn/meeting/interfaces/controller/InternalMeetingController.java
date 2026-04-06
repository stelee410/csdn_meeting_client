package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.service.MeetingApplicationService;
import com.csdn.meeting.application.service.MeetingAuditNotificationService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import com.csdn.meeting.interfaces.dto.internal.MeetingStatusChangeNotifyDTO;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 内部会议接口
 * 供其他服务（如 operation）调用，触发会议相关事件
 * 不对外暴露，仅限服务间内部调用
 *
 * 统一通知接口：/notify-status-change
 * 支持操作类型：PUBLISH、AUDIT_APPROVED、AUDIT_REJECTED、TAKEDOWN
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/internal/meetings")
public class InternalMeetingController {

    private final MeetingApplicationService meetingApplicationService;
    private final MeetingAuditNotificationService auditNotificationService;

    public InternalMeetingController(MeetingApplicationService meetingApplicationService,
                                      MeetingAuditNotificationService auditNotificationService) {
        this.meetingApplicationService = meetingApplicationService;
        this.auditNotificationService = auditNotificationService;
    }

    /**
     * 统一接收会议状态变更通知
     * 由 operation 服务调用，根据 actionType 执行不同通知逻辑
     */
    @PostMapping("/notify-status-change")
    public ResponseEntity<ApiResponse<Void>> notifyStatusChange(
            @Valid @RequestBody MeetingStatusChangeNotifyDTO request) {

        String actionType = request.getActionType();
        String meetingId = request.getMeetingId();

        log.info("收到会议状态变更通知: actionType={}, meetingId={}, title={}",
                actionType, meetingId, request.getTitle());

        try {
            switch (MeetingStatusChangeNotifyDTO.ActionType.valueOf(actionType)) {
                case PUBLISH:
                    // 会议发布：触发订阅推送
                    handlePublish(request);
                    break;

                case AUDIT_APPROVED:
                    // 审核通过：通知创建者
                    auditNotificationService.sendAuditApprovedNotification(
                            meetingId, request.getTitle(), request.getCreatorId(), request.getOperatorName());
                    break;

                case AUDIT_REJECTED:
                    // 审核拒绝：通知创建者
                    auditNotificationService.sendAuditRejectedNotification(
                            meetingId, request.getTitle(), request.getCreatorId(),
                            request.getOperatorName(), request.getReason(), request.getViolationTags());
                    break;

                case TAKEDOWN:
                    // 强制下架：通知创建者
                    auditNotificationService.sendTakedownNotification(
                            meetingId, request.getTitle(), request.getCreatorId(),
                            request.getOperatorName(), request.getReason(), request.getViolationTags());
                    break;

                default:
                    log.warn("未知的操作类型: {}", actionType);
                    return ResponseEntity.ok(ApiResponse.error(400, "未知的操作类型: " + actionType));
            }

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            log.error("非法的操作类型: {}", actionType, e);
            return ResponseEntity.ok(ApiResponse.error(400, "非法的操作类型: " + actionType));
        } catch (Exception e) {
            log.error("处理会议状态变更通知失败: meetingId={}, actionType={}", meetingId, actionType, e);
            return ResponseEntity.ok(ApiResponse.error(500, "处理失败: " + e.getMessage()));
        }
    }

    /**
     * 处理会议发布通知
     */
    private void handlePublish(MeetingStatusChangeNotifyDTO request) {
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            log.warn("会议发布通知缺少标签列表: meetingId={}", request.getMeetingId());
        }

        meetingApplicationService.handleExternalPublish(
                request.getMeetingId(),
                request.getTitle(),
                request.getTagIds(),
                request.getCreatorId());
    }
}
