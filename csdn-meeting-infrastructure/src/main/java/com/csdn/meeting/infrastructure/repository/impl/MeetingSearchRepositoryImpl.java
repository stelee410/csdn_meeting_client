package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.infrastructure.converter.MeetingConverter;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.po.TagNewMeetingCountPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingSearchMapper;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingTagMapper;
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
 */
@Repository
public class MeetingSearchRepositoryImpl implements MeetingSearchRepository {

    private final MeetingSearchMapper meetingSearchMapper;
    private final MeetingTagMapper meetingTagMapper;

    public MeetingSearchRepositoryImpl(MeetingSearchMapper meetingSearchMapper,
                                       MeetingTagMapper meetingTagMapper) {
        this.meetingSearchMapper = meetingSearchMapper;
        this.meetingTagMapper = meetingTagMapper;
    }

    @Override
    public Optional<Meeting> findByMeetingId(String meetingId) {
        MeetingPO meetingPO = meetingSearchMapper.selectByMeetingId(meetingId);
        return Optional.ofNullable(meetingPO).map(MeetingConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Meeting> findMeetingList(Integer format, Integer meetingType, Integer scene,
                                          LocalDateTime startTimeFrom, LocalDateTime startTimeTo,
                                          String keyword, int page, int size) {
        Page<MeetingPO> pageParam = new Page<>(page, size);

        // 转换时间格式
        String startTimeFromStr = formatDateTime(startTimeFrom);
        String startTimeToStr = formatDateTime(startTimeTo);

        Page<MeetingPO> resultPage = meetingSearchMapper.selectMeetingList(
                pageParam, format, meetingType, scene, startTimeFromStr, startTimeToStr, keyword);

        return resultPage.getRecords().stream()
                .map(MeetingConverter.INSTANCE::poToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countMeetingList(Integer format, Integer meetingType, Integer scene,
                                 LocalDateTime startTimeFrom, LocalDateTime startTimeTo,
                                 String keyword) {
        // 使用分页查询获取总数
        Page<MeetingPO> pageParam = new Page<>(1, 1);
        String startTimeFromStr = formatDateTime(startTimeFrom);
        String startTimeToStr = formatDateTime(startTimeTo);

        Page<MeetingPO> resultPage = meetingSearchMapper.selectMeetingList(
                pageParam, format, meetingType, scene, startTimeFromStr, startTimeToStr, keyword);

        return resultPage.getTotal();
    }

    @Override
    public List<Meeting> searchByKeyword(String keyword, int page, int size) {
        // 搜索时只传keyword，其他筛选条件为null
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
        List<TagNewMeetingCountPO> list = meetingTagMapper.countNewMeetingsByTagIdsSince(tagIds, since);
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
