package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;

/**
 * 会议数据高阶权益记录。
 * 权益类型 DATA_PREMIUM：解锁用户画像 + 简报高阶数据。
 */
public class MeetingRights {

    private Long id;
    private Long meetingId;
    private String rightsType;  // DATA_PREMIUM
    private String status;      // ACTIVE, INACTIVE
    private LocalDateTime activeTime;
    private String orderNo;
    private LocalDateTime createdAt;

    public static final String RIGHTS_TYPE_DATA_PREMIUM = "DATA_PREMIUM";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public String getRightsType() { return rightsType; }
    public void setRightsType(String rightsType) { this.rightsType = rightsType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getActiveTime() { return activeTime; }
    public void setActiveTime(LocalDateTime activeTime) { this.activeTime = activeTime; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
