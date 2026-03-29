package com.csdn.meeting.domain.entity;

import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 验证码实体
 * 用于短信验证码和邮箱验证码的统一管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VerificationCode extends BaseEntity {

    /**
     * 目标（手机号或邮箱）
     */
    private String target;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码类型
     */
    private VerificationCodeType type;

    /**
     * 业务场景
     */
    private VerificationCodeScene scene;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已使用
     */
    private Boolean used;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 判断验证码是否有效（未过期且未使用）
     */
    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expireTime);
    }

    /**
     * 判断验证码是否已过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 标记为已使用
     */
    public void markAsUsed() {
        this.used = true;
    }

    /**
     * 验证输入的验证码是否匹配
     */
    public boolean verify(String inputCode) {
        if (inputCode == null || this.code == null) {
            return false;
        }
        return this.code.equals(inputCode);
    }
}
