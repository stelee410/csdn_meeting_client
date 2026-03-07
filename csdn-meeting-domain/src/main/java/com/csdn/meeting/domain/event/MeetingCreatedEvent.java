package com.csdn.meeting.domain.event;

public class MeetingCreatedEvent extends DomainEvent {

    private final String meetingId;
    private final String title;
    private final String creatorId;

    public MeetingCreatedEvent(String meetingId, String title, String creatorId) {
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

    public String getCreatorId() {
        return creatorId;
    }
}
