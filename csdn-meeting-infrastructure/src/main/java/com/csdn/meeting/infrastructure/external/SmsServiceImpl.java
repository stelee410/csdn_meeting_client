package com.csdn.meeting.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现
 * 目前为Mock实现，后续接入阿里云/腾讯云短信平台
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public boolean sendVerificationCode(String mobile, String code, String scene) {
        // TODO: 后续接入阿里云/腾讯云短信平台
        // 目前仅打印日志，用于开发和测试
        log.info("[SMS Mock] 向手机号 {} 发送验证码: {}, 场景: {}", mobile, code, scene);
        return true;
    }
}
