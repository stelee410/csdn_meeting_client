package com.csdn.meeting.application.service;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationAuditedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.infrastructure.client.CsdnMessagePushClient;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageResponse;
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
 * 监听报名审核事件，发送多渠道通知（IM站内信、APP Push、邮件）
 * 
 * 通知场景：
 * 1. 报名审核通过 → 发送报名成功通知
 * 2. 报名审核拒绝 → 发送报名拒绝通知
 */
@Service
public class RegistrationNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationNotificationService.class);

    /**
     * 每批最大用户数（CSDN消息中心限制）
     */
    private static final int MAX_BATCH_SIZE = 1000;

    private final MeetingRepository meetingRepository;
    private final CsdnMessagePushClient messagePushClient;

    public RegistrationNotificationService(MeetingRepository meetingRepository,
                                           CsdnMessagePushClient messagePushClient) {
        this.meetingRepository = meetingRepository;
        this.messagePushClient = messagePushClient;
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
     * 支持分批处理，每批最多1000人
     */
    private void sendApprovedNotification(RegistrationAuditedEvent event,
                                          Meeting meeting,
                                          List<String> userIds) {
        String meetingId = meeting.getMeetingId();
        String meetingName = meeting.getTitle();
        String startTime = meeting.getStartTime() != null ?
                meeting.getStartTime().toString() : "";
        String venue = meeting.getVenue() != null ? meeting.getVenue() : "";

        // 准备模板变量
        Map<String, String> params = new HashMap<>();
        params.put("meetingTitle", meetingName);
        params.put("meetingId", meetingId);
        params.put("startTime", startTime);
        params.put("venue", venue);

        String bizId = "registration_" + event.getRegistrationId();

        // 1. 发送IM站内信（分批处理，使用审核配置）
        sendVerifySuccessImBatch(bizId, userIds, params, event.getRegistrationId());

        // 2. 发送APP Push（分批处理，使用审核配置）
        sendVerifySuccessPushBatch(bizId, userIds, params, event.getRegistrationId());

        // TODO: 3. 发送邮件通知（需接入邮件服务）
    }

    /**
     * 分批发送审核通过IM站内信
     */
    private void sendVerifySuccessImBatch(String bizId, List<String> userIds,
                                          Map<String, String> params, Long registrationId) {
        int totalBatches = (userIds.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        int totalUsers = userIds.size();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userIds.subList(i, Math.min(i + MAX_BATCH_SIZE, userIds.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;

            try {
                CsdnMessageResponse response = messagePushClient.sendVerifySuccessIm(
                        bizId + "_batch" + batchNum, batch, params);

                if (response.isSuccess()) {
                    successCount += batch.size();
                    logger.info("报名通过IM通知批次发送成功: registrationId={}, batch={}/{}, users={}",
                            registrationId, batchNum, totalBatches, batch.size());
                } else {
                    logger.warn("报名通过IM通知批次发送失败: registrationId={}, batch={}/{}, code={}, message={}",
                            registrationId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("报名通过IM通知批次发送异常: registrationId={}, batch={}/{}",
                        registrationId, batchNum, totalBatches, e);
            }
        }

        logger.info("报名通过IM通知发送完成: registrationId={}, totalUsers={}, successCount={}",
                registrationId, totalUsers, successCount);
    }

    /**
     * 分批发送审核通过APP Push
     */
    private void sendVerifySuccessPushBatch(String bizId, List<String> userIds,
                                            Map<String, String> params, Long registrationId) {
        int totalBatches = (userIds.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        int totalUsers = userIds.size();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userIds.subList(i, Math.min(i + MAX_BATCH_SIZE, userIds.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;

            try {
                CsdnMessageResponse response = messagePushClient.sendVerifySuccessPush(
                        bizId + "_batch" + batchNum, batch, params);

                if (response.isSuccess()) {
                    successCount += batch.size();
                    logger.info("报名通过Push通知批次发送成功: registrationId={}, batch={}/{}, users={}",
                            registrationId, batchNum, totalBatches, batch.size());
                } else {
                    logger.warn("报名通过Push通知批次发送失败: registrationId={}, batch={}/{}, code={}, message={}",
                            registrationId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("报名通过Push通知批次发送异常: registrationId={}, batch={}/{}",
                        registrationId, batchNum, totalBatches, e);
            }
        }

        logger.info("报名通过Push通知发送完成: registrationId={}, totalUsers={}, successCount={}",
                registrationId, totalUsers, successCount);
    }

    /**
     * 发送报名拒绝通知
     * 支持分批处理，每批最多1000人
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

        String bizId = "registration_" + event.getRegistrationId();

        // 1. 发送IM站内信（分批处理，使用审核配置）
        sendVerifyFailureImBatch(bizId, userIds, params, event.getRegistrationId());

        // 2. 发送APP Push（分批处理，使用审核配置）
        sendVerifyFailurePushBatch(bizId, userIds, params, event.getRegistrationId());

        // TODO: 3. 发送邮件通知（需接入邮件服务）
    }

    /**
     * 分批发送审核拒绝IM站内信
     */
    private void sendVerifyFailureImBatch(String bizId, List<String> userIds,
                                          Map<String, String> params, Long registrationId) {
        int totalBatches = (userIds.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        int totalUsers = userIds.size();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userIds.subList(i, Math.min(i + MAX_BATCH_SIZE, userIds.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;

            try {
                CsdnMessageResponse response = messagePushClient.sendVerifyFailureIm(
                        bizId + "_batch" + batchNum, batch, params);

                if (response.isSuccess()) {
                    successCount += batch.size();
                    logger.info("报名拒绝IM通知批次发送成功: registrationId={}, batch={}/{}, users={}",
                            registrationId, batchNum, totalBatches, batch.size());
                } else {
                    logger.warn("报名拒绝IM通知批次发送失败: registrationId={}, batch={}/{}, code={}, message={}",
                            registrationId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("报名拒绝IM通知批次发送异常: registrationId={}, batch={}/{}",
                        registrationId, batchNum, totalBatches, e);
            }
        }

        logger.info("报名拒绝IM通知发送完成: registrationId={}, totalUsers={}, successCount={}",
                registrationId, totalUsers, successCount);
    }

    /**
     * 分批发送审核拒绝APP Push
     */
    private void sendVerifyFailurePushBatch(String bizId, List<String> userIds,
                                            Map<String, String> params, Long registrationId) {
        int totalBatches = (userIds.size() + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
        int totalUsers = userIds.size();
        int successCount = 0;

        for (int i = 0; i < userIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch = userIds.subList(i, Math.min(i + MAX_BATCH_SIZE, userIds.size()));
            int batchNum = i / MAX_BATCH_SIZE + 1;

            try {
                CsdnMessageResponse response = messagePushClient.sendVerifyFailurePush(
                        bizId + "_batch" + batchNum, batch, params);

                if (response.isSuccess()) {
                    successCount += batch.size();
                    logger.info("报名拒绝Push通知批次发送成功: registrationId={}, batch={}/{}, users={}",
                            registrationId, batchNum, totalBatches, batch.size());
                } else {
                    logger.warn("报名拒绝Push通知批次发送失败: registrationId={}, batch={}/{}, code={}, message={}",
                            registrationId, batchNum, totalBatches, response.getCode(), response.getMessage());
                }
            } catch (Exception e) {
                logger.error("报名拒绝Push通知批次发送异常: registrationId={}, batch={}/{}",
                        registrationId, batchNum, totalBatches, e);
            }
        }

        logger.info("报名拒绝Push通知发送完成: registrationId={}, totalUsers={}, successCount={}",
                registrationId, totalUsers, successCount);
    }
}
