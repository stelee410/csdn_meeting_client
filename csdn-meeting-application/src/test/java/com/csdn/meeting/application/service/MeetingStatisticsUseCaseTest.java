package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingStatisticsDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.port.MeetingAnalyticsPort;
import com.csdn.meeting.domain.port.UserProfilePort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import com.csdn.meeting.domain.repository.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingStatisticsUseCase: real analytics data")
class MeetingStatisticsUseCaseTest {

    @Mock private MeetingRepository meetingRepository;
    @Mock private MeetingRightsRepository rightsRepository;
    @Mock private RegistrationRepository registrationRepository;
    @Mock private UserProfilePort userProfilePort;
    @Mock private MeetingAnalyticsPort meetingAnalyticsPort;

    private MeetingStatisticsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MeetingStatisticsUseCase(
                meetingRepository, rightsRepository, registrationRepository,
                userProfilePort, meetingAnalyticsPort);
    }

    @SuppressWarnings("unchecked")
    private void stubMeeting(Long id) {
        Meeting meeting = mock(Meeting.class);
        when(meeting.isAdvancedDataAvailable()).thenReturn(false);
        when(meetingRepository.findById(id)).thenReturn(Optional.of(meeting));
        PageResult pageResult = mock(PageResult.class);
        when(pageResult.getTotalElements()).thenReturn(0L);
        when(registrationRepository.findByMeetingIdAndStatus(eq(id), isNull(), anyInt(), anyInt()))
                .thenReturn(pageResult);
    }

    @Test
    @DisplayName("basic stats uses real analytics data instead of stub")
    void basicStats_usesRealAnalytics() {
        Long meetingId = 42L;
        stubMeeting(meetingId);

        when(meetingAnalyticsPort.countExposure(eq(meetingId), isNull())).thenReturn(500L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), isNull())).thenReturn(100L);
        when(meetingAnalyticsPort.countUniqueVisitors(eq(meetingId), isNull())).thenReturn(80L);
        when(meetingAnalyticsPort.countExposure(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.getDailyStats(eq(meetingId), any(), any())).thenReturn(Collections.emptyList());

        MeetingStatisticsDTO result = useCase.getStatistics(meetingId);

        assertEquals(500L, result.getBasic().getPv());
        assertEquals(100L, result.getBasic().getClicks());
        assertEquals(80L, result.getBasic().getUv());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("conversionRate = registrations / clicks * 100")
    void conversionRate_calculatedCorrectly() {
        Long meetingId = 42L;
        Meeting meeting = mock(Meeting.class);
        when(meeting.isAdvancedDataAvailable()).thenReturn(false);
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        PageResult pageResult = mock(PageResult.class);
        when(pageResult.getTotalElements()).thenReturn(25L);
        when(registrationRepository.findByMeetingIdAndStatus(eq(meetingId), isNull(), anyInt(), anyInt()))
                .thenReturn(pageResult);

        when(meetingAnalyticsPort.countExposure(eq(meetingId), isNull())).thenReturn(1000L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), isNull())).thenReturn(200L);
        when(meetingAnalyticsPort.countUniqueVisitors(eq(meetingId), isNull())).thenReturn(150L);
        when(meetingAnalyticsPort.countExposure(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.getDailyStats(eq(meetingId), any(), any())).thenReturn(Collections.emptyList());

        MeetingStatisticsDTO result = useCase.getStatistics(meetingId);

        assertEquals(12.5, result.getBasic().getConversionRate(), 0.01);
    }

    @Test
    @DisplayName("trend is 0 when yesterday count is 0 (avoids division by zero)")
    void trend_zeroWhenNoPreviousData() {
        Long meetingId = 42L;
        stubMeeting(meetingId);

        when(meetingAnalyticsPort.countExposure(eq(meetingId), isNull())).thenReturn(100L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), isNull())).thenReturn(50L);
        when(meetingAnalyticsPort.countUniqueVisitors(eq(meetingId), isNull())).thenReturn(30L);
        when(meetingAnalyticsPort.countExposure(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.getDailyStats(eq(meetingId), any(), any())).thenReturn(Collections.emptyList());

        MeetingStatisticsDTO result = useCase.getStatistics(meetingId);

        assertEquals(0.0, result.getBasic().getExposureTrend());
        assertEquals(0.0, result.getBasic().getClicksTrend());
    }

    @Test
    @DisplayName("conversionRate is 0 when no clicks (avoids division by zero)")
    void conversionRate_zeroWhenNoClicks() {
        Long meetingId = 42L;
        stubMeeting(meetingId);

        when(meetingAnalyticsPort.countExposure(eq(meetingId), isNull())).thenReturn(100L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), isNull())).thenReturn(0L);
        when(meetingAnalyticsPort.countUniqueVisitors(eq(meetingId), isNull())).thenReturn(0L);
        when(meetingAnalyticsPort.countExposure(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.countClicks(eq(meetingId), any(LocalDate.class))).thenReturn(0L);
        when(meetingAnalyticsPort.getDailyStats(eq(meetingId), any(), any())).thenReturn(Collections.emptyList());

        MeetingStatisticsDTO result = useCase.getStatistics(meetingId);

        assertEquals(0.0, result.getBasic().getConversionRate());
    }
}
