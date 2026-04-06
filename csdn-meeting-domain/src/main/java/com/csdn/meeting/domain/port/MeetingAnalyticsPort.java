package com.csdn.meeting.domain.port;

import java.time.LocalDate;
import java.util.List;

/**
 * 会议数据分析端口：从埋点数据中查询会议的曝光量、点击量等统计指标。
 */
public interface MeetingAnalyticsPort {

    /**
     * 查询指定会议在某日的曝光次数（列表页 impression）。
     * @param meetingId 会议 ID
     * @param date 日期，null 表示不限日期（全量累计）
     */
    long countExposure(Long meetingId, LocalDate date);

    /**
     * 查询指定会议在某日的点击次数（详情页 page_view）。
     * @param meetingId 会议 ID
     * @param date 日期，null 表示不限日期（全量累计）
     */
    long countClicks(Long meetingId, LocalDate date);

    /**
     * 查询指定会议在某日的独立访客数（详情页 page_view 按 user_id/anonymous_id 去重）。
     * @param meetingId 会议 ID
     * @param date 日期，null 表示不限日期（全量累计）
     */
    long countUniqueVisitors(Long meetingId, LocalDate date);

    /**
     * 查询指定会议在日期范围内的每日统计数据（用于趋势图）。
     */
    List<DailyStats> getDailyStats(Long meetingId, LocalDate from, LocalDate to);

    /**
     * 每日统计数据项。
     */
    class DailyStats {
        private final LocalDate date;
        private final long pv;
        private final long clicks;

        public DailyStats(LocalDate date, long pv, long clicks) {
            this.date = date;
            this.pv = pv;
            this.clicks = clicks;
        }

        public LocalDate getDate() { return date; }
        public long getPv() { return pv; }
        public long getClicks() { return clicks; }
    }
}
