package com.csdn.meeting.infrastructure.external;

/**
 * 短信服务接口
 * 用于发送短信验证码
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param mobile 目标手机号
     * @param code   验证码
     * @param scene  业务场景（注册/登录/重置）
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String mobile, String code, String scene);
}
