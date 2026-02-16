package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.ParticipantPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantJpaRepository extends JpaRepository<ParticipantPO, Long> {

    List<ParticipantPO> findByMeetingId(String meetingId);

    Optional<ParticipantPO> findByMeetingIdAndUserId(String meetingId, Long userId);
}
