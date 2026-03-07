package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议阅读视图项DTO（卡片视图）
 * 适用于沉浸式浏览场景
 * 突出海报、摘要、亮点，提供类似文章阅读的体验
 */
@Schema(description = "会议阅读视图项（卡片视图），适用于沉浸式浏览场景，突出海报、标签、热度")
public class MeetingCardItemDTO {

    @Schema(description = "会议ID", example = "1")
    private Long id;

    @Schema(description = "会议业务ID", example = "MT0000000001")
    private String meetingId;

    @Schema(description = "会议标题", example = "2024 Java技术峰会")
    private String title;

    @Schema(description = "会议描述/简介", example = "汇聚业界顶尖Java专家，探讨最新技术趋势...")
    private String description;

    // 海报（16:9大图）
    @Schema(description = "封面图URL（16:9大图）", example = "https://img.csdn.com/meeting/cover/123.jpg")
    private String coverImage;

    // 主办方信息
    @Schema(description = "主办方用户ID", example = "10086")
    private Long organizerId;

    @Schema(description = "主办方名称", example = "CSDN")
    private String organizerName;

    @Schema(description = "主办方头像URL", example = "https://img.csdn.com/avatar/csdn.png")
    private String organizerAvatar;

    // 状态
    @Schema(description = "状态编码", example = "PUBLISHED")
    private String status;

    @Schema(description = "状态显示名称", example = "报名中")
    private String statusDisplay;

    // 时间信息
    @Schema(description = "开始时间", example = "2024-03-15T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-03-15T18:00:00")
    private LocalDateTime endTime;

    @Schema(description = "时间显示格式", example = "03-15 ~ 03-15")
    private String timeDisplay;

    // 会议形式
    @Schema(description = "会议形式编码", example = "HYBRID")
    private String format;

    @Schema(description = "会议形式显示名称", example = "线上+线下")
    private String formatDisplay;

    // 会议类型
    @Schema(description = "会议类型编码", example = "SUMMIT")
    private String meetingType;

    @Schema(description = "会议类型显示名称", example = "技术峰会")
    private String meetingTypeDisplay;

    // 会议场景
    @Schema(description = "会议场景编码", example = "DEVELOPER")
    private String scene;

    @Schema(description = "会议场景显示名称", example = "开发者会议")
    private String sceneDisplay;

    // 标签（展示前3个）
    @Schema(description = "会议标签列表（通常展示前3个）")
    private List<TagDTO> tags;

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

    // 地点（线下/混合时展示）
    @Schema(description = "城市名称", example = "北京")
    private String cityName;

    @Schema(description = "场馆/详细地址", example = "国家会议中心")
    private String venue;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerAvatar() {
        return organizerAvatar;
    }

    public void setOrganizerAvatar(String organizerAvatar) {
        this.organizerAvatar = organizerAvatar;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTimeDisplay() {
        return timeDisplay;
    }

    public void setTimeDisplay(String timeDisplay) {
        this.timeDisplay = timeDisplay;
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

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingTypeDisplay() {
        return meetingTypeDisplay;
    }

    public void setMeetingTypeDisplay(String meetingTypeDisplay) {
        this.meetingTypeDisplay = meetingTypeDisplay;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getSceneDisplay() {
        return sceneDisplay;
    }

    public void setSceneDisplay(String sceneDisplay) {
        this.sceneDisplay = sceneDisplay;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
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
}
