package com.csdn.meeting.infrastructure.mapper.analytics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsTagSubscriptionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签订阅埋点Mapper接口
 */
@Mapper
public interface AnalyticsTagSubscriptionMapper extends BaseMapper<AnalyticsTagSubscriptionPO> {

    /**
     * 根据用户ID查询订阅记录
     */
    @Select("SELECT * FROM analytics_tag_subscription WHERE user_id = #{userId} ORDER BY occurred_at DESC")
    List<AnalyticsTagSubscriptionPO> selectByUserId(@Param("userId") String userId);

    /**
     * 根据标签ID查询订阅记录
     */
    @Select("SELECT * FROM analytics_tag_subscription WHERE tag_id = #{tagId} ORDER BY occurred_at DESC")
    List<AnalyticsTagSubscriptionPO> selectByTagId(@Param("tagId") Long tagId);

    /**
     * 根据用户ID和标签ID查询订阅记录
     */
    @Select("SELECT * FROM analytics_tag_subscription WHERE user_id = #{userId} AND tag_id = #{tagId} ORDER BY occurred_at DESC")
    List<AnalyticsTagSubscriptionPO> selectByUserIdAndTagId(
            @Param("userId") String userId,
            @Param("tagId") Long tagId);

    /**
     * 查询指定时间范围内的订阅记录
     */
    @Select("SELECT * FROM analytics_tag_subscription WHERE occurred_at >= #{startTime} AND occurred_at <= #{endTime} ORDER BY occurred_at DESC")
    List<AnalyticsTagSubscriptionPO> selectByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户订阅的标签数量
     */
    @Select("SELECT COUNT(DISTINCT tag_id) FROM analytics_tag_subscription WHERE user_id = #{userId} AND action = 'subscribe'")
    Long countSubscribedTagsByUserId(@Param("userId") String userId);

    /**
     * 统计标签被订阅的次数
     */
    @Select("SELECT COUNT(*) FROM analytics_tag_subscription WHERE tag_id = #{tagId} AND action = 'subscribe'")
    Long countSubscriptionsByTagId(@Param("tagId") Long tagId);
}
