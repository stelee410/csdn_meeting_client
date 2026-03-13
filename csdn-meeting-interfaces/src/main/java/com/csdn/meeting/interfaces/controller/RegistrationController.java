package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.FormFieldConfigDTO;
import com.csdn.meeting.application.dto.RegistrationAuditCommand;
import com.csdn.meeting.application.dto.RegistrationCommand;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.service.MeetingRegistrationUseCase;
import com.csdn.meeting.application.service.RegistrationAuditUseCase;
import com.csdn.meeting.application.service.RegistrationConfigUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报名相关 REST API
 * 包含用户报名、取消报名、查询报名状态、表单配置等功能
 * 
 * TODO【需与CSDN协调】：
 * 1. 用户身份获取：当前从RequestParam获取userId，需改为从JWT Token或Session获取
 * 2. 确认手机号加密传输方案（当前脱敏显示，需确认是否加密存储）
 * 3. 确认报名通知模板编码（IM站内信、APP Push、邮件）
 * 4. 确认报名成功后是否生成电子票/二维码
 * 5. 确认是否需要接入CSDN统一支付（如报名收费）
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
    public ResponseEntity<List<FormFieldConfigDTO>> getFormConfig(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId) {
        List<FormFieldConfigDTO> config = registrationConfigUseCase.getConfig(meetingId);
        return ResponseEntity.ok(config);
    }

    @Operation(
            summary = "获取预填表单数据",
            description = "获取报名表单的预填数据，从用户画像中自动填充姓名、手机号、邮箱等字段。"
    )
    @GetMapping("/pre-fill")
    public ResponseEntity<Map<String, String>> getPreFillData(
            @Parameter(description = "会议ID", example = "M123456789")
            @RequestParam String meetingId,
            @Parameter(description = "用户ID", example = "12345")
            @RequestParam Long userId) {
        Map<String, String> preFilledData = meetingRegistrationUseCase.getPreFilledForm(meetingId, userId);
        return ResponseEntity.ok(preFilledData);
    }

    @Operation(
            summary = "提交报名",
            description = "用户提交会议报名申请。校验名额、重复报名等，创建待审核报名记录。"
    )
    @PostMapping
    public ResponseEntity<RegistrationDTO> submitRegistration(
            @RequestBody RegistrationCommand command) {
        RegistrationDTO dto = meetingRegistrationUseCase.register(command);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "查询我的报名",
            description = "查询当前用户在指定会议的报名状态。未报名返回404。"
    )
    @GetMapping("/my/{meetingId}")
    public ResponseEntity<RegistrationDTO> getMyRegistration(
            @Parameter(description = "会议ID", example = "M123456789")
            @PathVariable String meetingId,
            @Parameter(description = "用户ID", example = "12345")
            @RequestParam Long userId) {
        RegistrationDTO dto = meetingRegistrationUseCase.getMyRegistration(meetingId, userId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "取消报名",
            description = "用户取消会议报名。只有待审核或已报名状态可取消，已签到后不可取消。"
    )
    @PostMapping("/{regId}/cancel")
    public ResponseEntity<Void> cancelRegistration(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId,
            @Parameter(description = "用户ID", example = "12345")
            @RequestParam Long userId) {
        meetingRegistrationUseCase.cancelRegistration(regId, userId);
        return ResponseEntity.ok().build();
    }

    // ==================== 主办方审核接口（原有） ====================

    @Operation(
            summary = "审核通过（主办方）",
            description = "将报名状态设为 APPROVED，触发多渠道通知（IM站内信、APP Push、邮件）。"
    )
    @PostMapping("/{regId}/approve")
    public ResponseEntity<RegistrationDTO> approve(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId) {
        RegistrationDTO dto = registrationAuditUseCase.approve(regId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "审核拒绝（主办方）",
            description = "将报名状态设为 REJECTED，可传 auditRemark，触发多渠道通知。"
    )
    @PostMapping("/{regId}/reject")
    public ResponseEntity<RegistrationDTO> reject(
            @Parameter(description = "报名记录ID", example = "12345")
            @PathVariable Long regId,
            @RequestBody(required = false) RegistrationAuditCommand command) {
        String auditRemark = command != null ? command.getAuditRemark() : null;
        RegistrationDTO dto = registrationAuditUseCase.reject(regId, auditRemark);
        return ResponseEntity.ok(dto);
    }
}
