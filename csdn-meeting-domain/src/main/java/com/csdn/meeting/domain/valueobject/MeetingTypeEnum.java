package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议类型枚举
 * 对应数据库字段 meeting_type
 * 用于多维度筛选中的"会议类型"维度
 */
@Getter
public enum MeetingTypeEnum {

    SUMMIT(1, "技术峰会", "summit"),
    SALON(2, "技术沙龙", "salon"),
    WORKSHOP(3, "技术研讨会", "workshop");

    private final int code;
    private final String displayName;
    private final String value;

    MeetingTypeEnum(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static MeetingTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MeetingTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    public static MeetingTypeEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (MeetingTypeEnum type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
