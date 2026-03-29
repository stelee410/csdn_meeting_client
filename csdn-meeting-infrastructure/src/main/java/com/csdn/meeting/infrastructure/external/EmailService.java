package com.csdn.meeting.infrastructure.external;

/**
 * 邮件服务接口
 * 用于发送邮箱验证码
 */
public interface EmailService {

    /**
     * 发送邮箱验证码
     *
     * @param email 目标邮箱
     * @param code  验证码
     * @param scene 业务场景（注册/绑定等）
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String email, String code, String scene);
}
