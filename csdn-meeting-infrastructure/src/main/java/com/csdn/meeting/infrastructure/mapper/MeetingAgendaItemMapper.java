package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.ScheduleDay;
import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts between agenda tree (ScheduleDay) and MeetingAgendaItemPO list.
 * Delegates to AgendaTreeConverter.
 */
@Component
public class MeetingAgendaItemMapper {

    private final AgendaTreeConverter converter;

    public MeetingAgendaItemMapper(AgendaTreeConverter converter) {
        this.converter = converter;
    }

    public List<ScheduleDay> toScheduleDays(List<MeetingAgendaItemPO> items) {
        return converter.toScheduleDays(items);
    }

    public List<AgendaTreeConverter.AgendaItemData> toAgendaItemDataList(Long meetingId, List<ScheduleDay> scheduleDays) {
        return converter.toAgendaItemDataList(meetingId, scheduleDays);
    }
}
