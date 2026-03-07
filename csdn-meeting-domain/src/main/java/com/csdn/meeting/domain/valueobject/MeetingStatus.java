package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 会议状态枚举
 * 对应数据库字段 status
 * @author 13786
 */
@Getter
public enum MeetingStatus {

    /**
     * 已创建状态
     */
    CREATED(0, "已创建", "created"),

    /**
     * 已发布状态
     */
    PUBLISHED(1, "已发布", "published"),

    /**
     * 进行中状态
     */
    ONGOING(2, "进行中", "ongoing"),

    /**
     * 已结束状态
     */
    ENDED(3, "已结束", "ended"),

    /**
     * 已取消状态
     */
    CANCELLED(4, "已取消", "cancelled");

    private final int code;
    private final String displayName;
    private final String value;

    MeetingStatus(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static MeetingStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (MeetingStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static MeetingStatus of(String value) {
        if (value == null) {
            return null;
        }
        for (MeetingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否可以在列表中展示的状态
     */
    public boolean isVisibleInList() {
        return this == PUBLISHED || this == ONGOING || this == ENDED;
    }

    /**
     * 是否可以报名
     */
    public boolean canRegister() {
        return this == PUBLISHED;
    }

    /**
     * 是否可以开始会议
     */
    public boolean canStart() {
        return this == PUBLISHED;
    }

    /**
     * 是否可以结束会议
     */
    public boolean canEnd() {
        return this == ONGOING;
    }
}
