package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.analytics.TrackEventPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 埋点事件 MyBatis-Plus Mapper
 */
@Mapper
public interface TrackEventPOMapper extends BaseMapper<TrackEventPO> {

    @Select("<script>"
            + "SELECT COUNT(*) FROM analytics_track_event"
            + " WHERE module = #{module} AND action = #{action}"
            + " AND JSON_EXTRACT(properties, '$.meetingId') = #{meetingId}"
            + "<if test='date != null'> AND DATE(occurred_at) = #{date}</if>"
            + "</script>")
    long countByModuleActionAndMeetingId(@Param("module") String module,
                                         @Param("action") String action,
                                         @Param("meetingId") long meetingId,
                                         @Param("date") String date);

    @Select("<script>"
            + "SELECT COUNT(DISTINCT COALESCE(user_id, anonymous_id)) FROM analytics_track_event"
            + " WHERE module = #{module} AND action = #{action}"
            + " AND JSON_EXTRACT(properties, '$.meetingId') = #{meetingId}"
            + "<if test='date != null'> AND DATE(occurred_at) = #{date}</if>"
            + "</script>")
    long countDistinctVisitors(@Param("module") String module,
                               @Param("action") String action,
                               @Param("meetingId") long meetingId,
                               @Param("date") String date);

    @Select("SELECT DATE(occurred_at) AS stat_date,"
            + " SUM(CASE WHEN module = 'meeting_list' AND action = 'impression' THEN 1 ELSE 0 END) AS pv,"
            + " SUM(CASE WHEN module = 'meeting_detail' AND action = 'page_view' THEN 1 ELSE 0 END) AS clicks"
            + " FROM analytics_track_event"
            + " WHERE JSON_EXTRACT(properties, '$.meetingId') = #{meetingId}"
            + " AND DATE(occurred_at) BETWEEN #{fromDate} AND #{toDate}"
            + " GROUP BY DATE(occurred_at)"
            + " ORDER BY DATE(occurred_at)")
    List<Map<String, Object>> selectDailyStats(@Param("meetingId") long meetingId,
                                                @Param("fromDate") String fromDate,
                                                @Param("toDate") String toDate);
}

