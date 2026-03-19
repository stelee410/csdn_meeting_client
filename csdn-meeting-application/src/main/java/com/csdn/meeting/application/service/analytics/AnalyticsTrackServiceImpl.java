package com.csdn.meeting.application.service.analytics;

import com.csdn.meeting.application.dto.TrackEventCommand;
import com.csdn.meeting.domain.entity.analytics.TrackEvent;
import com.csdn.meeting.domain.repository.analytics.TrackEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 埋点追踪服务实现
 * 提供异步的埋点事件处理能力
 */
@Service
public class AnalyticsTrackServiceImpl implements AnalyticsTrackService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsTrackServiceImpl.class);

    private final TrackEventRepository trackEventRepository;

    public AnalyticsTrackServiceImpl(TrackEventRepository trackEventRepository) {
        this.trackEventRepository = trackEventRepository;
    }

    @Override
    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void trackEvent(TrackEventCommand command, String ipAddress, String userAgent) {
        try {
            TrackEvent event = convertToEntity(command, ipAddress, userAgent);
            trackEventRepository.save(event);
            logger.debug("Tracked event: module={}, action={}, userId={}",
                    event.getModule(), event.getAction(), event.getUserId());
        } catch (Exception e) {
            logger.error("Failed to track event: module={}, action={}",
                    command.getModule(), command.getAction(), e);
        }
    }

    @Override
    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void trackEvents(List<TrackEventCommand> commands, String ipAddress, String userAgent) {
        if (commands == null || commands.isEmpty()) {
            return;
        }
        try {
            List<TrackEvent> events = commands.stream()
                    .map(cmd -> convertToEntity(cmd, ipAddress, userAgent))
                    .collect(Collectors.toList());
            trackEventRepository.saveBatch(events);
            logger.debug("Tracked {} events in batch", events.size());
        } catch (Exception e) {
            logger.error("Failed to track events in batch, count={}", commands.size(), e);
        }
    }

    /**
     * 将命令对象转换为领域实体
     */
    private TrackEvent convertToEntity(TrackEventCommand command, String ipAddress, String userAgent) {
        TrackEvent event = new TrackEvent();
        event.setEventId(command.getEventId());
        event.setModule(command.getModule());
        event.setAction(command.getAction());
        event.setEventType(command.getEventType());
        event.setUserId(command.getUserId());
        event.setAnonymousId(command.getAnonymousId());
        event.setSessionId(command.getSessionId());
        event.setDeviceId(command.getDeviceId());
        event.setPlatform(command.getPlatform());
        event.setAppVersion(command.getAppVersion());
        event.setOccurredAt(command.getOccurredAt());
        event.setProperties(command.getProperties());
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        event.setReceivedAt(LocalDateTime.now());
        return event;
    }
}
