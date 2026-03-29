package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationAuditedEvent;
import com.csdn.meeting.domain.port.MessagePushPort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报名审核通知服务
 * 监听报名审核事件，发送站内信通知
 * 改为内部存储推送（不再调用CSDN），通过MessagePushPort存储到数据库供前端拉取
 *
 * 通知场景：
 * 1. 报名审核通过 → 发送报名成功通知
 * 2. 报名审核拒绝 → 发送报名拒绝通知
 */
@Service
public class RegistrationNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationNotificationService.class);

    private static final int MAX_BATCH_SIZE = 1000;

    private final MeetingRepository meetingRepository;
    private final MessagePushPort messagePushPort;

    public RegistrationNotificationService(MeetingRepository meetingRepository,
                                          MessagePushPort messagePushPort) {
        this.meetingRepository = meetingRepository;
        this.messagePushPort = messagePushPort;
    }

    /**
     * 监听报名审核事件
     * 异步处理，避免阻塞主流程
     */
    @EventListener
    @Async
    public void onRegistrationAudited(RegistrationAuditedEvent event) {
        logger.info("收到报名审核事件: registrationId={}, meetingId={}, userId={}, status={}",
                event.getRegistrationId(), event.getMeetingId(), event.getUserId(), event.getStatus());

        // 查询会议信息
        Meeting meeting = meetingRepository.findById(event.getMeetingId()).orElse(null);
        if (meeting == null) {
            logger.error("发送通知失败：会议不存在, meetingId={}", event.getMeetingId());
            return;
        }

        // 获取用户ID列表（单用户）
        List<String> userIds = Collections.singletonList(String.valueOf(event.getUserId()));

        // 根据审核状态发送对应通知
        if (event.getStatus() == Registration.RegistrationStatus.APPROVED) {
            sendApprovedNotification(event, meeting, userIds);
        } else if (event.getStatus() == Registration.RegistrationStatus.REJECTED) {
            sendRejectedNotification(event, meeting, userIds);
        } else {
            logger.warn("未知的审核状态: {}", event.getStatus());
        }
    }

    /**
     * 发送报名通过通知
     * 改为内部存储推送（不再调用CSDN）
     */
    private void sendApprovedNotification(RegistrationAuditedEvent event,
                                          Meeting meeting,
                                          List<String> userIds) {
        String meetingId = meeting.getMeetingId();
        String meetingName = meeting.getTitle();
        String startTime = meeting.getStartTime() != null ?
                meeting.getStartTime().toString() : "";
        String venue = meeting.getVenue() != null ? meeting.getVenue() : "";

        String bizId = "registration_" + event.getRegistrationId();

        // 构造消息标题和内容
        String title = "【报名成功】" + meetingName;
        String content = String.format("恭喜您！报名申请已通过。会议：%s，时间：%s，地点：%s",
                meetingName, startTime, venue);

        // 准备扩展数据
        Map<String, Object> extra = new HashMap<>();
        extra.put("meetingId", meetingId);
        extra.put("meetingTitle", meetingName);
        extra.put("startTime", startTime);
        extra.put("venue", venue);
        extra.put("registrationId", event.getRegistrationId());

        // 发送站内信
        sendSiteMessageBatch(bizId, MessagePushPort.MessageType.REGISTRATION_APPROVED,
                userIds, title, content, extra, event.getRegistrationId());

    }

    /**
     * 发送报名拒绝通知
     * 改为内部存储推送（不再调用CSDN）
     */
    private void sendRejectedNotification(RegistrationAuditedEvent event,
                                          Meeting meeting,
                                          List<String> userIds) {
        String meetingId = meeting.getMeetingId();
        String meetingTitle = meeting.getTitle();
        String auditRemark = event.getAuditRemark() != null ? event.getAuditRemark() : "";

        String bizId = "registration_" + event.getRegistrationId();

        // 构造消息标题和内容
        String title = "【报名未通过】" + meetingTitle;
        String content = String.format("很抱歉，您的报名申请未通过。会议：%s。原因：%s",
                meetingTitle, auditRemark.isEmpty() ? "未提供具体原因" : auditRemark);

        // 准备扩展数据
        Map<String, Object> extra = new HashMap<>();
        extra.put("meetingId", meetingId);
        extra.put("meetingTitle", meetingTitle);
        extra.put("rejectReason", auditRemark);
        extra.put("registrationId", event.getRegistrationId());

        // 发送站内信
        sendSiteMessageBatch(bizId, MessagePushPort.MessageType.REGISTRATION_REJECTED,
                userIds, title, content, extra, event.getRegistrationId());
    }

    /**
     * 分批发送站内信
     */
    private void sendSiteMessageBatch(String bizId, MessagePushPort.MessageType type,
                                       List<String> userIds, String title, String content,
                                       Map<String, Object> extra, Long registrationId) {
        int totalBatches = (userIds.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        int totalUsers = userIds.size();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userIds.subList(i, Math.min(i + MAX_BATCH_SIZE, userIds.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;

            try {
                messagePushPort.sendSiteMessage(bizId, type, batch, title, content, extra);
                successCount += batch.size();
                logger.info("报名{}站内信批次发送成功: registrationId={}, batch={}/{}, users={}",
                        type == MessagePushPort.MessageType.REGISTRATION_APPROVED ? "通过" : "拒绝",
                        registrationId, batchNum, totalBatches, batch.size());
            } catch (Exception e) {
                logger.error("报名{}站内信批次发送异常: registrationId={}, batch={}/{}",
                        type == MessagePushPort.MessageType.REGISTRATION_APPROVED ? "通过" : "拒绝",
                        registrationId, batchNum, totalBatches, e);
            }
        }

        logger.info("报名{}站内信发送完成: registrationId={}, totalUsers={}, successCount={}",
                type == MessagePushPort.MessageType.REGISTRATION_APPROVED ? "通过" : "拒绝",
                registrationId, totalUsers, successCount);
    }

}
