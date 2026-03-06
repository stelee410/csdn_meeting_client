package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_meeting_rights", indexes = @Index(name = "idx_meeting_id", columnList = "meeting_id"))
public class MeetingRightsPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "rights_type", length = 50)
    private String rightsType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "active_time")
    private LocalDateTime activeTime;

    @Column(name = "order_no", length = 100)
    private String orderNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

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
