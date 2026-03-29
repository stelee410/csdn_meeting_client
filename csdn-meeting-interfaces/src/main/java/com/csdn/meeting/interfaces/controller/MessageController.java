package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.domain.entity.UserMessage;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.UserMessageRepository;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import com.csdn.meeting.interfaces.vo.MessagePageResult;
import com.csdn.meeting.interfaces.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息中心控制器
 * 提供用户站内信消息的查询、标记已读、删除等操作
 */
@Slf4j
@Tag(name = "消息中心接口", description = "用户站内信消息管理")
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final UserMessageRepository userMessageRepository;

    public MessageController(UserMessageRepository userMessageRepository) {
        this.userMessageRepository = userMessageRepository;
    }

    /**
     * 获取当前用户ID（从请求属性中获取，由LoginInterceptor设置）
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        throw new RuntimeException("用户未登录");
    }

    /**
     * 将领域实体转换为VO
     */
    private MessageVO toVO(UserMessage entity) {
        if (entity == null) {
            return null;
        }
        MessageVO vo = new MessageVO();
        vo.setMessageId(entity.getMessageId());
        vo.setMessageType(entity.getMessageType() != null ? entity.getMessageType().getCode() : null);
        vo.setMessageTypeDesc(entity.getMessageType() != null ? entity.getMessageType().getDesc() : null);
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setBizId(entity.getBizId());
        vo.setBizType(entity.getBizType());
        vo.setExtraData(entity.getExtraData());
        vo.setIsRead(entity.getIsRead());
        vo.setReadTime(entity.getReadTime());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    @Operation(
            summary = "获取用户消息列表",
            description = "分页获取当前用户的站内信消息列表，支持筛选未读消息"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<MessagePageResult>> listMessages(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "是否只查询未读消息", example = "false")
            @RequestParam(required = false) Boolean unreadOnly,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        log.debug("获取用户消息列表: userId={}, page={}, size={}, unreadOnly={}", userId, page, size, unreadOnly);

        PageResult<UserMessage> pageResult;
        if (Boolean.TRUE.equals(unreadOnly)) {
            pageResult = userMessageRepository.findUnreadByUserId(userId, page, size);
        } else {
            pageResult = userMessageRepository.findByUserId(userId, page, size);
        }

        // 转换为VO
        List<MessageVO> voList = pageResult.getContent().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        MessagePageResult result = new MessagePageResult();
        result.setList(voList);
        result.setTotal(pageResult.getTotalElements());
        result.setPage(page);
        result.setSize(size);
        result.setTotalPages(pageResult.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(
            summary = "获取未读消息数",
            description = "获取当前用户的未读消息数量，用于页面红点提醒"
    )
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        long count = userMessageRepository.countUnreadByUserId(userId);
        log.debug("获取用户未读消息数: userId={}, count={}", userId, count);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Operation(
            summary = "标记消息已读",
            description = "将指定消息标记为已读状态"
    )
    @PostMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "消息ID", required = true, example = "MSG202603290001")
            @PathVariable String messageId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        log.info("标记消息已读: userId={}, messageId={}", userId, messageId);
        userMessageRepository.markAsRead(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "标记全部消息已读",
            description = "将当前用户的所有未读消息标记为已读"
    )
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        log.info("标记全部消息已读: userId={}", userId);
        userMessageRepository.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "删除消息",
            description = "软删除指定消息（仅标记删除，不物理删除）"
    )
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @Parameter(description = "消息ID", required = true, example = "MSG202603290001")
            @PathVariable String messageId,
            HttpServletRequest request) {

        String userId = getCurrentUserId(request);
        log.info("删除消息: userId={}, messageId={}", userId, messageId);
        userMessageRepository.deleteById(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
