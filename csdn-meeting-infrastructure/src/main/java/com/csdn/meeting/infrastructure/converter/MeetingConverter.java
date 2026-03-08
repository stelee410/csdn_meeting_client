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
    @Mapping(target = "meetingId", source = "meetingId")
    @Mapping(target = "scheduleDays", ignore = true)
    @Mapping(target = "meetingType", source = "meetingType", qualifiedByName = "stringToMeetingType")
    @Mapping(target = "posterUrl", source = "posterUrl")
    @Mapping(target = "hotScore", source = "hotScore")
    @Mapping(target = "currentParticipants", source = "currentParticipants")
    @Mapping(target = "maxParticipants", source = "maxParticipants")
    @Mapping(target = "cityCode", source = "cityCode")
    @Mapping(target = "cityName", source = "cityName")
    @Mapping(target = "organizerId", source = "organizerId")
    @Mapping(target = "organizerName", source = "organizerName")
    @Mapping(target = "organizerAvatar", source = "organizerAvatar")
    @Mapping(target = "publishTime", source = "publishTime")
    Meeting poToEntity(MeetingPO meetingPO);

    @Mapping(target = "status", source = "status", qualifiedByName = "meetingStatusToInt")
    @Mapping(target = "format", source = "format", qualifiedByName = "formatToString")
    @Mapping(target = "meetingId", source = "meetingId")
    @Mapping(target = "posterUrl", source = "posterUrl")
    @Mapping(target = "hotScore", source = "hotScore")
    @Mapping(target = "currentParticipants", source = "currentParticipants")
    @Mapping(target = "maxParticipants", source = "maxParticipants")
    @Mapping(target = "cityCode", source = "cityCode")
    @Mapping(target = "cityName", source = "cityName")
    @Mapping(target = "meetingType", source = "meetingType", qualifiedByName = "meetingTypeToString")
    @Mapping(target = "organizerId", source = "organizerId")
    @Mapping(target = "organizerName", source = "organizerName")
    @Mapping(target = "organizerAvatar", source = "organizerAvatar")
    @Mapping(target = "publishTime", source = "publishTime")
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
        if (code == null) {
            return null;
        }
        for (Meeting.MeetingStatus s : Meeting.MeetingStatus.values()) {
            if (s.getCode() == code) {
                return s;
            }
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

    /**
     * PO 的 meeting_type 字符串转为 MeetingType，统一走 MeetingType.of 接口
     */
    @Named("stringToMeetingType")
    default MeetingType stringToMeetingType(String s) {
        return MeetingType.of(s);
    }

    @Named("meetingTypeToString")
    default String meetingTypeToString(MeetingType type) {
        return type == null ? null : String.valueOf(type.getCode());
    }
}
