package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AIParseResult;
import com.csdn.meeting.domain.port.SensitiveWordFilterPort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 敏感词过滤：检测 AI 回填内容中的敏感词，返回含敏感词的字段名列表。
 * Stub：测试时返回空列表；真实实现可接入敏感词库。
 */
@Component
public class SensitiveWordFilter implements SensitiveWordFilterPort {

    @Override
    public List<String> findSensitiveFields(AIParseResult parseResult) {
        // Stub: 测试时返回空列表
        return Collections.emptyList();
    }
}
