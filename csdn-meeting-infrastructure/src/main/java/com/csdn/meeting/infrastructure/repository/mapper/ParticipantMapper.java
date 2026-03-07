package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 参与者Mapper接口
 */
@Mapper
public interface ParticipantMapper extends BaseMapper<ParticipantPO> {

    /**
     * 根据会议ID查询参与者列表
     */
    @Select("SELECT * FROM t_participant WHERE meeting_id = #{meetingId} AND is_deleted = 0")
    List<ParticipantPO> selectByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 根据会议ID和用户ID查询参与者
     */
    @Select("SELECT * FROM t_participant WHERE meeting_id = #{meetingId} AND user_id = #{userId} AND is_deleted = 0 LIMIT 1")
    Optional<ParticipantPO> selectByMeetingIdAndUserId(@Param("meetingId") String meetingId, @Param("userId") Long userId);
}
