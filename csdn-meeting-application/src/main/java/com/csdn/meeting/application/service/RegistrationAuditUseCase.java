package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.event.RegistrationAuditedEvent;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报名审核 UseCase（agent.prd §2.9、§3.6）
 * 审核通过/拒绝，发布 RegistrationAuditedEvent
 */
@Service
public class RegistrationAuditUseCase {

    private final RegistrationRepository registrationRepository;
    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RegistrationAuditUseCase(RegistrationRepository registrationRepository,
                                    MeetingRepository meetingRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.registrationRepository = registrationRepository;
        this.meetingRepository = meetingRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 审核通过
     * 业务规则：仅 Meeting.status = PUBLISHED 时可审核
     */
    @Transactional
    public RegistrationDTO approve(Long regId) {
        Registration reg = registrationRepository.findById(regId)
                .orElseThrow(() -> new IllegalArgumentException("报名不存在: " + regId));
        Meeting meeting = meetingRepository.findById(reg.getMeetingId())
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + reg.getMeetingId()));
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("仅已发布状态的会议可进行报名审核，当前会议状态: " + meeting.getStatus());
        }
        reg.approve();
        registrationRepository.save(reg);
        eventPublisher.publishEvent(new RegistrationAuditedEvent(
                reg.getId(), reg.getMeetingId(), reg.getUserId(),
                reg.getStatus(), reg.getAuditedAt(), null));
        return toDTO(reg);
    }

    /**
     * 审核拒绝
     * 业务规则：仅 Meeting.status = PUBLISHED 时可审核；auditRemark 可选
     */
    @Transactional
    public RegistrationDTO reject(Long regId, String auditRemark) {
        Registration reg = registrationRepository.findById(regId)
                .orElseThrow(() -> new IllegalArgumentException("报名不存在: " + regId));
        Meeting meeting = meetingRepository.findById(reg.getMeetingId())
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + reg.getMeetingId()));
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("仅已发布状态的会议可进行报名审核，当前会议状态: " + meeting.getStatus());
        }
        reg.reject(auditRemark);
        registrationRepository.save(reg);
        eventPublisher.publishEvent(new RegistrationAuditedEvent(
                reg.getId(), reg.getMeetingId(), reg.getUserId(),
                reg.getStatus(), reg.getAuditedAt(), reg.getAuditRemark()));
        return toDTO(reg);
    }

    private RegistrationDTO toDTO(Registration reg) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(reg.getId());
        dto.setMeetingId(reg.getMeetingId());
        dto.setUserId(reg.getUserId());
        dto.setName(reg.getName());
        dto.setPhone(reg.getPhone());
        dto.setEmail(reg.getEmail());
        dto.setCompany(reg.getCompany());
        dto.setPosition(reg.getPosition());
        dto.setStatus(reg.getStatus() != null ? reg.getStatus().name() : null);
        dto.setRegisteredAt(reg.getRegisteredAt());
        dto.setAuditedAt(reg.getAuditedAt());
        dto.setAuditRemark(reg.getAuditRemark());
        return dto;
    }
}
