package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus entity for t_meeting, aligned with Flyway V1 DDL.
 */
@TableName("t_meeting")
public class MeetingPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("organizer")
    private String organizer;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("format")
    private String format;

    @TableField("scene")
    private String scene;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("venue")
    private String venue;

    @TableField("regions")
    private String regions;

    @TableField("cover_image")
    private String coverImage;

    @TableField("description")
    private String description;

    @TableField("tags")
    private String tags;

    @TableField("target_audience")
    private String targetAudience;

    @TableField("status")
    private Integer status;

    @TableField("is_premium")
    private Boolean isPremium;

    @TableField("takedown_reason")
    private String takedownReason;

    @TableField("reject_reason")
    private String rejectReason;

    // V5新增字段 - 会议列表功能
    @TableField("meeting_id")
    private String meetingId;

    @TableField("poster_url")
    private String posterUrl;

    @TableField("hot_score")
    private Integer hotScore;

    @TableField("current_participants")
    private Integer currentParticipants;

    @TableField("max_participants")
    private Integer maxParticipants;

    @TableField("city_code")
    private String cityCode;

    @TableField("city_name")
    private String cityName;

    @TableField("meeting_type")
    private String meetingType;

    @TableField("organizer_id")
    private Long organizerId;

    @TableField("organizer_name")
    private String organizerName;

    @TableField("organizer_avatar")
    private String organizerAvatar;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 软删除标志：0-未删除, 1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getRegions() { return regions; }
    public void setRegions(String regions) { this.regions = regions; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }
    public String getTakedownReason() { return takedownReason; }
    public void setTakedownReason(String takedownReason) { this.takedownReason = takedownReason; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // V5新增字段的getter/setter
    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public Integer getHotScore() { return hotScore; }
    public void setHotScore(Integer hotScore) { this.hotScore = hotScore; }
    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getMeetingType() { return meetingType; }
    public void setMeetingType(String meetingType) { this.meetingType = meetingType; }
    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    public String getOrganizerAvatar() { return organizerAvatar; }
    public void setOrganizerAvatar(String organizerAvatar) { this.organizerAvatar = organizerAvatar; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
