package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.infrastructure.mapper.AgendaTreeConverter;
import com.csdn.meeting.infrastructure.mapper.MeetingMapper;
import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingAgendaItemPOMapper;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingPOMapper;
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

    private final MeetingPOMapper meetingPOMapper;
    private final MeetingAgendaItemPOMapper agendaItemPOMapper;
    private final AgendaTreeConverter agendaTreeConverter;

    public MeetingRepositoryImpl(MeetingPOMapper meetingPOMapper,
                                 MeetingAgendaItemPOMapper agendaItemPOMapper,
                                 AgendaTreeConverter agendaTreeConverter) {
        this.meetingPOMapper = meetingPOMapper;
        this.agendaItemPOMapper = agendaItemPOMapper;
        this.agendaTreeConverter = agendaTreeConverter;
    }

    @Override
    @Transactional
    public Meeting save(Meeting meeting) {
        MeetingPO po = MeetingMapper.INSTANCE.toPO(meeting);
        if (po.getId() == null) {
            meetingPOMapper.insert(po);
        } else {
            meetingPOMapper.updateById(po);
        }
        Long meetingId = po.getId();
        agendaItemPOMapper.deleteByMeetingId(meetingId);

        if (meeting.getScheduleDays() != null && !meeting.getScheduleDays().isEmpty()) {
            List<AgendaTreeConverter.AgendaItemData> dataList = agendaTreeConverter.toAgendaItemDataList(meetingId, meeting.getScheduleDays());
            List<Long> savedIds = new ArrayList<>();
            for (AgendaTreeConverter.AgendaItemData data : dataList) {
                Long parentId = data.parentIndex >= 0 ? savedIds.get(data.parentIndex) : null;
                MeetingAgendaItemPO itemPo = agendaTreeConverter.toPO(data, meetingId, parentId);
                agendaItemPOMapper.insert(itemPo);
                savedIds.add(itemPo.getId());
            }
        }

        Meeting result = MeetingMapper.INSTANCE.toEntity(po);
        result.setScheduleDays(meeting.getScheduleDays());
        return result;
    }

    @Override
    public Optional<Meeting> findById(Long id) {
        MeetingPO po = meetingPOMapper.selectById(id);
        if (po == null) return Optional.empty();
        Meeting m = MeetingMapper.INSTANCE.toEntity(po);
        List<MeetingAgendaItemPO> items = agendaItemPOMapper.selectByMeetingIdOrderBySortOrderAsc(id);
        m.setScheduleDays(agendaTreeConverter.toScheduleDays(items));
        return Optional.of(m);
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
        return loadWithAgenda(meetingPOMapper.selectByCreatorId(creatorId));
    }

    @Override
    public List<Meeting> findByCreatorIdAndStatus(Long creatorId, Meeting.MeetingStatus status) {
        return loadWithAgenda(meetingPOMapper.selectByCreatorIdAndStatus(creatorId, status.getCode()));
    }

    @Override
    public List<Meeting> findByCreatorIdAndStartTimeBetween(Long creatorId, LocalDateTime start, LocalDateTime end) {
        return loadWithAgenda(meetingPOMapper.selectByCreatorIdAndStartTimeBetween(creatorId, start, end));
    }

    @Override
    public PageResult<Meeting> findPageByCreatorId(Long creatorId,
                                                   List<Meeting.MeetingStatus> statuses,
                                                   LocalDateTime startFrom,
                                                   LocalDateTime endTo,
                                                   int page, int size) {
        Page<MeetingPO> pageParam = new Page<>(page + 1, size);
        IPage<MeetingPO> springPage;
        boolean hasStatus = statuses != null && !statuses.isEmpty();
        boolean hasDateRange = startFrom != null || endTo != null;
        LocalDateTime start = startFrom != null ? startFrom : LocalDateTime.MIN;
        LocalDateTime end = endTo != null ? endTo : LocalDateTime.MAX;

        if (!hasStatus && !hasDateRange) {
            springPage = meetingPOMapper.selectPageByCreatorId(pageParam, creatorId);
        } else if (hasStatus && !hasDateRange) {
            List<Integer> codes = statuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
            springPage = meetingPOMapper.selectPageByCreatorIdAndStatusIn(pageParam, creatorId, codes);
        } else if (!hasStatus && hasDateRange) {
            springPage = meetingPOMapper.selectPageByCreatorIdAndStartTimeBetween(pageParam, creatorId, start, end);
        } else {
            List<Integer> codes = statuses.stream().map(Meeting.MeetingStatus::getCode).collect(Collectors.toList());
            springPage = meetingPOMapper.selectPageByCreatorIdAndStatusInAndStartTimeBetween(pageParam, creatorId, codes, start, end);
        }
        List<Meeting> content = loadWithAgenda(springPage.getRecords());
        return new PageResult<>(content, springPage.getTotal(), page, size);
    }

    @Override
    public List<Meeting> findByStatus(Meeting.MeetingStatus status) {
        return loadWithAgenda(meetingPOMapper.selectByStatus(status.getCode()));
    }

    @Override
    public List<Meeting> findPublishedWithStartTimeBefore(LocalDateTime threshold) {
        return loadWithAgenda(meetingPOMapper.selectByStatusAndStartTimeLessThanEqual(
                Meeting.MeetingStatus.PUBLISHED.getCode(), threshold));
    }

    @Override
    public List<Meeting> findInProgressWithEndTimeBefore(LocalDateTime threshold) {
        return loadWithAgenda(meetingPOMapper.selectByStatusAndEndTimeLessThanEqual(
                Meeting.MeetingStatus.IN_PROGRESS.getCode(), threshold));
    }

    @Override
    public List<Meeting> findAll() {
        return loadWithAgenda(meetingPOMapper.selectList(null));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        agendaItemPOMapper.deleteByMeetingId(id);
        meetingPOMapper.deleteById(id);
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
        if (pos == null || pos.isEmpty()) return Collections.emptyList();
        return pos.stream().map(po -> {
            Meeting m = MeetingMapper.INSTANCE.toEntity(po);
            List<MeetingAgendaItemPO> items = agendaItemPOMapper.selectByMeetingIdOrderBySortOrderAsc(po.getId());
            m.setScheduleDays(agendaTreeConverter.toScheduleDays(items));
            return m;
        }).collect(Collectors.toList());
    }
}
