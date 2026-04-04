package com.csdn.meeting.domain.event;

import java.time.LocalDateTime;

/**
 * 签到领域事件
 * 用户完成签到后发布，用于统计实到人数等后续处理
 */
public class RegistrationCheckinEvent extends DomainEvent {

    private final Long registrationId;
    private final Long meetingId;
    private final String userId;
    private final String meetingTitle;
    private final String userName;
    private final LocalDateTime checkinTime;
    private final String checkinMethod;
    private final String deviceInfo;

    public RegistrationCheckinEvent(Long registrationId,
                                    Long meetingId,
                                    String userId,
                                    String meetingTitle,
                                    String userName,
                                    LocalDateTime checkinTime,
                                    String checkinMethod,
                                    String deviceInfo) {
        super();
        this.registrationId = registrationId;
        this.meetingId = meetingId;
        this.userId = userId;
        this.meetingTitle = meetingTitle;
        this.userName = userName;
        this.checkinTime = checkinTime != null ? checkinTime : LocalDateTime.now();
        this.checkinMethod = checkinMethod != null ? checkinMethod : "QR_CODE";
        this.deviceInfo = deviceInfo;
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

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public String getCheckinMethod() {
        return checkinMethod;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }
}
