package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingRights;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import com.csdn.meeting.infrastructure.po.MeetingRightsPO;
import com.csdn.meeting.infrastructure.repository.MeetingRightsJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingRightsRepositoryImpl implements MeetingRightsRepository {

    private final MeetingRightsJpaRepository jpaRepository;

    public MeetingRightsRepositoryImpl(MeetingRightsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MeetingRights save(MeetingRights rights) {
        MeetingRightsPO po = toPO(rights);
        MeetingRightsPO saved = jpaRepository.save(po);
        return toEntity(saved);
    }

    @Override
    public Optional<MeetingRights> findActiveByMeetingId(Long meetingId) {
        return jpaRepository.findFirstByMeetingIdAndStatus(meetingId, MeetingRights.STATUS_ACTIVE)
                .map(this::toEntity);
    }

    @Override
    public List<MeetingRights> findByMeetingId(Long meetingId) {
        return jpaRepository.findByMeetingId(meetingId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private MeetingRightsPO toPO(MeetingRights e) {
        MeetingRightsPO po = new MeetingRightsPO();
        po.setId(e.getId());
        po.setMeetingId(e.getMeetingId());
        po.setRightsType(e.getRightsType());
        po.setStatus(e.getStatus());
        po.setActiveTime(e.getActiveTime());
        po.setOrderNo(e.getOrderNo());
        po.setCreatedAt(e.getCreatedAt());
        return po;
    }

    private MeetingRights toEntity(MeetingRightsPO po) {
        MeetingRights e = new MeetingRights();
        e.setId(po.getId());
        e.setMeetingId(po.getMeetingId());
        e.setRightsType(po.getRightsType());
        e.setStatus(po.getStatus());
        e.setActiveTime(po.getActiveTime());
        e.setOrderNo(po.getOrderNo());
        e.setCreatedAt(po.getCreatedAt());
        return e;
    }
}
