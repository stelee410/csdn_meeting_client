package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Meeting;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository {

    Meeting save(Meeting meeting);

    Optional<Meeting> findById(Long id);

    Optional<Meeting> findByMeetingId(String meetingId);

    List<Meeting> findByCreatorId(Long creatorId);

    List<Meeting> findAll();

    void deleteById(Long id);

    void delete(Meeting meeting);
}
