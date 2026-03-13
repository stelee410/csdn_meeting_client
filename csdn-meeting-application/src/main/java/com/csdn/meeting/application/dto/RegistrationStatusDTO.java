package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议报名状态查询DTO
 */
@Data
@Schema(description = "会议报名状态")
public class RegistrationStatusDTO {

    @Schema(description = "会议ID", example = "M123456789")
    private String meetingId;

    @Schema(description = "当前报名人数", example = "856")
    private Integer currentParticipants;

    @Schema(description = "最大参与人数（0或null表示不限）", example = "1000")
    private Integer maxParticipants;

    @Schema(description = "剩余名额（-1表示不限名额）", example = "144")
    private Integer remainingSpots;

    @Schema(description = "报名截止时间")
    private LocalDateTime regEndTime;

    @Schema(description = "是否名额已满", example = "false")
    private boolean full;

    @Schema(description = "报名是否开放", example = "true")
    private boolean registrationOpen;

    @Schema(description = "是否需要签到", example = "true")
    private Boolean requireCheckin;

    @Schema(description = "报名人数显示文案", example = "856 / 1000 人")
    private String participantsDisplay;

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public Integer getRemainingSpots() { return remainingSpots; }
    public void setRemainingSpots(Integer remainingSpots) { this.remainingSpots = remainingSpots; }
    public LocalDateTime getRegEndTime() { return regEndTime; }
    public void setRegEndTime(LocalDateTime regEndTime) { this.regEndTime = regEndTime; }
    public boolean isFull() { return full; }
    public void setFull(boolean full) { this.full = full; }
    public boolean isRegistrationOpen() { return registrationOpen; }
    public void setRegistrationOpen(boolean registrationOpen) { this.registrationOpen = registrationOpen; }
    public Boolean getRequireCheckin() { return requireCheckin; }
    public void setRequireCheckin(Boolean requireCheckin) { this.requireCheckin = requireCheckin; }
    public String getParticipantsDisplay() { return participantsDisplay; }
    public void setParticipantsDisplay(String participantsDisplay) { this.participantsDisplay = participantsDisplay; }
}
