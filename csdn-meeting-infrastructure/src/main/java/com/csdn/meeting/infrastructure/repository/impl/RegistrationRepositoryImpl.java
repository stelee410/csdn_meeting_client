package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import com.csdn.meeting.infrastructure.mapper.RegistrationMapper;
import com.csdn.meeting.infrastructure.po.RegistrationPO;
import com.csdn.meeting.infrastructure.repository.mapper.RegistrationPOMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RegistrationRepositoryImpl implements RegistrationRepository {

    private final RegistrationPOMapper registrationPOMapper;

    public RegistrationRepositoryImpl(RegistrationPOMapper registrationPOMapper) {
        this.registrationPOMapper = registrationPOMapper;
    }

    @Override
    public Registration save(Registration registration) {
        RegistrationPO po = RegistrationMapper.INSTANCE.toPO(registration);
        if (po.getId() == null) {
            registrationPOMapper.insert(po);
        } else {
            registrationPOMapper.updateById(po);
        }
        return RegistrationMapper.INSTANCE.toEntity(po);
    }

    @Override
    public Optional<Registration> findById(Long id) {
        RegistrationPO po = registrationPOMapper.selectById(id);
        return po == null ? Optional.empty() : Optional.of(RegistrationMapper.INSTANCE.toEntity(po));
    }

    @Override
    public PageResult<Registration> findByMeetingIdAndStatus(Long meetingId,
                                                             Registration.RegistrationStatus status,
                                                             int page, int size) {
        Page<RegistrationPO> pageParam = new Page<>(page + 1, size);
        IPage<RegistrationPO> springPage = status == null
                ? registrationPOMapper.selectPageByMeetingId(pageParam, meetingId)
                : registrationPOMapper.selectPageByMeetingIdAndStatus(pageParam, meetingId, status.name());
        List<Registration> content = springPage.getRecords().stream()
                .map(RegistrationMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotal(), page, size);
    }

    @Override
    public Optional<Registration> findByUserIdAndMeetingId(String userId, Long meetingId) {
        RegistrationPO po = registrationPOMapper.selectByUserIdAndMeetingId(userId, meetingId);
        return po == null ? Optional.empty() : Optional.of(RegistrationMapper.INSTANCE.toEntity(po));
    }

    @Override
    public List<Registration> findByMeetingIdAndPhone(Long meetingId, String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RegistrationPO> list = registrationPOMapper.selectByMeetingIdAndPhone(meetingId, phone.trim());
        return list == null ? Collections.emptyList() : list.stream()
                .map(RegistrationMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<Registration> findByUserIdAndMeetingStatusIn(String userId,
                                                                   List<Meeting.MeetingStatus> meetingStatuses,
                                                                   int page, int size) {
        if (meetingStatuses == null || meetingStatuses.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, page, size);
        }
        List<Integer> codes = meetingStatuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
        Page<RegistrationPO> pageParam = new Page<>(page + 1, size);
        IPage<RegistrationPO> springPage = registrationPOMapper.selectPageByUserIdAndMeetingStatusIn(pageParam, userId, codes);
        List<Registration> content = springPage.getRecords().stream()
                .map(RegistrationMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotal(), page, size);
    }

    @Override
    public void deleteById(Long id) {
        registrationPOMapper.deleteById(id);
    }

    @Override
    public long countByMeetingIdAndStatuses(Long meetingId, Collection<Registration.RegistrationStatus> statuses) {
        if (meetingId == null || statuses == null || statuses.isEmpty()) {
            return 0;
        }
        LambdaQueryWrapper<RegistrationPO> w = new LambdaQueryWrapper<>();
        w.eq(RegistrationPO::getMeetingId, meetingId);
        w.in(RegistrationPO::getStatus, statuses.stream().map(Enum::name).collect(Collectors.toList()));
        Long c = registrationPOMapper.selectCount(w);
        return c == null ? 0 : c;
    }
}
