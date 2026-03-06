package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * 标签推荐响应 DTO
 */
public class TagSuggestionDTO {

    private List<String> tags;

    public TagSuggestionDTO() {
    }

    public TagSuggestionDTO(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
