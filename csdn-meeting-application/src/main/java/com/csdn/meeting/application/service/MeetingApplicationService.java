package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.util.UrlNormalizer;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.domain.entity.ScheduleDay;
import com.csdn.meeting.domain.entity.Session;
import com.csdn.meeting.domain.entity.SubVenue;
import com.csdn.meeting.domain.entity.Topic;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingType;
import com.csdn.meeting.domain.event.MeetingPublishedEvent;
import com.csdn.meeting.domain.event.MeetingStatusChangedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.ParticipantRepository;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.service.MeetingDomainService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingApplicationService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final MeetingDomainService meetingDomainService;
    private final TagRepository tagRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MeetingRegistrationUseCase meetingRegistrationUseCase;

    public MeetingApplicationService(MeetingRepository meetingRepository,
                                     ParticipantRepository participantRepository,
                                     MeetingDomainService meetingDomainService,
                                     TagRepository tagRepository,
                                     ApplicationEventPublisher eventPublisher,
                                     MeetingRegistrationUseCase meetingRegistrationUseCase) {
        this.meetingRepository = meetingRepository;
        this.participantRepository = participantRepository;
        this.meetingDomainService = meetingDomainService;
        this.tagRepository = tagRepository;
        this.eventPublisher = eventPublisher;
        this.meetingRegistrationUseCase = meetingRegistrationUseCase;
    }

    /**
     * 创建草稿：仅校验 title 非空，日程可为空
     */
    @Transactional
    public MeetingDTO createDraft(CreateMeetingCommand command) {
        if (command.getTitle() == null || command.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("会议标题不能为空");
        }
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingDomainService.generateMeetingId());
        applyCreateCommandToMeeting(meeting, command);
        meeting.setStatus(Meeting.MeetingStatus.DRAFT);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 更新会议：仅 DRAFT/REJECTED 可编辑
     */
    @Transactional
    public MeetingDTO update(String meetingId, UpdateMeetingCommand command) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        if (meeting.getStatus() != Meeting.MeetingStatus.DRAFT
                && meeting.getStatus() != Meeting.MeetingStatus.REJECTED
                && meeting.getStatus() != Meeting.MeetingStatus.OFFLINE) {
            throw new IllegalStateException("只有草稿、已拒绝或已下架状态才能编辑");
        }
        applyUpdateCommandToMeeting(meeting, command);
        Meeting savedMeeting = meetingRepository.save(meeting);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 提交审核：先校验四级日程完整性，再 meeting.submit()
     * AgendaIntegrityException 向上传播，由 GlobalExceptionHandler 转为 400 响应
     */
    @Transactional
    public MeetingDTO submit(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        meetingDomainService.validateAgendaIntegrity(meeting);
        meeting.submit();
        Meeting savedMeeting = meetingRepository.save(meeting);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 撤回审核：PENDING_REVIEW -> DRAFT
     */
    @Transactional
    public MeetingDTO withdraw(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        Meeting.MeetingStatus from = meeting.getStatus();
        meeting.withdraw();
        Meeting savedMeeting = meetingRepository.save(meeting);
        publishStatusChanged(meetingId, from, savedMeeting.getStatus(), null);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 审核通过：PENDING_REVIEW -> PUBLISHED
     * 触发MeetingPublishedEvent通知订阅用户
     */
    @Transactional
    public MeetingDTO approve(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        Meeting.MeetingStatus from = meeting.getStatus();
        meeting.approve();
        Meeting savedMeeting = meetingRepository.save(meeting);
        publishStatusChanged(meetingId, from, savedMeeting.getStatus(), null);

        // 触发会议发布事件，通知订阅用户（PRD 2.3 订阅推送）
        publishMeetingPublishedEvent(savedMeeting);

        return toMeetingDTO(savedMeeting);
    }

    /**
     * 审核拒绝：PENDING_REVIEW -> REJECTED
     *
     * @param reason 拒绝原因，不能为空
     */
    @Transactional
    public MeetingDTO reject(String meetingId, String reason) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        Meeting.MeetingStatus from = meeting.getStatus();
        meeting.reject(reason);
        Meeting savedMeeting = meetingRepository.save(meeting);
        publishStatusChanged(meetingId, from, savedMeeting.getStatus(), null);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 主动下架：PUBLISHED / IN_PROGRESS -> OFFLINE
     *
     * @param reason 下架原因，不能为空
     */
    @Transactional
    public MeetingDTO takedown(String meetingId, String reason) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        Meeting.MeetingStatus from = meeting.getStatus();
        meeting.takedown(reason);
        Meeting savedMeeting = meetingRepository.save(meeting);
        publishStatusChanged(meetingId, from, savedMeeting.getStatus(), null);
        return toMeetingDTO(savedMeeting);
    }

    /**
     * 逻辑删除：DRAFT / ENDED / OFFLINE / REJECTED -> DELETED
     */
    @Transactional
    public void deleteMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        meeting.delete();
        meetingRepository.save(meeting);
    }

    private void publishStatusChanged(String meetingId, Meeting.MeetingStatus from,
                                      Meeting.MeetingStatus to, String actor) {
        eventPublisher.publishEvent(
                new MeetingStatusChangedEvent(meetingId, from, to, LocalDateTime.now(), actor));
    }

    /**
     * 触发会议发布事件（PRD 2.3 订阅推送）
     * 当会议审核通过时，通知订阅了该会议标签的用户
     */
    private void publishMeetingPublishedEvent(Meeting meeting) {
        try {
            // 查询会议关联的标签
            List<com.csdn.meeting.domain.entity.Tag> tags = tagRepository.findByMeetingId(meeting.getMeetingId());
            List<Long> tagIds = tags.stream()
                    .map(com.csdn.meeting.domain.entity.Tag::getId)
                    .collect(Collectors.toList());

            // 触发会议发布事件
            MeetingPublishedEvent event = new MeetingPublishedEvent(
                    meeting.getMeetingId(),
                    meeting.getTitle(),
                    tagIds,
                    LocalDateTime.now(),
                    meeting.getCreatorId()
            );
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            // 推送通知失败不应影响主流程，仅记录日志
            System.err.println("触发会议发布事件失败: " + e.getMessage());
        }
    }

    @Transactional
    public MeetingDTO createMeeting(CreateMeetingCommand command) {
        return createDraft(command);
    }

    @Transactional
    public MeetingDTO joinMeeting(JoinMeetingCommand command) {
        // 委托给统一的报名服务，确保与 POST /api/registrations 共用同一套业务逻辑
        RegistrationCommand regCmd = new RegistrationCommand();
        regCmd.setMeetingId(command.getMeetingId());
        regCmd.setUserId(command.getUserId());

        Map<String, String> formData = new HashMap<>();
        if (command.getUserName() != null) formData.put("name", command.getUserName());
        if (command.getPhone() != null) formData.put("phone", command.getPhone());
        regCmd.setFormData(formData);

        meetingRegistrationUseCase.register(regCmd);
        return getMeetingDetail(command.getMeetingId());
    }

    @Transactional
    public void leaveMeeting(String meetingId, Long userId) {
        Participant participant = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new IllegalArgumentException("参与者不存在"));

        participant.leave();
        participantRepository.save(participant);
    }

    @Transactional
    public void startMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.start();
        meetingRepository.save(meeting);
    }

    @Transactional
    public void endMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.end();
        meetingRepository.save(meeting);
    }

    @Transactional
    public void cancelMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.cancel();
        meetingRepository.save(meeting);
    }

    public MeetingDTO getMeetingDetail(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        return toMeetingDTOWithParticipants(meeting, meetingId);
    }

    public MeetingDTO getMeetingDetailById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        String meetingId = meeting.getMeetingId();
        MeetingDTO dto = toMeetingDTOWithParticipants(meeting, meetingId);
        // 将 tags 字段中的 tagId 解析为 tagName，便于前端编辑表单显示
        dto.setTags(resolveTagIdsToNames(dto.getTags()));
        return dto;
    }

    /**
     * 将逗号分隔的 tagId 字符串转为逗号分隔的 tagName 字符串。
     * 若值均为非数字（已是名称），直接原样返回。
     */
    private String resolveTagIdsToNames(String tagsStr) {
        if (tagsStr == null || tagsStr.trim().isEmpty()) return tagsStr;
        String[] parts = tagsStr.split(",");
        List<Long> ids = new java.util.ArrayList<>();
        for (String part : parts) {
            try {
                ids.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException e) {
                // 已经是名称，无需解析，直接返回原值
                return tagsStr;
            }
        }
        if (ids.isEmpty()) return tagsStr;
        List<com.csdn.meeting.domain.entity.Tag> tags = tagRepository.findByIds(ids);
        return tags.stream()
                .map(com.csdn.meeting.domain.entity.Tag::getTagName)
                .collect(java.util.stream.Collectors.joining(","));
    }

    private MeetingDTO toMeetingDTOWithParticipants(Meeting meeting, String meetingId) {
        MeetingDTO dto = toMeetingDTO(meeting);
        List<Participant> participants = participantRepository.findByMeetingId(meetingId);
        dto.setParticipants(participants.stream()
                .map(this::toParticipantDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public List<MeetingDTO> getMeetingsByCreator(String creatorId) {
        return meetingRepository.findByCreatorId(creatorId).stream()
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
    }

    private void applyCreateCommandToMeeting(Meeting meeting, CreateMeetingCommand cmd) {
        meeting.setTitle(cmd.getTitle());
        meeting.setDescription(cmd.getDescription());
        meeting.setCreatorId(cmd.getCreatorId());
        meeting.setCreatorName(cmd.getCreatorName());
        meeting.setStartTime(cmd.getStartTime());
        meeting.setEndTime(cmd.getEndTime());
        meeting.setMaxParticipants(cmd.getMaxParticipants());
        meeting.setOrganizer(cmd.getOrganizer());
        meeting.setFormat(parseFormat(cmd.getFormat()));
        meeting.setMeetingType(parseMeetingType(cmd.getMeetingType()));
        meeting.setScene(cmd.getScene());
        meeting.setVenue(cmd.getVenue());
        meeting.setRegions(cmd.getRegions());
        meeting.setCoverImage(UrlNormalizer.normalizeImageUrl(cmd.getCoverImage()));
        meeting.setTags(cmd.getTags());
        meeting.setTargetAudience(cmd.getTargetAudience());
        meeting.setIsPremium(cmd.getIsPremium());
        meeting.setSceneIndustry(cmd.getSceneIndustry());
        meeting.setSceneProduct(cmd.getSceneProduct());
        meeting.setSceneMarketingRegions(cmd.getSceneMarketingRegions());
        meeting.setSceneUniversities(cmd.getSceneUniversities());
        meeting.setScheduleDays(toScheduleDays(cmd.getScheduleDays()));
    }

    private void applyUpdateCommandToMeeting(Meeting meeting, UpdateMeetingCommand cmd) {
        if (cmd.getTitle() != null) meeting.setTitle(cmd.getTitle());
        if (cmd.getDescription() != null) meeting.setDescription(cmd.getDescription());
        if (cmd.getStartTime() != null) meeting.setStartTime(cmd.getStartTime());
        if (cmd.getEndTime() != null) meeting.setEndTime(cmd.getEndTime());
        if (cmd.getMaxParticipants() != null) meeting.setMaxParticipants(cmd.getMaxParticipants());
        if (cmd.getOrganizer() != null) meeting.setOrganizer(cmd.getOrganizer());
        if (cmd.getFormat() != null) meeting.setFormat(parseFormat(cmd.getFormat()));
        if (cmd.getMeetingType() != null) meeting.setMeetingType(parseMeetingType(cmd.getMeetingType()));
        if (cmd.getScene() != null) meeting.setScene(cmd.getScene());
        if (cmd.getVenue() != null) meeting.setVenue(cmd.getVenue());
        if (cmd.getRegions() != null) meeting.setRegions(cmd.getRegions());
        if (cmd.getCoverImage() != null) meeting.setCoverImage(UrlNormalizer.normalizeImageUrl(cmd.getCoverImage()));
        if (cmd.getTags() != null) meeting.setTags(cmd.getTags());
        if (cmd.getTargetAudience() != null) meeting.setTargetAudience(cmd.getTargetAudience());
        if (cmd.getIsPremium() != null) meeting.setIsPremium(cmd.getIsPremium());
        if (cmd.getSceneIndustry() != null) meeting.setSceneIndustry(cmd.getSceneIndustry());
        if (cmd.getSceneProduct() != null) meeting.setSceneProduct(cmd.getSceneProduct());
        if (cmd.getSceneMarketingRegions() != null) meeting.setSceneMarketingRegions(cmd.getSceneMarketingRegions());
        if (cmd.getSceneUniversities() != null) meeting.setSceneUniversities(cmd.getSceneUniversities());
        if (cmd.getScheduleDays() != null) meeting.setScheduleDays(toScheduleDays(cmd.getScheduleDays()));
    }

    private MeetingFormat parseFormat(String format) {
        if (format == null || format.trim().isEmpty()) return null;
        try {
            return MeetingFormat.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private MeetingType parseMeetingType(String meetingType) {
        if (meetingType == null || meetingType.trim().isEmpty()) return null;
        return MeetingType.of(meetingType);
    }

    private List<ScheduleDay> toScheduleDays(List<ScheduleDayDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return new ArrayList<>();
        return dtos.stream().map(this::toScheduleDay).collect(Collectors.toList());
    }

    private ScheduleDay toScheduleDay(ScheduleDayDTO dto) {
        List<Session> sessions = dto.getSessions() == null ? Collections.emptyList()
                : dto.getSessions().stream().map(this::toSession).collect(Collectors.toList());
        return new ScheduleDay(dto.getScheduleDate(), dto.getDayLabel(), sessions);
    }

    private Session toSession(SessionDTO dto) {
        List<SubVenue> subVenues = dto.getSubVenues() == null ? Collections.emptyList()
                : dto.getSubVenues().stream().map(this::toSubVenue).collect(Collectors.toList());
        return new Session(
                dto.getSessionName(),
                dto.getStartTime() != null ? dto.getStartTime() : java.time.LocalTime.MIDNIGHT,
                dto.getEndTime() != null ? dto.getEndTime() : java.time.LocalTime.of(23, 59),
                subVenues
        );
    }

    private SubVenue toSubVenue(SubVenueDTO dto) {
        List<Topic> topics = dto.getTopics() == null ? Collections.emptyList()
                : dto.getTopics().stream().map(this::toTopic).collect(Collectors.toList());
        return new SubVenue(dto.getSubVenueName(), topics);
    }

    private Topic toTopic(TopicDTO dto) {
        return new Topic(dto.getTitle(), dto.getTopicIntro(), dto.getInvolvedProducts(), dto.getGuests());
    }

    private List<ScheduleDayDTO> toScheduleDayDTOs(List<ScheduleDay> days) {
        if (days == null || days.isEmpty()) return Collections.emptyList();
        return days.stream().map(this::toScheduleDayDTO).collect(Collectors.toList());
    }

    private ScheduleDayDTO toScheduleDayDTO(ScheduleDay day) {
        ScheduleDayDTO dto = new ScheduleDayDTO();
        dto.setScheduleDate(day.getScheduleDate());
        dto.setDayLabel(day.getDayLabel());
        dto.setSessions(day.getSessions().stream().map(this::toSessionDTO).collect(Collectors.toList()));
        return dto;
    }

    private SessionDTO toSessionDTO(Session s) {
        SessionDTO dto = new SessionDTO();
        dto.setSessionName(s.getSessionName());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setSubVenues(s.getSubVenues().stream().map(this::toSubVenueDTO).collect(Collectors.toList()));
        return dto;
    }

    private SubVenueDTO toSubVenueDTO(SubVenue v) {
        SubVenueDTO dto = new SubVenueDTO();
        dto.setSubVenueName(v.getSubVenueName());
        dto.setTopics(v.getTopics().stream().map(this::toTopicDTO).collect(Collectors.toList()));
        return dto;
    }

    private TopicDTO toTopicDTO(Topic t) {
        TopicDTO dto = new TopicDTO();
        dto.setTitle(t.getTitle());
        dto.setTopicIntro(t.getTopicIntro());
        dto.setInvolvedProducts(t.getInvolvedProducts());
        dto.setGuests(t.getGuests());
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
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setOrganizer(meeting.getOrganizer());
        dto.setFormat(meeting.getFormat() != null ? meeting.getFormat().name() : null);
        dto.setMeetingType(meeting.getMeetingType() != null ? meeting.getMeetingType().name() : null);
        dto.setScene(meeting.getScene());
        dto.setVenue(meeting.getVenue());
        dto.setRegions(meeting.getRegions());
        dto.setCoverImage(meeting.getCoverImage() != null ? meeting.getCoverImage() : meeting.getPosterUrl());
        dto.setPosterUrl(meeting.getPosterUrl() != null ? meeting.getPosterUrl() : meeting.getCoverImage());
        dto.setTags(meeting.getTags());
        dto.setTargetAudience(meeting.getTargetAudience());
        dto.setIsPremium(meeting.getIsPremium());
        dto.setTakedownReason(meeting.getTakedownReason());
        dto.setRejectReason(meeting.getRejectReason());
        dto.setSceneIndustry(meeting.getSceneIndustry());
        dto.setSceneProduct(meeting.getSceneProduct());
        dto.setSceneMarketingRegions(meeting.getSceneMarketingRegions());
        dto.setSceneUniversities(meeting.getSceneUniversities());
        dto.setScheduleDays(toScheduleDayDTOs(meeting.getScheduleDays()));
        return dto;
    }

    private ParticipantDTO toParticipantDTO(Participant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(participant.getId());
        dto.setUserId(participant.getUserId());
        dto.setUserName(participant.getUserName());
        dto.setMeetingId(participant.getMeetingId());
        dto.setRole(participant.getRole().name());
        dto.setStatus(participant.getStatus().name());
        return dto;
    }
}
