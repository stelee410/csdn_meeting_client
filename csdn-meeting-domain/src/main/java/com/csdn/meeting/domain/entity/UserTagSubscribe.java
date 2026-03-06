package com.csdn.meeting.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户标签订阅实体
 * 记录用户订阅的标签，用于新会议推送通知
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserTagSubscribe extends BaseEntity {

    /**
     * 订阅ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 关联的标签对象（非持久化字段，用于关联查询）
     */
    private Tag tag;

    /**
     * 订阅时间（即创建时间）
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

    /**
     * 是否已删除（取消订阅）
     */
    private Integer isDeleted;
}
