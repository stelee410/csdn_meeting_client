package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingBillPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingBillJpaRepository extends JpaRepository<MeetingBillPO, Long> {

    List<MeetingBillPO> findByMeetingId(Long meetingId);
}
