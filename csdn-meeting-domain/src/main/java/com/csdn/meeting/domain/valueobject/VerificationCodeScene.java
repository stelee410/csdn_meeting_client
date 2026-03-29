package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 验证码业务场景枚举
 * 对应数据库字段 scene
 */
@Getter
public enum VerificationCodeScene {

    /**
     * 注册场景
     */
    REGISTER(0, "注册", "register"),

    /**
     * 登录场景
     */
    LOGIN(1, "登录", "login"),

    /**
     * 重置密码场景
     */
    RESET(2, "重置密码", "reset");

    private final int code;
    private final String displayName;
    private final String value;

    VerificationCodeScene(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static VerificationCodeScene of(Integer code) {
        if (code == null) {
            return null;
        }
        for (VerificationCodeScene scene : values()) {
            if (scene.code == code) {
                return scene;
            }
        }
        return null;
    }

    public static VerificationCodeScene of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (VerificationCodeScene scene : values()) {
                if (scene.name().equalsIgnoreCase(trimmed) || scene.value.equalsIgnoreCase(trimmed)) {
                    return scene;
                }
            }
            return null;
        }
    }
}
