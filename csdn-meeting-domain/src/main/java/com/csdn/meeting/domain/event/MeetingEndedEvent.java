package com.csdn.meeting.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件：会议进入 ENDED 状态时发布。
 * 由 autoEnd() 或 end() 调用方在状态变更后发布，用于触发简报数据聚合等异步任务。
 */
public class MeetingEndedEvent extends DomainEvent {

    private final String meetingId;
    private final LocalDateTime endedAt;

    public MeetingEndedEvent(String meetingId, LocalDateTime endedAt) {
        super();
        this.meetingId = meetingId;
        this.endedAt = endedAt != null ? endedAt : LocalDateTime.now();
    }

    public String getMeetingId() {
        return meetingId;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }
}
