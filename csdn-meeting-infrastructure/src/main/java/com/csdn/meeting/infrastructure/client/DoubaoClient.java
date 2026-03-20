package com.csdn.meeting.infrastructure.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.csdn.meeting.infrastructure.config.DoubaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * 豆包大模型通用 HTTP 客户端。
 * 封装 /api/v3/responses 端点的请求构建与响应解析，
 * 供 AIServiceClient、NLPTagClient、DescriptionGenerateClient 等共享使用。
 */
@Component
public class DoubaoClient {

    private static final Logger log = LoggerFactory.getLogger(DoubaoClient.class);

    private final DoubaoProperties properties;

    public DoubaoClient(DoubaoProperties properties) {
        this.properties = properties;
    }

    /**
     * 纯文本调用：向豆包发送文本 prompt，返回模型回复文本。
     *
     * @param prompt 用户 prompt
     * @return 模型回复文本；失败时抛出 RuntimeException
     */
    public String callText(String prompt) {
        JSONObject contentItem = new JSONObject();
        contentItem.put("type", "input_text");
        contentItem.put("text", prompt);

        return doCall(buildRequest(contentItem));
    }

    /**
     * 图片 + 文本调用：将图片以 base64 data URL 方式传入，附带 prompt。
     *
     * @param imageBytes 图片字节
     * @param mimeType   MIME 类型，如 "image/jpeg"
     * @param prompt     文本 prompt
     * @return 模型回复文本；失败时抛出 RuntimeException
     */
    public String callWithImage(byte[] imageBytes, String mimeType, String prompt) {
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        String dataUrl = "data:" + mimeType + ";base64," + base64;

        JSONObject imageItem = new JSONObject();
        imageItem.put("type", "input_image");
        imageItem.put("image_url", dataUrl);

        JSONObject textItem = new JSONObject();
        textItem.put("type", "input_text");
        textItem.put("text", prompt);

        JSONArray content = new JSONArray();
        content.add(imageItem);
        content.add(textItem);

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", content);

        JSONArray input = new JSONArray();
        input.add(message);

        JSONObject body = new JSONObject();
        body.put("model", properties.getModel());
        body.put("input", input);

        return doCall(body);
    }

    // ---- private helpers ----

    private JSONObject buildRequest(JSONObject contentItem) {
        JSONArray content = new JSONArray();
        content.add(contentItem);

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", content);

        JSONArray input = new JSONArray();
        input.add(message);

        JSONObject body = new JSONObject();
        body.put("model", properties.getModel());
        body.put("input", input);
        return body;
    }

    private String doCall(JSONObject requestBody) {
        String jsonBody = requestBody.toJSONString();
        log.debug("[Doubao] request model={} body_len={}", properties.getModel(), jsonBody.length());

        HttpResponse response = HttpRequest.post(properties.getBaseUrl())
                .header("Authorization", "Bearer " + properties.getApiKey())
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .timeout(properties.getTimeoutMs())
                .execute();

        String responseBody = response.body();
        log.debug("[Doubao] response status={} body_len={}", response.getStatus(), responseBody.length());

        if (!response.isOk()) {
            throw new RuntimeException("豆包 API 请求失败，HTTP " + response.getStatus() + ": " + responseBody);
        }

        return extractText(responseBody);
    }

    /**
     * 从豆包 /api/v3/responses 响应体中提取模型回复文本。
     * 响应结构：{ "output": [ { "type": "message", "content": [ { "type": "output_text", "text": "..." } ] } ] }
     */
    private String extractText(String responseBody) {
        try {
            JSONObject root = JSON.parseObject(responseBody);

            // /api/v3/responses 格式
            JSONArray output = root.getJSONArray("output");
            if (output != null && !output.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < output.size(); i++) {
                    JSONObject outItem = output.getJSONObject(i);
                    if (outItem == null) continue;
                    JSONArray content = outItem.getJSONArray("content");
                    if (content != null && !content.isEmpty()) {
                        for (int j = 0; j < content.size(); j++) {
                            JSONObject item = content.getJSONObject(j);
                            if (item == null) continue;
                            if ("output_text".equals(item.getString("type"))) {
                                String text = item.getString("text");
                                if (text != null && !text.trim().isEmpty()) {
                                    if (sb.length() > 0) sb.append('\n');
                                    sb.append(text.trim());
                                }
                            }
                        }
                    }
                    // 部分格式直接有 text 字段
                    String directText = outItem.getString("text");
                    if (directText != null && !directText.trim().isEmpty()) {
                        if (sb.length() > 0) sb.append('\n');
                        sb.append(directText.trim());
                    }
                }
                if (sb.length() > 0) {
                    return sb.toString();
                }
            }

            // 兼容 chat completions 格式
            JSONArray choicesArr = root.getJSONArray("choices");
            if (choicesArr != null && !choicesArr.isEmpty()) {
                JSONObject firstChoice = choicesArr.getJSONObject(0);
                if (firstChoice != null) {
                    JSONObject message = firstChoice.getJSONObject("message");
                    if (message != null) {
                        String content = message.getString("content");
                        if (content != null) return content;
                    }
                }
            }

            log.warn("[Doubao] 无法从响应中提取文本，原始响应: {}", responseBody);
            return "";
        } catch (Exception e) {
            log.error("[Doubao] 解析响应失败: {}", e.getMessage(), e);
            throw new RuntimeException("豆包响应解析失败: " + e.getMessage(), e);
        }
    }
}
