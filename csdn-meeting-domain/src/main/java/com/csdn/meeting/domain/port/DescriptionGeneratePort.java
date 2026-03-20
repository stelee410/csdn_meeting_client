package com.csdn.meeting.domain.port;

import java.util.List;

/**
 * 会议简介生成端口：根据标题和标签，调用 LLM 生成会议简介。
 */
public interface DescriptionGeneratePort {

    /**
     * 生成会议简介。
     *
     * @param title 会议标题
     * @param tags  会议标签列表（可为空）
     * @return 生成的简介文本
     */
    String generate(String title, List<String> tags);
}
