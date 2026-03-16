package com.csdn.meeting.domain.entity.analytics;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 移动端埋点事件实体 - 记录移动端特有的事件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsMobileEvent extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联事件ID
     */
    private String eventId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 来源渠道
     */
    private String source;

    /**
     * 会议ID
     */
    private String meetingId;

    /**
     * 操作结果(success/fail)
     */
    private String result;

    /**
     * 额外数据(JSON格式)
     */
    @JsonRawValue
    private String extraData;

    /**
     * 发生时间
     */
    private LocalDateTime occurredAt;

    // 事件名称常量
    public static final class EventNames {
        public static final String HOME_EXPOSURE = "mobile_home_exposure";
        public static final String CREATE_ENTRY_CLICK = "mobile_create_entry_click";
        public static final String MY_EVENTS_CLICK = "mobile_my_events_click";
        public static final String FAVORITES_TAB_CLICK = "mobile_favorites_tab_click";
        public static final String CHECKIN_SCAN = "mobile_checkin_scan";
        public static final String CHANNEL_ADD = "mobile_channel_add";
    }

    // 结果常量
    public static final class Results {
        public static final String SUCCESS = "success";
        public static final String FAIL = "fail";
    }
}
