package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.GenerateImageDTO;
import com.csdn.meeting.application.dto.GenerateImageRequest;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.port.ImageGeneratePort;
import com.csdn.meeting.domain.port.ImageStoragePort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 * AI 生成会议背景图用例。
 * <p>
 * 完整流程：
 * 1. 调用 AI 生成临时图片 URL
 * 2. 下载图片字节并写入本地磁盘，获得永久访问 URL
 * 3. 若请求携带 meetingId，更新该会议的 coverImage 字段
 * 4. 返回本地永久 URL
 */
@Service
public class GenerateImageUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateImageUseCase.class);

    private static final int DOWNLOAD_TIMEOUT_MS = 30_000;

    private final ImageGeneratePort imageGeneratePort;
    private final ImageStoragePort imageStoragePort;
    private final MeetingRepository meetingRepository;

    public GenerateImageUseCase(ImageGeneratePort imageGeneratePort,
                                ImageStoragePort imageStoragePort,
                                MeetingRepository meetingRepository) {
        this.imageGeneratePort = imageGeneratePort;
        this.imageStoragePort = imageStoragePort;
        this.meetingRepository = meetingRepository;
    }

    public GenerateImageDTO generate(GenerateImageRequest request) {
        if (request == null || request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return new GenerateImageDTO("");
        }

        String aiUrl;
        try {
            aiUrl = imageGeneratePort.generate(
                    request.getTitle().trim(),
                    request.getDescription() != null ? request.getDescription().trim() : "");
        } catch (Exception e) {
            log.warn("[GenerateImageUseCase] AI 图片生成失败，title={}: {}", request.getTitle(), e.getMessage());
            return new GenerateImageDTO("");
        }

        if (aiUrl == null || aiUrl.isEmpty()) {
            log.warn("[GenerateImageUseCase] AI 返回空 URL，title={}", request.getTitle());
            return new GenerateImageDTO("");
        }

        // 下载 AI 生成的临时图片，落入本地磁盘
        String localUrl;
        try {
            byte[] imageBytes = downloadImage(aiUrl);
            localUrl = imageStoragePort.store(imageBytes, "ai-generated.jpg");
            log.info("[GenerateImageUseCase] 图片已存入本地，localUrl={}", localUrl);
        } catch (Exception e) {
            log.warn("[GenerateImageUseCase] 图片下载或存储失败，降级返回 AI 原始 URL: {}", e.getMessage());
            // 存储失败时降级返回 AI 临时 URL，不阻断用户流程
            return new GenerateImageDTO(aiUrl);
        }

        // 若传入 meetingId，则自动更新会议封面图
        String meetingId = request.getMeetingId();
        if (meetingId != null && !meetingId.trim().isEmpty()) {
            try {
                Optional<Meeting> meetingOpt = meetingRepository.findByMeetingId(meetingId.trim());
                if (meetingOpt.isPresent()) {
                    Meeting meeting = meetingOpt.get();
                    meeting.setCoverImage(localUrl);
                    meetingRepository.save(meeting);
                    log.info("[GenerateImageUseCase] 会议封面图已更新，meetingId={}, url={}", meetingId, localUrl);
                } else {
                    log.warn("[GenerateImageUseCase] 未找到会议，meetingId={}", meetingId);
                }
            } catch (Exception e) {
                log.warn("[GenerateImageUseCase] 更新会议封面图失败，meetingId={}: {}", meetingId, e.getMessage());
            }
        }

        return new GenerateImageDTO(localUrl);
    }

    private byte[] downloadImage(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(DOWNLOAD_TIMEOUT_MS);
        connection.setReadTimeout(DOWNLOAD_TIMEOUT_MS);
        connection.setRequestProperty("User-Agent", "csdn-meeting-client/1.0");
        try (InputStream is = connection.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] chunk = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }
}
