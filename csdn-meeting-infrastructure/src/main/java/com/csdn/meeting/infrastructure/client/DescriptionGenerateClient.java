package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.DescriptionGeneratePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会议简介生成客户端：调用豆包大模型，根据标题和标签生成 100-200 字的中文会议简介。
 */
@Component
public class DescriptionGenerateClient implements DescriptionGeneratePort {

    private static final Logger log = LoggerFactory.getLogger(DescriptionGenerateClient.class);

    private static final String PROMPT_TEMPLATE =
            "请根据以下会议信息，生成一段专业、简洁的中文会议简介，100-200 字。\n" +
            "要求：语言正式，突出会议价值和目标受众，不要重复标题，不要空话套话。\n" +
            "只返回简介文本，不要任何前缀或格式标记。\n\n" +
            "会议标题：%s\n" +
            "会议标签：%s\n\n" +
            "简介：";

    private final DoubaoClient doubaoClient;

    public DescriptionGenerateClient(DoubaoClient doubaoClient) {
        this.doubaoClient = doubaoClient;
    }

    @Override
    public String generate(String title, List<String> tags) {
        String tagsStr = (tags != null && !tags.isEmpty()) ? String.join("、", tags) : "（无）";
        String prompt = String.format(PROMPT_TEMPLATE, title, tagsStr);
        try {
            String result = doubaoClient.callText(prompt);
            return result != null ? result.trim() : "";
        } catch (Exception e) {
            log.error("[DescriptionGenerateClient] 生成失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 生成简介失败: " + e.getMessage(), e);
        }
    }
}
