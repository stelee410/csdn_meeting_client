package com.csdn.meeting.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 豆包大模型 API 配置。
 * 对应 application.yml 中的 doubao.* 配置项。
 */
@Component
@ConfigurationProperties(prefix = "doubao")
public class DoubaoProperties {

    /** API Key，生产环境通过 DOUBAO_API_KEY 环境变量注入 */
    private String apiKey;

    /** 请求端点 */
    private String baseUrl = "https://ark.cn-beijing.volces.com/api/v3/responses";

    /** 使用的文本/多模态模型名称 */
    private String model = "doubao-seed-2-0-mini-260215";

    /** 文生图模型名称 */
    private String imageModel = "doubao-seedream-5-0-260128";

    /** 请求超时时间（毫秒） */
    private int timeoutMs = 30000;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageModel() {
        return imageModel;
    }

    public void setImageModel(String imageModel) {
        this.imageModel = imageModel;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
