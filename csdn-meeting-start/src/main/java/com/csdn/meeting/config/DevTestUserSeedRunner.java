package com.csdn.meeting.config;

import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.repository.UserRepository;
import com.csdn.meeting.domain.service.PasswordService;
import com.csdn.meeting.domain.service.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 开发环境：若库中不存在指定手机号，则自动插入一条可密码登录的测试用户。
 * <p>
 * 仅 {@code spring.profiles.active} 包含 {@code dev} 时生效。
 * 关闭：{@code meeting.dev.seed-test-user=false}
 */
@Slf4j
@Component
@Profile("dev")
@Order(100)
public class DevTestUserSeedRunner implements CommandLineRunner {

    @Value("${meeting.dev.seed-test-user:true}")
    private boolean seedEnabled;

    @Value("${meeting.dev.test-user.mobile:13800138000}")
    private String testMobile;

    @Value("${meeting.dev.test-user.password:Test1234}")
    private String testPassword;

    @Value("${meeting.dev.test-user.nickname:本地测试用户}")
    private String testNickname;

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final PasswordService passwordService;

    public DevTestUserSeedRunner(UserRepository userRepository,
                                 UserDomainService userDomainService,
                                 PasswordService passwordService) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.passwordService = passwordService;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }
        if (userRepository.findByMobile(testMobile.trim()).isPresent()) {
            log.info("[dev] 测试用户已存在 mobile={}，跳过创建", testMobile);
            return;
        }
        if (!passwordService.isStrongPassword(testPassword)) {
            log.warn("[dev] meeting.dev.test-user.password 不符合强度（至少8位且含字母与数字），跳过创建测试用户");
            return;
        }
        User user = userDomainService.createNormalUser(
                testMobile.trim(), testPassword, testNickname.trim(), null);
        user.acceptAgreements();
        user = userRepository.save(user);
        log.info("[dev] 已创建测试用户 userId={} mobile={} nickname={}，密码见配置 meeting.dev.test-user.password（默认 Test1234）",
                user.getUserId(), testMobile, testNickname);
    }
}
