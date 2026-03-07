package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MeetingAgendaItemPOMapper extends BaseMapper<MeetingAgendaItemPO> {

    List<MeetingAgendaItemPO> selectByMeetingIdOrderBySortOrderAsc(@Param("meetingId") Long meetingId);

    void deleteByMeetingId(@Param("meetingId") Long meetingId);
}
