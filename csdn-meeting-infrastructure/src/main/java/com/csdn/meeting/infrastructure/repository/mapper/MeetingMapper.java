package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 会议Mapper接口
 * 继承BaseMapper获得基本CRUD能力
 */
@Mapper
public interface MeetingMapper extends BaseMapper<MeetingPO> {

    /**
     * 发布会议
     */
    @Update("UPDATE t_meeting SET status = 'PUBLISHED', update_time = NOW() " +
            "WHERE meeting_id = #{meetingId} AND status = 'CREATED' AND is_deleted = 0")
    int publish(@Param("meetingId") String meetingId);

    /**
     * 开始会议
     */
    @Update("UPDATE t_meeting SET status = 'ONGOING', update_time = NOW() " +
            "WHERE meeting_id = #{meetingId} AND status = 'PUBLISHED' AND is_deleted = 0")
    int start(@Param("meetingId") String meetingId);

    /**
     * 结束会议
     */
    @Update("UPDATE t_meeting SET status = 'ENDED', update_time = NOW() " +
            "WHERE meeting_id = #{meetingId} AND status = 'ONGOING' AND is_deleted = 0")
    int end(@Param("meetingId") String meetingId);

    /**
     * 取消会议
     */
    @Update("UPDATE t_meeting SET status = 'CANCELLED', update_time = NOW() " +
            "WHERE meeting_id = #{meetingId} AND status != 'ENDED' AND is_deleted = 0")
    int cancel(@Param("meetingId") String meetingId);
}
