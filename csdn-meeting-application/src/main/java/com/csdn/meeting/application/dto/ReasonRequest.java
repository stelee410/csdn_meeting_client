package com.csdn.meeting.application.dto;

/**
 * 请求体：仅包含 reason 字段（用于 reject、takedown 等接口）
 */
public class ReasonRequest {

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
