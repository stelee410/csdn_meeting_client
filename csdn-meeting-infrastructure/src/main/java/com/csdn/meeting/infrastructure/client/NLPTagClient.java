package com.csdn.meeting.infrastructure.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.csdn.meeting.domain.port.NLPTagPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NLP 标签推荐客户端：调用豆包大模型，根据会议标题和描述推荐 3-5 个技术标签。
 */
@Component
public class NLPTagClient implements NLPTagPort {

    private static final Logger log = LoggerFactory.getLogger(NLPTagClient.class);

    private static final List<String> FALLBACK_TAGS = Collections.unmodifiableList(
            Arrays.asList("技术", "开发者", "AI"));

    private static final String TAG_PROMPT_TEMPLATE =
            "根据以下会议信息，推荐 3-5 个简洁的技术标签，用于帮助用户发现该会议。\n" +
            "要求：标签为中文或英文技术词汇，简短（1-4个字），以 JSON 数组格式返回，不要其他内容。\n" +
            "示例：[\"AI\", \"机器学习\", \"大数据\", \"云原生\"]\n\n" +
            "会议标题：%s\n" +
            "会议描述：%s\n\n" +
            "只返回 JSON 数组，不要任何解释：";

    private final DoubaoClient doubaoClient;

    public NLPTagClient(DoubaoClient doubaoClient) {
        this.doubaoClient = doubaoClient;
    }

    @Override
    public List<String> suggestTags(String title, String description) {
        try {
            String prompt = String.format(TAG_PROMPT_TEMPLATE,
                    title != null ? title : "",
                    description != null ? description : "");
            String responseText = doubaoClient.callText(prompt);
            return parseTags(responseText);
        } catch (Exception e) {
            log.warn("[NLPTagClient] 豆包标签推荐失败，降级使用默认标签: {}", e.getMessage());
            return new ArrayList<>(FALLBACK_TAGS);
        }
    }

    private List<String> parseTags(String responseText) {
        if (responseText == null || responseText.trim().isEmpty()) {
            return new ArrayList<>(FALLBACK_TAGS);
        }
        try {
            String json = extractJsonArray(responseText);
            JSONArray arr = JSON.parseArray(json);
            if (arr != null && !arr.isEmpty()) {
                List<String> tags = new ArrayList<>();
                for (int i = 0; i < arr.size(); i++) {
                    String tag = arr.getString(i);
                    if (tag != null && !tag.trim().isEmpty()) {
                        tags.add(tag.trim());
                    }
                }
                return tags;
            }
        } catch (Exception e) {
            log.warn("[NLPTagClient] 标签 JSON 解析失败，原始响应: {}", responseText);
        }
        return new ArrayList<>(FALLBACK_TAGS);
    }

    private String extractJsonArray(String text) {
        text = text.trim();
        if (text.startsWith("```")) {
            int start = text.indexOf('\n');
            int end = text.lastIndexOf("```");
            if (start > 0 && end > start) {
                text = text.substring(start + 1, end).trim();
            }
        }
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
