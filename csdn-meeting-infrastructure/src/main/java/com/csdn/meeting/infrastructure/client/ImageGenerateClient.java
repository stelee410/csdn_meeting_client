package com.csdn.meeting.infrastructure.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.csdn.meeting.domain.port.ImageGeneratePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * 会议背景图生成客户端：
 * 1. 调用豆包文本模型，将会议信息转换为英文图片关键词
 * 2. 调用 Unsplash Source API 根据关键词获取相关图片 URL
 *    (https://source.unsplash.com/featured/1920x1080?{keywords})
 *
 * 注：如需对接豆包文生图 API（如 doubao-seedream 系列），可替换 step 2 的实现。
 */
@Component
public class ImageGenerateClient implements ImageGeneratePort {

    private static final Logger log = LoggerFactory.getLogger(ImageGenerateClient.class);

    private static final String KEYWORD_PROMPT =
            "根据以下会议信息，生成 3-5 个英文关键词，用于搜索会议主题相关的专业背景图片。\n" +
            "要求：只返回英文关键词，用逗号分隔，不要其他内容。\n" +
            "例如：technology,conference,developer,AI\n\n" +
            "会议标题：%s\n" +
            "会议简介：%s\n\n" +
            "英文关键词：";

    private static final String UNSPLASH_BASE = "https://source.unsplash.com/featured/1920x1080/?";
    private static final String FALLBACK_KEYWORDS = "technology,conference,developer";

    private final DoubaoClient doubaoClient;

    public ImageGenerateClient(DoubaoClient doubaoClient) {
        this.doubaoClient = doubaoClient;
    }

    @Override
    public String generate(String title, String description) {
        String keywords = generateKeywords(title, description);
        return buildImageUrl(keywords);
    }

    private String generateKeywords(String title, String description) {
        try {
            String prompt = String.format(KEYWORD_PROMPT,
                    title,
                    description != null ? description : "");
            String response = doubaoClient.callText(prompt);
            if (response != null && !response.trim().isEmpty()) {
                // 清理响应：只保留英文字母、数字、逗号、空格
                String cleaned = response.trim().replaceAll("[^a-zA-Z0-9,\\s]", "").trim();
                if (!cleaned.isEmpty()) {
                    return cleaned;
                }
            }
        } catch (Exception e) {
            log.warn("[ImageGenerateClient] 关键词生成失败: {}", e.getMessage());
        }
        return FALLBACK_KEYWORDS;
    }

    private String buildImageUrl(String keywords) {
        try {
            String encoded = URLEncoder.encode(keywords, "UTF-8");
            // 添加随机数避免缓存，确保每次生成不同图片
            String url = UNSPLASH_BASE + encoded + "&sig=" + System.currentTimeMillis();
            // Unsplash Source 会 302 跳转到真实图片，这里验证连通性（可选）
            return url;
        } catch (Exception e) {
            log.warn("[ImageGenerateClient] URL 构建失败: {}", e.getMessage());
            return UNSPLASH_BASE + FALLBACK_KEYWORDS;
        }
    }
}
