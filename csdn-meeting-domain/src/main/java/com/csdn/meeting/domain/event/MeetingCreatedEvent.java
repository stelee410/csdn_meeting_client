package com.csdn.meeting.domain.event;

public class MeetingCreatedEvent extends DomainEvent {

    private final String meetingId;
    private final String title;
    private final Long creatorId;

    public MeetingCreatedEvent(String meetingId, String title, Long creatorId) {
        super();
        this.meetingId = meetingId;
        this.title = title;
        this.creatorId = creatorId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getTitle() {
        return title;
    }

    public Long getCreatorId() {
        return creatorId;
    }
}
