package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.MeetingTag;
import com.csdn.meeting.infrastructure.po.MeetingTagPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会议标签关联转换器
 * 用于 MeetingTagPO 和 MeetingTag 实体之间的转换
 */
@Mapper
public interface MeetingTagConverter {

    MeetingTagConverter INSTANCE = Mappers.getMapper(MeetingTagConverter.class);

    /**
     * PO 转 Entity
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "meetingId", target = "meetingId"),
            @Mapping(source = "tagId", target = "tagId"),
            @Mapping(source = "createTime", target = "createTime"),
            @Mapping(source = "createBy", target = "createBy"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "updateBy", target = "updateBy")
    })
    MeetingTag poToEntity(MeetingTagPO meetingTagPO);

    /**
     * Entity 转 PO
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "meetingId", target = "meetingId"),
            @Mapping(source = "tagId", target = "tagId"),
            @Mapping(source = "createTime", target = "createTime"),
            @Mapping(source = "createBy", target = "createBy"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "updateBy", target = "updateBy")
    })
    MeetingTagPO entityToPo(MeetingTag meetingTag);

    /**
     * PO列表转Entity列表
     */
    List<MeetingTag> poListToEntityList(List<MeetingTagPO> meetingTagPOList);

    /**
     * Entity列表转PO列表
     */
    List<MeetingTagPO> entityListToPoList(List<MeetingTag> meetingTagList);
}
