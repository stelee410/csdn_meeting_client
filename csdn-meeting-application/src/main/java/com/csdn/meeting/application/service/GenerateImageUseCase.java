package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.GenerateImageDTO;
import com.csdn.meeting.application.dto.GenerateImageRequest;
import com.csdn.meeting.domain.port.ImageGeneratePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AI 生成会议背景图用例：调用 ImageGeneratePort 生成封面图，异常时优雅降级。
 */
@Service
public class GenerateImageUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateImageUseCase.class);

    private final ImageGeneratePort imageGeneratePort;

    public GenerateImageUseCase(ImageGeneratePort imageGeneratePort) {
        this.imageGeneratePort = imageGeneratePort;
    }

    /**
     * 根据会议标题和简介生成背景图。
     *
     * @param request 请求体
     * @return 包含图片 URL 的 DTO；失败时返回空 imageUrl
     */
    public GenerateImageDTO generate(GenerateImageRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return new GenerateImageDTO("");
        }
        try {
            String imageUrl = imageGeneratePort.generate(
                    request.getTitle().trim(),
                    request.getDescription() != null ? request.getDescription().trim() : "");
            return new GenerateImageDTO(imageUrl != null ? imageUrl : "");
        } catch (Exception e) {
            log.warn("[GenerateImageUseCase] 图片生成失败，title={}: {}", request.getTitle(), e.getMessage());
            return new GenerateImageDTO("");
        }
    }
}
