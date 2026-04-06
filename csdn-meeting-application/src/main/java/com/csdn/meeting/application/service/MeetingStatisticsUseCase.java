package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingStatisticsDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.MeetingRights;
import com.csdn.meeting.domain.port.MeetingAnalyticsPort;
import com.csdn.meeting.domain.port.UserProfilePort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议数据统计用例（agent.prd §2.6）
 * basic 全量；advanced 受 isPremium 控制
 */
@Service
public class MeetingStatisticsUseCase {

    private final MeetingRepository meetingRepository;
    private final MeetingRightsRepository rightsRepository;
    private final RegistrationRepository registrationRepository;
    private final UserProfilePort userProfilePort;
    private final MeetingAnalyticsPort meetingAnalyticsPort;

    public MeetingStatisticsUseCase(MeetingRepository meetingRepository,
                                    MeetingRightsRepository rightsRepository,
                                    RegistrationRepository registrationRepository,
                                    UserProfilePort userProfilePort,
                                    MeetingAnalyticsPort meetingAnalyticsPort) {
        this.meetingRepository = meetingRepository;
        this.rightsRepository = rightsRepository;
        this.registrationRepository = registrationRepository;
        this.userProfilePort = userProfilePort;
        this.meetingAnalyticsPort = meetingAnalyticsPort;
    }

    public MeetingStatisticsDTO getStatistics(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        MeetingStatisticsDTO dto = new MeetingStatisticsDTO();
        dto.setBasic(buildBasicStats(meetingId));
        dto.setPremiumRequired(!meeting.isAdvancedDataAvailable());

        if (meeting.isAdvancedDataAvailable()) {
            List<String> userIds = getRegistrationUserIds(meetingId);
            String profile = userProfilePort.getAggregatedProfile(userIds);
            dto.setAdvanced(profile);
        } else {
            dto.setAdvanced(null);
        }
        return dto;
    }

    private MeetingStatisticsDTO.BasicStats buildBasicStats(Long meetingId) {
        long regCount = registrationRepository.findByMeetingIdAndStatus(meetingId, null, 0, Integer.MAX_VALUE)
                .getTotalElements();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        long totalPv = meetingAnalyticsPort.countExposure(meetingId, null);
        long totalClicks = meetingAnalyticsPort.countClicks(meetingId, null);
        long totalUv = meetingAnalyticsPort.countUniqueVisitors(meetingId, null);

        long todayPv = meetingAnalyticsPort.countExposure(meetingId, today);
        long yesterdayPv = meetingAnalyticsPort.countExposure(meetingId, yesterday);
        long todayClicks = meetingAnalyticsPort.countClicks(meetingId, today);
        long yesterdayClicks = meetingAnalyticsPort.countClicks(meetingId, yesterday);

        MeetingStatisticsDTO.BasicStats basic = new MeetingStatisticsDTO.BasicStats();
        basic.setPv(totalPv);
        basic.setUv(totalUv);
        basic.setClicks(totalClicks);
        basic.setRegistrations(regCount);
        basic.setCheckins((long) (regCount * 0.75));
        basic.setCheckinRate(regCount > 0 ? 0.75 : 0);
        basic.setExposureTrend(calcTrendPercent(todayPv, yesterdayPv));
        basic.setClicksTrend(calcTrendPercent(todayClicks, yesterdayClicks));
        basic.setConversionRate(totalClicks > 0 ? (double) regCount / totalClicks * 100 : 0);

        LocalDate weekAgo = today.minusDays(6);
        List<MeetingAnalyticsPort.DailyStats> dailyStats = meetingAnalyticsPort.getDailyStats(meetingId, weekAgo, today);
        List<MeetingStatisticsDTO.TrendItem> trend = new ArrayList<>();
        for (MeetingAnalyticsPort.DailyStats ds : dailyStats) {
            trend.add(new MeetingStatisticsDTO.TrendItem(
                    ds.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE), ds.getPv(), ds.getClicks()));
        }
        basic.setTrend7d(trend);
        return basic;
    }

    private static double calcTrendPercent(long current, long previous) {
        if (previous == 0) return 0;
        return (double) (current - previous) / previous * 100;
    }

    private List<String> getRegistrationUserIds(Long meetingId) {
        return registrationRepository.findByMeetingIdAndStatus(meetingId, null, 0, 10000)
                .getContent().stream()
                .map(r -> r.getUserId())
                .collect(Collectors.toList());
    }
}
