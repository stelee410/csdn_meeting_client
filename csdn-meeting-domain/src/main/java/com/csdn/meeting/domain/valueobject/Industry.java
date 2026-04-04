package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 行业枚举（用户职业信息）
 * 与会议侧的"所属产业"枚举保持一致
 * 对应《技术会议结构化-会议定义》1.4.2.1 所属产业
 */
@Getter
public enum Industry {

    /**
     * AI人工智能
     */
    AI(1, "AI人工智能", "ai"),

    /**
     * 云计算
     */
    CLOUD_COMPUTING(2, "云计算", "cloud_computing"),

    /**
     * 开源
     */
    OPEN_SOURCE(3, "开源", "open_source"),

    /**
     * 出海
     */
    OVERSEAS(4, "出海", "overseas"),

    /**
     * 鸿蒙
     */
    HARMONYOS(5, "鸿蒙", "harmonyos"),

    /**
     * 游戏
     */
    GAME(6, "游戏", "game"),

    /**
     * 金融
     */
    FINANCE(7, "金融", "finance");

    private final int code;
    private final String displayName;
    private final String value;

    Industry(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static Industry of(Integer code) {
        if (code == null) {
            return null;
        }
        for (Industry industry : values()) {
            if (industry.code == code) {
                return industry;
            }
        }
        return null;
    }

    public static Industry of(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String trimmed = s.trim();
        try {
            int code = Integer.parseInt(trimmed);
            return of(code);
        } catch (NumberFormatException e) {
            for (Industry industry : values()) {
                if (industry.name().equalsIgnoreCase(trimmed) || industry.value.equalsIgnoreCase(trimmed)
                        || industry.displayName.equalsIgnoreCase(trimmed)) {
                    return industry;
                }
            }
            return null;
        }
    }
}
