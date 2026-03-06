package com.csdn.meeting.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分会场（四级结构第三级）
 */
public class SubVenue implements Serializable {

    private final String subVenueName;
    private final List<Topic> topics;

    public SubVenue(String subVenueName, List<Topic> topics) {
        this.subVenueName = subVenueName;
        this.topics = topics == null ? new ArrayList<>() : new ArrayList<>(topics);
    }

    public String getSubVenueName() {
        return subVenueName;
    }

    public List<Topic> getTopics() {
        return Collections.unmodifiableList(topics);
    }
}
