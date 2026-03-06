package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.domain.repository.ParticipantRepository;
import com.csdn.meeting.infrastructure.converter.ParticipantConverter;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import com.csdn.meeting.infrastructure.repository.mapper.ParticipantMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 参与者仓储实现（MyBatisPlus版本）
 */
@Repository
public class ParticipantRepositoryImpl implements ParticipantRepository {

    private final ParticipantMapper participantMapper;

    public ParticipantRepositoryImpl(ParticipantMapper participantMapper) {
        this.participantMapper = participantMapper;
    }

    @Override
    public Participant save(Participant participant) {
        ParticipantPO po = ParticipantConverter.INSTANCE.entityToPo(participant);
        if (po.getId() == null) {
            participantMapper.insert(po);
        } else {
            participantMapper.updateById(po);
        }
        return ParticipantConverter.INSTANCE.poToEntity(po);
    }

    @Override
    public Optional<Participant> findById(Long id) {
        ParticipantPO po = participantMapper.selectById(id);
        return Optional.ofNullable(po).map(ParticipantConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Participant> findByMeetingId(String meetingId) {
        List<ParticipantPO> poList = participantMapper.selectByMeetingId(meetingId);
        return poList.stream()
                .map(ParticipantConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Participant> findByMeetingIdAndUserId(String meetingId, Long userId) {
        LambdaQueryWrapper<ParticipantPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParticipantPO::getMeetingId, meetingId)
                .eq(ParticipantPO::getUserId, userId)
                .eq(ParticipantPO::getIsDeleted, 0);
        ParticipantPO po = participantMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(ParticipantConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Participant> findAll() {
        List<ParticipantPO> poList = participantMapper.selectList(
                new LambdaQueryWrapper<ParticipantPO>().eq(ParticipantPO::getIsDeleted, 0)
        );
        return poList.stream()
                .map(ParticipantConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        participantMapper.deleteById(id);
    }

    @Override
    public void delete(Participant participant) {
        participantMapper.deleteById(participant.getId());
    }
}
