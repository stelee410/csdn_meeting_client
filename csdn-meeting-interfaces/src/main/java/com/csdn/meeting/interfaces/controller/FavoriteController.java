package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.service.MyMeetingsUseCase;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.MeetingFavorite;
import com.csdn.meeting.domain.repository.MeetingFavoriteRepository;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 收藏控制器
 * 提供会议收藏/取消收藏、查询收藏状态等API
 *
 * 【已改造】用户身份从JWT Token中获取（通过LoginInterceptor设置到request attribute）
 */
@Tag(name = "收藏接口", description = "会议收藏相关接口")
@RestController
@RequestMapping("/api/meetings/{meetingId}/favorite")
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    private final MeetingFavoriteRepository favoriteRepository;
    private final MeetingRepository meetingRepository;
    private final MyMeetingsUseCase myMeetingsUseCase;

    public FavoriteController(MeetingFavoriteRepository favoriteRepository,
                              MeetingRepository meetingRepository,
                              MyMeetingsUseCase myMeetingsUseCase) {
        this.favoriteRepository = favoriteRepository;
        this.meetingRepository = meetingRepository;
        this.myMeetingsUseCase = myMeetingsUseCase;
    }

    /**
     * 收藏/取消收藏（Toggle接口）
     * 切换收藏状态，返回当前状态
     */
    @Operation(
            summary = "收藏/取消收藏",
            description = "切换会议收藏状态。如果已收藏则取消收藏，如果未收藏则添加收藏。返回当前收藏状态。"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteResultDTO>> toggleFavorite(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);

        // 查找会议
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 检查当前收藏状态
        boolean currentlyFavorited = favoriteRepository.existsByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());

        if (currentlyFavorited) {
            // 取消收藏
            favoriteRepository.deleteByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());
            logger.info("用户 {} 取消收藏会议 {}", userId, meetingId);

            FavoriteResultDTO result = new FavoriteResultDTO();
            result.setMeetingId(meetingId);
            result.setUserId(Long.valueOf(userId));
            result.setFavorited(false);
            result.setMessage("已取消收藏");
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            // 添加收藏
            MeetingFavorite favorite = new MeetingFavorite();
            favorite.setUserId(Long.valueOf(userId));
            favorite.setMeetingId(meeting.getId());
            favorite.setCreatedAt(LocalDateTime.now());
            favoriteRepository.save(favorite);
            logger.info("用户 {} 收藏会议 {}", userId, meetingId);

            FavoriteResultDTO result = new FavoriteResultDTO();
            result.setMeetingId(meetingId);
            result.setUserId(Long.valueOf(userId));
            result.setFavorited(true);
            result.setMessage("收藏成功");
            return ResponseEntity.ok(ApiResponse.success(result));
        }
    }

    /**
     * 查询收藏状态
     */
    @Operation(
            summary = "查询收藏状态",
            description = "查询当前用户是否收藏了指定会议。"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);

        Meeting meeting = meetingRepository.findByMeetingId(meetingId).orElse(null);
        if (meeting == null) {
            return ResponseEntity.ok(ApiResponse.success(false));
        }

        boolean isFavorited = favoriteRepository.existsByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());
        return ResponseEntity.ok(ApiResponse.success(isFavorited));
    }

    /**
     * 添加收藏（显式接口，已收藏则报错）
     */
    @Operation(
            summary = "添加收藏",
            description = "添加会议收藏。如果已收藏则返回错误。"
    )
    @PutMapping
    public ResponseEntity<ApiResponse<FavoriteResultDTO>> addFavorite(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);

        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 检查是否已收藏
        boolean currentlyFavorited = favoriteRepository.existsByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());
        if (currentlyFavorited) {
            throw new IllegalStateException("您已收藏该会议，请勿重复收藏");
        }

        // 添加收藏
        MeetingFavorite favorite = new MeetingFavorite();
        favorite.setUserId(Long.valueOf(userId));
        favorite.setMeetingId(meeting.getId());
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
        logger.info("用户 {} 添加收藏会议 {}", userId, meetingId);

        FavoriteResultDTO result = new FavoriteResultDTO();
        result.setMeetingId(meetingId);
        result.setUserId(Long.valueOf(userId));
        result.setFavorited(true);
        result.setMessage("收藏成功");
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 取消收藏（显式接口，未收藏则报错）
     */
    @Operation(
            summary = "取消收藏",
            description = "取消会议收藏。如果未收藏则返回错误。"
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse<FavoriteResultDTO>> removeFavorite(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);

        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        // 检查是否已收藏
        boolean currentlyFavorited = favoriteRepository.existsByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());
        if (!currentlyFavorited) {
            throw new IllegalStateException("您未收藏该会议");
        }

        // 取消收藏
        favoriteRepository.deleteByUserIdAndMeetingId(Long.valueOf(userId), meeting.getId());
        logger.info("用户 {} 取消收藏会议 {}", userId, meetingId);

        FavoriteResultDTO result = new FavoriteResultDTO();
        result.setMeetingId(meetingId);
        result.setUserId(Long.valueOf(userId));
        result.setFavorited(false);
        result.setMessage("已取消收藏");
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 从请求中获取当前用户ID（由LoginInterceptor设置）
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        throw new RuntimeException("用户未登录");
    }

    /**
     * 收藏操作结果DTO
     */
    public static class FavoriteResultDTO {
        private String meetingId;
        private Long userId;
        private boolean favorited;
        private String message;

        // Getters and Setters
        public String getMeetingId() { return meetingId; }
        public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public boolean isFavorited() { return favorited; }
        public void setFavorited(boolean favorited) { this.favorited = favorited; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
