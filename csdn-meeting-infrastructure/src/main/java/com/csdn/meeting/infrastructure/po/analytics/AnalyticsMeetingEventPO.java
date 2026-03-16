package com.csdn.meeting.infrastructure.po.analytics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议业务事件扩展持久化对象
 */
@Data
@TableName("analytics_meeting_event")
public class AnalyticsMeetingEventPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("meeting_id")
    private String meetingId;

    @TableField("meeting_title")
    private String meetingTitle;

    @TableField("organizer_id")
    private String organizerId;

    @TableField("action_type")
    private String actionType;

    @TableField("source")
    private String source;

    @TableField("referrer")
    private String referrer;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
