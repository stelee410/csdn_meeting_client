package com.csdn.meeting.infrastructure.po.analytics;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签订阅埋点持久化对象
 */
@Data
@TableName("analytics_tag_subscription")
public class AnalyticsTagSubscriptionPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private String eventId;

    @TableField("user_id")
    private String userId;

    @TableField("tag_id")
    private Long tagId;

    @TableField("tag_name")
    private String tagName;

    @TableField("action")
    private String action;

    @TableField("source")
    private String source;

    @TableField("occurred_at")
    private LocalDateTime occurredAt;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
