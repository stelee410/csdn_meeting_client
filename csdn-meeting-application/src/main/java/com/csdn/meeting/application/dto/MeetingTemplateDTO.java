package com.csdn.meeting.application.dto;

/**
 * 活动模板 DTO，对应 t_activity_template 表
 */
public class MeetingTemplateDTO {

    private Long id;
    private String name;
    private String description;
    private String iconEmoji;
    private Integer sortWeight;
    /** 0=DRAFT, 1=UNLISTED, 2=LISTED */
    private Integer status;
    private Integer useCount;
    private String defaultMeetingType;
    private String defaultForm;
    private String defaultScene;
    private String defaultScale;
    private String defaultDuration;
    private String defaultRecurrence;
    private String defaultTitlePrefix;
    private String defaultHostCompany;
    private String defaultDepartment;
    private String defaultContact;
    private String defaultContactTitle;
    private String defaultContactPhone;
    private String defaultIntro;
    private String coverUrl;
    private String defaultAudience;
    private String defaultTags;
    private String defaultTopicSkeleton;
    private String defaultPanelSkeleton;
    private String defaultOtherContent;
    private String defaultImageMedia;
    private String defaultTextMedia;
    private String defaultDevType;
    private String defaultIndustry;
    private String defaultProducts;
    private String defaultRegions;
    private String defaultUniversities;
    private String defaultLocation;

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
}
