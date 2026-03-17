package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 埋点事件上报命令
 * 用于接收前端上报的埋点数据
 */
@Schema(description = "埋点事件上报命令")
public class TrackEventCommand {

    @Schema(description = "事件唯一标识(UUID)，由前端生成", example = "550e8400-e29b-41d4-a716-446655440000")
    private String eventId;

    @Schema(description = "模块(如: meeting_list, audit, dashboard)", example = "meeting_list", required = true)
    private String module;

    @Schema(description = "动作(如: click_view_switch, click_filter)", example = "click_view_switch", required = true)
    private String action;

    @Schema(description = "事件类型(client/operation/mobile)", example = "client", required = true)
    private String eventType;

    @Schema(description = "用户ID", example = "12345")
    private String userId;

    @Schema(description = "匿名用户标识", example = "anon_abc123")
    private String anonymousId;

    @Schema(description = "会话ID", example = "sess_xyz789")
    private String sessionId;

    @Schema(description = "设备标识", example = "device_123456")
    private String deviceId;

    @Schema(description = "平台(web/ios/android/miniapp)", example = "web")
    private String platform;

    @Schema(description = "应用版本", example = "1.0.0")
    private String appVersion;

    @Schema(description = "事件发生时间(ISO 8601格式)", example = "2026-03-17T10:30:00")
    private LocalDateTime occurredAt;

    @Schema(description = "事件属性(JSON对象)", example = "{\"target_view\": \"card\", \"meeting_id\": \"m_123\"}")
    private Map<String, Object> properties;

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

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
