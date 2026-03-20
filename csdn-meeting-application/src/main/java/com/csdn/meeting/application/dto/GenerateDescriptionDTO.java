package com.csdn.meeting.application.dto;

/**
 * AI 生成会议简介响应体。
 */
public class GenerateDescriptionDTO {

    /** 生成的会议简介文本 */
    private String description;

    public GenerateDescriptionDTO() {
    }

    public GenerateDescriptionDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
