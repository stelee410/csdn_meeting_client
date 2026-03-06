package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingRightsPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRightsJpaRepository extends JpaRepository<MeetingRightsPO, Long> {

    Optional<MeetingRightsPO> findFirstByMeetingIdAndStatus(Long meetingId, String status);

    List<MeetingRightsPO> findByMeetingId(Long meetingId);
}
