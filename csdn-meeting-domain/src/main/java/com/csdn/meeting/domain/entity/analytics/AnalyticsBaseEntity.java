package com.csdn.meeting.domain.entity.analytics;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 埋点实体基类
 */
@Data
public class AnalyticsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}
