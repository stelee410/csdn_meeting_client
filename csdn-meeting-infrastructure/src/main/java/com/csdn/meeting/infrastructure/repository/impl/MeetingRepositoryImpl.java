package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.repository.MeetingJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingRepositoryImpl implements MeetingRepository {

    private final MeetingJpaRepository jpaRepository;

    public MeetingRepositoryImpl(MeetingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Meeting save(Meeting meeting) {
        MeetingPO po = toPO(meeting);
        MeetingPO saved = jpaRepository.save(po);
        return toEntity(saved);
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        return jpaRepository.findById(id).map(this::toEntity);
    }

    @Override
    public Optional<Meeting> findByMeetingId(String meetingId) {
        return jpaRepository.findByMeetingId(meetingId).map(this::toEntity);
    }

    @Override
    public List<Meeting> findByCreatorId(Long creatorId) {
        return jpaRepository.findByCreatorId(creatorId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meeting> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Meeting meeting) {
        jpaRepository.deleteById(meeting.getId());
    }

    private MeetingPO toPO(Meeting meeting) {
        MeetingPO po = new MeetingPO();
        po.setId(meeting.getId());
        po.setMeetingId(meeting.getMeetingId());
        po.setTitle(meeting.getTitle());
        po.setDescription(meeting.getDescription());
        po.setCreatorId(meeting.getCreatorId());
        po.setCreatorName(meeting.getCreatorName());
        po.setStartTime(meeting.getStartTime());
        po.setEndTime(meeting.getEndTime());
        po.setStatus(MeetingPO.MeetingStatus.valueOf(meeting.getStatus().name()));
        po.setMaxParticipants(meeting.getMaxParticipants());
        return po;
    }

    private Meeting toEntity(MeetingPO po) {
        Meeting meeting = new Meeting();
        meeting.setId(po.getId());
        meeting.setMeetingId(po.getMeetingId());
        meeting.setTitle(po.getTitle());
        meeting.setDescription(po.getDescription());
        meeting.setCreatorId(po.getCreatorId());
        meeting.setCreatorName(po.getCreatorName());
        meeting.setStartTime(po.getStartTime());
        meeting.setEndTime(po.getEndTime());
        meeting.setStatus(Meeting.MeetingStatus.valueOf(po.getStatus().name()));
        meeting.setMaxParticipants(po.getMaxParticipants());
        return meeting;
    }
}
