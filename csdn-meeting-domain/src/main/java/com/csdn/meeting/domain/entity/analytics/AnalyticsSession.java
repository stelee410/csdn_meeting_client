package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话跟踪实体 - 用户会话信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsSession extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 地理位置
     */
    private String geoLocation;

    /**
     * 会话开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 会话结束时间
     */
    private LocalDateTime endedAt;

    /**
     * 页面浏览次数
     */
    private Integer pageViews = 0;

    /**
     * 事件触发次数
     */
    private Integer eventsCount = 0;
}
