package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.VerificationCode;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 验证码仓储接口
 */
public interface VerificationCodeRepository {

    /**
     * 保存验证码记录
     */
    VerificationCode save(VerificationCode verificationCode);

    /**
     * 根据ID查询
     */
    Optional<VerificationCode> findById(Long id);

    /**
     * 查询指定目标的最新一条验证码记录
     */
    Optional<VerificationCode> findLatestByTarget(String target, VerificationCodeType type, VerificationCodeScene scene);

    /**
     * 查询指定目标的最新有效验证码（未过期、未使用）
     */
    Optional<VerificationCode> findLatestValidCode(String target, VerificationCodeType type, VerificationCodeScene scene);

    /**
     * 统计今日发送次数
     */
    int countTodayByTarget(String target, VerificationCodeType type, VerificationCodeScene scene);

    /**
     * 删除过期验证码
     */
    void deleteExpiredCodes(LocalDateTime before);

    /**
     * 删除指定目标的所有记录
     */
    void deleteByTarget(String target);
}
