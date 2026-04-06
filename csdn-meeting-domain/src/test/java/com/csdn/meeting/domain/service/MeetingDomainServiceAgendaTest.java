package com.csdn.meeting.domain.service;

import com.csdn.meeting.domain.entity.*;
import com.csdn.meeting.domain.exception.AgendaIntegrityException;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingDomainService.validateAgendaIntegrity")
class MeetingDomainServiceAgendaTest {

    private MeetingDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new MeetingDomainService(new MeetingRepository() {
            @Override
            public Meeting save(Meeting meeting) { return meeting; }
            @Override
            public java.util.Optional<Meeting> findById(Long id) { return java.util.Optional.empty(); }
            @Override
            public java.util.Optional<Meeting> findByMeetingId(String meetingId) { return java.util.Optional.empty(); }
            @Override
            public java.util.List<Meeting> findByCreatorId(String creatorId) { return Collections.emptyList(); }
            @Override
            public java.util.List<Meeting> findByCreatorIdAndStatus(String creatorId, Meeting.MeetingStatus status) { return Collections.emptyList(); }
            @Override
            public java.util.List<Meeting> findByCreatorIdAndStartTimeBetween(String creatorId, LocalDateTime start, LocalDateTime end) { return Collections.emptyList(); }
            @Override
            public PageResult<Meeting> findPageByCreatorId(String creatorId, List<Meeting.MeetingStatus> statuses, LocalDateTime startFrom, LocalDateTime endTo, int page, int size) { return new PageResult<>(Collections.emptyList(), 0, page, size); }
            @Override
            public java.util.List<Meeting> findByStatus(Meeting.MeetingStatus status) { return Collections.emptyList(); }
            @Override
            public java.util.List<Meeting> findPublishedWithStartTimeBefore(LocalDateTime threshold) { return Collections.emptyList(); }
            @Override
            public java.util.List<Meeting> findInProgressWithEndTimeBefore(LocalDateTime threshold) { return Collections.emptyList(); }
            @Override
            public java.util.List<Meeting> findAll() { return Collections.emptyList(); }
            @Override
            public void deleteById(Long id) {}
            @Override
            public void delete(Meeting meeting) {}
        });
    }

    private Meeting createMeetingWithAgenda(List<ScheduleDay> scheduleDays) {
        Meeting m = new Meeting();
        m.setStartTime(LocalDateTime.of(2026, 3, 1, 9, 0));
        m.setEndTime(LocalDateTime.of(2026, 3, 3, 18, 0));
        m.setScheduleDays(scheduleDays);
        return m;
    }

    private ScheduleDay day(LocalDate date, List<Session> sessions) {
        return new ScheduleDay(date, "Day1", sessions);
    }

    private Session session(String name, LocalTime start, LocalTime end, List<SubVenue> subVenues) {
        return new Session(name, start, end, subVenues);
    }

    private SubVenue subVenue(String name, List<Topic> topics) {
        return new SubVenue(name, topics);
    }

    private Topic topic(String title) {
        return new Topic(title, null, null, null);
    }

    @Nested
    @DisplayName("缺失 ScheduleDay")
    class MissingScheduleDay {
        @Test
        void emptyScheduleDays_throws() {
            Meeting m = createMeetingWithAgenda(Collections.emptyList());
            assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        void nullScheduleDays_throws() {
            Meeting m = new Meeting();
            m.setStartTime(LocalDateTime.now());
            m.setEndTime(LocalDateTime.now().plusDays(1));
            assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
        }
    }

    @Nested
    @DisplayName("缺失 Session")
    class MissingSession {
        @Test
        void emptySessions_throws() {
            ScheduleDay day = new ScheduleDay(LocalDate.of(2026, 3, 1), "Day1", Collections.emptyList());
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("至少需要1个环节"));
        }
    }

    @Nested
    @DisplayName("SubVenue 可选（issue001）")
    class SubVenueOptional {
        @Test
        void emptySubVenues_passes() {
            Session session = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Collections.singletonList(session));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }
    }

    @Nested
    @DisplayName("缺失 Topic 或标题为空")
    class MissingTopicOrEmptyTitle {
        @Test
        void emptyTopics_throws() {
            SubVenue subVenue = subVenue("主会场", Collections.emptyList());
            Session session = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.singletonList(subVenue));
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Collections.singletonList(session));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("至少需要1个议题"));
        }

        @Test
        void topicTitleBlank_throws() {
            Topic topic = topic("  ");
            SubVenue subVenue = subVenue("主会场", Collections.singletonList(topic));
            Session session = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.singletonList(subVenue));
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Collections.singletonList(session));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("议题标题不能为空"));
        }
    }

    @Nested
    @DisplayName("ScheduleDay 日期超出范围")
    class ScheduleDateOutOfRange {
        @Test
        void scheduleDateBeforeStart_throws() {
            SubVenue sub = subVenue("主会场", Collections.singletonList(topic("议题1")));
            Session ses = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.singletonList(sub));
            ScheduleDay day = day(LocalDate.of(2026, 2, 28), Collections.singletonList(ses)); // 早于 start
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("必须在会议时间范围内"));
        }
    }

    @Nested
    @DisplayName("Session 时间重叠")
    class SessionOverlap {
        @Test
        void overlappingSessions_throws() {
            SubVenue sub = subVenue("主会场", Collections.singletonList(topic("议题1")));
            Session s1 = session("上午", LocalTime.of(9, 0), LocalTime.of(11, 0), Collections.singletonList(sub));
            Session s2 = session("上午2", LocalTime.of(10, 30), LocalTime.of(12, 0), Collections.singletonList(sub)); // 重叠
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Arrays.asList(s1, s2));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("时间重叠"));
        }
    }

    @Nested
    @DisplayName("完整四级结构")
    class ValidAgenda {
        @Test
        void validFullAgenda_passes() {
            Topic t = topic("议题1");
            SubVenue sub = subVenue("主会场", Collections.singletonList(t));
            Session ses = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.singletonList(sub));
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Collections.singletonList(ses));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }
    }

    private Meeting createMeetingWithTimeWindow(LocalDateTime start, LocalDateTime end, List<ScheduleDay> scheduleDays) {
        Meeting m = new Meeting();
        m.setStartTime(start);
        m.setEndTime(end);
        m.setScheduleDays(scheduleDays);
        return m;
    }

    @Nested
    @DisplayName("环节时间必须在会议时间窗口内")
    class SessionTimeWindow {

        @Test
        @DisplayName("半天上午会议(09:00-13:00)：环节09:00-12:00 → 通过")
        void halfDayMorning_sessionWithinWindow_passes() {
            Session ses = session("上午场", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 13, 0),
                    Collections.singletonList(d));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("半天上午会议(09:00-13:00)：环节09:00-21:00 → 结束时间超出，拒绝")
        void halfDayMorning_sessionEndExceedsWindow_throws() {
            Session ses = session("测试", LocalTime.of(9, 0), LocalTime.of(21, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 13, 0),
                    Collections.singletonList(d));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能晚于会议结束时间"));
        }

        @Test
        @DisplayName("半天上午会议(09:00-13:00)：环节07:00-12:00 → 开始时间超前，拒绝")
        void halfDayMorning_sessionStartBeforeWindow_throws() {
            Session ses = session("早场", LocalTime.of(7, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 13, 0),
                    Collections.singletonList(d));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能早于会议开始时间"));
        }

        @Test
        @DisplayName("半天下午会议(14:00-18:00)：环节14:00-17:30 → 通过")
        void halfDayAfternoon_sessionWithinWindow_passes() {
            Session ses = session("下午场", LocalTime.of(14, 0), LocalTime.of(17, 30), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 14, 0),
                    LocalDateTime.of(2026, 4, 7, 18, 0),
                    Collections.singletonList(d));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("半天下午会议(14:00-18:00)：环节09:00-17:00 → 开始时间超前，拒绝")
        void halfDayAfternoon_sessionStartBeforeWindow_throws() {
            Session ses = session("全天场", LocalTime.of(9, 0), LocalTime.of(17, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 14, 0),
                    LocalDateTime.of(2026, 4, 7, 18, 0),
                    Collections.singletonList(d));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能早于会议开始时间"));
        }

        @Test
        @DisplayName("半天下午会议(14:00-18:00)：环节14:00-20:00 → 结束时间超出，拒绝")
        void halfDayAfternoon_sessionEndExceedsWindow_throws() {
            Session ses = session("加班场", LocalTime.of(14, 0), LocalTime.of(20, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 14, 0),
                    LocalDateTime.of(2026, 4, 7, 18, 0),
                    Collections.singletonList(d));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能晚于会议结束时间"));
        }

        @Test
        @DisplayName("一天会议(09:00-17:00)：环节09:00-17:00 → 通过")
        void oneDay_sessionExactWindow_passes() {
            Session ses = session("全天", LocalTime.of(9, 0), LocalTime.of(17, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 17, 0),
                    Collections.singletonList(d));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("一天会议(09:00-17:00)：环节08:00-18:00 → 两端超出，拒绝")
        void oneDay_sessionExceedsBothEnds_throws() {
            Session ses = session("超时场", LocalTime.of(8, 0), LocalTime.of(18, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 17, 0),
                    Collections.singletonList(d));
            assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("两天会议：第一天环节不能早于会议开始时间")
        void twoDays_firstDaySessionStartBeforeMeeting_throws() {
            Session ses = session("早场", LocalTime.of(7, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay d1 = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses));
            Session ses2 = session("第二天", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay d2 = day(LocalDate.of(2026, 4, 8), Collections.singletonList(ses2));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 8, 17, 0),
                    Arrays.asList(d1, d2));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能早于会议开始时间"));
        }

        @Test
        @DisplayName("两天会议：最后一天环节不能晚于会议结束时间")
        void twoDays_lastDaySessionEndAfterMeeting_throws() {
            Session ses1 = session("第一天", LocalTime.of(9, 0), LocalTime.of(18, 0), Collections.emptyList());
            ScheduleDay d1 = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses1));
            Session ses2 = session("晚场", LocalTime.of(9, 0), LocalTime.of(20, 0), Collections.emptyList());
            ScheduleDay d2 = day(LocalDate.of(2026, 4, 8), Collections.singletonList(ses2));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 8, 17, 0),
                    Arrays.asList(d1, d2));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("不能晚于会议结束时间"));
        }

        @Test
        @DisplayName("两天会议：所有环节在时间窗口内 → 通过")
        void twoDays_allSessionsWithinWindow_passes() {
            Session ses1 = session("第一天", LocalTime.of(10, 0), LocalTime.of(18, 0), Collections.emptyList());
            ScheduleDay d1 = day(LocalDate.of(2026, 4, 7), Collections.singletonList(ses1));
            Session ses2 = session("第二天", LocalTime.of(9, 0), LocalTime.of(16, 0), Collections.emptyList());
            ScheduleDay d2 = day(LocalDate.of(2026, 4, 8), Collections.singletonList(ses2));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 8, 17, 0),
                    Arrays.asList(d1, d2));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("半天会议(09:00-13:00)：多个环节均在窗口内 → 通过")
        void halfDay_multipleSessionsWithinWindow_passes() {
            Session s1 = session("环节1", LocalTime.of(9, 0), LocalTime.of(10, 0), Collections.emptyList());
            Session s2 = session("环节2", LocalTime.of(10, 30), LocalTime.of(11, 30), Collections.emptyList());
            Session s3 = session("环节3", LocalTime.of(12, 0), LocalTime.of(13, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Arrays.asList(s1, s2, s3));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 13, 0),
                    Collections.singletonList(d));
            assertDoesNotThrow(() -> domainService.validateAgendaIntegrity(m));
        }

        @Test
        @DisplayName("半天会议(09:00-13:00)：多个环节中一个超出 → 拒绝")
        void halfDay_oneOfMultipleSessionsExceedsWindow_throws() {
            Session s1 = session("环节1", LocalTime.of(9, 0), LocalTime.of(10, 0), Collections.emptyList());
            Session s2 = session("环节2", LocalTime.of(11, 0), LocalTime.of(14, 0), Collections.emptyList());
            ScheduleDay d = day(LocalDate.of(2026, 4, 7), Arrays.asList(s1, s2));
            Meeting m = createMeetingWithTimeWindow(
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 13, 0),
                    Collections.singletonList(d));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class,
                    () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("环节2"));
            assertTrue(ex.getMessage().contains("不能晚于会议结束时间"));
        }
    }
}
