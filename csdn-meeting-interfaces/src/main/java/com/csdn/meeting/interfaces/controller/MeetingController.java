package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.AIParseResultDTO;
import com.csdn.meeting.application.dto.CreateMeetingCommand;
import com.csdn.meeting.application.dto.FilterOptionsDTO;
import com.csdn.meeting.application.dto.GenerateDescriptionDTO;
import com.csdn.meeting.application.dto.GenerateDescriptionRequest;
import com.csdn.meeting.application.dto.GenerateImageDTO;
import com.csdn.meeting.application.dto.GenerateImageRequest;
import com.csdn.meeting.application.dto.JoinMeetingCommand;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.MeetingDetailPageDTO;
import com.csdn.meeting.application.dto.MeetingListQueryDTO;
import com.csdn.meeting.application.dto.MeetingListResultDTO;
import com.csdn.meeting.application.dto.MeetingCardItemDTO;
import com.csdn.meeting.application.dto.MeetingRightsDTO;
import com.csdn.meeting.application.dto.MeetingBriefingDTO;
import com.csdn.meeting.application.dto.MeetingStatisticsDTO;
import com.csdn.meeting.application.dto.PageResult;
import com.csdn.meeting.application.dto.ReasonRequest;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.dto.RegistrationStatusDTO;
import com.csdn.meeting.application.dto.RightsPurchaseResultDTO;
import com.csdn.meeting.application.dto.SuggestTagsRequest;
import com.csdn.meeting.application.dto.TagDTO;
import com.csdn.meeting.application.dto.TagSuggestionDTO;
import com.csdn.meeting.application.dto.UpdateMeetingCommand;
import com.csdn.meeting.application.service.AIParsingUseCase;
import com.csdn.meeting.application.service.GenerateDescriptionUseCase;
import com.csdn.meeting.application.service.GenerateImageUseCase;
import com.csdn.meeting.application.service.HotTagsUseCase;
import com.csdn.meeting.application.service.MeetingApplicationService;
import com.csdn.meeting.application.service.MeetingBriefUseCase;
import com.csdn.meeting.application.service.MeetingBriefingUseCase;
import com.csdn.meeting.application.service.MeetingDetailPageUseCase;
import com.csdn.meeting.application.service.MeetingListUseCase;
import com.csdn.meeting.application.service.MeetingRightsPurchaseUseCase;
import com.csdn.meeting.application.service.MeetingStatisticsUseCase;
import com.csdn.meeting.application.service.MyMeetingsUseCase;
import com.csdn.meeting.application.service.TagSuggestionUseCase;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议控制器
 * 提供会议管理、列表查询、筛选、搜索等功能
 */
@Tag(name = "会议接口")
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
    private final MeetingBriefingUseCase meetingBriefingUseCase;
    private final MeetingListUseCase meetingListUseCase;
    private final MeetingDetailPageUseCase meetingDetailPageUseCase;
    private final HotTagsUseCase hotTagsUseCase;
    private final GenerateDescriptionUseCase generateDescriptionUseCase;
    private final GenerateImageUseCase generateImageUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public MeetingController(MeetingApplicationService meetingApplicationService,
                             TagSuggestionUseCase tagSuggestionUseCase,
                             AIParsingUseCase aiParsingUseCase,
                             MyMeetingsUseCase myMeetingsUseCase,
                             MeetingStatisticsUseCase meetingStatisticsUseCase,
                             MeetingRightsPurchaseUseCase meetingRightsPurchaseUseCase,
                             MeetingBriefUseCase meetingBriefUseCase,
                             MeetingBriefingUseCase meetingBriefingUseCase,
                             MeetingListUseCase meetingListUseCase,
                             MeetingDetailPageUseCase meetingDetailPageUseCase,
                             HotTagsUseCase hotTagsUseCase,
                             GenerateDescriptionUseCase generateDescriptionUseCase,
                             GenerateImageUseCase generateImageUseCase,
                             JwtTokenProvider jwtTokenProvider) {
        this.meetingApplicationService = meetingApplicationService;
        this.tagSuggestionUseCase = tagSuggestionUseCase;
        this.aiParsingUseCase = aiParsingUseCase;
        this.myMeetingsUseCase = myMeetingsUseCase;
        this.meetingStatisticsUseCase = meetingStatisticsUseCase;
        this.meetingRightsPurchaseUseCase = meetingRightsPurchaseUseCase;
        this.meetingBriefUseCase = meetingBriefUseCase;
        this.meetingBriefingUseCase = meetingBriefingUseCase;
        this.meetingListUseCase = meetingListUseCase;
        this.meetingDetailPageUseCase = meetingDetailPageUseCase;
        this.hotTagsUseCase = hotTagsUseCase;
        this.generateDescriptionUseCase = generateDescriptionUseCase;
        this.generateImageUseCase = generateImageUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "创建草稿", description = "创建会议草稿，仅校验会议标题必填，日程可为空。状态为 DRAFT。需携带 Authorization: Bearer <token> 请求头。")
    @PostMapping
    public ResponseEntity<ApiResponse<MeetingDTO>> createDraft(@RequestBody CreateMeetingCommand command,
                                                               HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        command.setCreatorId(userId);
        MeetingDTO meeting = meetingApplicationService.createDraft(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(meeting));
    }

    @Operation(summary = "创建并提交审核", description = "原子化操作：校验通过后创建会议并立即提交审核，校验不通过时不创建草稿。")
    @PostMapping("/create-and-submit")
    public ResponseEntity<ApiResponse<MeetingDTO>> createAndSubmit(@RequestBody CreateMeetingCommand command,
                                                                   HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        command.setCreatorId(userId);
        MeetingDTO meeting = meetingApplicationService.createAndSubmit(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(meeting));
    }

    @Operation(summary = "更新会议", description = "更新会议信息，仅 DRAFT/REJECTED 状态可编辑。")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDTO>> update(@PathVariable Long id, @RequestBody UpdateMeetingCommand command,
                                                          HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        MeetingDTO meeting = meetingApplicationService.update(String.valueOf(id), command, userId);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    /*
     * 以下字面路径必须放在 GET /{id} 之前，否则 Spring 会将「my-created」等整体匹配为 {id}，Long 转换失败导致 400，前端「我的会议」列表永远拉取失败。
     */

    @Operation(summary = "我报名的会议", description = "按会议日期倒序，默认仅已发布/进行中；includeEnded=true 时包含已结束。")
    @GetMapping("/my-registered")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyRegistered(
            @RequestParam(defaultValue = "false") boolean includeEnded,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyRegistered(userId, includeEnded, page, size)));
    }

    @Operation(summary = "我收藏的会议", description = "获取当前用户收藏的会议列表，按收藏时间倒序。")
    @GetMapping("/my-favorites")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyFavorites(userId, page, size)));
    }

    @Operation(summary = "我创建的会议", description = "办会方创建的所有会议，支持状态、时间范围筛选。")
    @GetMapping("/my-created")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyCreated(
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
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
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyCreated(userId, statuses, startDate, endDate, page, size)));
    }

    @Operation(
            summary = "获取筛选选项",
            description = "返回各筛选维度的可选值列表，供前端初始化筛选器使用。" +
                    "包括：会议形式、会议类型、会议场景、召开时间、视图模式的所有可选值及其显示名称"
    )
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<FilterOptionsDTO>> getFilterOptions() {
        FilterOptionsDTO options = meetingListUseCase.getFilterOptions();
        return ResponseEntity.ok(ApiResponse.success(options));
    }

    @Operation(
            summary = "获取热门标签",
            description = "按使用该标签的已发布会议数量降序，返回热门标签列表。" +
                    "供移动端会议首页展示热门技术标签使用。"
    )
    @GetMapping("/hot-tags")
    public ResponseEntity<ApiResponse<List<TagDTO>>> getHotTags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(hotTagsUseCase.getHotTags(limit)));
    }

    @Operation(summary = "查询会议详情", description = "获取会议详情，含四级日程结构（日程日→环节→分会场→议题）。")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDTO>> getDetail(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.getMeetingDetailById(id);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    /**
     * 会议详情页API（V1.2新增）
     * 包含会议信息、用户报名状态、收藏状态、报名按钮状态等完整数据
     * 支持游客访问，已登录用户从JWT Token获取userId，未登录为null
     */
    @Operation(
            summary = "获取会议详情页",
            description = "获取会议详情页完整数据，包含会议信息、当前用户报名状态、收藏状态、报名按钮状态、报名表单配置等。支持游客访问。"
    )
    @GetMapping("/{meetingId}/detail-page")
    public ResponseEntity<ApiResponse<MeetingDetailPageDTO>> getMeetingDetailPage(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {
        // 从Token获取用户ID（可选，游客为null）
        String userId = getCurrentUserIdOptional(request);
        MeetingDetailPageDTO detailPage = meetingDetailPageUseCase.getMeetingDetailPage(meetingId, userId);
        return ResponseEntity.ok(ApiResponse.success(detailPage));
    }

    /**
     * 报名状态查询API（V1.2新增）
     * 查询会议的报名状态（名额、截止时间等）
     */
    @Operation(
            summary = "查询会议报名状态",
            description = "查询会议的报名状态，包括当前报名人数、名额上限、剩余名额、报名截止时间、是否已满等。"
    )
    @GetMapping("/{meetingId}/registration-status")
    public ResponseEntity<ApiResponse<RegistrationStatusDTO>> getRegistrationStatus(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId) {
        RegistrationStatusDTO status = meetingDetailPageUseCase.getRegistrationStatus(meetingId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @Operation(summary = "AI 智能解析", description = "上传文档(PDF/Word)或图片(JPG/PNG)，自动提取会议信息填充表单。文档<20MB，图片<10MB。解析失败返回 422。")
    @PostMapping("/actions/ai-parse")
    public ResponseEntity<ApiResponse<AIParseResultDTO>> aiParse(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
            AIParseResultDTO result = aiParsingUseCase.parse(bytes, fileName);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("读取文件失败: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "智能标签推荐", description = "根据会议标题与简介，调用 NLP 返回 3-5 个推荐标签。")
    @PostMapping("/actions/suggest-tags")
    public ResponseEntity<ApiResponse<TagSuggestionDTO>> suggestTags(@RequestBody SuggestTagsRequest request) {
        TagSuggestionDTO dto = tagSuggestionUseCase.suggestTags(
                request != null ? request.getTitle() : null,
                request != null ? request.getDescription() : null);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "AI 生成会议简介", description = "根据会议标题和标签，调用豆包大模型生成 100-200 字的专业会议简介。")
    @PostMapping("/actions/generate-description")
    public ResponseEntity<ApiResponse<GenerateDescriptionDTO>> generateDescription(@RequestBody GenerateDescriptionRequest request) {
        GenerateDescriptionDTO dto = generateDescriptionUseCase.generate(request);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "AI 生成会议背景图", description = "根据会议标题和简介，生成适合的会议封面背景图 URL。")
    @PostMapping("/actions/generate-image")
    public ResponseEntity<ApiResponse<GenerateImageDTO>> generateImage(@RequestBody GenerateImageRequest request) {
        GenerateImageDTO dto = generateImageUseCase.generate(request);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "提交审核", description = "DRAFT/REJECTED → PENDING_REVIEW。校验四级日程完整性，不完整时返回 400/422。")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<MeetingDTO>> submit(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        MeetingDTO meeting = meetingApplicationService.submit(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "获取全部会议", description = "返回所有会议列表（管理用）。")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingDTO>>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingApplicationService.getAllMeetings();
        return ResponseEntity.ok(ApiResponse.success(meetings));
    }

    @Operation(summary = "会议报名列表", description = "获取某会议报名记录，支持按 status(PENDING/APPROVED/REJECTED) 筛选。")
    @GetMapping("/{id}/registrations")
    public ResponseEntity<ApiResponse<PageResult<RegistrationDTO>>> getMeetingRegistrations(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Registration.RegistrationStatus regStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                regStatus = Registration.RegistrationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMeetingRegistrations(id, regStatus, page, size)));
    }

    @Operation(summary = "按创建者查询会议", description = "根据创建者 ID 获取其创建的会议列表。")
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<ApiResponse<List<MeetingDTO>>> getMeetingsByCreator(@PathVariable String creatorId) {
        List<MeetingDTO> meetings = meetingApplicationService.getMeetingsByCreator(creatorId);
        return ResponseEntity.ok(ApiResponse.success(meetings));
    }

    @Operation(summary = "报名/加入会议", description = "用户报名参加会议。")
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<MeetingDTO>> joinMeeting(
            @PathVariable Long id,
            @RequestBody JoinMeetingCommand command,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        command.setMeetingId(String.valueOf(id));
        command.setUserId(userId);
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "取消报名/离开会议", description = "用户取消报名或离开会议。")
    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveMeeting(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingApplicationService.leaveMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "开始会议", description = "将会议状态置为进行中。")
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<Void>> startMeeting(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingApplicationService.startMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "结束会议", description = "将会议状态置为已结束。")
    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<Void>> endMeeting(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingApplicationService.endMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "取消会议", description = "取消会议。")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelMeeting(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingApplicationService.cancelMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "撤回审核", description = "PENDING_REVIEW → DRAFT，仅办会方可操作。")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse<MeetingDTO>> withdraw(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        MeetingDTO meeting = meetingApplicationService.withdraw(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "审核通过", description = "PENDING_REVIEW → PUBLISHED，需管理员权限。")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<MeetingDTO>> approve(@PathVariable Long id) {
        ensureAdmin();
        MeetingDTO meeting = meetingApplicationService.approve(String.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "审核拒绝", description = "PENDING_REVIEW → REJECTED，需管理员权限，reason 必填。")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<MeetingDTO>> reject(@PathVariable Long id, @RequestBody ReasonRequest request) {
        ensureAdmin();
        String reason = request != null ? request.getReason() : null;
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("拒绝原因不能为空");
        }
        MeetingDTO meeting = meetingApplicationService.reject(String.valueOf(id), reason);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "下架会议", description = "PUBLISHED/IN_PROGRESS → OFFLINE，需填写下架原因。")
    @PostMapping("/{id}/takedown")
    public ResponseEntity<ApiResponse<MeetingDTO>> takedown(@PathVariable Long id, @RequestBody ReasonRequest requestBody,
                                                              HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        String reason = requestBody != null ? requestBody.getReason() : null;
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("下架原因不能为空");
        }
        MeetingDTO meeting = meetingApplicationService.takedown(String.valueOf(id), reason, userId);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "逻辑删除会议", description = "DRAFT/ENDED/OFFLINE/REJECTED → DELETED。")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeeting(@PathVariable Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingApplicationService.deleteMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "会议数据统计", description = "PV/UV/报名/签到/趋势；用户画像需 Bearer Token，按 isPremium 控制可见性。")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<ApiResponse<MeetingStatisticsDTO>> getStatistics(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "未授权"));
        }
        MeetingStatisticsDTO dto = meetingStatisticsUseCase.getStatistics(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "查询权益状态", description = "会议数据高阶权益包购买状态。")
    @GetMapping("/{id}/rights")
    public ResponseEntity<ApiResponse<MeetingRightsDTO>> getRights(@PathVariable Long id) {
        MeetingRightsDTO dto = meetingRightsPurchaseUseCase.getRights(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "购买高阶权益", description = "以会议为粒度购买数据权益，唤起 CSDN 统一收银台。")
    @PostMapping("/{id}/rights/purchase")
    public ResponseEntity<ApiResponse<RightsPurchaseResultDTO>> purchaseRights(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        RightsPurchaseResultDTO dto = meetingRightsPurchaseUseCase.purchase(id, userId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "会议简报预览数据", description = "供弹窗展示的结构化简报：报名签到聚合、基本信息与规则生成的总结文案；需登录。")
    @GetMapping("/{id}/briefing")
    public ResponseEntity<ApiResponse<MeetingBriefingDTO>> getBriefing(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "未授权"));
        }
        MeetingBriefingDTO dto = meetingBriefingUseCase.getBriefing(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "会议简报下载", description = "生成会议简报文件，支持 format=pdf|word。高阶数据受权益控制。")
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
     * 从请求中获取当前用户ID（可选，游客访问时返回null）
     * 优先从拦截器设置的attribute中获取，若不存在则尝试手动解析JWT Token
     */
    private String getCurrentUserIdOptional(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    return jwtTokenProvider.getUserIdFromToken(token);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 会议列表查询（统一接口，支持筛选、搜索、分页、视图切换）
     * POST /api/meetings/list，请求体为 JSON，字段同 MeetingListQueryDTO
     * 已登录用户从JWT Token获取userId，用于个性化推荐和埋点统计
     *
     * @param query 查询条件（keyword、format、type、scene、timeRange、page、size）
     * @return 会议列表结果
     */
    @Operation(
            summary = "会议列表查询",
            description = "统一接口支持多维度筛选、关键词搜索、分页。" +
                    "筛选条件包括：会议形式（线上/线下/混合）、会议类型（峰会/沙龙/研讨会）、" +
                    "会议场景（开发者/产业/产品/区域/高校）、召开时间（本周/本月/未来三个月）。" +
                    "关键词搜索匹配会议标题、标签、主办方。" +
                    "接口统一返回卡片(card)结构。使用 POST 请求，参数放在请求体 JSON 中。"
    )
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<MeetingListResultDTO<MeetingCardItemDTO>>> queryMeetingList(
            @RequestBody MeetingListQueryDTO query,
            HttpServletRequest request) {

        if (query.getSize() == 0) {
            query.setSize(20);
        }

        // 从Token获取用户ID（可选），用于个性化推荐
        String userIdStr = getCurrentUserIdOptional(request);
        if (userIdStr != null) {
            query.setUserId(userIdStr);
        }

        MeetingListResultDTO<MeetingCardItemDTO> result = meetingListUseCase.queryMeetingList(query);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
