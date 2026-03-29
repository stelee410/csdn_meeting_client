package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.UserProfileDTO;
import com.csdn.meeting.application.dto.UserProfileUpdateCommand;
import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.service.UserDomainService;
import com.csdn.meeting.domain.service.VerificationCodeService;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户资料应用服务
 * 处理用户信息查询和更新
 */
@Slf4j
@Service
public class UserProfileAppService {

    private final UserDomainService userDomainService;
    private final VerificationCodeService verificationCodeService;

    public UserProfileAppService(UserDomainService userDomainService,
                                 VerificationCodeService verificationCodeService) {
        this.userDomainService = userDomainService;
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * 获取当前用户信息
     */
    public UserProfileDTO getCurrentUserProfile(String userId) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return convertToProfileDTO(user);
    }

    /**
     * 更新用户资料
     */
    @Transactional
    public UserProfileDTO updateProfile(String userId, UserProfileUpdateCommand command) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 如果需要更新邮箱，验证邮箱验证码
        if (command.getEmail() != null && !command.getEmail().isEmpty()) {
            if (!command.getEmail().equals(user.getEmail())) {
                if (command.getEmailCode() == null || command.getEmailCode().isEmpty()) {
                    throw new IllegalArgumentException("请填写邮箱验证码");
                }
                boolean verified = verificationCodeService.verifyCode(
                        command.getEmail(), command.getEmailCode(),
                        VerificationCodeType.EMAIL, VerificationCodeScene.REGISTER);
                if (!verified) {
                    throw new IllegalArgumentException("邮箱验证码错误或已过期");
                }
                userDomainService.updateEmail(user, command.getEmail());
            }
        }

        // 更新其他资料
        userDomainService.updateProfile(user, command.getNickname(), command.getAvatarUrl(),
                command.getRealName(), command.getCompany(), command.getJobTitle(),
                command.getIndustry());

        // 重新查询获取更新后的信息
        user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户更新失败"));

        log.info("用户[{}]资料更新成功", userId);

        return convertToProfileDTO(user);
    }

    /**
     * 转换为UserProfileDTO
     */
    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setMobile(maskMobile(user.getMobile()));
        dto.setUserType(user.getUserType() != null ? user.getUserType().name() : null);
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEmail(user.getEmail());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setRealName(user.getRealName());
        dto.setCompany(user.getCompany());
        dto.setJobTitle(user.getJobTitle());
        dto.setIndustry(user.getIndustry() != null ? user.getIndustry().getDisplayName() : null);
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setAgreementAccepted(user.getAgreementAccepted());
        dto.setPrivacyAccepted(user.getPrivacyAccepted());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }

    /**
     * 手机号脱敏
     */
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() != 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
}
