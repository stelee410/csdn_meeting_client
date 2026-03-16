package com.csdn.meeting.application.service.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsEvent;

import java.util.List;
import java.util.Map;

/**
 * 埋点服务接口
 * 用于记录各种用户行为和业务操作事件
 */
public interface AnalyticsService {

    /**
     * 记录通用事件
     */
    void trackEvent(AnalyticsEvent event);

    /**
     * 记录带属性的事件
     */
    void trackEvent(String eventType, String userId, Map<String, Object> properties);

    /**
     * 记录会议列表筛选
     */
    void trackMeetingListFilter(String userId, String format, String meetingType, String scene,
                                 String timeRange, String keyword, Integer resultCount);

    /**
     * 记录会议点击
     */
    void trackMeetingClick(String userId, String meetingId, String source);

    /**
     * 记录视图切换
     */
    void trackViewSwitch(String userId, String targetView);

    /**
     * 记录标签订阅
     */
    void trackTagSubscribe(String userId, Long tagId, String tagName, String source);

    /**
     * 记录标签取消订阅
     */
    void trackTagUnsubscribe(String userId, Long tagId, String tagName, String source);

    /**
     * 记录会议创建
     */
    void trackMeetingCreate(String userId, String meetingId, String meetingType);

    /**
     * 记录会议提交审核
     */
    void trackMeetingSubmit(String userId, String meetingId);

    /**
     * 记录会议发布
     */
    void trackMeetingPublish(String userId, String meetingId, String organizerId);

    /**
     * 记录报名参会
     */
    void trackMeetingRegister(String userId, String meetingId);

    /**
     * 记录签到
     */
    void trackMeetingCheckin(String userId, String meetingId, String result);

    /**
     * 记录收藏会议
     */
    void trackMeetingFavorite(String userId, String meetingId, boolean isAdd);

    // ============== 移动端埋点 ==============

    /**
     * 记录移动端首页曝光
     */
    void trackMobileHomeExposure(String userId, String source);

    /**
     * 记录发起会议入口点击
     */
    void trackMobileCreateEntryClick(String userId, String source);

    /**
     * 记录我的会议入口点击
     */
    void trackMobileMyEventsClick(String userId);

    /**
     * 记录收藏页签点击
     */
    void trackMobileFavoritesTabClick(String userId);

    /**
     * 记录签到扫码
     */
    void trackMobileCheckinScan(String userId, String meetingId, String result);

    /**
     * 记录频道添加
     */
    void trackMobileChannelAdd(String userId, String channelId);

    // ============== 运营端埋点 ==============

    /**
     * 记录会议审核通过
     */
    void trackMeetingAuditApprove(String operatorId, String operatorName,
                                   String meetingId, String meetingTitle, String organizerId);

    /**
     * 记录会议审核驳回
     */
    void trackMeetingAuditReject(String operatorId, String operatorName,
                                  String meetingId, String meetingTitle, String organizerId,
                                  List<String> violationTags, String comment);

    /**
     * 记录会议强制下架
     */
    void trackMeetingTakedown(String operatorId, String operatorName,
                              String meetingId, String meetingTitle, String organizerId,
                              List<String> violationTags, String comment, String originalStatus);

    /**
     * 记录模板创建
     */
    void trackTemplateCreate(String operatorId, Long templateId, String templateName);

    /**
     * 记录模板编辑
     */
    void trackTemplateUpdate(String operatorId, Long templateId);

    /**
     * 记录模板删除
     */
    void trackTemplateDelete(String operatorId, Long templateId);

    /**
     * 记录模板上架
     */
    void trackTemplateList(String operatorId, Long templateId, String templateName,
                           String oldStatus, String newStatus);

    /**
     * 记录模板下架
     */
    void trackTemplateUnlist(String operatorId, Long templateId, String templateName,
                             String oldStatus, String newStatus);

    /**
     * 记录数据看板查看
     */
    void trackDashboardView(String operatorId, String page, String module);

    /**
     * 记录统计数据曝光
     */
    void trackDashboardStatsExpose(String operatorId, String component, Map<String, Object> metrics);

    /**
     * 记录推广会议列表查看
     */
    void trackDashboardPromotedMeetingsView(String operatorId, Integer pageNum, Integer count);
}
