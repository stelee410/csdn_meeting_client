package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;

/**
 * 会议收藏实体（agent.prd §1.4）
 * 业务规则：UK(userId, meetingId)
 */
public class MeetingFavorite extends BaseEntity {

    private Long userId;
    private Long meetingId;
    private LocalDateTime createdAt;

    // ---- getters / setters ----

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
