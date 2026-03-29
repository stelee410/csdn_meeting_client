package com.csdn.meeting.infrastructure.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * CSDN授权客户端
 * TODO【需与CSDN对接】：此客户端需要与CSDN开放平台对接，确认实际的API地址和授权流程
 */
@Slf4j
@Component
public class CsdnAuthClient {

    // TODO【需与CSDN对接】：确认CSDN OAuth服务的实际地址
    @Value("${csdn.oauth.server:https://passport.csdn.net}")
    private String csdnServer;

    // TODO【需与CSDN对接】：申请应用时获取的clientId
    @Value("${csdn.oauth.client-id:your-client-id}")
    private String clientId;

    // TODO【需与CSDN对接】：申请应用时获取的clientSecret
    @Value("${csdn.oauth.client-secret:your-client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CsdnAuthClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 验证授权码并获取用户信息
     * TODO【需与CSDN对接】：确认实际的授权码验证接口和返回格式
     *
     * @param authCode 前端从CSDN回调中获取的授权码
     * @return CSDN用户信息
     */
    public CsdnUserInfo verifyAuthCode(String authCode) {
        try {
            // TODO【需与CSDN对接】：确认实际的token交换接口地址
            String tokenUrl = csdnServer + "/oauth2/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("code", authCode);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            // TODO【需与CSDN对接】：确认回调地址是否与CSDN应用配置一致
            params.add("redirect_uri", "https://meeting.csdn.net/auth/csdn/callback");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // TODO【需与CSDN对接】：实际对接时启用以下代码
            // ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
            // JsonNode tokenData = objectMapper.readTree(response.getBody());
            // String accessToken = tokenData.get("access_token").asText();

            // TODO【需与CSDN对接】：确认获取用户信息的接口地址
            // String userInfoUrl = csdnServer + "/oauth2/userinfo?access_token=" + accessToken;
            // ResponseEntity<String> userResponse = restTemplate.getForEntity(userInfoUrl, String.class);
            // JsonNode userData = objectMapper.readTree(userResponse.getBody());

            // 当前为模拟实现，实际对接后删除
            log.info("TODO【需与CSDN对接】模拟验证授权码: {}", authCode);
            CsdnUserInfo mockInfo = new CsdnUserInfo();
            mockInfo.setSuccess(true);
            mockInfo.setCsdnUserId("CSDN_" + authCode.substring(0, 8));
            mockInfo.setMobile("138****" + (int)(Math.random() * 10000));
            mockInfo.setNickname("CSDN用户" + authCode.substring(0, 4));
            mockInfo.setAvatarUrl("https://avatar.csdn.net/default.png");
            return mockInfo;

        } catch (Exception e) {
            log.error("TODO【需与CSDN对接】验证CSDN授权码失败", e);
            CsdnUserInfo errorInfo = new CsdnUserInfo();
            errorInfo.setSuccess(false);
            return errorInfo;
        }
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
    }
}
