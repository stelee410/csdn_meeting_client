package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.VerificationCodeSendCommand;
import com.csdn.meeting.domain.entity.VerificationCode;
import com.csdn.meeting.domain.service.VerificationCodeService;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import com.csdn.meeting.infrastructure.external.EmailService;
import com.csdn.meeting.infrastructure.external.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 验证码应用服务
 * 处理验证码发送等操作
 */
@Slf4j
@Service
public class VerificationCodeAppService {

    private final VerificationCodeService verificationCodeService;
    private final SmsService smsService;
    private final EmailService emailService;

    public VerificationCodeAppService(VerificationCodeService verificationCodeService,
                                      SmsService smsService,
                                      EmailService emailService) {
        this.verificationCodeService = verificationCodeService;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    /**
     * 发送验证码
     */
    public void sendVerificationCode(VerificationCodeSendCommand command) {
        VerificationCodeType type = VerificationCodeType.of(command.getType());
        VerificationCodeScene scene = VerificationCodeScene.of(command.getScene());

        if (type == null) {
            throw new IllegalArgumentException("验证码类型无效");
        }
        if (scene == null) {
            throw new IllegalArgumentException("业务场景无效");
        }

        // 生成并保存验证码
        VerificationCode code = verificationCodeService.generateCode(command.getTarget(), type, scene);

        // 发送验证码
        boolean sent = false;
        if (type == VerificationCodeType.SMS) {
            sent = smsService.sendVerificationCode(command.getTarget(), code.getCode(), command.getScene());
        } else if (type == VerificationCodeType.EMAIL) {
            sent = emailService.sendVerificationCode(command.getTarget(), code.getCode(), command.getScene());
        }

        if (!sent) {
            throw new RuntimeException("验证码发送失败，请稍后重试");
        }

        log.info("向[{}]发送{}验证码成功，场景: {}", command.getTarget(), type.getDisplayName(), scene.getDisplayName());
    }
}
