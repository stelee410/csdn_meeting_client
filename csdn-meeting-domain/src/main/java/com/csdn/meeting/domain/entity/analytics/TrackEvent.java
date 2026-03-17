package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 埋点事件领域实体
 * 统一封装前端上报的埋点数据
 */
@Data
public class TrackEvent {

    /**
     * 事件唯一标识(UUID)
     */
    private String eventId;

    /**
     * 模块(如: meeting_list, audit, dashboard)
     */
    private String module;

    /**
     * 动作(如: click_view_switch, click_filter)
     */
    private String action;

    /**
     * 事件类型(client/operation/mobile)
     */
    private String eventType;

    /**
     * 用户ID
     */
    private String userId;

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
     * 用户代理
     */
    private String userAgent;

    /**
     * 事件发生时间(前端上报)
     */
    private LocalDateTime occurredAt;

    /**
     * 后端接收时间
     */
    private LocalDateTime receivedAt;

    /**
     * 事件属性(JSON格式)
     */
    private Map<String, Object> properties;

    /**
     * 事件类型常量
     */
    public static final class EventTypes {
        public static final String CLIENT = "client";
        public static final String OPERATION = "operation";
        public static final String MOBILE = "mobile";
    }

    /**
     * 模块常量
     */
    public static final class Modules {
        public static final String MEETING_LIST = "meeting_list";
        public static final String MEETING_DETAIL = "meeting_detail";
        public static final String REGISTRATION_FORM = "registration_form";
        public static final String CHECKIN = "checkin";
        public static final String AUDIT = "audit";
        public static final String DASHBOARD = "dashboard";
        public static final String APP_HOME = "app_home";
        public static final String APP_MY = "app_my";
        public static final String APP_CHANNEL = "app_channel";
        public static final String APP_CHECKIN = "app_checkin";
    }

    /**
     * 动作常量
     */
    public static final class Actions {
        // 会议列表相关
        public static final String CLICK_VIEW_SWITCH = "click_view_switch";
        public static final String CLICK_FILTER = "click_filter";
        public static final String CLICK_MEETING_CARD = "click_meeting_card";

        // 会议详情相关
        public static final String CLICK_TAG_SUBSCRIBE = "click_tag_subscribe";
        public static final String CLICK_REGISTER = "click_register";
        public static final String CLICK_SHARE = "click_share";

        // 报名表单相关
        public static final String CLICK_SUBMIT = "click_submit";

        // 签到相关
        public static final String SCAN_RESULT = "scan_result";

        // 审核相关
        public static final String AUDIT_APPROVE = "audit_approve";
        public static final String AUDIT_REJECT = "audit_reject";

        // 数据看板相关
        public static final String VIEW_DASHBOARD = "view_dashboard";
        public static final String EXPOSE_COMPANY_STATS = "expose_company_stats";
        public static final String VIEW_PROMOTED_MEETINGS = "view_promoted_meetings";
        public static final String CLICK_PROMOTED_MEETING = "click_promoted_meeting";

        // App首页相关
        public static final String HOME_EXPOSURE = "home_exposure";
        public static final String CLICK_CREATE_ENTRY = "click_create_entry";

        // App我的相关
        public static final String CLICK_MY_MEETINGS = "click_my_meetings";
        public static final String CLICK_FAVORITES_TAB = "click_favorites_tab";

        // App频道相关
        public static final String CHANNEL_ADD = "channel_add";

        // App签到相关
        public static final String CHECKIN_SCAN = "checkin_scan";
    }
}
