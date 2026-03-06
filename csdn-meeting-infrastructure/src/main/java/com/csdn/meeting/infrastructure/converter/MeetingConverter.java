package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingScene;
import com.csdn.meeting.domain.valueobject.MeetingStatus;
import com.csdn.meeting.domain.valueobject.MeetingType;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会议转换器
 * 用于 MeetingPO 和 Meeting 实体之间的转换
 */
@Mapper
public interface MeetingConverter {

    MeetingConverter INSTANCE = Mappers.getMapper(MeetingConverter.class);

    /**
     * PO 转 Entity
     */
    Meeting poToEntity(MeetingPO meetingPO);

    /**
     * Entity 转 PO
     */
    MeetingPO entityToPo(Meeting meeting);

    /**
     * PO列表转Entity列表
     */
    List<Meeting> poListToEntityList(List<MeetingPO> meetingPOList);

    /**
     * Entity列表转PO列表
     */
    List<MeetingPO> entityListToPoList(List<Meeting> meetingList);

    // ========== 枚举转换方法 ==========

    /**
     * 字符串转状态枚举
     */
    default MeetingStatus stringToStatus(String status) {
        return MeetingStatus.of(status);
    }

    /**
     * 状态枚举转字符串
     */
    default String statusToString(MeetingStatus status) {
        return status == null ? null : status.getValue();
    }

    /**
     * 整型转会议形式枚举
     */
    default MeetingFormat intToFormat(Integer format) {
        return MeetingFormat.of(format);
    }

    /**
     * 会议形式枚举转整型
     */
    default Integer formatToInt(MeetingFormat format) {
        return format == null ? null : format.getCode();
    }

    /**
     * 整型转会议类型枚举
     */
    default MeetingType intToType(Integer meetingType) {
        return MeetingType.of(meetingType);
    }

    /**
     * 会议类型枚举转整型
     */
    default Integer typeToInt(MeetingType meetingType) {
        return meetingType == null ? null : meetingType.getCode();
    }

    /**
     * 整型转会议场景枚举
     */
    default MeetingScene intToScene(Integer scene) {
        return MeetingScene.of(scene);
    }

    /**
     * 会议场景枚举转整型
     */
    default Integer sceneToInt(MeetingScene scene) {
        return scene == null ? null : scene.getCode();
    }
}
