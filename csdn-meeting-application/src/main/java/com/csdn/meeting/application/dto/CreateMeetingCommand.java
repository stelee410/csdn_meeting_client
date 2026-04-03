package com.csdn.meeting.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建会议草稿命令
 * 仅校验 title 非空，日程可为空
 */
public class CreateMeetingCommand {

    private String title;
    private String description;
    private String creatorId;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;

    // agent.prd §1.1 新增字段
    private String organizer;
    private String format;           // ONLINE/OFFLINE/HYBRID
    private String meetingType;      // SUMMIT/SALON/WORKSHOP...
    private String scene;
    private String venue;
    private String regions;          // JSON
    private String coverImage;
    private String tags;             // 逗号分隔的 tagId，如 1,2,3
    private String targetAudience;   // JSON
    private Boolean isPremium;

    // V18 场景扩展字段
    private String sceneIndustry;
    private String sceneProduct;
    private String sceneMarketingRegions;
    private String sceneUniversities;

    // 四级日程结构
    private List<ScheduleDayDTO> scheduleDays;

    /** 联系人姓名（与 creatorName 二选一，优先 contactName） */
    private String contactName;
    private String contactPhone;
    private String department;
    private String position;

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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public List<ScheduleDayDTO> getScheduleDays() {
        return scheduleDays;
    }

    public void setScheduleDays(List<ScheduleDayDTO> scheduleDays) {
        this.scheduleDays = scheduleDays;
    }

    public String getSceneIndustry() { return sceneIndustry; }
    public void setSceneIndustry(String sceneIndustry) { this.sceneIndustry = sceneIndustry; }
    public String getSceneProduct() { return sceneProduct; }
    public void setSceneProduct(String sceneProduct) { this.sceneProduct = sceneProduct; }
    public String getSceneMarketingRegions() { return sceneMarketingRegions; }
    public void setSceneMarketingRegions(String sceneMarketingRegions) { this.sceneMarketingRegions = sceneMarketingRegions; }
    public String getSceneUniversities() { return sceneUniversities; }
    public void setSceneUniversities(String sceneUniversities) { this.sceneUniversities = sceneUniversities; }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
