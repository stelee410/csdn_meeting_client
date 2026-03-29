package com.csdn.meeting.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现
 * 目前为Mock实现，后续接入邮件服务器
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public boolean sendVerificationCode(String email, String code, String scene) {
        // TODO: 后续接入Spring Boot Mail Starter或邮件服务
        // 目前仅打印日志，用于开发和测试
        log.info("[Email Mock] 向邮箱 {} 发送验证码: {}, 场景: {}", email, code, scene);
        return true;
    }
}
