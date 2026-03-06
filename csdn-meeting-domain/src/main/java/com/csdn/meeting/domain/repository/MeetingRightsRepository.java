package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.MeetingRights;

import java.util.List;
import java.util.Optional;

public interface MeetingRightsRepository {

    MeetingRights save(MeetingRights rights);

    Optional<MeetingRights> findActiveByMeetingId(Long meetingId);

    List<MeetingRights> findByMeetingId(Long meetingId);
}
