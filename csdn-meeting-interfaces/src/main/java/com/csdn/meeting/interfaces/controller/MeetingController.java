package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.AIParseResultDTO;
import com.csdn.meeting.application.dto.CreateMeetingCommand;
import com.csdn.meeting.application.dto.JoinMeetingCommand;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.MeetingRightsDTO;
import com.csdn.meeting.application.dto.MeetingStatisticsDTO;
import com.csdn.meeting.application.dto.PagedResultDTO;
import com.csdn.meeting.application.dto.ReasonRequest;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.dto.RightsPurchaseResultDTO;
import com.csdn.meeting.application.dto.SuggestTagsRequest;
import com.csdn.meeting.application.dto.TagSuggestionDTO;
import com.csdn.meeting.application.dto.UpdateMeetingCommand;
import com.csdn.meeting.application.service.AIParsingUseCase;
import com.csdn.meeting.application.service.MeetingApplicationService;
import com.csdn.meeting.application.service.MeetingBriefUseCase;
import com.csdn.meeting.application.service.MeetingRightsPurchaseUseCase;
import com.csdn.meeting.application.service.MeetingStatisticsUseCase;
import com.csdn.meeting.application.service.MyMeetingsUseCase;
import com.csdn.meeting.application.service.TagSuggestionUseCase;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import io.swagger.annotations.Api;
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
 * 会议控制器
 * 提供会议管理、列表查询、筛选、搜索等功能
 */
@Api(tags = "会议接口")
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingApplicationService meetingApplicationService;
    private final TagSuggestionUseCase tagSuggestionUseCase;
    private final AIParsingUseCase aiParsingUseCase;
    private final MyMeetingsUseCase myMeetingsUseCase;
    private final MeetingStatisticsUseCase meetingStatisticsUseCase;
    private final MeetingRightsPurchaseUseCase meetingRightsPurchaseUseCase;
    private final MeetingBriefUseCase meetingBriefUseCase;

    public MeetingController(MeetingApplicationService meetingApplicationService,
                             TagSuggestionUseCase tagSuggestionUseCase,
                             AIParsingUseCase aiParsingUseCase,
                             MyMeetingsUseCase myMeetingsUseCase,
                             MeetingStatisticsUseCase meetingStatisticsUseCase,
                             MeetingRightsPurchaseUseCase meetingRightsPurchaseUseCase,
                             MeetingBriefUseCase meetingBriefUseCase) {
        this.meetingApplicationService = meetingApplicationService;
        this.tagSuggestionUseCase = tagSuggestionUseCase;
        this.aiParsingUseCase = aiParsingUseCase;
        this.myMeetingsUseCase = myMeetingsUseCase;
        this.meetingStatisticsUseCase = meetingStatisticsUseCase;
        this.meetingRightsPurchaseUseCase = meetingRightsPurchaseUseCase;
        this.meetingBriefUseCase = meetingBriefUseCase;
    }

    /** 创建草稿 */
    @PostMapping
    public ResponseEntity<MeetingDTO> createDraft(@RequestBody CreateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.createDraft(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }

    /** 更新会议（仅 DRAFT/REJECTED 可编辑） */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingDTO> update(@PathVariable Long id, @RequestBody UpdateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.update(String.valueOf(id), command);
        return ResponseEntity.ok(meeting);
    }

    /** 查询详情（含四级日程） */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getDetail(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.getMeetingDetailById(id);
        return ResponseEntity.ok(meeting);
    }

    /** AI 解析：上传文件，解析失败返回 422 */
    @PostMapping("/actions/ai-parse")
    public ResponseEntity<AIParseResultDTO> aiParse(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
            AIParseResultDTO result = aiParsingUseCase.parse(bytes, fileName);
            return ResponseEntity.ok(result);
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("读取文件失败: " + e.getMessage(), e);
        }
    }

    /** 标签推荐：根据 title + description 返回 3-5 个推荐标签 */
    @PostMapping("/actions/suggest-tags")
    public ResponseEntity<TagSuggestionDTO> suggestTags(@RequestBody SuggestTagsRequest request) {
        TagSuggestionDTO dto = tagSuggestionUseCase.suggestTags(
                request != null ? request.getTitle() : null,
                request != null ? request.getDescription() : null);
        return ResponseEntity.ok(dto);
    }

    /** 提交审核（日程不完整时返回 400/422） */
    @PostMapping("/{id}/submit")
    public ResponseEntity<MeetingDTO> submit(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.submit(String.valueOf(id));
        return ResponseEntity.ok(meeting);
    }

    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingApplicationService.getAllMeetings();
        return ResponseEntity.ok(meetings);
    }

    /** 我报名的会议：默认 PUBLISHED+IN_PROGRESS，includeEnded=true 时含 ENDED */
    @GetMapping("/my-registered")
    public ResponseEntity<PagedResultDTO<MeetingDTO>> getMyRegistered(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeEnded,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(PagedResultDTO.from(
                myMeetingsUseCase.getMyRegistered(userId, includeEnded, page, size)));
    }

    /** 我收藏的会议 */
    @GetMapping("/my-favorites")
    public ResponseEntity<PagedResultDTO<MeetingDTO>> getMyFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(PagedResultDTO.from(
                myMeetingsUseCase.getMyFavorites(userId, page, size)));
    }

    /** 我创建的会议：支持 status、startDate、endDate 筛选 */
    @GetMapping("/my-created")
    public ResponseEntity<PagedResultDTO<MeetingDTO>> getMyCreated(
            @RequestParam Long userId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Meeting.MeetingStatus> statuses = status == null || status.isEmpty()
                ? null
                : status.stream()
                .map(s -> {
                    try {
                        return Meeting.MeetingStatus.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(s -> s != null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(PagedResultDTO.from(
                myMeetingsUseCase.getMyCreated(userId, statuses, startDate, endDate, page, size)));
    }

    /** 会议报名列表：支持按 status 筛选 */
    @GetMapping("/{id}/registrations")
    public ResponseEntity<PagedResultDTO<RegistrationDTO>> getMeetingRegistrations(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Registration.RegistrationStatus regStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                regStatus = Registration.RegistrationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ResponseEntity.ok(PagedResultDTO.from(
                myMeetingsUseCase.getMeetingRegistrations(id, regStatus, page, size)));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<MeetingDTO>> getMeetingsByCreator(@PathVariable Long creatorId) {
        List<MeetingDTO> meetings = meetingApplicationService.getMeetingsByCreator(creatorId);
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<MeetingDTO> joinMeeting(@PathVariable Long id, @RequestBody JoinMeetingCommand command) {
        command.setMeetingId(String.valueOf(id));
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);
        return ResponseEntity.ok(meeting);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveMeeting(@PathVariable Long id, @RequestParam Long userId) {
        meetingApplicationService.leaveMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startMeeting(@PathVariable Long id) {
        meetingApplicationService.startMeeting(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<Void> endMeeting(@PathVariable Long id) {
        meetingApplicationService.endMeeting(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMeeting(@PathVariable Long id) {
        meetingApplicationService.cancelMeeting(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    /** 撤回审核：PENDING_REVIEW -> DRAFT */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<MeetingDTO> withdraw(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.withdraw(String.valueOf(id));
        return ResponseEntity.ok(meeting);
    }

    /** 审核通过：PENDING_REVIEW -> PUBLISHED（管理员） */
    @PostMapping("/{id}/approve")
    public ResponseEntity<MeetingDTO> approve(@PathVariable Long id) {
        ensureAdmin();
        MeetingDTO meeting = meetingApplicationService.approve(String.valueOf(id));
        return ResponseEntity.ok(meeting);
    }

    /** 审核拒绝：PENDING_REVIEW -> REJECTED（管理员） */
    @PostMapping("/{id}/reject")
    public ResponseEntity<MeetingDTO> reject(@PathVariable Long id, @RequestBody ReasonRequest request) {
        ensureAdmin();
        String reason = request != null ? request.getReason() : null;
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("拒绝原因不能为空");
        }
        MeetingDTO meeting = meetingApplicationService.reject(String.valueOf(id), reason);
        return ResponseEntity.ok(meeting);
    }

    /** 下架：PUBLISHED / IN_PROGRESS -> OFFLINE */
    @PostMapping("/{id}/takedown")
    public ResponseEntity<MeetingDTO> takedown(@PathVariable Long id, @RequestBody ReasonRequest request) {
        String reason = request != null ? request.getReason() : null;
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("下架原因不能为空");
        }
        MeetingDTO meeting = meetingApplicationService.takedown(String.valueOf(id), reason);
        return ResponseEntity.ok(meeting);
    }

    /** 逻辑删除：DRAFT / ENDED / OFFLINE / REJECTED -> DELETED */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingApplicationService.deleteMeeting(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    /** 数据统计：Bearer Token；advanced 按 isPremium 控制（agent.prd §2.6） */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<MeetingStatisticsDTO> getStatistics(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MeetingStatisticsDTO dto = meetingStatisticsUseCase.getStatistics(id);
        return ResponseEntity.ok(dto);
    }

    /** 查询权益状态（agent.prd §2.6） */
    @GetMapping("/{id}/rights")
    public ResponseEntity<MeetingRightsDTO> getRights(@PathVariable Long id) {
        MeetingRightsDTO dto = meetingRightsPurchaseUseCase.getRights(id);
        return ResponseEntity.ok(dto);
    }

    /** 购买高阶权益：唤起收银台（agent.prd §2.6） */
    @PostMapping("/{id}/rights/purchase")
    public ResponseEntity<RightsPurchaseResultDTO> purchaseRights(
            @PathVariable Long id,
            @RequestParam Long userId) {
        RightsPurchaseResultDTO dto = meetingRightsPurchaseUseCase.purchase(id, userId);
        return ResponseEntity.ok(dto);
    }

    /** 会议简报：format=pdf|word（agent.prd §2.7） */
    @GetMapping("/{id}/brief")
    public ResponseEntity<byte[]> getBrief(
            @PathVariable Long id,
            @RequestParam(defaultValue = "pdf") String format) {
        MeetingBriefUseCase.BriefFormat briefFormat;
        if ("word".equalsIgnoreCase(format)) {
            briefFormat = MeetingBriefUseCase.BriefFormat.WORD;
        } else {
            briefFormat = MeetingBriefUseCase.BriefFormat.PDF;
        }
        Object[] result = meetingBriefUseCase.generateBrief(id, briefFormat);
        byte[] bytes = (byte[]) result[0];
        String contentType = (String) result[1];
        String filename = (String) result[2];
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    /** 管理员校验：当前为占位实现，后续接入真实权限 */
    private void ensureAdmin() {
        // TODO: 接入真实权限系统，校验当前用户是否管理员
    }
}
