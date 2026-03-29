package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 用户账号状态枚举
 * 对应数据库字段 status
 */
@Getter
public enum UserStatus {

    /**
     * 正常状态，可正常登录使用
     */
    NORMAL(0, "正常", "normal"),

    /**
     * 冻结状态，禁止登录
     */
    FROZEN(1, "冻结", "frozen");

    private final int code;
    private final String displayName;
    private final String value;

    UserStatus(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static UserStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static UserStatus of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (UserStatus status : values()) {
                if (status.name().equalsIgnoreCase(trimmed) || status.value.equalsIgnoreCase(trimmed)) {
                    return status;
                }
            }
            return null;
        }
    }
}
