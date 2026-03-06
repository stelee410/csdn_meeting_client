package com.csdn.meeting.application.dto;

import java.time.LocalTime;
import java.util.List;

/**
 * 环节（四级结构第二级）
 */
public class SessionDTO {

    private String sessionName;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<SubVenueDTO> subVenues;

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<SubVenueDTO> getSubVenues() {
        return subVenues;
    }

    public void setSubVenues(List<SubVenueDTO> subVenues) {
        this.subVenues = subVenues;
    }
}
