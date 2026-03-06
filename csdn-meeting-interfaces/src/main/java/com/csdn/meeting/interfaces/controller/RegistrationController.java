package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.RegistrationAuditCommand;
import com.csdn.meeting.application.dto.RegistrationDTO;
import com.csdn.meeting.application.service.RegistrationAuditUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 报名审核 REST API（agent.prd §2.9）
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationAuditUseCase registrationAuditUseCase;

    public RegistrationController(RegistrationAuditUseCase registrationAuditUseCase) {
        this.registrationAuditUseCase = registrationAuditUseCase;
    }

    /** 审核通过 */
    @PostMapping("/{regId}/approve")
    public ResponseEntity<RegistrationDTO> approve(@PathVariable Long regId) {
        RegistrationDTO dto = registrationAuditUseCase.approve(regId);
        return ResponseEntity.ok(dto);
    }

    /** 审核拒绝（body 可含 auditRemark） */
    @PostMapping("/{regId}/reject")
    public ResponseEntity<RegistrationDTO> reject(@PathVariable Long regId,
                                                  @RequestBody(required = false) RegistrationAuditCommand command) {
        String auditRemark = command != null ? command.getAuditRemark() : null;
        RegistrationDTO dto = registrationAuditUseCase.reject(regId, auditRemark);
        return ResponseEntity.ok(dto);
    }
}
