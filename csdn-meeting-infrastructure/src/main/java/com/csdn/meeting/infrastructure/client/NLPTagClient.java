package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.NLPTagPort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NLP 标签推荐客户端：实现 NLPTagPort。
 * Stub 实现返回固定标签；真实实现可调用外部 NLP API。
 */
@Component
public class NLPTagClient implements NLPTagPort {

    private static final List<String> STUB_TAGS = Collections.unmodifiableList(
            Arrays.asList("Java", "后端", "微服务"));

    @Override
    public List<String> suggestTags(String title, String description) {
        return new java.util.ArrayList<>(STUB_TAGS);
    }
}
