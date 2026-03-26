package com.csdn.meeting.infrastructure.report;

import com.csdn.meeting.domain.port.ReportPort;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 简报引擎：Markdown → 合法 PDF（openhtmltopdf + Noto Sans SC）/ 合法 docx（POI）。
 */
@Component
public class ReportEngine implements ReportPort {

    private static final String NOTO_FONT_RESOURCE = "fonts/ttf/NotoSansSC/NotoSansSC-Regular.ttf";

    private static final String HTML_STYLES =
            "@page { size: A4; margin: 18mm; }"
                    + "body { font-family: 'Noto Sans SC', sans-serif; font-size: 12pt; line-height: 1.55; color: #222; }"
                    + "h1 { font-size: 20pt; margin: 14pt 0 8pt; font-weight: bold; }"
                    + "h2 { font-size: 15pt; margin: 12pt 0 6pt; font-weight: bold; }"
                    + "h3 { font-size: 13pt; margin: 10pt 0 4pt; font-weight: bold; }"
                    + "ul, ol { padding-left: 20pt; margin: 6pt 0; }"
                    + "li { margin: 3pt 0; }"
                    + "p { margin: 6pt 0; }"
                    + "code { font-family: monospace; font-size: 10.5pt; background: #f4f4f4; padding: 1pt 4pt; }"
                    + "pre { background: #f4f4f4; padding: 8pt; white-space: pre-wrap; }";

    @Override
    public byte[] markdownToPdf(String markdown) {
        String md = (markdown == null || markdown.isEmpty()) ? "# （无内容）\n" : markdown;
        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .softbreak("<br/>")
                .build();
        String bodyHtml = renderer.render(document);
        String html = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/>"
                + "<style>" + HTML_STYLES + "</style></head><body>"
                + bodyHtml
                + "</body></html>";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.useFont(this::openNotoSansScStream, "Noto Sans SC");
            builder.run();
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Markdown 转 PDF 失败", e);
        }
    }

    private InputStream openNotoSansScStream() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = cl != null ? cl.getResourceAsStream(NOTO_FONT_RESOURCE) : null;
        if (in == null) {
            in = ReportEngine.class.getClassLoader().getResourceAsStream(NOTO_FONT_RESOURCE);
        }
        if (in == null) {
            throw new IllegalStateException(
                    "未找到中文字体资源: " + NOTO_FONT_RESOURCE + "（请确认依赖 ph-fonts-noto-sans-sc 已引入）");
        }
        return in;
    }

    @Override
    public byte[] markdownToWord(String markdown) {
        String md = markdown == null ? "" : markdown;
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new MarkdownDocxRenderer(doc).render(md);
            doc.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Markdown 转 Word 失败", e);
        }
    }
}
