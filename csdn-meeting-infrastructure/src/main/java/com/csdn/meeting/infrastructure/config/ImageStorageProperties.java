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
     * 对外暴露的 URL 前缀，拼接相对路径后为完整访问 URL。
     * <ul>
     *   <li>完整 URL：{@code http://host:8080/uploads/images}</li>
     *   <li>仅端口+路径（host/scheme 随当前 HTTP 请求）：{@code :8080/uploads/images/}，
     *       无请求上下文时回退为 {@code http://localhost:8080} + 该路径</li>
     * </ul>
     */
    private String accessUrlPrefix = "http://localhost:8080/uploads/images";
}
