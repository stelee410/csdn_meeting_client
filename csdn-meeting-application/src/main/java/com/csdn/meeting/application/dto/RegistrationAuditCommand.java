package com.csdn.meeting.application.dto;

/**
 * 报名审核命令（拒绝时可选 auditRemark）
 */
public class RegistrationAuditCommand {

    private String auditRemark;

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
