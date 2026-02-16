package com.csdn.meeting.domain.event;

public class ParticipantJoinedEvent extends DomainEvent {

    private final String meetingId;
    private final Long userId;
    private final String userName;

    public ParticipantJoinedEvent(String meetingId, Long userId, String userName) {
        super();
        this.meetingId = meetingId;
        this.userId = userId;
        this.userName = userName;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
