package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 会议业务事件扩展实体 - 针对会议业务的专用扩展表
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsMeetingEvent extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联事件ID
     */
    private String eventId;

    /**
     * 会议ID
     */
    private String meetingId;

    /**
     * 会议标题
     */
    private String meetingTitle;

    /**
     * 主办方ID
     */
    private String organizerId;

    /**
     * 操作类型
     */
    private String actionType;

    /**
     * 来源渠道
     */
    private String source;

    /**
     * 引荐页面
     */
    private String referrer;
}
