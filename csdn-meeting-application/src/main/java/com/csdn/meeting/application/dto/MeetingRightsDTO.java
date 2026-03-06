package com.csdn.meeting.application.dto;

import java.time.LocalDateTime;

/**
 * 权益状态（agent.prd §2.6）
 */
public class MeetingRightsDTO {

    private String rightsType;
    private String status;
    private LocalDateTime activeTime;

    public String getRightsType() { return rightsType; }
    public void setRightsType(String rightsType) { this.rightsType = rightsType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getActiveTime() { return activeTime; }
    public void setActiveTime(LocalDateTime activeTime) { this.activeTime = activeTime; }
}
