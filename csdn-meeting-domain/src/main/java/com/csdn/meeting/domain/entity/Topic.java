package com.csdn.meeting.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 议题（四级结构第四级）
 * 提交审核时 topic_title 必须非空（由 MeetingDomainService 校验）
 */
public class Topic implements Serializable {

    private final String title;
    private final String topicIntro;
    private final String involvedProducts;
    private final List<String> guests;

    public Topic(String title, String topicIntro, String involvedProducts, List<String> guests) {
        this.title = title;
        this.topicIntro = topicIntro;
        this.involvedProducts = involvedProducts;
        this.guests = guests == null ? new ArrayList<>() : new ArrayList<>(guests);
    }

    public String getTitle() {
        return title;
    }

    public String getTopicIntro() {
        return topicIntro;
    }

    public String getInvolvedProducts() {
        return involvedProducts;
    }

    public List<String> getGuests() {
        return Collections.unmodifiableList(guests);
    }

    public boolean hasValidTitle() {
        return title != null && !title.isBlank();
    }
}
