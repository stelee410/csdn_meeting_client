package com.csdn.meeting.application.dto;

public class JoinMeetingCommand {

    private String meetingId;
    private String userId;
    private String userName;
    /** 手机号，用于同一会议下按手机号防重复报名 */
    private String phone;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
