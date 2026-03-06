package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.AIParseResultDTO;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.exception.AIParseException;
import com.csdn.meeting.application.exception.BusinessException;
import com.csdn.meeting.domain.port.AIParsePort;
import com.csdn.meeting.domain.port.AIParseResult;
import com.csdn.meeting.domain.port.SensitiveWordFilterPort;
import com.csdn.meeting.domain.port.VirusScanPort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AI 解析用例：编排 文件上传 → 病毒扫描 → 文本提取 → LLM 解析 → 敏感词过滤。
 * 15 秒超时，超时或服务不可用时抛出 AIParseException (422)。
 */
@Service
public class AIParsingUseCase {

    private static final int DEFAULT_PARSE_TIMEOUT_SECONDS = 15;

    private final VirusScanPort virusScanPort;
    private final AIParsePort aiParsePort;
    private final SensitiveWordFilterPort sensitiveWordFilterPort;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final int parseTimeoutSeconds;

    public AIParsingUseCase(VirusScanPort virusScanPort,
                            AIParsePort aiParsePort,
                            SensitiveWordFilterPort sensitiveWordFilterPort) {
        this(virusScanPort, aiParsePort, sensitiveWordFilterPort, DEFAULT_PARSE_TIMEOUT_SECONDS);
    }

    AIParsingUseCase(VirusScanPort virusScanPort,
                     AIParsePort aiParsePort,
                     SensitiveWordFilterPort sensitiveWordFilterPort,
                     int parseTimeoutSeconds) {
        this.virusScanPort = virusScanPort;
        this.aiParsePort = aiParsePort;
        this.sensitiveWordFilterPort = sensitiveWordFilterPort;
        this.parseTimeoutSeconds = parseTimeoutSeconds;
    }

    /**
     * 解析上传文件，返回 AI 解析结果 DTO。
     *
     * @param fileBytes 文件内容
     * @param fileName  文件名
     * @return 解析结果，含 traceId、filledFields、sensitiveFields、data
     * @throws BusinessException 病毒扫描失败，400
     * @throws AIParseException  超时或 LLM 不可用，422
     */
    public AIParseResultDTO parse(byte[] fileBytes, String fileName) {
        String traceId = UUID.randomUUID().toString();

        // 1. 病毒扫描
        try {
            virusScanPort.scan(fileBytes, fileName);
        } catch (Exception e) {
            throw new BusinessException(400, "文件病毒扫描失败: " + e.getMessage(), e);
        }

        // 2. LLM 解析（15s 超时）
        AIParseResult parseResult;
        try {
            Future<AIParseResult> future = executor.submit(new Callable<AIParseResult>() {
                @Override
                public AIParseResult call() {
                    return aiParsePort.parse(fileBytes, fileName);
                }
            });
            parseResult = future.get(parseTimeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new AIParseException("AI 解析超时(" + parseTimeoutSeconds + "秒)");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AIParseException) {
                throw (AIParseException) cause;
            }
            throw new AIParseException("AI 解析失败: " + (cause != null ? cause.getMessage() : e.getMessage()), cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AIParseException("AI 解析被中断", e);
        }

        // 3. 敏感词过滤
        List<String> sensitiveFields = sensitiveWordFilterPort.findSensitiveFields(parseResult);

        // 4. 构建 DTO
        AIParseResultDTO dto = new AIParseResultDTO();
        dto.setTraceId(traceId);
        dto.setFilledFields(computeFilledFields(parseResult));
        dto.setSensitiveFields(sensitiveFields);
        dto.setData(toMeetingDTO(parseResult));
        return dto;
    }

    private List<String> computeFilledFields(AIParseResult r) {
        List<String> filled = new ArrayList<>();
        if (isFilled(r.getTitle())) filled.add("title");
        if (isFilled(r.getDescription())) filled.add("description");
        if (isFilled(r.getOrganizer())) filled.add("organizer");
        if (isFilled(r.getFormat())) filled.add("format");
        if (isFilled(r.getScene())) filled.add("scene");
        if (isFilled(r.getVenue())) filled.add("venue");
        if (isFilled(r.getRegions())) filled.add("regions");
        if (isFilled(r.getCoverImage())) filled.add("coverImage");
        if (r.getTags() != null && !r.getTags().isEmpty()) filled.add("tags");
        if (isFilled(r.getTargetAudience())) filled.add("targetAudience");
        if (r.getStartTime() != null) filled.add("startTime");
        if (r.getEndTime() != null) filled.add("endTime");
        if (isFilled(r.getScheduleDaysJson())) filled.add("scheduleDaysJson");
        return filled;
    }

    private boolean isFilled(String s) {
        return s != null && !s.isBlank();
    }

    private MeetingDTO toMeetingDTO(AIParseResult r) {
        MeetingDTO dto = new MeetingDTO();
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setOrganizer(r.getOrganizer());
        dto.setFormat(r.getFormat());
        dto.setScene(r.getScene());
        dto.setVenue(r.getVenue());
        dto.setRegions(r.getRegions());
        dto.setCoverImage(r.getCoverImage());
        if (r.getTags() != null && !r.getTags().isEmpty()) {
            dto.setTags(String.join(",", r.getTags()));
        }
        dto.setTargetAudience(r.getTargetAudience());
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        // scheduleDaysJson -> scheduleDays 需 JSON 解析，此处暂留空；真实实现可用 ObjectMapper
        dto.setScheduleDays(Collections.emptyList());
        return dto;
    }
}
