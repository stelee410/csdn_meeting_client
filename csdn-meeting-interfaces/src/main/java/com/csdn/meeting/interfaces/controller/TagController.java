package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.TagDTO;
import com.csdn.meeting.application.service.HotTagsUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签接口
 * issue001-9：热门标签
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签接口")
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final HotTagsUseCase hotTagsUseCase;

    public TagController(HotTagsUseCase hotTagsUseCase) {
        this.hotTagsUseCase = hotTagsUseCase;
    }

    @Operation(summary = "热门标签", description = "按使用该标签的已发布会议数量降序，返回热门标签列表")
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getHotTags(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(ApiResponse.success(hotTagsUseCase.getHotTags(limit)));
    }
}
