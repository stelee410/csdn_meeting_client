package com.csdn.meeting.infrastructure.task;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.event.MeetingEndedEvent;
import com.csdn.meeting.domain.event.MeetingStatusChangedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务：按 startTime/endTime 自动切换 PUBLISHED→IN_PROGRESS、IN_PROGRESS→ENDED。
 * 扫描 PUBLISHED 且 startTime <= now 的会议执行 autoStart；
 * 扫描 IN_PROGRESS 且 endTime <= now 的会议执行 autoEnd 并发布 MeetingEndedEvent。
 */
@Component
public class MeetingStatusScheduleTask {

    private static final Logger log = LoggerFactory.getLogger(MeetingStatusScheduleTask.class);

    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MeetingStatusScheduleTask(MeetingRepository meetingRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.meetingRepository = meetingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 60_000) // every minute
    @Transactional
    public void scanAndUpdateMeetingStatus() {
        LocalDateTime now = LocalDateTime.now();

        List<Meeting> toStart = meetingRepository.findPublishedWithStartTimeBefore(now);
        for (Meeting m : toStart) {
            try {
                Meeting.MeetingStatus from = m.getStatus();
                m.autoStart();
                meetingRepository.save(m);
                eventPublisher.publishEvent(new MeetingStatusChangedEvent(
                        m.getMeetingId(), from, m.getStatus(), now, "SYSTEM"));
                log.info("Auto-started meeting id={} title={}", m.getMeetingId(), m.getTitle());
            } catch (Exception e) {
                log.warn("Failed to auto-start meeting id={}: {}", m.getMeetingId(), e.getMessage());
            }
        }

        List<Meeting> toEnd = meetingRepository.findInProgressWithEndTimeBefore(now);
        for (Meeting m : toEnd) {
            try {
                Meeting.MeetingStatus from = m.getStatus();
                m.autoEnd();
                meetingRepository.save(m);
                eventPublisher.publishEvent(new MeetingStatusChangedEvent(
                        m.getMeetingId(), from, m.getStatus(), now, "SYSTEM"));
                eventPublisher.publishEvent(new MeetingEndedEvent(m.getMeetingId(), now));
                log.info("Auto-ended meeting id={} title={}", m.getMeetingId(), m.getTitle());
            } catch (Exception e) {
                log.warn("Failed to auto-end meeting id={}: {}", m.getMeetingId(), e.getMessage());
            }
        }
    }
}
