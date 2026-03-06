package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationAuditedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 报名审核通知客户端（agent.prd §3.6）
 * Stub：监听 RegistrationAuditedEvent，对接消息中心发送 Push/短信/邮件/私信。
 * 当前为 stub 实现，仅记录日志；后续对接 CSDN 消息中心。
 */
@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    @EventListener
    public void onRegistrationAudited(RegistrationAuditedEvent event) {
        log.info("[NotificationClient] Registration audited: registrationId={} meetingId={} userId={} status={}",
                event.getRegistrationId(), event.getMeetingId(), event.getUserId(), event.getStatus());

        // Stub: 实际对接消息中心时，根据 status 发送 Push/短信/邮件/私信
        if (event.getStatus() == Registration.RegistrationStatus.APPROVED) {
            sendApprovedNotification(event);
        } else if (event.getStatus() == Registration.RegistrationStatus.REJECTED) {
            sendRejectedNotification(event);
        }
    }

    private void sendApprovedNotification(RegistrationAuditedEvent event) {
        // Stub: 调用消息中心 API 发送审核通过通知
        log.debug("Stub: would send approved notification to userId={}", event.getUserId());
    }

    private void sendRejectedNotification(RegistrationAuditedEvent event) {
        // Stub: 调用消息中心 API 发送审核拒绝通知，含 auditRemark
        log.debug("Stub: would send rejected notification to userId={} remark={}", event.getUserId(), event.getAuditRemark());
    }
}
