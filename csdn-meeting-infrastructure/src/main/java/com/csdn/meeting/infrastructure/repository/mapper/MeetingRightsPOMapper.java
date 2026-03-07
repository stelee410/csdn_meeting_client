package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingRightsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MeetingRightsPOMapper extends BaseMapper<MeetingRightsPO> {

    MeetingRightsPO selectFirstByMeetingIdAndStatus(@Param("meetingId") Long meetingId,
                                                    @Param("status") String status);

    List<MeetingRightsPO> selectByMeetingId(@Param("meetingId") Long meetingId);
}
