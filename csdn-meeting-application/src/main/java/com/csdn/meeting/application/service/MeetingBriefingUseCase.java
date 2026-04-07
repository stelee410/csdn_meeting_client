package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingBriefingDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 会议简报弹窗用：聚合会议、报名签到、标签等，生成预览 DTO。
 */
@Service
public class MeetingBriefingUseCase {

    private static final DateTimeFormatter GEN_FMT = DateTimeFormatter.ofPattern("yyyy/M/d", Locale.CHINA);

    private final MeetingRepository meetingRepository;
    private final RegistrationRepository registrationRepository;
    private final TagRepository tagRepository;

    public MeetingBriefingUseCase(MeetingRepository meetingRepository,
                                  RegistrationRepository registrationRepository,
                                  TagRepository tagRepository) {
        this.meetingRepository = meetingRepository;
        this.registrationRepository = registrationRepository;
        this.tagRepository = tagRepository;
    }

    public MeetingBriefingDTO getBriefing(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        long checkedIn = registrationRepository.countByMeetingIdAndStatuses(meetingId,
                Collections.singletonList(Registration.RegistrationStatus.CHECKED_IN));
        long eligible = registrationRepository.countByMeetingIdAndStatuses(meetingId,
                Arrays.asList(Registration.RegistrationStatus.APPROVED, Registration.RegistrationStatus.CHECKED_IN));
        long totalReg = registrationRepository.findByMeetingIdAndStatus(meetingId, null, 0, 1).getTotalElements();

        double signinRate = eligible > 0 ? (double) checkedIn / eligible : 0;

        MeetingBriefingDTO dto = new MeetingBriefingDTO();
        dto.setMeetingTitle(meeting.getTitle() != null ? meeting.getTitle() : "");
        dto.setGeneratedAt(LocalDateTime.now().format(GEN_FMT));
        dto.setTotalRegistrations(totalReg);
        dto.setCheckedInCount(checkedIn);
        dto.setEligibleApprovedCount(eligible);
        dto.setSigninRate(signinRate);
        dto.setSigninSubLabel(signinSubLabel(signinRate, eligible));

        if (checkedIn > 0) {
            dto.setAttendanceCount((int) Math.min(checkedIn, Integer.MAX_VALUE));
            dto.setAttendanceSubLabel("已签到人数");
        } else if (eligible > 0) {
            dto.setAttendanceCount((int) Math.min(eligible, Integer.MAX_VALUE));
            dto.setAttendanceSubLabel("已通过审核（暂无签到记录）");
        } else {
            dto.setAttendanceCount(totalReg > 0 ? (int) Math.min(totalReg, Integer.MAX_VALUE) : 0);
            dto.setAttendanceSubLabel(totalReg > 0 ? "报名记录（待审核或未通过）" : "暂无报名");
        }

        dto.setSatisfactionScore(null);
        dto.setInteractionCount(null);

        dto.setMeetingTimeDisplay(formatMeetingTime(meeting));
        dto.setLocationDisplay(formatLocation(meeting));
        dto.setPlannedScaleDisplay(formatPlannedScale(meeting));
        dto.setDurationDisplay(formatDuration(meeting.getMeetingDuration()));
        dto.setIntro(trimToEmpty(meeting.getDescription()));

        List<String> tagNames = new ArrayList<>();
        String bizId = meeting.getMeetingId();
        if (bizId != null && !bizId.trim().isEmpty()) {
            tagNames = tagRepository.findByMeetingId(bizId.trim()).stream()
                    .map(t -> t.getTagName() != null ? t.getTagName() : "")
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        dto.setTopicTags(tagNames);

        dto.setAiSummary(buildAiSummary(meeting, dto.getAttendanceCount(), checkedIn, eligible, signinRate, tagNames));

        return dto;
    }

    private static String signinSubLabel(double rate, long eligible) {
        if (eligible <= 0) {
            return "暂无已通过审核报名";
        }
        if (rate >= 0.8) {
            return "签到表现良好";
        }
        if (rate >= 0.5) {
            return "仍有提升空间";
        }
        return "建议加强现场签到引导";
    }

    private static String formatMeetingTime(Meeting m) {
        if (m.getStartTime() == null) {
            return "";
        }
        if (m.getEndTime() == null || m.getEndTime().toLocalDate().equals(m.getStartTime().toLocalDate())) {
            return m.getStartTime().toLocalDate().toString();
        }
        return m.getStartTime().toLocalDate() + " ~ " + m.getEndTime().toLocalDate();
    }

    private static String formatLocation(Meeting m) {
        String city = trimToEmpty(m.getCityName());
        String venue = trimToEmpty(m.getVenue());
        if (!city.isEmpty() && !venue.isEmpty()) {
            return city + " · " + venue;
        }
        if (!city.isEmpty()) {
            return city;
        }
        return venue.isEmpty() ? "—" : venue;
    }

    private static String formatPlannedScale(Meeting m) {
        if (m.getMaxParticipants() == null || m.getMaxParticipants() <= 0) {
            return "—";
        }
        return m.getMaxParticipants() + "人";
    }

    private static String formatDuration(String code) {
        if (code == null || code.isEmpty()) {
            return "—";
        }
        switch (code) {
            case "half_day":
                return "半天";
            case "one_day":
                return "一天";
            case "two_days":
                return "两天";
            case "three_days":
                return "三天";
            case "more":
                return "三天以上";
            default:
                return code;
        }
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String buildAiSummary(Meeting meeting, int attendanceHighlight, long checkedIn, long eligible,
                                         double signinRate, List<String> tagNames) {
        MeetingFormat fmt = meeting.getFormat();
        String formatText = fmt != null ? fmt.getDisplayName() : "会议";
        String where = formatLocation(meeting);
        if ("—".equals(where)) {
            where = "待定地点";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("本次会议以").append(formatText).append("形式举办");
        if (!where.isEmpty() && !"—".equals(where)) {
            sb.append("，地点为").append(where);
        }
        sb.append("。");

        if (checkedIn > 0) {
            sb.append("实际签到 ").append(checkedIn).append(" 人");
            if (eligible > checkedIn) {
                sb.append("（已通过审核可参会 ").append(eligible).append(" 人）");
            }
        } else if (eligible > 0) {
            sb.append("已通过审核可参会 ").append(eligible).append(" 人，暂无签到记录");
        } else if (attendanceHighlight > 0) {
            sb.append("当前报名相关记录 ").append(attendanceHighlight).append(" 条");
        } else {
            sb.append("暂无报名与签到数据");
        }
        sb.append("。");

        if (eligible > 0) {
            sb.append("在以已通过审核人数为基数时，签到率约 ")
                    .append(String.format(Locale.CHINA, "%.0f", signinRate * 100))
                    .append("%。");
        }

        if (!tagNames.isEmpty()) {
            sb.append("关联议题标签包括：");
            sb.append(String.join("、", tagNames.subList(0, Math.min(5, tagNames.size()))));
            sb.append("。");
        }

        sb.append("满意度与互动数据待接入问卷与现场互动统计后展示。");
        return sb.toString();
    }
}
