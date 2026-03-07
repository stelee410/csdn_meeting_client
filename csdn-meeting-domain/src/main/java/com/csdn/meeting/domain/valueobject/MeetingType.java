package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议类型枚举
 * 对应数据库字段 meeting_type
 * @author 13786
 */
@Getter
public enum MeetingType {

    /**
     * 技术峰会
     */
    SUMMIT(1, "技术峰会", "summit"),

    /**
     * 技术沙龙
     */
    SALON(2, "技术沙龙", "salon"),

    /**
     * 技术研讨会
     */
    WORKSHOP(3, "技术研讨会", "workshop");

    private final int code;
    private final String displayName;
    private final String value;

    MeetingType(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static MeetingType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MeetingType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据字符串解析为 MeetingType。
     * 支持：code 字符串（"1","2","3"）、枚举名（"SUMMIT"）、value（"summit"）。
     */
    public static MeetingType of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (MeetingType type : values()) {
                if (type.name().equalsIgnoreCase(trimmed) || type.value.equalsIgnoreCase(trimmed)) {
                    return type;
                }
            }
            return null;
        }
    }
}
