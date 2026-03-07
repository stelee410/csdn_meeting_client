package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.TagSuggestionDTO;
import com.csdn.meeting.domain.port.NLPTagPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签推荐用例：根据标题和描述调用 NLP 推荐 3-5 个标签；NLP 不可用时优雅降级。
 */
@Service
public class TagSuggestionUseCase {

    private static final Logger log = LoggerFactory.getLogger(TagSuggestionUseCase.class);

    private static final List<String> FALLBACK_TAGS = Collections.unmodifiableList(
            Arrays.asList("Java", "后端", "微服务"));

    private final NLPTagPort nlpTagPort;

    public TagSuggestionUseCase(NLPTagPort nlpTagPort) {
        this.nlpTagPort = nlpTagPort;
    }

    /**
     * 根据标题和描述推荐 3-5 个标签；NLP 不可用时返回 fallback 标签（优雅降级）
     */
    public TagSuggestionDTO suggestTags(String title, String description) {
        try {
            List<String> suggested = nlpTagPort.suggestTags(
                    title != null ? title : "",
                    description != null ? description : "");
            if (suggested == null || suggested.isEmpty()) {
                return new TagSuggestionDTO(new java.util.ArrayList<>(FALLBACK_TAGS));
            }
            List<String> tags = suggested.stream()
                    .filter(t -> t != null && !t.trim().isEmpty())
                    .limit(5)
                    .collect(Collectors.toList());
            if (tags.size() < 3) {
                tags = new java.util.ArrayList<>(FALLBACK_TAGS);
            }
            return new TagSuggestionDTO(tags);
        } catch (Exception e) {
            log.warn("NLP tag suggestion unavailable, using fallback tags: {}", e.getMessage());
            return new TagSuggestionDTO(new java.util.ArrayList<>(FALLBACK_TAGS));
        }
    }
}
