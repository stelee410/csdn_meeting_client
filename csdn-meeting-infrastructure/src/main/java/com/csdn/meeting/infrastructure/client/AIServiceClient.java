package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AIParsePort;
import com.csdn.meeting.domain.port.AIParseResult;
import org.springframework.stereotype.Component;

/**
 * AI 解析客户端：实现 AIParsePort，对接外部 LLM API。
 * Stub：无真实 LLM 配置时返回空/默认结果；测试时可注入 mock 数据。
 */
@Component
public class AIServiceClient implements AIParsePort {

    @Override
    public AIParseResult parse(byte[] fileBytes, String fileName) {
        // Stub: 无 LLM 配置时返回空结果
        AIParseResult result = new AIParseResult();
        return result;
    }
}
