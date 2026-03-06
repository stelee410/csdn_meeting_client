package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for t_meeting_agenda_item (tree structure).
 * level: 1=ScheduleDay 2=Session 3=SubVenue 4=Topic
 */
@Entity
@Table(name = "t_meeting_agenda_item", indexes = {
    @Index(name = "idx_meeting_id", columnList = "meeting_id"),
    @Index(name = "idx_parent_id", columnList = "parent_id")
})
public class MeetingAgendaItemPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "extra", columnDefinition = "json")
    private String extra;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private List<MeetingAgendaItemPO> children = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<MeetingAgendaItemPO> getChildren() { return children; }
    public void setChildren(List<MeetingAgendaItemPO> children) { this.children = children != null ? children : new ArrayList<>(); }
}
