package com.csdn.meeting.infrastructure.report;

import com.csdn.meeting.domain.port.ReportPort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 简报引擎：Markdown 转 PDF/Word。
 * Stub：返回 Markdown 内容作为"伪"PDF/Word 字节（实际为文本），无真实转换。
 */
@Component
public class ReportEngine implements ReportPort {

    private static final byte[] PDF_HEADER = "%PDF-1.4 stub\n".getBytes(StandardCharsets.UTF_8);

    @Override
    public byte[] markdownToPdf(String markdown) {
        if (markdown == null) return PDF_HEADER;
        // Stub: 实际生产应使用 iText 等库
        return (new String(PDF_HEADER, StandardCharsets.UTF_8) + markdown).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] markdownToWord(String markdown) {
        if (markdown == null) return new byte[0];
        // Stub: 实际生产应使用 Apache POI
        return markdown.getBytes(StandardCharsets.UTF_8);
    }
}
