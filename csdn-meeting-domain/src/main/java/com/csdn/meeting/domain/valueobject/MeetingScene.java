package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议场景枚举
 * 对应数据库字段 scene
 * @author 13786
 */
@Getter
public enum MeetingScene {

    /**
     * 开发者会议
     */
    DEVELOPER(1, "开发者会议", "developer"),

    /**
     * 产业会议
     */
    INDUSTRY(2, "产业会议", "industry"),

    /**
     * 产品发布会议
     */
    PRODUCT_RELEASE(3, "产品发布会议", "product_release"),

    /**
     * 区域营销会议
     */
    REGIONAL_MARKETING(4, "区域营销会议", "regional_marketing"),

    /**
     * 高校会议
     */
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
}
