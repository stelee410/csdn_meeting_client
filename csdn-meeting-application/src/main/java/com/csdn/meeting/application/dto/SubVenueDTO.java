package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * 分会场（四级结构第三级）
 */
public class SubVenueDTO {

    private String subVenueName;
    private List<TopicDTO> topics;

    public String getSubVenueName() {
        return subVenueName;
    }

    public void setSubVenueName(String subVenueName) {
        this.subVenueName = subVenueName;
    }

    public List<TopicDTO> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDTO> topics) {
        this.topics = topics;
    }
}
