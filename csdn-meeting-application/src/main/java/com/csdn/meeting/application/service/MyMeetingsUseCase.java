package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.MeetingFavorite;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.entity.ScheduleDay;
import com.csdn.meeting.domain.entity.Session;
import com.csdn.meeting.domain.entity.SubVenue;
import com.csdn.meeting.domain.entity.Topic;
import com.csdn.meeting.domain.repository.MeetingFavoriteRepository;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我的会议 UseCase（agent.prd §2.5）
 * 我报名的、我收藏的、我创建的
 */
@Service
public class MyMeetingsUseCase {

    private final MeetingRepository meetingRepository;
    private final MeetingFavoriteRepository favoriteRepository;
    private final RegistrationRepository registrationRepository;

    public MyMeetingsUseCase(MeetingRepository meetingRepository,
                             MeetingFavoriteRepository favoriteRepository,
                             RegistrationRepository registrationRepository) {
        this.meetingRepository = meetingRepository;
        this.favoriteRepository = favoriteRepository;
        this.registrationRepository = registrationRepository;
    }

    /**
     * 我报名的会议
     * 默认 status IN [PUBLISHED, IN_PROGRESS]；includeEnded=true 时增加 ENDED
     * 按会议日期倒序
     */
    public PageResult<MeetingDTO> getMyRegistered(Long userId, boolean includeEnded, int page, int size) {
        List<Meeting.MeetingStatus> statuses = new ArrayList<>();
        statuses.add(Meeting.MeetingStatus.PUBLISHED);
        statuses.add(Meeting.MeetingStatus.IN_PROGRESS);
        if (includeEnded) {
            statuses.add(Meeting.MeetingStatus.ENDED);
        }
        PageResult<Registration> regPage = registrationRepository.findByUserIdAndMeetingStatusIn(userId, statuses, page, size);
        List<MeetingDTO> dtos = regPage.getContent().stream()
                .map(reg -> meetingRepository.findById(reg.getMeetingId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
        return new PageResult<>(dtos, regPage.getTotalElements(), page, size);
    }

    /**
     * 我收藏的会议
     * 按收藏时间倒序；过滤掉已删除、草稿、待审、已拒绝的会议
     */
    public PageResult<MeetingDTO> getMyFavorites(Long userId, int page, int size) {
        PageResult<MeetingFavorite> favPage = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, page, size);
        List<MeetingDTO> dtos = favPage.getContent().stream()
                .map(fav -> meetingRepository.findById(fav.getMeetingId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .filter(m -> m.getStatus() != Meeting.MeetingStatus.DELETED
                        && m.getStatus() != Meeting.MeetingStatus.DRAFT
                        && m.getStatus() != Meeting.MeetingStatus.PENDING_REVIEW
                        && m.getStatus() != Meeting.MeetingStatus.REJECTED)
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
        return new PageResult<>(dtos, favPage.getTotalElements(), page, size);
    }

    /**
     * 我创建的会议
     * 支持按 status、startDate、endDate 筛选
     */
    public PageResult<MeetingDTO> getMyCreated(String userId, List<Meeting.MeetingStatus> statuses,
                                               LocalDate startDate, LocalDate endDate,
                                               int page, int size) {
        LocalDateTime startFrom = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endTo = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        PageResult<Meeting> meetingPage = meetingRepository.findPageByCreatorId(userId, statuses, startFrom, endTo, page, size);
        List<MeetingDTO> dtos = meetingPage.getContent().stream().map(this::toMeetingDTO).collect(Collectors.toList());
        return new PageResult<>(dtos, meetingPage.getTotalElements(), page, size);
    }

    /**
     * 获取会议报名列表（支持按 status 筛选）
     */
    public PageResult<RegistrationDTO> getMeetingRegistrations(Long meetingId,
                                                               Registration.RegistrationStatus status,
                                                               int page, int size) {
        PageResult<Registration> regPage = registrationRepository.findByMeetingIdAndStatus(meetingId, status, page, size);
        List<RegistrationDTO> dtos = regPage.getContent().stream().map(this::toRegistrationDTO).collect(Collectors.toList());
        return new PageResult<>(dtos, regPage.getTotalElements(), page, size);
    }

    private RegistrationDTO toRegistrationDTO(Registration reg) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(reg.getId());
        dto.setMeetingId(reg.getMeetingId());
        dto.setUserId(reg.getUserId());
        dto.setName(reg.getName());
        dto.setPhone(reg.getPhone());
        dto.setEmail(reg.getEmail());
        dto.setCompany(reg.getCompany());
        dto.setPosition(reg.getPosition());
        dto.setStatus(reg.getStatus() != null ? reg.getStatus().name() : null);
        dto.setRegisteredAt(reg.getRegisteredAt());
        dto.setAuditedAt(reg.getAuditedAt());
        dto.setAuditRemark(reg.getAuditRemark());
        return dto;
    }

    private MeetingDTO toMeetingDTO(Meeting meeting) {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setCreatorId(meeting.getCreatorId());
        dto.setCreatorName(meeting.getCreatorName());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setStatus(meeting.getStatus().name());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setOrganizer(meeting.getOrganizer());
        dto.setFormat(meeting.getFormat() != null ? meeting.getFormat().name() : null);
        dto.setScene(meeting.getScene());
        dto.setVenue(meeting.getVenue());
        dto.setRegions(meeting.getRegions());
        dto.setCoverImage(meeting.getCoverImage());
        dto.setTags(meeting.getTags());
        dto.setTargetAudience(meeting.getTargetAudience());
        dto.setIsPremium(meeting.getIsPremium());
        dto.setTakedownReason(meeting.getTakedownReason());
        dto.setRejectReason(meeting.getRejectReason());
        dto.setScheduleDays(toScheduleDayDTOs(meeting.getScheduleDays()));
        return dto;
    }

    private List<com.csdn.meeting.application.dto.ScheduleDayDTO> toScheduleDayDTOs(List<ScheduleDay> days) {
        if (days == null || days.isEmpty()) return Collections.emptyList();
        return days.stream().map(this::toScheduleDayDTO).collect(Collectors.toList());
    }

    private com.csdn.meeting.application.dto.ScheduleDayDTO toScheduleDayDTO(ScheduleDay day) {
        com.csdn.meeting.application.dto.ScheduleDayDTO dto = new com.csdn.meeting.application.dto.ScheduleDayDTO();
        dto.setScheduleDate(day.getScheduleDate());
        dto.setDayLabel(day.getDayLabel());
        dto.setSessions(day.getSessions().stream().map(this::toSessionDTO).collect(Collectors.toList()));
        return dto;
    }

    private com.csdn.meeting.application.dto.SessionDTO toSessionDTO(Session s) {
        com.csdn.meeting.application.dto.SessionDTO dto = new com.csdn.meeting.application.dto.SessionDTO();
        dto.setSessionName(s.getSessionName());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setSubVenues(s.getSubVenues().stream().map(this::toSubVenueDTO).collect(Collectors.toList()));
        return dto;
    }

    private com.csdn.meeting.application.dto.SubVenueDTO toSubVenueDTO(SubVenue v) {
        com.csdn.meeting.application.dto.SubVenueDTO dto = new com.csdn.meeting.application.dto.SubVenueDTO();
        dto.setSubVenueName(v.getSubVenueName());
        dto.setTopics(v.getTopics().stream().map(this::toTopicDTO).collect(Collectors.toList()));
        return dto;
    }

    private com.csdn.meeting.application.dto.TopicDTO toTopicDTO(Topic t) {
        com.csdn.meeting.application.dto.TopicDTO dto = new com.csdn.meeting.application.dto.TopicDTO();
        dto.setTitle(t.getTitle());
        dto.setTopicIntro(t.getTopicIntro());
        dto.setInvolvedProducts(t.getInvolvedProducts());
        dto.setGuests(t.getGuests());
        return dto;
    }
}
