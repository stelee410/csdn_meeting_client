package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 会议列表视图项DTO
 * 适用于紧凑的列表展示（高效查找场景）
 * 字段精简，侧重时间、地点、状态等关键元数据
 */
@Schema(description = "会议列表视图项，适用于紧凑列表展示，侧重时间、地点、状态等关键元数据")
public class MeetingListItemDTO {

    @Schema(description = "会议ID", example = "1")
    private Long id;

    @Schema(description = "会议业务ID", example = "MT0000000001")
    private String meetingId;

    @Schema(description = "会议标题", example = "2024 Java技术峰会")
    private String title;

    @Schema(description = "海报URL", example = "https://img.csdn.com/meeting/poster/123.jpg")
    private String posterUrl;

    @Schema(description = "状态编码", example = "PUBLISHED")
    private String status;

    @Schema(description = "状态显示名称", example = "报名中")
    private String statusDisplay;

    // 时间信息
    @Schema(description = "开始时间", example = "2024-03-15T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "开始时间显示格式", example = "03-15 09:00")
    private String startTimeDisplay;

    // 地点信息
    @Schema(description = "城市名称", example = "北京")
    private String cityName;

    @Schema(description = "场馆/详细地址", example = "国家会议中心")
    private String venue;

    // 会议形式
    @Schema(description = "会议形式编码", example = "ONLINE")
    private String format;

    @Schema(description = "会议形式显示名称", example = "线上")
    private String formatDisplay;

    // 热度信息
    @Schema(description = "热度分数（报名人数）", example = "1500")
    private Integer hotScore;

    @Schema(description = "热度显示格式", example = "1.5k人感兴趣")
    private String hotScoreDisplay;

    // 报名进度
    @Schema(description = "当前报名人数", example = "1500")
    private Integer currentParticipants;

    @Schema(description = "最大参与人数", example = "2000")
    private Integer maxParticipants;

    @Schema(description = "报名进度显示", example = "1500 / 2000 人")
    private String participantsDisplay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeDisplay() {
        return startTimeDisplay;
    }

    public void setStartTimeDisplay(String startTimeDisplay) {
        this.startTimeDisplay = startTimeDisplay;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormatDisplay() {
        return formatDisplay;
    }

    public void setFormatDisplay(String formatDisplay) {
        this.formatDisplay = formatDisplay;
    }

    public Integer getHotScore() {
        return hotScore;
    }

    public void setHotScore(Integer hotScore) {
        this.hotScore = hotScore;
    }

    public String getHotScoreDisplay() {
        return hotScoreDisplay;
    }

    public void setHotScoreDisplay(String hotScoreDisplay) {
        this.hotScoreDisplay = hotScoreDisplay;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getParticipantsDisplay() {
        return participantsDisplay;
    }

    public void setParticipantsDisplay(String participantsDisplay) {
        this.participantsDisplay = participantsDisplay;
    }
}
