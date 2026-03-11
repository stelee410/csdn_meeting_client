package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.ImageUploadResultDTO;
import com.csdn.meeting.application.exception.BusinessException;
import com.csdn.meeting.domain.port.ImageStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageUploadUseCase {

    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp")));

    private final ImageStoragePort imageStoragePort;

    /**
     * 上传图片到本地磁盘，返回可访问 URL
     *
     * @param imageBytes       图片字节
     * @param originalFileName 原始文件名
     * @return 上传结果（含 URL、文件名、大小）
     */
    public ImageUploadResultDTO upload(byte[] imageBytes, String originalFileName) {
        validateSize(imageBytes);
        validateExtension(originalFileName);

        String url = imageStoragePort.store(imageBytes, originalFileName);
        String savedFileName = url.substring(url.lastIndexOf('/') + 1);
        return new ImageUploadResultDTO(url, savedFileName, imageBytes.length);
    }

    private void validateSize(byte[] imageBytes) {
        if (imageBytes.length > MAX_SIZE_BYTES) {
            throw new BusinessException(400, "图片大小不能超过 10MB，当前大小："
                    + (imageBytes.length / 1024 / 1024) + "MB");
        }
    }

    private void validateExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException(400, "无法识别文件格式，请上传 JPG/PNG/GIF/WEBP 图片");
        }
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(400, "不支持的图片格式：" + ext
                    + "，支持格式：JPG、PNG、GIF、WEBP");
        }
    }
}
