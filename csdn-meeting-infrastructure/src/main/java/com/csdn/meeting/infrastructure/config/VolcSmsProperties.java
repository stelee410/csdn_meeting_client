package com.csdn.meeting.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 火山云短信服务配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "volcengine.sms")
public class VolcSmsProperties {

    /**
     * 访问密钥ID
     */
    private String accessKeyId;

    /**
     * 访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 短信服务端点
     */
    private String endpoint = "sms.volcengineapi.com";

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板配置
     */
    private Templates templates = new Templates();

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 短信模板配置类
     */
    @Data
    public static class Templates {
        private String register;
        private String login;
        private String resetPassword;
        private String general;
    }

    /**
     * 重试配置类
     */
    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long delayMs = 1000;
    }
}
