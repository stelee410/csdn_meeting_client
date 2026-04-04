package com.csdn.meeting.domain.entity;

public class Participant extends BaseEntity {

    private String userId;
    private String userName;
    private String meetingId;
    private ParticipantRole role;
    private ParticipantStatus status;

    public enum ParticipantRole {
        HOST,
        CO_HOST,
        ATTENDEE
    }

    public enum ParticipantStatus {
        INVITED,
        JOINED,
        LEFT,
        REJECTED
    }

    public void join() {
        if (this.status == ParticipantStatus.JOINED) {
            throw new IllegalStateException("您已报名该会议，请勿重复报名");
        }
        if (this.status == ParticipantStatus.LEFT || this.status == ParticipantStatus.REJECTED) {
            throw new IllegalStateException("无法加入会议");
        }
        this.status = ParticipantStatus.JOINED;
    }

    public void leave() {
        if (this.status != ParticipantStatus.JOINED) {
            throw new IllegalStateException("只有已加入的参与者才能离开");
        }
        this.status = ParticipantStatus.LEFT;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public ParticipantRole getRole() {
        return role;
    }

    public void setRole(ParticipantRole role) {
        this.role = role;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }
}
