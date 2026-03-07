//package com.csdn.meeting.application.service;
//
//import com.csdn.meeting.application.dto.*;
//import com.csdn.meeting.domain.entity.*;
//import com.csdn.meeting.domain.exception.AgendaIntegrityException;
//import com.csdn.meeting.domain.repository.MeetingRepository;
//import com.csdn.meeting.domain.repository.ParticipantRepository;
//import com.csdn.meeting.domain.service.MeetingDomainService;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.context.ApplicationEventPublisher;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("MeetingApplicationService: createDraft, update, submit")
//class MeetingApplicationServiceDraftUpdateSubmitTest {
//
//    @Mock
//    private MeetingRepository meetingRepository;
//    @Mock
//    private ParticipantRepository participantRepository;
//    @Mock
//    private MeetingDomainService meetingDomainService;
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    private MeetingApplicationService service;
//
//    @BeforeEach
//    void setUp() {
//        service = new MeetingApplicationService(meetingRepository, participantRepository, meetingDomainService, eventPublisher);
//    }
//
//    @Test
//    @DisplayName("createDraft: title null throws IllegalArgumentException")
//    void createDraft_titleNull_throws() {
//        CreateMeetingCommand cmd = new CreateMeetingCommand();
//        cmd.setCreatorId("1");
//        cmd.setCreatorName("Test");
//        cmd.setTitle(null);
//
//        assertThrows(IllegalArgumentException.class, () -> service.createDraft(cmd));
//        verify(meetingRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("createDraft: title blank throws IllegalArgumentException")
//    void createDraft_titleBlank_throws() {
//        CreateMeetingCommand cmd = new CreateMeetingCommand();
//        cmd.setCreatorId("1");
//        cmd.setCreatorName("Test");
//        cmd.setTitle("   ");
//
//        assertThrows(IllegalArgumentException.class, () -> service.createDraft(cmd));
//        verify(meetingRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("createDraft: valid title creates DRAFT")
//    void createDraft_validTitle_createsDraft() {
//        CreateMeetingCommand cmd = new CreateMeetingCommand();
//        cmd.setTitle("会议标题");
//        cmd.setCreatorId("1");
//        cmd.setCreatorName("张三");
//        cmd.setStartTime(LocalDateTime.of(2026, 3, 10, 9, 0));
//        cmd.setEndTime(LocalDateTime.of(2026, 3, 11, 18, 0));
//
//        when(meetingDomainService.generateMeetingId()).thenReturn("M123");
//        when(meetingRepository.save(any(Meeting.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        MeetingDTO result = service.createDraft(cmd);
//
//        assertNotNull(result);
//        assertEquals("M123", result.getMeetingId());
//        assertEquals("会议标题", result.getTitle());
//        assertEquals("DRAFT", result.getStatus());
//        verify(meetingRepository).save(argThat(m -> m.getStatus() == Meeting.MeetingStatus.DRAFT));
//    }
//
//    @Test
//    @DisplayName("update: non-DRAFT/REJECTED throws IllegalStateException")
//    void update_nonDraftOrRejected_throws() {
//        Meeting meeting = createDraftMeeting("M1");
//        meeting.setStatus(Meeting.MeetingStatus.PENDING_REVIEW);
//        when(meetingRepository.findByMeetingId("M1")).thenReturn(Optional.of(meeting));
//
//        UpdateMeetingCommand cmd = new UpdateMeetingCommand();
//        cmd.setTitle("新标题");
//
//        assertThrows(IllegalStateException.class, () -> service.update("M1", cmd));
//        verify(meetingRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("update: DRAFT updates successfully")
//    void update_draft_updates() {
//        Meeting meeting = createDraftMeeting("M1");
//        when(meetingRepository.findByMeetingId("M1")).thenReturn(Optional.of(meeting));
//        when(meetingRepository.save(any(Meeting.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        UpdateMeetingCommand cmd = new UpdateMeetingCommand();
//        cmd.setTitle("更新后标题");
//        cmd.setDescription("更新后描述");
//
//        MeetingDTO result = service.update("M1", cmd);
//
//        assertEquals("更新后标题", result.getTitle());
//        assertEquals("更新后描述", result.getDescription());
//        verify(meetingRepository).save(argThat(m -> "更新后标题".equals(m.getTitle())));
//    }
//
//    @Test
//    @DisplayName("submit: AgendaIntegrityException propagates")
//    void submit_agendaInvalid_throwsAgendaIntegrityException() {
//        Meeting meeting = createDraftMeeting("M1");
//        meeting.setScheduleDays(Collections.emptyList()); // 无日程
//        when(meetingRepository.findByMeetingId("M1")).thenReturn(Optional.of(meeting));
//        doThrow(new AgendaIntegrityException("AGENDA_INVALID: 至少需要1个日程日"))
//                .when(meetingDomainService).validateAgendaIntegrity(any());
//
//        AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> service.submit("M1"));
//        assertTrue(ex.getMessage().contains("至少需要1个日程日"));
//        verify(meetingRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("submit: valid agenda transitions to PENDING_REVIEW")
//    void submit_validAgenda_transitionsToPendingReview() {
//        Meeting meeting = createDraftMeetingWithValidAgenda("M1");
//        when(meetingRepository.findByMeetingId("M1")).thenReturn(Optional.of(meeting));
//        when(meetingRepository.save(any(Meeting.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        MeetingDTO result = service.submit("M1");
//
//        assertEquals("PENDING_REVIEW", result.getStatus());
//        verify(meetingDomainService).validateAgendaIntegrity(meeting);
//        verify(meetingRepository).save(argThat(m -> m.getStatus() == Meeting.MeetingStatus.PENDING_REVIEW));
//    }
//
//    private Meeting createDraftMeeting(String meetingId) {
//        Meeting m = new Meeting();
//        m.setId(1L);
//        m.setMeetingId(meetingId);
//        m.setTitle("标题");
//        m.setCreatorId("1");
//        m.setCreatorName("创建者");
//        m.setStartTime(LocalDateTime.of(2026, 3, 10, 9, 0));
//        m.setEndTime(LocalDateTime.of(2026, 3, 11, 18, 0));
//        m.setStatus(Meeting.MeetingStatus.DRAFT);
//        return m;
//    }
//
//    private Meeting createDraftMeetingWithValidAgenda(String meetingId) {
//        Meeting m = createDraftMeeting(meetingId);
//        Topic topic = new Topic("议题1", null, null, Collections.emptyList());
//        SubVenue sv = new SubVenue("主会场", Arrays.asList(topic));
//        Session session = new Session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Arrays.asList(sv));
//        ScheduleDay day = new ScheduleDay(LocalDate.of(2026, 3, 10), "Day1", Arrays.asList(session));
//        m.setScheduleDays(Arrays.asList(day));
//        return m;
//    }
//}
