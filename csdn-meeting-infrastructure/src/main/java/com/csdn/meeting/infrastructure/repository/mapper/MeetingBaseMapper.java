package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 会议基础Mapper接口（用于原有Meeting实体的CRUD）
 */
@Mapper
public interface MeetingBaseMapper extends BaseMapper<MeetingPO> {

    /**
     * 根据会议ID查询
     */
    @Select("SELECT * FROM t_meeting WHERE meeting_id = #{meetingId} AND is_deleted = 0 LIMIT 1")
    Optional<MeetingPO> selectByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 根据创建者ID查询
     */
    @Select("SELECT * FROM t_meeting WHERE creator_id = #{creatorId} AND is_deleted = 0")
    List<MeetingPO> selectByCreatorId(@Param("creatorId") String creatorId);
}
