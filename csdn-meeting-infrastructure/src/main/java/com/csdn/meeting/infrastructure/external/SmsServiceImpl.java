package com.csdn.meeting.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 短信服务Mock实现
 * 当volcengine.sms.access-key-id未配置时作为降级方案使用
 * 由VolcSmsServiceImpl在配置无效时自动降级调用此Mock逻辑
 *
 * @deprecated 请使用 VolcSmsServiceImpl 接入火山云真实短信服务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "volcengine.sms.access-key-id", matchIfMissing = true)
public class SmsServiceImpl implements SmsService {

    @Override
    public boolean sendVerificationCode(String mobile, String code, String scene) {
        // TODO: 后续接入阿里云/腾讯云短信平台
        // 目前仅打印日志，用于开发和测试
        log.info("[SMS Mock] 向手机号 {} 发送验证码: {}, 场景: {}", mobile, code, scene);
        return true;
    }
}
