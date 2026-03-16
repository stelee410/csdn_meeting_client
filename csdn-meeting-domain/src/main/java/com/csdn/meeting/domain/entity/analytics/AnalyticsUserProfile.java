package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户画像实体 - 用户属性汇总
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsUserProfile extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 首次访问时间
     */
    private LocalDateTime firstVisitAt;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastVisitAt;

    /**
     * 总会话数
     */
    private Integer totalSessions = 0;

    /**
     * 总事件数
     */
    private Integer totalEvents = 0;

    /**
     * 偏好标签
     */
    private String preferredTags;

    /**
     * 使用过的平台
     */
    private String devicePlatforms;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}
