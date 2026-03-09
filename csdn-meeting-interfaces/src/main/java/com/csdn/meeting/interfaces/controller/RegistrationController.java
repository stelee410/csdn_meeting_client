package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.RegistrationAuditCommand;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.service.RegistrationAuditUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 报名审核 REST API（agent.prd §2.9）
 */
@Tag(name = "报名审核接口")
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationAuditUseCase registrationAuditUseCase;

    public RegistrationController(RegistrationAuditUseCase registrationAuditUseCase) {
        this.registrationAuditUseCase = registrationAuditUseCase;
    }

    @Operation(summary = "审核通过", description = "将报名状态设为 APPROVED，触发多渠道通知（Push/短信/邮件/私信）。")
    @PostMapping("/{regId}/approve")
    public ResponseEntity<RegistrationDTO> approve(@PathVariable Long regId) {
        RegistrationDTO dto = registrationAuditUseCase.approve(regId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "审核拒绝", description = "将报名状态设为 REJECTED，可传 auditRemark，触发多渠道通知。")
    @PostMapping("/{regId}/reject")
    public ResponseEntity<RegistrationDTO> reject(@PathVariable Long regId,
                                                  @RequestBody(required = false) RegistrationAuditCommand command) {
        String auditRemark = command != null ? command.getAuditRemark() : null;
        RegistrationDTO dto = registrationAuditUseCase.reject(regId, auditRemark);
        return ResponseEntity.ok(dto);
    }
}
