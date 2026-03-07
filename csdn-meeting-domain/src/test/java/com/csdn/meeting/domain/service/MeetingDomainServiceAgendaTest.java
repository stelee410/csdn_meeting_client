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
    @DisplayName("缺失 SubVenue")
    class MissingSubVenue {
        @Test
        void emptySubVenues_throws() {
            Session session = session("上午", LocalTime.of(9, 0), LocalTime.of(12, 0), Collections.emptyList());
            ScheduleDay day = day(LocalDate.of(2026, 3, 1), Collections.singletonList(session));
            Meeting m = createMeetingWithAgenda(Collections.singletonList(day));
            AgendaIntegrityException ex = assertThrows(AgendaIntegrityException.class, () -> domainService.validateAgendaIntegrity(m));
            assertTrue(ex.getMessage().contains("至少需要1个分会场"));
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
}
