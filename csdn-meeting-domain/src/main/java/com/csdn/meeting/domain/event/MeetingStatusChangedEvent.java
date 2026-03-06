package com.csdn.meeting.domain.event;

import com.csdn.meeting.domain.entity.Meeting;

import java.time.LocalDateTime;

/**
 * 通用状态变更领域事件，用于审计日志。
 * 每次 Meeting 状态发生变更时发布。
 */
public class MeetingStatusChangedEvent extends DomainEvent {

    private final String meetingId;
    private final Meeting.MeetingStatus fromStatus;
    private final Meeting.MeetingStatus toStatus;
    private final LocalDateTime timestamp;
    private final String actor;

    public MeetingStatusChangedEvent(String meetingId,
                                     Meeting.MeetingStatus fromStatus,
                                     Meeting.MeetingStatus toStatus,
                                     LocalDateTime timestamp,
                                     String actor) {
        super();
        this.meetingId = meetingId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.actor = actor;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public Meeting.MeetingStatus getFromStatus() {
        return fromStatus;
    }

    public Meeting.MeetingStatus getToStatus() {
        return toStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 触发变更的操作者（如 userId 或 "SYSTEM"），可为 null
     */
    public String getActor() {
        return actor;
    }
}
