package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;

public class Meeting extends BaseEntity {

    private String meetingId;
    private String title;
    private String description;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MeetingStatus status;
    private Integer maxParticipants;

    public enum MeetingStatus {
        CREATED,
        STARTED,
        ENDED,
        CANCELLED
    }

    public void start() {
        if (this.status != MeetingStatus.CREATED) {
            throw new IllegalStateException("只有已创建的会议才能开始");
        }
        this.status = MeetingStatus.STARTED;
    }

    public void end() {
        if (this.status != MeetingStatus.STARTED) {
            throw new IllegalStateException("只有进行中的会议才能结束");
        }
        this.status = MeetingStatus.ENDED;
    }

    public void cancel() {
        if (this.status == MeetingStatus.ENDED) {
            throw new IllegalStateException("已结束的会议不能取消");
        }
        this.status = MeetingStatus.CANCELLED;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
