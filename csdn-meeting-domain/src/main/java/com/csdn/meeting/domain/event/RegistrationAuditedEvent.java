package com.csdn.meeting.domain.event;

import com.csdn.meeting.domain.entity.Registration;

import java.time.LocalDateTime;

/**
 * 报名审核领域事件（agent.prd §3.6）
 * 审核通过/拒绝后发布，由 NotificationClient 监听并触发 Push/短信/邮件/私信。
 */
public class RegistrationAuditedEvent extends DomainEvent {

    private final Long registrationId;
    private final Long meetingId;
    private final String userId;
    private final Registration.RegistrationStatus status;
    private final LocalDateTime auditedAt;
    private final String auditRemark;

    public RegistrationAuditedEvent(Long registrationId,
                                    Long meetingId,
                                    String userId,
                                    Registration.RegistrationStatus status,
                                    LocalDateTime auditedAt,
                                    String auditRemark) {
        super();
        this.registrationId = registrationId;
        this.meetingId = meetingId;
        this.userId = userId;
        this.status = status;
        this.auditedAt = auditedAt != null ? auditedAt : LocalDateTime.now();
        this.auditRemark = auditRemark;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public Registration.RegistrationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAuditedAt() {
        return auditedAt;
    }

    public String getAuditRemark() {
        return auditRemark;
    }
}
