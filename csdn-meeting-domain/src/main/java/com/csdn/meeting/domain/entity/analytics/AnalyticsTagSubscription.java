package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签订阅埋点实体 - 记录用户的标签订阅/取消订阅行为
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyticsTagSubscription extends AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联事件ID
     */
    private String eventId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 操作类型(subscribe/unsubscribe)
     */
    private String action;

    /**
     * 操作来源
     */
    private String source;

    /**
     * 发生时间
     */
    private LocalDateTime occurredAt;

    // 操作类型常量
    public static final class Actions {
        public static final String SUBSCRIBE = "subscribe";
        public static final String UNSUBSCRIBE = "unsubscribe";
    }
}
