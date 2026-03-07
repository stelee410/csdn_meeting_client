package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.PromotionConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PromotionConfigPOMapper extends BaseMapper<PromotionConfigPO> {

    PromotionConfigPO selectFirstByMeetingIdOrderByCreatedAtDesc(@Param("meetingId") Long meetingId);
}
