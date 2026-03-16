package com.csdn.meeting.infrastructure.repository.impl.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsMeetingEvent;
import com.csdn.meeting.domain.repository.analytics.AnalyticsMeetingEventRepository;
import com.csdn.meeting.infrastructure.mapper.analytics.AnalyticsMeetingEventMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsMeetingEventPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议业务事件扩展仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class AnalyticsMeetingEventRepositoryImpl implements AnalyticsMeetingEventRepository {

    private final AnalyticsMeetingEventMapper meetingEventMapper;

    @Override
    public void save(AnalyticsMeetingEvent event) {
        AnalyticsMeetingEventPO po = convertToPO(event);
        meetingEventMapper.insert(po);
    }

    @Override
    public AnalyticsMeetingEvent findByEventId(String eventId) {
        AnalyticsMeetingEventPO po = meetingEventMapper.selectByEventId(eventId);
        return po != null ? convertToEntity(po) : null;
    }

    @Override
    public List<AnalyticsMeetingEvent> findByMeetingId(String meetingId) {
        List<AnalyticsMeetingEventPO> pos = meetingEventMapper.selectByMeetingId(meetingId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsMeetingEvent> findByMeetingIdAndActionType(String meetingId, String actionType) {
        List<AnalyticsMeetingEventPO> pos = meetingEventMapper.selectByMeetingIdAndActionType(meetingId, actionType);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsMeetingEvent> findByOrganizerId(String organizerId) {
        List<AnalyticsMeetingEventPO> pos = meetingEventMapper.selectByOrganizerId(organizerId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    private AnalyticsMeetingEventPO convertToPO(AnalyticsMeetingEvent event) {
        AnalyticsMeetingEventPO po = new AnalyticsMeetingEventPO();
        po.setEventId(event.getEventId());
        po.setMeetingId(event.getMeetingId());
        po.setMeetingTitle(event.getMeetingTitle());
        po.setOrganizerId(event.getOrganizerId());
        po.setActionType(event.getActionType());
        po.setSource(event.getSource());
        po.setReferrer(event.getReferrer());
        return po;
    }

    private AnalyticsMeetingEvent convertToEntity(AnalyticsMeetingEventPO po) {
        AnalyticsMeetingEvent event = new AnalyticsMeetingEvent();
        event.setId(po.getId());
        event.setEventId(po.getEventId());
        event.setMeetingId(po.getMeetingId());
        event.setMeetingTitle(po.getMeetingTitle());
        event.setOrganizerId(po.getOrganizerId());
        event.setActionType(po.getActionType());
        event.setSource(po.getSource());
        event.setReferrer(po.getReferrer());
        event.setCreatedAt(po.getCreatedAt());
        return event;
    }
}
