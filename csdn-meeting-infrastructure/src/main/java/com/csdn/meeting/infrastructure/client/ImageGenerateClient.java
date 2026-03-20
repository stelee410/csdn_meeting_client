package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.ImageGeneratePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 会议背景图生成客户端：
 * 调用豆包 Seedream 文生图模型（doubao-seedream-5-0-260128），
 * 根据会议标题和简介生成 16:9 宽屏会议背景图。
 */
@Component
public class ImageGenerateClient implements ImageGeneratePort {

    private static final Logger log = LoggerFactory.getLogger(ImageGenerateClient.class);

    /** 图片尺寸：16:9 宽屏，2K 分辨率（2848×1600） */
    private static final String IMAGE_SIZE = "2848x1600";

    private static final String IMAGE_PROMPT_TEMPLATE =
            "专业会议海报横幅背景图，宽屏16:9比例，无任何文字和文本。\n" +
            "会议主题：%s\n" +
            "会议简介：%s\n" +
            "风格要求：科技感、现代简约、渐变色调（蓝紫或蓝绿）、" +
            "适合作为会议背景的抽象设计，光效粒子感，高清质感，无人物无文字。";

    private final DoubaoClient doubaoClient;

    public ImageGenerateClient(DoubaoClient doubaoClient) {
        this.doubaoClient = doubaoClient;
    }

    @Override
    public String generate(String title, String description) {
        try {
            String desc = description != null ? truncate(description, 80) : "";
            String prompt = String.format(IMAGE_PROMPT_TEMPLATE, title, desc);
            log.info("[ImageGenerateClient] 调用豆包文生图，title={}", title);
            String imageUrl = doubaoClient.callImageGenerate(prompt, IMAGE_SIZE);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                log.info("[ImageGenerateClient] 图片生成成功，url={}", imageUrl);
                return imageUrl;
            }
            log.warn("[ImageGenerateClient] 图片 URL 为空，title={}", title);
            return null;
        } catch (Exception e) {
            log.error("[ImageGenerateClient] 图片生成失败: {}", e.getMessage(), e);
            return null;
        }
    }

    private String truncate(String text, int maxLen) {
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }
}
