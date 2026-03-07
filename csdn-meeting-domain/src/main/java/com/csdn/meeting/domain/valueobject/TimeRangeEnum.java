package com.csdn.meeting.domain.valueobject;

import lombok.Getter;

/**
 * 时间范围枚举
 * 用于多维度筛选中的"召开时间"维度
 * 自然时间定义：
 * - THIS_WEEK: 本周一00:00:00 到 本周日23:59:59
 * - THIS_MONTH: 本月1号00:00:00 到 本月最后一天23:59:59
 * - NEXT_3_MONTHS: 今天00:00:00 到 3个月后23:59:59
 */
@Getter
public enum TimeRangeEnum {

    THIS_WEEK(1, "本周", "this_week"),
    THIS_MONTH(2, "本月", "this_month"),
    NEXT_3_MONTHS(3, "未来三个月", "next_3_months");

    private final int code;
    private final String displayName;
    private final String value;

    TimeRangeEnum(int code, String displayName, String value) {
        this.code = code;
        this.displayName = displayName;
        this.value = value;
    }

    public static TimeRangeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (TimeRangeEnum range : values()) {
            if (range.code == code) {
                return range;
            }
        }
        return null;
    }

    public static TimeRangeEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (TimeRangeEnum range : values()) {
            if (range.value.equalsIgnoreCase(value)) {
                return range;
            }
        }
        return null;
    }
}
