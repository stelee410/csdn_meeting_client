package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * 议题（四级结构第四级）
 */
public class TopicDTO {

    private String title;
    private String topicIntro;
    private String involvedProducts;
    private List<String> guests;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicIntro() {
        return topicIntro;
    }

    public void setTopicIntro(String topicIntro) {
        this.topicIntro = topicIntro;
    }

    public String getInvolvedProducts() {
        return involvedProducts;
    }

    public void setInvolvedProducts(String involvedProducts) {
        this.involvedProducts = involvedProducts;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }
}
