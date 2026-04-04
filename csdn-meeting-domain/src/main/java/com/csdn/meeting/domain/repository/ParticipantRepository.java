package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Participant;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {

    Participant save(Participant participant);

    Optional<Participant> findById(Long id);

    List<Participant> findByMeetingId(String meetingId);

    Optional<Participant> findByMeetingIdAndUserId(String meetingId, String userId);

    List<Participant> findAll();

    void deleteById(Long id);

    void delete(Participant participant);
}
