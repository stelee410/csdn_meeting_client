package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.domain.valueobject.DefaultAvatar;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 头像控制器
 * 提供系统默认头像列表查询
 */
@Slf4j
@Tag(name = "头像接口", description = "系统默认头像列表")
@RestController
@RequestMapping("/api/avatars")
public class AvatarController {

    @Operation(summary = "获取系统默认头像列表", 
               description = "获取系统预置的5个默认头像供用户选择注册时使用")
    @GetMapping("/defaults")
    public ResponseEntity<ApiResponse<List<AvatarVO>>> getDefaultAvatars() {
        List<AvatarVO> avatars = DefaultAvatar.getAllAvatars().stream()
                .map(avatar -> {
                    AvatarVO vo = new AvatarVO();
                    vo.setCode(avatar.getCode());
                    vo.setUrl(avatar.getUrl());
                    vo.setName(avatar.getDisplayName());
                    return vo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(avatars));
    }

    /**
     * 头像VO
     */
    @Data
    public static class AvatarVO {
        private int code;
        private String url;
        private String name;
    }
}
