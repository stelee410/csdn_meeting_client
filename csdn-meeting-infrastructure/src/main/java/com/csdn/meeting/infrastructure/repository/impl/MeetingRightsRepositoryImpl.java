package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingRights;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import com.csdn.meeting.infrastructure.po.MeetingRightsPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingRightsPOMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingRightsRepositoryImpl implements MeetingRightsRepository {

    private final MeetingRightsPOMapper rightsPOMapper;

    public MeetingRightsRepositoryImpl(MeetingRightsPOMapper rightsPOMapper) {
        this.rightsPOMapper = rightsPOMapper;
    }

    @Override
    public MeetingRights save(MeetingRights rights) {
        MeetingRightsPO po = toPO(rights);
        if (po.getId() == null) {
            rightsPOMapper.insert(po);
        } else {
            rightsPOMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<MeetingRights> findActiveByMeetingId(Long meetingId) {
        MeetingRightsPO po = rightsPOMapper.selectFirstByMeetingIdAndStatus(meetingId, MeetingRights.STATUS_ACTIVE);
        return po == null ? Optional.empty() : Optional.of(toEntity(po));
    }

    @Override
    public List<MeetingRights> findByMeetingId(Long meetingId) {
        return rightsPOMapper.selectByMeetingId(meetingId).stream()
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
