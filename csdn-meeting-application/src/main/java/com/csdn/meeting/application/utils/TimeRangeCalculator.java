package com.csdn.meeting.application.utils;

import com.csdn.meeting.domain.valueobject.TimeRangeEnum;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * 时间范围计算工具
 * 计算自然时间范围（本周/本月/未来三个月）
 */
@Component
public class TimeRangeCalculator {

    /**
     * 计算时间范围的起止时间
     *
     * @param range 时间范围枚举
     * @return LocalDateTime数组，[0]=开始时间, [1]=结束时间
     */
    public LocalDateTime[] calculateTimeRange(TimeRangeEnum range) {
        if (range == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        switch (range) {
            case THIS_WEEK:
                return calculateThisWeek(today);
            case THIS_MONTH:
                return calculateThisMonth(today);
            case NEXT_3_MONTHS:
                return calculateNext3Months(today);
            default:
                return null;
        }
    }

    /**
     * 计算本周范围（周一00:00:00 - 周日23:59:59）
     */
    private LocalDateTime[] calculateThisWeek(LocalDate today) {
        // 本周一
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 本周日
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime start = LocalDateTime.of(weekStart, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(weekEnd, LocalTime.MAX);

        return new LocalDateTime[]{start, end};
    }

    /**
     * 计算本月范围（1号00:00:00 - 月末23:59:59）
     */
    private LocalDateTime[] calculateThisMonth(LocalDate today) {
        // 本月1号
        LocalDate monthStart = today.withDayOfMonth(1);
        // 本月最后一天
        LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime start = LocalDateTime.of(monthStart, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(monthEnd, LocalTime.MAX);

        return new LocalDateTime[]{start, end};
    }

    /**
     * 计算未来三个月范围（今天00:00:00 - 3个月后23:59:59）
     */
    private LocalDateTime[] calculateNext3Months(LocalDate today) {
        LocalDate endDate = today.plusMonths(3);

        LocalDateTime start = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        return new LocalDateTime[]{start, end};
    }

    /**
     * 根据字符串值计算时间范围
     */
    public LocalDateTime[] calculateTimeRange(String rangeValue) {
        TimeRangeEnum range = TimeRangeEnum.of(rangeValue);
        return calculateTimeRange(range);
    }
}
