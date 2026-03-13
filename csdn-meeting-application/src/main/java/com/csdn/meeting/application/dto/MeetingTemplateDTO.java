package com.csdn.meeting.application.dto;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * 活动模板 DTO
 */
public class MeetingTemplateDTO {

    private Long id;
    private String name;
    private String scene;
    private String descriptionTemplate;
    private String defaultTags;
    private String targetAudience;
    private String meetingDuration;
    private String meetingScale;
    private String frequency;
    private Integer sortOrder;
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    /**
     * 兼容前端传数字类型（issue001：接口文档为 string，实际传数字才能存）
     */
    @JsonSetter("targetAudience")
    public void setTargetAudienceFlexible(Object value) {
        this.targetAudience = value == null ? null : String.valueOf(value);
    }

    public String getMeetingDuration() { return meetingDuration; }
    public void setMeetingDuration(String meetingDuration) { this.meetingDuration = meetingDuration; }
    public String getMeetingScale() { return meetingScale; }
    public void setMeetingScale(String meetingScale) { this.meetingScale = meetingScale; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

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
