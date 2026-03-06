package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for t_meeting_favorite, aligned with Flyway V2 DDL.
 */
@Entity
@Table(name = "t_meeting_favorite",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_meeting", columnNames = {"user_id", "meeting_id"}))
public class MeetingFavoritePO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
