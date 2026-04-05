package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.CancelAccountCommand;
import com.csdn.meeting.application.dto.ChangeEmailCommand;
import com.csdn.meeting.application.dto.ChangePasswordCommand;
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
     * 修改用户密码
     *
     * @param userId  用户ID
     * @param command 修改密码命令
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordCommand command) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 验证原密码是否正确
        boolean oldPasswordValid = userDomainService.verifyPassword(user, command.getOldPassword());
        if (!oldPasswordValid) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 验证新密码与确认密码是否一致
        if (!command.getNewPassword().equals(command.getConfirmPassword())) {
            throw new IllegalArgumentException("新密码与确认密码不一致");
        }

        // 验证新密码不能与原密码相同
        if (command.getOldPassword().equals(command.getNewPassword())) {
            throw new IllegalArgumentException("新密码不能与原密码相同");
        }

        // 更新密码
        userDomainService.updatePassword(user, command.getNewPassword());
        userDomainService.persistUser(user);

        log.info("用户[{}]修改密码成功", userId);
    }

    /**
     * 更换用户邮箱
     *
     * @param userId  用户ID
     * @param command 更换邮箱命令
     * @return 更新后的用户资料
     */
    @Transactional
    public UserProfileDTO changeEmail(String userId, ChangeEmailCommand command) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 验证新邮箱是否已被其他用户使用
        if (!command.getNewEmail().equals(user.getEmail())) {
            userDomainService.findByEmail(command.getNewEmail()).ifPresent(existingUser -> {
                if (!existingUser.getUserId().equals(userId)) {
                    throw new IllegalArgumentException("该邮箱已被其他用户绑定");
                }
            });
        }

        // 验证新邮箱验证码
        boolean newEmailVerified = verificationCodeService.verifyCode(
                command.getNewEmail(), command.getNewEmailCode(),
                VerificationCodeType.EMAIL, VerificationCodeScene.CHANGE_EMAIL_NEW);
        if (!newEmailVerified) {
            throw new IllegalArgumentException("新邮箱验证码错误或已过期");
        }

        // 身份验证（原邮箱验证码 或 短信验证码）
        boolean identityVerified = false;

        // 优先使用原邮箱验证码验证
        if (command.getOldEmailCode() != null && !command.getOldEmailCode().isEmpty()) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new IllegalArgumentException("用户未绑定邮箱，无法使用原邮箱验证");
            }
            identityVerified = verificationCodeService.verifyCode(
                    user.getEmail(), command.getOldEmailCode(),
                    VerificationCodeType.EMAIL, VerificationCodeScene.CHANGE_EMAIL_OLD);
        }

        // 如果原邮箱验证失败或未提供，尝试短信验证码
        if (!identityVerified && command.getSmsCode() != null && !command.getSmsCode().isEmpty()) {
            identityVerified = verificationCodeService.verifyCode(
                    user.getMobile(), command.getSmsCode(),
                    VerificationCodeType.SMS, VerificationCodeScene.CHANGE_EMAIL_OLD);
        }

        if (!identityVerified) {
            throw new IllegalArgumentException("身份验证失败，请提供正确的原邮箱验证码或短信验证码");
        }

        // 更新邮箱
        userDomainService.updateEmail(user, command.getNewEmail());
        userDomainService.persistUser(user);

        log.info("用户[{}]更换邮箱成功: {} -> {}", userId, user.getEmail(), command.getNewEmail());

        return convertToProfileDTO(user);
    }

    /**
     * 注销用户账号
     *
     * @param userId  用户ID
     * @param command 注销账号命令
     */
    @Transactional
    public void cancelAccount(String userId, CancelAccountCommand command) {
        User user = userDomainService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查账号是否已注销
        if (user.isCancelled()) {
            throw new IllegalArgumentException("账号已注销");
        }

        // 身份验证（邮箱验证码 或 短信验证码）
        boolean identityVerified = false;

        // 优先使用邮箱验证码验证
        if (command.getEmailCode() != null && !command.getEmailCode().isEmpty()) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new IllegalArgumentException("用户未绑定邮箱，无法使用邮箱验证");
            }
            identityVerified = verificationCodeService.verifyCode(
                    user.getEmail(), command.getEmailCode(),
                    VerificationCodeType.EMAIL, VerificationCodeScene.CANCEL_ACCOUNT);
        }

        // 如果邮箱验证失败或未提供，尝试短信验证码
        if (!identityVerified && command.getSmsCode() != null && !command.getSmsCode().isEmpty()) {
            identityVerified = verificationCodeService.verifyCode(
                    user.getMobile(), command.getSmsCode(),
                    VerificationCodeType.SMS, VerificationCodeScene.CANCEL_ACCOUNT);
        }

        if (!identityVerified) {
            throw new IllegalArgumentException("身份验证失败，请提供正确的邮箱验证码或短信验证码");
        }

        // 执行注销
        userDomainService.cancelUser(userId);

        log.info("用户[{}]注销账号成功", userId);
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
