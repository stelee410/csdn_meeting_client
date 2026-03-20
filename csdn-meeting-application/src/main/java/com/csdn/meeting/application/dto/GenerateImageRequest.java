package com.csdn.meeting.application.dto;

/**
 * AI 生成会议背景图请求体。
 */
public class GenerateImageRequest {

    /** 会议标题（必填） */
    private String title;

    /** 会议简介（可选，辅助生成更贴合内容的图片） */
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
