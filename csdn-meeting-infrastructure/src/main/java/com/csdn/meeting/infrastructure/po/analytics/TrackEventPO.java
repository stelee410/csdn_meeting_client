package com.csdn.meeting.infrastructure.po.analytics;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus entity for analytics_track_event
 */
@TableName("analytics_track_event")
public class TrackEventPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("module")
    private String module;

    @TableField("action")
    private String action;

    @TableField("event_type")
    private String eventType;

    @TableField("user_id")
    private String userId;

    @TableField("anonymous_id")
    private String anonymousId;

    @TableField("session_id")
    private String sessionId;

    @TableField("device_id")
    private String deviceId;

    @TableField("platform")
    private String platform;

    @TableField("app_version")
    private String appVersion;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("user_agent")
    private String userAgent;

    @TableField("occurred_at")
    private LocalDateTime occurredAt;

    @TableField("received_at")
    private LocalDateTime receivedAt;

    @TableField("properties")
    private String properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public void setAnonymousId(String anonymousId) {
        this.anonymousId = anonymousId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
