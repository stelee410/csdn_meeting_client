package com.csdn.meeting.domain.event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议发布领域事件
 * 当会议状态从 PENDING_REVIEW 变为 PUBLISHED 时触发
 * 用于通知订阅了该会议标签的用户
 */
public class MeetingPublishedEvent {

    /**
     * 会议业务ID
     */
    private String meetingId;

    /**
     * 会议标题
     */
    private String title;

    /**
     * 会议关联的标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 发布者ID
     */
    private Long publisherId;

    public MeetingPublishedEvent() {
    }

    public MeetingPublishedEvent(String meetingId, String title, List<Long> tagIds, 
                                  LocalDateTime publishedAt, Long publisherId) {
        this.meetingId = meetingId;
        this.title = title;
        this.tagIds = tagIds;
        this.publishedAt = publishedAt;
        this.publisherId = publisherId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }
}
