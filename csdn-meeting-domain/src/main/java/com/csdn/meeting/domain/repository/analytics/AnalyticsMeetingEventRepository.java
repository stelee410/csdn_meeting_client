package com.csdn.meeting.domain.repository.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsMeetingEvent;

import java.util.List;

/**
 * 会议业务事件扩展仓储接口
 */
public interface AnalyticsMeetingEventRepository {

    /**
     * 保存会议事件
     */
    void save(AnalyticsMeetingEvent event);

    /**
     * 根据事件ID查询
     */
    AnalyticsMeetingEvent findByEventId(String eventId);

    /**
     * 根据会议ID查询相关事件
     */
    List<AnalyticsMeetingEvent> findByMeetingId(String meetingId);

    /**
     * 根据会议ID和操作类型查询事件
     */
    List<AnalyticsMeetingEvent> findByMeetingIdAndActionType(String meetingId, String actionType);

    /**
     * 根据主办方ID查询事件
     */
    List<AnalyticsMeetingEvent> findByOrganizerId(String organizerId);
}
