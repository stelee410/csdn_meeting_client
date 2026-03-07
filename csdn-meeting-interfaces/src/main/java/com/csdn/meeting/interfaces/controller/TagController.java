package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.TagDTO;
import com.csdn.meeting.application.dto.TagListResponseDTO;
import com.csdn.meeting.application.service.TagAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签控制器
 * 提供标签列表查询、会议标签查询等功能
 */
@Slf4j
@Api(tags = "标签接口")
@RestController
@RequestMapping("/api")
public class TagController {

    private final TagAppService tagAppService;

    public TagController(TagAppService tagAppService) {
        this.tagAppService = tagAppService;
    }

    @ApiOperation(value = "获取所有标签", notes = "获取系统中所有可用的标签列表，按分类分组")
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<TagListResponseDTO>> getAllTags() {
        TagListResponseDTO result = tagAppService.getAllTags();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @ApiOperation(value = "获取会议标签", notes = "获取指定会议关联的标签列表")
    @ApiImplicitParam(name = "meetingId", value = "会议ID", required = true, dataTypeClass = String.class, paramType = "path")
    @GetMapping("/meetings/{meetingId}/tags")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getMeetingTags(
            @PathVariable String meetingId) {
        List<TagDTO> tags = tagAppService.getTagsByMeetingId(meetingId);
        return ResponseEntity.ok(ApiResponse.success(tags));
    }
}
