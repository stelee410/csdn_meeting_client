package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.infrastructure.po.RegistrationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationPOMapper extends BaseMapper<RegistrationPO> {

    IPage<RegistrationPO> selectPageByMeetingIdAndStatus(Page<RegistrationPO> page,
                                                         @Param("meetingId") Long meetingId,
                                                         @Param("status") String status);

    IPage<RegistrationPO> selectPageByMeetingId(Page<RegistrationPO> page, @Param("meetingId") Long meetingId);

    RegistrationPO selectByUserIdAndMeetingId(@Param("userId") String userId, @Param("meetingId") Long meetingId);

    List<RegistrationPO> selectByMeetingIdAndPhone(@Param("meetingId") Long meetingId, @Param("phone") String phone);

    IPage<RegistrationPO> selectPageByUserIdAndMeetingStatusIn(Page<RegistrationPO> page,
                                                               @Param("userId") String userId,
                                                               @Param("statusCodes") List<Integer> statusCodes);
}
