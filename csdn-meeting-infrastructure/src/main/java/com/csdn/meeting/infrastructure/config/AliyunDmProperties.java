package com.csdn.meeting.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云邮件推送配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.dm")
public class AliyunDmProperties {

    /**
     * 阿里云访问密钥ID
     */
    private String accessKeyId;

    /**
     * 阿里云访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 服务区域ID
     */
    private String regionId = "cn-hangzhou";

    /**
     * 发件人地址
     */
    private String fromAddress;

    /**
     * 发件人昵称
     */
    private String fromAlias = "CSDN会议系统";

    /**
     * 邮件标签
     */
    private String tagName = "meeting_verify";

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 重试配置类
     */
    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long delayMs = 1000;
    }
}
