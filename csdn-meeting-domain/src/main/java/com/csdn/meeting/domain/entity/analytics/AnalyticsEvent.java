package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 埋点事件实体 - 存储所有埋点事件的核心信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsEvent extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件唯一标识(UUID)
     */
    private String eventId;

    /**
     * 事件类型(如:meeting_create, tag_subscribe)
     */
    private String eventType;

    /**
     * 事件类别(client/operation/mobile)
     */
    private String eventCategory;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户类型(1=普通用户, 2=运营人员)
     */
    private Integer userType = 1;

    /**
     * 匿名用户标识
     */
    private String anonymousId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 设备标识
     */
    private String deviceId;

    /**
     * 平台(web/ios/android/miniapp)
     */
    private String platform;

    /**
     * 应用版本
     */
    private String appVersion;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理字符串
     */
    private String userAgent;

    /**
     * 事件发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 事件属性(动态扩展)
     */
    private Map<String, Object> properties = new HashMap<>();

    /**
     * 构建器模式创建事件
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 设置属性值
     */
    public AnalyticsEvent property(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    /**
     * 设置多个属性
     */
    public AnalyticsEvent properties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    /**
     * 自动生成事件ID
     */
    public void generateEventId() {
        if (this.eventId == null) {
            this.eventId = UUID.randomUUID().toString().replace("-", "");
        }
    }

    /**
     * 设置默认发生时间
     */
    public void setDefaultOccurredAt() {
        if (this.occurredAt == null) {
            this.occurredAt = LocalDateTime.now();
        }
    }

    public static class Builder {
        private AnalyticsEvent event = new AnalyticsEvent();

        public Builder eventId(String eventId) {
            event.setEventId(eventId);
            return this;
        }

        public Builder eventType(String eventType) {
            event.setEventType(eventType);
            return this;
        }

        public Builder eventCategory(String eventCategory) {
            event.setEventCategory(eventCategory);
            return this;
        }

        public Builder userId(String userId) {
            event.setUserId(userId);
            return this;
        }

        public Builder userType(Integer userType) {
            event.setUserType(userType);
            return this;
        }

        public Builder anonymousId(String anonymousId) {
            event.setAnonymousId(anonymousId);
            return this;
        }

        public Builder sessionId(String sessionId) {
            event.setSessionId(sessionId);
            return this;
        }

        public Builder deviceId(String deviceId) {
            event.setDeviceId(deviceId);
            return this;
        }

        public Builder platform(String platform) {
            event.setPlatform(platform);
            return this;
        }

        public Builder appVersion(String appVersion) {
            event.setAppVersion(appVersion);
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            event.setIpAddress(ipAddress);
            return this;
        }

        public Builder userAgent(String userAgent) {
            event.setUserAgent(userAgent);
            return this;
        }

        public Builder occurredAt(LocalDateTime occurredAt) {
            event.setOccurredAt(occurredAt);
            return this;
        }

        public Builder property(String key, Object value) {
            event.property(key, value);
            return this;
        }

        public AnalyticsEvent build() {
            event.generateEventId();
            event.setDefaultOccurredAt();
            return event;
        }
    }

    // 事件类型常量
    public static final class EventTypes {
        // 会议相关
        public static final String MEETING_CREATE = "meeting_create";
        public static final String MEETING_SUBMIT = "meeting_submit";
        public static final String MEETING_PUBLISH = "meeting_publish";
        public static final String MEETING_REGISTER = "meeting_register";
        public static final String MEETING_CHECKIN = "meeting_checkin";
        public static final String MEETING_FAVORITE = "meeting_favorite";
        public static final String MEETING_CLICK = "meeting_click";
        public static final String MEETING_VIEW_SWITCH = "meeting_view_switch";

        // 列表筛选
        public static final String MEETING_LIST_FILTER = "meeting_list_filter";

        // 标签相关
        public static final String TAG_SUBSCRIBE = "tag_subscribe";
        public static final String TAG_UNSUBSCRIBE = "tag_unsubscribe";

        // 移动端相关
        public static final String MOBILE_HOME_EXPOSURE = "mobile_home_exposure";
        public static final String MOBILE_CREATE_ENTRY_CLICK = "mobile_create_entry_click";
        public static final String MOBILE_MY_EVENTS_CLICK = "mobile_my_events_click";
        public static final String MOBILE_FAVORITES_TAB_CLICK = "mobile_favorites_tab_click";
        public static final String MOBILE_CHECKIN_SCAN = "mobile_checkin_scan";
        public static final String MOBILE_CHANNEL_ADD = "mobile_channel_add";

        // 运营端相关
        public static final String MEETING_AUDIT_APPROVE = "meeting_audit_approve";
        public static final String MEETING_AUDIT_REJECT = "meeting_audit_reject";
        public static final String MEETING_TAKEDOWN = "meeting_takedown";
        public static final String TEMPLATE_CREATE = "template_create";
        public static final String TEMPLATE_UPDATE = "template_update";
        public static final String TEMPLATE_DELETE = "template_delete";
        public static final String TEMPLATE_LIST = "template_list";
        public static final String TEMPLATE_UNLIST = "template_unlist";
        public static final String DASHBOARD_VIEW = "dashboard_view";
        public static final String DASHBOARD_STATS_EXPOSE = "dashboard_stats_expose";
        public static final String DASHBOARD_PROMOTED_MEETINGS_VIEW = "dashboard_promoted_meetings_view";
    }

    // 事件类别常量
    public static final class Categories {
        public static final String CLIENT = "client";
        public static final String OPERATION = "operation";
        public static final String MOBILE = "mobile";
    }

    // 用户类型常量
    public static final class UserTypes {
        public static final int NORMAL_USER = 1;
        public static final int OPERATOR = 2;
    }

    // 平台常量
    public static final class Platforms {
        public static final String WEB = "web";
        public static final String IOS = "ios";
        public static final String ANDROID = "android";
        public static final String MINIAPP = "miniapp";
    }
}
