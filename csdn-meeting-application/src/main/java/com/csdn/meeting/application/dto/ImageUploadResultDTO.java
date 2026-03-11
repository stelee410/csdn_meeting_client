package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "图片上传结果")
public class ImageUploadResultDTO {

    @Schema(description = "图片可访问 URL，可直接赋值给 coverImage / posterUrl 等字段",
            example = "http://localhost:8080/uploads/images/2024/03/11/abc123def456.png")
    private String url;

    @Schema(description = "服务端保存的文件名（含扩展名）",
            example = "abc123def456.png")
    private String fileName;

    @Schema(description = "文件大小，单位字节",
            example = "204800")
    private long size;
}
