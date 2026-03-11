package com.csdn.meeting.config;

import com.csdn.meeting.infrastructure.config.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 将本地磁盘上传目录映射为可通过 HTTP 直接访问的静态资源路径。
 * 访问路径示例：GET /uploads/images/2024/01/15/abc123.jpg
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ImageStorageProperties imageStorageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = imageStorageProperties.getBasePath();
        // 转为绝对路径，确保 file: 协议前缀正确
        String absolutePath = Paths.get(basePath).toAbsolutePath().toString();
        if (!absolutePath.endsWith("/")) {
            absolutePath = absolutePath + "/";
        }
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
