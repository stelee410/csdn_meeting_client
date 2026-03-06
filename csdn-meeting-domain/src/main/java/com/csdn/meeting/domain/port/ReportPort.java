package com.csdn.meeting.domain.port;

/**
 * 简报输出端口：Markdown 转 PDF/Word。
 * Infrastructure ReportEngine 实现。
 */
public interface ReportPort {

    /**
     * 将 Markdown 内容转为 PDF 字节。
     *
     * @param markdown Markdown 内容
     * @return PDF 字节数组
     */
    byte[] markdownToPdf(String markdown);

    /**
     * 将 Markdown 内容转为 Word 字节。
     *
     * @param markdown Markdown 内容
     * @return Word 字节数组
     */
    byte[] markdownToWord(String markdown);
}
