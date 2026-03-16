package com.csdn.meeting.domain.repository.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsTagSubscription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签订阅埋点仓储接口
 */
public interface AnalyticsTagSubscriptionRepository {

    /**
     * 保存标签订阅记录
     */
    void save(AnalyticsTagSubscription subscription);

    /**
     * 根据用户ID查询订阅记录
     */
    List<AnalyticsTagSubscription> findByUserId(String userId);

    /**
     * 根据标签ID查询订阅记录
     */
    List<AnalyticsTagSubscription> findByTagId(Long tagId);

    /**
     * 根据用户ID和标签ID查询订阅记录
     */
    List<AnalyticsTagSubscription> findByUserIdAndTagId(String userId, Long tagId);

    /**
     * 查询指定时间范围内的订阅记录
     */
    List<AnalyticsTagSubscription> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户订阅的标签数量
     */
    long countSubscribedTagsByUserId(String userId);

    /**
     * 统计标签被订阅的次数
     */
    long countSubscriptionsByTagId(Long tagId);
}
