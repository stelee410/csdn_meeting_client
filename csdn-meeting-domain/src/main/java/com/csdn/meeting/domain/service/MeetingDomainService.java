package com.csdn.meeting.domain.service;

import com.csdn.meeting.domain.entity.*;
import com.csdn.meeting.domain.exception.AgendaIntegrityException;
import com.csdn.meeting.domain.repository.MeetingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingDomainService {

    private final MeetingRepository meetingRepository;

    public MeetingDomainService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public String generateMeetingId() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return "M" + (System.currentTimeMillis() % 1000000000000L) + suffix.substring(0, 4);
    }

    public boolean isMeetingExists(String meetingId) {
        return meetingRepository.findByMeetingId(meetingId).isPresent();
    }

    /**
     * 四级日程完整性校验（agent.prd §3.3）
     * 任一失败抛 AgendaIntegrityException，并定位到具体缺失项
     */
    public void validateAgendaIntegrity(Meeting meeting) {
        if (meeting == null) {
            throw new AgendaIntegrityException("会议不能为空");
        }
        List<ScheduleDay> days = meeting.getScheduleDays();
        // 1. ScheduleDay 数量 >= 1
        if (days == null || days.isEmpty()) {
            throw new AgendaIntegrityException("AGENDA_INVALID: 至少需要1个日程日");
        }
        LocalDate startDate = meeting.getStartTime() != null ? meeting.getStartTime().toLocalDate() : null;
        LocalDate endDate = meeting.getEndTime() != null ? meeting.getEndTime().toLocalDate() : null;
        if (startDate == null || endDate == null) {
            throw new AgendaIntegrityException("AGENDA_INVALID: 会议开始/结束时间必填");
        }
        for (int d = 0; d < days.size(); d++) {
            ScheduleDay day = days.get(d);
            // 6. ScheduleDay.date 在 [startTime, endTime] 范围内
            LocalDate scheduleDate = day.getScheduleDate();
            if (scheduleDate == null) {
                throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日的日期不能为空");
            }
            if (scheduleDate.isBefore(startDate) || scheduleDate.isAfter(endDate)) {
                throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日(" + scheduleDate + ")必须在会议时间范围内[" + startDate + "," + endDate + "]");
            }
            List<Session> sessions = day.getSessions();
            // 2. 每个 ScheduleDay 下 Session 数量 >= 1
            if (sessions == null || sessions.isEmpty()) {
                throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日下至少需要1个环节");
            }
            // 3. 同一 ScheduleDay 内 Session 时间无重叠
            for (int i = 0; i < sessions.size(); i++) {
                for (int j = i + 1; j < sessions.size(); j++) {
                    if (sessions.get(i).overlaps(sessions.get(j))) {
                        throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日内，第" + (i + 1) + "个环节与第" + (j + 1) + "个环节时间重叠");
                    }
                }
                Session session = sessions.get(i);
                List<SubVenue> subVenues = session.getSubVenues();
                // 4. 每个 Session 下 SubVenue 数量 >= 1
                if (subVenues == null || subVenues.isEmpty()) {
                    throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日第" + (i + 1) + "个环节下至少需要1个分会场");
                }
                for (int v = 0; v < subVenues.size(); v++) {
                    SubVenue subVenue = subVenues.get(v);
                    List<Topic> topics = subVenue.getTopics();
                    // 5. 每个 (Session, SubVenue) 下 Topic 数量 >= 1，且 topic_title 非空
                    if (topics == null || topics.isEmpty()) {
                        throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日第" + (i + 1) + "个环节第" + (v + 1) + "个分会场下至少需要1个议题");
                    }
                    for (int t = 0; t < topics.size(); t++) {
                        if (!topics.get(t).hasValidTitle()) {
                            throw new AgendaIntegrityException("AGENDA_INVALID: 第" + (d + 1) + "个日程日第" + (i + 1) + "个环节第" + (v + 1) + "个分会场第" + (t + 1) + "个议题标题不能为空");
                        }
                    }
                }
            }
        }
    }
}
