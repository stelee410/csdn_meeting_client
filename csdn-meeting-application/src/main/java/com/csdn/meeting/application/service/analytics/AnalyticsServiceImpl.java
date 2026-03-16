package com.csdn.meeting.application.service.analytics;

import com.csdn.meeting.domain.entity.analytics.*;
import com.csdn.meeting.domain.repository.analytics.AnalyticsEventRepository;
import com.csdn.meeting.domain.repository.analytics.AnalyticsMeetingEventRepository;
import com.csdn.meeting.domain.repository.analytics.AnalyticsTagSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 埋点服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsEventRepository eventRepository;
    private final AnalyticsMeetingEventRepository meetingEventRepository;
    private final AnalyticsTagSubscriptionRepository tagSubscriptionRepository;

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackEvent(AnalyticsEvent event) {
        try {
            eventRepository.save(event);
            log.debug("Tracked event: type={}, userId={}", event.getEventType(), event.getUserId());
        } catch (Exception e) {
            log.error("Failed to track event: type={}, userId={}", event.getEventType(), event.getUserId(), e);
        }
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackEvent(String eventType, String userId, Map<String, Object> properties) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(eventType)
                .userId(userId)
                .build();

        if (properties != null) {
            event.setProperties(properties);
        }

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingListFilter(String userId, String format, String meetingType, String scene,
                                       String timeRange, String keyword, Integer resultCount) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_LIST_FILTER)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("format", format)
                .property("meeting_type", meetingType)
                .property("scene", scene)
                .property("time_range", timeRange)
                .property("keyword", keyword)
                .property("result_count", resultCount);

        trackEvent(event);

        // 保存到专用表
        saveMeetingListFilter(event.getEventId(), userId, format, meetingType, scene, timeRange, keyword, resultCount);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingClick(String userId, String meetingId, String source) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_CLICK)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("source", source);

        trackEvent(event);

        // 保存会议事件
        saveMeetingEvent(event.getEventId(), meetingId, null, null, "click", source, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackViewSwitch(String userId, String targetView) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_VIEW_SWITCH)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("target_view", targetView);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTagSubscribe(String userId, Long tagId, String tagName, String source) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TAG_SUBSCRIBE)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("tag_id", tagId)
                .property("tag_name", tagName)
                .property("action", "subscribe");

        trackEvent(event);

        // 保存到专用表
        saveTagSubscription(event.getEventId(), userId, tagId, tagName, "subscribe", source);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTagUnsubscribe(String userId, Long tagId, String tagName, String source) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TAG_UNSUBSCRIBE)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("tag_id", tagId)
                .property("tag_name", tagName)
                .property("action", "unsubscribe");

        trackEvent(event);

        // 保存到专用表
        saveTagSubscription(event.getEventId(), userId, tagId, tagName, "unsubscribe", source);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingCreate(String userId, String meetingId, String meetingType) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_CREATE)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("meeting_type", meetingType);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, userId, "create", null, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingSubmit(String userId, String meetingId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_SUBMIT)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, userId, "submit", null, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingPublish(String userId, String meetingId, String organizerId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_PUBLISH)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("organizer_id", organizerId);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, organizerId, "publish", null, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingRegister(String userId, String meetingId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_REGISTER)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, null, "register", null, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingCheckin(String userId, String meetingId, String result) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_CHECKIN)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("result", result);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, null, "checkin", null, null);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingFavorite(String userId, String meetingId, boolean isAdd) {
        String action = isAdd ? "add" : "remove";
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_FAVORITE)
                .eventCategory(AnalyticsEvent.Categories.CLIENT)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("action", action);

        trackEvent(event);

        saveMeetingEvent(event.getEventId(), meetingId, null, null, "favorite_" + action, null, null);
    }

    // ============== 移动端埋点 ==============

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileHomeExposure(String userId, String source) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_HOME_EXPOSURE)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .platform(AnalyticsEvent.Platforms.IOS) // 或根据实际平台设置
                .build();

        event.property("source", source);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileCreateEntryClick(String userId, String source) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_CREATE_ENTRY_CLICK)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .build();

        event.property("source", source);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileMyEventsClick(String userId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_MY_EVENTS_CLICK)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .build();

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileFavoritesTabClick(String userId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_FAVORITES_TAB_CLICK)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .build();

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileCheckinScan(String userId, String meetingId, String result) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_CHECKIN_SCAN)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .build();

        event.property("meeting_id", meetingId)
                .property("result", result);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMobileChannelAdd(String userId, String channelId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MOBILE_CHANNEL_ADD)
                .eventCategory(AnalyticsEvent.Categories.MOBILE)
                .userId(userId)
                .build();

        event.property("channel_id", channelId);

        trackEvent(event);
    }

    // ============== 运营端埋点 ==============

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingAuditApprove(String operatorId, String operatorName,
                                         String meetingId, String meetingTitle, String organizerId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_AUDIT_APPROVE)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("operator_name", operatorName)
                .property("meeting_id", meetingId)
                .property("meeting_title", meetingTitle)
                .property("organizer_id", organizerId);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingAuditReject(String operatorId, String operatorName,
                                       String meetingId, String meetingTitle, String organizerId,
                                       List<String> violationTags, String comment) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_AUDIT_REJECT)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("operator_name", operatorName)
                .property("meeting_id", meetingId)
                .property("meeting_title", meetingTitle)
                .property("organizer_id", organizerId)
                .property("violation_tags", violationTags != null ? String.join(",", violationTags) : null)
                .property("comment", comment);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackMeetingTakedown(String operatorId, String operatorName,
                                    String meetingId, String meetingTitle, String organizerId,
                                    List<String> violationTags, String comment, String originalStatus) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.MEETING_TAKEDOWN)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("operator_name", operatorName)
                .property("meeting_id", meetingId)
                .property("meeting_title", meetingTitle)
                .property("organizer_id", organizerId)
                .property("violation_tags", violationTags != null ? String.join(",", violationTags) : null)
                .property("comment", comment)
                .property("original_status", originalStatus);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTemplateCreate(String operatorId, Long templateId, String templateName) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TEMPLATE_CREATE)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("template_id", templateId)
                .property("template_name", templateName);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTemplateUpdate(String operatorId, Long templateId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TEMPLATE_UPDATE)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("template_id", templateId);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTemplateDelete(String operatorId, Long templateId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TEMPLATE_DELETE)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("template_id", templateId);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTemplateList(String operatorId, Long templateId, String templateName,
                                 String oldStatus, String newStatus) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TEMPLATE_LIST)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("template_id", templateId)
                .property("template_name", templateName)
                .property("old_status", oldStatus)
                .property("new_status", newStatus);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackTemplateUnlist(String operatorId, Long templateId, String templateName,
                                   String oldStatus, String newStatus) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.TEMPLATE_UNLIST)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("template_id", templateId)
                .property("template_name", templateName)
                .property("old_status", oldStatus)
                .property("new_status", newStatus);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackDashboardView(String operatorId, String page, String module) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.DASHBOARD_VIEW)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("page", page)
                .property("module", module);

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackDashboardStatsExpose(String operatorId, String component, Map<String, Object> metrics) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.DASHBOARD_STATS_EXPOSE)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("component", component);
        if (metrics != null) {
            metrics.forEach(event::property);
        }

        trackEvent(event);
    }

    @Async("analyticsTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void trackDashboardPromotedMeetingsView(String operatorId, Integer pageNum, Integer count) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(AnalyticsEvent.EventTypes.DASHBOARD_PROMOTED_MEETINGS_VIEW)
                .eventCategory(AnalyticsEvent.Categories.OPERATION)
                .userId(operatorId)
                .userType(AnalyticsEvent.UserTypes.OPERATOR)
                .build();

        event.property("page_num", pageNum)
                .property("count", count);

        trackEvent(event);
    }

    // ============== 私有辅助方法 ==============

    private void saveMeetingEvent(String eventId, String meetingId, String meetingTitle,
                                  String organizerId, String actionType, String source, String referrer) {
        try {
            AnalyticsMeetingEvent event = new AnalyticsMeetingEvent();
            event.setEventId(eventId);
            event.setMeetingId(meetingId);
            event.setMeetingTitle(meetingTitle);
            event.setOrganizerId(organizerId);
            event.setActionType(actionType);
            event.setSource(source);
            event.setReferrer(referrer);
            meetingEventRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to save meeting event: eventId={}, meetingId={}", eventId, meetingId, e);
        }
    }

    private void saveTagSubscription(String eventId, String userId, Long tagId,
                                     String tagName, String action, String source) {
        try {
            AnalyticsTagSubscription subscription = new AnalyticsTagSubscription();
            subscription.setEventId(eventId);
            subscription.setUserId(userId);
            subscription.setTagId(tagId);
            subscription.setTagName(tagName);
            subscription.setAction(action);
            subscription.setSource(source);
            subscription.setOccurredAt(LocalDateTime.now());
            tagSubscriptionRepository.save(subscription);
        } catch (Exception e) {
            log.error("Failed to save tag subscription: eventId={}, userId={}, tagId={}",
                    eventId, userId, tagId, e);
        }
    }

    private void saveMeetingListFilter(String eventId, String userId, String format,
                                       String meetingType, String scene, String timeRange,
                                       String keyword, Integer resultCount) {
        // 这里可以保存到专用的列表筛选表，如果需要的话
        // 目前数据已通过properties存储在analytics_event表中
    }
}
