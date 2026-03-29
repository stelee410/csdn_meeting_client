package com.csdn.meeting.infrastructure.external;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CSDN OAuth客户端
 * 用于CSDN App扫码授权登录/注册
 * 目前为Mock实现，后续对接CSDN授权服务
 */
@Slf4j
@Component
public class CsdnAuthClient {

    /**
     * 验证授权码并获取用户信息
     *
     * @param authCode 授权码
     * @return CSDN用户信息
     */
    public CsdnUserInfo verifyAuthCode(String authCode) {
        // TODO: 后续对接CSDN授权服务
        // 目前返回Mock数据，用于开发和测试
        log.info("[CSDN Auth Mock] 验证授权码: {}", authCode);

        CsdnUserInfo mockInfo = new CsdnUserInfo();
        mockInfo.setSuccess(true);
        mockInfo.setCsdnUserId("CSDN_" + authCode);
        mockInfo.setMobile("138****0000");
        mockInfo.setNickname("CSDN用户");
        mockInfo.setAvatarUrl("https://csdn.net/default-avatar.png");

        return mockInfo;
    }

    /**
     * CSDN用户信息
     */
    @Data
    public static class CsdnUserInfo {
        private boolean success;
        private String csdnUserId;
        private String mobile;
        private String nickname;
        private String avatarUrl;
        private String errorMessage;
    }
}
