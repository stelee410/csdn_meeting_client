package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.GenerateDescriptionDTO;
import com.csdn.meeting.application.dto.GenerateDescriptionRequest;
import com.csdn.meeting.domain.port.DescriptionGeneratePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * AI 生成会议简介用例：调用 DescriptionGeneratePort 生成简介，异常时优雅降级。
 */
@Service
public class GenerateDescriptionUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateDescriptionUseCase.class);

    private final DescriptionGeneratePort descriptionGeneratePort;

    public GenerateDescriptionUseCase(DescriptionGeneratePort descriptionGeneratePort) {
        this.descriptionGeneratePort = descriptionGeneratePort;
    }

    /**
     * 根据标题和标签生成会议简介。
     *
     * @param request 请求体，包含 title 和可选的 tags
     * @return 包含生成简介的 DTO；失败时返回空 description 而非抛出异常
     */
    public GenerateDescriptionDTO generate(GenerateDescriptionRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return new GenerateDescriptionDTO("");
        }
        List<String> tags = request.getTags() != null ? request.getTags() : Collections.emptyList();
        try {
            String description = descriptionGeneratePort.generate(request.getTitle().trim(), tags);
            return new GenerateDescriptionDTO(description != null ? description.trim() : "");
        } catch (Exception e) {
            log.warn("[GenerateDescriptionUseCase] 简介生成失败，title={}: {}", request.getTitle(), e.getMessage());
            return new GenerateDescriptionDTO("");
        }
    }
}
