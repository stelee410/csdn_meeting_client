package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.ImageUploadResultDTO;
import com.csdn.meeting.application.service.ImageUploadUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "图片上传", description = "会议封面图、海报图等图片文件的上传管理")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadUseCase imageUploadUseCase;

    @Operation(
            summary = "上传图片",
            description = "将图片上传并保存到服务器本地磁盘。\n\n" +
                    "**支持格式**：JPG、PNG、GIF、WEBP\n\n" +
                    "**大小限制**：单文件不超过 10MB\n\n" +
                    "**返回值**：图片的可访问 URL，可直接赋值给创建/更新会议接口中的 `coverImage` 或 `posterUrl` 字段。\n\n" +
                    "**存储规则**：按 年/月/日 分目录存储，文件名使用 UUID 保证唯一性。"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "上传成功",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ImageUploadResultDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误（文件为空、格式不支持、超过大小限制）",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "服务器内部错误（磁盘写入失败等）",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResultDTO>> upload(
            @Parameter(description = "图片文件，支持 JPG/PNG/GIF/WEBP，大小不超过 10MB",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        byte[] bytes = file.getBytes();
        String originalFileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "image.jpg";

        ImageUploadResultDTO result = imageUploadUseCase.upload(bytes, originalFileName);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
