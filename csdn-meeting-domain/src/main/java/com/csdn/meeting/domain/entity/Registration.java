package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;

/**
 * 报名实体（agent.prd §1.3）
 * 业务规则：仅 Meeting.status = PUBLISHED 时可对报名进行审核操作
 */
public class Registration extends BaseEntity {

    private Long meetingId;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String position;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime auditedAt;
    private String auditRemark;

    public enum RegistrationStatus {
        PENDING,
        APPROVED,
        REJECTED
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

    // ---- getters / setters ----

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
}
