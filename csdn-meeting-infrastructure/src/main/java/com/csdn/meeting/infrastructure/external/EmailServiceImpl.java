package com.csdn.meeting.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 邮件服务Mock实现
 * 当aliyun.dm.access-key-id未配置时作为降级方案使用
 * 由AliyunEmailServiceImpl在配置无效时自动降级调用此Mock逻辑
 *
 * @deprecated 请使用 AliyunEmailServiceImpl 接入阿里云真实邮件推送服务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "aliyun.dm.access-key-id", matchIfMissing = true)
public class EmailServiceImpl implements EmailService {

    @Override
    public boolean sendVerificationCode(String email, String code, String scene) {
        // TODO: 后续接入Spring Boot Mail Starter或邮件服务
        // 目前仅打印日志，用于开发和测试
        log.info("[Email Mock] 向邮箱 {} 发送验证码: {}, 场景: {}", email, code, scene);
        return true;
    }
}
