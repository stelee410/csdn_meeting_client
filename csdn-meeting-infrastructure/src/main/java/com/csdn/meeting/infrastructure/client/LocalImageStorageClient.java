package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.ImageStoragePort;
import com.csdn.meeting.infrastructure.config.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 图片存储的本地磁盘实现。
 * 按 年/月/日 分目录存储，文件名使用 UUID 避免冲突。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalImageStorageClient implements ImageStoragePort {

    private static final DateTimeFormatter DATE_PATH_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final ImageStorageProperties properties;

    @PostConstruct
    public void init() {
        try {
            Path baseDir = Paths.get(properties.getBasePath());
            Files.createDirectories(baseDir);
            log.info("[图片存储] 本地存储目录已就绪: {}", baseDir.toAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("无法创建图片存储目录: " + properties.getBasePath(), e);
        }
    }

    @Override
    public String store(byte[] imageBytes, String originalFileName) {
        String ext = extractExtension(originalFileName);
        String datePath = LocalDate.now().format(DATE_PATH_FORMAT);
        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        Path targetDir = Paths.get(properties.getBasePath(), datePath);
        Path targetFile = targetDir.resolve(uniqueFileName);

        try {
            Files.createDirectories(targetDir);
            Files.write(targetFile, imageBytes);
            log.info("[图片存储] 图片已保存: {}", targetFile.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("图片写入磁盘失败: " + targetFile, e);
        }

        // 拼接可访问 URL：前缀 + /年/月/日/文件名
        String accessUrlPrefix = properties.getAccessUrlPrefix();
        if (accessUrlPrefix.endsWith("/")) {
            accessUrlPrefix = accessUrlPrefix.substring(0, accessUrlPrefix.length() - 1);
        }
        return accessUrlPrefix + "/" + datePath + "/" + uniqueFileName;
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex >= 0 ? fileName.substring(dotIndex + 1).toLowerCase() : "jpg";
    }
}
