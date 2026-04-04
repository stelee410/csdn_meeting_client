package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingStatisticsDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.MeetingRights;
import com.csdn.meeting.domain.port.UserProfilePort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

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

    public MeetingStatisticsUseCase(MeetingRepository meetingRepository,
                                    MeetingRightsRepository rightsRepository,
                                    RegistrationRepository registrationRepository,
                                    UserProfilePort userProfilePort) {
        this.meetingRepository = meetingRepository;
        this.rightsRepository = rightsRepository;
        this.registrationRepository = registrationRepository;
        this.userProfilePort = userProfilePort;
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
        // Stub: t_meeting_stats 未建表，用模拟值
        MeetingStatisticsDTO.BasicStats basic = new MeetingStatisticsDTO.BasicStats();
        basic.setPv(1000);
        basic.setUv(500);
        basic.setRegistrations(regCount);
        basic.setCheckins((long) (regCount * 0.75));
        basic.setCheckinRate(regCount > 0 ? 0.75 : 0);
        List<MeetingStatisticsDTO.TrendItem> trend = new ArrayList<>();
        trend.add(new MeetingStatisticsDTO.TrendItem("2026-03-01", 100, 10));
        basic.setTrend7d(trend);
        return basic;
    }

    private List<String> getRegistrationUserIds(Long meetingId) {
        return registrationRepository.findByMeetingIdAndStatus(meetingId, null, 0, 10000)
                .getContent().stream()
                .map(r -> r.getUserId())
                .collect(Collectors.toList());
    }
}
