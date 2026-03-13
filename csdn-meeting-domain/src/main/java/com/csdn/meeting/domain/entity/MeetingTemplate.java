package com.csdn.meeting.domain.entity;

/**
 * 活动模板（运营端维护，办会方只读）
 * 用于快速创建会议草稿
 */
public class MeetingTemplate extends BaseEntity {

    private String name;
    private String scene;
    private String descriptionTemplate;
    private String defaultTags;       // JSON
    private String targetAudience;    // JSON
    private String meetingDuration;   // 会议时长
    private String meetingScale;      // 会议规模
    private String frequency;         // 举办频率
    private Integer sortOrder;
    private Boolean isActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getDescriptionTemplate() {
        return descriptionTemplate;
    }

    public void setDescriptionTemplate(String descriptionTemplate) {
        this.descriptionTemplate = descriptionTemplate;
    }

    public String getDefaultTags() {
        return defaultTags;
    }

    public void setDefaultTags(String defaultTags) {
        this.defaultTags = defaultTags;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getMeetingDuration() {
        return meetingDuration;
    }

    public void setMeetingDuration(String meetingDuration) {
        this.meetingDuration = meetingDuration;
    }

    public String getMeetingScale() {
        return meetingScale;
    }

    public void setMeetingScale(String meetingScale) {
        this.meetingScale = meetingScale;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
