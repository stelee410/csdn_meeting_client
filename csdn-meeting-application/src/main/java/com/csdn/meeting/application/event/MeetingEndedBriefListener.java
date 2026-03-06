package com.csdn.meeting.application.event;

import com.csdn.meeting.application.service.MeetingBriefUseCase;
import com.csdn.meeting.domain.event.MeetingEndedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听 MeetingEndedEvent，触发简报生成。
 */
@Component
public class MeetingEndedBriefListener {

    private static final Logger log = LoggerFactory.getLogger(MeetingEndedBriefListener.class);

    private final MeetingBriefUseCase meetingBriefUseCase;

    public MeetingEndedBriefListener(MeetingBriefUseCase meetingBriefUseCase) {
        this.meetingBriefUseCase = meetingBriefUseCase;
    }

    @Async
    @EventListener
    public void onMeetingEnded(MeetingEndedEvent event) {
        String meetingId = event.getMeetingId();
        log.info("Meeting ended, trigger brief generation: meetingId={}", meetingId);
        meetingBriefUseCase.generateForEndedMeeting(meetingId);
    }
}
