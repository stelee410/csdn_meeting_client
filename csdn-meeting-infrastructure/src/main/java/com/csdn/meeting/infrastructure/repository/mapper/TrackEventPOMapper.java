package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.analytics.TrackEventPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 埋点事件 MyBatis-Plus Mapper
 */
@Mapper
public interface TrackEventPOMapper extends BaseMapper<TrackEventPO> {
}
