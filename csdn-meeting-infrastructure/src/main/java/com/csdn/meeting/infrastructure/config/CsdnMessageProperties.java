package com.csdn.meeting.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CSDN消息推送配置属性
 * 对应application.yml中的csdn.message配置
 */
@Component
@ConfigurationProperties(prefix = "csdn.message")
public class CsdnMessageProperties {

    /**
     * 应用唯一标识
     */
    private String appKey;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 接口基础URL
     */
    private String baseUrl = "https://msg.csdn.net";

    /**
     * 模板编码配置
     */
    private Templates templates = new Templates();

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Templates getTemplates() {
        return templates;
    }

    public void setTemplates(Templates templates) {
        this.templates = templates;
    }

    /**
     * 模板编码配置内部类
     */
    public static class Templates {
        /**
         * 会议发布IM站内信模板编码
         */
        private String meetingPublishIm = "New_Notice_IM";

        /**
         * 会议发布APP推送模板编码
         */
        private String meetingPublishPush = "New_Notice_PUSH";

        public String getMeetingPublishIm() {
            return meetingPublishIm;
        }

        public void setMeetingPublishIm(String meetingPublishIm) {
            this.meetingPublishIm = meetingPublishIm;
        }

        public String getMeetingPublishPush() {
            return meetingPublishPush;
        }

        public void setMeetingPublishPush(String meetingPublishPush) {
            this.meetingPublishPush = meetingPublishPush;
        }
    }
}
