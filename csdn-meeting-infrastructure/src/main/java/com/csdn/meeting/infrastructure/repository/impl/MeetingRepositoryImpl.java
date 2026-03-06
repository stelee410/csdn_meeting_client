package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.infrastructure.mapper.AgendaTreeConverter;
import com.csdn.meeting.infrastructure.mapper.MeetingMapper;
import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.repository.MeetingAgendaItemJpaRepository;
import com.csdn.meeting.infrastructure.repository.MeetingJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingRepositoryImpl implements MeetingRepository {

    private final MeetingJpaRepository jpaRepository;
    private final MeetingAgendaItemJpaRepository agendaItemJpaRepository;
    private final AgendaTreeConverter agendaTreeConverter;

    public MeetingRepositoryImpl(MeetingJpaRepository jpaRepository,
                                 MeetingAgendaItemJpaRepository agendaItemJpaRepository,
                                 AgendaTreeConverter agendaTreeConverter) {
        this.jpaRepository = jpaRepository;
        this.agendaItemJpaRepository = agendaItemJpaRepository;
        this.agendaTreeConverter = agendaTreeConverter;
    }

    @Override
    @Transactional
    public Meeting save(Meeting meeting) {
        MeetingPO po = MeetingMapper.INSTANCE.toPO(meeting);
        MeetingPO saved = jpaRepository.save(po);
        Long meetingId = saved.getId();

        agendaItemJpaRepository.deleteByMeetingId(meetingId);

        if (meeting.getScheduleDays() != null && !meeting.getScheduleDays().isEmpty()) {
            List<AgendaTreeConverter.AgendaItemData> dataList = agendaTreeConverter.toAgendaItemDataList(meetingId, meeting.getScheduleDays());
            List<Long> savedIds = new ArrayList<>();
            for (AgendaTreeConverter.AgendaItemData data : dataList) {
                Long parentId = data.parentIndex >= 0 ? savedIds.get(data.parentIndex) : null;
                MeetingAgendaItemPO itemPo = agendaTreeConverter.toPO(data, meetingId, parentId);
                MeetingAgendaItemPO savedItem = agendaItemJpaRepository.save(itemPo);
                savedIds.add(savedItem.getId());
            }
        }

        Meeting result = MeetingMapper.INSTANCE.toEntity(saved);
        result.setScheduleDays(meeting.getScheduleDays());
        return result;
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        return jpaRepository.findById(id)
                .map(po -> {
                    Meeting m = MeetingMapper.INSTANCE.toEntity(po);
                    List<MeetingAgendaItemPO> items = agendaItemJpaRepository.findByMeetingIdOrderBySortOrderAsc(id);
                    m.setScheduleDays(agendaTreeConverter.toScheduleDays(items));
                    return m;
                });
    }

    @Override
    public Optional<Meeting> findByMeetingId(String meetingId) {
        try {
            Long id = Long.parseLong(meetingId);
            return findById(id);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Meeting> findByCreatorId(Long creatorId) {
        return loadWithAgenda(jpaRepository.findByCreatorId(creatorId));
    }

    @Override
    public List<Meeting> findByCreatorIdAndStatus(Long creatorId, Meeting.MeetingStatus status) {
        return loadWithAgenda(jpaRepository.findByCreatorIdAndStatus(creatorId, status.getCode()));
    }

    @Override
    public List<Meeting> findByCreatorIdAndStartTimeBetween(Long creatorId, LocalDateTime start, LocalDateTime end) {
        return loadWithAgenda(jpaRepository.findByCreatorIdAndStartTimeBetween(creatorId, start, end));
    }

    @Override
    public PageResult<Meeting> findPageByCreatorId(Long creatorId,
                                                   List<Meeting.MeetingStatus> statuses,
                                                   LocalDateTime startFrom,
                                                   LocalDateTime endTo,
                                                   int page, int size) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<MeetingPO> springPage;
        boolean hasStatus = statuses != null && !statuses.isEmpty();
        boolean hasDateRange = startFrom != null || endTo != null;
        LocalDateTime start = startFrom != null ? startFrom : LocalDateTime.MIN;
        LocalDateTime end = endTo != null ? endTo : LocalDateTime.MAX;

        if (!hasStatus && !hasDateRange) {
            springPage = jpaRepository.findByCreatorIdOrderByStartTimeDesc(creatorId, pageable);
        } else if (hasStatus && !hasDateRange) {
            List<Integer> codes = statuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
            springPage = jpaRepository.findByCreatorIdAndStatusInOrderByStartTimeDesc(creatorId, codes, pageable);
        } else if (!hasStatus && hasDateRange) {
            springPage = jpaRepository.findByCreatorIdAndStartTimeBetweenOrderByStartTimeDesc(creatorId, start, end, pageable);
        } else {
            List<Integer> codes = statuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
            springPage = jpaRepository.findByCreatorIdAndStatusInAndStartTimeBetweenOrderByStartTimeDesc(creatorId, codes, start, end, pageable);
        }
        List<Meeting> content = loadWithAgenda(springPage.getContent());
        return new PageResult<>(content, springPage.getTotalElements(), page, size);
    }

    @Override
    public List<Meeting> findByStatus(Meeting.MeetingStatus status) {
        return loadWithAgenda(jpaRepository.findByStatus(status.getCode()));
    }

    @Override
    public List<Meeting> findPublishedWithStartTimeBefore(LocalDateTime threshold) {
        return loadWithAgenda(jpaRepository.findByStatusAndStartTimeLessThanEqual(
                Meeting.MeetingStatus.PUBLISHED.getCode(), threshold));
    }

    @Override
    public List<Meeting> findInProgressWithEndTimeBefore(LocalDateTime threshold) {
        return loadWithAgenda(jpaRepository.findByStatusAndEndTimeLessThanEqual(
                Meeting.MeetingStatus.IN_PROGRESS.getCode(), threshold));
    }

    @Override
    public List<Meeting> findAll() {
        return loadWithAgenda(jpaRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        agendaItemJpaRepository.deleteByMeetingId(id);
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(Meeting meeting) {
        Long id = meeting.getId();
        if (id != null) {
            deleteById(id);
        }
    }

    private List<Meeting> loadWithAgenda(List<MeetingPO> pos) {
        return pos.stream().map(po -> {
            Meeting m = MeetingMapper.INSTANCE.toEntity(po);
            List<MeetingAgendaItemPO> items = agendaItemJpaRepository.findByMeetingIdOrderBySortOrderAsc(po.getId());
            m.setScheduleDays(agendaTreeConverter.toScheduleDays(items));
            return m;
        }).collect(Collectors.toList());
    }
}
