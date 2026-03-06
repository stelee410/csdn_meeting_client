package com.csdn.meeting.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 日程日（四级结构第一级）
 * 业务不变量：scheduleDate 必须在 Meeting.startTime ~ endTime 范围内（由 MeetingDomainService 校验）
 */
public class ScheduleDay implements Serializable {

    private final LocalDate scheduleDate;
    private final String dayLabel;
    private final List<Session> sessions;

    public ScheduleDay(LocalDate scheduleDate, String dayLabel, List<Session> sessions) {
        this.scheduleDate = Objects.requireNonNull(scheduleDate, "scheduleDate must not be null");
        this.dayLabel = dayLabel;
        this.sessions = sessions == null ? new ArrayList<>() : new ArrayList<>(sessions);
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public List<Session> getSessions() {
        return Collections.unmodifiableList(sessions);
    }
}
