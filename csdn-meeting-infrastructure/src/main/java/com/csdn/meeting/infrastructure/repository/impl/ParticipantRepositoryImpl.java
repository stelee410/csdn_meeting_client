package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.domain.repository.ParticipantRepository;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import com.csdn.meeting.infrastructure.repository.mapper.ParticipantPOMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 参与者仓储实现（MyBatisPlus版本）
 */
@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {

    private final ParticipantPOMapper participantPOMapper;

    public ParticipantRepositoryImpl(ParticipantPOMapper participantPOMapper) {
        this.participantPOMapper = participantPOMapper;
    }

    @Override
    public Participant save(Participant participant) {
        ParticipantPO po = toPO(participant);
        if (po.getId() == null) {
            participantPOMapper.insert(po);
        } else {
            participantPOMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<Participant> findById(Long id) {
        ParticipantPO po = participantPOMapper.selectById(id);
        return po == null ? Optional.empty() : Optional.of(toEntity(po));
    }

    @Override
    public List<Participant> findByMeetingId(String meetingId) {
        return participantPOMapper.selectByMeetingId(meetingId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Participant> findByMeetingIdAndUserId(String meetingId, String userId) {
        ParticipantPO po = participantPOMapper.selectByMeetingIdAndUserId(meetingId, userId);
        return po == null ? Optional.empty() : Optional.of(toEntity(po));
    }

    @Override
    public List<Participant> findAll() {
        return participantPOMapper.selectList(null).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        participantPOMapper.deleteById(id);
    }

    @Override
    public void delete(Participant participant) {
        participantPOMapper.deleteById(participant.getId());
    }

    private ParticipantPO toPO(Participant participant) {
        ParticipantPO po = new ParticipantPO();
        po.setId(participant.getId());
        po.setUserId(participant.getUserId());
        po.setUserName(participant.getUserName());
        po.setMeetingId(participant.getMeetingId());
        po.setRole(ParticipantPO.ParticipantRole.valueOf(participant.getRole().name()));
        po.setStatus(ParticipantPO.ParticipantStatus.valueOf(participant.getStatus().name()));
        return po;
    }

    private Participant toEntity(ParticipantPO po) {
        Participant participant = new Participant();
        participant.setId(po.getId());
        participant.setUserId(po.getUserId());
        participant.setUserName(po.getUserName());
        participant.setMeetingId(po.getMeetingId());
        participant.setRole(Participant.ParticipantRole.valueOf(po.getRole().name()));
        participant.setStatus(Participant.ParticipantStatus.valueOf(po.getStatus().name()));
        return participant;
    }
}
