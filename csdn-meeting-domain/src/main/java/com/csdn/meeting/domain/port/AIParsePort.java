package com.csdn.meeting.domain.port;

/**
 * AI 解析端口：将文件/上下文解析为结构化会议数据。
 * Infrastructure 层通过 LLM 实现，供应用层 AI 解析用例调用。
 */
public interface AIParsePort {

    /**
     * 从文件字节解析为结构化会议数据
     *
     * @param fileBytes 文件内容
     * @param fileName  文件名（含扩展名，用于判断格式）
     * @return 解析结果，字段未识别时可留空
     */
    AIParseResult parse(byte[] fileBytes, String fileName);

    /**
     * 从文件路径解析（部分实现可能直接读文件）
     *
     * @param filePath 文件路径
     * @return 解析结果
     */
    default AIParseResult parseFromPath(String filePath) {
        throw new UnsupportedOperationException("parseFromPath not implemented");
    }
}
