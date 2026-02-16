package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingJpaRepository extends JpaRepository<MeetingPO, Long> {

    Optional<MeetingPO> findByMeetingId(String meetingId);

    List<MeetingPO> findByCreatorId(Long creatorId);
}
