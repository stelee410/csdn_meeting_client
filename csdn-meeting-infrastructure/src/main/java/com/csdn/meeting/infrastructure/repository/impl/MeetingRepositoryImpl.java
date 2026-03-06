package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.infrastructure.converter.MeetingConverter;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会议仓储实现（MyBatisPlus版本）
 * 用于与原有Meeting实体的兼容
 */
@Repository
public class MeetingRepositoryImpl implements MeetingRepository {

    private final MeetingBaseMapper meetingBaseMapper;

    public MeetingRepositoryImpl(MeetingBaseMapper meetingBaseMapper) {
        this.meetingBaseMapper = meetingBaseMapper;
    }

    @Override
    public Meeting save(Meeting meeting) {
        MeetingPO po = MeetingConverter.INSTANCE.entityToPo(meeting);
        if (po.getId() == null) {
            meetingBaseMapper.insert(po);
        } else {
            meetingBaseMapper.updateById(po);
        }
        return MeetingConverter.INSTANCE.poToEntity(po);
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        MeetingPO po = meetingBaseMapper.selectById(id);
        return Optional.ofNullable(po).map(MeetingConverter.INSTANCE::poToEntity);
    }

    @Override
    public Optional<Meeting> findByMeetingId(String meetingId) {
        LambdaQueryWrapper<MeetingPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingPO::getMeetingId, meetingId)
                .eq(MeetingPO::getIsDeleted, 0);
        MeetingPO po = meetingBaseMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(MeetingConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Meeting> findByCreatorId(Long creatorId) {
        LambdaQueryWrapper<MeetingPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeetingPO::getCreatorId, creatorId)
                .eq(MeetingPO::getIsDeleted, 0);
        List<MeetingPO> poList = meetingBaseMapper.selectList(wrapper);
        return poList.stream()
                .map(MeetingConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meeting> findAll() {
        List<MeetingPO> poList = meetingBaseMapper.selectList(
                new LambdaQueryWrapper<MeetingPO>().eq(MeetingPO::getIsDeleted, 0)
        );
        return poList.stream()
                .map(MeetingConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        meetingBaseMapper.deleteById(id);
    }

    @Override
    public void delete(Meeting meeting) {
        meetingBaseMapper.deleteById(meeting.getId());
    }
}
