package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingScene;
import com.csdn.meeting.domain.valueobject.MeetingType;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会议转换器
 * 用于 MeetingPO 和 Meeting 实体之间的转换
 */
@Mapper
public interface MeetingConverter {

    MeetingConverter INSTANCE = Mappers.getMapper(MeetingConverter.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "intToMeetingStatus")
    @Mapping(target = "format", source = "format", qualifiedByName = "stringToFormat")
    @Mapping(target = "scene", source = "scene")
    @Mapping(target = "meetingId", expression = "java(meetingPO.getId() != null ? String.valueOf(meetingPO.getId()) : null)")
    @Mapping(target = "scheduleDays", ignore = true)
    @Mapping(target = "meetingType", ignore = true)
    Meeting poToEntity(MeetingPO meetingPO);

    @Mapping(target = "status", source = "status", qualifiedByName = "meetingStatusToInt")
    @Mapping(target = "format", source = "format", qualifiedByName = "formatToString")
    MeetingPO entityToPo(Meeting meeting);

    /**
     * PO列表转Entity列表
     */
    List<Meeting> poListToEntityList(List<MeetingPO> meetingPOList);

    /**
     * Entity列表转PO列表
     */
    List<MeetingPO> entityListToPoList(List<Meeting> meetingList);

    // ========== 枚举转换方法（适配 Meeting 内嵌 MeetingStatus） ==========

    @Named("intToMeetingStatus")
    default Meeting.MeetingStatus intToMeetingStatus(Integer code) {
        if (code == null) return null;
        for (Meeting.MeetingStatus s : Meeting.MeetingStatus.values()) {
            if (s.getCode() == code) return s;
        }
        return null;
    }

    @Named("meetingStatusToInt")
    default Integer meetingStatusToInt(Meeting.MeetingStatus status) {
        return status == null ? null : status.getCode();
    }

    @Named("stringToFormat")
    default MeetingFormat stringToFormat(String s) {
        return MeetingFormat.of(s);
    }

    @Named("formatToString")
    default String formatToString(MeetingFormat format) {
        return format == null ? null : format.getValue();
    }
}
