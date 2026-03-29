package com.csdn.meeting.domain.service;

import com.csdn.meeting.domain.entity.VerificationCode;
import com.csdn.meeting.domain.repository.VerificationCodeRepository;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * 验证码领域服务
 * 处理验证码生成、校验、频控等逻辑
 */
@Service
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    // 验证码有效期：5分钟
    private static final long EXPIRE_MINUTES = 5;
    // 发送间隔：60秒
    private static final long SEND_INTERVAL_SECONDS = 60;
    // 每日发送上限：10次
    private static final int DAILY_LIMIT = 10;
    // 验证码长度：6位数字
    private static final int CODE_LENGTH = 6;

    public VerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    /**
     * 生成验证码
     */
    public VerificationCode generateCode(String target, VerificationCodeType type, VerificationCodeScene scene) {
        // 检查频控
        checkFrequencyControl(target, type, scene);

        // 生成6位数字验证码
        String code = generateNumericCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setScene(scene);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        verificationCode.setUsed(false);
        verificationCode.setCreateTime(LocalDateTime.now());

        return verificationCodeRepository.save(verificationCode);
    }

    /**
     * 校验验证码
     *
     * @param target 目标（手机号/邮箱）
     * @param code   用户输入的验证码
     * @param type   验证码类型
     * @param scene  业务场景
     * @return 是否验证通过
     */
    public boolean verifyCode(String target, String code, VerificationCodeType type, VerificationCodeScene scene) {
        if (target == null || code == null) {
            return false;
        }

        Optional<VerificationCode> optionalCode = verificationCodeRepository
                .findLatestValidCode(target, type, scene);

        if (!optionalCode.isPresent()) {
            return false;
        }

        VerificationCode verificationCode = optionalCode.get();

        // 检查是否已过期
        if (verificationCode.isExpired()) {
            return false;
        }

        // 检查是否已使用
        if (verificationCode.getUsed()) {
            return false;
        }

        // 验证匹配
        if (!verificationCode.verify(code)) {
            return false;
        }

        // 标记为已使用
        verificationCode.markAsUsed();
        verificationCodeRepository.save(verificationCode);

        return true;
    }

    /**
     * 检查频控
     * 1. 发送间隔：60秒内不能重复发送
     * 2. 每日上限：同一目标每天最多10次
     */
    private void checkFrequencyControl(String target, VerificationCodeType type, VerificationCodeScene scene) {
        // 检查发送间隔
        Optional<VerificationCode> latestCode = verificationCodeRepository
                .findLatestByTarget(target, type, scene);

        if (latestCode.isPresent()) {
            LocalDateTime lastSendTime = latestCode.get().getCreateTime();
            if (lastSendTime.plusSeconds(SEND_INTERVAL_SECONDS).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("发送过于频繁，请稍后再试");
            }
        }

        // 检查每日上限
        int todayCount = verificationCodeRepository.countTodayByTarget(target, type, scene);
        if (todayCount >= DAILY_LIMIT) {
            throw new IllegalStateException("今日验证码发送次数已达上限");
        }
    }

    /**
     * 生成6位数字验证码
     */
    private String generateNumericCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 清理过期验证码（可由定时任务调用）
     */
    public void cleanExpiredCodes() {
        verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
    }
}
