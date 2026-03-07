package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.UserTagSubscribe;
import com.csdn.meeting.domain.event.MeetingPublishedEvent;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议发布推送服务
 * 监听会议发布事件，向订阅了该会议标签的用户发送通知
 * 
 * TODO: 目前仅打印日志，需要接入CSDN消息中心后完善实现
 */
@Component
public class MeetingPublishPushService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingPublishPushService.class);

    private final UserTagSubscribeRepository userTagSubscribeRepository;
    private final MeetingAnalyticsService analyticsService;

    public MeetingPublishPushService(UserTagSubscribeRepository userTagSubscribeRepository,
                                      MeetingAnalyticsService analyticsService) {
        this.userTagSubscribeRepository = userTagSubscribeRepository;
        this.analyticsService = analyticsService;
    }

    /**
     * 监听会议发布事件
     * 异步处理，避免阻塞主流程
     */
    @EventListener
    @Async
    public void onMeetingPublished(MeetingPublishedEvent event) {
        logger.info("收到会议发布事件: meetingId={}, title={}, tagIds={}", 
                event.getMeetingId(), event.getTitle(), event.getTagIds());

        if (event.getTagIds() == null || event.getTagIds().isEmpty()) {
            logger.info("会议 {} 没有关联标签，跳过推送", event.getMeetingId());
            return;
        }

        // 查询订阅了这些标签的所有用户
        Set<String> userIds = querySubscribedUsers(event.getTagIds());
        
        if (userIds.isEmpty()) {
            logger.info("标签 {} 暂无订阅用户", event.getTagIds());
            return;
        }

        logger.info("将向 {} 个用户推送会议发布通知: {}", userIds.size(), userIds);

        // TODO: 接入CSDN消息中心后，调用以下接口：
        // 1. 站内信通知
        // sendSiteMessage(userIds, event);
        // 2. App Push通知
        // sendPushNotification(userIds, event);

        // 记录埋点
        for (String userId : userIds) {
            analyticsService.trackMeetingClick(userId, event.getMeetingId(), -1);
        }
    }

    /**
     * 查询订阅了指定标签的所有用户ID（去重）
     */
    private Set<String> querySubscribedUsers(List<Long> tagIds) {
        return tagIds.stream()
                .flatMap(tagId -> userTagSubscribeRepository.findUserIdsByTagId(tagId).stream())
                .collect(Collectors.toSet());
    }

    /**
     * 发送站内信通知（预留接口，待接入CSDN消息中心）
     * 
     * TODO: 实现调用CSDN消息中心接口
     */
    private void sendSiteMessage(Set<String> userIds, MeetingPublishedEvent event) {
        String title = "您订阅的标签有新会议发布";
        String content = String.format("会议《%s》已发布，快来报名吧！", event.getTitle());
        String link = "/meeting/detail/" + event.getMeetingId();

        logger.info("[站内信通知] 用户: {}, 标题: {}, 内容: {}, 链接: {}", 
                userIds, title, content, link);
        
        // TODO: 调用CSDN消息中心接口
        // messageCenterService.sendSiteMessage(userIds, title, content, link);
    }

    /**
     * 发送App Push通知（预留接口，待接入CSDN推送服务）
     * 
     * TODO: 实现调用CSDN推送服务接口
     */
    private void sendPushNotification(Set<String> userIds, MeetingPublishedEvent event) {
        String title = "您订阅的标签有新会议";
        String content = event.getTitle();

        logger.info("[App Push通知] 用户: {}, 标题: {}, 内容: {}", 
                userIds, title, content);
        
        // TODO: 调用CSDN推送服务接口
        // pushService.sendNotification(userIds, title, content, payload);
    }
}
