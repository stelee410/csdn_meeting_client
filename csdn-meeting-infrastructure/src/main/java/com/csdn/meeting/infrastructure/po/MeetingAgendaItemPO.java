package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis-Plus entity for t_meeting_agenda_item (tree structure).
 * level: 1=ScheduleDay 2=Session 3=SubVenue 4=Topic
 */
@TableName("t_meeting_agenda_item")
public class MeetingAgendaItemPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meeting_id")
    private Long meetingId;

    @TableField("parent_id")
    private Long parentId;

    @TableField("level")
    private Integer level;

    @TableField("title")
    private String title;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("extra")
    private String extra;

    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 软删除标志：0-未删除, 1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private List<MeetingAgendaItemPO> children = new ArrayList<>();

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
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public List<MeetingAgendaItemPO> getChildren() { return children; }
    public void setChildren(List<MeetingAgendaItemPO> children) { this.children = children != null ? children : new ArrayList<>(); }
}
