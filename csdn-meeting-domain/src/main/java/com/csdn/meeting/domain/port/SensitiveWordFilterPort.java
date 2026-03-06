package com.csdn.meeting.domain.port;

import java.util.List;

/**
 * 敏感词过滤端口：检测 AI 回填内容中的敏感词，返回含敏感词的字段名列表。
 */
public interface SensitiveWordFilterPort {

    /**
     * 检测 AI 解析结果中哪些字段包含敏感词
     *
     * @param parseResult AI 解析结果
     * @return 包含敏感词的字段名列表，无则返回空列表
     */
    List<String> findSensitiveFields(AIParseResult parseResult);
}
