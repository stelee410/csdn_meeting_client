package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.*;
import com.csdn.meeting.application.service.analytics.AnalyticsService;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议控制器（含埋点示例）
 * 展示如何在Controller中集成埋点服务
 *
 * 使用说明：
 * 1. 注入 AnalyticsService 埋点服务
 * 2. 在关键业务操作后调用埋点方法
 * 3. 埋点采用异步方式，不影响主业务流程性能
 */
@Tag(name = "会议接口（含埋点示例）")
@RestController
@RequestMapping("/api/meetings")
public class MeetingControllerWithAnalytics {

    private final MeetingApplicationService meetingApplicationService;
    private final MeetingListUseCase meetingListUseCase;
    private final MeetingDetailPageUseCase meetingDetailPageUseCase;
    private final MyMeetingsUseCase myMeetingsUseCase;
    private final AnalyticsService analyticsService; // 埋点服务

    public MeetingControllerWithAnalytics(
            MeetingApplicationService meetingApplicationService,
            MeetingListUseCase meetingListUseCase,
            MeetingDetailPageUseCase meetingDetailPageUseCase,
            MyMeetingsUseCase myMeetingsUseCase,
            AnalyticsService analyticsService) {
        this.meetingApplicationService = meetingApplicationService;
        this.meetingListUseCase = meetingListUseCase;
        this.meetingDetailPageUseCase = meetingDetailPageUseCase;
        this.myMeetingsUseCase = myMeetingsUseCase;
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "创建草稿（带埋点）")
    @PostMapping("/with-analytics")
    public ResponseEntity<MeetingDTO> createDraftWithAnalytics(@RequestBody CreateMeetingCommand command,
                                                                  @RequestParam String userId) {
        MeetingDTO meeting = meetingApplicationService.createDraft(command);

        // 埋点：记录会议创建
        analyticsService.trackMeetingCreate(userId, meeting.getMeetingId(), meeting.getMeetingType());

        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }

    @Operation(summary = "提交审核（带埋点）")
    @PostMapping("/{id}/submit-with-analytics")
    public ResponseEntity<MeetingDTO> submitWithAnalytics(@PathVariable Long id,
                                                         @RequestParam String userId) {
        MeetingDTO meeting = meetingApplicationService.submit(String.valueOf(id));

        // 埋点：记录会议提交审核
        analyticsService.trackMeetingSubmit(userId, meeting.getMeetingId());

        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "审核通过（带埋点）")
    @PostMapping("/{id}/approve-with-analytics")
    public ResponseEntity<MeetingDTO> approveWithAnalytics(@PathVariable Long id,
                                                          @RequestParam String operatorId,
                                                          @RequestParam String operatorName) {
        MeetingDTO meeting = meetingApplicationService.approve(String.valueOf(id));

        // 埋点：记录审核通过
        analyticsService.trackMeetingAuditApprove(
                operatorId,
                operatorName,
                meeting.getMeetingId(),
                meeting.getTitle(),
                meeting.getOrganizerId() != null ? String.valueOf(meeting.getOrganizerId()) : null
        );

        // 埋点：记录会议发布
        if (meeting.getCreatorId() != null) {
            analyticsService.trackMeetingPublish(
                    meeting.getCreatorId(),
                    meeting.getMeetingId(),
                    meeting.getOrganizerId() != null ? String.valueOf(meeting.getOrganizerId()) : null
            );
        }

        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "审核拒绝（带埋点）")
    @PostMapping("/{id}/reject-with-analytics")
    public ResponseEntity<MeetingDTO> rejectWithAnalytics(@PathVariable Long id,
                                                           @RequestBody ReasonRequest request,
                                                           @RequestParam String operatorId,
                                                           @RequestParam String operatorName,
                                                           @RequestParam(required = false) List<String> violationTags) {
        String reason = request != null ? request.getReason() : null;
        MeetingDTO meeting = meetingApplicationService.reject(String.valueOf(id), reason);

        // 埋点：记录审核驳回
        analyticsService.trackMeetingAuditReject(
                operatorId,
                operatorName,
                meeting.getMeetingId(),
                meeting.getTitle(),
                meeting.getOrganizerId() != null ? String.valueOf(meeting.getOrganizerId()) : null,
                violationTags,
                reason
        );

        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "下架会议（带埋点）")
    @PostMapping("/{id}/takedown-with-analytics")
    public ResponseEntity<MeetingDTO> takedownWithAnalytics(@PathVariable Long id,
                                                           @RequestBody ReasonRequest request,
                                                           @RequestParam String operatorId,
                                                           @RequestParam String operatorName,
                                                           @RequestParam String originalStatus,
                                                           @RequestParam(required = false) List<String> violationTags) {
        String reason = request != null ? request.getReason() : null;
        MeetingDTO meeting = meetingApplicationService.takedown(String.valueOf(id), reason);

        // 埋点：记录强制下架
        analyticsService.trackMeetingTakedown(
                operatorId,
                operatorName,
                meeting.getMeetingId(),
                meeting.getTitle(),
                meeting.getOrganizerId() != null ? String.valueOf(meeting.getOrganizerId()) : null,
                violationTags,
                reason,
                originalStatus
        );

        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "报名参会（带埋点）")
    @PostMapping("/{id}/join-with-analytics")
    public ResponseEntity<MeetingDTO> joinMeetingWithAnalytics(@PathVariable Long id,
                                                               @RequestBody JoinMeetingCommand command,
                                                               @RequestParam String userId) {
        command.setMeetingId(String.valueOf(id));
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);

        // 埋点：记录报名参会
        analyticsService.trackMeetingRegister(userId, meeting.getMeetingId());

        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "取消报名（带埋点）")
    @PostMapping("/{id}/leave-with-analytics")
    public ResponseEntity<Void> leaveMeetingWithAnalytics(@PathVariable Long id,
                                                           @RequestParam String userId) {
        meetingApplicationService.leaveMeeting(String.valueOf(id), Long.valueOf(userId));

        // 埋点：可以在这里添加取消报名埋点
        // analyticsService.trackMeetingCancelRegistration(userId, String.valueOf(id));

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "收藏会议（带埋点）")
    @PostMapping("/{id}/favorite-with-analytics")
    public ResponseEntity<Void> favoriteMeetingWithAnalytics(@PathVariable Long id,
                                                            @RequestParam String userId,
                                                            @RequestParam(defaultValue = "true") boolean isAdd) {
        // 假设有收藏相关的Service方法
        // meetingApplicationService.favoriteMeeting(String.valueOf(id), userId, isAdd);

        // 埋点：记录收藏操作
        analyticsService.trackMeetingFavorite(userId, String.valueOf(id), isAdd);

        return ResponseEntity.ok().build();
    }

    /**
     * 会议列表查询（带埋点）
     * 记录列表筛选条件和结果数量
     */
    @Operation(summary = "会议列表查询（带埋点）")
    @PostMapping("/list-with-analytics")
    public ResponseEntity<MeetingListResultDTO<MeetingCardItemDTO>> queryMeetingListWithAnalytics(
            @RequestBody MeetingListQueryDTO query,
            @RequestParam(required = false) String userId) {

        if (query.getSize() == 0) {
            query.setSize(20);
        }

        MeetingListResultDTO<MeetingCardItemDTO> result = meetingListUseCase.queryMeetingList(query);

        // 埋点：记录列表筛选（仅当用户已登录时）
        if (userId != null && !userId.isEmpty()) {
            analyticsService.trackMeetingListFilter(
                    userId,
                    query.getFormat() != null ? String.valueOf(query.getFormat()) : null,
                    query.getType() != null ? String.valueOf(query.getType()) : null,
                    query.getScene() != null ? String.valueOf(query.getScene()) : null,
                    query.getTimeRange() != null ? String.valueOf(query.getTimeRange()) : null,
                    query.getKeyword(),
                    (int) result.getTotal()
            );
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "会议详情页（带埋点）")
    @GetMapping("/{meetingId}/detail-page-with-analytics")
    public ResponseEntity<MeetingDetailPageDTO> getMeetingDetailPageWithAnalytics(
            @Parameter(description = "会议ID") @PathVariable String meetingId,
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId) {

        MeetingDetailPageDTO detailPage = meetingDetailPageUseCase.getMeetingDetailPage(meetingId,
                userId != null ? Long.valueOf(userId) : null);

        // 埋点：记录会议点击/浏览
        if (userId != null) {
            analyticsService.trackMeetingClick(userId, meetingId, "detail_page");
        }

        return ResponseEntity.ok(detailPage);
    }

    @Operation(summary = "我收藏的会议（带埋点）")
    @GetMapping("/my-favorites-with-analytics")
    public ResponseEntity<PagedResultDTO<MeetingDTO>> getMyFavoritesWithAnalytics(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResultDTO<MeetingDTO> result = PagedResultDTO.from(
                myMeetingsUseCase.getMyFavorites(Long.valueOf(userId), page, size));

        // 埋点：移动端收藏页签点击（如果来自移动端）
        // analyticsService.trackMobileFavoritesTabClick(userId);

        return ResponseEntity.ok(result);
    }

    // ============== 移动端埋点示例 ==============

    @Operation(summary = "移动端首页曝光（埋点）")
    @PostMapping("/mobile/home-exposure")
    public ResponseEntity<Void> trackMobileHomeExposure(@RequestParam String userId,
                                                         @RequestParam String source) {
        analyticsService.trackMobileHomeExposure(userId, source);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "移动端创建入口点击（埋点）")
    @PostMapping("/mobile/create-entry-click")
    public ResponseEntity<Void> trackMobileCreateEntryClick(@RequestParam String userId,
                                                              @RequestParam String source) {
        analyticsService.trackMobileCreateEntryClick(userId, source);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "移动端签到扫码（埋点）")
    @PostMapping("/mobile/checkin-scan")
    public ResponseEntity<Void> trackMobileCheckinScan(@RequestParam String userId,
                                                       @RequestParam String meetingId,
                                                       @RequestParam String result) {
        analyticsService.trackMobileCheckinScan(userId, meetingId, result);
        return ResponseEntity.ok().build();
    }
}
