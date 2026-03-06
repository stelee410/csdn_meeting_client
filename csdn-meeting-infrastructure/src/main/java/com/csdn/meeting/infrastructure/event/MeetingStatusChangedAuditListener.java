package com.csdn.meeting.infrastructure.event;

import com.csdn.meeting.domain.event.MeetingStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听 MeetingStatusChangedEvent，记录审计日志（审核通过/拒绝、下架等关键操作）。
 * 使用 AUDIT 独立 logger，在 Log4j2 配置中可单独配置审计输出。
 */
@Component
public class MeetingStatusChangedAuditListener {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @EventListener
    public void onMeetingStatusChanged(MeetingStatusChangedEvent event) {
        auditLogger.info("[AUDIT] Meeting status changed: meetingId={} from={} to={} actor={} timestamp={}",
                event.getMeetingId(),
                event.getFromStatus(),
                event.getToStatus(),
                event.getActor() != null ? event.getActor() : "UNKNOWN",
                event.getTimestamp());
    }
}
