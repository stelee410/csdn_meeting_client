package com.csdn.meeting.domain.port;

import java.util.List;

/**
 * NLP 标签推荐端口：根据标题和描述推荐会议标签。
 * Infrastructure 层通过 NLP/LLM 实现，供应用层标签推荐用例调用。
 */
public interface NLPTagPort {

    /**
     * 根据标题和描述推荐 3-5 个标签
     *
     * @param title       会议标题
     * @param description 会议描述（可为空）
     * @return 推荐标签列表，3-5 个
     */
    List<String> suggestTags(String title, String description);
}
