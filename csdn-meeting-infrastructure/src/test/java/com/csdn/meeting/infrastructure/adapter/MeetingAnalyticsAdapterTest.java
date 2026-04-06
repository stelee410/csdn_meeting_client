package com.csdn.meeting.infrastructure.adapter;

import com.csdn.meeting.domain.port.MeetingAnalyticsPort;
import com.csdn.meeting.infrastructure.repository.mapper.TrackEventPOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingAnalyticsAdapter: query exposure, clicks, UV, daily stats")
class MeetingAnalyticsAdapterTest {

    @Mock
    private TrackEventPOMapper trackEventMapper;

    private MeetingAnalyticsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MeetingAnalyticsAdapter(trackEventMapper);
    }

    @Test
    @DisplayName("countExposure delegates to mapper with correct module/action")
    void countExposure_delegatesToMapper() {
        when(trackEventMapper.countByModuleActionAndMeetingId("meeting_list", "impression", 42L, null))
                .thenReturn(150L);

        long result = adapter.countExposure(42L, null);

        assertEquals(150L, result);
        verify(trackEventMapper).countByModuleActionAndMeetingId("meeting_list", "impression", 42L, null);
    }

    @Test
    @DisplayName("countExposure with date passes formatted date string")
    void countExposure_withDate() {
        LocalDate date = LocalDate.of(2026, 4, 5);
        when(trackEventMapper.countByModuleActionAndMeetingId("meeting_list", "impression", 42L, "2026-04-05"))
                .thenReturn(30L);

        long result = adapter.countExposure(42L, date);

        assertEquals(30L, result);
    }

    @Test
    @DisplayName("countClicks delegates with meeting_detail / page_view")
    void countClicks_delegatesToMapper() {
        when(trackEventMapper.countByModuleActionAndMeetingId("meeting_detail", "page_view", 99L, null))
                .thenReturn(75L);

        assertEquals(75L, adapter.countClicks(99L, null));
    }

    @Test
    @DisplayName("countUniqueVisitors delegates to countDistinctVisitors")
    void countUniqueVisitors_delegatesToMapper() {
        when(trackEventMapper.countDistinctVisitors("meeting_detail", "page_view", 99L, null))
                .thenReturn(50L);

        assertEquals(50L, adapter.countUniqueVisitors(99L, null));
    }

    @Test
    @DisplayName("getDailyStats maps rows to DailyStats list")
    void getDailyStats_mapsRows() {
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("stat_date", "2026-04-01");
        row1.put("pv", 100L);
        row1.put("clicks", 20L);

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("stat_date", "2026-04-02");
        row2.put("pv", 200L);
        row2.put("clicks", 40L);

        when(trackEventMapper.selectDailyStats(42L, "2026-04-01", "2026-04-07"))
                .thenReturn(Arrays.asList(row1, row2));

        List<MeetingAnalyticsPort.DailyStats> stats = adapter.getDailyStats(
                42L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 7));

        assertEquals(2, stats.size());
        assertEquals(LocalDate.of(2026, 4, 1), stats.get(0).getDate());
        assertEquals(100L, stats.get(0).getPv());
        assertEquals(20L, stats.get(0).getClicks());
        assertEquals(LocalDate.of(2026, 4, 2), stats.get(1).getDate());
        assertEquals(200L, stats.get(1).getPv());
        assertEquals(40L, stats.get(1).getClicks());
    }

    @Test
    @DisplayName("getDailyStats returns empty list when no data")
    void getDailyStats_emptyList() {
        when(trackEventMapper.selectDailyStats(42L, "2026-04-01", "2026-04-07"))
                .thenReturn(Collections.emptyList());

        List<MeetingAnalyticsPort.DailyStats> stats = adapter.getDailyStats(
                42L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 7));

        assertTrue(stats.isEmpty());
    }
}
