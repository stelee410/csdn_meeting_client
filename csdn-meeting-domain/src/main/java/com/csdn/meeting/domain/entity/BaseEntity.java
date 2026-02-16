package com.csdn.meeting.domain.entity;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {
    
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
