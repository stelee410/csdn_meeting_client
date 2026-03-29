package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.FormFieldConfigDTO;
import com.csdn.meeting.application.dto.RegistrationAuditCommand;
import com.csdn.meeting.application.dto.RegistrationCommand;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.service.MeetingRegistrationUseCase;
import com.csdn.meeting.application.service.RegistrationAuditUseCase;
import com.csdn.meeting.application.service.RegistrationConfigUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 报名相关 REST API
 * 包含用户报名、取消报名、查询报名状态、表单配置等功能
 *
 * 【已改造】用户身份从JWT Token中获取（通过LoginInterceptor设置到request attribute）
 */
@Tag(name = "报名接口", description = "会议报名相关接口")
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationAuditUseCase registrationAuditUseCase;
    private final MeetingRegistrationUseCase meetingRegistrationUseCase;
    private final RegistrationConfigUseCase registrationConfigUseCase;

    public RegistrationController(RegistrationAuditUseCase registrationAuditUseCase,
                                  MeetingRegistrationUseCase meetingRegistrationUseCase,
                                  RegistrationConfigUseCase registrationConfigUseCase) {
        this.registrationAuditUseCase = registrationAuditUseCase;
        this.meetingRegistrationUseCase = meetingRegistrationUseCase;
        this.registrationConfigUseCase = registrationConfigUseCase;
    }

    // ==================== 用户报名接口 ====================

    @Operation(
            summary = "获取报名表单配置",
            description = "获取指定会议的报名表单字段配置，用于前端渲染报名表单。"
    )
    @GetMapping("/config/{meetingId}")
    public ResponseEntity<ApiResponse<List<FormFieldConfigDTO>>> getFormConfig(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId) {
        List<FormFieldConfigDTO> config = registrationConfigUseCase.getConfig(meetingId);
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @Operation(
            summary = "获取预填表单数据",
            description = "获取报名表单的预填数据，从用户画像中自动填充姓名、手机号、邮箱等字段。"
    )
    @GetMapping("/pre-fill")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPreFillData(
            @Parameter(description = "会议ID", example = "M123456789")
            @RequestParam String meetingId,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        Map<String, String> preFilledData = meetingRegistrationUseCase.getPreFilledForm(meetingId, Long.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success(preFilledData));
    }

    @Operation(
            summary = "提交报名",
            description = "用户提交会议报名申请。校验名额、重复报名等，创建待审核报名记录。"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<RegistrationDTO>> submitRegistration(
            @RequestBody RegistrationCommand command,
            HttpServletRequest request) {
        // 从JWT获取用户ID并设置到command中
        String userId = getCurrentUserId(request);
        command.setUserId(Long.valueOf(userId));
        RegistrationDTO dto = meetingRegistrationUseCase.register(command);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "查询我的报名",
            description = "查询当前用户在指定会议的报名状态。未报名返回404。"
    )
    @GetMapping("/my/{meetingId}")
    public ResponseEntity<ApiResponse<RegistrationDTO>> getMyRegistration(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        RegistrationDTO dto = meetingRegistrationUseCase.getMyRegistration(meetingId, Long.valueOf(userId));
        // 未报名返回 200 + null body，而非 404（未报名是正常业务状态，不是资源不存在）
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "取消报名",
            description = "用户取消会议报名。只有待审核或已报名状态可取消，已签到后不可取消。"
    )
    @PostMapping("/{regId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        meetingRegistrationUseCase.cancelRegistration(regId, Long.valueOf(userId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== 主办方审核接口（原有） ====================

    @Operation(
            summary = "审核通过（主办方）",
            description = "将报名状态设为 APPROVED，触发站内信通知。"
    )
    @PostMapping("/{regId}/approve")
    public ResponseEntity<ApiResponse<RegistrationDTO>> approve(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId) {
        RegistrationDTO dto = registrationAuditUseCase.approve(regId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(
            summary = "审核拒绝（主办方）",
            description = "将报名状态设为 REJECTED，可传 auditRemark，触发站内信通知。"
    )
    @PostMapping("/{regId}/reject")
    public ResponseEntity<ApiResponse<RegistrationDTO>> reject(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId,
            @RequestBody(required = false) RegistrationAuditCommand command) {
        String auditRemark = command != null ? command.getAuditRemark() : null;
        RegistrationDTO dto = registrationAuditUseCase.reject(regId, auditRemark);
        return ResponseEntity.ok(ApiResponse.success(dto));
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
}
