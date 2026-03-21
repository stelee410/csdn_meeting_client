package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("t_activity_template")
public class MeetingTemplatePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("icon_emoji")
    private String iconEmoji;

    @TableField("sort_weight")
    private Integer sortWeight;

    /** 0=DRAFT, 1=UNLISTED, 2=LISTED */
    @TableField("status")
    private Integer status;

    @TableField("use_count")
    private Integer useCount;

    @TableField("default_meeting_type")
    private String defaultMeetingType;

    @TableField("default_form")
    private String defaultForm;

    @TableField("default_scene")
    private String defaultScene;

    @TableField("default_scale")
    private String defaultScale;

    @TableField("default_duration")
    private String defaultDuration;

    @TableField("default_recurrence")
    private String defaultRecurrence;

    @TableField("default_title_prefix")
    private String defaultTitlePrefix;

    @TableField("default_host_company")
    private String defaultHostCompany;

    @TableField("default_department")
    private String defaultDepartment;

    @TableField("default_contact")
    private String defaultContact;

    @TableField("default_contact_title")
    private String defaultContactTitle;

    @TableField("default_contact_phone")
    private String defaultContactPhone;

    @TableField("default_intro")
    private String defaultIntro;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("default_audience")
    private String defaultAudience;

    @TableField("default_tags")
    private String defaultTags;

    @TableField("default_topic_skeleton")
    private String defaultTopicSkeleton;

    @TableField("default_panel_skeleton")
    private String defaultPanelSkeleton;

    @TableField("default_other_content")
    private String defaultOtherContent;

    @TableField("default_image_media")
    private String defaultImageMedia;

    @TableField("default_text_media")
    private String defaultTextMedia;

    @TableField("default_dev_type")
    private String defaultDevType;

    @TableField("default_industry")
    private String defaultIndustry;

    @TableField("default_products")
    private String defaultProducts;

    @TableField("default_regions")
    private String defaultRegions;

    @TableField("default_universities")
    private String defaultUniversities;

    @TableField("default_location")
    private String defaultLocation;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }
    public Integer getSortWeight() { return sortWeight; }
    public void setSortWeight(Integer sortWeight) { this.sortWeight = sortWeight; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getUseCount() { return useCount; }
    public void setUseCount(Integer useCount) { this.useCount = useCount; }
    public String getDefaultMeetingType() { return defaultMeetingType; }
    public void setDefaultMeetingType(String defaultMeetingType) { this.defaultMeetingType = defaultMeetingType; }
    public String getDefaultForm() { return defaultForm; }
    public void setDefaultForm(String defaultForm) { this.defaultForm = defaultForm; }
    public String getDefaultScene() { return defaultScene; }
    public void setDefaultScene(String defaultScene) { this.defaultScene = defaultScene; }
    public String getDefaultScale() { return defaultScale; }
    public void setDefaultScale(String defaultScale) { this.defaultScale = defaultScale; }
    public String getDefaultDuration() { return defaultDuration; }
    public void setDefaultDuration(String defaultDuration) { this.defaultDuration = defaultDuration; }
    public String getDefaultRecurrence() { return defaultRecurrence; }
    public void setDefaultRecurrence(String defaultRecurrence) { this.defaultRecurrence = defaultRecurrence; }
    public String getDefaultTitlePrefix() { return defaultTitlePrefix; }
    public void setDefaultTitlePrefix(String defaultTitlePrefix) { this.defaultTitlePrefix = defaultTitlePrefix; }
    public String getDefaultHostCompany() { return defaultHostCompany; }
    public void setDefaultHostCompany(String defaultHostCompany) { this.defaultHostCompany = defaultHostCompany; }
    public String getDefaultDepartment() { return defaultDepartment; }
    public void setDefaultDepartment(String defaultDepartment) { this.defaultDepartment = defaultDepartment; }
    public String getDefaultContact() { return defaultContact; }
    public void setDefaultContact(String defaultContact) { this.defaultContact = defaultContact; }
    public String getDefaultContactTitle() { return defaultContactTitle; }
    public void setDefaultContactTitle(String defaultContactTitle) { this.defaultContactTitle = defaultContactTitle; }
    public String getDefaultContactPhone() { return defaultContactPhone; }
    public void setDefaultContactPhone(String defaultContactPhone) { this.defaultContactPhone = defaultContactPhone; }
    public String getDefaultIntro() { return defaultIntro; }
    public void setDefaultIntro(String defaultIntro) { this.defaultIntro = defaultIntro; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getDefaultAudience() { return defaultAudience; }
    public void setDefaultAudience(String defaultAudience) { this.defaultAudience = defaultAudience; }
    public String getDefaultTags() { return defaultTags; }
    public void setDefaultTags(String defaultTags) { this.defaultTags = defaultTags; }
    public String getDefaultTopicSkeleton() { return defaultTopicSkeleton; }
    public void setDefaultTopicSkeleton(String defaultTopicSkeleton) { this.defaultTopicSkeleton = defaultTopicSkeleton; }
    public String getDefaultPanelSkeleton() { return defaultPanelSkeleton; }
    public void setDefaultPanelSkeleton(String defaultPanelSkeleton) { this.defaultPanelSkeleton = defaultPanelSkeleton; }
    public String getDefaultOtherContent() { return defaultOtherContent; }
    public void setDefaultOtherContent(String defaultOtherContent) { this.defaultOtherContent = defaultOtherContent; }
    public String getDefaultImageMedia() { return defaultImageMedia; }
    public void setDefaultImageMedia(String defaultImageMedia) { this.defaultImageMedia = defaultImageMedia; }
    public String getDefaultTextMedia() { return defaultTextMedia; }
    public void setDefaultTextMedia(String defaultTextMedia) { this.defaultTextMedia = defaultTextMedia; }
    public String getDefaultDevType() { return defaultDevType; }
    public void setDefaultDevType(String defaultDevType) { this.defaultDevType = defaultDevType; }
    public String getDefaultIndustry() { return defaultIndustry; }
    public void setDefaultIndustry(String defaultIndustry) { this.defaultIndustry = defaultIndustry; }
    public String getDefaultProducts() { return defaultProducts; }
    public void setDefaultProducts(String defaultProducts) { this.defaultProducts = defaultProducts; }
    public String getDefaultRegions() { return defaultRegions; }
    public void setDefaultRegions(String defaultRegions) { this.defaultRegions = defaultRegions; }
    public String getDefaultUniversities() { return defaultUniversities; }
    public void setDefaultUniversities(String defaultUniversities) { this.defaultUniversities = defaultUniversities; }
    public String getDefaultLocation() { return defaultLocation; }
    public void setDefaultLocation(String defaultLocation) { this.defaultLocation = defaultLocation; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}
