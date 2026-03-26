package com.csdn.meeting.infrastructure.report;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportEngineTest {

    private final ReportEngine engine = new ReportEngine();

    @Test
    void markdownToPdf_producesValidPdfHeader() {
        byte[] pdf = engine.markdownToPdf("# 会议简报\n\n- 中文条目\n");
        assertTrue(pdf.length > 500, "PDF 过小");
        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.ISO_8859_1));
    }

    @Test
    void markdownToWord_producesZipBasedDocx() {
        byte[] docx = engine.markdownToWord("## 标题\n\n正文段落。");
        assertTrue(docx.length > 200, "docx 过小");
        assertEquals('P', (char) docx[0]);
        assertEquals('K', (char) docx[1]);
    }
}
