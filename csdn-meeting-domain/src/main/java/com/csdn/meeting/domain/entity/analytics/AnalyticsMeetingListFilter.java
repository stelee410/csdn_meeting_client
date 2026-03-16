package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会议列表筛选埋点实体 - 记录会议列表的筛选操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsMeetingListFilter extends AnalyticsBaseEntity implements Serializable {

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
     * 会议形式(ONLINE/OFFLINE/HYBRID)
     */
    private String format;

    /**
     * 会议类型
     */
    private String meetingType;

    /**
     * 会议场景
     */
    private String scene;

    /**
     * 时间范围
     */
    private String timeRange;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 返回结果数
     */
    private Integer resultCount;

    /**
     * 发生时间
     */
    private LocalDateTime occurredAt;
}
