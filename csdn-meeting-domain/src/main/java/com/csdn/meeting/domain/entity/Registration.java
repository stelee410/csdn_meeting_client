package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;

/**
 * 报名实体（agent.prd §1.3）
 * 业务规则：仅 Meeting.status = PUBLISHED 时可对报名进行审核操作
 * 
 * V1.2更新：增加签到功能、取消报名、状态扩展
 */
public class Registration extends BaseEntity {

    private Long meetingId;
    private String userId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String position;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime auditedAt;
    private String auditRemark;
    
    // V1.2新增字段 - 签到相关
    private LocalDateTime checkinTime;
    private LocalDateTime cancelTime;

    public enum RegistrationStatus {
        PENDING("待审核"),
        APPROVED("已报名"),
        REJECTED("已拒绝"),
        CANCELLED("已取消"),
        CHECKED_IN("已签到");
        
        private final String displayName;
        
        RegistrationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 审核通过：PENDING -> APPROVED
     * 调用方需确保 Meeting.status = PUBLISHED
     */
    public void approve() {
        if (this.status != RegistrationStatus.PENDING) {
            throw new IllegalStateException("只有待审核状态才能审核通过，当前状态: " + this.status);
        }
        this.status = RegistrationStatus.APPROVED;
        this.auditedAt = LocalDateTime.now();
        this.auditRemark = null;
    }

    /**
     * 审核拒绝：PENDING -> REJECTED
     * 调用方需确保 Meeting.status = PUBLISHED
     *
     * @param remark 拒绝备注，可为 null
     */
    public void reject(String remark) {
        if (this.status != RegistrationStatus.PENDING) {
            throw new IllegalStateException("只有待审核状态才能审核拒绝，当前状态: " + this.status);
        }
        this.status = RegistrationStatus.REJECTED;
        this.auditedAt = LocalDateTime.now();
        this.auditRemark = remark;
    }

    /**
     * 取消报名：PENDING/APPROVED -> CANCELLED
     * 用户主动取消报名，需在签到前操作
     */
    public void cancel() {
        if (this.status != RegistrationStatus.PENDING && this.status != RegistrationStatus.APPROVED) {
            throw new IllegalStateException("只有待审核或已报名状态才能取消，当前状态: " + this.status);
        }
        if (this.status == RegistrationStatus.CHECKED_IN) {
            throw new IllegalStateException("已完成签到，无法取消报名");
        }
        this.status = RegistrationStatus.CANCELLED;
        this.cancelTime = LocalDateTime.now();
    }

    /**
     * 签到：APPROVED -> CHECKED_IN
     * 参会者在会议现场完成签到
     */
    public void checkin() {
        if (this.status != RegistrationStatus.APPROVED) {
            throw new IllegalStateException("只有已报名状态才能签到，当前状态: " + this.status);
        }
        this.status = RegistrationStatus.CHECKED_IN;
        this.checkinTime = LocalDateTime.now();
    }

    /**
     * 是否可以取消报名
     */
    public boolean canCancel() {
        return (this.status == RegistrationStatus.PENDING || this.status == RegistrationStatus.APPROVED)
                && this.status != RegistrationStatus.CHECKED_IN;
    }

    /**
     * 是否可以签到
     */
    public boolean canCheckin() {
        return this.status == RegistrationStatus.APPROVED;
    }

    /**
     * 是否为有效报名（未被取消/拒绝）
     */
    public boolean isValid() {
        return this.status == RegistrationStatus.PENDING 
                || this.status == RegistrationStatus.APPROVED 
                || this.status == RegistrationStatus.CHECKED_IN;
    }

    // ---- getters / setters ----

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getAuditedAt() {
        return auditedAt;
    }

    public void setAuditedAt(LocalDateTime auditedAt) {
        this.auditedAt = auditedAt;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    // V1.2新增字段的getter/setter
    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }
}
