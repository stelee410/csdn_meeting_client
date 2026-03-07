package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingTemplatePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingTemplatePOMapper extends BaseMapper<MeetingTemplatePO> {

    List<MeetingTemplatePO> selectByIsActiveTrueOrderBySortOrderAsc();
}
