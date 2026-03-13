package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("t_meeting_template")
public class MeetingTemplatePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("scene")
    private String scene;

    @TableField("description_template")
    private String descriptionTemplate;

    @TableField("default_tags")
    private String defaultTags;

    @TableField("target_audience")
    private String targetAudience;

    @TableField("meeting_duration")
    private String meetingDuration;

    @TableField("meeting_scale")
    private String meetingScale;

    @TableField("frequency")
    private String frequency;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_active")
    private Boolean isActive;

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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }
    public String getDescriptionTemplate() { return descriptionTemplate; }
    public void setDescriptionTemplate(String descriptionTemplate) { this.descriptionTemplate = descriptionTemplate; }
    public String getDefaultTags() { return defaultTags; }
    public void setDefaultTags(String defaultTags) { this.defaultTags = defaultTags; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public String getMeetingDuration() { return meetingDuration; }
    public void setMeetingDuration(String meetingDuration) { this.meetingDuration = meetingDuration; }
    public String getMeetingScale() { return meetingScale; }
    public void setMeetingScale(String meetingScale) { this.meetingScale = meetingScale; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
