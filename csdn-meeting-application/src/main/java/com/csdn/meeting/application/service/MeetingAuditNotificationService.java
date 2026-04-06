package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.port.MessagePushPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会议审核通知服务
 * 处理来自 operation 服务的会议审核结果通知
 * 向会议创建者发送站内信通知
 */
@Slf4j
@Service
public class MeetingAuditNotificationService {

    private final MessagePushPort messagePushPort;

    public MeetingAuditNotificationService(MessagePushPort messagePushPort) {
        this.messagePushPort = messagePushPort;
    }

    /**
     * 发送会议审核通过通知
     *
     * @param meetingId   会议ID
     * @param title       会议标题
     * @param creatorId   创建者ID
     * @param operatorName 审核人姓名
     */
    public void sendAuditApprovedNotification(String meetingId, String title,
                                               String creatorId, String operatorName) {
        log.info("发送会议审核通过通知: meetingId={}, creatorId={}", meetingId, creatorId);

        if (creatorId == null || creatorId.isEmpty()) {
            log.warn("无法发送通知：创建者ID为空, meetingId={}", meetingId);
            return;
        }

        String bizId = "meeting_audit_approved_" + meetingId;
        String messageTitle = "【会议审核通过】" + title;
        String content = String.format("恭喜您！会议《%s》已通过审核并发布。审核人：%s",
                title, operatorName != null ? operatorName : "系统管理员");

        Map<String, Object> extra = new HashMap<>();
        extra.put("meetingId", meetingId);
        extra.put("meetingTitle", title);
        extra.put("operatorName", operatorName);
        extra.put("auditResult", "APPROVED");

        List<String> userIds = Collections.singletonList(creatorId);

        try {
            messagePushPort.sendSiteMessage(bizId, MessagePushPort.MessageType.MEETING_AUDIT_APPROVED,
                    userIds, messageTitle, content, extra);
            log.info("会议审核通过通知发送成功: meetingId={}, userId={}", meetingId, creatorId);
        } catch (Exception e) {
            log.error("会议审核通过通知发送失败: meetingId={}, userId={}", meetingId, creatorId, e);
        }
    }

    /**
     * 发送会议审核拒绝通知
     *
     * @param meetingId    会议ID
     * @param title        会议标题
     * @param creatorId    创建者ID
     * @param operatorName 审核人姓名
     * @param reason       拒绝原因
     * @param tags         违规标签列表
     */
    public void sendAuditRejectedNotification(String meetingId, String title, String creatorId,
                                               String operatorName, String reason, List<String> tags) {
        log.info("发送会议审核拒绝通知: meetingId={}, creatorId={}", meetingId, creatorId);

        if (creatorId == null || creatorId.isEmpty()) {
            log.warn("无法发送通知：创建者ID为空, meetingId={}", meetingId);
            return;
        }

        String bizId = "meeting_audit_rejected_" + meetingId;
        String messageTitle = "【会议审核未通过】" + title;

        // 构建拒绝原因描述
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(String.format("很遗憾，会议《%s》未通过审核。审核人：%s\n",
                title, operatorName != null ? operatorName : "系统管理员"));

        if (tags != null && !tags.isEmpty()) {
            contentBuilder.append("\n原因标签：").append(String.join(",", tags)).append("\n");
        }

        if (reason != null && !reason.isEmpty()) {
            contentBuilder.append("\n详细说明：").append(reason);
        }

        String content = contentBuilder.toString();

        Map<String, Object> extra = new HashMap<>();
        extra.put("meetingId", meetingId);
        extra.put("meetingTitle", title);
        extra.put("operatorName", operatorName);
        extra.put("rejectReason", reason);
        extra.put("violationTags", tags);
        extra.put("auditResult", "REJECTED");

        List<String> userIds = Collections.singletonList(creatorId);

        try {
            messagePushPort.sendSiteMessage(bizId, MessagePushPort.MessageType.MEETING_AUDIT_REJECTED,
                    userIds, messageTitle, content, extra);
            log.info("会议审核拒绝通知发送成功: meetingId={}, userId={}", meetingId, creatorId);
        } catch (Exception e) {
            log.error("会议审核拒绝通知发送失败: meetingId={}, userId={}", meetingId, creatorId, e);
        }
    }

    /**
     * 发送会议强制下架通知
     *
     * @param meetingId    会议ID
     * @param title        会议标题
     * @param creatorId    创建者ID
     * @param operatorName 操作人姓名
     * @param reason       下架原因
     * @param tags         违规标签列表
     */
    public void sendTakedownNotification(String meetingId, String title, String creatorId,
                                        String operatorName, String reason, List<String> tags) {
        log.info("发送会议强制下架通知: meetingId={}, creatorId={}", meetingId, creatorId);

        if (creatorId == null || creatorId.isEmpty()) {
            log.warn("无法发送通知：创建者ID为空, meetingId={}", meetingId);
            return;
        }

        String bizId = "meeting_takedown_" + meetingId;
        String messageTitle = "【会议强制下架】" + title;

        // 构建下架原因描述
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(String.format("会议《%s》已被强制下架。操作人：%s\n",
                title, operatorName != null ? operatorName : "系统管理员"));

        if (tags != null && !tags.isEmpty()) {
            contentBuilder.append("\n下架原因：").append(String.join(",", tags)).append("\n");
        }

        if (reason != null && !reason.isEmpty()) {
            contentBuilder.append("\n详细说明：").append(reason);
        }

        contentBuilder.append("\n\n您的会议将不再对外展示，请及时整改并重新提交审核。");

        String content = contentBuilder.toString();

        Map<String, Object> extra = new HashMap<>();
        extra.put("meetingId", meetingId);
        extra.put("meetingTitle", title);
        extra.put("operatorName", operatorName);
        extra.put("takedownReason", reason);
        extra.put("violationTags", tags);
        extra.put("auditResult", "TAKEDOWN");

        List<String> userIds = Collections.singletonList(creatorId);

        try {
            messagePushPort.sendSiteMessage(bizId, MessagePushPort.MessageType.MEETING_TAKEDOWN,
                    userIds, messageTitle, content, extra);
            log.info("会议强制下架通知发送成功: meetingId={}, userId={}", meetingId, creatorId);
        } catch (Exception e) {
            log.error("会议强制下架通知发送失败: meetingId={}, userId={}", meetingId, creatorId, e);
        }
    }
}
