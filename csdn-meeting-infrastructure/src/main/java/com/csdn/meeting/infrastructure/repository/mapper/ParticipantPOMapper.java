package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParticipantPOMapper extends BaseMapper<ParticipantPO> {

    List<ParticipantPO> selectByMeetingId(@Param("meetingId") String meetingId);

    ParticipantPO selectByMeetingIdAndUserId(@Param("meetingId") String meetingId,
                                            @Param("userId") String userId);
}
