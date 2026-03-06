package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for t_registration, aligned with Flyway V2 DDL.
 */
@Entity
@Table(name = "t_registration",
        indexes = @Index(name = "idx_meeting_status", columnList = "meeting_id, status"),
        uniqueConstraints = @UniqueConstraint(name = "uk_meeting_user", columnNames = {"meeting_id", "user_id"}))
public class RegistrationPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "company", length = 200)
    private String company;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "audited_at")
    private LocalDateTime auditedAt;

    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    @PrePersist
    public void prePersist() {
        if (this.registeredAt == null) {
            this.registeredAt = LocalDateTime.now();
        }
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public LocalDateTime getAuditedAt() { return auditedAt; }
    public void setAuditedAt(LocalDateTime auditedAt) { this.auditedAt = auditedAt; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
}
