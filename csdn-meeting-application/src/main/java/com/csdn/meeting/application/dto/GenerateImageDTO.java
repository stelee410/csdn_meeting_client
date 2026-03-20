package com.csdn.meeting.application.dto;

/**
 * AI 生成会议背景图响应体。
 */
public class GenerateImageDTO {

    /** 生成的背景图可访问 URL */
    private String imageUrl;

    public GenerateImageDTO() {
    }

    public GenerateImageDTO(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
