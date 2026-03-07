package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.infrastructure.converter.MeetingConverter;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.po.TagNewMeetingCountPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingSearchMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会议搜索仓储实现
 * 使用 MyBatis-Plus IPage 分页插件，一次查询返回列表与总数，避免二次 count 查询
 */
@Repository
public class MeetingSearchRepositoryImpl implements MeetingSearchRepository {

    private final MeetingSearchMapper meetingSearchMapper;

    public MeetingSearchRepositoryImpl(MeetingSearchMapper meetingSearchMapper) {
        this.meetingSearchMapper = meetingSearchMapper;
    }

    @Override
    public Optional<Meeting> findByMeetingId(String meetingId) {
        MeetingPO meetingPO = meetingSearchMapper.selectByMeetingId(meetingId);
        return Optional.ofNullable(meetingPO).map(MeetingConverter.INSTANCE::poToEntity);
    }

    @Override
    public PageResult<Meeting> findMeetingList(Integer format, Integer meetingType, Integer scene,
                                                LocalDateTime startTimeFrom, LocalDateTime startTimeTo,
                                                String keyword, int page, int size) {
        // MyBatis-Plus Page 当前页从 1 开始，应用层 page 从 0 开始
        Page<MeetingPO> pageParam = new Page<>(page + 1L, size);
        String startTimeFromStr = formatDateTime(startTimeFrom);
        String startTimeToStr = formatDateTime(startTimeTo);

        IPage<MeetingPO> resultPage = meetingSearchMapper.selectMeetingList(
                pageParam, format, meetingType, scene, startTimeFromStr, startTimeToStr, keyword);

        List<Meeting> content = resultPage.getRecords().stream()
                .map(MeetingConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public PageResult<Meeting> searchByKeyword(String keyword, int page, int size) {
        return findMeetingList(null, null, null, null, null, keyword, page, size);
    }

    @Override
    public void incrementHotScore(String meetingId, int score) {
        meetingSearchMapper.incrementHotScore(meetingId, score);
    }

    @Override
    public void updateCurrentParticipants(String meetingId, int delta) {
        meetingSearchMapper.updateCurrentParticipants(meetingId, delta);
    }

    @Override
    public Map<Long, Integer> countNewMeetingsByTagIdsSince(List<Long> tagIds, LocalDateTime since) {
        if (tagIds == null || tagIds.isEmpty() || since == null) {
            return Collections.emptyMap();
        }
        List<TagNewMeetingCountPO> list = meetingSearchMapper.countNewMeetingsByTagIdsSince(tagIds, since);
        return list.stream().collect(Collectors.toMap(
                TagNewMeetingCountPO::getTagId,
                TagNewMeetingCountPO::getNewMeetingCount,
                (a, b) -> a
        ));
    }

    /**
     * 格式化日期时间为字符串
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
