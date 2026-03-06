package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import com.csdn.meeting.infrastructure.mapper.RegistrationMapper;
import com.csdn.meeting.infrastructure.po.RegistrationPO;
import com.csdn.meeting.infrastructure.repository.RegistrationJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RegistrationRepositoryImpl implements RegistrationRepository {

    private final RegistrationJpaRepository jpaRepository;

    public RegistrationRepositoryImpl(RegistrationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Registration save(Registration registration) {
        RegistrationPO po = RegistrationMapper.INSTANCE.toPO(registration);
        RegistrationPO saved = jpaRepository.save(po);
        return RegistrationMapper.INSTANCE.toEntity(saved);
    }

    @Override
    public Optional<Registration> findById(Long id) {
        return jpaRepository.findById(id).map(RegistrationMapper.INSTANCE::toEntity);
    }

    @Override
    public PageResult<Registration> findByMeetingIdAndStatus(Long meetingId,
                                                             Registration.RegistrationStatus status,
                                                             int page, int size) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size);
        Page<RegistrationPO> springPage = status == null
                ? jpaRepository.findByMeetingId(meetingId, pageable)
                : jpaRepository.findByMeetingIdAndStatus(meetingId, status.name(), pageable);
        List<Registration> content = springPage.getContent().stream()
                .map(RegistrationMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotalElements(), page, size);
    }

    @Override
    public Optional<Registration> findByUserIdAndMeetingId(Long userId, Long meetingId) {
        return jpaRepository.findByUserIdAndMeetingId(userId, meetingId)
                .map(RegistrationMapper.INSTANCE::toEntity);
    }

    @Override
    public PageResult<Registration> findByUserIdAndMeetingStatusIn(Long userId,
                                                                   List<Meeting.MeetingStatus> meetingStatuses,
                                                                   int page, int size) {
        if (meetingStatuses == null || meetingStatuses.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, page, size);
        }
        List<Integer> codes = meetingStatuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
        org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<RegistrationPO> springPage =
                jpaRepository.findByUserIdAndMeetingStatusIn(userId, codes, pageable);
        List<Registration> content = springPage.getContent().stream()
                .map(RegistrationMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotalElements(), page, size);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
