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
import com.csdn.meeting.application.service.MeetingDetailPageUseCase;
import com.csdn.meeting.application.service.MeetingListUseCase;
import com.csdn.meeting.application.service.MeetingRightsPurchaseUseCase;
import com.csdn.meeting.application.service.MeetingStatisticsUseCase;
import com.csdn.meeting.application.service.MyMeetingsUseCase;
import com.csdn.meeting.application.service.TagSuggestionUseCase;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;
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
    private final MeetingListUseCase meetingListUseCase;
    private final MeetingDetailPageUseCase meetingDetailPageUseCase;
    private final HotTagsUseCase hotTagsUseCase;
    private final GenerateDescriptionUseCase generateDescriptionUseCase;
    private final GenerateImageUseCase generateImageUseCase;

    public MeetingController(MeetingApplicationService meetingApplicationService,
                             TagSuggestionUseCase tagSuggestionUseCase,
                             AIParsingUseCase aiParsingUseCase,
                             MyMeetingsUseCase myMeetingsUseCase,
                             MeetingStatisticsUseCase meetingStatisticsUseCase,
                             MeetingRightsPurchaseUseCase meetingRightsPurchaseUseCase,
                             MeetingBriefUseCase meetingBriefUseCase,
                             MeetingListUseCase meetingListUseCase,
                             MeetingDetailPageUseCase meetingDetailPageUseCase,
                             HotTagsUseCase hotTagsUseCase,
                             GenerateDescriptionUseCase generateDescriptionUseCase,
                             GenerateImageUseCase generateImageUseCase) {
        this.meetingApplicationService = meetingApplicationService;
        this.tagSuggestionUseCase = tagSuggestionUseCase;
        this.aiParsingUseCase = aiParsingUseCase;
        this.myMeetingsUseCase = myMeetingsUseCase;
        this.meetingStatisticsUseCase = meetingStatisticsUseCase;
        this.meetingRightsPurchaseUseCase = meetingRightsPurchaseUseCase;
        this.meetingBriefUseCase = meetingBriefUseCase;
        this.meetingListUseCase = meetingListUseCase;
        this.meetingDetailPageUseCase = meetingDetailPageUseCase;
        this.hotTagsUseCase = hotTagsUseCase;
        this.generateDescriptionUseCase = generateDescriptionUseCase;
        this.generateImageUseCase = generateImageUseCase;
    }

    @Operation(summary = "创建草稿", description = "创建会议草稿，仅校验会议标题必填，日程可为空。状态为 DRAFT。")
    @PostMapping
    public ResponseEntity<ApiResponse<MeetingDTO>> createDraft(@RequestBody CreateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.createDraft(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(meeting));
    }

    @Operation(summary = "更新会议", description = "更新会议信息，仅 DRAFT/REJECTED 状态可编辑。")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingDTO>> update(@PathVariable Long id, @RequestBody UpdateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.update(String.valueOf(id), command);
        return ResponseEntity.ok(ApiResponse.success(meeting));
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
     * 
     * TODO【需与CSDN协调】：
     * 1. 用户身份获取方式：当前从RequestParam获取userId，需对接CSDN统一认证
     *    - 方案A：从JWT Token解析用户ID（推荐）
     *    - 方案B：从Session获取用户ID
     *    - 方案C：从Header中获取用户ID
     * 2. 确认CSDN统一认证接口文档
     * 3. 确认Token校验方式和密钥
     * 4. 确认未登录用户的处理方式（是否允许浏览，报名时跳转登录）
     */
    @Operation(
            summary = "获取会议详情页",
            description = "获取会议详情页完整数据，包含会议信息、当前用户报名状态、收藏状态、报名按钮状态、报名表单配置等。"
    )
    @GetMapping("/{meetingId}/detail-page")
    public ResponseEntity<ApiResponse<MeetingDetailPageDTO>> getMeetingDetailPage(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            // TODO【需与CSDN协调】：改为从JWT Token或Session获取用户ID
            @Parameter(description = "用户ID（可选，未登录不传）", example = "12345")
            @RequestParam(required = false) Long userId) {
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
    public ResponseEntity<ApiResponse<MeetingDTO>> submit(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.submit(String.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "获取全部会议", description = "返回所有会议列表（管理用）。")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingDTO>>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingApplicationService.getAllMeetings();
        return ResponseEntity.ok(ApiResponse.success(meetings));
    }

    @Operation(summary = "我报名的会议", description = "按会议日期倒序，默认仅已发布/进行中；includeEnded=true 时包含已结束。")
    @GetMapping("/my-registered")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyRegistered(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeEnded,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyRegistered(userId, includeEnded, page, size)));
    }

    @Operation(summary = "我收藏的会议", description = "获取当前用户收藏的会议列表，按收藏时间倒序。")
    @GetMapping("/my-favorites")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyFavorites(userId, page, size)));
    }

    @Operation(summary = "我创建的会议", description = "办会方创建的所有会议，支持状态、时间范围筛选。")
    @GetMapping("/my-created")
    public ResponseEntity<ApiResponse<PageResult<MeetingDTO>>> getMyCreated(
            @RequestParam String userId,
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
        return ResponseEntity.ok(ApiResponse.success(
                myMeetingsUseCase.getMyCreated(userId, statuses, startDate, endDate, page, size)));
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
    public ResponseEntity<ApiResponse<MeetingDTO>> joinMeeting(@PathVariable Long id, @RequestBody JoinMeetingCommand command) {
        command.setMeetingId(String.valueOf(id));
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "取消报名/离开会议", description = "用户取消报名或离开会议。")
    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveMeeting(@PathVariable Long id, @RequestParam Long userId) {
        meetingApplicationService.leaveMeeting(String.valueOf(id), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "开始会议", description = "将会议状态置为进行中。")
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<Void>> startMeeting(@PathVariable Long id) {
        meetingApplicationService.startMeeting(String.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "结束会议", description = "将会议状态置为已结束。")
    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<Void>> endMeeting(@PathVariable Long id) {
        meetingApplicationService.endMeeting(String.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "取消会议", description = "取消会议。")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelMeeting(@PathVariable Long id) {
        meetingApplicationService.cancelMeeting(String.valueOf(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "撤回审核", description = "PENDING_REVIEW → DRAFT，仅办会方可操作。")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse<MeetingDTO>> withdraw(@PathVariable Long id) {
        MeetingDTO meeting = meetingApplicationService.withdraw(String.valueOf(id));
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
    public ResponseEntity<ApiResponse<MeetingDTO>> takedown(@PathVariable Long id, @RequestBody ReasonRequest request) {
        String reason = request != null ? request.getReason() : null;
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("下架原因不能为空");
        }
        MeetingDTO meeting = meetingApplicationService.takedown(String.valueOf(id), reason);
        return ResponseEntity.ok(ApiResponse.success(meeting));
    }

    @Operation(summary = "逻辑删除会议", description = "DRAFT/ENDED/OFFLINE/REJECTED → DELETED。")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeeting(@PathVariable Long id) {
        meetingApplicationService.deleteMeeting(String.valueOf(id));
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
            @RequestParam Long userId) {
        RightsPurchaseResultDTO dto = meetingRightsPurchaseUseCase.purchase(id, userId);
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
     * 会议列表查询（统一接口，支持筛选、搜索、分页、视图切换）
     * POST /api/meetings/list，请求体为 JSON，字段同 MeetingListQueryDTO
     *
     * @param query 查询条件（keyword、format、type、scene、timeRange、page、size、userId）
     * @return 会议列表结果
     *
     * TODO【需要和CSDN对接登录人信息获取】：
     * 1. 当前userId从请求体传入，用于个性化推荐和埋点统计
     * 2. 需对接CSDN统一认证服务，从JWT Token或Session中自动获取当前登录用户ID
     * 3. 对接后可从Spring Security上下文获取用户身份，无需前端显式传递userId
     * 4. 需CSDN提供：统一认证接口文档、用户身份获取方式
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
            @RequestBody MeetingListQueryDTO query) {

        // TODO【CSDN对接-用户身份】：从认证上下文获取当前登录用户ID，替代从请求体传入
        if (query.getSize() == 0) {
            query.setSize(20);
        }

        MeetingListResultDTO<MeetingCardItemDTO> result = meetingListUseCase.queryMeetingList(query);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取筛选选项枚举值（供前端初始化筛选器）
     * GET /api/meetings/filter-options
     *
     * @return 各筛选维度的可选值列表
     */
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

    /**
     * 获取热门标签列表
     * GET /api/meetings/hot-tags
     *
     * @param limit 返回数量限制，默认10个
     * @return 热门标签列表
     *
     * TODO【移动端适配】：移动端会议首页展示热门标签（人工智能、云计算、区块链等）
     */
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
}
