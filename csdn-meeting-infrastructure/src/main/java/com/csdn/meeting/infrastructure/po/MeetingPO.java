package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for t_meeting, aligned with Flyway V1 DDL.
 * Uses id as identity (no meeting_id column).
 */
@Entity
@Table(name = "t_meeting")
public class MeetingPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "organizer", length = 100)
    private String organizer;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "format", length = 20)
    private String format;

    @Column(name = "scene", length = 50)
    private String scene;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "venue", length = 255)
    private String venue;

    @Column(name = "regions", columnDefinition = "json")
    private String regions;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "tags", columnDefinition = "json")
    private String tags;

    @Column(name = "target_audience", columnDefinition = "json")
    private String targetAudience;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_premium")
    private Boolean isPremium;

    @Column(name = "takedown_reason", length = 500)
    private String takedownReason;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ---- getters / setters ----

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
}
