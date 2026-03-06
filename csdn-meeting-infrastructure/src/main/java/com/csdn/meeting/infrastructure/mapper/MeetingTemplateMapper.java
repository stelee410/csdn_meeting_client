package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.MeetingTemplate;
import com.csdn.meeting.infrastructure.po.MeetingTemplatePO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingTemplateMapper {

    MeetingTemplateMapper INSTANCE = Mappers.getMapper(MeetingTemplateMapper.class);

    MeetingTemplatePO toPO(MeetingTemplate template);

    MeetingTemplate toEntity(MeetingTemplatePO po);
}
