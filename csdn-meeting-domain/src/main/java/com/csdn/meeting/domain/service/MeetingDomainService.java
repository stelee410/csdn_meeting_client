package com.csdn.meeting.domain.service;

import com.csdn.meeting.domain.repository.MeetingRepository;

import java.util.UUID;

public class MeetingDomainService {

    private final MeetingRepository meetingRepository;

    public MeetingDomainService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public String generateMeetingId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public boolean isMeetingExists(String meetingId) {
        return meetingRepository.findByMeetingId(meetingId).isPresent();
    }
}
