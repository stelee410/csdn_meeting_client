package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户标签订阅持久化对象
 * 对应数据库表 t_user_tag_subscribe
 */
@Data
@TableName("t_user_tag_subscribe")
public class UserTagSubscribePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 创建时间（即订阅时间）
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 创建人ID todo
     */
//    @TableField(value = "create_by", fill = FieldFill.INSERT)
//    private String createBy;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 更新人ID todo
     */
//    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
//    private String updateBy;

    /**
     * 软删除标志：0-未删除（已订阅）, 1-已删除（取消订阅）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;
}
