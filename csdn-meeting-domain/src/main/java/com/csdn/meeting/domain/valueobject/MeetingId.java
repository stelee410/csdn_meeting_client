package com.csdn.meeting.domain.valueobject;

import java.io.Serializable;
import java.util.Objects;

public class MeetingId implements Serializable {

    private final String value;

    public MeetingId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("会议ID不能为空");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingId meetingId = (MeetingId) o;
        return Objects.equals(value, meetingId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
