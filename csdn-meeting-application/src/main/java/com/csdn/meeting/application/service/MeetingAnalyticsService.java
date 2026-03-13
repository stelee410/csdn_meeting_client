package com.csdn.meeting.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 会议分析埋点服务
 * 预留接口，后续对接CSDN数据分析平台
 * 当前仅打印日志，不实际发送埋点数据
 *
 * TODO【需要和CSDN对接数据分析平台】：
 * 1. 当前所有埋点方法仅打印日志，未上报真实数据
 * 2. 需对接CSDN数据分析平台埋点上报接口
 * 3. 上报方式：可考虑异步批量上报，减少对主流程影响
 * 4. 需CSDN提供：数据分析平台埋点API文档、字段规范、上报频率限制
 *
 * 需上报的埋点事件：
 * - trackViewSwitch: 视图切换（统计用户偏好的视图模式）
 * - trackFilter: 筛选操作（统计各维度筛选使用率）
 * - trackTagSubscribe: 标签订阅（统计订阅转化率）
 * - trackMeetingClick: 会议点击（统计列表点击率CTR）
 */
@Service
public class MeetingAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingAnalyticsService.class);

    /**
     * 记录视图切换事件
     *
     * @param userId     用户ID
     * @param targetView 目标视图（list/card）
     *
     * TODO【CSDN对接-数据分析】：上报视图切换埋点，统计用户偏好的视图模式(list/card)
     */
    public void trackViewSwitch(String userId, String targetView) {
        // TODO【CSDN对接-数据分析】：调用CSDN数据分析平台接口上报埋点
        logger.info("[Analytics:ViewSwitch-待对接CSDN数据分析平台] userId={}, targetView={}", userId, targetView);
    }

    /**
     * 记录筛选操作事件
     *
     * @param userId    用户ID
     * @param dimension 筛选维度
     * @param value     筛选值
     *
     * TODO【CSDN对接-数据分析】：上报筛选操作埋点，统计各维度筛选使用率
     */
    public void trackFilter(String userId, String dimension, String value) {
        // TODO【CSDN对接-数据分析】：调用CSDN数据分析平台接口上报埋点
        logger.info("[Analytics:Filter-待对接CSDN数据分析平台] userId={}, dimension={}, value={}", userId, dimension, value);
    }

    /**
     * 记录标签订阅事件
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @param action 操作（subscribe/unsubscribe）
     *
     * TODO【CSDN对接-数据分析】：上报标签订阅埋点，统计订阅转化率
     */
    public void trackTagSubscribe(String userId, Long tagId, String action) {
        // TODO【CSDN对接-数据分析】：调用CSDN数据分析平台接口上报埋点
        logger.info("[Analytics:TagSubscribe-待对接CSDN数据分析平台] userId={}, tagId={}, action={}", userId, tagId, action);
    }

    /**
     * 记录会议卡片点击事件
     *
     * @param userId    用户ID
     * @param meetingId 会议ID
     * @param position  位置（列表中的索引）
     *
     * TODO【CSDN对接-数据分析】：上报会议点击埋点，统计列表点击率(CTR)
     */
    public void trackMeetingClick(String userId, String meetingId, int position) {
        // TODO【CSDN对接-数据分析】：调用CSDN数据分析平台接口上报埋点
        logger.info("[Analytics:MeetingClick-待对接CSDN数据分析平台] userId={}, meetingId={}, position={}", userId, meetingId, position);
    }

    // ========== 移动端特有埋点 ==========

    /**
     * 记录会议频道添加事件
     * 用户将「会议」加入「我的频道」
     *
     * @param userId 用户ID
     *
     * TODO【CSDN对接-数据分析】：上报频道添加埋点，统计用户发现会议频道的转化率
     */
    public void trackMobileChannelAdd(Long userId) {
        logger.info("[Analytics:MobileChannelAdd-待对接CSDN数据分析平台] userId={}", userId);
    }

    /**
     * 记录会议首页曝光事件
     * 用户进入会议首页（卡片列表页）
     *
     * @param userId 用户ID
     * @param source 来源（channel_tab-频道tab/my_events-我的会议入口）
     *
     * TODO【CSDN对接-数据分析】：上报首页曝光埋点，统计会议频道活跃度
     */
    public void trackMobileHomeExposure(Long userId, String source) {
        logger.info("[Analytics:MobileHomeExposure-待对接CSDN数据分析平台] userId={}, source={}", userId, source);
    }

    /**
     * 记录移动端发起会议入口点击事件
     *
     * @param userId 用户ID
     * @param source 来源（home-首页/other-其他）
     *
     * TODO【CSDN对接-数据分析】：上报发起会议入口点击埋点，统计移动端发起会议意愿
     */
    public void trackMobileCreateEntryClick(Long userId, String source) {
        logger.info("[Analytics:MobileCreateEntryClick-待对接CSDN数据分析平台] userId={}, source={}", userId, source);
    }

    /**
     * 记录我的会议入口点击事件
     *
     * @param userId 用户ID
     *
     * TODO【CSDN对接-数据分析】：上报我的会议入口点击埋点，统计用户管理会议意愿
     */
    public void trackMobileMyEventsEntryClick(Long userId) {
        logger.info("[Analytics:MobileMyEventsEntryClick-待对接CSDN数据分析平台] userId={}", userId);
    }

    /**
     * 记录我收藏的会议页签点击事件
     *
     * @param userId 用户ID
     *
     * TODO【CSDN对接-数据分析】：上报收藏页签点击埋点，统计收藏功能使用率
     */
    public void trackMobileFavoritesTabClick(Long userId) {
        logger.info("[Analytics:MobileFavoritesTabClick-待对接CSDN数据分析平台] userId={}", userId);
    }

    /**
     * 记录会议签到扫码事件
     *
     * @param userId    用户ID
     * @param meetingId 会议ID
     * @param result    扫码结果（success-签到成功/not_registered-未报名/duplicate-重复签到/invalid_code-无效码）
     *
     * TODO【CSDN对接-数据分析】：上报签到扫码埋点，统计现场签到使用率
     */
    public void trackMobileCheckinScan(Long userId, String meetingId, String result) {
        logger.info("[Analytics:MobileCheckinScan-待对接CSDN数据分析平台] userId={}, meetingId={}, result={}", userId, meetingId, result);
    }
}
