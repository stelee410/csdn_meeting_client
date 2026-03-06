package com.csdn.meeting.domain.port;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * AI 解析结果，与 MeetingDTO 字段集对应。
 * Infrastructure 层通过 LLM 解析文件后填充此类。
 */
public class AIParseResult {

    private String title;
    private String description;
    private String organizer;
    private String format;
    private String scene;
    private String venue;
    private String regions;
    private String coverImage;
    private List<String> tags;
    private String targetAudience;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** 四级日程结构 JSON，由 LLM 输出 */
    private String scheduleDaysJson;

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

    public List<String> getTags() {
        return tags != null ? tags : Collections.emptyList();
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
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

    public String getScheduleDaysJson() {
        return scheduleDaysJson;
    }

    public void setScheduleDaysJson(String scheduleDaysJson) {
        this.scheduleDaysJson = scheduleDaysJson;
    }
}
