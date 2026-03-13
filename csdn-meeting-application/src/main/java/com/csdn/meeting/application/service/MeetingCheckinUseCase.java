package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.CheckinCommand;
import com.csdn.meeting.application.dto.CheckinQrDTO;
import com.csdn.meeting.application.dto.CheckinResultDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationCheckinEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * 会议签到服务
 * 处理签到码生成、扫码签到、签到状态查询等核心业务流程
 * 
 * 安全特性：
 * 1. 签到码使用HMAC-SHA256签名，防止伪造
 * 2. 二维码内容包含会议ID和加密Token
 * 3. 支持重复签到检测
 * 
 * TODO【需与CSDN协调】：
 * 1. 确认CSDN App扫码跳转Scheme（当前使用 app://csdn.meeting/checkin）
 * 2. 确认是否需要支持微信扫码跳转H5页面
 * 3. 确认签到二维码参数格式（meetingId、token等）
 * 4. 确认二维码过期策略（当前长期有效）
 * 5. 确认高安全级别会议是否需要动态刷新二维码（每5秒刷新）
 */
@Service
public class MeetingCheckinUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MeetingCheckinUseCase.class);

    // 签到码签名密钥（应从配置文件读取）
    @Value("${checkin.signing-key:csdn-meeting-checkin-secret-key}")
    private String signingKey;

    // 二维码Scheme前缀
    // TODO【需与CSDN协调】：与CSDN App团队确认Scheme协议
    @Value("${checkin.scheme:app://csdn.meeting/checkin}")
    private String checkinScheme;

    private final MeetingRepository meetingRepository;
    private final RegistrationRepository registrationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MeetingCheckinUseCase(MeetingRepository meetingRepository,
                                 RegistrationRepository registrationRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.meetingRepository = meetingRepository;
        this.registrationRepository = registrationRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 生成会议签到码
     * 主办方在会议后台生成签到二维码
     * 
     * @param meetingId 会议ID
     * @return 签到二维码数据
     */
    @Transactional
    public CheckinQrDTO generateCheckinCode(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 生成签到Token（使用HMAC-SHA256签名）
        String timestamp = String.valueOf(System.currentTimeMillis());
        String checkinToken = generateSignedToken(meetingId, timestamp);

        // 保存签到码到会议记录
        meeting.setCheckinCode(checkinToken);
        meeting.setRequireCheckin(true);
        meetingRepository.save(meeting);

        logger.info("生成会议 {} 签到码成功", meetingId);

        // 构建二维码数据
        CheckinQrDTO qrDTO = new CheckinQrDTO();
        qrDTO.setMeetingId(meetingId);
        qrDTO.setMeetingTitle(meeting.getTitle());
        qrDTO.setCheckinToken(checkinToken);
        qrDTO.setQrContent(buildQrContent(meetingId, checkinToken));
        qrDTO.setCheckinEnabled(true);

        return qrDTO;
    }

    /**
     * 获取签到二维码数据
     * 用于主办方展示签到二维码
     * 
     * @param meetingId 会议ID
     * @return 签到二维码数据
     */
    public CheckinQrDTO getCheckinQrData(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 检查是否已启用签到
        if (!Boolean.TRUE.equals(meeting.getRequireCheckin()) 
                || meeting.getCheckinCode() == null) {
            // 未启用签到，自动生成
            return generateCheckinCode(meetingId);
        }

        CheckinQrDTO qrDTO = new CheckinQrDTO();
        qrDTO.setMeetingId(meetingId);
        qrDTO.setMeetingTitle(meeting.getTitle());
        qrDTO.setCheckinToken(meeting.getCheckinCode());
        qrDTO.setQrContent(buildQrContent(meetingId, meeting.getCheckinCode()));
        qrDTO.setCheckinEnabled(true);

        return qrDTO;
    }

    /**
     * 执行签到
     * 参会者扫描二维码后调用此接口完成签到
     * 
     * @param command 签到命令
     * @return 签到结果
     */
    @Transactional
    public CheckinResultDTO checkin(CheckinCommand command) {
        String meetingId = command.getMeetingId();
        Long userId = command.getUserId();
        String providedToken = command.getCheckinToken();

        logger.info("用户 {} 尝试签到会议 {}", userId, meetingId);

        // 1. 校验会议有效性
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElse(null);
        if (meeting == null) {
            logger.warn("签到失败：会议不存在 {}", meetingId);
            return CheckinResultDTO.invalidCode(meetingId);
        }

        // 2. 校验签到码有效性
        if (meeting.getCheckinCode() == null) {
            logger.warn("签到失败：会议 {} 未启用签到", meetingId);
            return CheckinResultDTO.invalidCode(meetingId);
        }

        // 3. 校验Token（简单比较，生产环境可添加过期时间校验）
        if (!meeting.getCheckinCode().equals(providedToken)) {
            logger.warn("签到失败：签到码不匹配，会议 {} 用户 {}", meetingId, userId);
            return CheckinResultDTO.invalidCode(meetingId);
        }

        // 4. 查询用户报名记录
        Optional<Registration> regOpt = registrationRepository
                .findByUserIdAndMeetingId(userId, meeting.getId());
        
        if (!regOpt.isPresent()) {
            logger.warn("签到失败：用户 {} 未报名会议 {}", userId, meetingId);
            return CheckinResultDTO.notRegistered(meetingId, meeting.getTitle());
        }

        Registration registration = regOpt.get();

        // 5. 检查报名状态
        if (registration.getStatus() == Registration.RegistrationStatus.CANCELLED) {
            logger.warn("签到失败：用户 {} 报名已取消", userId);
            return CheckinResultDTO.notRegistered(meetingId, meeting.getTitle());
        }

        if (registration.getStatus() == Registration.RegistrationStatus.PENDING) {
            logger.warn("签到失败：用户 {} 报名待审核", userId);
            return CheckinResultDTO.notApproved(meetingId, meeting.getTitle());
        }

        if (registration.getStatus() == Registration.RegistrationStatus.REJECTED) {
            logger.warn("签到失败：用户 {} 报名被拒绝", userId);
            return CheckinResultDTO.notApproved(meetingId, meeting.getTitle());
        }

        // 6. 检查是否已签到
        if (registration.getStatus() == Registration.RegistrationStatus.CHECKED_IN) {
            logger.info("用户 {} 重复签到会议 {}", userId, meetingId);
            return CheckinResultDTO.duplicate(meetingId, meeting.getTitle(),
                    registration.getName(), registration.getCheckinTime());
        }

        // 7. 执行签到
        if (registration.getStatus() != Registration.RegistrationStatus.APPROVED) {
            logger.error("签到失败：意外的报名状态 {} 用户 {} 会议 {}", 
                    registration.getStatus(), userId, meetingId);
            return CheckinResultDTO.notApproved(meetingId, meeting.getTitle());
        }

        // 执行签到状态变更
        registration.checkin();
        registrationRepository.save(registration);

        // 8. 发布签到事件
        eventPublisher.publishEvent(new RegistrationCheckinEvent(
                registration.getId(),
                meeting.getId(),
                userId,
                meeting.getTitle(),
                registration.getName(),
                registration.getCheckinTime(),
                command.getCheckinMethod(),
                command.getDeviceInfo()
        ));

        logger.info("用户 {} 签到成功，会议 {} 时间 {}", 
                userId, meetingId, registration.getCheckinTime());

        // 返回成功结果
        return CheckinResultDTO.success(meetingId, meeting.getTitle(), 
                registration.getName(), registration.getCheckinTime());
    }

    /**
     * 查询签到状态
     * 
     * @param meetingId 会议ID
     * @param userId 用户ID
     * @return 签到状态
     */
    public CheckinStatusResult getCheckinStatus(String meetingId, Long userId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId).orElse(null);
        if (meeting == null) {
            return CheckinStatusResult.notFound(meetingId);
        }

        Optional<Registration> regOpt = registrationRepository
                .findByUserIdAndMeetingId(userId, meeting.getId());
        
        if (!regOpt.isPresent()) {
            return CheckinStatusResult.notRegistered(meetingId);
        }

        Registration registration = regOpt.get();
        
        // 返回签到状态
        boolean checkedIn = registration.getStatus() == Registration.RegistrationStatus.CHECKED_IN;
        return CheckinStatusResult.of(meetingId, registration.getStatus().name(), 
                checkedIn, registration.getCheckinTime());
    }

    /**
     * 生成带签名的签到Token
     * 使用HMAC-SHA256签名，包含会议ID和时间戳
     */
    private String generateSignedToken(String meetingId, String timestamp) {
        try {
            String data = meetingId + ":" + timestamp + ":" + UUID.randomUUID().toString().substring(0, 8);
            
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String base64Hash = Base64.getEncoder().encodeToString(hash);
            
            // 截取前32位作为Token
            return base64Hash.substring(0, Math.min(32, base64Hash.length()));
        } catch (Exception e) {
            logger.error("生成签到Token失败", e);
            // 降级方案：使用随机UUID
            return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
    }

    /**
     * 构建二维码内容
     */
    private String buildQrContent(String meetingId, String checkinToken) {
        return String.format("%s?m=%s&t=%s", checkinScheme, meetingId, checkinToken);
    }

    /**
     * 签到状态查询结果（内部类）
     */
    public static class CheckinStatusResult {
        private final String meetingId;
        private final String registrationStatus;
        private final boolean checkedIn;
        private final LocalDateTime checkinTime;
        private final boolean found;

        private CheckinStatusResult(String meetingId, String registrationStatus, 
                                    boolean checkedIn, LocalDateTime checkinTime, boolean found) {
            this.meetingId = meetingId;
            this.registrationStatus = registrationStatus;
            this.checkedIn = checkedIn;
            this.checkinTime = checkinTime;
            this.found = found;
        }

        public static CheckinStatusResult notFound(String meetingId) {
            return new CheckinStatusResult(meetingId, null, false, null, false);
        }

        public static CheckinStatusResult notRegistered(String meetingId) {
            return new CheckinStatusResult(meetingId, null, false, null, true);
        }

        public static CheckinStatusResult of(String meetingId, String status, 
                                             boolean checkedIn, LocalDateTime checkinTime) {
            return new CheckinStatusResult(meetingId, status, checkedIn, checkinTime, true);
        }

        // Getters
        public String getMeetingId() { return meetingId; }
        public String getRegistrationStatus() { return registrationStatus; }
        public boolean isCheckedIn() { return checkedIn; }
        public LocalDateTime getCheckinTime() { return checkinTime; }
        public boolean isFound() { return found; }
    }
}
