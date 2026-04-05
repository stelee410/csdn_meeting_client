package com.csdn.meeting.infrastructure.external;

import com.csdn.meeting.infrastructure.config.VolcSmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 火山云短信服务实现
 * 支持配置不完整时自动降级到Mock模式，带重试机制
 * 使用HTTP API调用火山云SMS服务
 */
@Slf4j
@Service
@Primary
public class VolcSmsServiceImpl implements SmsService {

    @Autowired
    private VolcSmsProperties properties;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        if (isConfigValid()) {
            log.info("火山云短信服务初始化成功，端点: {}", properties.getEndpoint());
        } else {
            log.warn("火山云短信配置不完整（缺少accessKeyId或accessKeySecret），将使用模拟模式");
        }
    }

    /**
     * 检查配置是否有效
     */
    private boolean isConfigValid() {
        return properties.getAccessKeyId() != null && !properties.getAccessKeyId().isEmpty()
                && properties.getAccessKeySecret() != null && !properties.getAccessKeySecret().isEmpty();
    }

    @Override
    public boolean sendVerificationCode(String mobile, String code, String scene) {
        if (!isConfigValid()) {
            log.info("[SMS Mock] 向手机号 {} 发送验证码: {}, 场景: {}", maskMobile(mobile), code, scene);
            return true;
        }

        String templateId = getTemplateId(scene);
        if (templateId == null || templateId.isEmpty()) {
            log.error("未找到场景 {} 对应的短信模板，请检查volcengine.sms.templates配置", scene);
            return false;
        }

        int maxAttempts = properties.getRetry().getMaxAttempts();
        long delayMs = properties.getRetry().getDelayMs();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                boolean success = sendSmsViaHttp(mobile, code, templateId);
                if (success) {
                    log.info("短信发送成功，手机号: {}，场景: {}，模板: {}",
                            maskMobile(mobile), scene, templateId);
                    return true;
                } else {
                    log.error("短信发送失败 (尝试 {}/{}}", attempt, maxAttempts);
                }

            } catch (Exception e) {
                log.error("短信发送异常 (尝试 {}/{}): {}", attempt, maxAttempts, e.getMessage(), e);
            }

            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(delayMs * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("短信重试等待被中断");
                    break;
                }
            }
        }

        return false;
    }

    /**
     * 通过HTTP调用火山云短信API
     * 使用签名验证方式调用OpenAPI
     */
    private boolean sendSmsViaHttp(String mobile, String code, String templateId) {
        try {
            // 火山云SMS API OpenAPI v3
            String service = "sms";
            String version = "2021-01-01";
            String action = "SendSms";
            String region = "cn-north-1";

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("PhoneNumbers", mobile);
            requestBody.put("SignName", properties.getSignName());
            requestBody.put("TemplateCode", templateId);
            requestBody.put("TemplateParam", "{\"code\":\"" + code + "\"}");

            HttpHeaders headers = buildHeaders(service, version, action, region, requestBody);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://" + properties.getEndpoint() + "/?Action=" + action
                    + "&Version=" + version;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            log.error("调用火山云短信API失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 构建HTTP请求头，包含签名
     */
    private HttpHeaders buildHeaders(String service, String version, String action,
                                      String region, Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String accessKeyId = properties.getAccessKeyId();
        String secretKey = properties.getAccessKeySecret();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateTime = sdf.format(new Date());

        headers.set("X-Date", dateTime);
        headers.set("X-Service", service);
        headers.set("X-Version", version);
        headers.set("X-Action", action);
        headers.set("X-Region", region);
        headers.set("X-Access-Key-Id", accessKeyId);

        // 计算签名
        try {
            String stringToSign = dateTime + service + action;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signature = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

            // Base64编码签名
            String signatureBase64 = java.util.Base64.getEncoder().encodeToString(signature);
            headers.set("Authorization", "HMAC-SHA256 Credential=" + accessKeyId + ", SignedHeaders=x-date, Signature=" + signatureBase64);
        } catch (Exception e) {
            log.error("计算签名失败: {}", e.getMessage());
        }

        return headers;
    }

    /**
     * 根据场景获取对应的模板ID
     */
    private String getTemplateId(String scene) {
        if (scene == null) {
            return properties.getTemplates().getGeneral();
        }

        String sceneLower = scene.toLowerCase();
        if (sceneLower.contains("register") || sceneLower.contains("注册")) {
            return properties.getTemplates().getRegister();
        } else if (sceneLower.contains("login") || sceneLower.contains("登录")) {
            return properties.getTemplates().getLogin();
        } else if (sceneLower.contains("reset") || sceneLower.contains("重置")) {
            return properties.getTemplates().getResetPassword();
        } else {
            return properties.getTemplates().getGeneral();
        }
    }

    /**
     * 手机号脱敏显示
     */
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
