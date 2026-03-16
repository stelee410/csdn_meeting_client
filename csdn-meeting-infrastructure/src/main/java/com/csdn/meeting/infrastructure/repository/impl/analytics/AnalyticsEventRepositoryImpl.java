package com.csdn.meeting.infrastructure.repository.impl.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsEvent;
import com.csdn.meeting.domain.repository.analytics.AnalyticsEventRepository;
import com.csdn.meeting.infrastructure.mapper.analytics.AnalyticsEventMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsEventPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 埋点事件仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class AnalyticsEventRepositoryImpl implements AnalyticsEventRepository {

    private final AnalyticsEventMapper eventMapper;

    @Override
    public void save(AnalyticsEvent event) {
        AnalyticsEventPO po = convertToPO(event);
        eventMapper.insert(po);
    }

    @Override
    public void saveBatch(List<AnalyticsEvent> events) {
        for (AnalyticsEvent event : events) {
            save(event);
        }
    }

    @Override
    public AnalyticsEvent findByEventId(String eventId) {
        AnalyticsEventPO po = eventMapper.selectByEventId(eventId);
        return po != null ? convertToEntity(po) : null;
    }

    @Override
    public List<AnalyticsEvent> findByUserIdAndTimeRange(String userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<AnalyticsEventPO> pos = eventMapper.selectByUserIdAndTimeRange(userId, startTime, endTime);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsEvent> findByEventTypeAndTimeRange(String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        List<AnalyticsEventPO> pos = eventMapper.selectByEventTypeAndTimeRange(eventType, startTime, endTime);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsEvent> findByMeetingId(String meetingId) {
        List<AnalyticsEventPO> pos = eventMapper.selectByMeetingId(meetingId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        Long count = eventMapper.countByTimeRange(startTime, endTime);
        return count != null ? count : 0;
    }

    @Override
    public long countByEventTypeAndTimeRange(String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        Long count = eventMapper.countByEventTypeAndTimeRange(eventType, startTime, endTime);
        return count != null ? count : 0;
    }

    private AnalyticsEventPO convertToPO(AnalyticsEvent event) {
        AnalyticsEventPO po = new AnalyticsEventPO();
        po.setEventId(event.getEventId());
        po.setEventType(event.getEventType());
        po.setEventCategory(event.getEventCategory());
        po.setUserId(event.getUserId());
        po.setUserType(event.getUserType());
        po.setAnonymousId(event.getAnonymousId());
        po.setSessionId(event.getSessionId());
        po.setDeviceId(event.getDeviceId());
        po.setPlatform(event.getPlatform());
        po.setAppVersion(event.getAppVersion());
        po.setIpAddress(event.getIpAddress());
        po.setUserAgent(event.getUserAgent());
        po.setOccurredAt(event.getOccurredAt());
        return po;
    }

    private AnalyticsEvent convertToEntity(AnalyticsEventPO po) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setId(po.getId());
        event.setEventId(po.getEventId());
        event.setEventType(po.getEventType());
        event.setEventCategory(po.getEventCategory());
        event.setUserId(po.getUserId());
        event.setUserType(po.getUserType());
        event.setAnonymousId(po.getAnonymousId());
        event.setSessionId(po.getSessionId());
        event.setDeviceId(po.getDeviceId());
        event.setPlatform(po.getPlatform());
        event.setAppVersion(po.getAppVersion());
        event.setIpAddress(po.getIpAddress());
        event.setUserAgent(po.getUserAgent());
        event.setOccurredAt(po.getOccurredAt());
        event.setCreatedAt(po.getCreatedAt());
        return event;
    }
}
