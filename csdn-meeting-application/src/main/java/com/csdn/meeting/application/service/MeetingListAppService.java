package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议列表应用服务
 * 处理会议列表查询、筛选、搜索等操作
 */
@Slf4j
@Service
public class MeetingListAppService {

    private final MeetingSearchRepository meetingSearchRepository;
    private final TagRepository tagRepository;
    private final UserTagSubscribeRepository userTagSubscribeRepository;

    public MeetingListAppService(MeetingSearchRepository meetingSearchRepository,
                                  TagRepository tagRepository,
                                  UserTagSubscribeRepository userTagSubscribeRepository) {
        this.meetingSearchRepository = meetingSearchRepository;
        this.tagRepository = tagRepository;
        this.userTagSubscribeRepository = userTagSubscribeRepository;
    }

    /**
     * 查询会议列表（支持多维度筛选）
     */
    public PageResult<MeetingListItemDTO> queryMeetingList(MeetingListQuery query, Long userId) {
        // 解析时间范围
        TimeRange timeRange = parseTimeRange(query.getTimeRange());

        // 查询会议列表
        List<Meeting> meetings = meetingSearchRepository.findMeetingList(
                query.getFormat(),
                query.getMeetingType(),
                query.getScene(),
                timeRange.getStart(),
                timeRange.getEnd(),
                query.getKeyword(),
                query.getSafePage(),
                query.getSafeSize()
        );

        // 查询总数
        long total = meetingSearchRepository.countMeetingList(
                query.getFormat(),
                query.getMeetingType(),
                query.getScene(),
                timeRange.getStart(),
                timeRange.getEnd(),
                query.getKeyword()
        );

        // 转换为DTO（先批量查本页会议关联的标签，避免循环内查库）
        List<String> meetingIds = meetings.stream().map(Meeting::getMeetingId).collect(Collectors.toList());
        Map<String, List<Tag>> tagsByMeetingId = meetingIds.isEmpty()
                ? Collections.emptyMap()
                : tagRepository.findTagsByMeetingIds(meetingIds);

        List<MeetingListItemDTO> records = meetings.stream()
                .map(meeting -> convertToListItemDTO(meeting, tagsByMeetingId.getOrDefault(meeting.getMeetingId(), Collections.emptyList()), userId))
                .collect(Collectors.toList());

        return PageResult.of(total, (long) query.getSafePage(), (long) query.getSafeSize(), records);
    }

    /**
     * 关键词搜索会议
     */
    public PageResult<MeetingListItemDTO> searchMeetings(String keyword, MeetingListQuery query, Long userId) {
        // 将关键词设置到查询条件中
        query.setKeyword(keyword);
        return queryMeetingList(query, userId);
    }

    /**
     * 获取会议详情
     */
    public MeetingDetailDTO getMeetingDetail(String meetingId, Long userId) {
        Meeting meeting = meetingSearchRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new RuntimeException("会议不存在"));

        return convertToDetailDTO(meeting, userId);
    }

    /**
     * 将Meeting转换为MeetingListItemDTO（标签列表由外部传入，避免循环内查库）
     */
    private MeetingListItemDTO convertToListItemDTO(Meeting meeting, List<Tag> tags, Long userId) {
        MeetingListItemDTO dto = new MeetingListItemDTO();
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(truncateDescription(meeting.getDescription()));
        dto.setPosterUrl(meeting.getPosterUrl());
        dto.setOrganizerId(meeting.getOrganizerId());
        dto.setOrganizerName(meeting.getOrganizerName());
        dto.setOrganizerAvatar(meeting.getOrganizerAvatar());

        // 枚举值转换
        if (meeting.getFormat() != null) {
            dto.setFormat(meeting.getFormat().getCode());
            dto.setFormatName(meeting.getFormat().getDisplayName());
        }
        if (meeting.getMeetingType() != null) {
            dto.setMeetingType(meeting.getMeetingType().getCode());
            dto.setMeetingTypeName(meeting.getMeetingType().getDisplayName());
        }
        if (meeting.getScene() != null) {
            dto.setScene(meeting.getScene().getCode());
            dto.setSceneName(meeting.getScene().getDisplayName());
        }

        dto.setCityCode(meeting.getCityCode());
        dto.setCityName(meeting.getCityName());
        dto.setVenue(meeting.getVenue());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());

        if (meeting.getStatus() != null) {
            dto.setStatus(meeting.getStatus().getCode());
            dto.setStatusName(meeting.getStatus().getDisplayName());
        }

        dto.setHotScore(meeting.getHotScore());
        dto.setHotScoreDisplay(meeting.getHotScoreDisplay());
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setParticipantsDisplay(meeting.getParticipantsDisplay());

        dto.setTags(tags.stream()
                .map(this::convertToTagDTO)
                .limit(3)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * 将Meeting转换为MeetingDetailDTO
     */
    private MeetingDetailDTO convertToDetailDTO(Meeting meeting, Long userId) {
        MeetingDetailDTO dto = new MeetingDetailDTO();
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setPosterUrl(meeting.getPosterUrl());
        dto.setOrganizerId(meeting.getOrganizerId());
        dto.setOrganizerName(meeting.getOrganizerName());
        dto.setOrganizerAvatar(meeting.getOrganizerAvatar());

        if (meeting.getFormat() != null) {
            dto.setFormat(meeting.getFormat().getCode());
            dto.setFormatName(meeting.getFormat().getDisplayName());
        }
        if (meeting.getMeetingType() != null) {
            dto.setMeetingType(meeting.getMeetingType().getCode());
            dto.setMeetingTypeName(meeting.getMeetingType().getDisplayName());
        }
        if (meeting.getScene() != null) {
            dto.setScene(meeting.getScene().getCode());
            dto.setSceneName(meeting.getScene().getDisplayName());
        }

        dto.setCityCode(meeting.getCityCode());
        dto.setCityName(meeting.getCityName());
        dto.setVenue(meeting.getVenue());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());

        if (meeting.getStatus() != null) {
            dto.setStatus(meeting.getStatus().getCode());
            dto.setStatusName(meeting.getStatus().getDisplayName());
        }

        dto.setHotScore(meeting.getHotScore());
        dto.setHotScoreDisplay(meeting.getHotScoreDisplay());
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setParticipantsDisplay(meeting.getParticipantsDisplay());

        // 查询关联标签；再一次性查询用户对这批标签的订阅状态，避免循环内查库
        List<Tag> tags = tagRepository.findByMeetingId(meeting.getMeetingId());
        Set<Long> subscribedTagIds = Collections.emptySet();
        if (userId != null && !tags.isEmpty()) {
            List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
            subscribedTagIds = userTagSubscribeRepository.findSubscribedTagIdsByUserIdAndTagIds(userId, tagIds);
        }
        final Set<Long> finalSubscribedTagIds = subscribedTagIds;
        dto.setTags(tags.stream()
                .map(tag -> convertToMeetingTagDTO(tag, finalSubscribedTagIds.contains(tag.getId())))
                .collect(Collectors.toList()));

        dto.setCreateTime(meeting.getCreateTime());
        dto.setUpdateTime(meeting.getUpdateTime());

        return dto;
    }

    /**
     * 将Tag转换为TagDTO
     */
    private TagDTO convertToTagDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setTagId(tag.getId());
        dto.setTagName(tag.getTagName());
        dto.setTagCategory(tag.getTagCategory() != null ? tag.getTagCategory().name().toLowerCase() : null);
        dto.setTagCategoryName(tag.getTagCategory() != null ? tag.getTagCategory().getDisplayName() : null);
        return dto;
    }

    /**
     * 将Tag转换为MeetingTagDTO（订阅状态由外部传入，避免循环内查库）
     */
    private MeetingTagDTO convertToMeetingTagDTO(Tag tag, boolean subscribed) {
        MeetingTagDTO dto = new MeetingTagDTO();
        dto.setTagId(tag.getId());
        dto.setTagName(tag.getTagName());
        dto.setTagCategory(tag.getTagCategory() != null ? tag.getTagCategory().name().toLowerCase() : null);
        dto.setTagCategoryName(tag.getTagCategory() != null ? tag.getTagCategory().getDisplayName() : null);
        dto.setSubscribed(subscribed);
        return dto;
    }

    /**
     * 截断描述文本（用于列表展示）
     */
    private String truncateDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() <= 100) {
            return description;
        }
        return description.substring(0, 100) + "...";
    }

    /**
     * 解析时间范围
     */
    private TimeRange parseTimeRange(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = null;
        LocalDateTime end = null;

        if ("this_week".equals(timeRange)) {
            // 本周：从本周一开始到本周日结束
            start = now.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
            end = now.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
        } else if ("this_month".equals(timeRange)) {
            // 本月：从本月1号到本月最后一天
            start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            end = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear()))
                    .withHour(23).withMinute(59).withSecond(59);
        } else if ("next_3_months".equals(timeRange)) {
            // 未来三个月
            start = now;
            end = now.plusMonths(3).withHour(23).withMinute(59).withSecond(59);
        }

        return new TimeRange(start, end);
    }

    /**
     * 时间范围内部类
     */
    private static class TimeRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public TimeRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }
    }
}
