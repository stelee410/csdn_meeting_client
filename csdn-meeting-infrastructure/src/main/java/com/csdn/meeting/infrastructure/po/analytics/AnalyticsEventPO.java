package com.csdn.meeting.infrastructure.po.analytics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 埋点事件持久化对象
 */
@Data
@TableName("analytics_event")
public class AnalyticsEventPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("event_type")
    private String eventType;

    @TableField("event_category")
    private String eventCategory;

    @TableField("user_id")
    private String userId;

    @TableField("user_type")
    private Integer userType;

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

    @TableField("created_at")
    private LocalDateTime createdAt;
}
