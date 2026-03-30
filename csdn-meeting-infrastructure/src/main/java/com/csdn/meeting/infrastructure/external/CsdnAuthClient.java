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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * CSDN授权客户端
 * 实现CSDN OAuth2.0授权流程，支持扫码登录和用户信息获取
 *
 * TODO【需与CSDN对接】: 需要与CSDN开放平台确认以下事项：
 * 1. OAuth2.0授权接口的实际地址（authorize、token、userinfo）
 * 2. 申请App的client-id和client-secret
 * 3. 授权scope范围（当前使用basic phone）
 * 4. 用户信息返回字段格式（id、mobile、nickname、avatar等）
 * 5. 回调地址需在CSDN开放平台配置白名单
 * 6. 确认二维码有效期策略（当前实现5分钟）
 */
@Slf4j
@Component
public class CsdnAuthClient {

    // TODO【需与CSDN对接】: 确认CSDN OAuth服务实际地址
    @Value("${csdn.oauth.server:https://passport.csdn.net}")
    private String csdnServer;

    // TODO【需与CSDN对接】: 从CSDN开放平台获取client-id
    @Value("${csdn.oauth.client-id:}")
    private String clientId;

    // TODO【需与CSDN对接】: 从CSDN开放平台获取client-secret
    @Value("${csdn.oauth.client-secret:}")
    private String clientSecret;

    // TODO【需与CSDN对接】: 确认回调地址已在CSDN开放平台配置
    @Value("${csdn.oauth.redirect-uri:https://meeting.csdn.net/auth/csdn/callback}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CsdnAuthClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 生成CSDN扫码授权URL
     * 用户扫码后，CSDN会跳转到redirectUri并携带授权码
     *
     * TODO【需与CSDN对接】: 确认实际授权URL格式和参数
     *
     * @param state 防CSRF攻击的随机状态码
     * @return 完整的授权URL
     */
    public String generateAuthUrl(String state) {
        if (clientId == null || clientId.isEmpty()) {
            log.error("CSDN OAuth clientId未配置，请从CSDN开放平台获取");
            return null;
        }

        // TODO【需与CSDN对接】: 确认授权端点路径（当前使用标准OAuth2.0 /oauth2/authorize）
        StringBuilder authUrl = new StringBuilder();
        authUrl.append(csdnServer).append("/oauth2/authorize");
        authUrl.append("?client_id=").append(clientId);
        authUrl.append("&redirect_uri=").append(redirectUri);
        authUrl.append("&response_type=code");
        // TODO【需与CSDN对接】: 确认scope范围（当前使用basic获取基本信息，phone获取手机号）
        authUrl.append("&scope=basic phone");
        authUrl.append("&state=").append(state);

        log.info("生成CSDN授权URL: {}", authUrl);
        return authUrl.toString();
    }

    /**
     * 验证授权码并获取用户信息
     * 标准OAuth2.0流程：使用授权码换取access_token，再获取用户信息
     *
     * @param authCode 用户扫码后CSDN回调返回的授权码
     * @return CSDN用户信息
     */
    public CsdnUserInfo verifyAuthCode(String authCode) {
        if (authCode == null || authCode.isEmpty()) {
            log.error("授权码不能为空");
            return CsdnUserInfo.fail("授权码不能为空");
        }

        // 检查是否配置了OAuth参数
        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            log.warn("CSDN OAuth未配置(clientId或clientSecret为空)，使用模拟模式");
            return mockVerifyAuthCode(authCode);
        }

        try {
            // Step 1: 使用授权码换取access_token
            String accessToken = exchangeCodeForToken(authCode);
            if (accessToken == null) {
                return CsdnUserInfo.fail("换取access_token失败");
            }

            // Step 2: 使用access_token获取用户信息
            return fetchUserInfo(accessToken);

        } catch (Exception e) {
            log.error("CSDN OAuth验证失败", e);
            return CsdnUserInfo.fail("验证失败: " + e.getMessage());
        }
    }

    /**
     * 使用授权码换取access_token
     *
     * TODO【需与CSDN对接】: 确认实际token交换接口地址和请求方式
     */
    private String exchangeCodeForToken(String authCode) {
        try {
            // TODO【需与CSDN对接】: 确认token端点路径
            String tokenUrl = csdnServer + "/oauth2/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("code", authCode);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            log.info("请求CSDN token: url={}, client_id={}", tokenUrl, clientId);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("换取token失败: status={}, body={}", response.getStatusCode(), response.getBody());
                return null;
            }

            JsonNode tokenData = objectMapper.readTree(response.getBody());
            // TODO【需与CSDN对接】: 确认access_token字段名
            String accessToken = tokenData.get("access_token").asText();

            log.info("成功获取access_token");
            return accessToken;

        } catch (RestClientException e) {
            log.error("请求CSDN token接口失败，请确认CSDN OAuth服务地址和接口格式", e);
            return null;
        } catch (Exception e) {
            log.error("解析token响应失败", e);
            return null;
        }
    }

    /**
     * 使用access_token获取用户信息
     *
     * TODO【需与CSDN对接】: 确认实际用户信息接口地址和返回字段格式
     */
    private CsdnUserInfo fetchUserInfo(String accessToken) {
        try {
            // TODO【需与CSDN对接】: 确认userinfo端点路径（当前使用标准OAuth2.0 /oauth2/userinfo）
            String userInfoUrl = csdnServer + "/oauth2/userinfo?access_token=" + accessToken;

            log.info("请求CSDN用户信息: url={}", userInfoUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(userInfoUrl, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("获取用户信息失败: status={}, body={}", response.getStatusCode(), response.getBody());
                return CsdnUserInfo.fail("获取用户信息失败");
            }

            JsonNode userData = objectMapper.readTree(response.getBody());

            CsdnUserInfo userInfo = new CsdnUserInfo();
            userInfo.setSuccess(true);
            // TODO【需与CSDN对接】: 确认用户ID字段名（当前使用"id"）
            userInfo.setCsdnUserId(userData.get("id").asText());
            // TODO【需与CSDN对接】: 确认手机号字段名（当前使用"mobile"）
            userInfo.setMobile(userData.get("mobile").asText());
            // TODO【需与CSDN对接】: 确认昵称字段名（当前使用"nickname"）
            userInfo.setNickname(userData.get("nickname").asText());
            // TODO【需与CSDN对接】: 确认头像字段名（当前使用"avatar"）
            userInfo.setAvatarUrl(userData.has("avatar") ? userData.get("avatar").asText() : null);

            log.info("成功获取CSDN用户信息: csdnUserId={}", userInfo.getCsdnUserId());
            return userInfo;

        } catch (RestClientException e) {
            log.error("请求CSDN用户信息接口失败，请确认CSDN用户信息接口地址", e);
            return CsdnUserInfo.fail("请求用户信息失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("解析用户信息失败，请确认返回字段格式", e);
            return CsdnUserInfo.fail("解析用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 模拟验证授权码（用于开发和测试环境，未配置CSDN OAuth时）
     */
    private CsdnUserInfo mockVerifyAuthCode(String authCode) {
        log.warn("使用模拟模式验证CSDN授权码: {}", authCode);

        // 生成基于authCode的确定性用户信息（便于测试）
        String seed = authCode.substring(0, Math.min(8, authCode.length()));
        String mockUserId = "CSDN_" + seed;

        // 生成模拟手机号（格式：13800138000 + seed哈希）
        int hash = Math.abs(seed.hashCode()) % 10000;
        String mockMobile = "138" + String.format("%04d", hash) + String.format("%04d", (hash * 7) % 10000);

        CsdnUserInfo mockInfo = new CsdnUserInfo();
        mockInfo.setSuccess(true);
        mockInfo.setCsdnUserId(mockUserId);
        mockInfo.setMobile(mockMobile);
        mockInfo.setNickname("CSDN用户" + seed);
        mockInfo.setAvatarUrl("https://avatar.csdn.net/default.png");

        log.info("生成模拟CSDN用户信息: userId={}, mobile={}", mockUserId, mockMobile);
        return mockInfo;
    }

    /**
     * CSDN用户信息
     */
    @Data
    public static class CsdnUserInfo {
        private boolean success;
        private String errorMessage;
        private String csdnUserId;
        private String mobile;
        private String nickname;
        private String avatarUrl;

        public static CsdnUserInfo fail(String message) {
            CsdnUserInfo info = new CsdnUserInfo();
            info.setSuccess(false);
            info.setErrorMessage(message);
            return info;
        }
    }
}
