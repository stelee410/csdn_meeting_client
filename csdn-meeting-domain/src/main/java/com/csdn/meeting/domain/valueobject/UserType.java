package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 用户类型枚举
 * 对应数据库字段 user_type
 */
@Getter
public enum UserType {

    /**
     * 普通用户（通过client端注册）
     */
    USER(0, "普通用户", "user"),

    /**
     * 管理员（通过operation端创建）
     */
    ADMIN(1, "管理员", "admin"),

    /**
     * 运营人员（通过operation端创建）
     */
    OPERATOR(2, "运营人员", "operator");

    private final int code;
    private final String displayName;
    private final String value;

    UserType(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static UserType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    public static UserType of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (UserType type : values()) {
                if (type.name().equalsIgnoreCase(trimmed) || type.value.equalsIgnoreCase(trimmed)) {
                    return type;
                }
            }
            return null;
        }
    }
}
