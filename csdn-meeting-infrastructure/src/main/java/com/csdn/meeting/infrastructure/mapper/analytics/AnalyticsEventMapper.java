package com.csdn.meeting.infrastructure.mapper.analytics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 埋点事件Mapper接口
 */
@Mapper
public interface AnalyticsEventMapper extends BaseMapper<AnalyticsEventPO> {

    /**
     * 根据事件ID查询
     */
    @Select("SELECT * FROM analytics_event WHERE event_id = #{eventId}")
    AnalyticsEventPO selectByEventId(@Param("eventId") String eventId);

    /**
     * 根据用户ID和时间范围查询
     */
    @Select("SELECT * FROM analytics_event WHERE user_id = #{userId} " +
            "AND occurred_at >= #{startTime} AND occurred_at <= #{endTime} " +
            "ORDER BY occurred_at DESC")
    List<AnalyticsEventPO> selectByUserIdAndTimeRange(
            @Param("userId") String userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根据事件类型和时间范围查询
     */
    @Select("SELECT * FROM analytics_event WHERE event_type = #{eventType} " +
            "AND occurred_at >= #{startTime} AND occurred_at <= #{endTime} " +
            "ORDER BY occurred_at DESC")
    List<AnalyticsEventPO> selectByEventTypeAndTimeRange(
            @Param("eventType") String eventType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 根据会议ID查询相关事件(通过关联表)
     */
    @Select("SELECT e.* FROM analytics_event e " +
            "INNER JOIN analytics_meeting_event m ON e.event_id = m.event_id " +
            "WHERE m.meeting_id = #{meetingId} " +
            "ORDER BY e.occurred_at DESC")
    List<AnalyticsEventPO> selectByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 统计指定时间范围内的事件数量
     */
    @Select("SELECT COUNT(*) FROM analytics_event " +
            "WHERE occurred_at >= #{startTime} AND occurred_at <= #{endTime}")
    Long countByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定事件类型在指定时间范围内的数量
     */
    @Select("SELECT COUNT(*) FROM analytics_event " +
            "WHERE event_type = #{eventType} " +
            "AND occurred_at >= #{startTime} AND occurred_at <= #{endTime}")
    Long countByEventTypeAndTimeRange(
            @Param("eventType") String eventType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
