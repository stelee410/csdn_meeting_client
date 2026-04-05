package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议场景枚举
 * 对应数据库字段 scene
 * 用于多维度筛选中的"会议场景"维度
 */
@Getter
public enum MeetingScene {

    DEVELOPER(1, "开发者会议", "developer"),
    INDUSTRY(2, "产业会议", "industry"),
    PRODUCT(3, "产品发布会议", "product"),
    REGIONAL(4, "区域营销会议", "regional"),
    UNIVERSITY(5, "高校会议", "university");

    private final int code;
    private final String displayName;
    private final String value;

    MeetingScene(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static MeetingScene of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MeetingScene scene : values()) {
            if (scene.code == code) {
                return scene;
            }
        }
        return null;
    }

    public static MeetingScene of(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (MeetingScene scene : values()) {
                if (scene.name().equalsIgnoreCase(trimmed) || scene.value.equalsIgnoreCase(trimmed)) {
                    return scene;
                }
            }
            return null;
        }
    }
}
