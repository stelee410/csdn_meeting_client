package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.FormFieldConfigDTO;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.MeetingDetailDTO;
import com.csdn.meeting.application.dto.MeetingDetailPageDTO;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.dto.RegistrationStatusDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.domain.repository.MeetingFavoriteRepository;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 会议详情页服务
 * 组装会议详情页所需的完整数据，包括会议信息、报名状态、收藏状态等
 */
@Service
public class MeetingDetailPageUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MeetingDetailPageUseCase.class);

    private final MeetingRepository meetingRepository;
    private final RegistrationRepository registrationRepository;
    private final MeetingFavoriteRepository favoriteRepository;
    private final RegistrationConfigUseCase configUseCase;
    private final MeetingRegistrationUseCase registrationUseCase;
    private final MeetingApplicationService meetingApplicationService;

    public MeetingDetailPageUseCase(MeetingRepository meetingRepository,
                                    RegistrationRepository registrationRepository,
                                    MeetingFavoriteRepository favoriteRepository,
                                    RegistrationConfigUseCase configUseCase,
                                    MeetingRegistrationUseCase registrationUseCase,
                                    MeetingApplicationService meetingApplicationService) {
        this.meetingRepository = meetingRepository;
        this.registrationRepository = registrationRepository;
        this.favoriteRepository = favoriteRepository;
        this.configUseCase = configUseCase;
        this.registrationUseCase = registrationUseCase;
        this.meetingApplicationService = meetingApplicationService;
    }

    /**
     * 获取会议详情页数据
     * 
     * @param meetingId 会议ID
     * @param userId 用户ID（可为null，未登录时）
     * @return 会议详情页DTO
     */
    public MeetingDetailPageDTO getMeetingDetailPage(String meetingId, Long userId) {
        logger.debug("获取会议详情页: meetingId={}, userId={}", meetingId, userId);

        // 1. 获取会议基础信息
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        MeetingDTO meetingDTO = meetingApplicationService.getMeetingDetailById(meeting.getId());
        MeetingDetailDTO detailDTO = convertToDetailDTO(meetingDTO, meeting);

        // 2. 组装详情页数据
        MeetingDetailPageDTO pageDTO = new MeetingDetailPageDTO();
        pageDTO.setMeeting(detailDTO);

        // 3. 获取用户报名状态（如果用户已登录）
        if (userId != null) {
            MeetingDetailPageDTO.MyRegistrationStatusDTO regStatus = getMyRegistrationStatus(meeting, userId);
            pageDTO.setMyRegistration(regStatus);

            // 查询收藏状态
            boolean isFavorited = favoriteRepository.existsByUserIdAndMeetingId(userId, meeting.getId());
            pageDTO.setIsFavorite(isFavorited);
        } else {
            pageDTO.setMyRegistration(null);
            pageDTO.setIsFavorite(false);
        }

        // 4. 计算按钮状态
        MeetingDetailPageDTO.ButtonStateDTO buttonState = calculateButtonState(meeting, pageDTO.getMyRegistration(), userId);
        pageDTO.setButtonState(buttonState);

        // 5. 获取报名表单配置
        List<FormFieldConfigDTO> formConfig = configUseCase.getConfig(meetingId);
        pageDTO.setFormConfig(formConfig);

        return pageDTO;
    }

    /**
     * 获取会议报名状态
     */
    public RegistrationStatusDTO getRegistrationStatus(String meetingId) {
        return registrationUseCase.checkRegistrationStatus(meetingId);
    }

    /**
     * 获取我的报名状态
     */
    private MeetingDetailPageDTO.MyRegistrationStatusDTO getMyRegistrationStatus(Meeting meeting, Long userId) {
        Optional<Registration> regOpt = registrationRepository
                .findByUserIdAndMeetingId(userId, meeting.getId());

        MeetingDetailPageDTO.MyRegistrationStatusDTO statusDTO = new MeetingDetailPageDTO.MyRegistrationStatusDTO();

        if (!regOpt.isPresent()) {
            statusDTO.setStatus("NOT_REGISTERED");
            statusDTO.setStatusName("未报名");
            statusDTO.setCanCancel(false);
            statusDTO.setCanCheckin(false);
            return statusDTO;
        }

        Registration registration = regOpt.get();
        statusDTO.setRegistrationId(registration.getId());
        statusDTO.setStatus(registration.getStatus().name());
        statusDTO.setStatusName(registration.getStatus().getDisplayName());
        statusDTO.setRegisteredAt(registration.getRegisteredAt());
        statusDTO.setCheckinAt(registration.getCheckinTime());
        statusDTO.setAuditRemark(registration.getAuditRemark());
        statusDTO.setCanCancel(registration.canCancel());
        statusDTO.setCanCheckin(registration.canCheckin());

        return statusDTO;
    }

    /**
     * 计算底部按钮状态
     */
    private MeetingDetailPageDTO.ButtonStateDTO calculateButtonState(
            Meeting meeting,
            MeetingDetailPageDTO.MyRegistrationStatusDTO myReg,
            Long userId) {

        MeetingDetailPageDTO.ButtonStateDTO button = new MeetingDetailPageDTO.ButtonStateDTO();

        // 未登录
        if (userId == null) {
            button.setType("LOGIN_REQUIRED");
            button.setText("立即报名");
            button.setEnabled(true);
            button.setAction("LOGIN");
            button.setTip("请登录后报名");
            return button;
        }

        // 会议状态判断
        Meeting.MeetingStatus meetingStatus = meeting.getStatus();

        // 会议未发布
        if (meetingStatus != Meeting.MeetingStatus.PUBLISHED) {
            button.setType("CLOSED");
            button.setText(getStatusButtonText(meetingStatus));
            button.setEnabled(false);
            button.setAction("NONE");
            button.setTip("会议未开放报名");
            return button;
        }

        // 报名截止时间判断
        if (meeting.getRegEndTime() != null && LocalDateTime.now().isAfter(meeting.getRegEndTime())) {
            button.setType("CLOSED");
            button.setText("报名已结束");
            button.setEnabled(false);
            button.setAction("NONE");
            button.setTip("已超过报名截止时间");
            return button;
        }

        // 名额判断
        if (meeting.getMaxParticipants() != null && meeting.getMaxParticipants() > 0) {
            int remaining = meeting.getMaxParticipants() - meeting.getCurrentParticipants();
            button.setRemainingSpots(remaining);
            if (remaining <= 0) {
                button.setType("FULL");
                button.setText("名额已满");
                button.setEnabled(false);
                button.setAction("NONE");
                button.setTip("报名人数已满");
                return button;
            }
        } else {
            button.setRemainingSpots(-1); // 不限名额
        }

        // 用户报名状态判断
        if (myReg == null || "NOT_REGISTERED".equals(myReg.getStatus())) {
            // 未报名
            button.setType("REGISTER");
            button.setText("立即报名");
            button.setEnabled(true);
            button.setAction("REGISTER");
            button.setTip("点击填写报名信息");
        } else {
            switch (myReg.getStatus()) {
                case "PENDING":
                    button.setType("ALREADY_REGISTERED");
                    button.setText("审核中");
                    button.setEnabled(false);
                    button.setAction("NONE");
                    button.setTip("您的报名正在审核中");
                    break;
                case "APPROVED":
                    button.setType("ALREADY_REGISTERED");
                    button.setText("已报名");
                    button.setEnabled(true);
                    button.setAction("VIEW_TICKET");
                    button.setTip("您已成功报名，点击查看电子票");
                    break;
                case "REJECTED":
                    button.setType("CLOSED");
                    button.setText("报名未通过");
                    button.setEnabled(false);
                    button.setAction("NONE");
                    button.setTip("您的报名未通过审核");
                    break;
                case "CANCELLED":
                    button.setType("REGISTER");
                    button.setText("重新报名");
                    button.setEnabled(true);
                    button.setAction("REGISTER");
                    button.setTip("您的报名已取消，可重新报名");
                    break;
                case "CHECKED_IN":
                    button.setType("ALREADY_REGISTERED");
                    button.setText("已签到");
                    button.setEnabled(true);
                    button.setAction("VIEW_TICKET");
                    button.setTip("您已完成签到");
                    break;
                default:
                    button.setType("REGISTER");
                    button.setText("立即报名");
                    button.setEnabled(true);
                    button.setAction("REGISTER");
            }
        }

        return button;
    }

    /**
     * 根据会议状态获取按钮文案
     */
    private String getStatusButtonText(Meeting.MeetingStatus status) {
        switch (status) {
            case DRAFT:
                return "即将开始";
            case PENDING_REVIEW:
                return "即将开始";
            case REJECTED:
                return "报名未开放";
            case OFFLINE:
                return "报名已关闭";
            case ENDED:
                return "会议已结束";
            case IN_PROGRESS:
                return "进行中";
            default:
                return "报名未开放";
        }
    }

    /**
     * 将MeetingDTO转换为MeetingDetailDTO
     * 简化版本，只映射实际存在的字段
     */
    private MeetingDetailDTO convertToDetailDTO(MeetingDTO meetingDTO, Meeting meeting) {
        MeetingDetailDTO detailDTO = new MeetingDetailDTO();
        detailDTO.setMeetingId(meetingDTO.getMeetingId());
        detailDTO.setTitle(meetingDTO.getTitle());
        detailDTO.setDescription(meetingDTO.getDescription());
        detailDTO.setPosterUrl(meetingDTO.getPosterUrl());
        detailDTO.setStartTime(meetingDTO.getStartTime());
        detailDTO.setEndTime(meetingDTO.getEndTime());
        detailDTO.setVenue(meetingDTO.getVenue());
        String organizerLabel = meetingDTO.getOrganizerName();
        if (organizerLabel == null || organizerLabel.trim().isEmpty()) {
            organizerLabel = meetingDTO.getOrganizer();
        }
        detailDTO.setOrganizerName(organizerLabel);
        detailDTO.setContactName(meetingDTO.getCreatorName());
        detailDTO.setContactPhone(meetingDTO.getContactPhone());
        detailDTO.setContactDepartment(meetingDTO.getContactDepartment());
        detailDTO.setContactPosition(meetingDTO.getContactPosition());
        detailDTO.setTagIds(meetingDTO.getTagIds());
        detailDTO.setStatus(1); // 默认值
        detailDTO.setStatusName(meetingDTO.getStatus());
        detailDTO.setMaxParticipants(meetingDTO.getMaxParticipants());
        detailDTO.setScheduleDays(meetingDTO.getScheduleDays());

        // 设置报名人数信息（从Meeting实体获取更准确的数据）
        Integer currentParticipants = meeting.getCurrentParticipants() != null ? meeting.getCurrentParticipants() : 0;
        Integer maxParticipants = meeting.getMaxParticipants();
        detailDTO.setCurrentParticipants(currentParticipants);

        // 计算报名进度百分比（用于移动端展示进度条）
        if (maxParticipants != null && maxParticipants > 0) {
            int progress = (int) ((double) currentParticipants / maxParticipants * 100);
            detailDTO.setParticipantsProgress(Math.min(progress, 100)); // 最大100%
            detailDTO.setParticipantsDisplay(currentParticipants + " / " + maxParticipants + " 人");
        } else {
            detailDTO.setParticipantsProgress(0);
            detailDTO.setParticipantsDisplay(currentParticipants + " 人");
        }

        // 设置默认值
        detailDTO.setHotScore(0);
        return detailDTO;
    }
}
