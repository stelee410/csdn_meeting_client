package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.UpdateUserProfileCommand;
import com.csdn.meeting.application.dto.UserProfileDTO;
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
 * 处理用户资料查询、更新等业务
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
     * 获取用户完整资料
     *
     * @param userId 用户ID
     * @return 用户资料DTO
     */
    public UserProfileDTO getUserProfile(String userId) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        return convertToProfileDTO(user);
    }

    /**
     * 更新用户资料
     *
     * @param userId  用户ID
     * @param command 更新命令
     * @return 更新后的用户资料
     */
    @Transactional
    public UserProfileDTO updateUserProfile(String userId, UpdateUserProfileCommand command) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 验证并更新邮箱（如果填写了邮箱）
        if (command.getEmail() != null && !command.getEmail().isEmpty()) {
            if (!command.getEmail().equals(user.getEmail())) {
                // 邮箱变更，需要验证
                if (command.getEmailCode() == null || command.getEmailCode().isEmpty()) {
                    throw new IllegalArgumentException("修改邮箱需要填写验证码");
                }
                boolean verified = verificationCodeService.verifyCode(
                        command.getEmail(), command.getEmailCode(),
                        VerificationCodeType.EMAIL, VerificationCodeScene.PROFILE_UPDATE);
                if (!verified) {
                    throw new IllegalArgumentException("邮箱验证码错误或已过期");
                }
                userDomainService.updateEmail(user, command.getEmail());
            }
        }

        // 更新用户资料
        userDomainService.updateProfile(
                user,
                command.getNickname(),
                command.getAvatarUrl(),
                command.getRealName(),
                command.getCompany(),
                command.getJobTitle(),
                command.getIndustry()
        );

        // 保存用户 - 显式调用保存确保更新生效
        userDomainService.persistUser(user);

        log.info("用户[{}]更新资料成功", userId);

        // 返回更新后的资料
        if (Boolean.TRUE.equals(command.getReturnFullProfile())) {
            return convertToProfileDTO(user);
        }
        return null;
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
//        if (mobile == null || mobile.length() != 11) {
//            return mobile;
//        }
//        return mobile.substring(0, 3) + "****" + mobile.substring(7);
        return mobile;
    }
}
