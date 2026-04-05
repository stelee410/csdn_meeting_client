package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议形式枚举
 * 对应数据库字段 format
 */
@Getter
public enum MeetingFormat {

    ONLINE(1, "线上", "online"),
    OFFLINE(2, "线下", "offline"),
    HYBRID(3, "线上+线下", "hybrid");

    private final int code;
    private final String displayName;
    private final String value;

    MeetingFormat(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static MeetingFormat of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MeetingFormat format : values()) {
            if (format.code == code) {
                return format;
            }
        }
        return null;
    }

    public static MeetingFormat of(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (MeetingFormat format : values()) {
                if (format.name().equalsIgnoreCase(trimmed) || format.value.equalsIgnoreCase(trimmed)) {
                    return format;
                }
            }
            return null;
        }
    }
}
