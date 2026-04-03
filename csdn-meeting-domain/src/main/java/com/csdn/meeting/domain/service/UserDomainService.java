package com.csdn.meeting.domain.service;

import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.repository.UserRepository;
import com.csdn.meeting.domain.valueobject.UserStatus;
import com.csdn.meeting.domain.valueobject.UserType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户领域服务
 * 处理用户注册、登录等业务逻辑
 */
@Service
public class UserDomainService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserDomainService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    /**
     * 生成业务用户ID
     */
    public String generateUserId() {
        return "U" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * 创建普通用户（表单注册）
     */
    public User createNormalUser(String mobile, String password, String nickname, String avatarUrl) {
        User user = new User();
        user.setUserId(generateUserId());
        user.setMobile(mobile);
        user.setPassword(passwordService.encode(password));
        user.setUserType(UserType.USER);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        user.setStatus(UserStatus.NORMAL);
        user.setEmailVerified(false);
        user.setAgreementAccepted(false);
        user.setPrivacyAccepted(false);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 创建CSDN扫码用户（需补充资料）
     */
    public User createCsdnUser(String mobile, String nickname, String csdnBindId) {
        User user = new User();
        user.setUserId(generateUserId());
        user.setMobile(mobile);
        user.setUserType(UserType.USER);
        user.setNickname(nickname);
        user.setCsdnBindId(csdnBindId);
        user.setStatus(UserStatus.NORMAL);
        user.setEmailVerified(false);
        user.setAgreementAccepted(false);
        user.setPrivacyAccepted(false);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(User user, String rawPassword) {
        if (user == null || rawPassword == null) {
            return false;
        }
        return passwordService.matches(rawPassword, user.getPassword());
    }

    /**
     * 更新用户密码
     */
    public void updatePassword(User user, String newPassword) {
        if (user == null || newPassword == null) {
            throw new IllegalArgumentException("用户或密码不能为空");
        }
        user.setPassword(passwordService.encode(newPassword));
    }

    /**
     * 持久化用户（注册等场景写入数据库）
     */
    public User persistUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 检查手机号是否已注册
     */
    public boolean isMobileRegistered(String mobile) {
        return userRepository.findByMobile(mobile).isPresent();
    }

    /**
     * 根据手机号查找用户
     */
    public Optional<User> findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    /**
     * 根据CSDN绑定ID查找用户
     */
    public Optional<User> findByCsdnBindId(String csdnBindId) {
        return userRepository.findByCsdnBindId(csdnBindId);
    }

    /**
     * 根据用户ID查找用户
     */
    public Optional<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 登录成功后的处理（更新最后登录时间等）
     */
    public void onLoginSuccess(User user) {
        user.updateLastLoginTime();
        userRepository.save(user);
    }

    /**
     * 验证用户是否可以登录
     */
    public boolean canLogin(User user) {
        if (user == null) {
            return false;
        }
        return user.isActive();
    }

    /**
     * 补充CSDN用户资料（扫码注册后补充密码和协议）
     */
    public void completeCsdnUserProfile(User user, String password, String avatarUrl) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordService.encode(password));
        }
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            user.setAvatarUrl(avatarUrl);
        }
        user.acceptAgreements();
    }

    /**
     * 更新用户资料
     */
    public void updateProfile(User user, String nickname, String avatarUrl,
                              String realName, String company, String jobTitle,
                              String industry) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (realName != null) {
            user.setRealName(realName);
        }
        if (company != null) {
            user.setCompany(company);
        }
        if (jobTitle != null) {
            user.setJobTitle(jobTitle);
        }
        if (industry != null) {
            user.setIndustry(com.csdn.meeting.domain.valueobject.Industry.of(industry));
        }
    }

    /**
     * 更新用户邮箱并标记为已验证
     */
    public void updateEmail(User user, String email) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        user.setEmail(email);
        user.verifyEmail();
    }

    /**
     * 冻结用户账号
     */
    public void freezeUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.freeze();
        userRepository.save(user);
    }

    /**
     * 解冻用户账号
     */
    public void unfreezeUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.unfreeze();
        userRepository.save(user);
    }

    /**
     * 重置用户密码（管理员操作）
     */
    public void resetPassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        updatePassword(user, newPassword);
        userRepository.save(user);
    }
}
