package com.csdn.meeting.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "upload.image")
public class ImageStorageProperties {

    /**
     * 图片保存的本地磁盘目录，例如 /data/uploads/images
     * 支持相对路径（相对于启动目录）和绝对路径
     */
    private String basePath = "./uploads/images";

    /**
     * 对外暴露的 URL 前缀，例如 http://localhost:8080/uploads/images
     * 拼接文件名后即为完整访问 URL
     */
    private String accessUrlPrefix = "http://localhost:8080/uploads/images";
}
