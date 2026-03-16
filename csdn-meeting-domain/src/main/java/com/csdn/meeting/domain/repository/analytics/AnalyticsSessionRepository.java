package com.csdn.meeting.domain.repository.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话跟踪仓储接口
 */
public interface AnalyticsSessionRepository {

    /**
     * 保存会话
     */
    void save(AnalyticsSession session);

    /**
     * 更新会话
     */
    void update(AnalyticsSession session);

    /**
     * 根据会话ID查询
     */
    AnalyticsSession findBySessionId(String sessionId);

    /**
     * 根据用户ID查询会话列表
     */
    List<AnalyticsSession> findByUserId(String userId);

    /**
     * 查询指定时间范围内的会话列表
     */
    List<AnalyticsSession> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 结束会话
     */
    void endSession(String sessionId, LocalDateTime endTime);

    /**
     * 增加页面浏览次数
     */
    void incrementPageViews(String sessionId);

    /**
     * 增加事件触发次数
     */
    void incrementEventsCount(String sessionId);
}
