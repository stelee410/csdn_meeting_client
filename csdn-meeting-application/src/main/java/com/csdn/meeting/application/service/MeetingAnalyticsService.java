package com.csdn.meeting.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 会议分析埋点服务
 * 预留接口，后续对接CSDN数据分析平台
 * 当前仅打印日志，不实际发送埋点数据
 */
@Service
public class MeetingAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingAnalyticsService.class);

    /**
     * 记录视图切换事件
     *
     * @param userId     用户ID
     * @param targetView 目标视图（list/card）
     */
    public void trackViewSwitch(String userId, String targetView) {
        // TODO: 后续对接CSDN数据分析平台
        logger.info("[Analytics:ViewSwitch] userId={}, targetView={}", userId, targetView);
    }

    /**
     * 记录筛选操作事件
     *
     * @param userId    用户ID
     * @param dimension 筛选维度
     * @param value     筛选值
     */
    public void trackFilter(String userId, String dimension, String value) {
        // TODO: 后续对接CSDN数据分析平台
        logger.info("[Analytics:Filter] userId={}, dimension={}, value={}", userId, dimension, value);
    }

    /**
     * 记录标签订阅事件
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @param action 操作（subscribe/unsubscribe）
     */
    public void trackTagSubscribe(String userId, Long tagId, String action) {
        // TODO: 后续对接CSDN数据分析平台
        logger.info("[Analytics:TagSubscribe] userId={}, tagId={}, action={}", userId, tagId, action);
    }

    /**
     * 记录会议卡片点击事件
     *
     * @param userId    用户ID
     * @param meetingId 会议ID
     * @param position  位置（列表中的索引）
     */
    public void trackMeetingClick(String userId, String meetingId, int position) {
        // TODO: 后续对接CSDN数据分析平台
        logger.info("[Analytics:MeetingClick] userId={}, meetingId={}, position={}", userId, meetingId, position);
    }
}
