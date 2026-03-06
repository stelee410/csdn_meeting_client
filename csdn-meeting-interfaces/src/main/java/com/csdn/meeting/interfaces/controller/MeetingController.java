package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.MeetingApplicationService;
import com.csdn.meeting.application.service.MeetingListAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 会议控制器
 * 提供会议管理、列表查询、筛选、搜索等功能
 */
@Api(tags = "会议接口")
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingApplicationService meetingApplicationService;
    private final MeetingListAppService meetingListAppService;

    public MeetingController(MeetingApplicationService meetingApplicationService,
                             MeetingListAppService meetingListAppService) {
        this.meetingApplicationService = meetingApplicationService;
        this.meetingListAppService = meetingListAppService;
    }

    /**
     * 获取当前用户ID（从请求头或Session中获取）
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null && !userIdStr.isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // ==================== 会议管理接口 ====================

    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody CreateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.createMeeting(command);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<MeetingDetailDTO>> getMeeting(
            @PathVariable String meetingId,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        MeetingDetailDTO detail = meetingListAppService.getMeetingDetail(meetingId, userId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingApplicationService.getAllMeetings();
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<MeetingDTO>> getMeetingsByCreator(@PathVariable Long creatorId) {
        List<MeetingDTO> meetings = meetingApplicationService.getMeetingsByCreator(creatorId);
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/{meetingId}/join")
    public ResponseEntity<MeetingDTO> joinMeeting(@PathVariable String meetingId, @RequestBody JoinMeetingCommand command) {
        command.setMeetingId(meetingId);
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);
        return ResponseEntity.ok(meeting);
    }

    @PostMapping("/{meetingId}/leave")
    public ResponseEntity<Void> leaveMeeting(@PathVariable String meetingId, @RequestParam Long userId) {
        meetingApplicationService.leaveMeeting(meetingId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/start")
    public ResponseEntity<Void> startMeeting(@PathVariable String meetingId) {
        meetingApplicationService.startMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/end")
    public ResponseEntity<Void> endMeeting(@PathVariable String meetingId) {
        meetingApplicationService.endMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/cancel")
    public ResponseEntity<Void> cancelMeeting(@PathVariable String meetingId) {
        meetingApplicationService.cancelMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    // ==================== 会议列表查询接口 ====================

    @ApiOperation(value = "查询会议列表", notes = "支持多维度筛选和分页，包括会议形式、类型、场景、时间范围等筛选条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "format", value = "会议形式(1:线上 2:线下 3:混合)", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "meetingType", value = "会议类型(1:技术峰会 2:技术沙龙 3:技术研讨会)", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "scene", value = "会议场景(1:开发者会议 2:产业会议 3:产品发布 4:区域营销 5:高校会议)", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "timeRange", value = "时间范围(this_week-本周, this_month-本月, next_3_months-未来三个月)", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "搜索关键词(标题/标签/主办方/城市)", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "viewType", value = "视图类型(card-卡片视图, list-列表视图)", dataTypeClass = String.class, paramType = "query", defaultValue = "card"),
            @ApiImplicitParam(name = "page", value = "页码", dataTypeClass = Integer.class, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小", dataTypeClass = Integer.class, paramType = "query", defaultValue = "10")
    })
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResult<MeetingListItemDTO>>> list(
            @Valid MeetingListQuery query,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        PageResult<MeetingListItemDTO> result = meetingListAppService.queryMeetingList(query, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @ApiOperation(value = "关键词搜索会议", notes = "根据关键词搜索会议，支持标题、标签、主办方、城市匹配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键词", required = true, dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "format", value = "会议形式", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "meetingType", value = "会议类型", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "scene", value = "会议场景", dataTypeClass = Integer.class, paramType = "query"),
            @ApiImplicitParam(name = "timeRange", value = "时间范围", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "viewType", value = "视图类型", dataTypeClass = String.class, paramType = "query", defaultValue = "card"),
            @ApiImplicitParam(name = "page", value = "页码", dataTypeClass = Integer.class, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页大小", dataTypeClass = Integer.class, paramType = "query", defaultValue = "10")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResult<MeetingListItemDTO>>> search(
            @RequestParam String keyword,
            @Valid MeetingListQuery query,
            HttpServletRequest request) {

        Long userId = getCurrentUserId(request);
        PageResult<MeetingListItemDTO> result = meetingListAppService.searchMeetings(keyword, query, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
