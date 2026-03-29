package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 验证码类型枚举
 * 对应数据库字段 type
 */
@Getter
public enum VerificationCodeType {

    /**
     * 短信验证码
     */
    SMS(0, "短信", "sms"),

    /**
     * 邮箱验证码
     */
    EMAIL(1, "邮箱", "email");

    private final int code;
    private final String displayName;
    private final String value;

    VerificationCodeType(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static VerificationCodeType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (VerificationCodeType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    public static VerificationCodeType of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (VerificationCodeType type : values()) {
                if (type.name().equalsIgnoreCase(trimmed) || type.value.equalsIgnoreCase(trimmed)) {
                    return type;
                }
            }
            return null;
        }
    }
}
