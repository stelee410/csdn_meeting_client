package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.RegistrationCommand;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.dto.RegistrationStatusDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationSubmittedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 会议报名服务
 * 处理用户报名、取消报名、查询报名状态等核心业务流程
 * 
 * 关键特性：
 * 1. 防超卖：使用内存锁控制并发报名
 * 2. 预填表单：从用户画像自动填充信息
 * 3. 重复报名拦截
 * 4. 名额已满检查
 */
@Service
public class MeetingRegistrationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MeetingRegistrationUseCase.class);

    // 会议报名锁（内存级，简单场景使用；生产环境建议使用Redis分布式锁）
    private final Map<String, ReentrantLock> meetingLocks = new ConcurrentHashMap<>();

    private final RegistrationRepository registrationRepository;
    private final MeetingRepository meetingRepository;
    private final RegistrationConfigUseCase configUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public MeetingRegistrationUseCase(RegistrationRepository registrationRepository,
                                      MeetingRepository meetingRepository,
                                      RegistrationConfigUseCase configUseCase,
                                      ApplicationEventPublisher eventPublisher) {
        this.registrationRepository = registrationRepository;
        this.meetingRepository = meetingRepository;
        this.configUseCase = configUseCase;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 提交报名
     * 核心业务逻辑：校验 -> 锁控制 -> 双重检查 -> 创建记录 -> 发布事件
     * 
     * @param command 报名命令
     * @return 报名结果DTO
     */
    @Transactional
    public RegistrationDTO register(RegistrationCommand command) {
        String meetingId = command.getMeetingId();
        Long userId = command.getUserId();
        Map<String, String> formData = command.getFormData();

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空，请先登录");
        }

        logger.info("用户 {} 申请报名会议 {}", userId, meetingId);

        // 1. 基础校验
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 检查会议状态
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("会议未发布，不可报名，当前状态: " + meeting.getStatus());
        }

        // 检查报名截止时间
        if (meeting.getRegEndTime() != null && LocalDateTime.now().isAfter(meeting.getRegEndTime())) {
            throw new IllegalStateException("报名已截止");
        }

        // 2. 按用户ID判重：同一用户同一会议仅允许一条有效报名
        Optional<Registration> existingReg = registrationRepository
                .findByUserIdAndMeetingId(userId, meeting.getId());
        if (existingReg.isPresent() && existingReg.get().isValid()) {
            logger.warn("用户 {} 已报名会议 {}，状态: {}", userId, meetingId, existingReg.get().getStatus());
            throw new IllegalStateException("您已报名该会议，请勿重复报名");
        }

        // 3. 校验表单数据
        validateFormData(meetingId, formData);

        // 4. 并发控制（内存锁）
        ReentrantLock lock = meetingLocks.computeIfAbsent(meetingId, k -> new ReentrantLock());
        lock.lock();
        try {
            // 双重检查：名额是否已满
            Meeting freshMeeting = meetingRepository.findByMeetingId(meetingId)
                    .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
            
            if (freshMeeting.getMaxParticipants() != null 
                    && freshMeeting.getMaxParticipants() > 0
                    && freshMeeting.getCurrentParticipants() >= freshMeeting.getMaxParticipants()) {
                logger.warn("会议 {} 名额已满", meetingId);
                throw new IllegalStateException("名额已满");
            }

            // 5. 创建报名记录
            Registration registration = new Registration();
            registration.setMeetingId(freshMeeting.getId());
            registration.setUserId(userId);
            registration.setName(formData.get("name"));
            String phoneRaw = formData != null ? formData.get("phone") : null;
            String phoneNormalized = phoneRaw != null ? phoneRaw.trim() : null;
            registration.setPhone(phoneNormalized != null ? phoneNormalized : formData.get("phone"));
            registration.setEmail(formData.get("email"));
            registration.setCompany(formData.get("company"));
            registration.setPosition(formData.get("position"));
            registration.setStatus(Registration.RegistrationStatus.PENDING);
            registration.setRegisteredAt(LocalDateTime.now());

            // 保存报名记录
            registrationRepository.save(registration);

            // 6. 增加会议报名人数
            freshMeeting.incrementParticipants();
            meetingRepository.save(freshMeeting);

            // 7. 发布报名提交事件
            eventPublisher.publishEvent(new RegistrationSubmittedEvent(
                    registration.getId(),
                    freshMeeting.getId(),
                    userId,
                    freshMeeting.getTitle(),
                    Registration.RegistrationStatus.PENDING,
                    registration.getRegisteredAt(),
                    formData
            ));

            logger.info("用户 {} 成功提交会议 {} 报名，报名ID: {}", 
                    userId, meetingId, registration.getId());

            return toDTO(registration);

        } finally {
            lock.unlock();
        }
    }

    /**
     * 取消报名
     * 业务规则：只有PENDING或APPROVED状态可取消，CHECKED_IN后不可取消
     * 
     * @param registrationId 报名记录ID
     * @param userId 用户ID（用于权限校验）
     */
    @Transactional
    public void cancelRegistration(Long registrationId, Long userId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("报名记录不存在: " + registrationId));

        // 权限校验
        if (!registration.getUserId().equals(userId)) {
            throw new IllegalStateException("无权操作他人的报名记录");
        }

        // 执行取消
        registration.cancel();
        registrationRepository.save(registration);

        // 减少会议报名人数
        Meeting meeting = meetingRepository.findById(registration.getMeetingId())
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.decrementParticipants();
        meetingRepository.save(meeting);

        logger.info("用户 {} 取消报名 {}, 会议: {}", userId, registrationId, meeting.getMeetingId());
    }

    /**
     * 查询我的报名状态
     * 
     * @param meetingId 会议ID
     * @param userId 用户ID
     * @return 报名DTO，未报名返回null
     */
    public RegistrationDTO getMyRegistration(String meetingId, Long userId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId).orElse(null);
        if (meeting == null) {
            return null;
        }

        return registrationRepository.findByUserIdAndMeetingId(userId, meeting.getId())
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * 获取预填表单数据
     * 从用户画像中读取可预填字段的值
     * 
     * @param meetingId 会议ID
     * @param userId 用户ID
     * @return 预填字段Map
     */
    public Map<String, String> getPreFilledForm(String meetingId, Long userId) {
        List<String> preFillableFields = configUseCase.getPreFillableFields(meetingId);
        Map<String, String> preFilledData = new HashMap<>();

        // 从用户服务获取用户画像（简化实现，实际应从用户服务查询）
        // 这里模拟从用户画像获取数据
        for (String field : preFillableFields) {
            String value = getUserProfileField(userId, field);
            if (value != null && !value.isEmpty()) {
                preFilledData.put(field, value);
            }
        }

        logger.debug("用户 {} 预填字段: {}", userId, preFilledData.keySet());
        return preFilledData;
    }

    /**
     * 检查报名状态（供会议详情页使用）
     * 
     * @param meetingId 会议ID
     * @return 报名状态信息
     */
    public RegistrationStatusDTO checkRegistrationStatus(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        RegistrationStatusDTO status = new RegistrationStatusDTO();
        status.setMeetingId(meetingId);
        status.setCurrentParticipants(meeting.getCurrentParticipants());
        status.setMaxParticipants(meeting.getMaxParticipants());
        status.setRegEndTime(meeting.getRegEndTime());
        status.setRequireCheckin(meeting.getRequireCheckin());

        // 计算剩余名额
        if (meeting.getMaxParticipants() != null && meeting.getMaxParticipants() > 0) {
            int remaining = meeting.getMaxParticipants() - meeting.getCurrentParticipants();
            status.setRemainingSpots(Math.max(0, remaining));
            status.setFull(remaining <= 0);
        } else {
            status.setRemainingSpots(-1); // 不限名额
            status.setFull(false);
        }

        // 判断报名是否开放
        boolean registrationOpen = meeting.getStatus() == Meeting.MeetingStatus.PUBLISHED
                && (meeting.getRegEndTime() == null || LocalDateTime.now().isBefore(meeting.getRegEndTime()))
                && !status.isFull();
        status.setRegistrationOpen(registrationOpen);

        return status;
    }

    /**
     * 验证表单数据
     */
    private void validateFormData(String meetingId, Map<String, String> formData) {
        if (formData == null) {
            throw new IllegalArgumentException("表单数据不能为空");
        }

        List<String> requiredFields = configUseCase.getRequiredFields(meetingId);
        
        for (String field : requiredFields) {
            String value = formData.get(field);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("必填字段不能为空: " + field);
            }
        }

        // 手机号格式校验
        String phone = formData.get("phone");
        if (phone != null && !phone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 邮箱格式校验
        String email = formData.get("email");
        if (email != null && !email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    /**
     * 从用户画像获取字段值（简化实现）
     * 实际应从用户服务查询
     * 
     * TODO【需与CSDN协调】：
     * 1. 确认CSDN用户服务接口地址和调用方式
     * 2. 确认用户画像字段映射关系（姓名、手机号、邮箱、公司、职位）
     * 3. 确认接口鉴权方式（Token/AppKey）
     * 4. 确认是否需要用户授权才能获取画像数据
     */
    private String getUserProfileField(Long userId, String field) {
        // TODO: 接入CSDN用户服务获取真实数据
        // 这里返回模拟数据，实际项目中应从用户服务查询
        Map<String, String> mockProfile = new HashMap<>();
        mockProfile.put("name", "用户" + userId);
        mockProfile.put("phone", "138****8888");
        mockProfile.put("email", "user" + userId + "@csdn.net");
        
        return mockProfile.get(field);
    }

    /**
     * 手机号脱敏显示
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 转换为DTO
     */
    private RegistrationDTO toDTO(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setMeetingId(registration.getMeetingId());
        dto.setUserId(registration.getUserId());
        dto.setName(registration.getName());
        dto.setPhone(maskPhone(registration.getPhone()));
        dto.setEmail(registration.getEmail());
        dto.setCompany(registration.getCompany());
        dto.setPosition(registration.getPosition());
        dto.setStatus(registration.getStatus() != null ? registration.getStatus().name() : null);
        dto.setRegisteredAt(registration.getRegisteredAt());
        dto.setAuditedAt(registration.getAuditedAt());
        dto.setAuditRemark(registration.getAuditRemark());
        // V1.2新增字段
        dto.setCheckinTime(registration.getCheckinTime());
        dto.setCancelTime(registration.getCancelTime());
        return dto;
    }
}
