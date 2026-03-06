package com.csdn.meeting.application.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 日程日（四级结构第一级）
 */
public class ScheduleDayDTO {

    private LocalDate scheduleDate;
    private String dayLabel;
    private List<SessionDTO> sessions;

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public List<SessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }
}
