package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingAgendaItemJpaRepository extends JpaRepository<MeetingAgendaItemPO, Long> {

    List<MeetingAgendaItemPO> findByMeetingIdOrderBySortOrderAsc(Long meetingId);

    List<MeetingAgendaItemPO> findByMeetingIdAndParentIdOrderBySortOrderAsc(Long meetingId, Long parentId);

    List<MeetingAgendaItemPO> findByParentIdOrderBySortOrderAsc(Long parentId);

    void deleteByMeetingId(Long meetingId);
}
