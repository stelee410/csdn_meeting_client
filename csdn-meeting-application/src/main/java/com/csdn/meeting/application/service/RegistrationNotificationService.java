package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationAuditedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.infrastructure.client.CsdnMessagePushClient;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageResponse;
import com.csdn.meeting.infrastructure.config.CsdnMessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报名审核通知服务
 * 监听报名审核事件，发送多渠道通知（IM站内信、APP Push、邮件）
 * 
 * 通知场景：
 * 1. 报名审核通过 → 发送报名成功通知
 * 2. 报名审核拒绝 → 发送报名拒绝通知
 */
@Service
public class RegistrationNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationNotificationService.class);

    private final MeetingRepository meetingRepository;
    private final CsdnMessagePushClient messagePushClient;
    private final CsdnMessageProperties messageProperties;

    public RegistrationNotificationService(MeetingRepository meetingRepository,
                                           CsdnMessagePushClient messagePushClient,
                                           CsdnMessageProperties messageProperties) {
        this.meetingRepository = meetingRepository;
        this.messagePushClient = messagePushClient;
        this.messageProperties = messageProperties;
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
     */
    private void sendApprovedNotification(RegistrationAuditedEvent event, 
                                          Meeting meeting, 
                                          List<String> userIds) {
        String meetingId = meeting.getMeetingId();
        String meetingTitle = meeting.getTitle();
        String startTime = meeting.getStartTime() != null ? 
                meeting.getStartTime().toString() : "";
        String venue = meeting.getVenue() != null ? meeting.getVenue() : "";

        // 准备模板变量
        Map<String, String> params = new HashMap<>();
        params.put("meetingTitle", meetingTitle);
        params.put("meetingId", meetingId);
        params.put("startTime", startTime);
        params.put("venue", venue);

        // 1. 发送IM站内信
        try {
            String imTemplate = messageProperties.getTemplates().getRegistrationApprovedIm();
            CsdnMessageResponse imResponse = messagePushClient.sendImMessage(
                    "registration_" + event.getRegistrationId(), 
                    imTemplate, userIds, params);
            
            if (imResponse.isSuccess()) {
                logger.info("报名通过IM通知发送成功: registrationId={}, userCount={}",
                        event.getRegistrationId(), userIds.size());
            } else {
                logger.warn("报名通过IM通知发送失败: registrationId={}, code={}, message={}",
                        event.getRegistrationId(), imResponse.getCode(), imResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("报名通过IM通知发送异常: registrationId={}", event.getRegistrationId(), e);
        }

        // 2. 发送APP Push
        try {
            String pushTemplate = messageProperties.getTemplates().getRegistrationApprovedPush();
            CsdnMessageResponse pushResponse = messagePushClient.sendPushNotification(
                    "registration_" + event.getRegistrationId(),
                    pushTemplate, userIds, params);
            
            if (pushResponse.isSuccess()) {
                logger.info("报名通过Push通知发送成功: registrationId={}, userCount={}",
                        event.getRegistrationId(), userIds.size());
            } else {
                logger.warn("报名通过Push通知发送失败: registrationId={}, code={}, message={}",
                        event.getRegistrationId(), pushResponse.getCode(), pushResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("报名通过Push通知发送异常: registrationId={}", event.getRegistrationId(), e);
        }

        // TODO: 3. 发送邮件通知（需接入邮件服务）
    }

    /**
     * 发送报名拒绝通知
     */
    private void sendRejectedNotification(RegistrationAuditedEvent event,
                                          Meeting meeting,
                                          List<String> userIds) {
        String meetingId = meeting.getMeetingId();
        String meetingTitle = meeting.getTitle();
        String auditRemark = event.getAuditRemark() != null ? event.getAuditRemark() : "";

        // 准备模板变量
        Map<String, String> params = new HashMap<>();
        params.put("meetingTitle", meetingTitle);
        params.put("meetingId", meetingId);
        params.put("rejectReason", auditRemark);

        // 1. 发送IM站内信
        try {
            String imTemplate = messageProperties.getTemplates().getRegistrationRejectedIm();
            CsdnMessageResponse imResponse = messagePushClient.sendImMessage(
                    "registration_" + event.getRegistrationId(),
                    imTemplate, userIds, params);

            if (imResponse.isSuccess()) {
                logger.info("报名拒绝IM通知发送成功: registrationId={}, userCount={}",
                        event.getRegistrationId(), userIds.size());
            } else {
                logger.warn("报名拒绝IM通知发送失败: registrationId={}, code={}, message={}",
                        event.getRegistrationId(), imResponse.getCode(), imResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("报名拒绝IM通知发送异常: registrationId={}", event.getRegistrationId(), e);
        }

        // 2. 发送APP Push
        try {
            String pushTemplate = messageProperties.getTemplates().getRegistrationRejectedPush();
            CsdnMessageResponse pushResponse = messagePushClient.sendPushNotification(
                    "registration_" + event.getRegistrationId(),
                    pushTemplate, userIds, params);

            if (pushResponse.isSuccess()) {
                logger.info("报名拒绝Push通知发送成功: registrationId={}, userCount={}",
                        event.getRegistrationId(), userIds.size());
            } else {
                logger.warn("报名拒绝Push通知发送失败: registrationId={}, code={}, message={}",
                        event.getRegistrationId(), pushResponse.getCode(), pushResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("报名拒绝Push通知发送异常: registrationId={}", event.getRegistrationId(), e);
        }

        // TODO: 3. 发送邮件通知（需接入邮件服务）
    }
}
