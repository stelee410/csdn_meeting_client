package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.event.MeetingPublishedEvent;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import com.csdn.meeting.infrastructure.client.CsdnMessagePushClient;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议发布推送服务
 * 监听会议发布事件，向订阅了该会议标签的用户发送通知
 * 已对接CSDN消息中心IM站内信和APP推送接口
 * 按tag分别发送，可追踪各tag的推送效果
 */
@Component
public class MeetingPublishPushService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingPublishPushService.class);

    private static final int MAX_BATCH_SIZE = 1000;

    private final UserTagSubscribeRepository userTagSubscribeRepository;
    private final TagRepository tagRepository;
    private final CsdnMessagePushClient messagePushClient;

    public MeetingPublishPushService(UserTagSubscribeRepository userTagSubscribeRepository,
                                      TagRepository tagRepository,
                                      CsdnMessagePushClient messagePushClient) {
        this.userTagSubscribeRepository = userTagSubscribeRepository;
        this.tagRepository = tagRepository;
        this.messagePushClient = messagePushClient;
    }

    /**
     * 监听会议发布事件
     * 异步处理，避免阻塞主流程
     * 按tag分别发送推送，便于追踪各tag的推送效果
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

        // 查询所有标签信息（包括tagName）
        List<Tag> tags = tagRepository.findByIds(event.getTagIds());
        Map<Long, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        // 按tag分别查询订阅用户
        Map<Long, Set<String>> tagUserMap = querySubscribedUsersByTag(event.getTagIds());
        
        // 过滤掉没有订阅用户的tag
        tagUserMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        
        if (tagUserMap.isEmpty()) {
            logger.info("标签 {} 暂无订阅用户", event.getTagIds());
            return;
        }

        // 统计总用户数（去重）
        Set<String> allUsers = tagUserMap.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        logger.info("会议 {} 共关联 {} 个标签，总订阅用户 {} 人", 
                event.getMeetingId(), tagUserMap.size(), allUsers.size());

        // 按tag分别发送推送
        for (Map.Entry<Long, Set<String>> entry : tagUserMap.entrySet()) {
            Long tagId = entry.getKey();
            Set<String> userIds = entry.getValue();
            
            // 获取tagName
            String tagName = tagMap.getOrDefault(tagId, new Tag()).getTagName();
            if (tagName == null || tagName.isEmpty()) {
                tagName = String.valueOf(tagId);
            }
            
            logger.info("[Tag={}] 开始向 {} 个用户推送会议发布通知, tagName={}", tagId, userIds.size(), tagName);
            
            // 1. 发送IM站内信通知
            sendSiteMessage(tagId, tagName, userIds, event);
            
            // 2. 发送App Push通知
            sendPushNotification(tagId, tagName, userIds, event);
        }

        logger.info("会议 {} 推送完成，共处理 {} 个标签", event.getMeetingId(), tagUserMap.size());
    }

    /**
     * 按tag分别查询订阅用户
     * 返回Map<tagId, 该tag的订阅用户集合>
     */
    private Map<Long, Set<String>> querySubscribedUsersByTag(List<Long> tagIds) {
        Map<Long, Set<String>> result = new HashMap<>();
        for (Long tagId : tagIds) {
            List<String> userIds = userTagSubscribeRepository.findUserIdsByTagId(tagId);
            result.put(tagId, new HashSet<>(userIds));
        }
        return result;
    }

    /**
     * 发送站内信通知
     * 使用CSDN消息中心IM接口，按tag分批发送
     * params中包含: meetingTitle, meetingId, tag, tagId
     */
    private void sendSiteMessage(Long tagId, String tagName, Set<String> userIds, MeetingPublishedEvent event) {
        String meetingId = event.getMeetingId();
        String title = event.getTitle();
        
        logger.info("[Tag={}] 开始发送IM站内信通知: meetingId={}, userCount={}, tagName={}", 
                tagId, meetingId, userIds.size(), tagName);

        // 分批处理，每批最多1000人
        List<String> userList = new ArrayList<>(userIds);
        int totalBatches = (userList.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        
        for (int i = 0; i < userList.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userList.subList(i, Math.min(i + MAX_BATCH_SIZE, userList.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;
            
            try {
                CsdnMessageResponse response = messagePushClient.sendMeetingPublishIm(
                        meetingId, batch, title, meetingId, tagId, tagName);
                
                if (response.isSuccess()) {
                    logger.info("[Tag={}] IM站内信批次发送成功: meetingId={}, batch={}/{}", 
                            tagId, meetingId, batchNum, totalBatches);
                } else {
                    logger.warn("[Tag={}] IM站内信批次发送失败: meetingId={}, batch={}/{}, code={}, message={}",
                            tagId, meetingId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("[Tag={}] IM站内信批次发送异常: meetingId={}, batch={}/{}", 
                        tagId, meetingId, batchNum, totalBatches, e);
            }
        }
        
        logger.info("[Tag={}] IM站内信通知发送完成: meetingId={}, totalUsers={}", 
                tagId, meetingId, userIds.size());
    }

    /**
     * 发送App Push通知
     * 使用CSDN消息中心PUSH接口，按tag分批发送
     * params中包含: meetingTitle, meetingId, tag, tagId
     */
    private void sendPushNotification(Long tagId, String tagName, Set<String> userIds, MeetingPublishedEvent event) {
        String meetingId = event.getMeetingId();
        String title = event.getTitle();
        
        logger.info("[Tag={}] 开始发送App Push通知: meetingId={}, userCount={}, tagName={}", 
                tagId, meetingId, userIds.size(), tagName);

        // 分批处理，每批最多1000人
        List<String> userList = new ArrayList<>(userIds);
        int totalBatches = (userList.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        
        for (int i = 0; i < userList.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userList.subList(i, Math.min(i + MAX_BATCH_SIZE, userList.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;
            
            try {
                CsdnMessageResponse response = messagePushClient.sendMeetingPublishPush(
                        meetingId, batch, title, meetingId, tagId, tagName);
                
                if (response.isSuccess()) {
                    logger.info("[Tag={}] App Push批次发送成功: meetingId={}, batch={}/{}", 
                            tagId, meetingId, batchNum, totalBatches);
                } else {
                    logger.warn("[Tag={}] App Push批次发送失败: meetingId={}, batch={}/{}, code={}, message={}",
                            tagId, meetingId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("[Tag={}] App Push批次发送异常: meetingId={}, batch={}/{}", 
                        tagId, meetingId, batchNum, totalBatches, e);
            }
        }
        
        logger.info("[Tag={}] App Push通知发送完成: meetingId={}, totalUsers={}", 
                tagId, meetingId, userIds.size());
    }
}
