package com.csdn.meeting.infrastructure.adapter;

import com.csdn.meeting.domain.port.MeetingAnalyticsPort;
import com.csdn.meeting.infrastructure.repository.mapper.TrackEventPOMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 会议数据分析适配器：从 analytics_track_event 表查询埋点统计数据。
 */
@Component
public class MeetingAnalyticsAdapter implements MeetingAnalyticsPort {

    private static final String MODULE_LIST = "meeting_list";
    private static final String ACTION_IMPRESSION = "impression";
    private static final String MODULE_DETAIL = "meeting_detail";
    private static final String ACTION_PAGE_VIEW = "page_view";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final TrackEventPOMapper trackEventMapper;

    public MeetingAnalyticsAdapter(TrackEventPOMapper trackEventMapper) {
        this.trackEventMapper = trackEventMapper;
    }

    @Override
    public long countExposure(Long meetingId, LocalDate date) {
        return trackEventMapper.countByModuleActionAndMeetingId(
                MODULE_LIST, ACTION_IMPRESSION, meetingId, formatDate(date));
    }

    @Override
    public long countClicks(Long meetingId, LocalDate date) {
        return trackEventMapper.countByModuleActionAndMeetingId(
                MODULE_DETAIL, ACTION_PAGE_VIEW, meetingId, formatDate(date));
    }

    @Override
    public long countUniqueVisitors(Long meetingId, LocalDate date) {
        return trackEventMapper.countDistinctVisitors(
                MODULE_DETAIL, ACTION_PAGE_VIEW, meetingId, formatDate(date));
    }

    @Override
    public List<DailyStats> getDailyStats(Long meetingId, LocalDate from, LocalDate to) {
        List<Map<String, Object>> rows = trackEventMapper.selectDailyStats(
                meetingId, from.format(DATE_FMT), to.format(DATE_FMT));
        List<DailyStats> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            LocalDate date = LocalDate.parse(String.valueOf(row.get("stat_date")));
            long pv = ((Number) row.get("pv")).longValue();
            long clicks = ((Number) row.get("clicks")).longValue();
            result.add(new DailyStats(date, pv, clicks));
        }
        return result;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : null;
    }
}
