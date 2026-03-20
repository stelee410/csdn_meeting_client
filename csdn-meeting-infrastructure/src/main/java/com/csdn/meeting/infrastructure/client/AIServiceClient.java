package com.csdn.meeting.infrastructure.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.csdn.meeting.domain.port.AIParsePort;
import com.csdn.meeting.domain.port.AIParseResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * AI 解析客户端：对接豆包大模型，从文件中提取结构化会议信息。
 * - 图片（JPG/PNG/GIF）：使用豆包视觉 API（base64 方式）
 * - PDF：Apache PDFBox 提取文本 → 豆包文本 API
 * - Word（.doc/.docx）：Apache POI 提取文本 → 豆包文本 API
 */
@Component
public class AIServiceClient implements AIParsePort {

    private static final Logger log = LoggerFactory.getLogger(AIServiceClient.class);

    private static final String AI_PARSE_PROMPT =
            "请从以下会议资料中提取结构化信息，严格以 JSON 格式返回，不要任何解释或 markdown 标记，只返回 JSON。\n" +
            "JSON 字段（未识别的字段留空字符串或空数组）：\n" +
            "{\n" +
            "  \"title\": \"会议标题\",\n" +
            "  \"description\": \"会议简介（100-300字）\",\n" +
            "  \"organizer\": \"主办方\",\n" +
            "  \"format\": \"会议形式，只能是：线上 或 线下 或 线上+线下\",\n" +
            "  \"scene\": \"会议场景，如：开发者会议、产业会议、产品发布会议\",\n" +
            "  \"venue\": \"会议地点\",\n" +
            "  \"regions\": \"所在地区，多个用逗号分隔\",\n" +
            "  \"startTime\": \"开始时间，ISO 8601 格式，如 2026-06-01T09:00:00\",\n" +
            "  \"endTime\": \"结束时间，ISO 8601 格式\",\n" +
            "  \"tags\": [\"标签1\", \"标签2\"],\n" +
            "  \"targetAudience\": \"目标受众，如：开发者、产品经理\"\n" +
            "}\n";
    private static final String OCR_PROMPT =
            "请识别这张图片中的全部可见文字，按阅读顺序输出为纯文本。\n" +
            "要求：\n" +
            "1) 只输出识别到的文本，不要解释\n" +
            "2) 保留关键字段（标题、时间、地点、主办方、议程等）\n" +
            "3) 如果有多段内容可换行输出\n";

    private static final List<String> IMAGE_EXTS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final List<DateTimeFormatter> DT_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    );

    private final DoubaoClient doubaoClient;

    public AIServiceClient(DoubaoClient doubaoClient) {
        this.doubaoClient = doubaoClient;
    }

    @Override
    public AIParseResult parse(byte[] fileBytes, String fileName) {
        String lowerName = fileName.toLowerCase();
        try {
            log.info("[AIServiceClient] start parse, fileName={}", fileName);
            String responseText;
            String imageMimeType = null;
            if (isImage(lowerName)) {
                log.info("[AIServiceClient] parse mode=image");
                imageMimeType = lowerName.endsWith(".png") ? "image/png"
                        : lowerName.endsWith(".gif") ? "image/gif"
                        : lowerName.endsWith(".webp") ? "image/webp"
                        : "image/jpeg";
                responseText = doubaoClient.callWithImage(fileBytes, imageMimeType, AI_PARSE_PROMPT);
            } else {
                log.info("[AIServiceClient] parse mode=text, ext={}", lowerName);
                String extractedText = extractText(fileBytes, lowerName);
                if (extractedText == null || extractedText.trim().isEmpty()) {
                    log.warn("[AIServiceClient] 文件文本提取为空，fileName={}", fileName);
                    return new AIParseResult();
                }
                log.debug("[AIServiceClient] 提取文本长度={} 内容预览={}", extractedText.length(),
                        truncate(extractedText.replace('\n', ' '), 200));
                String prompt = AI_PARSE_PROMPT + "\n以下是文件内容：\n" + truncate(extractedText, 4000);
                log.debug("[AIServiceClient] 发送给豆包的 prompt 长度={}\n{}", prompt.length(), prompt);
                responseText = doubaoClient.callText(prompt);
            }
            AIParseResult result = parseJsonToResult(responseText);
            // 图片场景下首轮全空时，走 OCR -> 二次结构化提取兜底
            if (!hasAnyField(result) && imageMimeType != null) {
                log.warn("[AIServiceClient] first parse empty, try OCR fallback, fileName={}", fileName);
                String ocrText = doubaoClient.callWithImage(fileBytes, imageMimeType, OCR_PROMPT);
                if (notBlank(ocrText)) {
                    String retryPrompt = AI_PARSE_PROMPT + "\n以下是图片 OCR 识别文本：\n" + truncate(ocrText, 4000);
                    String retryResponse = doubaoClient.callText(retryPrompt);
                    result = parseJsonToResult(retryResponse);
                }
            }
            fillTitleFallbackIfNeeded(result, fileName);
            if (!hasAnyField(result)) {
                String snippet = responseText == null ? "" : truncate(responseText.replace('\n', ' '), 400);
                throw new RuntimeException("AI 返回成功但未提取到任何字段，response=" + snippet);
            }
            return result;
        } catch (Exception e) {
            log.error("[AIServiceClient] 解析失败 fileName={}: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("AI 文件解析失败: " + e.getMessage(), e);
        }
    }

    // ---- file text extraction ----

    private String extractText(byte[] bytes, String lowerName) {
        try {
            if (lowerName.endsWith(".pdf")) {
                return extractPdf(bytes);
            } else if (lowerName.endsWith(".docx")) {
                return extractDocx(bytes);
            } else if (lowerName.endsWith(".doc")) {
                return extractDoc(bytes);
            } else {
                // 未知格式尝试直接当文本读取
                return new String(bytes, "UTF-8");
            }
        } catch (Exception e) {
            log.warn("[AIServiceClient] 文本提取失败: {}", e.getMessage());
            return "";
        }
    }

    private String extractPdf(byte[] bytes) throws Exception {
        try (PDDocument doc = PDDocument.load(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private String extractDocx(byte[] bytes) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes));
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String extractDoc(byte[] bytes) throws Exception {
        try (HWPFDocument doc = new HWPFDocument(new ByteArrayInputStream(bytes));
             WordExtractor extractor = new WordExtractor(doc)) {
            return extractor.getText();
        }
    }

    // ---- response parsing ----

    private AIParseResult parseJsonToResult(String responseText) {
        AIParseResult result = new AIParseResult();
        if (responseText == null || responseText.trim().isEmpty()) {
            return result;
        }
        try {
            String json = extractJson(responseText);
            JSONObject obj = JSON.parseObject(json);
            if (obj == null) return result;
            // 兼容 { "data": { ... } } 包裹格式
            JSONObject data = obj.getJSONObject("data");
            if (data != null) {
                obj = data;
            }

            result.setTitle(firstNonBlank(
                    safeStr(obj, "title"),
                    safeStr(obj, "meetingTitle"),
                    safeStr(obj, "meetingName"),
                    safeStr(obj, "name"),
                    safeStr(obj, "subject"),
                    safeStr(obj, "会议名称"),
                    safeStr(obj, "会议标题"),
                    safeStr(obj, "主题")
            ));
            result.setDescription(safeStr(obj, "description"));
            result.setOrganizer(safeStr(obj, "organizer"));
            result.setFormat(safeStr(obj, "format"));
            result.setScene(safeStr(obj, "scene"));
            result.setVenue(safeStr(obj, "venue"));
            result.setRegions(safeStr(obj, "regions"));
            result.setTargetAudience(safeStr(obj, "targetAudience"));
            result.setStartTime(parseDateTime(safeStr(obj, "startTime")));
            result.setEndTime(parseDateTime(safeStr(obj, "endTime")));

            JSONArray tags = obj.getJSONArray("tags");
            if (tags != null) {
                result.setTags(tags.toJavaList(String.class));
            } else {
                String tagsText = safeStr(obj, "tags");
                if (tagsText != null && !tagsText.isEmpty()) {
                    result.setTags(Arrays.asList(tagsText.split("[,，]")));
                }
            }
        } catch (Exception e) {
            log.warn("[AIServiceClient] JSON 解析失败，原始响应: {}", responseText, e);
        }
        return result;
    }

    /** 从可能含 markdown 代码块的字符串中提取 JSON */
    private String extractJson(String text) {
        text = text.trim();
        if (text.startsWith("```")) {
            int start = text.indexOf('\n');
            int end = text.lastIndexOf("```");
            if (start > 0 && end > start) {
                return text.substring(start + 1, end).trim();
            }
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String safeStr(JSONObject obj, String key) {
        String v = obj.getString(key);
        return v != null ? v.trim() : null;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        for (DateTimeFormatter fmt : DT_FORMATTERS) {
            try {
                return LocalDateTime.parse(value.trim(), fmt);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private boolean isImage(String lowerName) {
        for (String ext : IMAGE_EXTS) {
            if (lowerName.endsWith(ext)) return true;
        }
        return false;
    }

    private String truncate(String text, int maxLen) {
        return text.length() > maxLen ? text.substring(0, maxLen) : text;
    }

    private boolean hasAnyField(AIParseResult r) {
        if (r == null) return false;
        return notBlank(r.getTitle())
                || notBlank(r.getDescription())
                || notBlank(r.getOrganizer())
                || notBlank(r.getFormat())
                || notBlank(r.getScene())
                || notBlank(r.getVenue())
                || notBlank(r.getRegions())
                || notBlank(r.getCoverImage())
                || !r.getTags().isEmpty()
                || notBlank(r.getTargetAudience())
                || r.getStartTime() != null
                || r.getEndTime() != null
                || notBlank(r.getScheduleDaysJson());
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (notBlank(value)) return value.trim();
        }
        return null;
    }

    private void fillTitleFallbackIfNeeded(AIParseResult result, String fileName) {
        if (result == null) return;
        if (notBlank(result.getTitle())) return;
        // 仅当已经解析出其他字段时兜底标题，避免“全空数据”被误判为成功
        if (!hasAnyFieldExcludingTitle(result)) return;

        String titleFromDesc = extractTitleFromDescription(result.getDescription());
        if (notBlank(titleFromDesc)) {
            result.setTitle(titleFromDesc);
            return;
        }

        String titleFromFileName = extractTitleFromFileName(fileName);
        if (notBlank(titleFromFileName)) {
            result.setTitle(titleFromFileName);
        }
    }

    private boolean hasAnyFieldExcludingTitle(AIParseResult r) {
        if (r == null) return false;
        return notBlank(r.getDescription())
                || notBlank(r.getOrganizer())
                || notBlank(r.getFormat())
                || notBlank(r.getScene())
                || notBlank(r.getVenue())
                || notBlank(r.getRegions())
                || notBlank(r.getCoverImage())
                || !r.getTags().isEmpty()
                || notBlank(r.getTargetAudience())
                || r.getStartTime() != null
                || r.getEndTime() != null
                || notBlank(r.getScheduleDaysJson());
    }

    private String extractTitleFromDescription(String description) {
        if (!notBlank(description)) return null;
        String d = description.trim().replace('\n', ' ').replace('\r', ' ');
        String[] separators = new String[]{"。", "！", "？", ".", "!", "?", "；", ";", "，", ","};
        int end = d.length();
        for (String separator : separators) {
            int idx = d.indexOf(separator);
            if (idx > 0 && idx < end) end = idx;
        }
        String candidate = d.substring(0, Math.min(end, 30)).trim();
        return candidate.isEmpty() ? null : candidate;
    }

    private String extractTitleFromFileName(String fileName) {
        if (!notBlank(fileName)) return null;
        String name = fileName.trim();
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0 && slash + 1 < name.length()) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }
        return name.trim();
    }
}
