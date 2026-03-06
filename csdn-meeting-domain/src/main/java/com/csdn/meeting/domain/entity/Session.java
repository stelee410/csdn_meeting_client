package com.csdn.meeting.domain.entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 环节（四级结构第二级）
 * 业务不变量：同一 ScheduleDay 内 Session 开始/结束时间不得重叠（由 MeetingDomainService 校验）
 */
public class Session implements Serializable {

    private final String sessionName;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<SubVenue> subVenues;

    public Session(String sessionName, LocalTime startTime, LocalTime endTime, List<SubVenue> subVenues) {
        this.sessionName = sessionName;
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null");
        this.endTime = Objects.requireNonNull(endTime, "endTime must not be null");
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("session startTime must not be after endTime");
        }
        this.subVenues = subVenues == null ? new ArrayList<>() : new ArrayList<>(subVenues);
    }

    public String getSessionName() {
        return sessionName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public List<SubVenue> getSubVenues() {
        return Collections.unmodifiableList(subVenues);
    }

    /** 检查与另一环节时间是否重叠（同一日） */
    public boolean overlaps(Session other) {
        return !this.endTime.isBefore(other.startTime) && !this.startTime.isAfter(other.endTime);
    }
}
