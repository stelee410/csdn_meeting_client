package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingType;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {

    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToInt")
    @Mapping(target = "format", source = "format", qualifiedByName = "formatToString")
    @Mapping(target = "meetingType", source = "meetingType", qualifiedByName = "meetingTypeToString")
    @Mapping(target = "targetAudience", source = "targetAudience", qualifiedByName = "toJsonArray")
    @Mapping(target = "regions", source = "regions", qualifiedByName = "toJsonArray")
    MeetingPO toPO(Meeting meeting);

    @Mapping(target = "status", source = "status", qualifiedByName = "intToStatus")
    @Mapping(target = "format", source = "format", qualifiedByName = "stringToFormat")
    @Mapping(target = "meetingId", expression = "java(po.getId() != null ? String.valueOf(po.getId()) : null)")
    @Mapping(target = "meetingType", source = "meetingType", qualifiedByName = "stringToMeetingType")
    @Mapping(target = "scheduleDays", ignore = true)
    Meeting toEntity(MeetingPO po);

    @Named("statusToInt")
    default Integer statusToInt(Meeting.MeetingStatus status) {
        return status == null ? null : status.getCode();
    }

    @Named("intToStatus")
    default Meeting.MeetingStatus intToStatus(Integer code) {
        if (code == null) return null;
        for (Meeting.MeetingStatus s : Meeting.MeetingStatus.values()) {
            if (s.getCode() == code) return s;
        }
        return null;
    }

    @Named("formatToString")
    default String formatToString(MeetingFormat format) {
        return format == null ? null : format.getValue();
    }

    @Named("stringToFormat")
    default MeetingFormat stringToFormat(String s) {
        return MeetingFormat.of(s);
    }

    /**
     * PO 的 meeting_type 字符串转为 MeetingType，统一走 MeetingType.of 接口
     */
    @Named("stringToMeetingType")
    default MeetingType stringToMeetingType(String s) {
        return MeetingType.of(s);
    }

    @Named("meetingTypeToString")
    default String meetingTypeToString(MeetingType meetingType) {
        return meetingType == null ? null : meetingType.name();
    }

    /**
     * 将逗号分隔字符串或已有 JSON 转为合法 JSON 数组字符串。
     * 若值本身已是 JSON（以 [ 开头），直接返回；否则按逗号拆分后序列化。
     */
    @Named("toJsonArray")
    default String toJsonArray(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("[")) {
            return trimmed;
        }
        String[] parts = trimmed.split(",");
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < parts.length; i++) {
            String item = parts[i].trim().replace("\"", "\\\"");
            sb.append("\"").append(item).append("\"");
            if (i < parts.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
