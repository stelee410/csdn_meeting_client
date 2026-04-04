package com.csdn.meeting.domain.event;

import com.csdn.meeting.domain.entity.Registration;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 报名提交领域事件
 * 用户提交报名后发布，触发报名审核通知
 */
public class RegistrationSubmittedEvent extends DomainEvent {

    private final Long registrationId;
    private final Long meetingId;
    private final String userId;
    private final String meetingTitle;
    private final Registration.RegistrationStatus status;
    private final LocalDateTime submittedAt;
    private final Map<String, String> formData;

    public RegistrationSubmittedEvent(Long registrationId,
                                      Long meetingId,
                                      String userId,
                                      String meetingTitle,
                                      Registration.RegistrationStatus status,
                                      LocalDateTime submittedAt,
                                      Map<String, String> formData) {
        super();
        this.registrationId = registrationId;
        this.meetingId = meetingId;
        this.userId = userId;
        this.meetingTitle = meetingTitle;
        this.status = status;
        this.submittedAt = submittedAt != null ? submittedAt : LocalDateTime.now();
        this.formData = formData;
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

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public Registration.RegistrationStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
