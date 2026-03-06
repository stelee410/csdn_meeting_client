package com.csdn.meeting.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI 解析结果 DTO，包含 traceId、填充字段、敏感词字段及解析出的会议数据。
 */
public class AIParseResultDTO {

    private String traceId;
    private List<String> filledFields = new ArrayList<>();
    private List<String> sensitiveFields = new ArrayList<>();
    private MeetingDTO data;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<String> getFilledFields() {
        return filledFields != null ? filledFields : Collections.emptyList();
    }

    public void setFilledFields(List<String> filledFields) {
        this.filledFields = filledFields != null ? filledFields : new ArrayList<>();
    }

    public List<String> getSensitiveFields() {
        return sensitiveFields != null ? sensitiveFields : Collections.emptyList();
    }

    public void setSensitiveFields(List<String> sensitiveFields) {
        this.sensitiveFields = sensitiveFields != null ? sensitiveFields : new ArrayList<>();
    }

    public MeetingDTO getData() {
        return data;
    }

    public void setData(MeetingDTO data) {
        this.data = data;
    }
}
