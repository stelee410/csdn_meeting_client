package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.port.ReportPort;
import com.csdn.meeting.domain.port.UserProfilePort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议简报用例（agent.prd §2.7、§3.5）
 * 聚合数据、调用 ReportEngine；高阶内容受 isPremium 控制
 */
@Service
public class MeetingBriefUseCase {

    public enum BriefFormat { PDF, WORD }

    private final MeetingRepository meetingRepository;
    private final RegistrationRepository registrationRepository;
    private final UserProfilePort userProfilePort;
    private final ReportPort reportPort;

    public MeetingBriefUseCase(MeetingRepository meetingRepository,
                               RegistrationRepository registrationRepository,
                               UserProfilePort userProfilePort,
                               ReportPort reportPort) {
        this.meetingRepository = meetingRepository;
        this.registrationRepository = registrationRepository;
        this.userProfilePort = userProfilePort;
        this.reportPort = reportPort;
    }

    /**
     * 生成简报文件（PDF 或 Word）
     *
     * @param meetingId  会议ID
     * @param briefFormat PDF 或 WORD
     * @return Object[3] = {byte[] bytes, String contentType, String filename}
     */
    public Object[] generateBrief(Long meetingId, BriefFormat briefFormat) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        String markdown = buildMarkdown(meeting);
        byte[] bytes;
        String contentType;
        String filename;
        if (briefFormat == BriefFormat.WORD) {
            bytes = reportPort.markdownToWord(markdown);
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            filename = "brief-" + meetingId + ".docx";
        } else {
            bytes = reportPort.markdownToPdf(markdown);
            contentType = "application/pdf";
            filename = "brief-" + meetingId + ".pdf";
        }
        return new Object[]{bytes, contentType, filename};
    }

    /**
     * 会议结束后触发简报生成（供 MeetingEndedEvent 监听器调用）
     */
    public void generateForEndedMeeting(String meetingIdStr) {
        try {
            Long meetingId = Long.parseLong(meetingIdStr);
            generateBrief(meetingId, BriefFormat.PDF);
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    private String buildMarkdown(Meeting meeting) {
        StringBuilder sb = new StringBuilder();
        sb.append("# 会议简报：").append(meeting.getTitle()).append("\n\n");

        long regCount = registrationRepository.findByMeetingIdAndStatus(meeting.getId(), null, 0, 10000).getTotalElements();
        sb.append("## 基础数据\n");
        sb.append("- 报名人数：").append(regCount).append("\n");
        sb.append("- 浏览量/访客：1000/500（模拟）\n\n");

        sb.append("## 热门议题\n");
        sb.append("- （基于议题点击/评分，此处为占位）\n\n");

        if (meeting.isAdvancedDataAvailable()) {
            List<Long> userIds = registrationRepository.findByMeetingIdAndStatus(meeting.getId(), null, 0, 10000)
                    .getContent().stream()
                    .map(r -> r.getUserId())
                    .collect(Collectors.toList());
            String profile = userProfilePort.getAggregatedProfile(userIds);
            sb.append("## 参会者画像分析\n");
            sb.append(profile).append("\n");
        } else {
            sb.append("## 参会者画像分析\n");
            sb.append("*（购买高阶权益后可查看）*\n");
        }
        return sb.toString();
    }
}
