package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingBillPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MeetingBillPOMapper extends BaseMapper<MeetingBillPO> {

    List<MeetingBillPO> selectByMeetingId(@Param("meetingId") Long meetingId);
}
