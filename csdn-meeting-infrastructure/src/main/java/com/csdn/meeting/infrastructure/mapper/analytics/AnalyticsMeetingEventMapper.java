package com.csdn.meeting.infrastructure.mapper.analytics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsMeetingEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会议业务事件扩展Mapper接口
 */
@Mapper
public interface AnalyticsMeetingEventMapper extends BaseMapper<AnalyticsMeetingEventPO> {

    /**
     * 根据事件ID查询
     */
    @Select("SELECT * FROM analytics_meeting_event WHERE event_id = #{eventId}")
    AnalyticsMeetingEventPO selectByEventId(@Param("eventId") String eventId);

    /**
     * 根据会议ID查询相关事件
     */
    @Select("SELECT * FROM analytics_meeting_event WHERE meeting_id = #{meetingId} ORDER BY created_at DESC")
    List<AnalyticsMeetingEventPO> selectByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 根据会议ID和操作类型查询事件
     */
    @Select("SELECT * FROM analytics_meeting_event WHERE meeting_id = #{meetingId} AND action_type = #{actionType} ORDER BY created_at DESC")
    List<AnalyticsMeetingEventPO> selectByMeetingIdAndActionType(
            @Param("meetingId") String meetingId,
            @Param("actionType") String actionType);

    /**
     * 根据主办方ID查询事件
     */
    @Select("SELECT * FROM analytics_meeting_event WHERE organizer_id = #{organizerId} ORDER BY created_at DESC")
    List<AnalyticsMeetingEventPO> selectByOrganizerId(@Param("organizerId") String organizerId);
}
