package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 标签订阅DTO
 */
@Schema(description = "标签订阅信息")
public class TagSubscriptionDTO {

    @Schema(description = "订阅记录ID", example = "1")
    private Long id;

    @Schema(description = "标签ID", example = "100")
    private Long tagId;

    @Schema(description = "标签名称", example = "Java")
    private String tagName;

    @Schema(description = "是否已订阅", example = "true")
    private Boolean isSubscribed;

    @Schema(description = "是否接收站内信通知", example = "true")
    private Boolean notifySite;

    @Schema(description = "订阅时间", example = "2024-03-01T10:00:00")
    private LocalDateTime subscribedAt;

    @Schema(description = "上次通知时间", example = "2024-03-10T15:30:00")
    private LocalDateTime lastNotifyAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(Boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public Boolean getNotifySite() {
        return notifySite;
    }

    public void setNotifySite(Boolean notifySite) {
        this.notifySite = notifySite;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }

    public LocalDateTime getLastNotifyAt() {
        return lastNotifyAt;
    }

    public void setLastNotifyAt(LocalDateTime lastNotifyAt) {
        this.lastNotifyAt = lastNotifyAt;
    }
}
