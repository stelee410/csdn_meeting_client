package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * AI 生成会议简介请求体。
 */
public class GenerateDescriptionRequest {

    /** 会议标题（必填） */
    private String title;

    /** 会议标签（可选，辅助生成更精准的简介） */
    private List<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
