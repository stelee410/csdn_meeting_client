package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.service.PasswordService;
import com.csdn.meeting.domain.service.UserDomainService;
import com.csdn.meeting.domain.service.VerificationCodeService;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import com.csdn.meeting.infrastructure.external.CsdnAuthClient;
import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户认证应用服务
 * 处理注册、登录、登出等操作
 */
@Slf4j
@Service
public class UserAuthAppService {

    private final UserDomainService userDomainService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CsdnAuthClient csdnAuthClient;
    private final CsdnQrCodeUseCase csdnQrCodeUseCase;

    public UserAuthAppService(UserDomainService userDomainService,
                              VerificationCodeService verificationCodeService,
                              PasswordService passwordService,
                              JwtTokenProvider jwtTokenProvider,
                              CsdnAuthClient csdnAuthClient,
                              CsdnQrCodeUseCase csdnQrCodeUseCase) {
        this.userDomainService = userDomainService;
        this.verificationCodeService = verificationCodeService;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.csdnAuthClient = csdnAuthClient;
        this.csdnQrCodeUseCase = csdnQrCodeUseCase;
    }

    /**
     * 表单注册
     */
    @Transactional
    public LoginResultDTO register(UserRegisterCommand command) {
        // 1. 验证密码一致性
        if (!command.getPassword().equals(command.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        // 2. 验证密码强度
        if (!passwordService.isStrongPassword(command.getPassword())) {
            throw new IllegalArgumentException("密码长度至少8位，且包含字母和数字");
        }

        // 3. 验证协议同意
        if (!Boolean.TRUE.equals(command.getAgreementAccepted()) || !Boolean.TRUE.equals(command.getPrivacyAccepted())) {
            throw new IllegalArgumentException("请阅读并同意《用户协议》和《隐私政策》");
        }

        // 4. 验证短信验证码
        boolean smsVerified = verificationCodeService.verifyCode(
                command.getMobile(), command.getSmsCode(), VerificationCodeType.SMS, VerificationCodeScene.REGISTER);
        if (!smsVerified) {
            throw new IllegalArgumentException("短信验证码错误或已过期");
        }

        // 5. 检查手机号是否已被非注销用户占用（允许注销后的手机号重新注册）
        if (userDomainService.isMobileActive(command.getMobile())) {
            throw new IllegalArgumentException("该手机号已注册，请直接登录");
        }

        // 6. 创建用户
        User user = userDomainService.createNormalUser(
                command.getMobile(), command.getPassword(), command.getNickname(), command.getAvatarUrl());
        user.acceptAgreements();

        // 7. 处理邮箱（如果有）
        if (command.getEmail() != null && !command.getEmail().isEmpty()) {
            if (command.getEmailCode() == null || command.getEmailCode().isEmpty()) {
                throw new IllegalArgumentException("请填写邮箱验证码");
            }
            boolean emailVerified = verificationCodeService.verifyCode(
                    command.getEmail(), command.getEmailCode(), VerificationCodeType.EMAIL, VerificationCodeScene.REGISTER);
            if (!emailVerified) {
                throw new IllegalArgumentException("邮箱验证码错误或已过期");
            }
            userDomainService.updateEmail(user, command.getEmail());
        }

        user = userDomainService.persistUser(user);

        // 8. 生成JWT
        String token = jwtTokenProvider.generateToken(user.getUserId());

        log.info("用户[{}]注册成功", user.getUserId());

        return buildLoginResult(user, token);
    }

    /**
     * 密码登录
     */
    public LoginResultDTO loginByPassword(UserLoginPasswordCommand command) {
        // 1. 查找用户
        User user = userDomainService.findByMobile(command.getMobile())
                .orElseThrow(() -> new IllegalArgumentException("手机号未注册"));

        // 2. 验证账号状态
        if (!userDomainService.canLogin(user)) {
            throw new IllegalArgumentException("账号已被冻结，请联系客服");
        }

        // 3. 验证密码
        if (!userDomainService.verifyPassword(user, command.getPassword())) {
            throw new IllegalArgumentException("手机号或密码错误");
        }

        // 4. 更新登录时间
        userDomainService.onLoginSuccess(user);

        // 5. 生成JWT
        String token = jwtTokenProvider.generateToken(user.getUserId());

        log.info("用户[{}]密码登录成功", user.getUserId());

        return buildLoginResult(user, token);
    }

    /**
     * 短信验证码登录
     */
    public LoginResultDTO loginBySms(UserLoginSmsCommand command) {
        // 1. 验证短信验证码
        boolean verified = verificationCodeService.verifyCode(
                command.getMobile(), command.getSmsCode(), VerificationCodeType.SMS, VerificationCodeScene.LOGIN);
        if (!verified) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }

        // 2. 查找用户
        User user = userDomainService.findByMobile(command.getMobile())
                .orElseThrow(() -> new IllegalArgumentException("手机号未注册"));

        // 3. 验证账号状态
        if (!userDomainService.canLogin(user)) {
            throw new IllegalArgumentException("账号已被冻结，请联系客服");
        }

        // 4. 更新登录时间
        userDomainService.onLoginSuccess(user);

        // 5. 生成JWT
        String token = jwtTokenProvider.generateToken(user.getUserId());

        log.info("用户[{}]短信登录成功", user.getUserId());

        return buildLoginResult(user, token);
    }

    /**
     * CSDN扫码回调处理
     * 用户通过CSDN App扫码授权后调用此接口
     * 已绑定用户直接登录，未绑定用户自动创建账号
     *
     * @param command CSDN授权回调命令
     * @return 登录结果
     */
    @Transactional
    public LoginResultDTO handleCsdnAuthCallback(CsdnAuthCallbackCommand command) {
        // 1. 调用CSDN服务验证授权码
        CsdnAuthClient.CsdnUserInfo csdnInfo = csdnAuthClient.verifyAuthCode(command.getAuthCode());
        if (!csdnInfo.isSuccess()) {
            throw new IllegalArgumentException("CSDN授权验证失败: " + csdnInfo.getErrorMessage());
        }

        log.info("CSDN授权验证成功: csdnUserId={}, mobile={}, nickname={}",
                csdnInfo.getCsdnUserId(), csdnInfo.getMobile(), csdnInfo.getNickname());

        // 2. 检查是否已存在绑定用户
        User existingUser = userDomainService.findByCsdnBindId(csdnInfo.getCsdnUserId()).orElse(null);

        if (existingUser != null) {
            // 已注册，直接登录
            if (!userDomainService.canLogin(existingUser)) {
                throw new IllegalArgumentException("账号已被冻结，请联系客服");
            }
            userDomainService.onLoginSuccess(existingUser);
            String token = jwtTokenProvider.generateToken(existingUser.getUserId());
            log.info("用户[{}]通过CSDN扫码登录成功", existingUser.getUserId());

            LoginResultDTO result = buildLoginResult(existingUser, token);
            // 更新二维码状态（如果是扫码登录流程）
            if (command.getQrId() != null && !command.getQrId().isEmpty()) {
                csdnQrCodeUseCase.updateQrCodeLoggedIn(command.getQrId(), result);
            }
            return result;
        }

        // 3. 检查手机号是否已被非注销用户注册
        User mobileUser = userDomainService.findActiveUserByMobile(csdnInfo.getMobile()).orElse(null);
        if (mobileUser != null) {
            // 手机号已注册，绑定CSDN ID并登录
            mobileUser.setCsdnBindId(csdnInfo.getCsdnUserId());
            userDomainService.onLoginSuccess(mobileUser);
            String token = jwtTokenProvider.generateToken(mobileUser.getUserId());
            log.info("用户[{}]通过CSDN扫码绑定并登录成功", mobileUser.getUserId());

            LoginResultDTO result = buildLoginResult(mobileUser, token);
            // 更新二维码状态（如果是扫码登录流程）
            if (command.getQrId() != null && !command.getQrId().isEmpty()) {
                csdnQrCodeUseCase.updateQrCodeLoggedIn(command.getQrId(), result);
            }
            return result;
        }

        // 4. 未注册用户，需补充资料
        if (command.getPassword() == null || command.getConfirmPassword() == null) {
            throw new IllegalArgumentException("新用户需设置登录密码");
        }
        if (!command.getPassword().equals(command.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        if (!passwordService.isStrongPassword(command.getPassword())) {
            throw new IllegalArgumentException("密码长度至少8位，且包含字母和数字");
        }
        if (!Boolean.TRUE.equals(command.getAgreementAccepted()) || !Boolean.TRUE.equals(command.getPrivacyAccepted())) {
            throw new IllegalArgumentException("请阅读并同意《用户协议》和《隐私政策》");
        }

        // 5. 创建新用户
        User newUser = userDomainService.createCsdnUser(
                csdnInfo.getMobile(), csdnInfo.getNickname(), csdnInfo.getCsdnUserId());

        // 补充资料
        String avatarUrl = command.getAvatarUrl() != null ? command.getAvatarUrl() : csdnInfo.getAvatarUrl();
        userDomainService.completeCsdnUserProfile(newUser, command.getPassword(), avatarUrl);

        // 重新查询获取完整信息
        newUser = userDomainService.findByCsdnBindId(csdnInfo.getCsdnUserId())
                .orElseThrow(() -> new RuntimeException("用户创建失败"));

        String token = jwtTokenProvider.generateToken(newUser.getUserId());

        log.info("用户[{}]通过CSDN扫码注册并登录成功", newUser.getUserId());

        LoginResultDTO result = buildLoginResult(newUser, token);
        // 更新二维码状态（如果是扫码登录流程）
        if (command.getQrId() != null && !command.getQrId().isEmpty()) {
            csdnQrCodeUseCase.updateQrCodeLoggedIn(command.getQrId(), result);
        }
        return result;
    }

    /**
     * 构建登录结果
     */
    private LoginResultDTO buildLoginResult(User user, String token) {
        LoginResultDTO result = new LoginResultDTO();
        result.setAccessToken(token);
        result.setTokenType("Bearer");
        result.setExpiresIn(jwtTokenProvider.getExpirationSeconds());
        result.setUserInfo(convertToProfileDTO(user));
        return result;
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
