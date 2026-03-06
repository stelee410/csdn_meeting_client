package com.csdn.meeting.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会议标签关联实体
 * 记录会议与标签的多对多关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MeetingTag extends BaseEntity {

    /**
     * 关联ID
     */
    private Long id;

    /**
     * 会议ID
     */
    private String meetingId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 关联的标签对象（非持久化字段，用于关联查询）
     */
    private Tag tag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人ID
     */
    private Long updateBy;
}
