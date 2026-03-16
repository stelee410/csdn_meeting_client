package com.csdn.meeting.domain.repository.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 埋点事件仓储接口
 */
public interface AnalyticsEventRepository {

    /**
     * 保存埋点事件
     */
    void save(AnalyticsEvent event);

    /**
     * 批量保存埋点事件
     */
    void saveBatch(List<AnalyticsEvent> events);

    /**
     * 根据事件ID查询
     */
    AnalyticsEvent findByEventId(String eventId);

    /**
     * 根据用户ID和时间范围查询事件列表
     */
    List<AnalyticsEvent> findByUserIdAndTimeRange(String userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据事件类型和时间范围查询事件列表
     */
    List<AnalyticsEvent> findByEventTypeAndTimeRange(String eventType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据会议ID查询相关事件
     */
    List<AnalyticsEvent> findByMeetingId(String meetingId);

    /**
     * 统计指定时间范围内的事件数量
     */
    long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定事件类型在指定时间范围内的数量
     */
    long countByEventTypeAndTimeRange(String eventType, LocalDateTime startTime, LocalDateTime endTime);
}
