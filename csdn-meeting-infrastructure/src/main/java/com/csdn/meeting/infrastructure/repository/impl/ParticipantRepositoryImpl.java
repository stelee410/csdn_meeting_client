package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.domain.repository.ParticipantRepository;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import com.csdn.meeting.infrastructure.repository.ParticipantJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {

    private final ParticipantJpaRepository jpaRepository;

    public ParticipantRepositoryImpl(ParticipantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Participant save(Participant participant) {
        ParticipantPO po = toPO(participant);
        ParticipantPO saved = jpaRepository.save(po);
        return toEntity(saved);
    }

    @Override
    public Optional<Participant> findById(Long id) {
        return jpaRepository.findById(id).map(this::toEntity);
    }

    @Override
    public List<Participant> findByMeetingId(String meetingId) {
        return jpaRepository.findByMeetingId(meetingId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Participant> findByMeetingIdAndUserId(String meetingId, Long userId) {
        return jpaRepository.findByMeetingIdAndUserId(meetingId, userId).map(this::toEntity);
    }

    @Override
    public List<Participant> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Participant participant) {
        jpaRepository.deleteById(participant.getId());
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
